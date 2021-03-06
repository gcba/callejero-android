package com.gcba.callejero.api;

import com.gcba.callejero.CallejeroCTE;
import com.gcba.callejero.model.AddressLatLong;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.Places;
import com.gcba.callejero.model.Places.PlacesObjectContent;
import com.gcba.callejero.model.StandardizedAddress;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ignacio on 08/06/17.
 */

public class ApiManager {

    private String excludedCities = "almirante_brown,avellaneda,berazategui,berisso,canuelas,ensenada,escobar,esteban_echeverria,ezeiza," +
            "florencio_varela,general_rodriguez,general_san_martin,hurlingham,ituzaingo,jose_c_paz,la_matanza,lanus,la_plata," +
            "lomas_de_zamora,malvinas_argentinas,marcos_paz,merlo,moreno,moron,pilar,presidente_peron,quilmes,san_fernando," +
            "san_isidro,san_miguel,san_vicente,tigre,tres_de_febrero,vicente_lopez";

    private CallejeroApi api;

    public ApiManager() {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpBuilder.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://servicios.usig.buenosaires.gob.ar/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpBuilder.build())
                .build();

        api = retrofit.create(CallejeroApi.class);
    }

    public Observable<NormalizeResponse> normalizeQuery(String query, boolean onlyFromCABA) {
        return api.normalizeQuery(CallejeroCTE.URL_NORMALIZE, query, true, onlyFromCABA ? excludedCities : "");
    }

    public Observable<StandardizedAddress> normalizeLocation(AddressLocation location) {
        return api.normalizeLocation(CallejeroCTE.URL_NORMALIZE, location.getX(), location.getY());
    }

    public Observable<AddressLatLong> addressWithLatLong(AddressLocation location) {
        return api.addressWithLatLong(CallejeroCTE.URL_LATLONG, location.getX(), location.getY()).map(parseResponse());
    }

    public Observable<Places> searchPlaces(String query) {
        return api.searchPlaces(CallejeroCTE.URL_EPOK_BUENOSAIRES_GOB_AR_BUSCAR, query, "10");
    }

    public Observable<PlacesObjectContent> searchPlacesObjectContent(String idQuery) {
        return api.searchPlacesObjectContent(CallejeroCTE.URL_EPOK_BUENOSAIRES_GOB_AR_GET_OBJECT_CONTENT, idQuery, "4326");
    }

    private Func1<ResponseBody, AddressLatLong> parseResponse() {
        return new Func1<ResponseBody, AddressLatLong>() {
            @Override
            public AddressLatLong call(ResponseBody responseBody) {
                try {
                    String response = responseBody.string().replace("(", " ").replace(")", " ");

                    return new Gson().fromJson(response, AddressLatLong.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

}
