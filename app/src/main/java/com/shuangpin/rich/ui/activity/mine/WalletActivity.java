package com.shuangpin.rich.ui.activity.mine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.shuangpin.rich.zbor.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletActivity extends BaseActivity {

    private static final String TAG = "WalletActivity";
    private Context mContext;
    private String token;

    @InjectView(R.id.txt_shop_title_phone)
    TextView txtShopTitleMoney;//

    @InjectView(R.id.tv_guize)
    TextView tvGuize;//规则


    @InjectView(R.id.id_fans_help)
    TextView fansHelpMoney;//粉丝助力金额


    @InjectView(R.id.tv_expend_detail)
    TextView tvExpendDetail;//余额支出明细

    @InjectView(R.id.btn_withdraw_fans)
    Button btn_withdraw_fans;//粉丝助力提现

    @InjectView(R.id.rl_income_money)
    RelativeLayout rlIncomeMoney;//
    @InjectView(R.id.rl_history)
    RelativeLayout rl_history;//

    @InjectView(R.id.rl_pay_money)
    RelativeLayout payMoney;//


    @InjectView(R.id.rl_history_gathering)
    RelativeLayout rl_history_gathering;//


    @InjectView(R.id.txt_cash_number)
    TextView txtCashNumber;//中奖余额_大转盘

    @InjectView(R.id.tv_winning_record)
    TextView tvWinningRecord;//中奖纪录

    @InjectView(R.id.btn_winning_cash)
    Button btnWinningCash;//中奖余额提现_大转盘

    private RuntCustomProgressDialog runtDialog;
    private String mHelpMoney;
    private String mPrizeMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(WalletActivity.this, R.color.white);
        ButterKnife.inject(this);
        mContext = WalletActivity.this;
        token = PrefUtils.readToken(mContext);


        getDataFromServer();

        rlIncomeMoney.setOnClickListener(this);
        tvGuize.setOnClickListener(this);
        payMoney.setOnClickListener(this);
        rl_history.setOnClickListener(this);
        rl_history_gathering.setOnClickListener(this);
        btn_withdraw_fans.setOnClickListener(this);
        tvExpendDetail.setOnClickListener(this);
        btnWinningCash.setOnClickListener(this);
        tvWinningRecord.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.tv_expend_detail:

                //余额支出明细
                Intent intentExpend = new Intent(mContext, ExpendDetailActivity.class);
                intentExpend.putExtra("title", "余额支出明细");
                startActivity(intentExpend);

                break;

            case R.id.tv_winning_record:  //中奖纪录

                //中奖纪录
                Intent winningRecord = new Intent(mContext, WinningRecordActivity.class);
                winningRecord.putExtra("title", "中奖纪录");
                startActivity(winningRecord);

                break;

            case R.id.rl_history_gathering:
                //历史记录
                Intent intentHistoryGathering = new Intent(mContext, PayAndGatheringActivity.class);
                intentHistoryGathering.putExtra("title", "用户支付与合伙人收款记录");
                startActivity(intentHistoryGathering);

                break;
            case R.id.rl_history:
                //历史记录
                Intent intentHistory = new Intent(mContext, TheWithdrawalOfSubsidiaryActivity.class);
                intentHistory.putExtra("title", "提现记录");
                startActivity(intentHistory);

                break;
            case R.id.rl_pay_money:
                Intent intentCamera = new Intent(mContext, MipcaActivityCapture.class);
                intentCamera.putExtra("title", "扫一扫");
                startActivity(intentCamera);

                break;
            case R.id.rl_income_money:
                //                ToastUtils.showToast(mContext, "未达到提现要求");
                Intent mIntent = new Intent(mContext, WithdrawActivity.class);
                mIntent.putExtra("title", "提现");
                startActivity(mIntent);
                break;
            case R.id.btn_withdraw_fans://粉丝助力提现


                double doubleMoney = Double.parseDouble(mHelpMoney);
                if (doubleMoney == 0) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.find_open_dialog, null);
                    final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
                    dialog.setContentView(dialogView);
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.CENTER);
                    dialog.setCanceledOnTouchOutside(true);
                    TextView findBirdSuccess = (TextView) dialogView.findViewById(R.id.iv_find_bird_success);//成功寻鸟
                    ImageView iv_close_button = (ImageView) dialogView.findViewById(R.id.iv_close_button);//成功寻鸟
                    iv_close_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    //成功寻鸟
                    findBirdSuccess.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {


                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                } else {
                    Intent mIntentFans = new Intent(mContext, FansWithdrawalActivity.class);
                    mIntentFans.putExtra("title", "粉丝助力提现");
                    startActivity(mIntentFans);
                }

                break;
            case R.id.btn_winning_cash://转盘余额提现


                double doubleMoneyWinning = Double.parseDouble(mPrizeMoney);
                if (doubleMoneyWinning == 0) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.find_winning_dialog, null);
                    final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
                    dialog.setContentView(dialogView);
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.CENTER);
                    dialog.setCanceledOnTouchOutside(true);
                    TextView findBirdSuccess = (TextView) dialogView.findViewById(R.id.iv_find_bird_success);//成功寻鸟
                    ImageView iv_close_button = (ImageView) dialogView.findViewById(R.id.iv_close_button);//成功寻鸟
                    iv_close_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    //成功寻鸟
                    findBirdSuccess.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {


                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                } else {
                    Intent mIntentFans = new Intent(mContext, WinningWithdrawalActivity.class);
                    mIntentFans.putExtra("title", "中奖余额提现");
                    startActivity(mIntentFans);
                }

                break;
            case R.id.tv_guize:

                AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);//MainActivity.this为当前环境


                builder.setTitle("收益说明");//提示框标题

                builder.setMessage("1、拥有十个粉丝才可提现。\n" +
                        "2、您可以通过完成本APP内提供的任务来赚取元宝。\n" +
                        "3、元宝可转换成现金，可提现或在线购物消费。\n" +
                        "4、或提现没有及时到账，别担心，可能会延迟。\n" +
                        "5、如果您连续10日未登录APP，那么每日将会扣除钱包元宝收益，连续未登录将持续扣除。扣完为止。\n" +
                        "6、我们应用先进的人工智能分析您的行为，如发现现造假等违规操作，我们有权阻止您的使用（系统内全部操作）具体可参考系统攻略。\n");//提示内容

                builder.setPositiveButton("确认", null);//确定按钮

                builder.create().show();
                break;

        }
    }

    private void getDataFromServer() {

        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
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
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                runtDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_USER_MONEY_result:" + responseString);
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
                                //                                {"code":0,"data":{"url":"http://pickup.oss-cn-beijing.aliyuncs.com/user/share/uid4.jpg"},"msg":"推广海报"}

                                JSONObject data = object.optJSONObject("data");
                                String money = data.optString("money");
                                mHelpMoney = data.optString("helpMoney");
                                mPrizeMoney = data.optString("prizeMoney");

                                if (!TextUtils.isEmpty(money)) {
                                    txtShopTitleMoney.setText(money);
                                }


                                if (!TextUtils.isEmpty(mHelpMoney)) {
                                    fansHelpMoney.setText(mHelpMoney);


                                }
                                if (!TextUtils.isEmpty(mPrizeMoney)) {
                                    txtCashNumber.setText(mPrizeMoney);


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
