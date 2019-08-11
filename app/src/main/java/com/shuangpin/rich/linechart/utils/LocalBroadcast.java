package com.shuangpin.rich.linechart.utils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shuangpin.app.MyApplication;


/**
 * Created by gaosheng on 2016/12/21.
 */

public class LocalBroadcast {
    private static LocalBroadcastManager localBroadcastManager ;


    public static LocalBroadcastManager getLocalBroastManager(){
        if(localBroadcastManager == null){
            synchronized (LocalBroadcast.class){
                if(localBroadcastManager == null){
                    localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getApplication().getApplicationContext());
                }
            }
        }

        return localBroadcastManager;
    }
}
