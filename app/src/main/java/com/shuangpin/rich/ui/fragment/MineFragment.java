package com.shuangpin.rich.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuangpin.R;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.home.HowToPlayActivity;
import com.shuangpin.rich.ui.activity.logo.BindPhoneActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.mine.FansEarningsActivity;
import com.shuangpin.rich.ui.activity.mine.FocusActivity;
import com.shuangpin.rich.ui.activity.mine.FriendsActivity;
import com.shuangpin.rich.ui.activity.mine.InviteFansActivity;
import com.shuangpin.rich.ui.activity.mine.PickABirdReturnsActivity;
import com.shuangpin.rich.ui.activity.mine.WalletActivity;
import com.shuangpin.rich.ui.activity.mine.business.MerchantDataDetailsActivity;
import com.shuangpin.rich.ui.activity.mine.business.SelfEmployedBusinessmanActivity;
import com.shuangpin.rich.ui.activity.mine.setting.SettingActivity;
import com.shuangpin.rich.ui.activity.mine.upgrade.AgentBenefitDetailsActivity;
import com.shuangpin.rich.ui.activity.mine.upgrade.AsAgentOfVActivity;
import com.shuangpin.rich.ui.activity.mine.upgrade.BuyFansActivity;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.ImageLoaderOptions;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static anetwork.channel.download.DownloadManager.TAG;

