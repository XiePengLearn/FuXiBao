package com.shuangpin.rich.ui.activity.logo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.Canfx;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.util.utilsbar.XpStatusBarUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";
    @InjectView(R.id.btn_wei_xin)
    Button   weiXin;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.tv_xieyi)
    TextView xieyi;

    @InjectView(R.id.cb_apply_logo)
    CheckBox cbApplyLogo;
    private Context mContext;


    @InjectView(R.id.edit_login_phone)
    EditText edit_login_phone;


    @InjectView(R.id.edit_login_pwd)
    EditText edit_login_pwd;

    @InjectView(R.id.btn_test_login)
    Button       btn_test_login;
    @InjectView(R.id.ll_login_root)
    LinearLayout ll_login_root;

    @InjectView(R.id.left_btn)
    LinearLayout left_btn;
    private String mLoginPhone;
    private String mLoginPwd;
    private int    mCurrentVersionCode;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_login);
        //        StatusBarUtil.setStatusBar(this, R.color.theme_color_title_them);

        XpStatusBarUtil.setImmersiveStatusBar(this, true);
        ButterKnife.inject(this);
        mContext = LoginActivity.this;

        weiXin.setOnClickListener(this);
        xieyi.setOnClickListener(this);
        title.setText("登录");
        //        tackOutWeiXin.setOnClickListener(this);
        btn_test_login.setOnClickListener(this);


        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            // 版本号
            mCurrentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        isCheckMethod();
        left_btn.setVisibility(View.GONE);


