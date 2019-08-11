package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.ImageLoaderOptions;
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
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentCodeActivity extends BaseActivity {
    private static final String TAG = "PaymentCodeActivity";
    private Context mContext;
    private RuntCustomProgressDialog runtDialog;
    private String shopId;
    private String url;
    @InjectView(R.id.iv_payment_code)
    ImageView ivInviteFriendsImg;//邀请好友背景图

    @InjectView(R.id.tv_pick_up_deduction)
    TextView mPickUpDeduction;//拣鸟余额最高抵扣

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_code);


        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(PaymentCodeActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = PaymentCodeActivity.this;


        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");

        requestDataFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;


        }
    }

    private void requestDataFromServer() {
        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", shopId + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_QRCODE)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                runtDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_INVITE_POSTER_result:" + responseString);
                runtDialog.dismiss();
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //                       isOut 1  展示
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                //                                {"code":0,"data":{"url":"http://pickup.oss-cn-beijing.aliyuncs.com/user/share/uid4.jpg"},"msg":"推广海报"}

                                JSONObject data = object.optJSONObject("data");
                                url = data.optString("url");
                                String discount = data.optString("discount");
                                if (!TextUtils.isEmpty(discount)) {
                                    if (!discount.equals("0")) {
                                        double vDiscount = Double.parseDouble(discount);
                                        vDiscount = vDiscount * 100;
                                        mPickUpDeduction.setText("拣元宝余额最高抵扣:  "+vDiscount+"%");
                                    }
                                }


                                ImageLoader.getInstance().displayImage(url, ivInviteFriendsImg, ImageLoaderOptions.optionsQrCode);

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
}
