package com.shuangpin.rich.ui.html;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.tencent.mm.sdk.openapi.IWXAPI;

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

import static com.shuangpin.R.id.left_btn;
import static com.shuangpin.R.id.open_method;
import static com.shuangpin.R.id.tv_count_down;

public class ShopAdHtmlActivity extends BaseActivity {

    private static final String TAG = "HtmlActivity";
    @InjectView(R.id.my_progress_bar)
    ProgressBar mProgressBar;//进度条
    @InjectView(R.id.wv_browser)
    WebView mWebView;//浏览器

    @InjectView(R.id.title)
    TextView title;//
    @InjectView(R.id.ll_for_the_bird_root)
    RelativeLayout forTheBirdRoot;//

    @InjectView(R.id.tv_count_down)
    TextView countDown;//
    @InjectView(R.id.open_method)
    Button open_method1;//
    private Context mContext = ShopAdHtmlActivity.this;
    private String url;
    private String key;

    private int time = 5;
    private String id;
    private String bird;
    private String token;
    private String type;
    private String sys;
    //    private Handler handler = new Handler() {
    //        @Override
    //        public void handleMessage(Message msg) {
    //            super.handleMessage(msg);
    //            time--;
    //            countDown.setText(time + "");
    //            if (time == 0) {
    //                countDown.setVisibility(View.VISIBLE);
    //                leftBtn.setVisibility(View.GONE);
    //                handler.removeMessages(0);
    //            } else {
    //                countDown.setVisibility(View.GONE);
    //                leftBtn.setVisibility(View.VISIBLE);
    //
    //            }
    //            handler.sendEmptyMessageDelayed(0, 1000);
    //        }
    //    };
    private IWXAPI api;
    private Toast toast;
    private String tagType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        StatusBarUtil.setStatusBar(this, R.color.theme_color_title);
        setTitleBar(SHOW_NOTHING);
        ButterKnife.inject(this);
        key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
        token = PrefUtils.readToken(mContext);
        Intent mIntent = getIntent();
        url = mIntent.getStringExtra("url");
        id = mIntent.getStringExtra("id");
        bird = mIntent.getStringExtra("bird");
        type = mIntent.getStringExtra("type");
        sys = mIntent.getStringExtra("sys");

        tagType = mIntent.getStringExtra("tagType");
        open_method1.setOnClickListener(this);


        LogUtilsxp.e2(TAG, "url------------" + url);

        countDown.setOnClickListener(this);
        //        handler.sendEmptyMessageDelayed(0, 1000);
        /** 倒计时3秒，一次1秒 */
        new CountDownTimer(6 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time--;
                //倒计时的过程中回调该函数
                countDown.setVisibility(View.VISIBLE);

                countDown.setText(millisUntilFinished / 1000 + "");


                leftBtn.setVisibility(View.GONE);

            }

