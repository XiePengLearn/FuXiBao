package com.shuangpin.rich.ui.fragment;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.LogUtilsxp;

/**
 * 商城
 */

public class StoreFragment extends BaseFragment {

	private static final String TAG = "StoreFragment";
	private ProgressBar mProgressBar;//进度条
	private WebView mWebView;//浏览器

	private TextView mTitle;//

	private String url;
	private LinearLayout leftButton;


	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
							  Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_brand, null);


		mProgressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);
		mWebView = (WebView) view.findViewById(R.id.wv_browser);
		mTitle = (TextView) view.findViewById(R.id.title);
		leftButton = (LinearLayout) view.findViewById(R.id.left_btn);
		leftButton.setVisibility(View.INVISIBLE);


		url = HttpsApi.SHOPHTML;
		LogUtilsxp.e2(TAG, "url------------" + url);
		initCompent();
		return view;
	}


	private void initCompent() {


		WebSettings settings = mWebView.getSettings();

		String ua = settings.getUserAgentString();
		settings.setUserAgentString(ua + "JianDao");

		settings.setJavaScriptEnabled(true);
		settings.setUseWideViewPort(true);//设定支持viewport  //设置webview推荐使用的窗口
		settings.setLoadWithOverviewMode(true);           //设置webview加载的页面的模式
		settings.setBuiltInZoomControls(false);           // 隐藏显示缩放按钮
		settings.setSupportZoom(false);                    //设定不支持缩放
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

}
