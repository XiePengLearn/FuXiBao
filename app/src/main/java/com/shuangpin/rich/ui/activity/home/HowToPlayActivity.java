package com.shuangpin.rich.ui.activity.home;

import android.os.Bundle;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.util.StatusBarUtil;


public class HowToPlayActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(HowToPlayActivity.this, R.color.tv_ff7b2e);
    }

}
