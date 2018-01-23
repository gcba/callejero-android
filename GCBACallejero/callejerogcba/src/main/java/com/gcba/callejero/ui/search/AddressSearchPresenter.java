package com.gcba.callejero.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gcba.callejero.CallejeroManager;
import com.gcba.callejero.SearchCallback;
import com.gcba.callejero.cache.CacheManager;
import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.PlaceClasesEncontradas;
import com.gcba.callejero.model.Places.PlaceInstancias;
import com.gcba.callejero.model.Places.Places;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.LocationCallBackPlaces;

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
    public AddressSearchPresenter(Context context){
        cacheManager = new CacheManager(context);
    }
    public void onAttachView(AddressSearchView view){
        this.view = view;
    }
    public void onDetachView(){
        this.view = null;
    }
    public void recents(){

        cacheManager.getAddress()
                .delay(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<StandardizedAddress>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<StandardizedAddress> addresses) {
                        if(view != null)
                            view.onResultSuccess("", addresses);
                    }
                });
    }

    public void search(final String query, boolean onlyFromCABA,boolean showPlaces, CharSequence charSequence){
        if(query == null || query.isEmpty()){

            if (view != null){
                view.onEmptySearch();
                return;
            }
        }

        if(view != null){
            view.onStartSearch();
        }

        addressList = new ArrayList<>();

        searchNormalizeAdress(query, onlyFromCABA);

        searchPlaces(query, showPlaces, charSequence);

    }


    public void saveSelection(StandardizedAddress address){
        if (address != null){
            cacheManager.saveAddress(address);
        }
    }

    private void searchPlaces(final String query, boolean showPlaces, CharSequence charSequence) {
        if (charSequence.length() >= 4){

            if (showPlaces){
                CallejeroManager.getInstance().loadPlaces(query, new LocationCallBackPlaces() {
                    @Override
                    public void onSuccess(Places places) {

                        List<StandardizedAddress> addressListFromPlace = placesToStandardizedAddresses(places);

                        addResult(query,addressListFromPlace);

                    }

                    @NonNull
                    private List<StandardizedAddress> placesToStandardizedAddresses(Places places) {
                        ArrayList<PlaceInstancias> instancias = places.getInstancias();
                        List<StandardizedAddress> addressListFromPlace = new ArrayList<>();
                        for (PlaceInstancias object: instancias) {
                            StandardizedAddress adress = new StandardizedAddress();
                            adress.setName("P - " + object.getNombre() /*+ object.getId()*/);
                            adress.setType("PLACE");
                            adress.setIdPlaceInstance(object.getId());
                            addressListFromPlace.add(adress);
                        }
                        return addressListFromPlace;
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
            }

        }
    }

    private void searchNormalizeAdress(final String query, boolean onlyFromCABA) {
        CallejeroManager.getInstance().normalizeQuery(query, onlyFromCABA, new SearchCallback() {
            @Override
            public void onSuccess(NormalizeResponse normalize) {

                if (view == null)
                    return;

                if (normalize.getAddressList().isEmpty() && normalize.fromNetwork()) {
                    view.onEmptyResults();

                    return;
                }

                if (!normalize.getAddressList().isEmpty()) {
                    addResult(query,normalize.getAddressList());
                }

            }

            @Override
            public void onError(Throwable error) {
                view.onResultError();
            }
        });
    }

    private synchronized void addResult(final String query,List<StandardizedAddress> partialList){
        addressList.addAll(partialList);
        view.onResultSuccess(query, addressList);
    }
}
