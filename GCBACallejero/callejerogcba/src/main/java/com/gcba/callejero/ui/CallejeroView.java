package com.gcba.callejero.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcba.callejero.CallejeroManager;
import com.gcba.callejero.CallejeroOptions;
import com.gcba.callejero.LocationCallBack;
import com.gcba.callejero.R;
import com.gcba.callejero.SelectionCallback;
import com.gcba.callejero.CallejeroCTE;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.StandardizedAddress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by ignacio on 08/06/17.
 */

public class CallejeroView extends RelativeLayout {

    static final int REQUEST_SEARCH_CODE = 1001;

    private CallejeroOptions callejeroOptions = new CallejeroOptions();

    private static final boolean DEFAULT_ONLY_CABA = false;
    public static final String REQUEST_ID_DATA = "_REQUEST_ID_DATA_";

    private SelectionCallback callback;

    private StandardizedAddress selectedAddress;

    private TextView addressTextView;
    private View divider;
    private ImageView myLocation;




    private FusedLocationProviderClient fusedLocationClient;

    public CallejeroView(Context context) {
        this(context, null);
    }

    public CallejeroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallejeroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setBackgroundResource(R.drawable.callejero_background);

        LayoutInflater.from(getContext()).inflate(R.layout.callejero_layout, this, true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        initControls();
        setListeners();
    }

    private void initControls() {
        addressTextView = (TextView) findViewById(R.id.callejero_address);
        divider = findViewById(R.id.callejero_divider);
        myLocation = (ImageView) findViewById(R.id.callejero_my_location_button);
    }

    private void setListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnabled(false);
                goSearchScreen();
            }
        });

        myLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            AddressLocation addressLocation = new AddressLocation();
                            addressLocation.setX(location.getLongitude());
                            addressLocation.setY(location.getLatitude());


                            CallejeroManager.getInstance().loadAddressLatLong(addressLocation, new LocationCallBack() {
                                @Override
                                public void onSuccess(StandardizedAddress address) {

                                    CallejeroView.this.selectedAddress = address;
                                    hanldeResult(address);
                                }

                                @Override
                                public void onError(Throwable error) {

                                }
                            });
                        }
                    }
                });
    }


    @RequiresPermission(allOf = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION })
    public void enabledLocation(){

        RelativeLayout.LayoutParams myLocationParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myLocationParams.addRule(ALIGN_PARENT_RIGHT);

        myLocation.setLayoutParams(myLocationParams);
        myLocation.setVisibility(VISIBLE);

        int heigth = getResources().getDimensionPixelSize(R.dimen.callejero_height);

        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(1, heigth);
        dividerParams.addRule(RelativeLayout.LEFT_OF, myLocation.getId());
        dividerParams.addRule(CENTER_VERTICAL);

        divider.setLayoutParams(dividerParams);
        divider.setVisibility(VISIBLE);

        int margin = getResources().getDimensionPixelSize(R.dimen.callejero_padding);

        RelativeLayout.LayoutParams addressParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heigth);
        addressParams.addRule(RelativeLayout.LEFT_OF, divider.getId());
        addressParams.addRule(CENTER_VERTICAL);
        addressParams.setMargins(margin, margin, margin, margin);

        addressTextView.setLayoutParams(addressParams);
    }

    private void goSearchScreen(){

        Activity activity = null;

        if(getContext() instanceof Activity) {
            activity = (Activity) getContext();
        }else if(getContext() instanceof ContextThemeWrapper){
            activity = (Activity) ((ContextThemeWrapper) getContext()).getBaseContext();
        }

        if(activity != null)
            CallejeroManager.getInstance().startSearch(activity,callejeroOptions, getId(), callback);
    }


    private void hanldeResult(StandardizedAddress address){
        addressTextView.setText(address.getName());

        if(callback != null){
            callback.onAddressSelection(address);
        }
    }

    public StandardizedAddress getSelectedAddress() {
        return selectedAddress;
    }

    public void setOptions(CallejeroOptions optionsParams){
        this.callejeroOptions = optionsParams;
    }

    public void setSelectedAddress(String name) {
        this.selectedAddress = new StandardizedAddress();
        this.selectedAddress.setName(name);
        hanldeResult(selectedAddress);
    }

    public void setSelectedAddress(StandardizedAddress selectedAddress) {
        this.selectedAddress = selectedAddress;
        hanldeResult(selectedAddress);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){

        setEnabled(true);

        int requestId = data.getIntExtra(CallejeroView.REQUEST_ID_DATA, 0);

        if(requestId != getId())
            return;

        CallejeroManager.getInstance().onActivityResult(requestCode, resultCode, data);

        if(requestCode != REQUEST_SEARCH_CODE || resultCode != Activity.RESULT_OK)
            return;

        if(data.getIntExtra(REQUEST_ID_DATA, 0) != getId())
            return;

        selectedAddress = data.getParcelableExtra(CallejeroCTE.STANDARDIZED_ADDRESS_DATA);
        hanldeResult(selectedAddress);
    }

    public void setSelectionCallback(SelectionCallback callback) {
        this.callback = callback;
    }
}
