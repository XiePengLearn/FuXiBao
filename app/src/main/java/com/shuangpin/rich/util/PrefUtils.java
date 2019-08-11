package com.shuangpin.rich.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 专门访问和设置SharePreference的工具类, 保存和配置一些设置信息
 * 
 * @author Kevin
 * 
 */
public class PrefUtils {

	private static final String SHARE_PREFS_NAME = "shuangpinkeji";

	public static void putBoolean(Context ctx, String key, boolean value) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		pref.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		return pref.getBoolean(key, defaultValue);
	}

	public static void putString(Context ctx, String key, String value) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		pref.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		return pref.getString(key, defaultValue);
	}

	public static void putInt(Context ctx, String key, int value) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		pref.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ctx, String key, int defaultValue) {
		SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
				Context.MODE_PRIVATE);

		return pref.getInt(key, defaultValue);
	}
	public static void writePhone(String phone,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("phone", phone);
		editor.commit();
	}

	public static String readPhone(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("phone", "");
	}
	//0没有绑定手机号  1已经绑定手机号
	public static void writeIsPhone(String isPhone,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("isPhone", isPhone);
		editor.commit();
	}

	public static String readIsPhone(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("isPhone", "");
	}

	public static void writeCity(String city,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("city", city);
		editor.commit();
	}

	public static String readCity(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("city", "");
	}

	public static void writeLongitude(String longitude,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("longitude", longitude);
		editor.commit();
	}

	public static String readLongitude(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("longitude", "");
	}

	public static void writeLatitude(String latitude,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("latitude", latitude);
		editor.commit();
	}

	public static String readLatitude(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("latitude", "");
	}



	public static void writePassword(String password,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("password", password);
		editor.commit();
	}

	public static String readPassword(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("password", "");

	}

	//拣到 token
	public static void writeToken(String token,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("token", token);
		editor.commit();
	}

	public static String readToken(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("token", "");

	}

	//拣到 UID
	public static void writeUid(String token,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("uid", token);
		editor.commit();
	}

	public static String readUid(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("uid", "");

	}

//微信 头像

	public static void writeHeadImg(String token,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("head_img", token);
		editor.commit();
	}

	public static String readHeadImg(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("head_img", "");

	}


//微信昵称
	public static void writeNiceName(String token,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("nick_name", token);
		editor.commit();
	}

	public static String readNiceName(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("nick_name", "");

	}

//性别
	public static void writeGender(String token,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("gender", token);
		editor.commit();
	}

	public static String readGender(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("gender", "");

	}

	//写缓存
	public static void writeCacheDate(String key,String msg,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, msg);
		editor.commit();
	}

	//读缓存
	public static String readCacheDate(String key,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		String state = mySharedPreferences.getString(key, "");
		return state;
	}
	public static void writeDownloadUrl(String url,Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("url", url);
		editor.commit();
	}

	public static String readDownloadUrl(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				SHARE_PREFS_NAME, Activity.MODE_PRIVATE);
		String url = mySharedPreferences.getString("url", "");
		return url;
	}


}
