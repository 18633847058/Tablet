package com.yang.nav.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yang on 2016/12/16.
 */

public class TimeUtils {

    public static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    /**
     * 将毫秒值转化为格式化的时间字符串
     * @param millisecond
     * @return
     */
    public static String convertToStr(Long millisecond) {
        Date d = new Date(millisecond);
        return sf.format(d);
    }
    public static Long convertToMil(String string){
        Date date = null;
        try {
            date = sf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 获取毫秒数
        return date.getTime();
    }
}
