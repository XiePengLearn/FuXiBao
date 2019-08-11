package com.shuangpin.rich.ui.activity.logo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.GzwTimeCountUtil;
import com.shuangpin.rich.util.GzwUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

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

public class BindPhoneActivity extends BaseActivity {

    private static final String TAG ="BindPhoneActivity" ;
    private Context mContext;
    private String token;

    @InjectView(R.id.edit_bind_tel)
    EditText editBindTel;
    @InjectView(R.id.btn_bind_get_code)
    Button btnBindGetCode;
    @InjectView(R.id.edit_bind_code)
    EditText editBindCode;
    @InjectView(R.id.btn_bind_confirm)
    Button btnBindConfirm;
    private RuntCustomProgressDialog runtDialog;
    private String phoneNum1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(BindPhoneActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = BindPhoneActivity.this;
        token = PrefUtils.readToken(mContext);
        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        btnBindGetCode.setOnClickListener(this);
        btnBindConfirm.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.btn_bind_get_code:


                String phoneNum = editBindTel.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNum)){

                    ToastUtils.showToast(mContext,"手机号不能为空");
                    return;
                }

                if (!GzwUtils.isMobileNum(phoneNum)) {

                    ToastUtils.showToast(mContext,"请输入正确的手机号码");
                    return;
                }

                getCodeCAPTCHA(phoneNum);//获取注册验证码
                break;
            case R.id.btn_bind_confirm:

                phoneNum1 = editBindTel.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNum1)){

                    ToastUtils.showToast(mContext,"手机号不能为空");
                    return;
                }

                if (!GzwUtils.isMobileNum(phoneNum1)) {

                    ToastUtils.showToast(mContext,"请输入正确的手机号码");
                    return;
                }


                String bindCode = editBindCode.getText().toString().trim();
                if(TextUtils.isEmpty(bindCode)){

                    ToastUtils.showToast(mContext,"验证码不能为空");
                    return;
                }


                runtDialog.show();
                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                String token = PrefUtils.readToken(mContext);
                RequestBody requestBody = new FormBody.Builder()
                        .add("phone", phoneNum1)
                        .add("captcha",bindCode)
                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_ADD_PHONE)
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

                        LogUtilsxp.e2(TAG, "URL_SEND_CAPTCHA_result:" + responseString);
                        runtDialog.dismiss();
                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                //                        {"code":0,"data":"","msg":true}
                                //                       isOut 1  展示
                                try {
                                    JSONObject object = new JSONObject(responseString);
                                    int resultCode = object.optInt("code");
                                    String msg = object.optString("msg");
                                    if (resultCode == 0) {

//                                        {"code":0,"data":["15601267550"],"msg":"绑定成功"}
                                        ToastUtils.showToast(mContext, msg);
                                        PrefUtils.writeIsPhone("1",mContext);
                                        PrefUtils.writePhone(phoneNum1,mContext);

                                        Intent mIntent = new Intent(mContext, MainActivity.class);
                                        mIntent.putExtra("title", "首页");
                                        startActivity(mIntent);

                                        finish();

                                    } else if (resultCode == 403) {//token失效 重新登录
                                        ToastUtils.showToast(mContext, msg);
                                        Intent mIntent = new Intent(mContext, LoginActivity.class);
                                        mIntent.putExtra("title", "登录");
                                        PrefUtils.writeToken("", mContext);
                                        mContext.startActivity(mIntent);  //重新启动LoginActivity

                                    } else {
                                        ToastUtils.showToast(mContext, msg);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });


                break;
        }
    }

    private void getCodeCAPTCHA(String phoneNum) {


        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("phone",phoneNum)
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND_CAPTCHA)
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

                LogUtilsxp.e2(TAG, "URL_SEND_CAPTCHA_result:" + responseString);
                runtDialog.dismiss();
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
//                        {"code":0,"data":"","msg":true}
                        //                       isOut 1  展示
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                ToastUtils.showToast(mContext,"获取验证码成功");

                                // 点击验证码倒计时
                                GzwTimeCountUtil countUtil = new GzwTimeCountUtil(BindPhoneActivity.this, 60000, 1000, btnBindGetCode);
                                countUtil.start();
                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mContext, msg);
                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mContext);
                                mContext.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(mContext, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}
