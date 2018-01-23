package com.gcba.callejero.ui;

import com.gcba.callejero.model.Places.Places;


/**
 * Created by gcbamobile on 21/12/17.
 */

public interface LocationCallBackPlaces {

    public void onSuccess(Places places);
    public void onError(Throwable error);
}
