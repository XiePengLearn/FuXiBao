package com.shuangpin.rich.ui.activity.mine.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.UpdataOfVBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.widget.RadioGroupEx;
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
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class BuyFansActivity extends BaseActivity {

    private static final String TAG = "BuyFansActivity";

    @InjectView(R.id.rge_group_v)
    RadioGroupEx mRadioGroupEx;

    @InjectView(R.id.rb_check_one)
    RadioButton rbCheck1;//100元
    @InjectView(R.id.rb_check_two)
    RadioButton rbCheck2;//299元
    @InjectView(R.id.rb_check_three)
    RadioButton rbCheck3;//495元
    @InjectView(R.id.rb_check_four)
    RadioButton rbCheck4;//980元

    @InjectView(R.id.rb_check_five)
    RadioButton rbCheck5;//2900
    @InjectView(R.id.rb_check_six)
    RadioButton rbCheck6;//4950
    @InjectView(R.id.rb_check_seven)
    RadioButton rbCheck7;//隐藏

    @InjectView(R.id.btn_next_data_buy_v)
    Button btnNextDataBuyV;//购买按钮

    @InjectView(R.id.iv_as_agent_of_v)
    ImageView ivAsAgentOfV;//成为大V代理
    private Context mContext;
    private String token;
    private RuntCustomProgressDialog runtDialog;
    private List<UpdataOfVBean> mDataList;
    private String fixedAmount = "0";//固定金额;
    private String type;

    private IWXAPI api;
    private Toast toast;
    public static String ALIPAY_JSON_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_of_v);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(BuyFansActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = BuyFansActivity.this;
        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();

        getDataFromServer();


        btnNextDataBuyV.setOnClickListener(this);
        ivAsAgentOfV.setOnClickListener(this);

        mRadioGroupEx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.rb_check_one:
                        fixedAmount = mDataList.get(0).getMoney();
                        type = mDataList.get(0).getType();

                        break;
                    case R.id.rb_check_two:
                        fixedAmount = mDataList.get(1).getMoney();
                        type = mDataList.get(1).getType();
                        break;
                    case R.id.rb_check_three:
                        fixedAmount = mDataList.get(2).getMoney();
                        type = mDataList.get(2).getType();

                        break;
                    case R.id.rb_check_four:
                        fixedAmount = mDataList.get(3).getMoney();
                        type = mDataList.get(3).getType();

                        break;

                    case R.id.rb_check_five:
                        fixedAmount = mDataList.get(4).getMoney();
                        type = mDataList.get(4).getType();

                        break;
                    case R.id.rb_check_six:
                        fixedAmount = mDataList.get(5).getMoney();
                        type = mDataList.get(5).getType();


                        break;
                    default:
                        break;
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;

            case R.id.btn_next_data_buy_v:

                //充值金额为固定金额  先做判断 因为有个gong RadioButton
                if (fixedAmount.equals("0")) {
                    ToastUtils.showToast(mContext, "请选择您要购买的粉丝");
                    return;
                } else {
                    ToastUtils.showToast(mContext, "金额为:" + fixedAmount);
                    atOnceChange(type);
                }


                break;
            case R.id.iv_as_agent_of_v:
                Intent mIntent = new Intent(mContext, AsAgentOfVActivity.class);
                mIntent.putExtra("title", "成为大微合伙人");
                startActivity(mIntent);
                break;
        }
    }


    private void atOnceChange(final String type) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.at_once_change_dialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout changeBank = (RelativeLayout) v.findViewById(R.id.rl_wechat_of_v);//微信
        RelativeLayout changeMoneyBag = (RelativeLayout) v.findViewById(R.id.rl_alipay_of_v);//支付宝
        RelativeLayout changeOtherAccount = (RelativeLayout) v.findViewById(R.id.rl_balance_of_v);//余额
        /**
         * if (modePaymentStr.equals("余额")) {
         modePayment = "1";
         } else if (modePaymentStr.equals("微信")) {
         modePayment = "2";
         } else {
         modePayment = "3";
         }
         */
        //微信
        changeBank.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                payMoneyToBuyFans(type, "2");

                dialog.dismiss();
            }
        });
        //支付宝
        changeMoneyBag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoneyToBuyFans(type, "3");

                dialog.dismiss();
            }
        });
        //余额
        changeOtherAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoneyToBuyFans(type, "1");

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void payMoneyToBuyFans(String type, final String payType) {

        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()
                .add("type", type)//
                .add("payType", payType)//


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BUY_FANS)
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

                LogUtilsxp.e2(TAG, "URL_BUY_FANS_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                //支付方式 1 余额 2 微信 3 支付宝
                                if (payType.equals("1")) {

                                    ToastUtils.showToast(mContext, "余额支付成功");
                                    Intent mIntent = new Intent(BuyFansActivity.this, MainActivity.class);
                                    startActivity(mIntent);
                                    finish();

                                } else if (payType.equals("2")) {


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
                                    WXPayHelper.getInstance(BuyFansActivity.this, new WXPayHelper.WXPayCallBack() {
                                        @Override
                                        public void success() {
                                            ToastUtils.showToast(mContext, "支付成功");
                                            Intent mIntent = new Intent(BuyFansActivity.this, MainActivity.class);
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
                                            PayTask payTask = new PayTask(BuyFansActivity.this);
                                            String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                            LogUtilsxp.e2(TAG, "支付结果:" + result);
                                            if (result.contains("resultStatus={9000}")) {
                                                ToastUtils.showToast(mContext, "支付成功");
                                                Intent mIntent = new Intent(BuyFansActivity.this, MainActivity.class);
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

    private void getDataFromServer() {
        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        //        RequestBody requestBody = new FormBody.Builder()
        //                .add("num",num+"")
        //                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_FANS)
                .addHeader("Authorization", token)
                //                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_FANS_result:" + responseString);
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

                                JSONArray jsonArray = object.optJSONArray("data");


                                /**
                                 * "money": 100,
                                 "fans": 100,
                                 "type": 1
                                 */
                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<UpdataOfVBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            UpdataOfVBean detailBean = new UpdataOfVBean();

                                            detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));
                                            detailBean.setFans(jsonArray.optJSONObject(z).optString("fans"));
                                            detailBean.setType(jsonArray.optJSONObject(z).optString("type"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);

                                        if (mDataList.size() >= 1) {
                                            String fans = mDataList.get(0).getFans();
                                            String money = mDataList.get(0).getMoney();
                                            String type = mDataList.get(0).getType();

                                            rbCheck1.setText(fans + "个\n售价" + money + "元");

                                        }
                                        if (mDataList.size() >= 2) {
                                            String fans = mDataList.get(1).getFans();
                                            String money = mDataList.get(1).getMoney();
                                            String type = mDataList.get(1).getType();

                                            rbCheck2.setText(fans + "个\n售价" + money + "元");

                                        }
                                        if (mDataList.size() >= 3) {
                                            String fans = mDataList.get(2).getFans();
                                            String money = mDataList.get(2).getMoney();
                                            String type = mDataList.get(2).getType();

                                            rbCheck3.setText(fans + "个\n售价" + money + "元");

                                        }
                                        if (mDataList.size() >= 4) {
                                            String fans = mDataList.get(3).getFans();
                                            String money = mDataList.get(3).getMoney();
                                            String type = mDataList.get(3).getType();

                                            rbCheck4.setText(fans + "个\n售价" + money + "元");

                                        }
                                        if (mDataList.size() >= 5) {
                                            String fans = mDataList.get(4).getFans();
                                            String money = mDataList.get(4).getMoney();
                                            String type = mDataList.get(4).getType();

                                            rbCheck5.setText(fans + "个\n售价" + money + "元");

                                        }
                                        if (mDataList.size() >= 6) {
                                            String fans = mDataList.get(5).getFans();
                                            String money = mDataList.get(5).getMoney();
                                            String type = mDataList.get(5).getType();

                                            rbCheck6.setText(fans + "个\n售价" + money + "元");

                                        }

                                    }

                                    //

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
