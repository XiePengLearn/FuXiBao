package com.shuangpin.rich.wxapi;

import android.app.Activity;

/**
 * Created by Administrator on 2018/12/18.
 */

public class WXPayHelper {
    private Activity context;
    public static WXPayCallBack callBack;

    public interface WXPayCallBack{
        void success();
        void cancel();
        void falure(String msg);
    }

    private WXPayHelper(Activity context,WXPayCallBack callBack){
        this.context = context;
        this.callBack = callBack;
    }
    public static synchronized WXPayHelper getInstance(Activity context,WXPayCallBack callBack){
        return new WXPayHelper(context,callBack);
    }
}
