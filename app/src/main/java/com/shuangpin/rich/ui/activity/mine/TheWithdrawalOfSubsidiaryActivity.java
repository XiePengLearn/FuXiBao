package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.Canfx;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.widget.RefreshListView;
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

public class TheWithdrawalOfSubsidiaryActivity extends BaseActivity {
    private static final String TAG = "TheWithdrawalOfSubsidiaryActivity";
    private Context mContext;
    private String token;
    private int num;
    private int type;
    private List<Canfx> mDataList;
    @InjectView(R.id.rv_refreshListView)
    RefreshListView mRefreshListView;//自定义list条目

    @InjectView(R.id.tv_ti_xian)
    TextView tvTiXian;//提现
    @InjectView(R.id.tv_sheng_yu)
    TextView tvShengYu;//剩余
    @InjectView(R.id.tv_all_ti_xian)
    TextView tvAllTiXian;//顶部选择按钮


    private RuntCustomProgressDialog dialog;
    private AllProductAdapter adapter;

    private View contentView3;
    private PopupWindow popupWindow3 = null;
    private ArrayAdapter adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_withdrawal_of_subsidiary);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(TheWithdrawalOfSubsidiaryActivity.this, R.color.theme_color_3ab1f5);
        ButterKnife.inject(this);
        mContext = TheWithdrawalOfSubsidiaryActivity.this;
        token = PrefUtils.readToken(mContext);

        String[] list3 = new String[]{"全部提现明细", "粉丝提现明细", "合伙人提现明细", "合伙人提现明细"};
        adapter3 = new ArrayAdapter<String>(mContext, R.layout.item_textview, list3);


        num = 1;
        type = 0;

        mDataList = new ArrayList<>();
        adapter = new AllProductAdapter(mDataList);
        mRefreshListView.setAdapter(adapter);
        mRefreshListView.setOnRefreshListener(new RefreshListView.RefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新
                num = 1;
                mDataList.clear();
                getAllProductData(num + "", type);
                mRefreshListView.onRefreshComplete(true);
            }

            @Override
            public void onLoadMore() {

                num++;
                dialog.show();
                getAllProductData(num + "", type);
                mRefreshListView.onRefreshComplete(false);
            }


        });
        getAllProductData(num + "", type);

        tvAllTiXian.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;

            case R.id.tv_all_ti_xian:
                classifyMethod();
                break;


        }

    }

    private void classifyMethod() {
        contentView3 = create3Classificationview();
        int width = mContext.getResources().getDisplayMetrics().heightPixels;
        int height = adapter3.getCount() * CommonUtil.dip2px(mContext, 45) + CommonUtil.dip2px(mContext, (adapter3.getCount() - 1));
        boolean focusable = true;
        popupWindow3 = new PopupWindow(contentView3, width, height, focusable);
        popupWindow3.setBackgroundDrawable(new ColorDrawable());
        popupWindow3.showAsDropDown(tvAllTiXian, 0, 0);
    }

    private View create3Classificationview() {
        ListView listView = (ListView) View.inflate(mContext, R.layout.listview, null);
        listView.setAdapter(adapter3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkPayment = (String) parent.getItemAtPosition(position);
                if (checkPayment.equals("全部提现明细")) {
                    num = 1;
                    //类型 0全部1粉丝提现2商户提现3代理提现
                    type = 0;
                    mDataList.clear();
                    getAllProductData(num + "", type);
                } else if (checkPayment.equals("粉丝提现明细")) {
                    num = 1;

                    type = 1;
                    mDataList.clear();
                    getAllProductData(num + "", type);
                } else if (checkPayment.equals("合伙人提现明细")) {
                    num = 1;

                    type = 3;
                    mDataList.clear();
                    getAllProductData(num + "", type);
                } else if (checkPayment.equals("合伙人提现明细")) {
                    num = 1;

                    type = 2;
                    mDataList.clear();
                    getAllProductData(num + "", type);
                }
                tvAllTiXian.setText(checkPayment);
                popupWindow3.dismiss();


            }
        });
        return listView;
    }

    public void getAllProductData(String number, final int type) {
        dialog = new RuntCustomProgressDialog(mContext);
        dialog.setMessage("正在加载中...");
        dialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("page", number + "")
//                .add("type", type + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_WITHDRAWAL_LIST)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_WITHDRAWAL_LIST:" + responseString);
                dialog.dismiss();
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * {
                         "code": 0,
                         "data": {
                         "list": [{
                         "status": "0",
                         "uid": "2",
                         "toMoney": "95.00",
                         "type": "1",
                         "createdAt": "2019-02-20 03:19:39",
                         "orderId": "215506471795c6cff8bc4440",
                         "status_msg": "审核中",
                         "type_msg": "粉丝提现"
                         }],
                         "already": null,
                         "balance": {
                         "money": "228.99",
                         "sys": "0.0000",
                         "bird": "1.8090",
                         "fans": "117.6857",
                         "proxy": "109.5000",
                         "shop": "0.0000"
                         }
                         },
                         "msg": "提现记录"
                         }
                         */
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                String already = data.optString("already");//提现
                                JSONObject balance = data.optJSONObject("balance");
                                String money = balance.optString("money");
                                String fans = balance.optString("fans");
                                String proxy = balance.optString("proxy");
                                String shop = balance.optString("shop");

                                if (!TextUtils.isEmpty(already)) {
                                    tvTiXian.setText("提现: ¥" + already);
                                }
                                //类型 0全部1粉丝提现2商户提现3代理提现
                                tvShengYu.setText("剩余: ¥" + money);

//                                if (type == 0) {
//                                    tvShengYu.setText("剩余: ¥" + money);
//                                } else if (type == 1) {
//                                    tvShengYu.setText("剩余: ¥" + fans);
//                                } else if (type == 2) {
//                                    tvShengYu.setText("剩余: ¥" + proxy);
//                                } else if (type == 3) {
//                                    tvShengYu.setText("剩余: ¥" + shop);
//                                }


                                JSONArray jsonArray = data.optJSONArray("list");
                                /**
                                 * status : 0
                                 * uid : 2
                                 * toMoney : 95.00
                                 * type : 1
                                 * createdAt : 2019-02-20 03:19:39
                                 * orderId : 215506471795c6cff8bc4440
                                 * status_msg : 审核中
                                 * type_msg : 粉丝提现
                                 */
                                if (null == jsonArray) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<Canfx> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            Canfx detailBean = new Canfx();

                                            detailBean.setStatus(jsonArray.optJSONObject(z).optString("status"));
                                            detailBean.setUid(jsonArray.optJSONObject(z).optString("uid"));
                                            detailBean.setToMoney(jsonArray.optJSONObject(z).optString("toMoney"));
                                            detailBean.setType(jsonArray.optJSONObject(z).optString("type"));
                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));
                                            detailBean.setOrderId(jsonArray.optJSONObject(z).optString("orderId"));
                                            detailBean.setStatus_msg(jsonArray.optJSONObject(z).optString("status_msg"));
                                            detailBean.setType_msg(jsonArray.optJSONObject(z).optString("type_msg"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);

                                        adapter.notifyDataSetChanged();

                                    }else {
                                        adapter.notifyDataSetChanged();
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

    /**
     * 商品适配器
     */
    private class AllProductAdapter extends BaseAdapter {
        private boolean flage = false;
        private List<Canfx> canfxList;

        public AllProductAdapter(List<Canfx> canfxList) {
            this.canfxList = canfxList;
        }

        @Override
        public int getCount() {
            return canfxList.size();
        }

        @Override
        public Object getItem(int position) {
            return canfxList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.xp_myfx_item, null);
                holder.name1 = (TextView) convertView.findViewById(R.id.tv_fans1);
                holder.name2 = (TextView) convertView.findViewById(R.id.tv_fans2);
                holder.name3 = (TextView) convertView.findViewById(R.id.tv_fans3);
                holder.name4 = (TextView) convertView.findViewById(R.id.tv_fans4);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Canfx item = canfxList.get(position);
            /**
             * status : 0
             * uid : 2
             * toMoney : 95.00
             * type : 1
             * createdAt : 2019-02-20 03:19:39
             * orderId : 215506471795c6cff8bc4440
             * status_msg : 审核中
             * type_msg : 粉丝提现
             */

            String status = item.getStatus();
            String uid = item.getUid();
            String toMoney = item.getToMoney();
            String type = item.getType();
            String createdAt = item.getCreatedAt();
            String orderId = item.getOrderId();
            String status_msg = item.getStatus_msg();
            String type_msg = item.getType_msg();


            if (!TextUtils.isEmpty(type_msg)) {
                holder.name1.setText(type_msg);
            } else {
                holder.name1.setText("");
            }
            if (!TextUtils.isEmpty(toMoney)) {
                holder.name2.setText(toMoney);
            } else {
                holder.name2.setText("");
            }
            if (!TextUtils.isEmpty(createdAt)) {
                holder.name3.setText(createdAt);
            } else {
                holder.name3.setText("");
            }
            if (!TextUtils.isEmpty(status_msg)) {
                holder.name4.setText(status_msg);
            } else {
                holder.name4.setText("");
            }

            return convertView;
        }

        private class ViewHolder {
            TextView name1;
            TextView name2;
            TextView name3;
            TextView name4;

        }
    }
}
