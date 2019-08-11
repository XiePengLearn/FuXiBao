package com.shuangpin.rich.ui.activity.mine.setting;

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
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
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

public class NickNameInputActivity extends BaseActivity {

    private static final String TAG = "NickNameInputActivity";
    private Context mContext;
    @InjectView(R.id.et_input_font)
    EditText et_input_font;

    @InjectView(R.id.btn_que_ding)
    Button btn_que_ding;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name_input);

        setTitleBar(SHOW_LEFT);
        mContext = NickNameInputActivity.this;
        StatusBarUtil.setStatusBar(NickNameInputActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        token = PrefUtils.readToken(mContext);
        btn_que_ding.setVisibility(View.VISIBLE);
        btn_que_ding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nickName = et_input_font.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName)) {


                    final RuntCustomProgressDialog runtDialog = new RuntCustomProgressDialog(mContext);
                    runtDialog.setMessage("正在上传");
                    runtDialog.show();

                    CustomTrust customTrust = new CustomTrust(mContext);
                    OkHttpClient okHttpClient = customTrust.client;

                    //                LogUtilsxp.e2(TAG,"shopName:"+brandName+"province:"+provinceAreaId+
                    //                        "city:"+cityAreaId+"county:"+districtAreaId+
                    //                        "address:"+stringExtraAdress+"header:"+url+
                    //                        "mobile:"+phoneNumber+"identity:"+idCardNo+
                    //                        "referrer:"+agentNumber+"name:"+businessName+
                    //                        "longitude:"+longitudePutTheBird + ""+"latitude:"+latitudePutTheBird);

                    RequestBody requestBody = new FormBody.Builder()
                            .add("nickname", nickName)

                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_EDIT_NICKNAME)
                            .post(requestBody)
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

                            LogUtilsxp.e2(TAG, "URL_CREATE_result:" + responseString);
                            runtDialog.dismiss();

                            CommonUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {


                                    try {
                                        JSONObject object = new JSONObject(responseString);
                                        int resultCode = object.optInt("code");
                                        String msg = object.optString("msg");
                                        if (resultCode == 0) {
                                            ToastUtils.showToast(mContext, msg);
                                            Intent data = new Intent();
                                            data.putExtra("returnMsg", nickName);
                                            setResult(0, data);
                                            finish();//关闭窗口

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


                } else {
                    ToastUtils.showToast(mContext, "请输入要修改的昵称");
                }
            }
        });
        //

    }


}
