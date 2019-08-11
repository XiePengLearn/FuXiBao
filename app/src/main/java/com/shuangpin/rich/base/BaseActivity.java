package com.shuangpin.rich.base;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangpin.R;

/**
 * Created by Xpeng on 2017/11/28 0028.
 */

public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected final int SHOW_LEFT = 100;
    protected final int SHOW_NOTHING = 101;
    protected final int SHOW_RIGHT = 102;
    protected LinearLayout leftBtn;
    protected TextView titleText;
    protected final String PARAMS_TITLE = "title";
    protected String title_str;

    public final static String KEY_TOKEN = "token";
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x0000010;
    public static final int NOTIFY_ID = 0x0000011;//状态栏通知标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            title_str = getIntent().getSerializableExtra(PARAMS_TITLE)
                    .toString();
        } catch (Exception e) {
            Log.i("error", "BaseActivity没传值过来");
            e.printStackTrace();
        }
        if (title_str == null) {
            title_str = getApplicationName();
        }

    }

    protected void setTitleBar(int showRight) {

        leftBtn = (LinearLayout) findViewById(R.id.left_btn);


        titleText = (TextView) findViewById(R.id.title);


        switch (showRight) {
            case SHOW_RIGHT:
                leftBtn.setOnClickListener(this);
                break;

            case SHOW_LEFT:
                leftBtn.setOnClickListener(this);
                break;

            case SHOW_NOTHING:
                leftBtn.setOnClickListener(this);
                break;

            default:
                break;
        }

        titleText.setText(title_str);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;


        }
    }

    //  获取应用名
    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

}
