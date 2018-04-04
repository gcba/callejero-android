package com.gcba.callejero.ui.search.adapter;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcba.callejero.R;
import com.gcba.callejero.model.StandardizedAddress;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by ignacio on 08/06/17.
 */

public class CallejeroAdapter extends BaseAdapter {

    private List<StandardizedAddress> addressList;
    private static int ADDRESS_TYPE = 0;
    private static int PIN_TYPE = 1;
    private boolean showPin;
    private String[] searchQueries;

    public CallejeroAdapter(List<StandardizedAddress> addressList, String searchQuery, boolean showPin) {
        this.showPin = showPin;
        this.addressList = addressList;
        this.searchQueries = searchQuery.split("\\s+");
    }

    public void addSearches(List<StandardizedAddress> addresses) {
        if (addressList.isEmpty()) addressList.addAll(addresses);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = addressList.size();

        if (showPin) {
            size += 1;
        }

        return size;
    }

    @Override
    public int getViewTypeCount() {
        if (showPin) {
            return 2;
        }

        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == addressList.size() && showPin) {
            return PIN_TYPE;
        }

        return ADDRESS_TYPE;
    }

    @Override
    public Object getItem(int position) {
        if (showPin && position + 1 == getCount()) {
            return null;
        }

        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == PIN_TYPE) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_pin, parent, false);
        }

        if (convertView == null) convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_result, parent, false);

        StandardizedAddress address = addressList.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ImageView);

        if (address.isPlace()) {
            imageView.setImageResource(R.drawable.bandera1);
        } else {
            imageView.setImageResource(R.drawable.direccion1);
        }

        TextView resultName = (TextView) convertView.findViewById(R.id.search_result_name);

        resultName.setText(highlightMatches(address.getName()));

        return convertView;
    }

    private SpannableString highlightMatches(String text) {
        SpannableString highlightAddress = new SpannableString(text);

        for (String query : searchQueries) {
            try {
                Matcher matcher = Pattern.compile(query, Pattern.CASE_INSENSITIVE).matcher(highlightAddress);

                while (matcher.find()) {
                    highlightAddress.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), matcher.start(), matcher.end(), 0);
                }
            } catch (PatternSyntaxException e) { }
        }

        return highlightAddress;
    }

}