/**
 * 我的
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    ImageView mHeader;//头像
    TextView mineUserUid;//uid
    TextView mineUserMember;//会员名称
    ProgressBar mineProgress;//进度条
    TextView mineProgressPlan;//差多少进度升级大V
    TextView mineUpgradeMember;//升级
    TextView dynamicCondition;//动态
    TextView mAttention;//关注
    TextView mFans;//粉丝


    TextView mMessage;//消息
    RelativeLayout pickABirdReturns;//拣鸟收益
    RelativeLayout fansEarnings;//粉丝收益
    RelativeLayout escalationOfMicro;//升级大微
    RelativeLayout asAgent;//成为代理
    RelativeLayout selfEmployedBusinessman;//个体商户
    RelativeLayout mallOrders;//商城订单
    RelativeLayout myWallet;//我的钱包
    RelativeLayout onlineCustomerService;//在线客服
    RelativeLayout businessCooperation;//商务合作
    RelativeLayout mSetting;//设置
    private String token;
    private JSONObject shop;
    private RuntCustomProgressDialog runtDialog;
    private TextView tv_businessman;
    private RelativeLayout escalationOfV;
    private ImageView ivGongLv;
    private String grade;
    private String headImg;
    private String nickname;
    private String mIsShop;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, null);
        StatusBarUtil.setStatusBar(mMainActivity, R.color.theme_color_title_them);

        mHeader = (ImageView) view.findViewById(R.id.iv_header);//uid
        //uid
        ivGongLv = (ImageView) view.findViewById(R.id.iv_gong_lv);
        mineUserUid = (TextView) view.findViewById(R.id.txt_mine_user_uid);//uid
        mineUserMember = (TextView) view.findViewById(R.id.txt_mine_user_member);//会员名称
        mineProgress = (ProgressBar) view.findViewById(R.id.pb_mine_progress);//进度条
        mineProgressPlan = (TextView) view.findViewById(R.id.txt_mine_progress_plan);//差多少进度升级大V
        mineUpgradeMember = (TextView) view.findViewById(R.id.iv_mine_upgrade_member);//升级
        dynamicCondition = (TextView) view.findViewById(R.id.tv_dynamic_condition);//动态
        //动态
        tv_businessman = (TextView) view.findViewById(R.id.tv_businessman);
        mAttention = (TextView) view.findViewById(R.id.tv_attention);//关注
        mFans = (TextView) view.findViewById(R.id.tv_fans);//粉丝
        mMessage = (TextView) view.findViewById(R.id.tv_message);//消息
        pickABirdReturns = (RelativeLayout) view.findViewById(R.id.rl_pick_a_bird_returns);//拣鸟收益
        fansEarnings = (RelativeLayout) view.findViewById(R.id.rl_fans_earnings);//粉丝收益
        escalationOfMicro = (RelativeLayout) view.findViewById(R.id.ll_escalation_of_micro);//购买粉丝
        //升级大微
        escalationOfV = (RelativeLayout) view.findViewById(R.id.ll_escalation_of_v);
        asAgent = (RelativeLayout) view.findViewById(R.id.ll_as_agent);//成为代理
        selfEmployedBusinessman = (RelativeLayout) view.findViewById(R.id.ll_self_employed_businessman);//个体商户
        mallOrders = (RelativeLayout) view.findViewById(R.id.ll_mall_orders);//商城订单
        myWallet = (RelativeLayout) view.findViewById(R.id.ll_my_wallet);//我的钱包
        onlineCustomerService = (RelativeLayout) view.findViewById(R.id.ll_online_customer_service);//在线客服
        businessCooperation = (RelativeLayout) view.findViewById(R.id.ll_business_cooperation);//商务合作
        mSetting = (RelativeLayout) view.findViewById(R.id.ll_setting);//设置
        runtDialog = new RuntCustomProgressDialog(mMainActivity);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();

        token = PrefUtils.readToken(mMainActivity);

        mineProgress.setMax(100);
        mineProgress.setProgress(88);

        mineUpgradeMember.setOnClickListener(this);
        dynamicCondition.setOnClickListener(this);
        mAttention.setOnClickListener(this);
        mFans.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        pickABirdReturns.setOnClickListener(this);
        fansEarnings.setOnClickListener(this);
        escalationOfMicro.setOnClickListener(this);
        asAgent.setOnClickListener(this);
        selfEmployedBusinessman.setOnClickListener(this);
        mallOrders.setOnClickListener(this);
        myWallet.setOnClickListener(this);
        onlineCustomerService.setOnClickListener(this);
        businessCooperation.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        escalationOfV.setOnClickListener(this);
        ivGongLv.setOnClickListener(this);

        getUserDataFromServer();

        return view;
    }

    private void getUserDataFromServer() {
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()

                //				.add("longitude", longitude + "")//经度
                //				.add("latitude", latitude + "")//纬度


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_USER_INDEX)
                //				.post(requestBody)
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
                runtDialog.dismiss();
                LogUtilsxp.e2(TAG, "URL_USER_INDEX_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                nickname = data.optString("nickname");
                                String uid = data.optString("uid");
                                headImg = data.optString("headImg");
                                shop = data.optJSONObject("shop");
                                /**
                                 *   "sendCount": "3",
                                 "focusCount": "8",
                                 "friendsCount": 21
                                 */
                                String sendCount = data.optString("sendCount");
                                String focusCount = data.optString("focusCount");
                                String friendsCount = data.optString("friendsCount");
                                mIsShop = data.optString("isShop");


                                if (!TextUtils.isEmpty(sendCount)) {
                                    dynamicCondition.setText("动态\r\n" + sendCount);
                                }
                                if (!TextUtils.isEmpty(focusCount)) {
                                    mAttention.setText("关注\r\n" + focusCount);
                                }
                                if (!TextUtils.isEmpty(friendsCount)) {
                                    mFans.setText("好友\r\n" + friendsCount);
                                }

                                if (shop == null) {
                                    tv_businessman.setText("合伙人广告位申请");
                                } else {
                                    tv_businessman.setText("合伙人广告位管理");
                                }
                                grade = data.optString("grade");
                                if (!TextUtils.isEmpty(grade)) {


                                }


                                if (!TextUtils.isEmpty(uid)) {
                                    mineUserUid.setText("ID:" + uid);
                                }

                                if (!TextUtils.isEmpty(nickname)) {
                                    mineUserMember.setText("" + nickname);
                                }


                                ImageLoader.getInstance().displayImage(headImg, mHeader, ImageLoaderOptions.fadein_options);
                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mMainActivity, msg);
                                Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mMainActivity);
                                mMainActivity.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(mMainActivity, msg);
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
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            //升级
            case R.id.iv_mine_upgrade_member:
                ToastUtils.showToast(mMainActivity, "暂无数据");
                break;
            //动态
            case R.id.tv_dynamic_condition:
                String forTheBirdUrl22 = HttpsApi.HTML_DYNAMIC;
                mIntent = new Intent(mMainActivity, HtmlActivity.class);
                mIntent.putExtra("title", "动态");
                mIntent.putExtra("dynamic", "dynamic");
                mIntent.putExtra("url", forTheBirdUrl22);
                mMainActivity.startActivity(mIntent);
                break;
            //关注
            case R.id.tv_attention:
                mIntent = new Intent(mMainActivity, FocusActivity.class);
                mIntent.putExtra("title", "关注");
                mMainActivity.startActivity(mIntent);
                break;
            //粉丝
            case R.id.tv_fans:
                mIntent = new Intent(mMainActivity, FriendsActivity.class);
                mIntent.putExtra("title", "好友");
                mMainActivity.startActivity(mIntent);

                break;

            //攻略
            case R.id.iv_gong_lv:
                mIntent = new Intent(mMainActivity, HowToPlayActivity.class);
                mIntent.putExtra("title", "攻略");
                mMainActivity.startActivity(mIntent);

                break;
            //邀请粉丝
            case R.id.tv_message:
                mIntent = new Intent(mMainActivity, InviteFansActivity.class);
                //                mIntent = new Intent(mMainActivity, PaymentCodeActivity.class);
                mIntent.putExtra("title", "邀请粉丝");
                mMainActivity.startActivity(mIntent);
                break;
            //拣鸟收益
            case R.id.rl_pick_a_bird_returns:

                mIntent = new Intent(mMainActivity, PickABirdReturnsActivity.class);
                mIntent.putExtra("title", "摘元宝收益");
                mMainActivity.startActivity(mIntent);
                break;
            //粉丝收益
            case R.id.rl_fans_earnings:

                mIntent = new Intent(mMainActivity, FansEarningsActivity.class);
                mIntent.putExtra("title", "粉丝助力收益");
                mMainActivity.startActivity(mIntent);
                break;
            //购买粉丝
            case R.id.ll_escalation_of_micro:
                String isPhone = PrefUtils.readIsPhone(mMainActivity);//0没有绑定手机号  1已经绑定手机号
                if ("1".equals(isPhone)) {

                    String phone = PrefUtils.readPhone(mMainActivity);
                    LogUtilsxp.e2(TAG, "phone:" + phone);

                    mIntent = new Intent(mMainActivity, BuyFansActivity.class);
                    mIntent.putExtra("title", "购买粉丝");
                    mMainActivity.startActivity(mIntent);

                } else {

                    //没有绑定手机号,需要
                    mIntent = new Intent(mMainActivity, BindPhoneActivity.class);
                    mIntent.putExtra("title", "绑定手机号");

                    startActivity(mIntent);
                }


                break;
            //成为大微代理
            case R.id.ll_escalation_of_v:


                String isPhone1 = PrefUtils.readIsPhone(mMainActivity);//0没有绑定手机号  1已经绑定手机号
                if ("1".equals(isPhone1)) {

                    runtDialog.show();
                    //获取 当前的地理位置
                    CustomTrust customTrust = new CustomTrust(mMainActivity);
                    OkHttpClient okHttpClient = customTrust.client;
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_USER_INDEX)
                            .addHeader("Authorization", token)
                            .build();
                    okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                            runtDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseString = response.body().string();
                            runtDialog.dismiss();
                            LogUtilsxp.e2(TAG, "URL_USER_INDEX_result:" + responseString);

                            CommonUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject object = new JSONObject(responseString);
                                        int resultCode = object.optInt("code");
                                        String msg = object.optString("msg");
                                        if (resultCode == 0) {
                                            JSONObject data = object.optJSONObject("data");
                                            shop = data.optJSONObject("shop");

                                            if (shop == null) {
                                                tv_businessman.setText("合伙人广告位申请");
                                            } else {
                                                tv_businessman.setText("合伙人广告位管理");
                                            }
                                            grade = data.optString("grade");
                                            if (!TextUtils.isEmpty(grade)) {
                                                //grade 1普通  2大微  3商户  4 代理
                                                if ("4".equals(grade)) {
                                                    Intent mIntent = new Intent(mMainActivity, AgentBenefitDetailsActivity.class);
                                                    mIntent.putExtra("title", "合伙人收益");
                                                    mMainActivity.startActivity(mIntent);
                                                } else {
                                                    Intent mIntent = new Intent(mMainActivity, AsAgentOfVActivity.class);
                                                    mIntent.putExtra("title", "成为合伙人");
                                                    mMainActivity.startActivity(mIntent);
                                                }

                                            }


                                        } else if (resultCode == 403) {//token失效 重新登录
                                            ToastUtils.showToast(mMainActivity, msg);
                                            Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                                            mIntent.putExtra("title", "登录");
                                            PrefUtils.writeToken("", mMainActivity);
                                            mMainActivity.startActivity(mIntent);  //重新启动LoginActivity

                                        } else {
                                            ToastUtils.showToast(mMainActivity, msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    });


                } else {

                    //没有绑定手机号,需要
                    mIntent = new Intent(mMainActivity, BindPhoneActivity.class);
                    mIntent.putExtra("title", "绑定手机号");

                    startActivity(mIntent);
                }

                break;

            //个体商户
            case R.id.ll_self_employed_businessman:


                String isPhone2 = PrefUtils.readIsPhone(mMainActivity);//0没有绑定手机号  1已经绑定手机号
                if ("1".equals(isPhone2)) {

                    String phone = PrefUtils.readPhone(mMainActivity);
                    LogUtilsxp.e2(TAG, "phone:" + phone);

                    //判断是否有资格成为代理
                    if (!TextUtils.isEmpty(mIsShop)) {
                        if ("1".equals(mIsShop)) {

                            //1有资格
                            employedBusinessmanMethod();
                        } else {
                            //0没有资格
                            ToastUtils.showToast(mMainActivity, "申请成为运营商/合伙人，才能发广告");

                        }
                    }


                } else {

                    //没有绑定手机号,需要
                    mIntent = new Intent(mMainActivity, BindPhoneActivity.class);
                    mIntent.putExtra("title", "绑定手机号");

                    startActivity(mIntent);
                }


                break;
            //成为代理
            case R.id.ll_as_agent:
                //已经隐藏
                ToastUtils.showToast(mMainActivity, "成为合伙人");

                break;
            //商城订单
            case R.id.ll_mall_orders:
                ToastUtils.showToast(mMainActivity, "暂无数据");
                //                 mIntent = new Intent(mMainActivity, SelectTheScopeAndPayActivity.class);
                //                mIntent.putExtra("title", "支付");
                //                mIntent.putExtra("shopId", "1");
                //                startActivity(mIntent);
                break;
            //我的钱包
            case R.id.ll_my_wallet:
                mIntent = new Intent(mMainActivity, WalletActivity.class);
                mIntent.putExtra("title", "钱包");
                startActivity(mIntent);
                break;
            //在线客服
            case R.id.ll_online_customer_service:
                String forTheBirdUrl = HttpsApi.ONLINE_SERVICE;
                mIntent = new Intent(mMainActivity, HtmlActivity.class);
                mIntent.putExtra("title", "联系我们");
                mIntent.putExtra("url", forTheBirdUrl);
                mMainActivity.startActivity(mIntent);
                break;
            //商务合作
            case R.id.ll_business_cooperation:
                ToastUtils.showToast(mMainActivity, "商务合作");
                break;

            //设置
            case R.id.ll_setting:
                mIntent = new Intent(mMainActivity, SettingActivity.class);
                mIntent.putExtra("title", "设置");
                mIntent.putExtra("headImg", headImg);
                mIntent.putExtra("nickname", nickname);
                mMainActivity.startActivity(mIntent);
                break;
        }
    }

    private void employedBusinessmanMethod() {

        runtDialog.show();
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_USER_INDEX)
                .addHeader("Authorization", token)
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                runtDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                runtDialog.dismiss();
                LogUtilsxp.e2(TAG, "URL_USER_INDEX_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                String nickname = data.optString("nickname");
                                String uid = data.optString("uid");
                                String headImg = data.optString("headImg");
                                shop = data.optJSONObject("shop");
                                String shopUrl = data.optString("shopUrl");

                                if (shop == null) {
                                    tv_businessman.setText("合伙人广告位申请");
                                } else {
                                    tv_businessman.setText("合伙人广告位管理");
                                }


                                if (!TextUtils.isEmpty(uid)) {
                                    mineUserUid.setText("ID:" + uid);
                                }

                                if (!TextUtils.isEmpty(nickname)) {
                                    mineUserMember.setText("" + nickname);
                                }


                                if (shop == null) {

                                    Intent mIntent = new Intent(mMainActivity, SelfEmployedBusinessmanActivity.class);
                                    mIntent.putExtra("title", "销售商广告位申请");
                                    if (!TextUtils.isEmpty(shopUrl)) {
                                        mIntent.putExtra("shopUrl", shopUrl);
                                    } else {
                                        mIntent.putExtra("shopUrl", "");
                                    }
                                    mMainActivity.startActivity(mIntent);


                                } else {
                                    //                                    ToastUtils.showToast(mMainActivity, "您已经申请过个体商户");
                                    //                                    "id": "1",
                                    //                                            "shopName": "拣到",
                                    //                                            "name": "拣到",
                                    String id = shop.optString("id");
                                    String shopName = shop.optString("shopName");
                                    String name = shop.optString("name");

                                    Intent mIntent = new Intent(mMainActivity, MerchantDataDetailsActivity.class);
                                    mIntent.putExtra("title", "合伙人广告位管理");
                                    mIntent.putExtra("id", id);
                                    mIntent.putExtra("shopName", shopName);
                                    mIntent.putExtra("name", name);
                                    mMainActivity.startActivity(mIntent);
                                }


                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mMainActivity, msg);
                                Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mMainActivity);
                                mMainActivity.startActivity(mIntent);  //重新启动LoginActivity

                            } else {
                                ToastUtils.showToast(mMainActivity, msg);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
