package com.yang.nav.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Yang on 2016/12/16.
 */

public class TimeUtils {

    /**
     * 将毫秒值转化为格式化的时间字符串
     * @param millisecond
     * @return
     */
    //"yyyy-MM-dd'T'HH:mm:ss'Z'"
    //"yyyy-MM-dd HH:mm:ss"
    public static String convertToStr(Long millisecond, String f) {
        SimpleDateFormat sf = new SimpleDateFormat(f, Locale.ENGLISH);
        Date d = new Date(millisecond);
        return sf.format(d);
    }

    public static String getUTC(String utcDay, String utcTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(utcDay);
        sb.append(utcTime);
        return sb.toString();
    }

    public static Long convertToMil(String string, String sf) {
        SimpleDateFormat f = new SimpleDateFormat(sf, Locale.ENGLISH);
        Date date = null;
        try {
            date = f.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 获取毫秒数
        return date.getTime();
    }

    public static String getLocalTimeFromUTC(String UTCTime, String sf) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(sf, Locale.CHINESE);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(UTCTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(sf, Locale.CHINESE);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
}
