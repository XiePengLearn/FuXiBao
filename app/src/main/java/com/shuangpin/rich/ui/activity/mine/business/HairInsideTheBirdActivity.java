package com.shuangpin.rich.ui.activity.mine.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
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
import com.shuangpin.rich.wxapi.WXPayHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class HairInsideTheBirdActivity extends BaseActivity {


    private static final String TAG = "HairInsideTheBirdActivity";
    private Context mContext;
    private String token;

    @InjectView(R.id.btn_next_data)
    Button nextData;//放鸟喽按钮

    @InjectView(R.id.et_enter_the_amount)
    EditText enteTheAamount;//放鸟 金额

    @InjectView(R.id.et_the_bird_number)
    EditText theBirdNumber;//放鸟 个数

    @InjectView(R.id.tv_start_time)
    TextView tvStartTime;//开始时间
    @InjectView(R.id.tv_end_time)
    TextView tvEndTime;//结束时间

    @InjectView(R.id.ll_location)
    LinearLayout llLocation;//具体位置 跟布局

    @InjectView(R.id.tv_mode_of_payment)
    TextView modeOfPayment;//支付方式
    @InjectView(R.id.ll_mode_of_payment)
    LinearLayout modeOfPaymentRoot;//支付方式跟布局


    private View contentView3;
    private PopupWindow popupWindow3 = null;
    private ArrayAdapter adapter3;
    private RuntCustomProgressDialog runtDialog;

    private IWXAPI api;
    private Toast toast;
    public static String ALIPAY_JSON_DATA;

    private String dateStr;

    private int hour;
    private int minute;
    private String time1;
    private String time2;
    private String shopId;
    private String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_inside_the_bird);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(HairInsideTheBirdActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = HairInsideTheBirdActivity.this;
        token = PrefUtils.readToken(mContext);

        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");
        payType = "2";

        String[] list3 = new String[]{"微信", "支付宝", "余额"};
        adapter3 = new ArrayAdapter<String>(mContext, R.layout.item_textview, list3);


        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        nextData.setOnClickListener(this);
        modeOfPaymentRoot.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            //放鸟
            case R.id.btn_next_data:


                String putTheAamount = enteTheAamount.getText().toString().trim();
                if (TextUtils.isEmpty(putTheAamount)) {
                    ToastUtils.showToast(mContext, "请输入金额");
                    return;
                }
                String putBirdNumber = theBirdNumber.getText().toString().trim();
                if (TextUtils.isEmpty(putBirdNumber)) {
                    ToastUtils.showToast(mContext, "请输入元宝个数");
                    return;
                }

                double putMoney = Double.parseDouble(putTheAamount);
                double putNumBird = Double.parseDouble(putBirdNumber + ".0");
                double putMoneyTen = putMoney / putNumBird;

                if (putMoney < 5) {
                    //发店内鸟的总金额 不能低于5元
                    ToastUtils.showToast(mContext, "发送金额不能低于5元");
                    return;
                }
                if (putMoneyTen < 1) {
                    //发送鸟的金额 单个不能低于0.1元
                    ToastUtils.showToast(mContext, "单个元宝金额最低为1元");
                    return;
                }
                //        支付方式 1 余额 2 微信 3 支付宝
                final String modePaymentStr = modeOfPayment.getText().toString().trim();
                if (modePaymentStr.equals("余额")) {
                    payType = "1";
                } else if (modePaymentStr.equals("微信")) {
                    payType = "2";
                } else {
                    payType = "3";
                }


                //开始时间
                String startTime = tvStartTime.getText().toString();
                //结束时间
                String endTime = tvEndTime.getText().toString();
                if (TextUtils.isEmpty(startTime)) {
                    ToastUtils.showToast(mContext, "请选择开始时间");
                    return;
                }
                if (TextUtils.isEmpty(endTime)) {
                    ToastUtils.showToast(mContext, "请选择结束时间");
                    return;
                }

                if(startTime.equals(endTime)){
                    ToastUtils.showToast(mContext, "开始时间和结束时间不能相同");
                    return;
                }


                String[] allStart=startTime.split(":");
                String[] allEnd=endTime.split(":");

                int startHour = Integer.parseInt(allStart[0]);
                int startMin = Integer.parseInt(allStart[1]);


                int endHour = Integer.parseInt(allEnd[0]);
                int endMin = Integer.parseInt(allEnd[1]);

                if(startHour>endHour){
                    ToastUtils.showToast(mContext, "开始时间须小于结束时间");
                    return;
                }
                if(startHour == endHour){
                    if(startMin>endMin){
                        ToastUtils.showToast(mContext, "开始时间须小于结束时间");
                        return;
                    }

                }


                LogUtilsxp.e2(TAG, "startTime:" + allStart[0]+"--"+ allStart[1]+ "endTime:" + endTime);

                runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("正在上传");
                runtDialog.show();
                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                /**
                 * money	是	int	金额
                 shopId	是	int	店
                 numbers	是	int	数方式 1 余额 2 微信 3 支付宝
                 type	是	int	1店外鸟 2店内鸟量
                 payType	是	int	支付
                 scope	否	int	2市3省4全国
                 start	否	int	1230 12点30分
                 end	否	int	1702 17点02分
                 */

                LogUtilsxp.e2(TAG, "money:" +putTheAamount+"shopId"+ shopId+ "numbers:" + putBirdNumber+"payType:"+payType);
                RequestBody requestBody = new FormBody.Builder()
                        .add("money", putTheAamount)//金额
                        .add("shopId", shopId)//店
                        .add("type", "2")	//1店外鸟 2店内鸟量
                        .add("numbers", putBirdNumber)//数量
                        .add("payType", payType)//支付方式 1 余额 2 微信 3 支付宝
                        .add("start", allStart[0]+allStart[1])//开始时间
                        .add("end", allEnd[0]+allEnd[1])//结束时间

                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND_OUT_IN)
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

                        LogUtilsxp.e2(TAG, "URL_SEND_OUT_IN_result:" + responseString);
                        runtDialog.dismiss();

                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    JSONObject object = new JSONObject(responseString);
                                    int resultCode = object.optInt("code");
                                    String msg = object.optString("msg");
                                    if (resultCode == 0) {

                                        //支付方式 1 余额 2 微信 3 支付宝
                                        String modePaymentStr = modeOfPayment.getText().toString().trim();
                                        if (modePaymentStr.equals("余额")) {

                                            ToastUtils.showToast(mContext, "余额支付成功");
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
                                            WXPayHelper.getInstance(HairInsideTheBirdActivity.this, new WXPayHelper.WXPayCallBack() {
                                                @Override
                                                public void success() {
                                                    ToastUtils.showToast(mContext, "支付成功");
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
                                                    PayTask payTask = new PayTask(HairInsideTheBirdActivity.this);
                                                    String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                                    LogUtilsxp.e2(TAG, "支付结果:" + result);
                                                    if (result.contains("resultStatus={9000}")) {
                                                        ToastUtils.showToast(mContext, "支付成功");
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


                break;
            case R.id.left_btn:
                this.finish();
                break;
            //选择支付方式
            case R.id.ll_mode_of_payment:

                classifyMethod();

                break;
            //选择 具体位置
            case R.id.tv_start_time:
                startTime(tvStartTime);

                break;
            case R.id.tv_end_time:
                startTime(tvEndTime);

                break;
        }
    }

    private void startTime(final TextView time) {
        dateStr = "";
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.time_dialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePicker timePicker = (TimePicker) v.findViewById(R.id.time_picker);//时间空件
        TextView timeSure = (TextView) v.findViewById(R.id.time_sure);//确定
        TextView timeCancel = (TextView) v.findViewById(R.id.time_cancel);//取消
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {


            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                HairInsideTheBirdActivity.this.hour = hourOfDay;
                HairInsideTheBirdActivity.this.minute = minute;
                LogUtilsxp.e2(TAG, hourOfDay + ":" + minute);
                if (HairInsideTheBirdActivity.this.hour < 10) {
                    time1 = "0" + Integer.toString(HairInsideTheBirdActivity.this.hour);
                } else {
                    time1 = Integer.toString(HairInsideTheBirdActivity.this.hour);
                }
                if (HairInsideTheBirdActivity.this.minute < 10) {
                    time2 = "0" + Integer.toString(HairInsideTheBirdActivity.this.minute);
                } else {
                    time2 = Integer.toString(HairInsideTheBirdActivity.this.minute);
                }


                dateStr = time1 + ":" + time2;
            }

        });

        timeSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                try {
                    df.parse(time1 + ":" + time2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(dateStr)) {
                    Calendar c1 = Calendar.getInstance();
                    int hour1 = c1.get(Calendar.HOUR_OF_DAY);
                    int minute1 = c1.get(Calendar.MINUTE);
                    time.setText(hour1 + ":" + minute1);
                } else {
                    time.setText(dateStr);
                }

                dialog.dismiss();
            }
        });
        timeCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void postToOss() {


    }


    private void classifyMethod() {
        contentView3 = create3Classificationview();
        int width = mContext.getResources().getDisplayMetrics().heightPixels;
        int height = adapter3.getCount() * CommonUtil.dip2px(mContext, 45) + CommonUtil.dip2px(mContext, (adapter3.getCount() - 1));
        boolean focusable = true;
        popupWindow3 = new PopupWindow(contentView3, width, height, focusable);
        popupWindow3.setBackgroundDrawable(new ColorDrawable());
        popupWindow3.showAsDropDown(modeOfPayment, 0, 0);
    }

    private View create3Classificationview() {
        ListView listView = (ListView) View.inflate(mContext, R.layout.listview, null);
        listView.setAdapter(adapter3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkPayment = (String) parent.getItemAtPosition(position);
                modeOfPayment.setText(checkPayment);
                popupWindow3.dismiss();


            }
        });
        return listView;
    }

}
