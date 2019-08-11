package com.shuangpin.rich.ui.html;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HtmlStoreActivity extends BaseActivity {


    private static final String TAG = "HtmlStoreActivity";
    @InjectView(R.id.my_progress_bar)
    ProgressBar mProgressBar;//进度条
    @InjectView(R.id.wv_browser)
    WebView mWebView;//浏览器

    @InjectView(R.id.title)
    TextView title;//
    @InjectView(R.id.btn_close)
    Button btnClose;//
    @InjectView(R.id.btn_share)
    ImageView btn_share;//
    private Context mContext = HtmlStoreActivity.this;
    private String url;
    private String SHARE_STORE_URL;
    private IWXAPI api;
    private Toast toast;
    private String token;
    private String uid;
    private String dynamic;
    private String shareUrl;
    private String shareTitle = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_store);
        StatusBarUtil.setStatusBar(this, R.color.white);
        setTitleBar(SHOW_NOTHING);
        ButterKnife.inject(this);

        token = PrefUtils.readToken(HtmlStoreActivity.this);
        uid = PrefUtils.readUid(HtmlStoreActivity.this);

        url = getIntent().getStringExtra("url");
        SHARE_STORE_URL = getIntent().getStringExtra("url");


        dynamic = getIntent().getStringExtra("dynamic");

        LogUtilsxp.e2(TAG, "url------------" + url);
        initCompent();
        btnClose.setVisibility(View.VISIBLE);
        btn_share.setVisibility(View.VISIBLE);

        btnClose.setOnClickListener(this);

        //
        btn_share.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    btn_share.setVisibility(View.GONE);
                } else {
                    finish();
                }
                break;
            case R.id.btn_close:

                finish();

                break;
            case R.id.btn_share:


                new ShareAction(HtmlStoreActivity.this)
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
                    cm.setText(shareUrl);
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


                UMImage thumb = new UMImage(HtmlStoreActivity.this, R.drawable.logo_zhijiao);


                UMWeb web = new UMWeb(HttpsApi.SHARE_SHOP_URL);
                web.setTitle("富硒宝商城");//标题
                web.setThumb(thumb);  //缩略图
                web.setDescription("点击查看详情");//描述

                //                new ShareAction(HtmlStoreActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).
                //                        setCallback(shareListener).withMedia(web).share();
                new ShareAction(HtmlStoreActivity.this)
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
            Toast.makeText(HtmlStoreActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HtmlStoreActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

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


        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + "KeRanEPin");
        mWebView.addJavascriptInterface(new JSHook(), "hello");
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
                shareUrl = url;

                LogUtilsxp.e2(TAG, "url:" + url);

                if(url.contains("&c=index&a=index")||url.contains("check&token")){

                    view.loadUrl(url);
                }else {
                    Intent mIntent = new Intent(HtmlStoreActivity.this, HtmlStorePayActivity.class);
                    mIntent.putExtra("title", " ");
                    mIntent.putExtra("url", url);
                    mIntent.putExtra("isShopEnter", "1");

                    startActivity(mIntent);
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

                shareTitle = title;
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
                btn_share.setVisibility(View.GONE);
            } else {
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


        }

        @JavascriptInterface
        public void showAndroid() {
            //            final String info = "来自手机内的内容！！！";

            CommonUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {


                    mWebView.loadUrl("javascript:getInfo('" + token + "')");
                }
            });

        }

        public String getInfo() {

            return token;
        }
    }

}
