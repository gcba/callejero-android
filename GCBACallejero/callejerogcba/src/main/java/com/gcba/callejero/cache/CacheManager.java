package com.gcba.callejero.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.GcbaUtils;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ignacio on 14/06/17.
 */

public class CacheManager {

    static final int VERSION = 1;
    static final String PREFS_NAME = "_CALLEJERO_CACHE_";
    static final String VERSION_KEY = "_VERSION_";
    static final String SEARCHES_KEY = "_SEARCHS_";
    static final String SEARCHES_ATTR = "searchs";
    static final String STREET_CODE_ATTR = "street_code";
    static final String STREET_NUMBER_ATTR = "street_number_code";
    static final String STREET_NAME_ATTR = "street_name";
    static final String STREET_CORNER_NAME_ATTR = "street_corner_name";
    static final String ADDRESS_NAME_ATTR = "address";
    static final String TYPE_ATTR = "type";
    static final String PLACE_NAME = "place_name";
    static final String X_ATTR = "x";
    static final String Y_ATTR = "y";
    static final String COD_PARTIDO = "cityCode";

    private SharedPreferences preferences;

    public CacheManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        init();
    }

    private void init() {
        initJsonPath();

        int currentVersion = preferences.getInt(VERSION_KEY, 0);

        if (currentVersion < VERSION) {
            cleanSearches();
            updateVersion();
        }

        if (preferences.contains(SEARCHES_KEY)) {
            return;
        }

        try {
            JSONObject object = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            object.put(SEARCHES_ATTR, jsonArray);
            saveSearchesJson(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateVersion() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(VERSION_KEY, VERSION);
        editor.apply();
    }

    private void cleanSearches() {
        preferences.edit().remove(SEARCHES_KEY).apply();
    }

    private void initJsonPath() {
        Configuration.setDefaults(new Configuration.Defaults() {
            @Override
            public JsonProvider jsonProvider() {
                return new JsonOrgJsonProvider();
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }

            @Override
            public MappingProvider mappingProvider() {
                return new JsonOrgMappingProvider();
            }
        });
    }

    public void saveAddress(StandardizedAddress address) {
        JSONObject searches = getSearches();

        if (searches == null) return;

        if (findByStreetCode(address.getStreetCode()) != null) {
            reorderAddress(searches, address);

            return;
        }

        try {
            JSONArray results = searches.getJSONArray(SEARCHES_ATTR);
            List<StandardizedAddress> addresses = new ArrayList<>();

            for (int index = 0; index < results.length(); index++) {
                addresses.add(parseAddress(results.getJSONObject(index)));
            }

            addresses.add(0, address);

            if (addresses.size() >= 10) {
                addresses.remove(addresses.size() - 1);
            }

            results = new JSONArray();

            for (int index = 0; index < addresses.size(); index++) {
                StandardizedAddress newAddress = addresses.get(index);
                JSONObject search = convertAddressToJson(newAddress);

                if (search != null) results.put(search);
            }

            searches.put(SEARCHES_ATTR, results);
            saveSearchesJson(searches.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getSearches() {
        String searchesJson = preferences.getString(SEARCHES_KEY, "");

        try {
            return new JSONObject(searchesJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void reorderAddress(JSONObject searches, StandardizedAddress address) {
        try {
            JSONArray results = searches.getJSONArray(SEARCHES_ATTR);
            List<StandardizedAddress> addresses = new ArrayList<>();

            addresses.add(address);

            for (int index = 0; index < results.length(); index++) {
                StandardizedAddress oldAddress = parseAddress(results.getJSONObject(index));

                if (oldAddress != null && oldAddress.getStreetCode() != address.getStreetCode()) addresses.add(oldAddress);
            }

            results = new JSONArray();

            for (int index = 0; index < addresses.size(); index++) {
                StandardizedAddress newAddress = addresses.get(index);
                JSONObject search = convertAddressToJson(newAddress);

                if (search != null) results.put(search);
            }

            searches.put(SEARCHES_ATTR, results);
            saveSearchesJson(searches.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject convertAddressToJson(StandardizedAddress address) {
        try {
            JSONObject jAddress = new JSONObject();

            jAddress.put(STREET_CODE_ATTR, address.getStreetCode());
            jAddress.put(STREET_NUMBER_ATTR, address.getNumber());
            jAddress.put(STREET_NAME_ATTR, address.getStreetName());
            jAddress.put(ADDRESS_NAME_ATTR, address.getName());
            jAddress.put(STREET_CORNER_NAME_ATTR, address.getStreetCornerName());
            jAddress.put(TYPE_ATTR, address.getType());
            jAddress.put(PLACE_NAME, address.getPlaceName());
            jAddress.put(COD_PARTIDO, address.getCityCode());

            if (address.getLocation() != null) {
                jAddress.put(X_ATTR, address.getLocation().getX());
                jAddress.put(Y_ATTR, address.getLocation().getY());
            }

            return jAddress;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getSearchesJson() {
        return preferences.getString(SEARCHES_KEY, "");
    }

    public Observable<List<StandardizedAddress>> getAddress() {
        return Observable.unsafeCreate(new Observable.OnSubscribe<List<StandardizedAddress>>() {
            @Override
            public void call(Subscriber<? super List<StandardizedAddress>> subscriber) {
                JSONObject searches = getSearches();
                List<StandardizedAddress> addresses = new ArrayList<>();

                try {
                    JSONArray results = searches.getJSONArray(SEARCHES_ATTR);

                    for (int index = 0; index < results.length(); index++) {
                        StandardizedAddress result = parseAddress(results.getJSONObject(index));

                        if (result != null) {
                            addresses.add(result);
                        }
                    }
                } catch (JSONException e) {
                    subscriber.onError(e);
                }

                subscriber.onNext(addresses);
                subscriber.onCompleted();
            }

        });
    }

    public StandardizedAddress findByStreetCode(int streetCode) {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(getSearchesJson());
        JSONArray addresses = JsonPath.read(document, "$.searchs[?(@.street_code == '" + streetCode + "')]");

        try {
            return parseAddress(addresses.getJSONObject(0));
        } catch (Exception e) {
            Log.d("ERROR", addresses.toString());
            e.printStackTrace();
        }

        return null;
    }

    /*
    public Observable<NormalizeResponse> findByQuery(final String query){
        return Observable.create(new Observable.OnSubscribe<NormalizeResponse>() {
            @Override
            public void call(Subscriber<? super NormalizeResponse> subscriber) {
                NormalizeResponse response = new NormalizeResponse();

                response.setSource(NormalizeResponse.CACHE);

                Object document = Configuration.defaultConfiguration().jsonProvider().parse(getSearchesJson());
                DocumentContext dc = JsonPath.parse(document);
                JSONArray addresses = dc.read("$.searchs[?]", JSONArray.class, new SearchAddressPredicate(query));

                try {
                    List<StandardizedAddress> addressList = new ArrayList<>();

                    for (int index = 0; index < addresses.length(); index++) {
                        addressList.add(parseAddress(addresses.getJSONObject(index)));
                    }

                    response.setAddressList(addressList);
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    */

    private StandardizedAddress parseAddress(JSONObject jAddress) {
        StandardizedAddress address = new StandardizedAddress();

        // address.setType(jAddress.has(TYPE_ATTR) ? GcbaUtils.getString(jAddress, TYPE_ATTR) : "calle_altura");
        address.setType(GcbaUtils.getString(jAddress, TYPE_ATTR));
        address.setName(GcbaUtils.getString(jAddress, ADDRESS_NAME_ATTR));
        address.setNumber(GcbaUtils.getInt(jAddress, STREET_NUMBER_ATTR));
        address.setStreetName(GcbaUtils.getString(jAddress, STREET_NAME_ATTR));
        address.setStreetCornerName(GcbaUtils.getString(jAddress, STREET_CORNER_NAME_ATTR));
        address.setStreetCode(GcbaUtils.getInt(jAddress, STREET_CODE_ATTR));
        address.setCityCode(GcbaUtils.getString(jAddress, COD_PARTIDO));
        address.setPlaceName(GcbaUtils.getString(jAddress, PLACE_NAME));

        AddressLocation location = new AddressLocation();

        location.setX(GcbaUtils.getDouble(jAddress, X_ATTR));
        location.setY(GcbaUtils.getDouble(jAddress, Y_ATTR));
        address.setLocation(location);

        return address;
    }

    private void saveSearchesJson(String json) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SEARCHES_KEY, json);
        editor.apply();
    }

    private class SearchAddressPredicate implements Predicate {

        private String[] queries;

        public SearchAddressPredicate(String query) {
            queries = query.split("\\s+");
        }

        @Override
        public boolean apply(PredicateContext ctx) {
            JSONObject address = ctx.item(JSONObject.class);
            int ocurrences = 0;

            try {
                String name = address.getString(ADDRESS_NAME_ATTR).toLowerCase();

                for (int index = 0; index < queries.length; index++) {
                    if (name.contains(queries[index])) {
                        ocurrences++;
                    } else {
                        break;
                    }
                }

                return ocurrences == queries.length;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

    }

}
