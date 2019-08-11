package com.shuangpin.rich.util;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;


/**
 *  @author wangbing
 * 吐司工具类
 */
public class ToastUtils {

    public static Toast mToast;

    //自定义时长吐司
    public static void show(@NonNull final Context context, @NonNull final String msg, final int duration) {


        CommonUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context.getApplicationContext(), "", duration);
                }
                mToast.setText(msg);
                mToast.show();

            }
        });
    }


    /**
     * 立即连续弹吐司
     * @param mContext
     * @param msg
     */
    public static void showToast(final Context mContext, final String msg) {
        CommonUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
                }
                mToast.setText(msg);
                mToast.show();
            }
        });
    }
}