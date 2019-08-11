package com.shuangpin.rich.ui.activity.mine.threelinkage;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by wbing on 2017/6/29 0029.
 */

public class ViewHolder {
    //无参构造
    public ViewHolder() {
    }

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();//j节省内存，提高性能，使用SparseArray
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView=view.findViewById(id);
            viewHolder.put(id,childView);
        }
        return (T) childView;
    }
}
