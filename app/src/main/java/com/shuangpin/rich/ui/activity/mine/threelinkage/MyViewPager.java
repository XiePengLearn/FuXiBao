package com.shuangpin.rich.ui.activity.mine.threelinkage;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by wbing on 2017/6/29 0029.
 */

public class MyViewPager extends ViewPager {

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }
    //判断menu在x,y的位置
    public void scrollTo(int x,int y){
        if(getAdapter()==null||x>getWidth()*(getAdapter().getCount()-2)){
            return;
        }
        super.scrollTo(x,y);
    }
}
