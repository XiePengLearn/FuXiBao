package com.shuangpin.rich.ui.fragmentwallet;

import androidx.fragment.app.Fragment;

/**
 * Created by Administrator on 2019/2/18.
 */

public class WalletFragmentFactory {
    /**
     * 根据不同的position生产对应的Fragment对象
     * @param position
     * @return
     */
    public static Fragment create(int position){
        Fragment fragment = null;

        switch (position) {

            case 0:
                fragment = new FansWithdrawalFragment();
                break;
            case 1:
                fragment = new AgentWithdrawalFragment();
                break;
            case 2:
                fragment = new AccountWithdrawalFragment();
                break;

        }
        return fragment;
    }
}
