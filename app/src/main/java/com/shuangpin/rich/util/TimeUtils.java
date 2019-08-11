package com.shuangpin.rich.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ruhuakeji-ios on 16/12/16.
 */

public class TimeUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Date mDate;
    /**
     * 将时间格式化
     * @param times
     * @return
     */
    public static String getTime(long times){
        mDate = new Date(times);
        return format.format(mDate);
    }
}
