package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gcbamobile on 22/12/17.
 */

public class PlaceInstancias  implements Parcelable {

    @SerializedName("headline")
    private String headline;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("claseId")
    private String claseId ;

    @SerializedName("clase")
    private String clase;

    @SerializedName("id")
    private String id;


    protected PlaceInstancias(Parcel in) {
        headline = in.readString();
        nombre = in.readString();
        claseId = in.readString();
        clase = in.readString();
        id = in.readString();
    }

    @Override
    public String toString() {
        return "PlaceInstancias{" +
                "headline='" + headline + '\'' +
                ", nombre='" + nombre + '\'' +
                ", claseId='" + claseId + '\'' +
                ", clase='" + clase + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public static final Creator<PlaceInstancias> CREATOR = new Creator<PlaceInstancias>() {
        @Override
        public PlaceInstancias createFromParcel(Parcel in) {
            return new PlaceInstancias(in);
        }

        @Override
        public PlaceInstancias[] newArray(int size) {
            return new PlaceInstancias[size];
        }
    };

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClaseId() {
        return claseId;
    }

    public void setClaseId(String claseId) {
        this.claseId = claseId;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(headline);
        dest.writeString(nombre);
        dest.writeString(claseId);
        dest.writeString(clase);
        dest.writeString(id);
    }
}
