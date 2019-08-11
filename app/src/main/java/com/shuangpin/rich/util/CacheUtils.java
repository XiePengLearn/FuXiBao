package com.shuangpin.rich.util;

import android.content.Context;

/**
 * 处理网络缓存
 * 
 * @author Kevin
 * 
 */
public class CacheUtils {

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            缓存表示: 可以使用url来标示一段json数据
	 * @param value
	 *            缓存内容是json数据
	 */
	public static void setCache(Context ctx, String key, String value) {
		PrefUtils.putString(ctx, key, value);
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */
	public static String getCache(Context ctx, String key) {
		return PrefUtils.getString(ctx, key, null);
	}

}
