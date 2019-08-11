package com.shuangpin.rich.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	/* 时间戳转换成字符串 */
    public static String formatDate(String dataFormat, long timeStamp) {
		if (timeStamp == 0) {
			return "";
		}
		timeStamp = timeStamp * 1000;
		String result;
		SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.CHINA);
		result = format.format(new Date(timeStamp));
		return result;
	}

	public static String formatToday(String string){
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return format.format(new Date(string));
	}
	public static String formatMonth(String string){
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
		return format.format(new Date(string));
	}
}
