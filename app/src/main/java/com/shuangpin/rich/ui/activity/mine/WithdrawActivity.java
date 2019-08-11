package com.shuangpin.rich.ui.activity.mine;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.ui.fragmentwallet.PagerSlidingTab;
import com.shuangpin.rich.ui.fragmentwallet.WalletPagerAdapter;
import com.shuangpin.rich.util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WithdrawActivity extends BaseActivity {
    //分销商发布商品,发布商品页面的ViewPagerFragMent
    @InjectView(R.id.slidingTab)
    PagerSlidingTab slidingTab;
    @InjectView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.inject(this);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(WithdrawActivity.this, R.color.theme_color);
        //分销商发布商品,发布商品页面的ViewPagerFragMent
        //1.填充ViewPager
        WalletPagerAdapter adapter = new WalletPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //2.绑定ViewPager和Indicator
        slidingTab.setViewPager(viewPager);
    }
}
