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
import com.shuangpin.rich.adapter.GridViewAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.PickABirdReturnsBean;
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

public class PickABirdReturnsActivity extends BaseActivity {

    private static final String TAG = "PickABirdReturnsActivity";
    private Context mContext;

    @InjectView(R.id.pull_refresh_grid)
    PullToRefreshHeadGridView mPullRefreshListView;//放鸟喽按钮


    private GridViewAdapter mGridViewAdapter;
    private String token;

    private List<PickABirdReturnsBean> mDataList;
    private int num;
    private RuntCustomProgressDialog runtDialog;
    private TextView allMoney;
    private TextView inviteFans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_abird_returns);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(PickABirdReturnsActivity.this, R.color.theme_color);
        ButterKnife.inject(this);
        mContext = PickABirdReturnsActivity.this;
        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();


        num = 0;
        // 得到控件
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //        mPullRefreshListView.setAdapter(null);


        View viewHeader = View.inflate(mContext, R.layout.layout_home_header, null);
        allMoney = (TextView) viewHeader.findViewById(R.id.txt_shop_title_phone);
        inviteFans = (TextView) viewHeader.findViewById(R.id.txt_invite_fans);

        HeaderGridView lv = mPullRefreshListView.getRefreshableView();
        lv.setNumColumns(1);

        lv.addHeaderView(viewHeader);

        mGridViewAdapter = new GridViewAdapter(mContext, R.layout.item_gride_goods_layout, mDataList, token);
        //        mAdapter = new ArrayAdapter<String>(mActivity, R.layout.grid_item,
        //                R.id.id_grid_item_text, mListItems);
        mPullRefreshListView.setAdapter(mGridViewAdapter);

        mPullRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {

                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                        num++;
                        getPickABirdInfo(num);
                        mPullRefreshListView.onRefreshComplete();
                    }
                });

        getPickABirdInfo(num);

        inviteFans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.txt_invite_fans:
                Intent mIntent = new Intent(mContext, InviteFansActivity.class);
                mIntent.putExtra("title", "邀请粉丝");
                startActivity(mIntent);

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
                .add("num",num+"")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_FANS)
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

                LogUtilsxp.e2(TAG, "URL_BIRD_MAP_result:" + responseString);
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
                                String total = object.optString("total");
                                if(!TextUtils.isEmpty(total)){
                                    allMoney.setText(total);
                                }
                                /**
                                 * {
                                 "createdAt": "2018-12-20 20:16:10",
                                 "money": 0.0496,
                                 "referrer": "",
                                 "topReferrer": "",
                                 "referrerMoney": 0,
                                 "topReferrerMoney": 0,
                                 "AllMoney": 0.0496
                                 }
                                 */
                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<PickABirdReturnsBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            PickABirdReturnsBean detailBean = new PickABirdReturnsBean();

                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));
                                            detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));
                                            detailBean.setReferrer(jsonArray.optJSONObject(z).optString("referrer"));
                                            detailBean.setTopReferrer(jsonArray.optJSONObject(z).optString("topReferrer"));
                                            detailBean.setReferrerMoney(jsonArray.optJSONObject(z).optString("referrerMoney"));
                                            detailBean.setTopReferrerMoney(jsonArray.optJSONObject(z).optString("topReferrerMoney"));
                                            detailBean.setAllMoney(jsonArray.optJSONObject(z).optString("AllMoney"));

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
