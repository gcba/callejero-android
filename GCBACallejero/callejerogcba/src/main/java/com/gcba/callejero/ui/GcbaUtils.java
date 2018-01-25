package com.gcba.callejero.ui;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leonardopalazzo on 18/01/2018.
 */

public class GcbaUtils {


    static public Double parseX(String point){
        return GcbaUtils.parseXoY(point,0);
    }

    static public Double parseY(String point){
        return GcbaUtils.parseXoY(point,1);
    }

    static private Double parseXoY(String point,int indice){
        Double result = new Double(0);

        if (point!=null){
            String[] xy = point.substring(point.indexOf("(")+1,point.indexOf(")")-1).split("\\s+");
            result = new Double(xy[indice]);
        }

        return result;
    }

    static public String getString(JSONObject json, String key){
        try {
            if (json.has(key))
                return  json.getString(key);
            else
                Log.w("parser","en el json no se encontro Key " + key);

        } catch (JSONException e) {
            Log.w("parser", "fallo al buscar Key " + key );
        }
        return "";
    }

    static public Integer getInt(JSONObject json,String key){
        try {
            if (json.has(key))
                return  json.getInt(key);
            else
                Log.w("parser","en el json no se encontro Key " + key);

        } catch (JSONException e) {
            Log.w("parser", "fallo al buscar Key " + key );
        }
        return null;
    }

}