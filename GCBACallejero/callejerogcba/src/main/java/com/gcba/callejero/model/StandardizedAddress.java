package com.gcba.callejero.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gcba.callejero.CallejeroCTE;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ignacio on 08/06/17.
 */

public class StandardizedAddress implements Parcelable{

    @SerializedName("altura")
    private int number;

    @SerializedName("cod_calle")
    private int streetCode;

    @SerializedName("cod_partido")
    private String cityCode;

    @SerializedName("direccion")
    private String name;

    @SerializedName("nombre_calle")
    private String streetName;

    @SerializedName("nombre_calle_cruce")
    private String streetCornerName;

    @SerializedName("tipo")
    private String type;

    @SerializedName("coordenadas")
    private AddressLocation location;

    @SerializedName("placeName")
    private String placeName;

    @Override
    public String toString() {
        return "StandardizedAddress{" +
                "number=" + number +
                ", streetCode=" + streetCode +
                ", cityCode='" + cityCode + '\'' +
                ", name='" + name + '\'' +
                ", streetName='" + streetName + '\'' +
                ", streetCornerName='" + streetCornerName + '\'' +
                ", type='" + type + '\'' +
                ", location=" + location +
                ", placeName='" + placeName + '\'' +
                '}';
    }


    public StandardizedAddress(){}

    protected StandardizedAddress(Parcel in) {
        number = in.readInt();
        streetCode = in.readInt();
        cityCode = in.readString();
        name = in.readString();
        streetName = in.readString();
        streetCornerName = in.readString();
        type = in.readString();
        placeName = in.readString();
        location = in.readParcelable(AddressLocation.class.getClassLoader());

    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(int streetCode) {
        this.streetCode = streetCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetCornerName() {
        return streetCornerName;
    }

    public void setStreetCornerName(String streetCornerName) {
        this.streetCornerName = streetCornerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AddressLocation getLocation() {
        return location;
    }

    public void setLocation(AddressLocation location) {
        this.location = location;
    }

    public boolean isStreet(){
        return  type.equalsIgnoreCase(CallejeroCTE.CALLE);
    }

    public boolean isPlace(){
        return placeName != null && ! placeName.isEmpty();
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeInt(streetCode);
        dest.writeString(cityCode);
        dest.writeString(name);
        dest.writeString(streetName);
        dest.writeString(streetCornerName);
        dest.writeString(type);
        dest.writeString(placeName);
        dest.writeParcelable(location, flags);

    }

    public static final Creator<StandardizedAddress> CREATOR = new Creator<StandardizedAddress>() {
        @Override
        public StandardizedAddress createFromParcel(Parcel in) {
            return new StandardizedAddress(in);
        }

        @Override
        public StandardizedAddress[] newArray(int size) {
            return new StandardizedAddress[size];
        }
    };
}
