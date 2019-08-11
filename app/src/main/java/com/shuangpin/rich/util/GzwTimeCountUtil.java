package com.shuangpin.rich.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;


public class GzwTimeCountUtil extends CountDownTimer {
	private Activity mActivity;
	private Button btn;// 按钮

	// 在这个构造方法里需要传入三个参数，一个是Activity，一个是总的时间millisInFuture，一个是countDownInterval，Button是给相应的按钮设置倒计时
	public GzwTimeCountUtil(Activity mActivity, long millisInFuture,
                            long countDownInterval, Button btn) {
		super(millisInFuture, countDownInterval);
		this.mActivity = mActivity;
		this.btn = btn;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTick(long millisUntilFinished) {
		btn.setClickable(false);// 设置不能点击
//		btn.setBackground(R.color.gainsboro);
		btn.setText("还剩"+ (millisUntilFinished/1000) + "秒");// 设置倒计时时间

		// 设置按钮为灰色，这时是不能点击的
//		  设置为灰色
		Spannable span = new SpannableString(btn.getText().toString()); // 获取按钮的文字
		span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);// 设置倒计时时间/字体为黑色
		btn.setText(span.toString());

	}

	@SuppressLint("NewApi")
	@Override
	public void onFinish() {
		btn.setText("重获验证码");
		btn.setClickable(true);// 重新获得点击
	}

}
