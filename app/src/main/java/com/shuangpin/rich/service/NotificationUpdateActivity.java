package com.shuangpin.rich.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.app.MyApplication;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.xutils.x;

public class NotificationUpdateActivity extends BaseActivity {

    private DownloadService.DownloadBinder binder;
    private boolean isBinded;
    // 获取到下载url后，直接复制给MapApp,里面的全局变量
    private String downloadUrl;
    private float percent = 0;
    private boolean isDestroy = true;
    private MyApplication app;
    private Context mContext = NotificationUpdateActivity.this;
    private TextView tv_up_txt;
    private ProgressBar pb_updata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_update);

        StatusBarUtil.setStatusBar(this, R.color.theme_color_title);
        x.view().inject(this);
        setTitleBar(SHOW_NOTHING);
        app = (MyApplication) getApplication();
        tv_up_txt = (TextView) findViewById(R.id.tv_up_txt);
        pb_updata = (ProgressBar) findViewById(R.id.pb_updata);
        TextView btn_cancel = (TextView) findViewById(R.id.cancel);
        //        btn_cancel.setTypeface(iconfont);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //				binder.cancel();
                //				binder.cancelNotification();//取消下载
                finish();
            }
        });
    }



    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;
            isBinded = true;// 开始下载
            binder.addCallback(callback);
            binder.start();

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(NotificationUpdateActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NotificationUpdateActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            ToastUtils.showToast(mContext,"读写手机储存权限被拒,您的应用无法正常使用,请在权限管理页面打开");
        } else {
            if (isDestroy && app.isDownload()) {
                Intent it = new Intent(NotificationUpdateActivity.this,
                        DownloadService.class);
                startService(it);
                bindService(it, conn, Context.BIND_AUTO_CREATE);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isDestroy && app.isDownload()) {
            Intent it = new Intent(NotificationUpdateActivity.this,
                    DownloadService.class);
            startService(it);
            bindService(it, conn, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroy = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBinded) {
            unbindService(conn);
        }
        if (binder != null && binder.isCanceled()) {
            Intent it = new Intent(this, DownloadService.class);
            stopService(it);
        }
    }

    private ICallbackResult callback = new ICallbackResult() {

        @Override
        public void OnBackResult(Object result) {
            if ("finish".equals(result)) {
                finish();
                return;
            }
            final int i = (Integer) result;
            mHandler.sendEmptyMessage(i);
        }

    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            changePercent(msg.what);

        };
    };

    public interface ICallbackResult {
        void OnBackResult(Object result);
    }



    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_up_txt.setText("已经下载: "+percent+"%");
                pb_updata.setProgress(percent);
            }
        });
    }

    public void resetLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
