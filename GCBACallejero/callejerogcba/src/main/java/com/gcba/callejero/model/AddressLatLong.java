package com.gcba.callejero.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by julian on 10/08/17.
 */

public class AddressLatLong {

    @SerializedName("parcela")
    private String plot;

    @SerializedName("puerta")
    private String door;

    @SerializedName("puerta_x")
    private String doorX;

    @SerializedName("puerta_y")
    private String doorY;

    @SerializedName("calle_alturas")
    private String streetNumbers;

    @SerializedName("esquina")
    private String corner;

    @SerializedName("metros_a_esquina")
    private String distanceCorner;

    @SerializedName("altura_par")
    private String streetAndNumberPair;

    @SerializedName("altura_impar")
    private String streetAndNumberOdd;

    public String getPlot() {
        return plot;
    }

    public AddressLatLong setPlot(String plot) {
        this.plot = plot;

        return this;
    }

    public String getDoor() {
        return door;
    }

    public AddressLatLong setDoor(String door) {
        this.door = door;

        return this;
    }

    public String getDoorX() {
        return doorX;
    }

    public AddressLatLong setDoorX(String doorX) {
        this.doorX = doorX;

        return this;
    }

    public String getDoorY() {
        return doorY;
    }

    public AddressLatLong setDoorY(String doorY) {
        this.doorY = doorY;

        return this;
    }

    public String getStreetNumbers() {
        return streetNumbers;
    }

    public AddressLatLong setStreetNumbers(String streetNumbers) {
        this.streetNumbers = streetNumbers;

        return this;
    }

    public String getCorner() {
        return corner;
    }

    public AddressLatLong setCorner(String corner) {
        this.corner = corner;

        return this;
    }

    public String getDistanceCorner() {
        return distanceCorner;
    }

    public AddressLatLong setDistanceCorner(String distanceCorner) {
        this.distanceCorner = distanceCorner;

        return this;
    }

    public String getStreetAndNumberPair() {
        return streetAndNumberPair;
    }

    public AddressLatLong setStreetAndNumberPair(String streetAndNumberPair) {
        this.streetAndNumberPair = streetAndNumberPair;

        return this;
    }

    public String getStreetAndNumberOdd() {
        return streetAndNumberOdd;
    }

    public AddressLatLong setStreetAndNumberOdd(String streetAndNumberOdd) {
        this.streetAndNumberOdd = streetAndNumberOdd;

        return this;
    }

}
