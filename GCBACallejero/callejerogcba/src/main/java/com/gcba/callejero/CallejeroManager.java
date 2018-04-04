package com.gcba.callejero;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;

import com.gcba.callejero.api.ApiManager;
import com.gcba.callejero.model.AddressLatLong;
import com.gcba.callejero.model.AddressLocation;
import com.gcba.callejero.model.NormalizeResponse;
import com.gcba.callejero.model.Places.Places;
import com.gcba.callejero.model.Places.PlacesObjectContent;
import com.gcba.callejero.model.StandardizedAddress;
import com.gcba.callejero.ui.CallejeroView;
import com.gcba.callejero.ui.LocationCallBackPlaces;
import com.gcba.callejero.ui.LocationCallbackPlacesObjectCntent;
import com.gcba.callejero.ui.search.AddressSearchActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julian on 15/08/17.
 */

public class CallejeroManager {

    static final int SEARCH_REQUEST_CODE = 1001;
    static final int REQUEST_PIN_CODE = 15001;
    static final int REQUEST_CURRENT_CODE = 15002;

    private SparseArray<SelectionCallback> callbacks = new SparseArray<SelectionCallback>();
    private ApiManager api = new ApiManager();

    public static CallejeroManager instance;

    public static CallejeroManager getInstance() {
        if (instance == null) {
            instance = new CallejeroManager();
        }

        return instance;
    }

    private CallejeroManager() {}

    /**
     * Devuelve un objeto address después de recibir la latitud y la longitud (SÓLO CAPITAL)
     *
     * @param location
     * @param locationCallBack
     */
    public void loadAddressLaqtLongFromCABA(final AddressLocation location, final LocationCallBack locationCallBack) {
        api.addressWithLatLong(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AddressLatLong>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        locationCallBack.onError(e);
                    }

                    @Override
                    public void onNext(AddressLatLong addressLatLong) {
                        String[] s = addressLatLong.getDoor().split("\\s+");
                        int number = Integer.parseInt(s[s.length - 1]);
                        StandardizedAddress address = new StandardizedAddress();
                        address.setNumber(number);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < s.length - 1; i++) {
                            sb.append(s[i]);
                            sb.append(" ");
                        }

                        address.setLocation(location);
                        address.setName(addressLatLong.getDoor() + ", CABA");
                        address.setStreetName(String.valueOf(sb));
                        address.setType(CallejeroCTE.CALLE_ALTURA);
                        address.setStreetCornerName(addressLatLong.getCorner());
                        address.setCityCode("CABA");

                        locationCallBack.onSuccess(address);
                    }
                });
    }

    /**
     * Devuelve un objeto address en el que la dirección es siempre la esquina para capital y provincia.
     *
     * @param location
     * @param locationCallBack
     */
    public void loadAddressLatLong(AddressLocation location, final LocationCallBack locationCallBack) {
        api.normalizeLocation(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<StandardizedAddress>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        locationCallBack.onError(e);
                    }

                    @Override
                    public void onNext(StandardizedAddress standardizedAddress) {
                        locationCallBack.onSuccess(standardizedAddress);
                    }

                });
    }

    public void loadPlaces(String query, final LocationCallBackPlaces locationCallBack) {
        api.searchPlaces(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Places>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        locationCallBack.onError(e);
                    }

                    @Override
                    public void onNext(Places places) {
                        locationCallBack.onSuccess(places);
                    }
                });
    }


    /**
     * El primer parámetro es la calle que se desea normalizar
     */
    public void loadPlacesObjectContent(String query, final LocationCallbackPlacesObjectCntent locationCallBack) {
        api.searchPlacesObjectContent(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<PlacesObjectContent>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        locationCallBack.onError(e);
                    }

                    @Override
                    public void onNext(PlacesObjectContent places) {
                        locationCallBack.onSuccess(places);
                    }
                });
    }


    /**
     * Devuelve una dirección normalizada después de ingresarle un string válido de calle. Funciona tanto para provincia como para capital en base al boolean onlyFromCaba
     *
     * @param query
     * @param onlyFromCABA
     * @param searchCallback
     */
    public void normalizeQuery(String query, boolean onlyFromCABA, final SearchCallback searchCallback) {
        api.normalizeQuery(query, onlyFromCABA)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<NormalizeResponse>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        searchCallback.onError(e);
                    }

                    @Override
                    public void onNext(NormalizeResponse normalizeResponse) {

                        searchCallback.onSuccess(normalizeResponse);
                    }
                });
    }

    /**
     * Inicia la búsqueda de una dirección, mostrando opciones de CABA y Gran Buenos Aires sin poder elegir por pin
     *
     * @param context
     * @param requestCode
     * @param callback
     */
    public void startSearch(Activity context, int requestCode, SelectionCallback callback) {
        startSearch(context, requestCode, callback);
    }

    /**
     * Inicia la búsqueda de una dirección
     *
     * @param context
     * @param requestCode
     * @param callback
     */
    public void startSearch(Activity context, CallejeroOptions callejeroOptions, int requestCode, SelectionCallback callback) {
        callbacks.put(requestCode, callback);

        Intent intent = new Intent(context, AddressSearchActivity.class);

        intent.putExtra(CallejeroCTE.SHOW_ADDRESS_FROM_CABA, callejeroOptions.getShowOnlyFromCaba());
        intent.putExtra(CallejeroCTE.SHOW_PIN, callejeroOptions.getShowPin());
        intent.putExtra(CallejeroCTE.SHOW_LABEL, callejeroOptions.getShowLabel());
        intent.putExtra(CallejeroCTE.SHOW_PLACES, callejeroOptions.getShowPlaces());
        intent.putExtra(CallejeroView.REQUEST_ID_DATA, requestCode);

        context.startActivityForResult(intent, SEARCH_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != SEARCH_REQUEST_CODE) {
            return;
        }

        int requestId = data.getIntExtra(CallejeroView.REQUEST_ID_DATA, 0);
        SelectionCallback callbackSelection = callbacks.get(requestId);

        if (callbackSelection == null) {
            return;
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            callbackSelection.onCancel();
        } else if (resultCode == REQUEST_PIN_CODE) {
            callbackSelection.onSelectedPin();
        } else if (resultCode == REQUEST_CURRENT_CODE) {
            callbackSelection.onSelectionLabel(data.getExtras().getString("current"));
        } else {
            callbackSelection.onAddressSelection((StandardizedAddress) data.getParcelableExtra(CallejeroCTE.STANDARDIZED_ADDRESS_DATA));
        }
    }

}
