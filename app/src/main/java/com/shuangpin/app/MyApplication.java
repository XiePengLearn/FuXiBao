package com.shuangpin.app;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.shuangpin.rich.util.GlobalParam;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 全局Application类
 * 通常做一些初始化工作，
 * Notice：记得在AndroidManife.xml清单文件中配置APP路径
 * <p>
 * Created by xiongmc on 2016/4/26.
 */
public class MyApplication extends MultiDexApplication {

    /**
     * 全局上下文，方便使用
     */
    public static Context appliction;


    private static MyApplication application;
    private static Handler mHandler;
    public static Context mApp;
    private static Map<String, String> cacheMap;
    private boolean isDownload;
    /**
     * 网络加载数据
     */

    /**
     * activity的list集合
     */
    public static List<Activity> list_activity;

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        appliction = this;
        isDownload = true;
        mHandler = new Handler();
        mApp = getApplicationContext();
        cacheMap = new HashMap<>();

        initImageLoader(getApplicationContext());

        list_activity = new ArrayList<>();
        //初始化xutils3
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        //初始化友盟组件
        initUmeng();

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this);

        //7.0之后 调用相册报错   严格模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

//        /**
//         * 初始化common库
//         * 参数1:上下文，不能为空
//         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
//         * 参数3:Push推送业务的secret
//         */
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
    }

    private void initUmeng() {


        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
//        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, GlobalParam.UMNG, "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");//应用Umeng
    }

    {

        PlatformConfig.setWeixin(GlobalParam.WEICHAT_APP_ID, GlobalParam.WEICHAT_APP_SECRET);
        PlatformConfig.setQQZone(GlobalParam.QQ_APP_ID, GlobalParam.QQ_APP_KEY);

        //新浪微博
        PlatformConfig.setSinaWeibo(GlobalParam.SINA_APP_ID, GlobalParam.SINA_APP_SECRET, "http://sns.whalecloud.com");
    }




    public static MyApplication getApplication() {
        return application;
    }

    public static Handler getHandler() {
        return mHandler;
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    /**
     * 获取app更新标识
     *
     * @return 返回更新条件 true | false
     */
    public boolean isDownload() {
        return isDownload;
    }

    /**
     * 设置更新参数
     * @param isDownload 更新标识
     */
    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }
}
