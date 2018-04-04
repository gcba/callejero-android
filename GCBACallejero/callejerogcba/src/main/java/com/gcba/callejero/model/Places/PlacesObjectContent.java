package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gcbamobile on 22/12/17.
 * Esta clase es el modelo para la respuesta del servicio epok getObjectContent <br>
 * http://epok.buenosaires.gob.ar/getObjectContent/?id=dependencias_culturales|547
 */

public class PlacesObjectContent implements Parcelable {

    @SerializedName("direccionNormalizada")
    private String direccionNormalizada;

    @SerializedName("contenido")
    private ArrayList<PlaceContenidos> contenido;

    @SerializedName("fuente")
    private String fuente;

    @SerializedName("claseId")
    private String claseId;

    @SerializedName("clase")
    private String clase;

    @SerializedName("fechaAlta")
    private String fechaAlta;

    @SerializedName("fechaActualizacion")
    private String fechaActualizacion;

    @SerializedName("idForaneo")
    private String idForaneo;

    @SerializedName("fechaUltimaModificacion")
    private String fechaUltimaModificacion;

    @SerializedName("ubicacion")
    private PlacesLocations ubicacion;

    @SerializedName("id")
    private String id;

    public PlacesObjectContent() { }

    @Override
    public String toString() {
        return "PlacesObjectContent{" +
                "direccionNormalizada='" + direccionNormalizada + '\'' +
                ", contenido=" + contenido +
                ", fuente='" + fuente + '\'' +
                ", claseId='" + claseId + '\'' +
                ", clase='" + clase + '\'' +
                ", fechaAlta='" + fechaAlta + '\'' +
                ", fechaActualizacion='" + fechaActualizacion + '\'' +
                ", idForaneo='" + idForaneo + '\'' +
                ", fechaUltimaModificacion='" + fechaUltimaModificacion + '\'' +
                ", ubicacion=" + ubicacion +
                ", id='" + id + '\'' +
                '}';
    }

    public String getDireccionNormalizada() {
        return direccionNormalizada;
    }

    public void setDireccionNormalizada(String direccionNormalizada) {
        this.direccionNormalizada = direccionNormalizada;
    }

    public ArrayList<PlaceContenidos> getContenido() {
        return contenido;
    }

    public void setContenido(ArrayList<PlaceContenidos> contenido) {
        this.contenido = contenido;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
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

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getIdForaneo() {
        return idForaneo;
    }

    public void setIdForaneo(String idForaneo) {
        this.idForaneo = idForaneo;
    }

    public String getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(String fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public PlacesLocations getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(PlacesLocations ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlacesObjectContent(Parcel in) {
        direccionNormalizada = in.readString();
        fuente = in.readString();
        claseId = in.readString();
        clase = in.readString();
        fechaAlta = in.readString();
        fechaActualizacion = in.readString();
        idForaneo = in.readString();
        fechaUltimaModificacion = in.readString();
        id = in.readString();
    }

    public static final Creator<PlacesObjectContent> CREATOR = new Creator<PlacesObjectContent>() {
        @Override
        public PlacesObjectContent createFromParcel(Parcel in) {
            return new PlacesObjectContent(in);
        }

        @Override
        public PlacesObjectContent[] newArray(int size) {
            return new PlacesObjectContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(direccionNormalizada);
        dest.writeString(fuente);
        dest.writeString(claseId);
        dest.writeString(clase);
        dest.writeString(fechaAlta);
        dest.writeString(fechaActualizacion);
        dest.writeString(idForaneo);
        dest.writeString(fechaUltimaModificacion);
        dest.writeString(id);
    }

}
