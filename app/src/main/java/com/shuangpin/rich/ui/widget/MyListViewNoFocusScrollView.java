package com.shuangpin.rich.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by wbing on 2017/6/9 0009.
 */

public class MyListViewNoFocusScrollView extends ScrollView {
    public MyListViewNoFocusScrollView(Context context) {
        super(context);
    }

    public MyListViewNoFocusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListViewNoFocusScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /* 原因：
       ScrollView中的scrollToChild这个方法会根据computeScrollDeltaToGetChildRectOnScreen的返回值来计算滚动的位置，
       重载computeScrollDeltaToGetChildRectOnScreen让其返回0 会导致ScrollView内布局产生变化时, 不能正确滚动到focus
       child位置,当然,这也就是我们想要的效果,布局变化时ScrollView不需要自己去滚动.*/
    @Override

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
