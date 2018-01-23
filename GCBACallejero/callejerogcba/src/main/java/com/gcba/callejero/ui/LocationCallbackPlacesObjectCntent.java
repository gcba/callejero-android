package com.gcba.callejero.ui;

import com.gcba.callejero.model.Places.PlacesObjectContent;

/**
 * Created by gcbamobile on 22/12/17.
 */
public interface LocationCallbackPlacesObjectCntent {

    public void onSuccess(PlacesObjectContent placesTwo);
    public void onError(Throwable error);
}
