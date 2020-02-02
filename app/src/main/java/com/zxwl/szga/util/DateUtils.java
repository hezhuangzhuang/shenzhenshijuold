package com.zxwl.szga.util;

import android.util.Log;

import com.zxwl.szga.Constant.Constant;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    /**
     * 年-月-日 时:分:秒 显示格式
     */ // 备注:如果使用大写HH标识使用24小时显示格式,如果使用小写hh就表示使用12小时制格式。
    public static String DATE_TO_STRING_DETAIAL_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 年-月-日 显示格式
     */
    public static String DATE_TO_STRING_SHORT_PATTERN = "yyyy-MM-dd";
    private static SimpleDateFormat simpleDateFormat;

    /**
     * Date类型转为指定格式的String类型 * * @param source * @param pattern * @return
     */
    public static String DateToString(Date source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(source);
    }

    /**
     * * unix时间戳转为指定格式的String类型 * * * System.currentTimeMillis()获得的是是从1970年1月1日开始所经过的毫秒数 * unix时间戳:是从1970年1月1日（UTC/GMT的午夜）开始所经过的秒数,不考虑闰秒 * * @param source * @param pattern * @return
     */
    public static String timeStampToString(long source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(source * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 将日期转换为时间戳(unix时间戳,单位秒) * * @param date * @return
     */
    public static long dateToTimeStamp(Date date) {
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.getTime() / 1000;
    }

    /**
     * * 字符串转换为对应日期(可能会报错异常) * * @param source * @param pattern * @return
     */
    public static Date stringToDate(String source, String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
        } catch (ParseException e) {
            Log.d(Constant.tag, "字符串转换日期异常");
        }
        return date;
    }

    /**
     * 获得当前时间对应的指定格式 * * @param pattern * @return
     */
    public static String currentFormatDate(String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获得当前unix时间戳(单位秒) * * @return 当前unix时间戳
     */
    public static long currentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取持续时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static String timeDur(Date begin, Date end) {
        if (begin.before(end)) {
            if (begin.getYear() == end.getYear() && begin.getMonth() == end.getMonth()
                    && begin.getDay() == end.getDay()) {//同一天
                return DateToString(begin, DATE_TO_STRING_SHORT_PATTERN) + " "
                        + (begin.getHours() >= 10 ? begin.getHours() : ("0") + begin.getHours()) + ":" + (begin.getMinutes() >= 10 ? begin.getMinutes() : ("0") + begin.getMinutes())
                        + "-" + (end.getHours() >= 10 ? end.getHours() : ("0") + end.getHours()) + ":" + (end.getMinutes() >= 10 ? end.getMinutes() : ("0") + end.getMinutes());
            }
        }
        return "Not Single Day";
    }
}