package com.shuangpin.rich.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.shuangpin.rich.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GzwUtils {


    // 获取登陆后token的方法
    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("shared",
                Context.MODE_PRIVATE);
        // String id = preferences.getString("id", "");
        return preferences.getString(BaseActivity.KEY_TOKEN, "");
    }

    // 截取数据字符串
    public static String subString(String data) {
        return data.substring(data.indexOf("{"));
    }

    // 判断是否是手机号
    public static boolean isMobileNum(String mobiles) {
        Pattern pattern = Pattern.compile("^(1(2|3|4|5|6|8|7|9)[0-9]{9})$");// "^13/d{9}||15[8,9]/d{8}$");
        Matcher m = pattern.matcher(mobiles);
        // Log.e("手机格式验证",""+ m.matches());
        return m.matches() && mobiles.length() == 11;
    }

    // 判断是否是座机号
    public static boolean isWireTelephony(String mobiles) {
        String regx = "([0-9]{3,4}-)?[0-9]{7,8}";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /* 时间戳转换成字符串 */
    public static String getDateToString(String time) {
        // Log.i("传过来的time", time);
        try {
            String time_date = time + "000";
            long log_time = Long.parseLong(time_date);
            Date d = new Date(log_time);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return sf.format(d);
        } catch (Exception e) {
            return "";
        }
    }

    /* 时间戳转换成字符串 */
    public static String getDateToHourString(String time) {
        // Log.i("传过来的time", time);
        try {
            String time_date = time + "000";
            long log_time = Long.parseLong(time_date);
            Date d = new Date(log_time);
            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA);
            return sf.format(d);
        } catch (Exception e) {
            return "";
        }
    }


}
