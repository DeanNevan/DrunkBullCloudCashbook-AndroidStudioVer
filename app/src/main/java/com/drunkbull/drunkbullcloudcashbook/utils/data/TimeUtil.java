package com.drunkbull.drunkbullcloudcashbook.utils.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String formatStringToValidDate(String s){
        String result = "";
        String[] temp = s.split("/");
        int year = 0, month = 0, day = 0;
        if (temp.length == 3){
            year = Integer.valueOf(temp[0]);
            month = Integer.valueOf(temp[1]);
            day = Integer.valueOf(temp[2]);
            year = MathUtil.clamp(year, 1970, 10000);
            month = MathUtil.clamp(month, 1, 12);
            day = MathUtil.clamp(day, 1, 31);
            result = String.format("%d/%02d/%02d", year, month, day);
        }
        return result;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToSecondStamp(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long res = 0;
        if (!"".equals(s)) {
            try {
                res = sdf.parse(s).getTime() / 1000;
            } catch (Exception e) {
                System.out.println("传入了null值");
            }
        }else {
            long time = System.currentTimeMillis();
            res = time / 1000;
        }

        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToMilliSecondStamp(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long res = 0;
        if (!"".equals(s)) {
            try {
                res = sdf.parse(s).getTime();
            } catch (Exception e) {
                System.out.println("传入了null值");
            }
        }else {
            long time = System.currentTimeMillis();
            res = time;
        }

        return res;
    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String times = format.format(new Date(time));
//	    System.out.println("日期格式---->" + times);
        return times;
    }


}
