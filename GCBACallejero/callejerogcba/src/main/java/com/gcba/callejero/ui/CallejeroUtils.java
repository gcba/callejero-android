package com.gcba.callejero.ui;

/**
 * Created by leonardopalazzo on 18/01/2018.
 */

public class CallejeroUtils {


    static public Double parseX(String point){
        return CallejeroUtils.parseXoY(point,0);
    }

    static public Double parseY(String point){
        return CallejeroUtils.parseXoY(point,1);
    }

    static private Double parseXoY(String point,int indice){
        Double result = new Double(0);

        if (point!=null){
            String[] xy = point.substring(point.indexOf("(")+1,point.indexOf(")")-1).split("\\s+");
            result = new Double(xy[indice]);
        }

        return result;
    }

}