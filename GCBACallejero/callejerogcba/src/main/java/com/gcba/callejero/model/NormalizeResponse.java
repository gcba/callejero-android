package com.gcba.callejero.model;

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by ignacio on 08/06/17.
 */

public class NormalizeResponse {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NETWORK, CACHE})
    public @interface SOURCE { }

    public static final int NETWORK = 0;
    public static final int CACHE = 1;

    private int source;

    @SerializedName("direccionesNormalizadas")
    private List<StandardizedAddress> addressList;

    public List<StandardizedAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<StandardizedAddress> addressList) {
        this.addressList = addressList;
    }

    public void setSource(@SOURCE int source) {
        this.source = source;
    }

    public boolean fromNetwork() {
        return source == NETWORK;
    }

    public boolean fromCache() {
        return source == CACHE;
    }

}
