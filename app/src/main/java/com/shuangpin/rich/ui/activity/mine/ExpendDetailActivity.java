package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeadGridView;
import com.shuangpin.R;
import com.shuangpin.rich.adapter.ExpendGridViewAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.ExpendDetailBean;
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

public class ExpendDetailActivity extends BaseActivity {


    private static final String TAG = "ExpendDetailActivity";
    private Context mContext = ExpendDetailActivity.this;
    @InjectView(R.id.rl_point_no_data)
    RelativeLayout rlPointNoData;//没有数据

    @InjectView(R.id.lv_list_point_detail)
    PullToRefreshHeadGridView mPullRefreshListView;//listview

    private List<ExpendDetailBean> mDataList;
    private RuntCustomProgressDialog runtDialog;
    private int num;

    private ExpendGridViewAdapter mGridViewAdapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expend_detail);
        StatusBarUtil.setStatusBar(this, R.color.theme_color);
        ButterKnife.inject(this);
        setTitleBar(SHOW_NOTHING);
        mDataList = new ArrayList<>();


        token = PrefUtils.readToken(mContext);


        num = 0;
        // 得到控件
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //        mPullRefreshListView.setAdapter(null);


        //        View viewHeader = View.inflate(mContext, R.layout.layout_home_header, null);
        //        allMoney = (TextView) viewHeader.findViewById(R.id.txt_shop_title_phone);
        //        inviteFans = (TextView) viewHeader.findViewById(R.id.txt_invite_fans);

        HeaderGridView lv = mPullRefreshListView.getRefreshableView();
        lv.setNumColumns(1);

        //        lv.addHeaderView(viewHeader);

        mGridViewAdapter = new ExpendGridViewAdapter(mContext, R.layout.expend_item_detail, mDataList, token);
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
                        applyDataFromServer(num);
                        mPullRefreshListView.onRefreshComplete();
                    }
                });


        applyDataFromServer(num);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;


        }
    }

    private void applyDataFromServer(int num) {

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
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_PAY_LOG)
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

                LogUtilsxp.e2(TAG, "URL_URL_PAY_LOG_result:" + responseString);
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

                                /**
                                 {
                                 "uid": "3",
                                 "change": "-10000.0000",
                                 "createdAt": "2019-05-27 07:27:44",
                                 "type": "发豆"
                                 }
                                 */
                                if (null == jsonArray) {

                                    if (mDataList.size() > 0) {
                                        rlPointNoData.setVisibility(View.GONE);
                                    } else {
                                        rlPointNoData.setVisibility(View.VISIBLE);
                                    }

                                } else {

                                    if (jsonArray.length() > 0) {
                                        rlPointNoData.setVisibility(View.GONE);
                                        List<ExpendDetailBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            ExpendDetailBean detailBean = new ExpendDetailBean();
                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));

                                            detailBean.setType(jsonArray.optJSONObject(z).optString("type"));
                                            detailBean.setChange(jsonArray.optJSONObject(z).optString("change"));
                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);
                                        mGridViewAdapter.notifyDataSetChanged();

                                    } else {
                                        if (mDataList.size() > 0) {
                                            rlPointNoData.setVisibility(View.GONE);
                                        } else {
                                            rlPointNoData.setVisibility(View.VISIBLE);
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
