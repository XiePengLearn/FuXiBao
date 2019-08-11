package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.WalletFansBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.widget.RadioGroupEx;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.R.id;

public class FansWithdrawalActivity extends BaseActivity {

    private List<WalletFansBean> pList = new ArrayList<>();
    private boolean isFirstResu = false;
    private RuntCustomProgressDialog dialog;
    RadioGroupEx mRadioGroupEx;
    private Context mContext;
    RadioButton rb_1;//50元
    RadioButton rb_2;//100元
    RadioButton rb_3;//200元

    RadioButton mGoneHndred;//隐藏的radioButton

    private String fixedAmount = "15";//固定金额
    private RuntCustomProgressDialog runtDialog;
    private TextView tv_returned_to_fans;
    private TextView tv_fans_withdraw_record;
    private Button btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_withdrawal);
        ButterKnife.inject(this);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(FansWithdrawalActivity.this, R.color.theme_color);

        mContext = FansWithdrawalActivity.this;

        dialog = new RuntCustomProgressDialog(mContext);
        dialog.setMessage("正在请求数据...");
        dialog.show();

        mRadioGroupEx = (RadioGroupEx) findViewById(R.id.rge_group_fans);
        rb_1 = (RadioButton) findViewById(R.id.rb_1);
        rb_2 = (RadioButton) findViewById(R.id.rb_2);
        rb_3 = (RadioButton) findViewById(R.id.rb_3);
        mGoneHndred = (RadioButton) findViewById(R.id.rb_gong);

        btnWithdraw = (Button) findViewById(R.id.btn_withdraw);


        tv_returned_to_fans = (TextView) findViewById(R.id.tv_returned_to_fans_fans);
        tv_fans_withdraw_record = (TextView) findViewById(R.id.tv_fans_withdraw_record);
        tv_fans_withdraw_record.setVisibility(View.VISIBLE);
        tv_fans_withdraw_record.setOnClickListener(this);

        rb_1.setChecked(true);
        mRadioGroupEx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case id.rb_1:
                        fixedAmount = "15";

                        break;
                    case id.rb_2:
                        fixedAmount = "100";

                        break;
                    case id.rb_3:
                        fixedAmount = "200";

                        break;

                    case id.rb_gong:
                        fixedAmount = "0";

                        break;
                    default:
                        break;
                }
            }
        });

        btnWithdraw.setOnClickListener(this);
        getPickABirdInfo(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;

            case R.id.tv_fans_withdraw_record:

                Intent mIntent = new Intent(FansWithdrawalActivity.this, TheWithdrawalOfSubsidiaryActivity.class);

                mIntent.putExtra("title", "提现记录");

                startActivity(mIntent);

                break;
            case R.id.btn_withdraw:
                runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("数据加载中···");
                runtDialog.show();
                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                /**
                 * money	是	int	金额
                 type	是	int	1粉丝提现 2 商户提现 3 代理提现
                 proxy	否	int	1扣20% 2 电子发票 3 纸质发票
                 remark	否	string	邮箱/
                 */
                String token = PrefUtils.readToken(mContext);
                RequestBody requestBody = new FormBody.Builder()
                        .add("money", fixedAmount)
                        .add("type", "1")
                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_TI_XIAN)
                        .addHeader("Authorization", token)
                        .post(requestBody)
                        .build();


                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtilsxp.e("tag", "onFailure: " + e.getMessage());
                        runtDialog.dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseString = response.body().string();

                        LogUtilsxp.e2("tag", "URL_TI_XIAN:" + responseString);
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
                                        ToastUtils.showToast(mContext, msg);
                                        getPickABirdInfo(0);


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

    private void getPickABirdInfo(int num) {


        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                //                .add("num",num+"")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_USER_MONEY)
                .addHeader("Authorization", token)
                //                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                dialog.dismiss();
                LogUtilsxp.e2("tag", "URL_USER_MONEY:" + responseString);
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //                       isOut 1  展示
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                /**
                                 * {
                                 "code": 0,
                                 "data": {
                                 "money": "275.34",
                                 "userNum": "0.0000",
                                 "birdMoney": "8.71",
                                 "fansMoney": "157.13",
                                 "proxyMoney": "109.5000",
                                 "shopMoney": "0.00"
                                 },
                                 "msg": "操作成功"
                                 }
                                 */
                                JSONObject data = object.optJSONObject("data");
                                String money = data.optString("helpMoney");
                                if (!TextUtils.isEmpty(money)) {
                                    tv_returned_to_fans.setText("粉丝助力收益:  ¥" + money);
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


}
