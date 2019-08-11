package com.shuangpin.rich.ui.fragmentwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.bean.WalletFansBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.widget.MyScrollView;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/2/18.
 */

public class AgentWithdrawalFragment extends WalletBaseFragment {


    private List<WalletFansBean> pList = new ArrayList<>();
    private MyScrollView relativeLayout;
    private RuntCustomProgressDialog dialog;
    private TextView witgdralMoney;
    private TextView nowMoney;
    private CheckBox cb_1;
    private CheckBox cb_2;
    private CheckBox cb_3;
    private EditText et1;
    private EditText et2;
    private LinearLayout llWallet1;
    private LinearLayout llWallet2;
    private LinearLayout llWallet3;
    private Button btnWithdraw;
    private String proxy;
    private RuntCustomProgressDialog runtDialog;
    private String money;

    @Override
    protected View getSuccessView() {
        dialog = new RuntCustomProgressDialog(getActivity());
        dialog.setMessage("正在刷新数据...");
        relativeLayout = (MyScrollView) View.inflate(getActivity(), R.layout.agent_withdrawal_fragment, null);

        witgdralMoney = (TextView) relativeLayout.findViewById(R.id.tv_point_explain);
        nowMoney = (TextView) relativeLayout.findViewById(R.id.tv_now_money);
        cb_1 = (CheckBox) relativeLayout.findViewById(R.id.cb_electronic_invoice);
        cb_2 = (CheckBox) relativeLayout.findViewById(R.id.cb_paper_invoice);
        cb_3 = (CheckBox) relativeLayout.findViewById(R.id.cb_generation_of_tax_point);
        et1 = (EditText) relativeLayout.findViewById(R.id.et_electronic_invoice);
        et2 = (EditText) relativeLayout.findViewById(R.id.et_paper_invoice);

        llWallet1 = (LinearLayout) relativeLayout.findViewById(R.id.ll_wallet_1);
        llWallet2 = (LinearLayout) relativeLayout.findViewById(R.id.ll_wallet_2);
        llWallet3 = (LinearLayout) relativeLayout.findViewById(R.id.ll_wallet_3);

        cb_1.setChecked(true);
        llWallet1.setVisibility(View.VISIBLE);
        cb_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_2.setChecked(false);
                    cb_3.setChecked(false);
                    llWallet1.setVisibility(View.VISIBLE);
                    llWallet2.setVisibility(View.GONE);
                    llWallet3.setVisibility(View.GONE);

                }
            }
        });
        cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_1.setChecked(false);
                    cb_3.setChecked(false);
                    llWallet2.setVisibility(View.VISIBLE);
                    llWallet1.setVisibility(View.GONE);
                    llWallet3.setVisibility(View.GONE);
                }
            }
        });

        cb_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_1.setChecked(false);
                    cb_2.setChecked(false);
                    llWallet3.setVisibility(View.VISIBLE);
                    llWallet2.setVisibility(View.GONE);
                    llWallet1.setVisibility(View.GONE);
                }
            }
        });
        proxy = "2";
        btnWithdraw = (Button) relativeLayout.findViewById(R.id.btn_withdraw);

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
//            proxy	否	int	1扣20% 2 电子发票 3 纸质发票
            public void onClick(View view) {
                if (cb_1.isChecked() || cb_2.isChecked() ||cb_3.isChecked()) {
                    if (cb_1.isChecked()) {//
                        String invoice1 = et1.getText().toString().trim();
                        if(TextUtils.isEmpty(invoice1)){
                            ToastUtils.showToast(baseContext, "请输入邮箱号码");
                            return;
                        }
                        proxy = "2";
                        runtDialog = new RuntCustomProgressDialog(getActivity());
                        runtDialog.setMessage("数据加载中···");
                        runtDialog.show();
                        CustomTrust customTrust = new CustomTrust(baseContext);
                        OkHttpClient okHttpClient = customTrust.client;

                        /**
                         * money	是	int	金额
                         type	是	int	1粉丝提现 2 商户提现 3 代理提现
                         proxy	否	int	1扣20% 2 电子发票 3 纸质发票
                         remark	否	string	邮箱/
                         */
                        String token = PrefUtils.readToken(baseContext);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("money", money)
                                .add("type", "3")
                                .add("proxy", proxy)
                                .add("remark", invoice1)
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
                                                ToastUtils.showToast(baseContext, msg);
                                                getPickABirdInfo();


                                            } else if (resultCode == 403) {//token失效 重新登录
                                                ToastUtils.showToast(baseContext, msg);
                                                Intent mIntent = new Intent(baseContext, LoginActivity.class);
                                                mIntent.putExtra("title", "登录");
                                                PrefUtils.writeToken("", baseContext);
                                                baseContext.startActivity(mIntent);  //重新启动LoginActivity

                                            } else {
                                                ToastUtils.showToast(baseContext, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });


                    } else if (cb_2.isChecked()) {//
                        String invoice1 = et2.getText().toString().trim();

                        if(TextUtils.isEmpty(invoice1)){
                            ToastUtils.showToast(baseContext, "请输入邮箱号码");
                            return;
                        }
                        proxy = "3";
                        runtDialog = new RuntCustomProgressDialog(getActivity());
                        runtDialog.setMessage("数据加载中···");
                        runtDialog.show();
                        CustomTrust customTrust = new CustomTrust(baseContext);
                        OkHttpClient okHttpClient = customTrust.client;

                        /**
                         * money	是	int	金额
                         type	是	int	1粉丝提现 2 商户提现 3 代理提现
                         proxy	否	int	1扣20% 2 电子发票 3 纸质发票
                         remark	否	string	邮箱/
                         */
                        String token = PrefUtils.readToken(baseContext);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("money", money)
                                .add("type", "3")
                                .add("proxy", proxy)
                                .add("remark", invoice1)
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
                                                ToastUtils.showToast(baseContext, msg);
                                                getPickABirdInfo();


                                            } else if (resultCode == 403) {//token失效 重新登录
                                                ToastUtils.showToast(baseContext, msg);
                                                Intent mIntent = new Intent(baseContext, LoginActivity.class);
                                                mIntent.putExtra("title", "登录");
                                                PrefUtils.writeToken("", baseContext);
                                                baseContext.startActivity(mIntent);  //重新启动LoginActivity

                                            } else {
                                                ToastUtils.showToast(baseContext, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });

                    }else if (cb_3.isChecked()) {//
                        proxy = "1";

                        runtDialog = new RuntCustomProgressDialog(getActivity());
                        runtDialog.setMessage("数据加载中···");
                        runtDialog.show();
                        CustomTrust customTrust = new CustomTrust(baseContext);
                        OkHttpClient okHttpClient = customTrust.client;

                        /**
                         * money	是	int	金额
                         type	是	int	1粉丝提现 2 商户提现 3 代理提现
                         proxy	否	int	1扣20% 2 电子发票 3 纸质发票
                         remark	否	string	邮箱/
                         */
                        String token = PrefUtils.readToken(baseContext);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("money", money)
                                .add("type", "3")
                                .add("proxy", proxy)
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
                                                ToastUtils.showToast(baseContext, msg);
                                                getPickABirdInfo();


                                            } else if (resultCode == 403) {//token失效 重新登录
                                                ToastUtils.showToast(baseContext, msg);
                                                Intent mIntent = new Intent(baseContext, LoginActivity.class);
                                                mIntent.putExtra("title", "登录");
                                                PrefUtils.writeToken("", baseContext);
                                                baseContext.startActivity(mIntent);  //重新启动LoginActivity

                                            } else {
                                                ToastUtils.showToast(baseContext, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }




                } else {
                    ToastUtils.showToast(baseContext, "请选择方式");

                    return;
                }


            }
        });
        getPickABirdInfo();
        return relativeLayout;
    }


    @Override
    protected List<WalletFansBean> requestData() {
        setDisPort();
        return pList;
    }

    // 我是老板中获取分销商品列表方法
    public void setDisPort() {


    }

    private void getPickABirdInfo() {


        CustomTrust customTrust = new CustomTrust(baseContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(baseContext);
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //                       isOut 1  展示
                        try {
                            LogUtilsxp.e2("AgentWithdrawalFragment:", responseString);
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
                                money = data.optString("proxyMoney");
                                if (!TextUtils.isEmpty(money)) {
                                    witgdralMoney.setText("" + money);
                                }
                                if (!TextUtils.isEmpty(money)) {
                                    nowMoney.setText("当前余额:  ¥" + money);
                                }
                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(baseContext, msg);
                                Intent mIntent = new Intent(baseContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", baseContext);
                                baseContext.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(baseContext, msg);
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
