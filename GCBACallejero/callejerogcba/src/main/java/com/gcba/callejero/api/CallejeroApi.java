package com.gcba.callejero.api;

import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.Places;
import com.gcba.callejero.model.Places.PlacesObjectContent;
import com.gcba.callejero.model.StandardizedAddress;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by ignacio on 08/06/17.
 */

public interface CallejeroApi {

    @GET
    Observable<NormalizeResponse> normalizeQuery(@Url String url,
                                                 @Query("direccion") String query,
                                                 @Query("geocodificar") boolean geocoding,
                                                 @Query("exclude") String exclude);

    @GET
    Observable<StandardizedAddress> normalizeLocation(@Url String url,
                                                      @Query("lng") double longitude,
                                                      @Query("lat") double latitude);

    @GET
    Observable<ResponseBody> addressWithLatLong(@Url String url,
                                                @Query("x") double longitude,
                                                @Query("y") double latitude);

    @GET
    Observable<Places> searchPlaces(@Url String url,
                                    @Query("texto") String query,
                                    @Query("limit") String limit);

    @GET
    Observable<PlacesObjectContent> searchPlacesobjectContent(@Url String url,
                                                              @Query("id") String id,
                                                              @Query("srid") String srid);

}
