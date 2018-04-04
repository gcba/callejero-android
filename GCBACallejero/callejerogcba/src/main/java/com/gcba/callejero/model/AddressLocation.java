package com.gcba.callejero.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ignacio on 08/06/17.
 */

public class AddressLocation implements Parcelable {

    private double x;
    private double y;

    public AddressLocation() { }

    protected AddressLocation(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    @Override
    public String toString() {
        return "AddressLocation{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    public static final Creator<AddressLocation> CREATOR = new Creator<AddressLocation>() {
        @Override
        public AddressLocation createFromParcel(Parcel in) {
            return new AddressLocation(in);
        }

        @Override
        public AddressLocation[] newArray(int size) {
            return new AddressLocation[size];
        }
    };

}
