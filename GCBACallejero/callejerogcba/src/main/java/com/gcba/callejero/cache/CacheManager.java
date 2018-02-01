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

    static final String SEARCHS_KEY = "_SEARCHS_";

    static final String SEARCHS_ATTR = "searchs";

    static final String STREET_CODE_ATTR = "street_code";

    static final String STREET_NUMBER_ATTR = "street_number_code";

    static final String STREET_NAME_ATTR = "street_name";

    static final String STREET_CORNER_NAME_ATTR = "street_corner_name";

    static final String ADDRESS_NAME_ATTR = "address";

    static final String TYPE_ATTR = "type";

    static final String IDPLACES = "idPlace";

    static final String X_ATTR = "x";

    static final String Y_ATTR = "y";

    static final String COD_PARTIDO = "cityCode";


    private SharedPreferences preferences;

    public CacheManager(Context context){
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        init();
    }

    private void init(){
        initJsonPath();
        int currentVersion = preferences.getInt(VERSION_KEY, 0);
        if(currentVersion < VERSION){
            cleanSearchs();
            updateVersion();
        }
        if(preferences.contains(SEARCHS_KEY)){
            return;
        }

        try {
            JSONObject object = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            object.put(SEARCHS_ATTR, jsonArray);
            saveSearchsJson(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateVersion(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(VERSION_KEY, VERSION);
        editor.apply();
    }

    private void cleanSearchs(){
        preferences.edit().remove(SEARCHS_KEY).apply();
    }

    private void initJsonPath(){
        Configuration.setDefaults(new Configuration.Defaults(){
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

        JSONObject searchs = getSearchs();

        if(searchs == null)
            return;

        if(findByStreetCode(address.getStreetCode()) != null) {
            reorderAddress(searchs, address);
            return;
        }

        try {

            JSONArray results = searchs.getJSONArray(SEARCHS_ATTR);

            List<StandardizedAddress> addresses = new ArrayList<>();


            for (int index = 0; index < results.length(); index++){
                addresses.add(parseAddress(results.getJSONObject(index)));
            }

            addresses.add(0, address);

            if(addresses.size() >= 10){

                addresses.remove(addresses.size() - 1);

            }

            results = new JSONArray();

            for (int index = 0; index < addresses.size(); index++){

                StandardizedAddress newAddress = addresses.get(index);

                JSONObject search = convertAddresToJson(newAddress);

                if(search != null)
                    results.put(search);
            }

            searchs.put(SEARCHS_ATTR, results);

            saveSearchsJson(searchs.toString());

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    private JSONObject getSearchs(){

        String searchsJson = preferences.getString(SEARCHS_KEY, "");

        try {

            return new JSONObject(searchsJson);

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return null;

    }

    private void reorderAddress(JSONObject searchs, StandardizedAddress address){

        try {

            JSONArray results = searchs.getJSONArray(SEARCHS_ATTR);

            List<StandardizedAddress> addresses = new ArrayList<>();

            addresses.add(address);

            for (int index = 0; index < results.length(); index++){

                StandardizedAddress oldAddress = parseAddress(results.getJSONObject(index));

                if(oldAddress!=null && oldAddress.getStreetCode() != address.getStreetCode())

                    addresses.add(oldAddress);

            }

            results = new JSONArray();

            for (int index = 0; index < addresses.size(); index++){

                StandardizedAddress newAddress = addresses.get(index);

                JSONObject search = convertAddresToJson(newAddress);

                if(search != null)
                    results.put(search);
            }

            searchs.put(SEARCHS_ATTR, results);

            saveSearchsJson(searchs.toString());

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    private JSONObject convertAddresToJson(StandardizedAddress address){

        try {
            JSONObject jAddress = new JSONObject();


            jAddress.put(STREET_CODE_ATTR, address.getStreetCode());
            jAddress.put(STREET_NUMBER_ATTR, address.getNumber());
            jAddress.put(STREET_NAME_ATTR, address.getStreetName());
            jAddress.put(ADDRESS_NAME_ATTR, address.getName());
            jAddress.put(STREET_CORNER_NAME_ATTR, address.getStreetCornerName());
            jAddress.put(TYPE_ATTR, address.getType());
            jAddress.put(IDPLACES, address.getIdPlaceInstance());
            jAddress.put(COD_PARTIDO, address.getCityCode());




            if(address.getLocation() != null) {
                jAddress.put(X_ATTR, address.getLocation().getX());
                jAddress.put(Y_ATTR, address.getLocation().getY());
            }

            return jAddress;
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

        return null;
    }

    private String getSearchsJson(){

        return preferences.getString(SEARCHS_KEY,  "");

    }



    public Observable<List<StandardizedAddress>> getAddress(){

        return Observable.unsafeCreate(new Observable.OnSubscribe<List<StandardizedAddress>>() {

            @Override

            public void call(Subscriber<? super List<StandardizedAddress>> subscriber) {

                JSONObject searchs = getSearchs();

                List<StandardizedAddress> addresses = new ArrayList<>();

                try {

                    JSONArray results = searchs.getJSONArray(SEARCHS_ATTR);

                    for (int index = 0; index < results.length(); index++){

                        StandardizedAddress result = parseAddress(results.getJSONObject(index));

                        if(result != null){
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

    public StandardizedAddress findByStreetCode(int streetCode){

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(getSearchsJson());

        JSONArray addresses = JsonPath.read(document, "$.searchs[?(@.street_code == '" + streetCode + "')]");

        try {
            return parseAddress(addresses.getJSONObject(0));
        } catch (Exception e) {
            Log.d("ERROR",addresses.toString());
            e.printStackTrace();
        }

        return null;

    }

/*    public Observable<NormalizeResponse> findByQuery(final String query){

        return Observable.create(new Observable.OnSubscribe<NormalizeResponse>() {

            @Override

            public void call(Subscriber<? super NormalizeResponse> subscriber) {

                NormalizeResponse response = new NormalizeResponse();
                response.setSource(NormalizeResponse.CACHE);

                Object document = Configuration.defaultConfiguration().jsonProvider().parse(getSearchsJson());

                DocumentContext dc = JsonPath.parse(document);

                JSONArray addresses = dc.read("$.searchs[?]", JSONArray.class, new SearchAddressPredicate(query));

                try {

                    List<StandardizedAddress> addressList = new ArrayList<>();

                    for (int index = 0; index < addresses.length(); index++){

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

    }*/







    private StandardizedAddress parseAddress(JSONObject jAddress){

        StandardizedAddress address = new StandardizedAddress();


        // address.setType(jAddress.has(TYPE_ATTR) ? GcbaUtils.getString(jAddress,TYPE_ATTR) : "calle_altura");
        address.setType(GcbaUtils.getString(jAddress,TYPE_ATTR));
        address.setName(GcbaUtils.getString(jAddress,ADDRESS_NAME_ATTR));
        address.setNumber(GcbaUtils.getInt(jAddress,STREET_NUMBER_ATTR));
        address.setStreetName( GcbaUtils.getString(jAddress,STREET_NAME_ATTR));
        address.setStreetCornerName(GcbaUtils.getString(jAddress,STREET_CORNER_NAME_ATTR));
        address.setStreetCode(GcbaUtils.getInt(jAddress,STREET_CODE_ATTR));
        address.setCityCode(GcbaUtils.getString(jAddress,COD_PARTIDO) );

        AddressLocation location = new AddressLocation();
        location.setX(GcbaUtils.getDouble(jAddress,X_ATTR));
        location.setY(GcbaUtils.getDouble(jAddress,Y_ATTR));
        address.setLocation(location);

        return address;
    }


    private void saveSearchsJson(String json){

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SEARCHS_KEY, json);

        editor.apply();

    }

    private class SearchAddressPredicate implements Predicate {

        private String[] queries;

        public SearchAddressPredicate(String query){

            queries = query.split("\\s+");

        }

        @Override

        public boolean apply(PredicateContext ctx) {

            JSONObject address = ctx.item(JSONObject.class);

            int ocurrences = 0;

            try {

                String name = address.getString(ADDRESS_NAME_ATTR).toLowerCase();

                for (int index = 0; index < queries.length; index++){

                    if(name.contains(queries[index])){

                        ocurrences++;

                    }else{

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
