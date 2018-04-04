package com.gcba.callejero.ui;

import com.gcba.callejero.model.Places.Places;

/**
 * Created by gcbamobile on 21/12/17.
 */

public interface LocationCallBackPlaces {

    void onSuccess(Places places);
    void onError(Throwable error);

}