//        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(this);
//        swipeRefreshLayout.setColorSchemeColors();
//        swipeRefreshLayout.s

    }

    private void isCheckMethod() {


        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("version", mCurrentVersionCode + "")
                .add("type", "android")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_IS_CHECK)
                //                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_IS_CHECK_result:" + responseString);
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //{"code":0,"data":{"isCheck":1},"msg":"成功"}  //1审核  0不审核
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultcode = object.optInt("result");
                            String msg = object.optString("msg");
                            if (resultcode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                String isCheck = data.optString("isCheck");

                                if (isCheck.equals("1")) {
                                    //审核期间
                                    //                                    ll_login_root.setVisibility(View.VISIBLE);
                                    //                                    weiXin.setVisibility(View.GONE);

                                    ll_login_root.setVisibility(View.GONE);
                                    weiXin.setVisibility(View.VISIBLE);

                                } else {
                                    ll_login_root.setVisibility(View.GONE);
                                    weiXin.setVisibility(View.VISIBLE);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.left_btn:
                this.finish();
                break;
            case R.id.btn_wei_xin:
                boolean checked = cbApplyLogo.isChecked();
                if (!checked) {
                    ToastUtils.showToast(mContext, "请阅读并同意《用户登录协议》");
                    return;
                }
                UMShareAPI mShareAPI = UMShareAPI.get(LoginActivity.this);
                mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);



                break;
            case R.id.tv_xieyi:
                String url = HttpsApi.REGISTER;


                Intent mIntent = new Intent(mContext, HtmlActivity.class);
                mIntent.putExtra("title", "用户登录协议");
                mIntent.putExtra("url", url);
                startActivity(mIntent);

                break;

            case R.id.btn_test_login:
                mLoginPhone = edit_login_phone.getText().toString().trim();
                mLoginPwd = edit_login_pwd.getText().toString().trim();

                if (!TextUtils.isEmpty(mLoginPhone) && !TextUtils.isEmpty(mLoginPwd)) {

                    if (mLoginPhone.equals("15601267550") && mLoginPwd.equals("123456")) {

                        loginMethod("oa_4Z56uBCAYALS17R7h12UBcr54", "鹏.谢", "男", "zh_CN", "朝阳", "北京",
                                "中国", "http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83erToic2lDbQho82K2VU4gk8DiccuWLHPem0MnbibIKG52yd1olYicI7cxs3FhCOd8LfKYXc2ar9JiaLpFw/132",
                                "ohpIw1C_T6hLj_hwZ3_LL9KJurnc", "23_4BlyC-h5tv2Glp8R_x1LWY1MpWH-Y7rC3MmCc-1bns0hd5xTRtoOwc_oGxKjmzFovql4PPHb5DDFJAgoCBoXUu7P1fXiZAebWiwfZsm53ag");

                    }
                } else {
                    ToastUtils.showToast(mContext, "请输入账号密码");
                    return;
                }

                break;
        }
    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            ToastUtils.showToast(mContext, "登录成功");
            LogUtilsxp.e2(TAG, data.toString());
            requestWXLogin(data);

        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            ToastUtils.showToast(mContext, "登录失败");
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.showToast(mContext, "用户取消登录");
        }
    };


    /**
     * NSDictionary *dic = @{@"userinfo[openid]":model.openid,
     *
     * @param data
     * @"userinfo[nickname]":model.nickname,
     * @"userinfo[sex]":model.sex,
     * @"userinfo[language]":model.language,
     * @"userinfo[city]":model.city,
     * @"userinfo[province]":model.province,
     * @"userinfo[country]":model.country,
     * @"userinfo[headimgurl]":model.headimgurl,
     * @"userinfo[unionid]":model.unionid,
     * @"userinfo[access_token]":model.accesstoken,
     * @"istest":@""};
     */
    private void requestWXLogin(Map<String, String> data) {


        String openid = data.get("openid");//openid
        String nickname = data.get("screen_name");//用户名称
        String sex = data.get("gender");//性别
        String language = data.get("language");//语言
        String city = data.get("city");//城市
        String province = data.get("province");//省份
        String country = data.get("country");//国家
        String headimgurl = data.get("profile_image_url");//头像
        String unionid = data.get("unionid");//unionid
        String access_token = data.get("access_token");//access_token

        loginMethod(openid, nickname, sex, language, city, province, country, headimgurl, unionid, access_token);


    }

    private void loginMethod(String openid, String nickname, String sex, String language, String city, String province, String country, String headimgurl, String unionid, String access_token) {
        CustomTrust customTrust = new CustomTrust(LoginActivity.this);
        OkHttpClient okHttpClient = customTrust.client;


        //        PrefUtils.putString(mContext,"openid:",openid);
        //        PrefUtils.putString(mContext,"nickname:",nickname);
        //        PrefUtils.putString(mContext,"sex:",sex);
        //        PrefUtils.putString(mContext,"language:",language);
        //        PrefUtils.putString(mContext,"city:",city);
        //        PrefUtils.putString(mContext,"province:",province);
        //        PrefUtils.putString(mContext,"country:",country);
        //        PrefUtils.putString(mContext,"headimgurl:",headimgurl);
        //        PrefUtils.putString(mContext,"unionid:",unionid);
        //        PrefUtils.putString(mContext,"access_token:",access_token);


        RequestBody requestBody = new FormBody.Builder()

                .add("userinfo[openid]", openid)
                .add("userinfo[nickname]", nickname)
                .add("userinfo[sex]", sex)
                .add("userinfo[language]", language)
                .add("userinfo[city]", city)
                .add("userinfo[province]", province)
                .add("userinfo[country]", country)
                .add("userinfo[headimgurl]", headimgurl)
                .add("userinfo[unionid]", unionid)
                .add("userinfo[access_token]", access_token)
                .build();
        LogUtilsxp.e2(TAG, "userinfo[openid]:" + openid + "userinfo[nickname]:" + nickname +
                "userinfo[sex]:" + sex
                + "userinfo[language]:" + language +
                "userinfo[city]:" + city +
                "userinfo[province]:" + province +
                "userinfo[country]:" + country +
                "userinfo[headimgurl]:" + headimgurl +
                "userinfo[unionid]:" + unionid +
                "userinfo[access_token]:" + access_token);
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_LOGIN)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //
                ////                String myResponse = response.body().string();
                //                //耗时操作，完成之后发送消息给Handler，完成UI更新；
                //                mHandler.sendEmptyMessage(0);
                //
                //                //需要数据传递，用下面方法；
                //                Message msg = new Message();
                //                msg.obj = response;//可以是基本类型，可以是对象，可以是List、map等；
                //                mHandler.sendMessage(msg);

                String responseData = response.body().string();
                LogUtilsxp.e2(TAG, "URL_LOGIN_message:" + responseData);
                try {
                    String data = responseData;
                    JSONObject object = new JSONObject(data);
                    int code = object.optInt("code");
                    String message = object.optString("msg");

                    //code=403  重新登录
                    if (code == 0) {

                        JSONObject dataObject = object.optJSONObject("data");
                        String token = dataObject.optString("token");
                        JSONObject userInfoObject = dataObject.optJSONObject("userInfo");

                        String uid = userInfoObject.optString("uid");
                        String head_img = userInfoObject.optString("head_img");
                        String nick_name = userInfoObject.optString("nick_name");
                        String gender = userInfoObject.optString("gender");
                        String phone = userInfoObject.optString("phone");
                        if (!TextUtils.isEmpty(phone)) {
                            if (phone.length() > 10) {
                                PrefUtils.writePhone(phone, mContext);
                            }

                        }

                        String isPhone = dataObject.optString("isPhone");//0没有绑定手机号  1已经绑定手机号

                        PrefUtils.writeIsPhone(isPhone, mContext);
                        PrefUtils.writeToken(token, mContext);
                        PrefUtils.writeUid(uid, mContext);
                        PrefUtils.writeHeadImg(head_img, mContext);
                        PrefUtils.writeNiceName(nick_name, mContext);
                        PrefUtils.writeGender(gender, mContext);


                        if ("1".equals(isPhone)) {

                            Intent mIntent = new Intent(mContext, MainActivity.class);
                            mIntent.putExtra("title", "首页");
                            startActivity(mIntent);

                            finish(); //结束当前的activity的生命周期


                        } else {

                            //没有绑定手机号,需要
                            Intent mIntent = new Intent(mContext, BindPhoneActivity.class);
                            mIntent.putExtra("title", "绑定手机号");

                            startActivity(mIntent);
                        }


                    } else if (code == 403) {
                        ToastUtils.showToast(mContext, message);
                        //                            Intent mIntent = new Intent(mContext, LoginActivity.class);
                        //                            mIntent.putExtra("title", "登录");
                        //                            PrefUtils.writePassword("",mContext);
                        //                            PrefUtils.writeToken("",mContext);
                        //                            mContext.startActivity(mIntent);  //重新启动LoginActivity
                        //                            finish();

                    } else {
                        ToastUtils.showToast(mContext, message);


                    }
                } catch (Exception e) {


                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
