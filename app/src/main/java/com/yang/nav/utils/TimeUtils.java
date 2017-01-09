package com.yang.nav.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yang on 2016/12/16.
 */

public class TimeUtils {

    private static SimpleDateFormat Nor_sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat GPX_sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    private static SimpleDateFormat YMD_sf = new SimpleDateFormat("yyyy年MM月dd日", Locale.ENGLISH);
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日hh时mm分ss秒", Locale.ENGLISH);

    /**
     * 将毫秒值转化为格式化的时间字符串
     * @param millisecond
     * @return
     */
    public static String convertToNormalStr(Long millisecond) {
        Date d = new Date(millisecond);
        return Nor_sf.format(d);
    }
    public static String convertToGPXStr(Long millisecond) {
        Date d = new Date(millisecond);
        return GPX_sf.format(d);
    }
    public static String getCurrentTime(Date date){
        return YMD_sf.format(date);
    }

    public static Long currentTimeToMil(Date date,String utcTime){
        StringBuffer sb = new StringBuffer();
        sb.append(getCurrentTime(date));
        sb.append(utcTime);
        Date utc = null;
        try {
            utc = sf.parse(sb.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utc.getTime();
    }

    public static Long convertToMil(String string){
        Date date = null;
        try {
            date = Nor_sf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 获取毫秒数
        return date.getTime();
    }

    public static Long GPXconvertToMil(String string){
        Date date = null;
        try {
            date = GPX_sf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 获取毫秒数
        return date.getTime();
    }
}
