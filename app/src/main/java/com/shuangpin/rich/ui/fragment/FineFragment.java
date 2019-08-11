package com.shuangpin.rich.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.find.PutDynamicStateActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 发现
 */
public class FineFragment extends BaseFragment {
    private static final String TAG = "FineFragment";
    private ProgressBar mProgressBar;//进度条
    private WebView mWebView;//浏览器

    private TextView mTitle;//

    private String url;
    private LinearLayout leftButton;
    private String token;
    private ImageView tianJia;
    private RuntCustomProgressDialog runtDialog;


    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_brand, null);


        mProgressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);
        mWebView = (WebView) view.findViewById(R.id.wv_browser);
        mTitle = (TextView) view.findViewById(R.id.title);
        leftButton = (LinearLayout) view.findViewById(R.id.left_btn);
        tianJia = (ImageView) view.findViewById(R.id.tian_jia);
        leftButton.setVisibility(View.INVISIBLE);
        token = PrefUtils.readToken(mMainActivity);
        StatusBarUtil.setStatusBar(mMainActivity, R.color.theme_color_title);
        url = HttpsApi.HTML_DISCOVER;
        LogUtilsxp.e2(TAG, "url------------" + url);
        initCompent();
        tianJia.setVisibility(View.VISIBLE);
        tianJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUserDataFromServer();
            }
        });


        return view;
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

        String ua = settings.getUserAgentString();
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
                Intent mIntent = new Intent(mMainActivity, HtmlActivity.class);
                mIntent.putExtra("title", " ");
                mIntent.putExtra("url", url);

                mMainActivity.startActivity(mIntent);
                return true;
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.loadUrl("javascript:getInfo('" + token + "')");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mMainActivity, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
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
                if (!TextUtils.isEmpty(title)) {
                    mTitle.setText(title);
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


    //销毁Webview
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void getUserDataFromServer() {


        runtDialog = new RuntCustomProgressDialog(mMainActivity);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()

                //				.add("longitude", longitude + "")//经度
                //				.add("latitude", latitude + "")//纬度


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_IS_SEND)
                //				.post(requestBody)
                .addHeader("Authorization", token)
                .build();

        //
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                runtDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                LogUtilsxp.e2(TAG, "URL_IS_SEND_result:" + responseString);
                runtDialog.dismiss();
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

//{"code":0,"data":{"isSend":0,"shopUrl":""},"msg":"您还不是代理，无此权限"}
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                String isSend = data.optString("isSend");
                                String shopUrl = data.optString("shopUrl");
                                //                                0没有  1有
                                if (!TextUtils.isEmpty(isSend)) {
                                    if (isSend.equals("1")) {

                                        if(!TextUtils.isEmpty(shopUrl)){
                                            Intent mIntent = new Intent(mMainActivity, PutDynamicStateActivity.class);
                                            mIntent.putExtra("title", "富硒宝粉丝圈");
                                            mIntent.putExtra("shopUrl",shopUrl);
                                            mMainActivity.startActivity(mIntent);
                                        }else {
                                            Intent mIntent = new Intent(mMainActivity, PutDynamicStateActivity.class);
                                            mIntent.putExtra("title", "富硒宝粉丝圈");
                                            mIntent.putExtra("shopUrl","");
                                            mMainActivity.startActivity(mIntent);
                                        }


                                    } else {
                                        ToastUtils.showToast(mMainActivity, msg);
                                    }
                                }


                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mMainActivity, msg);
                                Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mMainActivity);
                                mMainActivity.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(mMainActivity, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }
}
