package com.shuangpin.rich.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.EditText;

import com.shuangpin.app.MyApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 获取手机相关信息的工具类
 * Created by ruhuakeji-ios on 16/9/19.
 */
public class PhoneUtils {
    //需要权限

    /**
     * 获取手机IMEI号
     *
     * @return IMEI号, 获取失败时返回"0"
     */
    public static String getIMEI() {
        String imei = "0";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) MyApplication.mApp.getSystemService(MyApplication.mApp.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imei = TextUtils.isEmpty(imei) ? "0" : imei;
        return imei;
    }

    /**
     * 获取手机的IMSI号
     *
     * @return INSI号
     */
    public static String getIMSI() {
        String imsi = "0";
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) MyApplication.mApp.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = mTelephonyMgr.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imsi = TextUtils.isEmpty(imsi) ? "0" : imsi;

        return imsi;
    }

    /**
     * 获取当前应用版本号
     *
     * @return 当前版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = MyApplication.mApp.getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.mApp.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public static int getWindowWidth() {
        WindowManager windowManager = (WindowManager) MyApplication.mApp.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    public static int getWindowHeight() {
        WindowManager windowManager = (WindowManager) MyApplication.mApp.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();
    }

    /**
     * 判断微信是否安装
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = PhoneUtils.getWindowHeight();
        final int screenWidth = PhoneUtils.getWindowWidth();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }


    /***
     * 定时弹出软键盘
     *
     * @param etContent
     */
    public static void showSoftKeyBorad(final EditText etContent) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etContent, 0);
            }
        }, 500);
    }

    public static void showSoftKeyBorad(final EditText etContent,int time) {
        if(time == 0){
            time = 200;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etContent, 0);
            }
        }, time);
    }

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 请求文件读写权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            return true;
        }
        return false;
    }

    public static final  int CAMERA = 2;
    /**拍照权限，文件读写权限*/
    private static String[] PERMISSIONS_CAMERA = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE"};

    /**
     * 请求拍照权限
     *
     * @param activity
     * @return
     */
    public static boolean cameraPermissions(Activity activity){

        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        //因为要读写文件，所以需要文件读写权限
        boolean cameraPermissionGranted = cameraPermissionState == 0 && verifyStoragePermissions(activity);
        if(!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(activity,PERMISSIONS_CAMERA, CAMERA);
        }

        return cameraPermissionGranted;
    }

    public static final int REQUEST_ALERT_WINDOW = 24;

    /**
     * 请求开启悬浮窗权限
     *
     * @param activity
     * @return
     */
    public static boolean windowPermissions(Activity activity) {
        String[] permission_window = {Manifest.permission.SYSTEM_ALERT_WINDOW};
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permission_window, REQUEST_ALERT_WINDOW);
        } else {
            return true;
        }
        return false;
    }

    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static String getTime1() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }

    /**
     * 获取当前的网络连接状态
     *
     * @return
     */
    public static boolean getNetStatus() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected() && info.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 切换软键盘打开关闭状态
     */
    public static void toggleSoftBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
// 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideSoftBoard(Activity activity){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&activity.getCurrentFocus()!=null){
            if (activity.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static String getUserAgent(){
        String userAgent = "";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            try {
                userAgent = WebSettings.getDefaultUserAgent(MyApplication.mApp);
            }catch (Exception e){
                userAgent = System.getProperty("http.agent");
            }
        }else{
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    /**拍照标识*/
    public static final int TAKE_PICTURE = 0x000001;
    /**拍照的图片的路径*/
    public static final String PhotoPath = Environment.getExternalStorageDirectory().getPath()+"/DCIM/";

    /**
     * 拍照
     */
    public static String photo(Activity activity) {
        String imgName = System.currentTimeMillis()+".jpg";
        File dir = new File(Environment.getExternalStorageDirectory().getPath(),"DCIM");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File imgFile = new File(dir,imgName);
        if(imgFile.exists()){
            imgFile.delete();
        }
        Uri imgUri = Uri.fromFile(imgFile);
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        activity.startActivityForResult(openCameraIntent, TAKE_PICTURE);
        return imgName;
    }

    /**判断权限是否全部被允许*/
    public static boolean isAdmirePermissions(int[] permissions){
        for(int i=0;i<permissions.length;i++){
            if(permissions[i]!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void deleteService(Context mContext) {


    }

}


