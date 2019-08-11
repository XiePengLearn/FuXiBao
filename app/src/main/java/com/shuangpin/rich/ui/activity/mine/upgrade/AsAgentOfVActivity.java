package com.shuangpin.rich.ui.activity.mine.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.MainActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.mine.threelinkage.CityBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.CountryBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.LocationBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.ThreeMenuDialogArea;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.GzwUtils;
import com.shuangpin.rich.util.IDCard;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.wxapi.WXPayHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class AsAgentOfVActivity extends BaseActivity {

    private static final String TAG = "AsAgentOfVActivity";

    private Context mContext;

    @InjectView(R.id.cb_apply_logo)
    CheckBox cbApplyLogo;
    @InjectView(R.id.tv_xieyi)
    TextView xieyi;

    @InjectView(R.id.et_business_merchant_name)
    EditText businessMerchantName; //商户姓名
    @InjectView(R.id.et_business_phone_number)
    EditText businessPhoneNumber; //手机号
    @InjectView(R.id.et_business_id_card_no)
    EditText businessIdCardNo; //身份证号码
    @InjectView(R.id.et_business_Invitation_agent_number)
    EditText businessInvitationAgentNumber; //邀请代理号
    @InjectView(R.id.btn_business_submit_audit)
    Button businessSubmitAudit; //提交审核

    @InjectView(R.id.et_select_city)
    TextView selectCity;//请选择省市区
    private String token;

    private String cityAreaName;
    private String cityAreaId;
    private String districtAreaName;
    private String districtAreaId;
    private String provinceAreaName;
    private String provinceAreaId;

    private String area_province;
    private String area_city;
    private String area_county;
    private IWXAPI api;
    private Toast toast;
    public static String ALIPAY_JSON_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_as_agent_of_v);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(AsAgentOfVActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = AsAgentOfVActivity.this;
        token = PrefUtils.readToken(mContext);
        selectCity.setOnClickListener(this);
        businessSubmitAudit.setOnClickListener(this);
        xieyi.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            //升级
            case R.id.et_select_city:
                selectCity();
                //                String longitude = PrefUtils.readLongitude(mContext);
                //                String latitude = PrefUtils.readLatitude(mContext);
                //
                //                if (!longitude.equals("0.0") && !latitude.equals("0.0")) {
                //                    mIntent = new Intent(mContext, LocateTheAddressActivity.class);
                //                    mIntent.putExtra("title", "");
                //                    double v1 = Double.parseDouble(latitude);
                //                    double v2 = Double.parseDouble(longitude);
                //                    mIntent.putExtra("latitude", v1);//获取纬度信息
                //                    mIntent.putExtra("longitude", v2);//获取经度信息
                //                    startActivityForResult(mIntent, 100);
                //
                //                } else {
                //                    ToastUtils.showToast(mContext, "定位请求失败,请回到首页重新请求定位");
                //                }
                break;

            case R.id.tv_xieyi:
                String url = HttpsApi.DAILIXIEYI;


                mIntent = new Intent(mContext, HtmlActivity.class);
                mIntent.putExtra("title", "大微合伙人协议");
                mIntent.putExtra("url", url);
                startActivity(mIntent);

                break;
            //提交审核
            case R.id.btn_business_submit_audit:


                boolean checked = cbApplyLogo.isChecked();
                if (!checked) {
                    ToastUtils.showToast(mContext, "请阅读并同意《大微区域合伙人协议》");
                    return;
                }


                String businessName = businessMerchantName.getText().toString().trim();
                if (TextUtils.isEmpty(businessName)) {
                    ToastUtils.showToast(mContext, "请输入姓名");
                    return;
                }

                String phoneNumber = businessPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToast(mContext, "请输入手机号码");
                    return;
                } else if (!GzwUtils.isMobileNum(phoneNumber)) {
                    ToastUtils.showToast(mContext, "请输入正确的手机号码");
                    return;
                }

                String idCardNo = businessIdCardNo.getText().toString().trim();
                if (TextUtils.isEmpty(idCardNo)) {
                    ToastUtils.showToast(mContext, "请输入身份证号码");
                    return;
                }

                try {
                    if (!IDCard.IDCardValidate(idCardNo)) {
                        ToastUtils.showToast(mContext, "请输入正确的身份证号");
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    ToastUtils.showToast(mContext, "请输入正确的身份证号");
                    return;
                }

                String agentNumber = businessInvitationAgentNumber.getText().toString().trim();
                if (TextUtils.isEmpty(agentNumber)) {
                    //选填
                    agentNumber = "0";
                } else if (!GzwUtils.isMobileNum(agentNumber)) {
                    ToastUtils.showToast(mContext, "请输入正确的手机号码");
                    return;
                }
                String chanceCity = selectCity.getText().toString().trim();
                if (TextUtils.isEmpty(chanceCity)) {
                    ToastUtils.showToast(mContext, "请选择省市区");
                    return;
                }

                /**
                 * 参数名	是否必须	类型	说明
                 type	是	int	类型1大微代理2黄金代理
                 payType	是	int	支付类型
                 name	是	string	名字
                 identity	是	string	身份证
                 province	是	int	省
                 city	是	int	市
                 county	是	int	区
                 referrer	否	int	手机号
                 */
                atOnceChange("1",businessName,idCardNo,agentNumber);

                break;
        }
    }



    private void atOnceChange(final String type, final String businessName, final String idCardNo, final String agentNumber ) {

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

                payMoneyToBuyFans(type, "2",businessName,idCardNo,agentNumber);

                dialog.dismiss();
            }
        });
        //支付宝
        changeMoneyBag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoneyToBuyFans(type, "3",businessName,idCardNo,agentNumber);

                dialog.dismiss();
            }
        });
        //余额
        changeOtherAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoneyToBuyFans(type, "1",businessName,idCardNo,agentNumber);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void payMoneyToBuyFans(String type, final String payType,String businessName,String idCardNo,String agentNumber) {

        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()
                .add("type", type)
                .add("payType", payType)
                .add("name", businessName)
                .add("identity", idCardNo)
                .add("province", area_province)
                .add("city", area_city)//oss路径
                .add("county", area_county)
                .add("referrer", agentNumber)


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_USER_PROXY)
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

                LogUtilsxp.e2(TAG, "URL_USER_PROXY_result:" + responseString);

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
                                    Intent mIntent = new Intent(AsAgentOfVActivity.this, MainActivity.class);
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
                                    WXPayHelper.getInstance(AsAgentOfVActivity.this, new WXPayHelper.WXPayCallBack() {
                                        @Override
                                        public void success() {
                                            ToastUtils.showToast(mContext, "支付成功");
                                            Intent mIntent = new Intent(AsAgentOfVActivity.this, MainActivity.class);
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
                                            PayTask payTask = new PayTask(AsAgentOfVActivity.this);
                                            String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                            LogUtilsxp.e2(TAG, "支付结果:" + result);
                                            if (result.contains("resultStatus={9000}")) {
                                                ToastUtils.showToast(mContext, "支付成功");
                                                Intent mIntent = new Intent(AsAgentOfVActivity.this, MainActivity.class);
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







    /**
     * 选择城市
     */
    private void selectCity() {


        final ThreeMenuDialogArea dialog = new ThreeMenuDialogArea(mContext, AsAgentOfVActivity.this);
        dialog.setonItemClickListener(new ThreeMenuDialogArea.MenuItemClickListener() {
            @Override
            public void onMenuItemClick(CountryBean.DataBean menuData, LocationBean.DataBean locationBean, CityBean.DataBean cityBean) {
                selectCity.setText(locationBean.getArea_name() + " " + cityBean.getArea_name() + " " + menuData.getArea_name());
                area_province = locationBean.getArea_id();
                area_city = cityBean.getArea_id();

                area_county = menuData.getArea_id();
                LogUtilsxp.e2(TAG, "area_province:" + area_province + "area_city:" + area_city + "area_county:" + area_county);

            }
        });
        dialog.show();

    }



}
