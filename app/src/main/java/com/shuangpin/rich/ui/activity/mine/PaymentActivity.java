package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alipay.sdk.app.PayTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.ImageLoaderOptions;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.wxapi.WXPayHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class PaymentActivity extends BaseActivity {

    private static final String TAG = "PaymentActivity";
    private Context mContext;
    private String mToken;

    private IWXAPI api;
    private Toast toast;


    @InjectView(R.id.cb_wechat_pay1)
    CheckBox cbWechatPay;//微信支付
    @InjectView(R.id.cb_alipay_pay1)
    CheckBox cbAlipayPay;//支付宝支付

    @InjectView(R.id.iv_header)
    ImageView ivHeaderView;//商户头像

    @InjectView(R.id.txt_shop_name)
    TextView txtShopName;//商户名称

    @InjectView(R.id.tv_all_money)
    TextView allMoney;//当前可用余额
    @InjectView(R.id.tv_can_deduction_money)
    TextView canDeductionMoney;//当前可抵扣余额

    @InjectView(R.id.et_pay_money_number)
    TextView etPayMoneyNumber;//输入金额

    @InjectView(R.id.tb_money_toggle)
    ToggleButton toggleButton;//使用余额抵扣

    @InjectView(R.id.btn_finish)
    Button btnFinish;//确认支付
    private Intent mIntent;
    private String mShopId;
    public static String ALIPAY_JSON_DATA;
    private RuntCustomProgressDialog mRuntDialog;
    private String mWallet;
    private String mDiscount;

    private double mCanDeductionPickMoney = 0.0;
    private double mCanDeductionPickMoneynumber = 0.0;
    private String mIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(PaymentActivity.this, R.color.theme_color);
        ButterKnife.inject(this);
        mContext = PaymentActivity.this;
        mToken = PrefUtils.readToken(mContext);

        mIntent = getIntent();
        mShopId = mIntent.getStringExtra("id");

        cbWechatPay.setChecked(true);
        cbWechatPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbAlipayPay.setChecked(false);
                }
            }
        });
        cbAlipayPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbWechatPay.setChecked(false);
                }
            }
        });

        btnFinish.setOnClickListener(this);
        etPayMoneyNumber.addTextChangedListener(mTextWatcher);

        toggleButton.setChecked(true);
        requestDataFromServer();

    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String string = editable.toString();
            if (string == null || string.equals("") || string.startsWith(".")) {
                mCanDeductionPickMoney = 0.0;
                canDeductionMoney.setText("当前可抵扣余额:" + 0.0);
            } else {
                double mInputMoneyNumber = Double.parseDouble(string);//输入支付的金额

                double mDiscountNumber = Double.parseDouble(mDiscount);//商户设置的拣鸟余额使用比例
                double mWalletNumber = Double.parseDouble(mWallet);//当前钱包的可用余额

                double canDeductionmoneyNumber = mInputMoneyNumber * mDiscountNumber;//根据输入金额和拣鸟余额的乘积


                if (canDeductionmoneyNumber > mWalletNumber) {//根据输入金额和拣鸟余额的乘积  大于 当前余额  最多使用当前余额
                    mCanDeductionPickMoney = mWalletNumber;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String p = decimalFormat.format(mCanDeductionPickMoney);//format 返回的是字符串
                    canDeductionMoney.setText("当前可抵扣余额:" + p);
                } else {
                    mCanDeductionPickMoney = canDeductionmoneyNumber;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String p = decimalFormat.format(mCanDeductionPickMoney);//format 返回的是字符串
                    canDeductionMoney.setText("当前可抵扣余额:" + p);
                }

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;


            case R.id.btn_finish:

                if (cbWechatPay.isChecked() || cbAlipayPay.isChecked()) {
                    if (cbWechatPay.isChecked()) {//选择范围1微信

                        //                        支付方式 2微信 3支付宝

                        payMethod("2");


                    } else if (cbAlipayPay.isChecked()) {//选择范围2支付宝

                        //                        支付方式 2微信 3支付宝


                        payMethod("3");


                    }
                } else {
                    ToastUtils.showToast(mContext, "请选择支付方式");

                    return;
                }

                break;


        }
    }


    private void payMethod(final String modePayment) {

        String inputMoney = etPayMoneyNumber.getText().toString().trim();
        if (TextUtils.isEmpty(inputMoney)) {
            ToastUtils.showToast(mContext, "请输入支付金额");
            return;
        }
        if (inputMoney.startsWith(".")) {
            ToastUtils.showToast(mContext, "输入格式有误,请重新输入");
            return;
        }
        if (inputMoney.endsWith(".")) {
            ToastUtils.showToast(mContext, "输入格式有误,请重新输入");
            return;
        }
        double doubleInputMoney = Double.parseDouble(inputMoney);
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        /**
         * shopId	是	int	店id
         payType	是	int	支付方式 1余额 2微信 3支付宝
         money	是	float	实际支付金额
         isOpen	是	int	余额抵扣开关 0不抵扣 1抵扣
         wallet	否	float	余额抵扣金额
         */
        boolean checked = toggleButton.isChecked();

        if (checked) {
            mIsOpen = "1";
            mCanDeductionPickMoneynumber = mCanDeductionPickMoney;
        } else {
            mIsOpen = "0";
            mCanDeductionPickMoneynumber = 0.0;
        }


        double money = doubleInputMoney - mCanDeductionPickMoneynumber; //输入金额-拣鸟抵扣=支付金额


        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String pmoney = decimalFormat.format(money);//format 返回的是字符串
        String pMCanDeductionPickMoneynumber = decimalFormat.format(mCanDeductionPickMoneynumber);//format 返回的是字符串

        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", mShopId)//
                .add("payType", modePayment)//
                .add("money", inputMoney + "")//
                .add("isOpen", mIsOpen)//
//                .add("wallet", pMCanDeductionPickMoneynumber + "")//


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_SHOP_QRCODE)
                .post(requestBody)
                .addHeader("Authorization", mToken)
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
                                //         支付方式 2微信 3支付宝

                                if (modePayment.equals("2")) {


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
                                    WXPayHelper.getInstance(PaymentActivity.this, new WXPayHelper.WXPayCallBack() {
                                        @Override
                                        public void success() {
                                            ToastUtils.showToast(mContext, "支付成功");
                                            Intent mIntent = new Intent(PaymentActivity.this, MainActivity.class);
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
                                            PayTask payTask = new PayTask(PaymentActivity.this);
                                            String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                            LogUtilsxp.e2(TAG, "支付结果:" + result);
                                            if (result.contains("resultStatus={9000}")) {
                                                ToastUtils.showToast(mContext, "支付成功");
                                                Intent mIntent = new Intent(PaymentActivity.this, MainActivity.class);
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


    private void requestDataFromServer() {
        mRuntDialog = new RuntCustomProgressDialog(mContext);
        mRuntDialog.setMessage("数据加载中···");
        mRuntDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", mShopId)
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_QRCODE_DATA)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                mRuntDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_INVITE_POSTER_result:" + responseString);
                mRuntDialog.dismiss();
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
                                 * data”: {
                                 “wallet”: “18.53”,
                                 “discount”: “0.0500”,
                                 “header”：”beijing.aliyuncs.com/shop/6/1546079989840.jpg”,
                                 “name”：”拣到”
                                 },
                                 */

                                JSONObject data = object.optJSONObject("data");
                                mWallet = data.optString("wallet");
                                mDiscount = data.optString("discount");
                                String header = data.optString("header");
                                String name = data.optString("name");

                                if (!TextUtils.isEmpty(name)) {
                                    txtShopName.setText(name);
                                }
                                if (!TextUtils.isEmpty(mWallet)) {
                                    allMoney.setText("当前可用总余额:" + mWallet);
                                }

                                ImageLoader.getInstance().displayImage(header, ivHeaderView, ImageLoaderOptions.fadein_options);

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
