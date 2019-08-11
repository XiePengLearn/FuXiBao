package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeadGridView;
import com.shuangpin.R;
import com.shuangpin.rich.adapter.WinningRecordGridViewAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.WinningRecordBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

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


public class WinningRecordActivity extends BaseActivity {




    private static final String TAG = "WinningRecordActivity";
    private Context mContext;
    private String token;
    @InjectView(R.id.pull_refresh_grid)
    PullToRefreshHeadGridView mPullRefreshListView;//放鸟喽按钮


    private WinningRecordGridViewAdapter mGridViewAdapter;


    private List<WinningRecordBean> mDataList;
    private int num;
    private RuntCustomProgressDialog runtDialog;
    private TextView txtWinningMoney;
    private TextView tvSurplusMoney;
    private TextView tvWinningGiftNumber;
    private TextView tvSurplusWinningGiftNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_record);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(WinningRecordActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = WinningRecordActivity.this;
        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();
        num = 0;
        // 得到控件
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //        mPullRefreshListView.setAdapter(null);


        View viewHeader = View.inflate(mContext, R.layout.layout_winning_record_header, null);
        txtWinningMoney = (TextView) viewHeader.findViewById(R.id.txt_winning_money);//中奖总金额
        tvSurplusMoney = (TextView) viewHeader.findViewById(R.id.tv_surplus_money);//剩余金额
        tvWinningGiftNumber = (TextView) viewHeader.findViewById(R.id.tv_winning_record_gift_number);//总中奖 罐数
        tvSurplusWinningGiftNumber = (TextView) viewHeader.findViewById(R.id.tv_winning_record_gift_surplus_number);//剩余 罐数


        HeaderGridView lv = mPullRefreshListView.getRefreshableView();
        lv.setNumColumns(1);

        lv.addHeaderView(viewHeader);

        mGridViewAdapter = new WinningRecordGridViewAdapter(mContext, R.layout.item_gride_winning_layout, mDataList, token);
        //        mAdapter = new ArrayAdapter<String>(mActivity, R.layout.grid_item,
        //                R.id.id_grid_item_text, mListItems);
        mPullRefreshListView.setAdapter(mGridViewAdapter);

        mPullRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {

                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                        mPullRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                        num++;
                        getPickABirdInfo(num);
                        mPullRefreshListView.onRefreshComplete();
                    }
                });

        getPickABirdInfo(num);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;



        }
    }




    private void getPickABirdInfo(int num) {
        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("page", num + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_PRIZE_LOG)
                .addHeader("Authorization", token)
                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_FANS_LIST_result:" + responseString);
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
                                /**
                                 * 	"surplusPrize": "9.00",
                                 "surplusTin": 12,
                                 "allPrize": "24.00",
                                 "allTin": "12.00"
                                 */

                                JSONObject data = object.optJSONObject("data");

                                String allPrize = data.optString("allPrize");//所有 金额
                                String surplusPrize = data.optString("surplusPrize");//剩余金额

                                String allTin = data.optString("allTin");//
                                String surplusTin = data.optString("surplusTin");//剩余罐数



                                if(!TextUtils.isEmpty(allPrize)){
                                    txtWinningMoney.setText(allPrize);//中奖总金额
                                }

                                if(!TextUtils.isEmpty(surplusPrize)){
                                    tvSurplusMoney.setText(surplusPrize);//剩余金额
                                }

                                if(!TextUtils.isEmpty(allTin)){
                                    tvWinningGiftNumber.setText(allTin);//总中奖 罐数
                                }

                                if(!TextUtils.isEmpty(surplusTin)){
                                    tvSurplusWinningGiftNumber.setText(surplusTin);//剩余 罐数
                                }


                                JSONArray jsonArray = data.optJSONArray("list");
                                /**
                                 *   {
                                 "id": "13",
                                 "uid": "6",

                                 "createdAt": "2019-06-28 13:48:30",
                                 "prize": "1罐富硒宝",
                                 "source": "余额抽奖",
                                 "updatedAt": "1561700910"
                                 }
                                 */
                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<WinningRecordBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            WinningRecordBean detailBean = new WinningRecordBean();

                                            detailBean.setUid(jsonArray.optJSONObject(z).optString("uid"));
                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));
                                            detailBean.setPrize(jsonArray.optJSONObject(z).optString("prize"));
                                            detailBean.setSource(jsonArray.optJSONObject(z).optString("source"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);

                                        mGridViewAdapter.notifyDataSetChanged();

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
