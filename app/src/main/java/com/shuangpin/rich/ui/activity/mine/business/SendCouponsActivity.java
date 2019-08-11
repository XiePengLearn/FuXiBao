package com.shuangpin.rich.ui.activity.mine.business;

import android.content.Context;
import android.os.Bundle;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;

import butterknife.ButterKnife;

public class SendCouponsActivity extends BaseActivity {
    private Context mContext;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coupons);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(SendCouponsActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = SendCouponsActivity.this;
        token = PrefUtils.readToken(mContext);



    }
}
