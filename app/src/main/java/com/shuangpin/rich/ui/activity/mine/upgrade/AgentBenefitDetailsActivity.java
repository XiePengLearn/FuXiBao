package com.shuangpin.rich.ui.activity.mine.upgrade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeadGridView;
import com.shuangpin.R;
import com.shuangpin.rich.adapter.AgentProxyGridViewAdapter;
import com.shuangpin.rich.adapter.AgentShopGridViewAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.AgentProxyListBean;
import com.shuangpin.rich.bean.AgentShopListBean;
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

public class AgentBenefitDetailsActivity extends BaseActivity {

    private static final String TAG = "AgentBenefitDetailsActivity";
    private Context mContext;

    @InjectView(R.id.pull_refresh_grid)
    PullToRefreshHeadGridView mPullRefreshListView;//放鸟喽按钮

    RadioGroup mRadioGroup;//

    LinearLayout llRootItem;//
    LinearLayout llRootItem2;//

    private AgentShopGridViewAdapter mShopGridViewAdapter;
    private AgentProxyGridViewAdapter mProxyGridViewAdapter;
    private String token;

    private List<AgentShopListBean> mShopList;
    private List<AgentProxyListBean> mProxyList;
    private int num;
    private RuntCustomProgressDialog runtDialog;
    private TextView allMoney;
    private TextView tvReturnedToFans;
    private TextView txtButEarnings;
    private TextView txtDoNotEarnings;
    private Button btnIncomeMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_benefit_details);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(AgentBenefitDetailsActivity.this, R.color.theme_color_3ab1f5);
        ButterKnife.inject(this);
        mContext = AgentBenefitDetailsActivity.this;

        token = PrefUtils.readToken(mContext);
        mShopList = new ArrayList<>();
        mProxyList = new ArrayList<>();
        num = 0;
        // 得到控件
        //        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        //        mPullRefreshListView.setAdapter(null);

        View viewHeader = View.inflate(mContext, R.layout.layout_agent_details_header, null);
        allMoney = (TextView) viewHeader.findViewById(R.id.txt_shop_title_phone);
        tvReturnedToFans = (TextView) viewHeader.findViewById(R.id.tv_returned_to_fans);
        txtButEarnings = (TextView) viewHeader.findViewById(R.id.txt_but_earnings);
        txtDoNotEarnings = (TextView) viewHeader.findViewById(R.id.txt_do_not_earnings);
        btnIncomeMoney = (Button) viewHeader.findViewById(R.id.btn_income_money);


        mRadioGroup = (RadioGroup) viewHeader.findViewById(R.id.rg_content_group);
        llRootItem = (LinearLayout) viewHeader.findViewById(R.id.ll_root_item);
        llRootItem2 = (LinearLayout) viewHeader.findViewById(R.id.ll_root_item2);

        HeaderGridView lv = mPullRefreshListView.getRefreshableView();
        lv.setNumColumns(1);

        lv.addHeaderView(viewHeader);

        mShopGridViewAdapter = new AgentShopGridViewAdapter(mContext, R.layout.item_gride_agent_shop_layout, mShopList, token);
        //        mAdapter = new ArrayAdapter<String>(mActivity, R.layout.grid_item,
        //                R.id.id_grid_item_text, mListItems);
        mPullRefreshListView.setAdapter(mShopGridViewAdapter);

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


        mRadioGroup.check(R.id.rb_home);// 设置默认选项为商户收益
        // 监听RadioGroup的选中事件,对页面进行切换
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        llRootItem.setVisibility(View.VISIBLE);
                        llRootItem2.setVisibility(View.GONE);

                        mShopGridViewAdapter = new AgentShopGridViewAdapter(mContext, R.layout.item_gride_agent_shop_layout, mShopList, token);
                        //        mAdapter = new ArrayAdapter<String>(mActivity, R.layout.grid_item,
                        //                R.id.id_grid_item_text, mListItems);
                        mPullRefreshListView.setAdapter(mShopGridViewAdapter);
                        mShopGridViewAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rb_news:

                        llRootItem.setVisibility(View.GONE);
                        llRootItem2.setVisibility(View.VISIBLE);

                        mProxyGridViewAdapter = new AgentProxyGridViewAdapter(mContext, R.layout.item_gride_agent_shop_layout, mProxyList, token);
                        //        mAdapter = new ArrayAdapter<String>(mActivity, R.layout.grid_item,
                        //                R.id.id_grid_item_text, mListItems);
                        mPullRefreshListView.setAdapter(mProxyGridViewAdapter);
                        mProxyGridViewAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });


        txtDoNotEarnings.setOnClickListener(this);
        btnIncomeMoney.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.txt_do_not_earnings:
                Intent mIntent = new Intent(mContext, AsAgentOfVActivity.class);
                mIntent.putExtra("title", "成为合伙人");
                startActivity(mIntent);

                break;
            case R.id.btn_income_money:
                ToastUtils.showToast(mContext,"未达到提现要求");

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
                .add("num", num + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_PROXY_LIST)
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

                LogUtilsxp.e2(TAG, "URL_PROXY_LIST_result:" + responseString);
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
                                 *  "allEarning": null,
                                 "earning": null,
                                 "nothing": "0",
                                 "shopList": [],
                                 "proxyList": []
                                 */
                                JSONObject data = object.optJSONObject("data");

                                JSONArray shopList = data.optJSONArray("shopList");
                                JSONArray proxyList = data.optJSONArray("proxyList");

                                String allEarning = data.optString("allEarning");
                                String earning = data.optString("earning");
                                String nothing = data.optString("nothing");


                                if (!TextUtils.isEmpty(earning) && !earning.equals("null")) {
                                    allMoney.setText(earning);
                                } else {
                                    allMoney.setText("0");
                                }

                                if (!TextUtils.isEmpty(allEarning) && !allEarning.equals("null")) {
                                    tvReturnedToFans.setText("累计收入   " + allEarning);
                                } else {
                                    tvReturnedToFans.setText("累计收入   0");
                                }

                                if (!TextUtils.isEmpty(nothing) && !nothing.equals("null")) {
                                    txtButEarnings.setText(nothing);
                                }

                                /**
                                 *  private String id;
                                 private String name;
                                 private String county;
                                 private String toReferrer;
                                 */


                                if (null == shopList) {

                                } else {
                                    if (shopList.length() > 0) {
                                        List<AgentShopListBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < shopList.length(); z++) {
                                            AgentShopListBean detailBean = new AgentShopListBean();

                                            detailBean.setId(shopList.optJSONObject(z).optString("id"));
                                            detailBean.setName(shopList.optJSONObject(z).optString("name"));
                                            detailBean.setCounty(shopList.optJSONObject(z).optString("county"));
                                            detailBean.setToReferrer(shopList.optJSONObject(z).optString("toReferrer"));

                                            listDetailBean.add(detailBean);
                                        }

                                        mShopList.addAll(listDetailBean);

                                        mShopGridViewAdapter.notifyDataSetChanged();

                                    }

                                    //

                                }

                                /**
                                 * private String id;
                                 private String name;
                                 private String mobile;
                                 private String toReferrer;
                                 */

                                if (null == proxyList) {

                                } else {
                                    if (proxyList.length() > 0) {
                                        List<AgentProxyListBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < proxyList.length(); z++) {
                                            AgentProxyListBean detailBean = new AgentProxyListBean();

                                            detailBean.setId(proxyList.optJSONObject(z).optString("id"));
                                            detailBean.setName(proxyList.optJSONObject(z).optString("name"));
                                            detailBean.setMobile(proxyList.optJSONObject(z).optString("mobile"));
                                            detailBean.setToReferrer(proxyList.optJSONObject(z).optString("toReferrer"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mProxyList.addAll(listDetailBean);
                                    }
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
