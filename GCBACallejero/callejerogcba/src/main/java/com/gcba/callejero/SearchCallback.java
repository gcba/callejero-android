package com.gcba.callejero;

import com.gcba.callejero.model.NormalizeResponse;

/**
 * Created by julian on 17/08/17.
 */

public interface SearchCallback {
    public void onSuccess(NormalizeResponse normalize);
    public void onError(Throwable error);

}
