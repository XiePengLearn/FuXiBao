package com.shuangpin.rich.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

public class DeviceUtils {

    /***
     * 获取当前设备信息
     * @param activity 当前Activity
     * @return 当前屏幕的宽/高
     */
    public static int getWidth(Activity activity)
    {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getHeight(Activity activity)
    {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 通过包名获取应用程序的名称。
     * @param context
     *            Context对象。
     * @param packageName
     *            包名。
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context,
                                                     String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /** * 检测Android设备是否支持摄像 */
    public static boolean checkCameraDevice(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
