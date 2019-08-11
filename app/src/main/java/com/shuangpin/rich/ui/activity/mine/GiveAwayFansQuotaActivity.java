package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class GiveAwayFansQuotaActivity extends BaseActivity {
    private static final String TAG = "GiveAwayFansQuotaActivity";
    private Context mContext;
    private String token;


    @InjectView(R.id.et_business_phone_number)
    EditText businessPhoneNumber; //手机号
    @InjectView(R.id.et_business_id_card_no)
    EditText businessIdCardNo; //对方ID
    @InjectView(R.id.et_business_Invitation_agent_number)
    EditText businessInvitationAgentNumber; //转让粉丝数
    @InjectView(R.id.btn_business_submit_audit)
    Button businessSubmitAudit; //提交审核


    @InjectView(R.id.tv_give_fans_note)
    TextView tvGiveFansNote; //手机号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_away_fans_quota);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(GiveAwayFansQuotaActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = GiveAwayFansQuotaActivity.this;
        token = PrefUtils.readToken(mContext);

        businessSubmitAudit.setOnClickListener(this);
        tvGiveFansNote.setVisibility(View.VISIBLE);
        tvGiveFansNote.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.tv_give_fans_note:
                mIntent = new Intent(mContext, GiveAwayFansNoteActivity.class);
                mIntent.putExtra("title", "转赠记录");
                startActivity(mIntent);

                break;
            //确定
            case R.id.btn_business_submit_audit:




                String phoneNumber = businessPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToast(mContext, "请输入手机号码");
                    return;
                }

                String idCardNo = businessIdCardNo.getText().toString().trim();
                if (TextUtils.isEmpty(idCardNo)) {
                    ToastUtils.showToast(mContext, "请输入对方用户ID");
                    return;
                }

                String fansNumber = businessInvitationAgentNumber.getText().toString().trim();
                if (TextUtils.isEmpty(fansNumber)) {
                    ToastUtils.showToast(mContext, "请输入转让粉丝数额");
                    return;
                }
                final RuntCustomProgressDialog runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("正在提交数据");
                runtDialog.show();
                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                /**
                 * toPhone	是	int	手机号
                 toUid	是	int	uid
                 fans	是	int	粉丝数
                 */

                RequestBody requestBody = new FormBody.Builder()
                        .add("toPhone", phoneNumber)
                        .add("toUid", idCardNo)
                        .add("fans", fansNumber)
                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_TRANSFER_FANS)
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
                                        data.putExtra("returnMsg", "成功");
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

                break;
        }
    }
}
