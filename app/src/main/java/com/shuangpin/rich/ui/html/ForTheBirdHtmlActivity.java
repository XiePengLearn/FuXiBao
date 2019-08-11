package com.shuangpin.rich.ui.html;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.DensityUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForTheBirdHtmlActivity extends BaseActivity {


    private static final String TAG = "HtmlActivity";
    @InjectView(R.id.my_progress_bar)
    ProgressBar mProgressBar;//进度条
    @InjectView(R.id.wv_browser)
    WebView mWebView;//浏览器

    @InjectView(R.id.title)
    TextView title;//
    @InjectView(R.id.ll_for_the_bird_root)
    RelativeLayout forTheBirdRoot;//

    @InjectView(R.id.btn_close)
    Button btnClose;//
    private String url;
    private Context mContext;
    private String token;
    private int lianjie;
    private String key;
    private String mBean;
    private String mAid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        StatusBarUtil.setStatusBar(this, R.color.theme_color_title);
        setTitleBar(SHOW_NOTHING);
        ButterKnife.inject(this);
        lianjie = 0;
        key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
        mContext = ForTheBirdHtmlActivity.this;
        url = getIntent().getStringExtra("url");
        mBean = getIntent().getStringExtra("gold");
        mAid = getIntent().getStringExtra("aid");





        if (mBean.equals("1")) {
            /** 倒计时3秒，一次1秒 */
            new CountDownTimer(3 * 1000, 1000) {
                @Override
                public void onTick(final long millisUntilFinished) {
                    CommonUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });


                }

                @Override
                public void onFinish() {
                    //倒计时结束时回调该函数

                    CommonUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            atOnceChange();
                        }
                    });

                }
            }.start();
        } else {


        }


        if(!url.contains("http")){
            url = "http://" + url;
        }
        LogUtilsxp.e2(TAG, "url------------" + url);
        token = PrefUtils.readToken(mContext);


        initCompent();
        btnClose.setVisibility(View.VISIBLE);
        btnClose.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                lianjie = 0;
                LogUtilsxp.e2(TAG, "lianjie:" + lianjie);
                if (mWebView.canGoBack()) {
                    mWebView.goBack();


                } else {
                    if (mWebView != null) {
                        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                        mWebView.clearHistory();

                        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                        mWebView.destroy();
                        mWebView = null;
                    }
                    finish();
                }
                break;
            case R.id.btn_close:
                if (mWebView != null) {
                    mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                    mWebView.clearHistory();

                    ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                    mWebView.destroy();
                    mWebView = null;
                }
                finish();

                break;
        }
    }


    private void initCompent() {

        WebSettings settings = mWebView.getSettings();

        //webView  加载视频
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }

        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setPluginState(WebSettings.PluginState.ON);

        final String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + "JianDao");
        mWebView.addJavascriptInterface(new JSHook(), "hello");
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);//设定支持viewport  //设置webview推荐使用的窗口
        settings.setLoadWithOverviewMode(true);           //设置webview加载的页面的模式
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 隐藏显示缩放按钮

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setSupportZoom(false);                    //设定不支持缩放
        settings.setDisplayZoomControls(false);           //隐藏webview缩放按钮
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setInitialScale(100);
        //        settings.setDomStorageEnabled(true);//DOM储存API
        //        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);//默认缩放模式 设置以上无效 使用

        final Message msg = new Message();

        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }

        mWebView.setWebViewClient(new WebViewClient() {

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                lianjie = lianjie + 1;
                LogUtilsxp.e2(TAG, "ForTheBirdurl:" + url);

                if (mBean.equals("1")) {

                    view.loadUrl(url);

                } else {
                    if (lianjie >= 3) {

                        atOnceChange();
                        //                        tvPickUp.setVisibility(View.VISIBLE);
                        Intent intent = new Intent();
                        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    } else {
                        view.loadUrl(url);
                    }
                }


                return true;
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mContext, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //                Message msg = new Message();
                //                msg.what = 1;
                //                msg.obj = title;
                //                mHandler.sendMessage(msg);
                if (!TextUtils.isEmpty(title)) {
                    titleText.setText(title);
                }

            }

        });
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mWebView.getSettings().setSavePassword(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            lianjie = 0;
            LogUtilsxp.e2(TAG, "lianjie:" + lianjie);
            if (mWebView.canGoBack()) {
                mWebView.goBack();

                //                            if(lianjie<=0){
                //                                lianjie = 0;
                //                            }else {
                //                                lianjie = lianjie - 1;
                //                            }
            } else {
                if (mWebView != null) {
                    mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                    mWebView.clearHistory();

                    ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                    mWebView.destroy();
                    mWebView = null;
                }
                finish();
            }
        }
        return false;
    }


    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }


    public class JSHook {
        @JavascriptInterface
        public void javaMethod(String aid, String bird) {


//
//            if (bird.equals("1")) {
//                /** 倒计时3秒，一次1秒 */
//                new CountDownTimer(3 * 1000, 1000) {
//                    @Override
//                    public void onTick(final long millisUntilFinished) {
//                        CommonUtil.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        //倒计时结束时回调该函数
//
//                        CommonUtil.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                atOnceChange();
//                            }
//                        });
//
//                    }
//                }.start();
//            } else {
//
//
//            }

        }

        @JavascriptInterface
        public void showAndroid() {
            //            final String info = "来自手机内的内容！！！";

            CommonUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {


                    mWebView.loadUrl("javascript:getAdMsg('" + token + "')");
                }
            });

        }

        public String getInfo() {

            return token;
        }
    }

    private void atOnceChange() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.find_bird_dialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(false);
        TextView findBirdSuccess = (TextView) v.findViewById(R.id.iv_find_bird_success1);//成功寻鸟


        //成功寻鸟
        findBirdSuccess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;



                String uid = PrefUtils.readUid(mContext);
                long time = System.currentTimeMillis() / 1000;


                String encryptionString = uid + "_" + mAid + "_" + mBean + "_" + time;
                String encStr = CommonUtil.md5(CommonUtil.md5(encryptionString) + key);

                String token = PrefUtils.readToken(mContext);
                RequestBody requestBody = new FormBody.Builder()
                        .add("aid", mAid + "")
                        .add("gold", mBean + "")
                        .add("sign", encStr + "")
                        .add("time", time + "")
                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_ADV_RECORD)
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();

                LogUtilsxp.e2(TAG, "token:" + token);

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        LogUtilsxp.e2(TAG, "URL_PRODUCT_VIEW_message:" + responseData);
                        try {
                            String data = responseData;
                            JSONObject object = new JSONObject(data);
                            int code = object.optInt("code");
                            String message = object.optString("msg");

                            //code=403  重新登录
                            if (code == 0) {

                                lianjie = 0;
                                CommonUtil.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        birdFlying();
                                    }
                                });


                            } else if (code == 403) {
                                ToastUtils.showToast(mContext, message);
                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mContext);
                                mContext.startActivity(mIntent);  //重新启动LoginActivity
                                finish();

                            } else {
                                ToastUtils.showToast(mContext, message);


                            }
                        } catch (Exception e) {


                        }
                    }
                });


                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void birdFlying() {
        WindowManager wm1 = getWindowManager();
        int width1 = wm1.getDefaultDisplay().getWidth();
        int widthHalf = wm1.getDefaultDisplay().getWidth() / 2;
        int height1 = wm1.getDefaultDisplay().getHeight() / 2;


        Random generate = new Random();
        int nextIntY = generate.nextInt(height1 / 2) + height1;
        int nextIntX = generate.nextInt(width1 - 100);
        final ImageView point = new ImageView(mContext);
        point.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(DensityUtils.dp2px(mContext, 34),
                DensityUtils.dp2px(mContext, 34));
        point.setLayoutParams(paramsImg);
        if (nextIntX - widthHalf > 0) {
            //获取绘制出来鸟的坐标,和屏幕的一半做比较,大于零鸟朝向左边
            point.setBackgroundResource(R.drawable.a_bean_flying);
        } else {
            //获取绘制出来鸟的坐标,和屏幕的一半做比较,小于零鸟朝向右边
            point.setBackgroundResource(R.drawable.a_bean_flying);
        }

        final RelativeLayout myRelativeLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(mContext, 36), DensityUtils.dp2px(mContext, 36));
        params.setMargins(nextIntX, nextIntY + DensityUtils.dp2px(mContext, 5), 0, 0);
        myRelativeLayout.setLayoutParams(params);
        myRelativeLayout.addView(point);
        final AnimationDrawable mAnimation = (AnimationDrawable) point.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        point.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();
            }
        });

        AnimationSet animationSet = new AnimationSet(true);//共用动画补间
        animationSet.setDuration(2000);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.9f);
        alphaAnimation.setDuration(2000);
        TranslateAnimation translateAnimation = null;
        if (nextIntX - widthHalf > 0) {
            translateAnimation = new TranslateAnimation(0, nextIntX - widthHalf * 2, 0, -nextIntY);
        } else {
            translateAnimation = new TranslateAnimation(0, widthHalf - nextIntX, 0, -nextIntY);
        }

        translateAnimation.setDuration(2000);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画结束的回调
            @Override
            public void onAnimationEnd(Animation animation) {
                myRelativeLayout.setVisibility(View.GONE);


            }
        });
        myRelativeLayout.startAnimation(animationSet);
        forTheBirdRoot.addView(myRelativeLayout);
    }

}
