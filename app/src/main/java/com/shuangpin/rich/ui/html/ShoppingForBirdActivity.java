package com.shuangpin.rich.ui.html;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

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

public class ShoppingForBirdActivity extends BaseActivity {

    private static final String TAG = "HtmlActivity";
    @InjectView(R.id.my_progress_bar)
    ProgressBar mProgressBar;//进度条
    @InjectView(R.id.wv_browser)
    WebView mWebView;//浏览器

    @InjectView(R.id.title)
    TextView title;//
    @InjectView(R.id.open_method)
    Button open_method1;//

    @InjectView(R.id.ll_for_the_bird_root)
    RelativeLayout forTheBirdRoot;//


    @InjectView(R.id.btn_share)
    ImageView btn_share;//

    private String url;
    private Context mContext;
    private String token;
    private Intent mIntent;
    private String shopName;
    private String longitude;
    private String latitude;
    private String isOut;
    private String id;
    private String key;
    private String tagType;
    private IWXAPI api;
    private Toast toast;


    private String mShareUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        StatusBarUtil.setStatusBar(this, R.color.theme_color_title);
        setTitleBar(SHOW_NOTHING);
        ButterKnife.inject(this);
        mContext = ShoppingForBirdActivity.this;
        token = PrefUtils.readToken(mContext);

        key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
        mIntent = getIntent();

        url = mIntent.getStringExtra("url");
        shopName = mIntent.getStringExtra("shopName");
        longitude = mIntent.getStringExtra("longitude");
        latitude = mIntent.getStringExtra("latitude");
        isOut = mIntent.getStringExtra("isOut");
        id = mIntent.getStringExtra("id");
        tagType = mIntent.getStringExtra("tagType");

        open_method1.setOnClickListener(this);
        mShareUrl = HttpsApi.HTML_SHARE_SHOP + "sendId=" + "" + "&shopId=" + id + "&token=" + token;

        initCompent();


        addBirdToHomeBirdList();

        btn_share.setVisibility(View.VISIBLE);
        btn_share.setOnClickListener(this);
    }

    private void addBirdToHomeBirdList() {
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

      /*  $time = $data['time'];
        $sign = $data['sign'];
        $str = $uid.'_'.$data['shopId'].'_'.$time.'_'.$key.'_'.$data['city'];
        $str = MD5(MD5($str));*/


        //请求鸟的金额
        String uid = PrefUtils.readUid(mContext);
        String city = PrefUtils.readCity(mContext);
        //获取当前的秒值
        long time = System.currentTimeMillis() / 1000;
        String encryptionString = uid + "_" + id + "_" + time + "_" + key + "_" + city;
        String encStr1 = CommonUtil.md5(encryptionString);
        String encStr = CommonUtil.md5(encStr1);


        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", id + "")//店id
                .add("sign", encStr + "")//签名
                .add("city", city + "")//城市编号
                .add("time", time + "")//当前时间
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND_BIRD)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();

        LogUtilsxp.e2(TAG, "token:" + token);
        LogUtilsxp.e2(TAG, "shopId:" + id);
        LogUtilsxp.e2(TAG, "sign:" + encStr);
        LogUtilsxp.e2(TAG, "token:" + token);
        LogUtilsxp.e2(TAG, "city:" + city);
        LogUtilsxp.e2(TAG, "time:" + time + "");
        LogUtilsxp.e2(TAG, "url:" + HttpsApi.SERVER_URL + HttpsApi.URL_SEND_BIRD);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtilsxp.e2(TAG, "URL_SEND_BIRD_message:" + responseData);
                        try {
                            String data = responseData;
                            JSONObject object = new JSONObject(data);
                            int code = object.optInt("code");
                            String message = object.optString("msg");

                            //code=403  重新登录
                            if (code == 0) {
                                JSONObject dataObj = object.optJSONObject("data");
                                String isSend = dataObj.optString("isSend");
                                LogUtilsxp.e2(TAG, "isSend:" + isSend);
                                //                        "isSend":0 //不送  1 送

                                if (!TextUtils.isEmpty(isSend)) {
                                    if (isSend.equals("1")) {
                                        ToastUtils.showToast(mContext, message);
                                        LogUtilsxp.e2(TAG, "isSend2:" + isSend);

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

                                        translateAnimation.setDuration(4000);
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
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
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

            case R.id.btn_share:


                new ShareAction(ShoppingForBirdActivity.this)
                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                        //分享平台
                        .addButton("复制链接", "复制链接", "info_icon_1", "info_icon_1")
                        // 分享面板添加自定义按钮
                        .setShareboardclickCallback(shareBoardlistener)
                        //面板点击监听器
                        .open();


                break;
        }
    }




    //分享的监听
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            if (share_media == null) {
                //根据key来区分自定义按钮的类型，并进行对应的操作
                if (snsPlatform.mKeyword.equals("复制链接")) {
                    //点击后复制微信号的逻辑
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(mShareUrl);
                    ToastUtils.showToast(mContext, "链接已复制成功!");
                }

            } else {//社交平台的分享行为
                //分享的图片
                //                UMImage thumb = new UMImage(HtmlStoreActivity.this, R.drawable.logo_zhijiao);
                //                //分享链接
                //                UMWeb web = new UMWeb(UrlContent.getContentDetialsData+myApplication.getThe_farmer_aid());
                //                web.setTitle("");//标题
                //                web.setThumb(thumb);  //缩略图
                //                web.setDescription("");//描述
                //                new ShareAction(getActivity())
                //                        .setPlatform(share_media)
                //                        .withText("多平台分享")
                //                        .withMedia(web)
                //                        .setCallback(shareListener)
                //                        .share();


                UMImage thumb = new UMImage(ShoppingForBirdActivity.this, R.drawable.logo_zhijiao);


                UMWeb web = new UMWeb(mShareUrl);
                web.setTitle(shopName);//标题
                web.setThumb(thumb);  //缩略图
                web.setDescription("下载富硒宝app领红包");//描述

                //                new ShareAction(HtmlStoreActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).
                //                        setCallback(shareListener).withMedia(web).share();
                new ShareAction(ShoppingForBirdActivity.this)
                        .setPlatform(share_media)
                        //                        .withText("多平台分享")
                        .withMedia(web)
                        .setCallback(shareListener)
                        .share();


            }
        }
    };


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ShoppingForBirdActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShoppingForBirdActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //            Toast.makeText(InviteFansActivity.this,"取消了",Toast.LENGTH_LONG).show();

        }
    };








    private void initCompent() {


        WebSettings settings = mWebView.getSettings();

        //webView  加载视频
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
        }

        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setPluginState(WebSettings.PluginState.ON);

        final String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + "JianDao");
        settings.setJavaScriptEnabled(true);
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
        mWebView.setWebViewClient(new WebViewClient() {

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                LogUtilsxp.e2(TAG, "ForTheBirdurl:" + url);

                Intent mIntent = new Intent(ShoppingForBirdActivity.this, HtmlStorePayActivity.class);
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
                    mWebView.loadUrl("javascript:getShopMsg('" + token + "','" + "" + "','" + id + "')");
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
                if (!TextUtils.isEmpty(shopName)) {
                    titleText.setText(shopName);
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

}
