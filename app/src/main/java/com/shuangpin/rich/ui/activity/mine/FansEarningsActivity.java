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
import com.shuangpin.rich.adapter.FansGridViewAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.FansReturnsBean;
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

public class FansEarningsActivity extends BaseActivity {
    private static final String TAG = "PickABirdReturnsActivity";
    private Context mContext;
    private String token;
    @InjectView(R.id.pull_refresh_grid)
    PullToRefreshHeadGridView mPullRefreshListView;//放鸟喽按钮


    private FansGridViewAdapter mGridViewAdapter;


    private List<FansReturnsBean> mDataList;
    private int num;
    private RuntCustomProgressDialog runtDialog;
    private TextView allMoney;
    private TextView tvReturnedToFans;
    private TextView txtButEarnings;
    private TextView txtDoNotEarnings;
    private TextView giveToFriends;
    //    private TextView inviteFans;
    private TextView inviteFans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_abird_returns);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(FansEarningsActivity.this, R.color.theme_color);
        ButterKnife.inject(this);
        mContext = FansEarningsActivity.this;
        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();
        num = 0;
        // 得到控件
        //        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //        mPullRefreshListView.setAdapter(null);


        View viewHeader = View.inflate(mContext, R.layout.layout_fans_header, null);
        allMoney = (TextView) viewHeader.findViewById(R.id.txt_shop_title_phone);
        tvReturnedToFans = (TextView) viewHeader.findViewById(R.id.tv_returned_to_fans);
        giveToFriends = (TextView) viewHeader.findViewById(R.id.tv_give_to_friends);
        txtButEarnings = (TextView) viewHeader.findViewById(R.id.txt_but_earnings);
        txtDoNotEarnings = (TextView) viewHeader.findViewById(R.id.txt_do_not_earnings);

        inviteFans = (TextView) viewHeader.findViewById(R.id.txt_invite_fans);

        HeaderGridView lv = mPullRefreshListView.getRefreshableView();
        lv.setNumColumns(1);

        lv.addHeaderView(viewHeader);

        mGridViewAdapter = new FansGridViewAdapter(mContext, R.layout.item_gride_fans_layout, mDataList, token);
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

        //        inviteFans.setOnClickListener(this);
        giveToFriends.setOnClickListener(this);
        inviteFans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.tv_give_to_friends:
                Intent mIntent = new Intent(mContext, GiveAwayFansQuotaActivity.class);
                mIntent.putExtra("title", "赠送粉丝额度");
                startActivityForResult(mIntent, 1);

                break;
            case R.id.txt_invite_fans:
                Intent mIntent1 = new Intent(mContext, InviteFansActivity.class);
                mIntent1.putExtra("title", "邀请粉丝");
                startActivity(mIntent1);

                break;

        }
    }


    @Override
    //resultCode区分哪一个页面的传来的值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                String returnMsg = data.getStringExtra("returnMsg");
                if (!TextUtils.isEmpty(returnMsg)) {
                    if (returnMsg.equals("成功")) {
                        mDataList.clear();
                        getPickABirdInfo(0);
                    }
                }
            }

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
                .add("num", num + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_FANS_LIST)
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
                                 * "total": "0.07",
                                 "usable": 10,
                                 "disabled": 0,
                                 */
                                JSONArray jsonArray = object.optJSONArray("data");
                                String total = object.optString("total");
                                String usable = object.optString("usable");
                                String disabled = object.optString("disabled");
                                String fans = object.optString("fans");
                                if (!TextUtils.isEmpty(total)) {
                                    allMoney.setText(total);
                                }

                                if (!TextUtils.isEmpty(fans)) {
                                    tvReturnedToFans.setText("剩余可收益粉丝   " + fans);
                                }
                                if (!TextUtils.isEmpty(usable)) {
                                    txtButEarnings.setText(usable);
                                }
                                if (!TextUtils.isEmpty(disabled)) {
                                    txtDoNotEarnings.setText(disabled);
                                }
                                /**
                                 *   "uid": 3,
                                 "grade": 2,
                                 "nickname": "一闪一闪亮晶晶🌟",
                                 "money": 0
                                 "type": 0,
                                 */
                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<FansReturnsBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            FansReturnsBean detailBean = new FansReturnsBean();

                                            detailBean.setUid(jsonArray.optJSONObject(z).optString("uid"));
                                            detailBean.setGrade(jsonArray.optJSONObject(z).optString("grade"));
                                            detailBean.setNickname(jsonArray.optJSONObject(z).optString("nickname"));
                                            detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));
                                            detailBean.setType(jsonArray.optJSONObject(z).optString("type"));

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
