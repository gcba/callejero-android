package com.gcba.callejero.ui.search;

import android.content.Context;
import android.util.Log;

import com.gcba.callejero.CallejeroCTE;
import com.gcba.callejero.CallejeroManager;
import com.gcba.callejero.SearchCallback;
import com.gcba.callejero.cache.CacheManager;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.PlaceInstancias;
import com.gcba.callejero.model.Places.Places;
import com.gcba.callejero.model.Places.PlacesObjectContent;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.LocationCallBackPlaces;
import com.gcba.callejero.ui.LocationCallbackPlacesObjectCntent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ignacio on 08/06/17.
 */

public class AddressSearchPresenter {

    private CacheManager cacheManager;
    private AddressSearchView view;
    private List<StandardizedAddress> addressList = new ArrayList<>();

    public AddressSearchPresenter(Context context) {
        cacheManager = new CacheManager(context);
    }

    public void onAttachView(AddressSearchView view) {
        this.view = view;
    }

    public void onDetachView() {
        this.view = null;
    }

    public void recents() {
        cacheManager.getAddress()
                .delay(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<StandardizedAddress>>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onNext(List<StandardizedAddress> addresses) {
                        if (view != null) view.onResultSuccess("", addresses);
                    }
                });
    }

    public void search(final String query, boolean onlyFromCABA, boolean showPlaces, CharSequence charSequence) {
        if (query == null || query.isEmpty()) {
            if (view != null) {
                view.onEmptySearch();

                return;
            }
        }

        if (view != null) {
            view.onStartSearch();
        }

        addressList = new ArrayList<>();

        searchNormalizeAddress(query, onlyFromCABA);

        if (charSequence.length() >= 4 && showPlaces) searchPlaces(query, showPlaces, charSequence);
    }

    public void saveSelection(StandardizedAddress address) {
        if (address != null) {
            cacheManager.saveAddress(address);
        }
    }

    /**
     * Busca lugares en epok y los normaliza
     */
    private void searchPlaces(final String query, boolean showPlaces, CharSequence charSequence) {
        CallejeroManager.getInstance().loadPlaces(query, new LocationCallBackPlaces() {
            @Override
            public void onSuccess(Places places) {
                for (PlaceInstancias instance : places.getInstancias()) {
                    Log.i("CALLEJERO ", "place encontrado " + instance.getNombre());
                    intanceToStandarizedAddress(instance.getNombre(), instance);
                }
            }

            @Override
            public void onError(Throwable error) { }
        });
    }

    /**
     * Busca instancias de lugar, si tiene una dirección la normaliza y agrega como standardized address, sino la descarta
     */
    private void intanceToStandarizedAddress(final String placeName, PlaceInstancias instance) {
        CallejeroManager.getInstance().loadPlacesObjectContent(instance.getId(), new LocationCallbackPlacesObjectCntent() {
            @Override
            public void onSuccess(final PlacesObjectContent objectContent) {
                if (!objectContent.getDireccionNormalizada().isEmpty()) {
                    CallejeroManager.getInstance().normalizeQuery(objectContent.getDireccionNormalizada(), Boolean.FALSE, new SearchCallback() {
                        @Override
                        public void onSuccess(NormalizeResponse normalize) {
                            if (normalize.getAddressList().size() > 0) {
                                StandardizedAddress sa = normalize.getAddressList().get(0);

                                sa.setPlaceName(placeName);
                                sa.setName(placeName);

                                Log.i("CALLEJERO", "resultado de la normalización " + sa.getStreetName());
                                addResult(objectContent.getDireccionNormalizada(), sa);
                            }else{
                                if ("sitios_de_interes|172".equalsIgnoreCase(objectContent.getId())){
                                    Log.d("CALLEJERO", " no pudo normalizar lugar");
                                    AddressLocation al=new AddressLocation();
                                    al.setX(-34.809370);
                                    al.setY(-58.543533);

                                    StandardizedAddress sa = new StandardizedAddress();
                                    sa.setName("Aeropuerto Internacional de Ezeiza");
                                    sa.setPlaceName("Aeropuerto Internacional de Ezeiza");
                                    sa.setLocation(al);
                                    sa.setType(CallejeroCTE.CALLE_CALLE);

                                    addResult(objectContent.getDireccionNormalizada(),sa);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            Log.d("CALLEJERO", " error normalizar lugar");
                        }
                    });
                }else{
                    Log.d("CALLEJERO","descarto lugar " + objectContent.getId() );


                }
            }

            @Override
            public void onError(Throwable error) {
                Log.d("CALLEJERO", " no pudo buscar lugar");
            }
        });
    }

    private void searchNormalizeAddress(final String query, boolean onlyFromCABA) {
        CallejeroManager.getInstance().normalizeQuery(query, onlyFromCABA, new SearchCallback() {
            @Override
            public void onSuccess(NormalizeResponse normalize) {
                if (view == null) return;

                if (normalize.getAddressList().isEmpty() && normalize.fromNetwork()) {
                    view.onEmptyResults();

                    return;
                }

                if (!normalize.getAddressList().isEmpty()) {
                    addResult(query, normalize.getAddressList());
                }
            }

            @Override
            public void onError(Throwable error) {
                if (view == null) return;

                view.onResultError();
            }
        });
    }

    private synchronized void addResult(final String query, List<StandardizedAddress> partialList) {
        addressList.addAll(partialList);
        view.onResultSuccess(query, addressList);
    }

    private synchronized void addResult(final String query, StandardizedAddress sa) {
        addressList.add(sa);
        view.onResultSuccess(query, addressList);
    }

}
