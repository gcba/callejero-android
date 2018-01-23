package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gcbamobile on 22/12/17.
 */

public class PlaceClasesEncontradas  implements Parcelable {

    @SerializedName("nombreId")
    private String nombreId;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("total")
    private  String total;

    @SerializedName("id")
    private String id;

    @SerializedName("nombreNorm")
    private String nombreNorm;

    public PlaceClasesEncontradas(Parcel in) {
        nombreId = in.readString();
        nombre = in.readString();
        id = in.readString();
        nombreNorm = in.readString();
    }

    @Override
    public String toString() {
        return "PlaceClasesEncontradas{" +
                "nombreId='" + nombreId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", total='" + total + '\'' +
                ", id='" + id + '\'' +
                ", nombreNorm='" + nombreNorm + '\'' +
                '}';
    }

    public static final Creator<PlaceClasesEncontradas> CREATOR = new Creator<PlaceClasesEncontradas>() {
        @Override
        public PlaceClasesEncontradas createFromParcel(Parcel in) {
            return new PlaceClasesEncontradas(in);
        }

        @Override
        public PlaceClasesEncontradas[] newArray(int size) {
            return new PlaceClasesEncontradas[size];
        }
    };

    public PlaceClasesEncontradas() {

    }

    public String getNombreId() {
        return nombreId;
    }

    public void setNombreId(String nombreId) {
        this.nombreId = nombreId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreNorm() {
        return nombreNorm;
    }

    public void setNombreNorm(String nombreNorm) {
        this.nombreNorm = nombreNorm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreId);
        dest.writeString(nombre);
        dest.writeString(id);
        dest.writeString(nombreNorm);
    }
}
