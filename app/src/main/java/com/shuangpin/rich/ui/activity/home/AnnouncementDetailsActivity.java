package com.shuangpin.rich.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.DateUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnnouncementDetailsActivity extends BaseActivity {

    private static final String TAG = "AnnouncementDetailsActivity";
    @InjectView(R.id.tv_announcement_details_title)
    TextView titleName;//标题
    @InjectView(R.id.tv_announcement_details_time)
    TextView timeName;//时间
    @InjectView(R.id.wv_browser)
    WebView mWebView;//公告详情
    private Context mContext;
    private String token;
    private String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_details);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(AnnouncementDetailsActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = AnnouncementDetailsActivity.this;
        token = PrefUtils.readToken(mContext);

        Intent mIntent = getIntent();
        id = mIntent.getStringExtra("id");

        LogUtilsxp.e2(TAG,"id:"+id);


        getDataFromServer();


    }

    private void getDataFromServer() {

        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)

                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_ANNOUNCEMENT)
                .post(requestBody)
                .addHeader("Authorization", token)
                .build();

        //
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_ANNOUNCEMENT_result:" + responseString);
                //                {
                //                    "code": 0,
                //                        "data": {
                //                             "title": "拣到平台内部上线！",
                //                            "content": "SFDASFDAS",
                //                            "createdAt": "1545117789"
                //                },
                //                    "msg": "公告内容"
                //                }
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {

                                JSONObject data = object.optJSONObject("data");
                                String title = data.optString("title");
                                String content = data.optString("content");
                                String createdAt = data.optString("createdAt");

                                if (!TextUtils.isEmpty(title)) {
                                    titleName.setText(title);
                                }
                                if (!TextUtils.isEmpty(content)) {
//                                    des.setText(content);
                                    initCompent(content);
                                }
                                if (!TextUtils.isEmpty(createdAt)) {
                                    long l = Long.parseLong(createdAt);
                                    String time = DateUtils.formatDate("yyyy-MM-dd HH:mm:ss", l);
                                    if (!TextUtils.isEmpty(time)) {
                                        timeName.setText(time);
                                    }
                                }


                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mContext, msg);
                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mContext);
                                mContext.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(mContext, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }


    private void initCompent(String data) {


        WebSettings settings = mWebView.getSettings();

        settings.setTextZoom(110);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);//设定支持viewport  //设置webview推荐使用的窗口
        settings.setLoadWithOverviewMode(true);           //设置webview加载的页面的模式
        settings.setBuiltInZoomControls(false);           // 隐藏显示缩放按钮
        settings.setSupportZoom(true);                    //设定不支持缩放
        settings.setDisplayZoomControls(false);           //隐藏webview缩放按钮
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setInitialScale(100);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//使用默认缓存
        settings.setDomStorageEnabled(true);//DOM储存API

        final Message msg = new Message();
        mWebView.loadDataWithBaseURL( null, data , "text/html", "UTF-8", null ) ;
        mWebView.setWebViewClient(new WebViewClient() {

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {


                  view.loadUrl(url);
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
//                if (newProgress == 100) {
//                    mProgressBar.setVisibility(View.GONE);
//                } else {
//                    if (View.GONE == mProgressBar.getVisibility()) {
//                        mProgressBar.setVisibility(View.GONE);
//                    }
//                    mProgressBar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //                Message msg = new Message();
                //                msg.what = 1;
                //                msg.obj = title;
                //                mHandler.sendMessage(msg);
//                if (!TextUtils.isEmpty(title)) {
//                    titleText.setText(title);
//                }

            }

        });
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mWebView.getSettings().setSavePassword(false);
    }
}
