package com.shuangpin.rich.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.cashluckdraw.LuckPanLayout;
import com.shuangpin.rich.util.ButtonUtils;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/6/20.
 */

public class CashLuckyDrawFragmentDialog extends DialogFragment implements LuckPanLayout.AnimationEndListener, View.OnClickListener {
    private LuckPanLayout luckPanLayout;
    private ImageView goBtn;
    private ImageView mDrawClose;
    private TextView mCodingDrawLottery;
    private TextView mBalanceDrawLottery;
    private ImageView mIvCodingDrawLottery;
    private ImageView mIvBalanceDrawLottery;
    private LinearLayout mLlCodingDraw;
    private LinearLayout mLlBalanceDraw;
    private EditText mEtCodingDraw;
    private TextView mTvBalanceDrawOwn;
    private TextView mTvOneTimeDraw;
    private Button mBtnCodingDraw;
    private Button mBtnBalanceDraw;
    private static final String WINNING_DATE = "WinningDate";
    public static final String EXTRA_DATA = "com.shuangpin.rich.dialog.CashLuckyDrawFragmentDialog";

    private int typeDraw = 1;//  1 为余额抽奖 2为编码抽奖
    private String key;
    private String TAG = "CashLuckyDrawFragmentDialog";
    private static final String ARG_DATE_CASH = "cashdate";
    private String mBirdMoneyNumber;
    private TextView mViewRules;
    private static final String RULE_DATE = "RulesDate";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.cash_luck_draw_dialog, null);


        key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
        //转盘
        luckPanLayout = (LuckPanLayout) view.findViewById(R.id.luckpan_layout);
        mDrawClose = (ImageView) view.findViewById(R.id.iv_draw_close);

        mIvCodingDrawLottery = (ImageView) view.findViewById(R.id.iv_coding_draw_lottery);
        mIvBalanceDrawLottery = (ImageView) view.findViewById(R.id.iv_balance_draw_lottery);

        mCodingDrawLottery = (TextView) view.findViewById(R.id.tv_coding_draw_lottery);
        mBalanceDrawLottery = (TextView) view.findViewById(R.id.tv_balance_draw_lottery);
        mViewRules = (TextView) view.findViewById(R.id.tv_view_rules);

        mLlCodingDraw = (LinearLayout) view.findViewById(R.id.ll_coding_draw);
        mLlBalanceDraw = (LinearLayout) view.findViewById(R.id.ll_balance_draw);
        mEtCodingDraw = (EditText) view.findViewById(R.id.et_coding_draw);

        mTvBalanceDrawOwn = (TextView) view.findViewById(R.id.tv_balance_draw_own);
        mTvOneTimeDraw = (TextView) view.findViewById(R.id.tv_one_time_draw);

        mBtnCodingDraw = (Button) view.findViewById(R.id.btn_coding_draw);
        mBtnBalanceDraw = (Button) view.findViewById(R.id.btn_balance_draw);


        luckPanLayout.setAnimationEndListener(this);
        goBtn = (ImageView) view.findViewById(R.id.go);

        mBirdMoneyNumber = getArguments().getString(ARG_DATE_CASH);

        if (!TextUtils.isEmpty(mBirdMoneyNumber)) {
            mTvBalanceDrawOwn.setText("当前余额\r\n" + mBirdMoneyNumber + "元");
        }


        goBtn.setOnClickListener(this);
        mDrawClose.setOnClickListener(this);
        mCodingDrawLottery.setOnClickListener(this);
        mBalanceDrawLottery.setOnClickListener(this);

        mBtnCodingDraw.setOnClickListener(this);
        mBtnBalanceDraw.setOnClickListener(this);
        mViewRules.setOnClickListener(this);
