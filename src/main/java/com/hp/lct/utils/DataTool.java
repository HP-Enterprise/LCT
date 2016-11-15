package com.hp.lct.utils;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jackl on 2016/11/14.
 */
@Component
public class DataTool {
    public static Date date = null;

    public static DateFormat dateFormat = null;

    public static final String onlineDeviceHash="tricheer-online-device";

    /**
     * 功能描述：以指定的格式来格式化日期
     * @param date 日期
     * @param format String 格式
     * @return String 日期字符串
     */
    public static String formatDateByFormat(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 功能描述：常用的格式化日期
     * @param date 日期
     * @return String 日期字符串 yyyy-MM-dd HH:mm:ss格式
     */
    public static String formatDateTime(Date date) {
        return formatDateByFormat(date, "yyyy-MM-dd HH:mm:ss");
    }





    /**
     * 功能描述：格式化日期
     * @param dateStr 字符型日期：YYYY-MM-DD 格式
     * @return Date 日期
     */
    public static Date parseStrToDate(String dateStr) {
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = (Date) dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

}
