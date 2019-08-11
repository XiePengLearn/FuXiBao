package com.shuangpin.rich.ui.activity.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import com.shuangpin.R;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.activity.guide.GuideActivity;
import com.shuangpin.rich.ui.activity.logo.BindPhoneActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.utilsbar.XpStatusBarUtil;

public class SplashActivity extends Activity {


    protected static final String TAG = SplashActivity.class.getSimpleName();

    public static final String PREF_IS_USER_GUIDE_SHOWED = "is_user_guide_showed";//拣到标记新手引导是否已经展示过
    public static final String PREF_IS_ENTER_LOG_OR_MAIN = "is_enter_login_or_main";//拣到标记进入登录页还是主页
    private Context mContext = SplashActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        StatusBarUtil.setStatusBar(this, R.color.theme_color_title_them);
        XpStatusBarUtil.setImmersiveStatusBar(this,true);

        initViews();
    }

    // 初始化欢迎页面的动画
    private void initViews() {


        RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(1500);
        alpha.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);// 初始化动画集合
        set.addAnimation(alpha);

        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画结束的回调
            @Override
            public void onAnimationEnd(Animation animation) {
                // 判断新手引导是否展示过
                boolean showed = PrefUtils.getBoolean(
                        SplashActivity.this, PREF_IS_USER_GUIDE_SHOWED, false);

                if (showed) {
                    // 已经展示过, 进入主页面
                    //enterLogin ture 进登录界面 false 进main界面
                    boolean enterLogin = PrefUtils.getBoolean(SplashActivity.this, PREF_IS_ENTER_LOG_OR_MAIN, true);
                    if (enterLogin) {

                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra("title", "登录");
                        startActivity(intent);
                        finish();
                    } else {
                        String token = PrefUtils.readToken(SplashActivity.this);
                        if (!TextUtils.isEmpty(token)) {
                            String isPhone = PrefUtils.readIsPhone(SplashActivity.this);

                            if (!TextUtils.isEmpty(isPhone)) {
                                if ("1".equals(isPhone)) {

                                    Intent mIntent = new Intent(mContext, MainActivity.class);
                                    mIntent.putExtra("title", "首页");
                                    startActivity(mIntent);

                                    finish(); //结束当前的activity的生命周期


                                } else {

                                    //没有绑定手机号,需要
                                    Intent mIntent = new Intent(mContext, BindPhoneActivity.class);
                                    mIntent.putExtra("title", "绑定手机号");

                                    startActivity(mIntent);
                                    finish();
                                }
                            } else {
                                //没有绑定手机号,需要
                                Intent mIntent = new Intent(mContext, BindPhoneActivity.class);
                                mIntent.putExtra("title", "绑定手机号");

                                startActivity(mIntent);
                                finish();
                            }

                        } else {
//                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                            intent.putExtra("title", "登录");
//                            startActivity(intent);
//                            finish();


                            Intent mIntent = new Intent(mContext, MainActivity.class);
                            mIntent.putExtra("title", "首页");
                            startActivity(mIntent);

                            finish(); //结束当前的activity的生命周期
                        }
                    }

                } else {
                    // 没展示, 进入新手引导页面
                    startActivity(new Intent(SplashActivity.this,
                            GuideActivity.class));
                }

                finish();
            }
        });

        rlRoot.startAnimation(set);
    }

}
