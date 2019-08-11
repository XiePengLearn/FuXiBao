package com.shuangpin.rich.ui.activity.mine.business;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.BirdScopeBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.wxapi.WXPayHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.R.id.ll_mode_of_payment;
import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class SelectTheScopeAndPayActivity extends BaseActivity {

    private static final String TAG = "SelectTheScopeAndPayActivity";
    private Context mContext;
    private List<BirdScopeBean> mDataList;
    @InjectView(R.id.tv_select_scope_name1)
    TextView scopeName1;//
    @InjectView(R.id.tv_select_scope_buy_money1)
    TextView buyMoney1;//
    @InjectView(R.id.cb_select_scope_pay1)
    CheckBox cbPay1;//


    @InjectView(R.id.tv_select_scope_info1)
    TextView desnfo1;//
    @InjectView(R.id.tv_select_scope_info2)
    TextView desnfo2;//
    @InjectView(R.id.tv_select_scope_info3)
    TextView desnfo3;//

    @InjectView(R.id.tv_select_scope_name2)
    TextView scopeName2;//
    @InjectView(R.id.tv_select_scope_buy_money2)
    TextView buyMoney2;//
    @InjectView(R.id.cb_select_scope_pay2)
    CheckBox cbPay2;//

    @InjectView(R.id.tv_select_scope_name3)
    TextView scopeName3;//
    @InjectView(R.id.tv_select_scope_buy_money3)
    TextView buyMoney3;//
    @InjectView(R.id.cb_select_scope_pay3)
    CheckBox cbPay3;//

    @InjectView(ll_mode_of_payment)
    LinearLayout llPayRoot;//
    @InjectView(R.id.tv_mode_of_payment)
    TextView tvModePayment;//
    private String token;

    private IWXAPI api;
    private Toast toast;
    public static String ALIPAY_JSON_DATA;
    @InjectView(R.id.btn_pay_money)
    Button btnPayMoney;//

    private View contentView3;
    private PopupWindow popupWindow3 = null;
    private ArrayAdapter adapter3;
    private String modePayment;
    private String shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_the_scope_and_pay);
        setTitleBar(SHOW_LEFT);
        mContext = SelectTheScopeAndPayActivity.this;
        StatusBarUtil.setStatusBar(SelectTheScopeAndPayActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();
        cbPay1.setChecked(true);
        desnfo1.setVisibility(View.VISIBLE);
        shopId = getIntent().getStringExtra("shopId");

        String[] list3 = new String[]{"微信", "支付宝", "余额"};
        adapter3 = new ArrayAdapter<String>(mContext, R.layout.item_textview, list3);
        cbPay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPay2.setChecked(false);
                    cbPay3.setChecked(false);

                    desnfo1.setVisibility(View.VISIBLE);
                    desnfo2.setVisibility(View.GONE);
                    desnfo3.setVisibility(View.GONE);
                }
            }
        });
        cbPay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPay1.setChecked(false);
                    cbPay3.setChecked(false);

                    desnfo1.setVisibility(View.GONE);
                    desnfo2.setVisibility(View.VISIBLE);
                    desnfo3.setVisibility(View.GONE);
                }
            }
        });
        cbPay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPay2.setChecked(false);
                    cbPay1.setChecked(false);

                    desnfo1.setVisibility(View.GONE);
                    desnfo2.setVisibility(View.GONE);
                    desnfo3.setVisibility(View.VISIBLE);
                }
            }
        });


        getDataFromServer();

        btnPayMoney.setOnClickListener(this);
        llPayRoot.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;

            case R.id.ll_mode_of_payment:
                classifyMethod();
                break;
            case R.id.btn_pay_money:
                if (cbPay1.isChecked() || cbPay2.isChecked() || cbPay3.isChecked()) {
                    if (cbPay1.isChecked()) {//选择范围1
                        if (mDataList.size() > 0) {
                            final String modePaymentStr = tvModePayment.getText().toString().trim();
                            modePayment = "2";
                            if (modePaymentStr.equals("余额")) {
                                modePayment = "1";
                            } else if (modePaymentStr.equals("微信")) {
                                modePayment = "2";
                            } else {
                                modePayment = "3";
                            }

                            String scope = mDataList.get(0).getScope();

                            payMethod(scope);

                        } else {
                            ToastUtils.showToast(mContext, "此支付范围已经去除");
                        }


                    } else if (cbPay2.isChecked()) {//选择范围2
                        if (mDataList.size() > 1) {

                            final String modePaymentStr = tvModePayment.getText().toString().trim();
                            modePayment = "2";
                            if (modePaymentStr.equals("余额")) {
                                modePayment = "1";
                            } else if (modePaymentStr.equals("微信")) {
                                modePayment = "2";
                            } else {
                                modePayment = "3";
                            }

                            String scope = mDataList.get(1).getScope();
                            payMethod(scope);

                        } else {
                            ToastUtils.showToast(mContext, "此支付范围已经去除");
                        }


                    } else if (cbPay3.isChecked()) {//选择范围3
                        if (mDataList.size() > 2) {
                            final String modePaymentStr = tvModePayment.getText().toString().trim();
                            modePayment = "2";
                            if (modePaymentStr.equals("余额")) {
                                modePayment = "1";
                            } else if (modePaymentStr.equals("微信")) {
                                modePayment = "2";
                            } else {
                                modePayment = "3";
                            }

                            String scope = mDataList.get(2).getScope();
                            payMethod(scope);


                        } else {
                            ToastUtils.showToast(mContext, "此支付范围已经去除");
                        }


                    }
                } else {
                    ToastUtils.showToast(mContext, "请选择支付方式");

                    return;
                }


                break;

        }

    }

    private void classifyMethod() {
        contentView3 = create3Classificationview();
        int width = mContext.getResources().getDisplayMetrics().heightPixels;
        int height = adapter3.getCount() * CommonUtil.dip2px(mContext, 45) + CommonUtil.dip2px(mContext, (adapter3.getCount() - 1));
        boolean focusable = true;
        popupWindow3 = new PopupWindow(contentView3, width, height, focusable);
        popupWindow3.setBackgroundDrawable(new ColorDrawable());
        popupWindow3.showAsDropDown(tvModePayment, 0, 0);
    }

    private View create3Classificationview() {
        ListView listView = (ListView) View.inflate(mContext, R.layout.listview, null);
        listView.setAdapter(adapter3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkPayment = (String) parent.getItemAtPosition(position);
                tvModePayment.setText(checkPayment);
                popupWindow3.dismiss();


            }
        });
        return listView;
    }

    private void getDataFromServer() {

        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()

                //                .add("longitude", longitude + "")//经度
                //                .add("latitude", latitude + "")//纬度


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_SCOPE)
                //                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_BIRD_SCOPE_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONArray jsonArray = object.optJSONArray("data");
                                //    "title": "全市放鸟",
                                //            "info": "全市放3650只鸟",
                                //            "scope": 1,
                                //            "money": 365
                                if (null == jsonArray) {
                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<BirdScopeBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            BirdScopeBean detailBean = new BirdScopeBean();

                                            detailBean.setTitle(jsonArray.optJSONObject(z).optString("title"));
                                            detailBean.setInfo(jsonArray.optJSONObject(z).optString("info"));
                                            detailBean.setScope(jsonArray.optJSONObject(z).optString("scope"));
                                            detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);

                                    }

                                    for (int i = 0; i < mDataList.size(); i++) {
                                        if (i == 0) {
                                            String info = mDataList.get(0).getInfo();
                                            String money = mDataList.get(0).getMoney();
                                            String scope = mDataList.get(0).getScope();
                                            String title = mDataList.get(0).getTitle();

                                            if (!TextUtils.isEmpty(info)) {
                                                desnfo1.setText(info);
                                            }
                                            if (!TextUtils.isEmpty(money)) {
                                                buyMoney1.setText(money + "元");
                                            }
                                            if (!TextUtils.isEmpty(title)) {
                                                scopeName1.setText(title);
                                            }

                                        }
                                        if (i == 1) {
                                            String info = mDataList.get(1).getInfo();
                                            String money = mDataList.get(1).getMoney();
                                            String scope = mDataList.get(1).getScope();
                                            String title = mDataList.get(1).getTitle();

                                            if (!TextUtils.isEmpty(info)) {
                                                desnfo2.setText(info);
                                            }
                                            if (!TextUtils.isEmpty(money)) {
                                                buyMoney2.setText(money + "元");
                                            }
                                            if (!TextUtils.isEmpty(title)) {
                                                scopeName2.setText(title);
                                            }

                                        }
                                        if (i == 2) {
                                            String info = mDataList.get(2).getInfo();
                                            String money = mDataList.get(2).getMoney();
                                            String scope = mDataList.get(2).getScope();
                                            String title = mDataList.get(2).getTitle();

                                            if (!TextUtils.isEmpty(info)) {
                                                desnfo3.setText(info);
                                            }
                                            if (!TextUtils.isEmpty(money)) {
                                                buyMoney3.setText(money + "元");
                                            }
                                            if (!TextUtils.isEmpty(title)) {
                                                scopeName3.setText(title);
                                            }

                                        }

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

    private void payMethod(String scope) {

        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", shopId)//
                .add("scope", scope)//
                .add("payType", modePayment)//


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BUY_BIRD)
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

                LogUtilsxp.e2(TAG, "URL_BUY_BIRD_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                //支付方式 1 余额 2 微信 3 支付宝
                                String modePaymentStr = tvModePayment.getText().toString().trim();
                                if (modePaymentStr.equals("余额")) {

                                    ToastUtils.showToast(mContext, "余额支付成功");
                                    Intent mIntent = new Intent(SelectTheScopeAndPayActivity.this, MainActivity.class);
                                    startActivity(mIntent);
                                    finish();

                                } else if (modePaymentStr.equals("微信")) {


                                    api = WXAPIFactory.createWXAPI(mContext, WEICHAT_APP_ID);
                                    api.registerApp(WEICHAT_APP_ID);
                /* 自定义获取订单toast */
                                    LayoutInflater inflater = getLayoutInflater();
                                    final View layout = inflater.inflate(R.layout.dialog_ordering,
                                            (RelativeLayout) findViewById(R.id.dialog_getordering));
                                    toast = new Toast(getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

                                    JSONObject inObj = object.getJSONObject("data");
                                    PayReq req = new PayReq();
                                    req.sign = inObj.getString("sign");
                                    req.timeStamp = inObj.getString("timestamp");
                                    req.packageValue = inObj.getString("package");
                                    req.nonceStr = inObj.getString("noncestr");
                                    req.partnerId = inObj.getString("partnerid");
                                    req.appId = inObj.getString("appid");
                                    req.prepayId = inObj.getString("prepayid");
                                    req.extData = "app data"; // optional
                                    api.sendReq(req);
                                    WXPayHelper.getInstance(SelectTheScopeAndPayActivity.this, new WXPayHelper.WXPayCallBack() {
                                        @Override
                                        public void success() {
                                            ToastUtils.showToast(mContext, "支付成功");
                                            Intent mIntent = new Intent(SelectTheScopeAndPayActivity.this, MainActivity.class);
                                            startActivity(mIntent);
                                            finish();
                                        }

                                        @Override
                                        public void cancel() {
                                            ToastUtils.showToast(mContext, "取消了微信支付");
                                        }

                                        @Override
                                        public void falure(String msg) {
                                            ToastUtils.showToast(mContext, "支付失败");
                                        }
                                    });


                                } else {
                                    //支付宝
                                    JSONObject obj = new JSONObject(responseString);
                                    ALIPAY_JSON_DATA = obj.getString("data");
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            PayTask payTask = new PayTask(SelectTheScopeAndPayActivity.this);
                                            String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                            LogUtilsxp.e2(TAG, "支付结果:" + result);
                                            if (result.contains("resultStatus={9000}")) {
                                                ToastUtils.showToast(mContext, "支付成功");
                                                Intent mIntent = new Intent(SelectTheScopeAndPayActivity.this, MainActivity.class);
                                                startActivity(mIntent);
                                                finish();
                                            } else {
                                                ToastUtils.showToast(mContext, "支付失败");


                                            }

                                        }
                                    }.start();

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