//        mEtCodingDraw.setOnClickListener(this);

        return view;
    }


    private void sendResult(int resultCode, String data) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, data);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);

    }


    //大转盘请求服务器

    /**
     * if ($type == 1) {  // 1 码  2 钱
     * if (!isset($data['code']) || empty($data['code'])) {
     * return $this->responseJson(1, '', '失败：参数错误1');
     * }
     * $str = $uid . '_' . $time . '_' . $data['code'] . '_' . $time;
     * }
     * if ($type == 2) {  // 1 码  2 钱
     * $str = $uid . '_' . $time . '_' . '_' . $time;
     * }
     */
    private void getDataFromServer(String type, String code) {

        //请求鸟的金额
        String uid = PrefUtils.readUid(getActivity());
        //获取当前的秒值
        long time = System.currentTimeMillis() / 1000;


        String encryptionString = uid + "_" + time + "_" + code + "_" + time;
        String encStr1 = CommonUtil.md5(encryptionString) + key;
        String encStr = CommonUtil.md5(encStr1);

        /**
         * 参数名	是否必须	类型	说明
         type	是	int	1 编码 2 钱
         code	否	string	编码
         sign	是	string	签名
         time	是	int	时间戳(秒)
         */

        //                            LogUtilsxp.e(TAG, "URL_BIRD_CLICK_message:  测试"+encStr22 );
        //        LogUtilsxp.e(TAG, "URL_BIRD_CLICK_message:  测试----" + "id:" + id + "bean:" + birdSign +
        //                "sign:" + encStr + "type:" + type + "time:" + time);
        CustomTrust customTrust = new CustomTrust(getActivity());
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(getActivity());
        RequestBody requestBody = new FormBody.Builder()

                .add("type", type)
                .add("code", code)
                .add("sign", encStr)

                .add("time", time + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_TURN_PLATE)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                /**
                 * {
                 "code": 0,
                 "data": {
                 "id": "1",
                 "name": "1",
                 "balance": 0.0667
                 },
                 "msg": "操作成功"
                 }
                 */

                LogUtilsxp.e2(TAG, "URL_TURN_PLATE_message:" + responseString);
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //                       isOut 1  展示
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {

                                JSONObject data = object.optJSONObject("data");
                                String id = data.optString("id");

                                int position = Integer.parseInt(id);

                                luckPanLayout.rotate(position, 100);


                                mBirdMoneyNumber = data.optString("balance");
                                if (!TextUtils.isEmpty(mBirdMoneyNumber)) {
                                    mTvBalanceDrawOwn.setText("当前余额\r\n" + mBirdMoneyNumber + "元");
                                }

                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(getActivity(), msg);
                                Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", getActivity());
                                getActivity().startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(getActivity(), msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景半透明
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    @Override
    public void endAnimation(int position) {
        //        ToastUtils.showToast(getActivity(), "Position = " + position + ",");

        FragmentManager manager = getFragmentManager();


        WinningFragmentDialog winningFragmentDialog = WinningFragmentDialog.newInstance(position + "");
        winningFragmentDialog.show(manager, WINNING_DATE);
        //        dismiss();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go:
                if (typeDraw == 2) {
                    //编码抽奖
                    String codingDrawNumber = mEtCodingDraw.getText().toString().trim();
                    if (TextUtils.isEmpty(codingDrawNumber)) {
                        ToastUtils.showToast(getActivity(), "请输入包装罐内编码");
                        return;
                    }

                    /**
                     * 参数名	是否必须	类型	说明
                     type	是	int	1 编码 2 钱
                     code	否	string	编码
                     sign	是	string	签名
                     time	是	int	时间戳(秒)
                     */
                    boolean fastDoubleClick = ButtonUtils.isFastDoubleClick();
                    if (fastDoubleClick) {
                        ToastUtils.showToast(getActivity(), "请不要重复点击");
                    } else {
                        getDataFromServer("1", codingDrawNumber);
                    }

                    //


                } else {

                    boolean fastDoubleClick = ButtonUtils.isFastDoubleClick();
                    if (fastDoubleClick) {
                        ToastUtils.showToast(getActivity(), "请不要重复点击");
                    } else {
                        getDataFromServer("2", "");
                    }
                }

                break;
            case R.id.iv_draw_close:


                sendResult(Activity.RESULT_OK, mBirdMoneyNumber);
                dismiss();
                break;

            case R.id.tv_coding_draw_lottery:
                mCodingDrawLottery.setTextColor(getResources().getColor(R.color.white));
                mBalanceDrawLottery.setTextColor(getResources().getColor(R.color.tv_E5E5E5));
                mIvCodingDrawLottery.setVisibility(View.VISIBLE);
                mIvBalanceDrawLottery.setVisibility(View.INVISIBLE);

                mLlCodingDraw.setVisibility(View.VISIBLE);
                mLlBalanceDraw.setVisibility(View.GONE);
                typeDraw = 2;
                break;

            case R.id.tv_balance_draw_lottery:

                mCodingDrawLottery.setTextColor(getResources().getColor(R.color.tv_E5E5E5));
                mBalanceDrawLottery.setTextColor(getResources().getColor(R.color.white));
                mIvCodingDrawLottery.setVisibility(View.INVISIBLE);
                mIvBalanceDrawLottery.setVisibility(View.VISIBLE);

                mLlCodingDraw.setVisibility(View.GONE);
                mLlBalanceDraw.setVisibility(View.VISIBLE);
                typeDraw = 1;
                break;

            case R.id.btn_coding_draw:
                //编码抽奖
                String codingDrawNumber = mEtCodingDraw.getText().toString().trim();
                if (TextUtils.isEmpty(codingDrawNumber)) {
                    ToastUtils.showToast(getActivity(), "请输入包装罐内编码");
                    return;
                }
                boolean fastDoubleClick = ButtonUtils.isFastDoubleClick();
                if (fastDoubleClick) {
                    ToastUtils.showToast(getActivity(), "请不要重复点击");
                } else {
                    getDataFromServer("1", codingDrawNumber);
                }

                break;

            case R.id.btn_balance_draw:
                //余额抽奖
                boolean fastDoubleClick1 = ButtonUtils.isFastDoubleClick();
                if (fastDoubleClick1) {
                    ToastUtils.showToast(getActivity(), "请不要重复点击");
                } else {
                    getDataFromServer("2", "");
                }

                break;

            case R.id.tv_view_rules:
                FragmentManager manager = getFragmentManager();


                RulesFragmentDialog winningFragmentDialog = RulesFragmentDialog.newInstance(1 + "");
                winningFragmentDialog.show(manager, RULE_DATE);
                break;
//            case R.id.et_coding_draw:
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                break;
        }
    }

    public static CashLuckyDrawFragmentDialog newInstance(String data) {

        Bundle args = new Bundle();
        args.putString(ARG_DATE_CASH, data);
        CashLuckyDrawFragmentDialog FragmentDialog = new CashLuckyDrawFragmentDialog();
        FragmentDialog.setArguments(args);
        return FragmentDialog;

    }

}
