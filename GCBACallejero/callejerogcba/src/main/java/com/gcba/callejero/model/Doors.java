package com.gcba.callejero.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julian on 10/08/17.
 */

public class Doors {

    @SerializedName("calle")
    private String streetName;

    @SerializedName("altura")
    private Integer streetNumber;

    public String getStreetName() {
        return streetName;
    }

    public Doors setStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public Doors setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

}
