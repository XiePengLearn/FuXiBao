package com.shuangpin.rich.ui.fragmentwallet;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shuangpin.R;
import com.shuangpin.rich.util.CommonUtil;

/**
 * Created by Administrator on 2019/2/18.
 */

public class WalletPagerAdapter  extends FragmentPagerAdapter {
    private String[] tabs;
    public WalletPagerAdapter(FragmentManager fm) {
        super(fm);
        tabs = CommonUtil.getStringArray(R.array.wallet_income);
    }
    /**
     * 返回每一页需要的fragment
     */
    @Override
    public Fragment getItem(int position) {
        return WalletFragmentFactory.create(position);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
    /**
     * 返回每一页对应的title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    } 
}
