package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gcbamobile on 22/12/17.
 */

class PlaceContenidos implements Parcelable{

    @SerializedName("nombreId")
    private String nombreId ;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("posicion")
    private String posicion;

    @SerializedName("valor")
    private String valor;

    protected PlaceContenidos(Parcel in) {
        nombreId = in.readString();
        nombre = in.readString();
        posicion = in.readString();
        valor = in.readString();
    }

    @Override
    public String toString() {
        return "PlaceContenidos{" +
                "nombreId='" + nombreId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", posicion='" + posicion + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }

    public static final Creator<PlaceContenidos> CREATOR = new Creator<PlaceContenidos>() {
        @Override
        public PlaceContenidos createFromParcel(Parcel in) {
            return new PlaceContenidos(in);
        }

        @Override
        public PlaceContenidos[] newArray(int size) {
            return new PlaceContenidos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreId);
        dest.writeString(nombre);
        dest.writeString(posicion);
        dest.writeString(valor);
    }
}
