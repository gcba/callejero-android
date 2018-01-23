package com.gcba.callejero;

import com.gcba.callejero.model.StandardizedAddress;

/**
 * Created by julian on 15/08/17.
 */

public interface LocationCallBack {

    public void onSuccess(StandardizedAddress address);
    public void onError(Throwable error);

}
