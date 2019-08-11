package com.shuangpin.rich.ui.fragmentwallet;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.shuangpin.app.MyApplication;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/2/18.
 */

public abstract class WalletBaseFragment extends Fragment {
    Context baseContext = MyApplication.mApp;
    protected LoadingPage loadingPage;//注意：修饰符不能是private
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ButterKnife.inject(getActivity());
        Log.e(this.getClass().getName(), "onCreateView: loadingPage ==" + loadingPage, null);
        if(loadingPage==null){
            loadingPage = new LoadingPage(getActivity()) {
                @Override
                public List loadData() {
                    return requestData();
                }
                @Override
                public View createSuccessView() {
                    return getSuccessView();
                }
            };
        }else {
            //需要拿loadingPage的父View（NoSaveStateFramelayout）去移除它自己
            if(loadingPage!=null){
                ViewParent parent = loadingPage.getParent();
                if(parent!=null && parent instanceof ViewGroup){
                    ViewGroup group = (ViewGroup) parent;
                    group.removeView(loadingPage);//移除子View
                }
            }
            //但是呢，在Android5.0之后的FragmentManager已经不会在Fragment的view外边包裹一层，这意味着不用移除也不会报错;
        }
        return loadingPage;
    }
    /**
     * 获取每个子类的successView
     * @return
     */
    protected abstract View getSuccessView();
    /**
     * 获取每个子类的数据
     * @return
     */
    protected abstract List requestData();
}
