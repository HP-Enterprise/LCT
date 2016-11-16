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
    public static final String outCmdPreStr="output:tricheer:";

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

    /**
     * 生成一个序列ID，标识会话
     * @return
     */
    public  static String getSequenceId(){
        StringBuilder sb=new StringBuilder();
        sb.append(new Date().getTime());
        sb.append(getNoBetween(10000,99999));
        return sb.toString();

    }
    public static int getNoBetween(int a,int b){
        return (int)(a+Math.random()*(b-a+1));
    }


    /**
     * 获取控制指令描述信息
     * @param command
     * @param operate
     * @return
     */
    public static String getCommandDesc(String command,String operate){
        //106 NAVIGATE
        StringBuilder sb=new StringBuilder();
        if(command!=null){
            if(command.equals("101")){
                sb.append("获取设备信息");
            }else if(command.equals("103")){
                sb.append("获取状态信息");
            }else if(command.equals("106")){
                sb.append("控制");
                if(operate!=null){
                    if(operate.equalsIgnoreCase("NAVIGATE")){
                        sb.append("导航");
                    }else if(operate.equalsIgnoreCase("VIDEO")){
                        sb.append("录像");
                    }else if(operate.equalsIgnoreCase("PHOTO")){
                        sb.append("拍照");
                    }
                }
            }
        }

        return sb.toString();
    }
}
