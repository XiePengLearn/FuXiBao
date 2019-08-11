package com.shuangpin.rich.ui.html;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.wxapi.WXPayHelper;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;


public class HtmlStorePayActivity extends BaseActivity {


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
    private Context mContext = HtmlStorePayActivity.this;
    private String url;
    private String SHARE_STORE_URL;
    private IWXAPI api;
    private Toast toast;
    private String token;
    private String uid;
    private String dynamic;
    private String shareUrl;
    private String shareTitle = " ";
    private String mIsShopEnter;

    private MyWebChromeClient myWebChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_store_pay);
        StatusBarUtil.setStatusBar(this, R.color.theme_color_title);
        setTitleBar(SHOW_NOTHING);
        ButterKnife.inject(this);

        token = PrefUtils.readToken(HtmlStorePayActivity.this);
        uid = PrefUtils.readUid(HtmlStorePayActivity.this);

        url = getIntent().getStringExtra("url");
        SHARE_STORE_URL = getIntent().getStringExtra("url");


        dynamic = getIntent().getStringExtra("dynamic");
        mIsShopEnter = getIntent().getStringExtra("isShopEnter");

        LogUtilsxp.e2(TAG, "url------------" + url);
        initCompent();
        btnClose.setVisibility(View.VISIBLE);

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


                new ShareAction(HtmlStorePayActivity.this)
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
                //                UMImage thumb = new UMImage(HtmlStorePayActivity.this, R.drawable.logo_zhijiao);
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


                UMImage thumb = new UMImage(HtmlStorePayActivity.this, R.drawable.logo_zhijiao);


                UMWeb web = new UMWeb(shareUrl);
                web.setTitle("富硒宝商城");//标题
                web.setThumb(thumb);  //缩略图
                web.setDescription(shareTitle);//描述

                //                new ShareAction(HtmlStorePayActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).
                //                        setCallback(shareListener).withMedia(web).share();
                new ShareAction(HtmlStorePayActivity.this)
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
            Toast.makeText(HtmlStorePayActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HtmlStorePayActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
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
                if(!TextUtils.isEmpty(mIsShopEnter)){
                    if(mIsShopEnter.equals("1")){

                        if(url.equals("http://shop.xjkrfx.net/mobile/index.php")){

                            //跳到首页
                            finish();

                            LogUtilsxp.e2(TAG,"我执行了");

                        }

                    }
                }


                LogUtilsxp.e2(TAG, "url:" + url);
                if (url.contains("m=drp&c=shop&id=")) {
                    btn_share.setVisibility(View.VISIBLE);
                }else {
                    btn_share.setVisibility(View.GONE);
                }


                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    view.loadUrl(url);
                    return true;
                }


                if (url.contains("we_pay&order_sn") || url.contains("a=pay&type=2")) {
                    api = WXAPIFactory.createWXAPI(mContext, WEICHAT_APP_ID);
                    api.registerApp(WEICHAT_APP_ID);
        /* 自定义获取订单toast */
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.dialog_ordering,
                            (RelativeLayout) findViewById(R.id.dialog_getordering));
                    toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();


                    String weiChatUrl = url;
                    String token = PrefUtils.readToken(mContext);
                    RequestParams params = new RequestParams(weiChatUrl);
                    //                    params.addQueryStringParameter("token", token);
                    //
                    //                    params.addQueryStringParameter("pay_type", "wechat");
                    //                    params.addQueryStringParameter("money", money);
                    final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
                    dialog.setMessage("数据提交中···");
                    dialog.show();

                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            dialog.dismiss();
                            LogUtilsxp.e2(TAG, "GET_WeChat_result" + response);

                            try {
                                /**
                                 * {
                                 "return_code": "SUCCESS",
                                 "return_msg": "OK",
                                 "appid": "wxc57aed6aa217aefd",
                                 "mch_id": "1455742202",
                                 "device_info": "WEB",
                                 "nonce_str": "oKcjkj4J24BWTlvd",
                                 "sign": "835014DF4AB38E13BB1DD1B4068C612A",
                                 "result_code": "SUCCESS",
                                 "prepay_id": "wx22092326552634b93249f7660823521701",
                                 "trade_type": "APP",
                                 "timestamp": 1548120209,
                                 "package": "Sign=WXPay"
                                 }
                                 */

                                JSONObject jsonObject = new JSONObject(response);
                                //                                String return_code = jsonObject.getString("return_code");
                                //                                String return_msg = jsonObject.getString("return_msg");

                                LogUtilsxp.e2(TAG, "sign:" + jsonObject.getString("sign") +
                                        "timestamp:" + jsonObject.getString("timestamp") +
                                        "package:" + jsonObject.getString("package") +
                                        "noncestr:" + jsonObject.getString("noncestr") +
                                        "partnerid:" + jsonObject.getString("partnerid") +
                                        "appid:" + jsonObject.getString("appid") +
                                        "prepayid:" + jsonObject.getString("prepayid"));
                                PayReq req = new PayReq();
                                req.sign = jsonObject.getString("sign");
                                req.timeStamp = jsonObject.getString("timestamp");
                                req.packageValue = jsonObject.getString("package");
                                req.nonceStr = jsonObject.getString("noncestr");
                                req.partnerId = jsonObject.getString("partnerid");
                                req.appId = jsonObject.getString("appid");
                                req.prepayId = jsonObject.getString("prepayid");
                                req.extData = "app data"; // optional
                                api.sendReq(req);
                                WXPayHelper.getInstance(HtmlStorePayActivity.this, new WXPayHelper.WXPayCallBack() {
                                    @Override
                                    public void success() {
                                        ToastUtils.showToast(mContext, "支付成功");
                                        //                                                Intent intent = new Intent();
                                        //                                                intent.setClass(mContext, MainActivity.class);
                                        //                                                intent.putExtra("page", "3");
                                        //                                                startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void cancel() {
                                        ToastUtils.showToast(mContext, "取消了微信支付");
                                    }

                                    @Override
                                    public void falure(String msg) {
                                        ToastUtils.showToast(mContext, "支付失败");
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            ToastUtils.showToast(mContext, ex.getMessage());
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            ToastUtils.showToast(mContext, "cancelled");
                            dialog.dismiss();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });

                } else {
                    /**
                     *
                     *  推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                     */
                    final PayTask task = new PayTask(HtmlStorePayActivity.this);
                    boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                        @Override
                        public void onPayResult(final H5PayResultModel result) {
                            // 支付结果返回

                            final String url = result.getReturnUrl();
                            if (!TextUtils.isEmpty(url)) {
                                HtmlStorePayActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(mContext, "支付成功");
                                        //                                    view.loadUrl(url);
                                        //                                    view.loadUrl(url + "/token/" + token);
                                        //                                    Intent intent = new Intent();
                                        //                                    intent.setClass(mContext, MainActivity.class);
                                        //                                    intent.putExtra("page", "3");
                                        //                                    startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });


                    /**
                     * 判断是否成功拦截
                     * 若成功拦截，则无需继续加载该URL；否则继续加载
                     */
                    if (!isIntercepted) {
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


        myWebChromeClient = new MyWebChromeClient(HtmlStorePayActivity.this);
        mWebView.setWebChromeClient(myWebChromeClient);
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//
//
//        //        browser.setWebViewClient(new MyWebViewClient());
//        mWebView.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    mProgressBar.setVisibility(View.GONE);
//                } else {
//                    if (View.GONE == mProgressBar.getVisibility()) {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                    mProgressBar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//
//                shareTitle = title;
//                //                Message msg = new Message();
//                //                msg.what = 1;
//                //                msg.obj = title;
//                //                mHandler.sendMessage(msg);
//                if (!TextUtils.isEmpty(title)) {
//                    titleText.setText(title);
//                }
//
//            }
//
//        });
//        mWebView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        mWebView.getSettings().setSavePassword(false);
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






    public class MyWebChromeClient extends WebChromeClient {
        private Activity mContext;
        private String mCameraFilePath;
        private ValueCallback<Uri> uploadMessage;
        private ValueCallback<Uri[]> uploadMessageAboveL;
        private final static String TAG = "MyWebChromeClient";
        private final static int FILE_CHOOSER_RESULT_CODE = 10000;


        public MyWebChromeClient(Activity context) {
            mContext = context;
        }


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

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }

        @Override
        //扩容
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(requiredStorage * 2);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.e("h5端的log", String.format("%s -- From line %s of %s", message, lineNumber, sourceID));
        }


        private void openImageChooserActivity() {

            initDialog();
        }

        /**
         * 上传头像时的弹出框
         */
        private void initDialog() {
            new AlertDialog.Builder(mContext)
                    .setTitle("选择照片")
                    .setItems(new String[]{"拍照", "图库选取"},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            Intent i1 = createCameraIntent();
                                            mContext.startActivityForResult(Intent.createChooser(i1, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                            break;
                                        case 1:
                                            Intent i = createFileItent();
                                            mContext.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                            break;
                                    }

                                }
                            }).setNegativeButton("取消", null).show();
        }

        /**
         * 创建选择图库的intent
         *
         * @return
         */
        private Intent createFileItent() {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            return intent;
        }

        /**
         * 创建调用照相机的intent
         *
         * @return
         */
        private Intent createCameraIntent() {
            //            VersionUtils.checkAndRequestPermissionAbove23(mContext);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File externalDataDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            System.out.println("externalDataDir:" + externalDataDir);
            File cameraDataDir = new File(externalDataDir.getAbsolutePath()
                    + File.separator + "browser-photo");
            cameraDataDir.mkdirs();
            mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
                    + System.currentTimeMillis() + ".jpg";
            System.out.println("mcamerafilepath:" + mCameraFilePath);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(mCameraFilePath)));

            return cameraIntent;
        }

        /**
         * 处理拍照返回函数
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (null == uploadMessage && null == uploadMessageAboveL)
                    return;
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                        : data.getData();
                if (uploadMessageAboveL != null) {//5.0以上
                    onActivityResultAboveL(requestCode, resultCode, data);
                } else if (uploadMessage != null) {
                    if (result == null && data == null
                            && resultCode == Activity.RESULT_OK) {
                        File cameraFile = new File(mCameraFilePath);

                        Bitmap bitmap1 = getimage(cameraFile.getPath());

                        result = Uri.parse(MediaStore.Images.Media.insertImage(
                                mContext.getContentResolver(), bitmap1, null, null));
                    }
                    Log.e(TAG, "5.0-result=" + result);
                    uploadMessage.onReceiveValue(result);
                    uploadMessage = null;
                }


            }


        }

        /**
         * 处理拍照返回函数  5。0以上
         *
         * @param requestCode
         * @param resultCode
         * @param intent
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
            Log.e(TAG, "5.0+ 返回了");
            if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
                return;
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                } else {

                    File cameraFile = new File(mCameraFilePath);

                    Bitmap bitmap1 = getimage(cameraFile.getPath());

                    Uri result = Uri.parse(MediaStore.Images.Media.insertImage(
                            mContext.getContentResolver(), bitmap1, null, null));
                    results = new Uri[]{result};
                }
            }
            uploadMessageAboveL.onReceiveValue(results);
            uploadMessageAboveL = null;
        }

        /**
         * 根据图片路径获取图p片
         *
         * @param srcPath
         * @return
         */
        private Bitmap getimage(String srcPath) {
            //            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            //            newOpts.inJustDecodeBounds = true;
            //            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
            //
            //            newOpts.inJustDecodeBounds = false;
            //            int w = newOpts.outWidth;
            //            int h = newOpts.outHeight;
            //            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            //            float hh = 120f;// 这里设置高度为800f
            //            float ww = 120f;// 这里设置宽度为480f
            //            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            //            int be = 1;// be=1表示不缩放
            //            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            //                be = (int) (newOpts.outWidth / ww);
            //            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            //                be = (int) (newOpts.outHeight / hh);
            //            }
            //            if (be <= 0)
            //                be = 1;
            //            newOpts.inSampleSize = be;// 设置缩放比例
            //            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            //            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            //            return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeFile(srcPath, options);
            int height = options.outHeight;
            int width= options.outWidth;
            int inSampleSize = 4; // 默认像素压缩比例，压缩为原图的1/2
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            int minLen = Math.min(height, width); // 原图的最小边长
            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
                inSampleSize = (int)ratio;
            }
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            return  BitmapFactory.decodeFile(srcPath, options); // 解码文件




        }

        //        /**
        //         * 裁剪图片大小
        //         *
        //         * @param image
        //         * @return
        //         */
        //        private Bitmap compressImage(Bitmap image) {
        //
        ////            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ////            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        ////            int options = 100;
        ////            while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        ////                baos.reset();// 重置baos即清空baos
        ////                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        ////                options -= 10;// 每次都减少10
        ////            }
        ////            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        ////            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        //
        //
        //
        //
        //
        //            // 设置参数
        //            BitmapFactory.Options options = new BitmapFactory.Options();
        //            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        //            BitmapFactory.decodeFile(image, options);
        //            int height = options.outHeight;
        //            int width= options.outWidth;
        //            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        //            int minLen = Math.min(height, width); // 原图的最小边长
        //            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
        //                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
        //                inSampleSize = (int)ratio;
        //            }
        //            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        //            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        //            Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
        //
        //
        //
        //
        //            return bitmap;
        //        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "收到返回消息了");
        myWebChromeClient.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }














}
