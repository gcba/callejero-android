package com.gcba.callejero.model.Places;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gcbamobile on 21/12/17.
 */

public class Places implements Parcelable {

    @SerializedName("clasesEncontradas")
    private ArrayList<PlaceClasesEncontradas> clasesEncontradas;

    @SerializedName("instancias")
    private ArrayList<PlaceInstancias> instancias;

    @SerializedName("total")
    private String total;

    @SerializedName("totalFull")
    private String totalFull;

    public Places(Parcel in) {
        total = in.readString();
        totalFull = in.readString();
    }

    @Override
    public String toString() {
        return "Places{" +
                "clasesEncontradas=" + clasesEncontradas +
                ", instancias=" + instancias +
                ", total='" + total + '\'' +
                ", totalFull='" + totalFull + '\'' +
                '}';
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public Places() {
        this.clasesEncontradas = clasesEncontradas;
        this.instancias = instancias;
        this.total = total;
        this.totalFull = totalFull;
    }

    public ArrayList<PlaceClasesEncontradas> getClasesEncontradas() {
        return clasesEncontradas;
    }

    public void setClasesEncontradas(ArrayList<PlaceClasesEncontradas> clasesEncontradas) {
        this.clasesEncontradas = clasesEncontradas;
    }

    public ArrayList<PlaceInstancias> getInstancias() {
        return instancias;
    }

    public void setInstancias(ArrayList<PlaceInstancias> instancias) {
        this.instancias = instancias;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalFull() {
        return totalFull;
    }

    public void setTotalFull(String totalFull) {
        this.totalFull = totalFull;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(total);
        dest.writeString(totalFull);
    }

}
