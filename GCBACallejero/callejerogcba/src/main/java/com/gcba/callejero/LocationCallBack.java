package com.gcba.callejero;

import com.gcba.callejero.model.StandardizedAddress;

/**
 * Created by julian on 15/08/17.
 */

public interface LocationCallBack {

    void onSuccess(StandardizedAddress address);
    void onError(Throwable error);

}
