package com.shuangpin.rich.ui.activity.mine.threelinkage;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangpin.R;


/**
 * Created by wbing on 2017/6/29 0029.
 */

public class SecondMenuDialogArea extends Dialog {
    public Context mContext;
    public LinearLayout containerViewGroup;
    public View mContentView;
    public TextView titleView;
    Window window = null;

    //构造器
    public SecondMenuDialogArea(Context context) {
        super(context, R.style.dialog_change_card);//样式
        mContext = context;
        containerViewGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.second_menu_dialog, null);
        titleView = (TextView) containerViewGroup.findViewById(R.id.dictdialog_title_tv);
    }
    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    /**
     * 设置窗口显示
     */
    public void windowDeploy() {
        window = getWindow(); // 得到对话框
//        window.setWindowAnimations(R.style.RegDialogAnimation); // 设置窗口弹出动画效果
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.x = 0; // x小于0左移，大于0右移
        windowAttributes.y = 0; // y小于0上移，大于0下移
        windowAttributes.height = 6 * mContext.getResources().getDisplayMetrics().heightPixels /11;
        windowAttributes.width = LinearLayout.LayoutParams.FILL_PARENT;
        windowAttributes.alpha = 1f; //设置透明度
        windowAttributes.gravity = Gravity.CENTER; // 设置重力，对齐方式
        window.setAttributes(windowAttributes);



//        window = getWindow();
//        window.setWindowAnimations(R.style.RegDialogAnimation); // 设置窗口弹出动画效果
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        //这里必须设置成MATCH_PARENT，否则
//        layoutParams.height = 2 * mContext.getResources().getDisplayMetrics().heightPixels / 3;
//        layoutParams.x = 0;
//        layoutParams.y = 0;
//
//        window.setAttributes(layoutParams);


    }
    //显示到layout里面
    @Override
    public void show() {
        if (mContentView != null) {
            containerViewGroup.removeView(mContentView);

            containerViewGroup.addView(mContentView);
        }
        setContentView(containerViewGroup);
        setCanceledOnTouchOutside(true);
        windowDeploy();
        super.show();
    }
    //选中的title设置为title
    @Override
    public void setTitle(CharSequence title) {
        if (titleView != null)
            titleView.setText(title);
    }
}
