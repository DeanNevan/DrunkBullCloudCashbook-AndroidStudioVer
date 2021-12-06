package com.drunkbull.drunkbullcloudcashbook.utils.data;

import com.drunkbull.drunkbullcloudcashbook.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    enum DayTimePeriod{
        MIDNIGHT,
        MORNING,
        NOON,
        AFTERNOON,
        NIGHT
    }

    public static DayTimePeriod getDayTimePeriod(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 6){
            return DayTimePeriod.MIDNIGHT;
        }
        if (hour < 12){
            return DayTimePeriod.MORNING;
        }
        if (hour < 14){
            return DayTimePeriod.NOON;
        }
        if (hour < 18){
            return DayTimePeriod.AFTERNOON;
        }
        return DayTimePeriod.NIGHT;

    }

    public static int getDayTimePeriodStringID(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 6){
            return R.string.text_midnight;
        }
        if (hour < 12){
            return R.string.text_morning;
        }
        if (hour < 14){
            return R.string.text_noon;
        }
        if (hour < 18){
            return R.string.text_afternoon;
        }
        return R.string.text_night;
    }



    /**
     * 获取时间戳
     * 输出结果:1438692801766
     */
    public static long getTimeStamp() {
        Date date = new Date();
        long times = date.getTime();
        return times;
    }
    /**
     * 获取精确到秒的时间戳
     * @return
     */
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    /**
     * 获取精确到毫秒的时间戳
     * @return
     */
    public static long getMillisecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Long.valueOf(timestamp.substring(0,length));
        } else {
            return 0;
        }
    }

    public static Date getCurrentDate(){
        return new Date();
    }

    /**
     * 获取格式化的时间
     * 输出格式：2015-08-04 20:55:35
     */
    public static void getFormatDate(){
        Date date = new Date();
        long times = date.getTime();//时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        System.out.println(dateString);
    }

    /**
     * 将时间戳转化为标准时间
     * 输出：Tue Oct 07 12:04:36 CST 2014
     */
    public static void timestampToDate(){
        long times = 1412654676572L;
        Date date = new Date(times);
        System.out.println(date);
    }
}
