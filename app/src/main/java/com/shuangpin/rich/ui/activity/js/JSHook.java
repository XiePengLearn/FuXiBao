package com.shuangpin.rich.ui.activity.js;

import com.shuangpin.app.MyApplication;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;

/**
 * Created by Administrator on 2018/12/21.
 */

public class JSHook {

    public void javaMethod(String p){
        LogUtilsxp.d("xiepengJSHook" , "xiepengJSHook: "+p);
    }

    public void showAndroid(){
        String info = "来自手机内的内容！！！";
//        webView.loadUrl("javascript:show('"+info+"')");
    }

    public String getInfo(){

        String token = PrefUtils.readToken(MyApplication.mApp);
        return "获取手机内的信息！！";
    }
}
