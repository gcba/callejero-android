package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gcbamobile on 22/12/17.
 */

 public class PlacesLocations implements Parcelable {

    @SerializedName("centroide")
    private String controide ;

    @SerializedName("tipo")
    private String tipo ;

    private Double x = -58.4169274781971595;

    private Double y = -34.5588030592561353;


    public PlacesLocations(Parcel in) {
        controide = in.readString();
        tipo = in.readString();
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getControide() {
        return controide;
    }

    public void setControide(String controide) {
        this.controide = controide;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public static Creator<PlacesLocations> getCREATOR() {
        return CREATOR;
    }

    public PlacesLocations() {

    }

    @Override
    public String toString() {
        return "PlacesLocations{" +
                "controide='" + controide + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }

    public static final Creator<PlacesLocations> CREATOR = new Creator<PlacesLocations>() {
        @Override
        public PlacesLocations createFromParcel(Parcel in) {
            return new PlacesLocations(in);
        }

        @Override
        public PlacesLocations[] newArray(int size) {
            return new PlacesLocations[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(controide);
        dest.writeString(tipo);
    }
}