            @Override
            public void onFinish() {
                //倒计时结束时回调该函数
                countDown.setVisibility(View.GONE);
                leftBtn.setVisibility(View.VISIBLE);

            }
        }.start();

        initCompent();

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
                //
                //请求鸟的金额
                String uid = PrefUtils.readUid(mContext);
                //获取当前的秒值
                long time = System.currentTimeMillis() / 1000;

                String idSign = id;
                String birdSign = bird;

                String encryptionString = uid + "_" + time + "_" + idSign + "_" + birdSign;
                String encStr = CommonUtil.md5(CommonUtil.md5(encryptionString) + key);

                LogUtilsxp.e(TAG, "URL_BIRD_CLICK_message:  测试");
                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                String token = PrefUtils.readToken(mContext);

                LogUtilsxp.e2(TAG, "id:" + idSign + "birdSign:" + bird + "sign:" + encStr + "time:" + time);
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", idSign)
                        .add("gold", birdSign)
                        .add("sign", encStr)
                        .add("type", type)
                        .add("sys", sys)
                        .add("time", time + "")
                        .build();

                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_CLICK)
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        LogUtilsxp.e2(TAG, "URL_BIRD_CLICK_message:" + responseData);
                        try {
                            String data = responseData;
                            JSONObject object = new JSONObject(data);
                            int code = object.optInt("code");
                            String message = object.optString("msg");

                            //code=403  重新登录
                            if (code == 0) {
                                ToastUtils.showToast(mContext, "拣元宝成功");


                            } else if (code == 403) {
                                ToastUtils.showToast(mContext, message);
                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mContext);
                                startActivity(mIntent);  //重新启动LoginActivity
                                finish();

                            } else {
                                ToastUtils.showToast(mContext, message);


                            }
                        } catch (Exception e) {


                        }
                    }
                });


            }
        });
        myRelativeLayout.startAnimation(animationSet);

        forTheBirdRoot.addView(myRelativeLayout);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case left_btn:
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

            case tv_count_down:
                if (time > 0) {
                    ToastUtils.showToast(mContext, "请观看片刻");
                } else {

                }

                break;
            case open_method:

                //                CustomTrust customTrust = new CustomTrust(mContext);
                //                OkHttpClient okHttpClient = customTrust.client;
                //
                //                String token = PrefUtils.readToken(mContext);
                //                RequestBody requestBody = new FormBody.Builder()
                ////                        .add("longitude", longitude + "")
                ////                        .add("latitude", latitude + "")
                //                        .build();
                //                Request request = new Request.Builder()
                //                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_PRODUCT_VIEW)
                //                        .addHeader("Authorization", token)
                ////                        .post(requestBody)
                //                        .build();
                //
                //                LogUtilsxp.e2(TAG, "token:" + token);
                //
                //                okHttpClient.newCall(request).enqueue(new Callback() {
                //                    @Override
                //                    public void onFailure(Call call, IOException e) {
                //                        LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                //                    }
                //
                //                    @Override
                //                    public void onResponse(Call call, Response response) throws IOException {
                //                        String responseData = response.body().string();
                //
                //                        LogUtilsxp.e2(TAG, "URL_PRODUCT_VIEW_message:" + responseData);
                //                        try {
                //                            String data = responseData;
                //                            JSONObject object = new JSONObject(data);
                //                            int code = object.optInt("code");
                //                            String message = object.optString("msg");
                //
                //                            //code=403  重新登录
                //                            if (code == 0) {
                //
                ////                                JSONObject dataObject = object.optJSONObject("data");
                ////
                ////                                JSONArray jsonArray = dataObject.optJSONArray("list");
                ////
                ////                                if (null == jsonArray) {
                ////
                ////                                } else {
                ////
                ////                                }
                //
                //                            } else if (code == 403) {
                //                                ToastUtils.showToast(mContext, message);
                //                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                //                                mIntent.putExtra("title", "登录");
                //                                PrefUtils.writeToken("", mContext);
                //                                mContext.startActivity(mIntent);  //重新启动LoginActivity
                //                                finish();
                //
                //                            } else {
                //                                ToastUtils.showToast(mContext, message);
                //
                //
                //                            }
                //                        } catch (Exception e) {
                //
                //
                //                        }
                //                    }
                //                });

                mWebView.loadUrl("javascript:getUserRedMsg('" + id + "','" + token + "','" + "" + "')");

                break;

        }
    }


    private void initCompent() {


        WebSettings settings = mWebView.getSettings();


        settings.setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new JSHook(), "hello");
        settings.setUseWideViewPort(true);//设定支持viewport  //设置webview推荐使用的窗口
        settings.setLoadWithOverviewMode(true);           //设置webview加载的页面的模式
        settings.setBuiltInZoomControls(false);           // 隐藏显示缩放按钮
        settings.setSupportZoom(false);                    //设定不支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setDisplayZoomControls(false);           //隐藏webview缩放按钮
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setInitialScale(100);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//使用默认缓存
        settings.setDomStorageEnabled(true);//DOM储存API
        //        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);//默认缩放模式 设置以上无效 使用

        final Message msg = new Message();
        mWebView.loadUrl(url);
        // 注意调用的JS方法名要对应上
        // 调用javascript的callJS()方法l
        mWebView.setWebViewClient(new WebViewClient() {

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {


                LogUtilsxp.e2(TAG, "url:" + url);

                Intent mIntent = new Intent(ShopAdHtmlActivity.this, HtmlStorePayActivity.class);
                mIntent.putExtra("title", " ");
                mIntent.putExtra("url", url);

                startActivity(mIntent);


                return true;
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!TextUtils.isEmpty(tagType) && tagType.equals("tagType")) {

                    tagType = "tagType_no_error";

                    if (type.equals("1")) {
                        mWebView.loadUrl("javascript:getUserMSG('" + token + "','" + id + "')");
                    } else {
                        mWebView.loadUrl("javascript:getShopMsg('" + token + "','" + id + "','" + "" + "')");
                    }
                }


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mContext, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
            }
        });
        //        browser.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    //                    String uid = PrefUtils.readUid(mContext);
                    //                    mWebView.loadUrl("javascript:getShopMsg("+token+","+uid+")");
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
            if (mWebView.canGoBack()) {
                mWebView.goBack();

            } else {
                if (time > 0) {
                    ToastUtils.showToast(mContext, "请观看片刻");
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
        public void javaMethod(String p) {


            LogUtilsxp.e2(TAG, "JSHook.JavaMethod() called! + " + p);


        }

        @JavascriptInterface
        public void showAndroid() {
            final String info = "来自手机内的内容！！！";

            CommonUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {


                    mWebView.loadUrl("javascript:show('" + info + "')");
                }
            });

        }

        public String getInfo() {


            return token + "," + id;
        }
    }

}
