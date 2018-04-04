package com.gcba.callejero.ui;

import com.gcba.callejero.model.Places.PlacesObjectContent;

/**
 * Created by gcbamobile on 22/12/17.
 */

public interface LocationCallbackPlacesObjectCntent {

    void onSuccess(PlacesObjectContent placesTwo);
    void onError(Throwable error);

}
