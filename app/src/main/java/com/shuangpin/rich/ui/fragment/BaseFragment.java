package com.shuangpin.rich.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.shuangpin.R;
import com.shuangpin.rich.ui.activity.MainActivity;


public abstract class BaseFragment extends Fragment {
	
	protected MainActivity mMainActivity;
	private View mViewRoot;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMainActivity = (MainActivity)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//判断为空，为空就去加载布局，onCreateView在界面切换的时候会被多次调用,防止界面跳转回来的时候显示空白
		if (mViewRoot==null) {
			mViewRoot = createView(inflater, container, savedInstanceState);
//			ButterKnife.inject(this, mViewRoot);
		}else {
			mViewRoot = createView(inflater, container, savedInstanceState);
//			ButterKnife.inject(this, mViewRoot);
		}
		return mViewRoot;

	}
	
	
	//提供方法Fragment切换方法
	public void swichToChildFragment(Fragment fragment, boolean addToBackStack) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (addToBackStack) {
			//添加回退栈
			transaction.addToBackStack(fragment.getTag());
		}
		transaction.replace(R.id.fl_container, fragment);
		transaction.commit();
	}
	
	//切换到底部导航的Fragment
	public void switchNavigationFragment(int checkedId) {
		mMainActivity.switchNavigationFragment(checkedId);
	}
	
	abstract protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
	
	
	//当前的界面被切换出去的时候被调用,解决ViewGroup只有一个子View的bug
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if (mViewRoot!=null) {
			ViewParent parent = mViewRoot.getParent();
			if (parent instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parent;
				viewGroup.removeView(mViewRoot);
			}
		}
	}
	
	

}
