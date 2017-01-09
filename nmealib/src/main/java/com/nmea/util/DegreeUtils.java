package com.nmea.util;

/**
 * Created by Yang on 2017/1/3.
 */

public class DegreeUtils {
    //3723.2475
    public static String degreeConvert(String string){
        String str = null;
        double degree = Double.valueOf(string);
        int d = (int) (degree / 100);
        double minutes = degree % 100;
        minutes /= 60;
        degree = d + minutes;
        str = String.format("%.6f",degree);

        return str;
    }
    public static void main(String[] args){
        System.out.println(degreeConvert("-3723.2475"));
    }
}
