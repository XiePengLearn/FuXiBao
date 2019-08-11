package com.shuangpin.rich.ui.fragmentwallet;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.shuangpin.R;
import com.shuangpin.rich.util.CommonUtil;

import java.util.List;

/**
 * Created by wbing on 2017/6/14 0014.
 * 负责管理界面加载数据的逻辑
 * @author Administrator
 */

public abstract class LoadingPage extends FrameLayout {
    //定义3种状态常量
    enum PageState{
        STATE_LOADING,//加载中的状态
        STATE_ERROR,//加载失败的状态
        STATE_SUCCESS;//加载成功的状态
    }
    private PageState mState = PageState.STATE_LOADING;//表示界面当前的state，默认是加载中
    private View loadingView;
    private View errorView;
    private View successView;

    public LoadingPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLoadingPage();
    }
    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLoadingPage();
    }
    public LoadingPage(Context context) {
        super(context);
        initLoadingPage();
    }

    /**
     * 天然地往LoadingPage中添加3个view
     */
    private void initLoadingPage(){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //1.依次添加3个view对象
        if(loadingView==null){
            loadingView = View.inflate(getContext(), R.layout.page_loading, null);
        }
        addView(loadingView,params);

        if(errorView==null){
            errorView = View.inflate(getContext(), R.layout.page_error, null);
            Button btn_reload = (Button) errorView.findViewById(R.id.btn_reload);
            btn_reload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.先显示loadingView
                    mState = PageState.STATE_LOADING;
                    showPage();
                    //2.重新加载数据，然后刷新Page
                    loadDataAndRefreshPage();
                }
            });
        }
        addView(errorView,params);

        if(successView==null){
            successView = createSuccessView();//需要不固定的successView
        }
        if(successView==null){
            throw new IllegalArgumentException("The method createSuccessView() can not return null!");
        }else {
            addView(successView,params);
        }

        //2.显示默认的loadingView
        showPage();

        //3.去请求数据然后刷新page
        loadDataAndRefreshPage();
    }
    /**
     * 根据当前的mState显示对应的View
     */
    private void showPage(){
        //1.先隐藏所有的view
        loadingView.setVisibility(View.INVISIBLE);
        errorView.setVisibility(View.INVISIBLE);
        successView.setVisibility(View.INVISIBLE);
        //2.谁的状态谁显示
        switch (mState) {
            case STATE_LOADING://加载中的状态
                loadingView.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR://加载失败的状态
                errorView.setVisibility(View.VISIBLE);
                break;
            case STATE_SUCCESS://加载成功的状态
                successView.setVisibility(View.VISIBLE);
                break;
        }
    }
    /**
     * 请求数据，然后根据回来的数据去刷新Page
     */
    public void loadDataAndRefreshPage(){
        new Thread(){
            public void run() {
                //模拟请求服务器的耗时
                SystemClock.sleep(1500);

                //1.去服务器请求数据，
                List data = loadData();
                //2.判断data是否为空，如果为空则为error，否则为success状态
                mState = data==null? PageState.STATE_ERROR: PageState.STATE_SUCCESS;
                //3.根据新的state，更新page
                //在主线程更新UI
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        showPage();
                    }
                });
            }
        }.start();



    }

    /**
     * 获取successView，由于每个界面的successView都不一样，那么应该由每个界面自己实现
     * @return
     */
    public abstract View createSuccessView();

    /**
     * 去服务器请求数据，由于我不关心具体的数据类型，只需判断数据是否为空
     * @return
     */
    public abstract List loadData();
}
