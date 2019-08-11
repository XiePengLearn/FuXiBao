package com.shuangpin.rich.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.ZhaiDouBean;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

public class ZhaiDouListActivity extends BaseActivity {

    private static final String TAG = "ZhaiDouListActivity";
    private Context mContext;
    private RuntCustomProgressDialog mRuntDialog;

    private List<ZhaiDouBean> mDataList;
    @InjectView(R.id.tv_zhai_dou_number)
    TextView tv_zhai_dou_number;//摘豆总人数

    @InjectView(R.id.lv_list_zhai_dou)
    ListView mListView;//摘豆总人数
    private MyAdapter mAdapter;
    private String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhai_dou_list);

        ButterKnife.inject(this);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(ZhaiDouListActivity.this, R.color.theme_color);

        mContext = ZhaiDouListActivity.this;

        Intent intent = getIntent();
        mId = intent.getStringExtra("id");



        mDataList = new ArrayList<>();

        mAdapter = new MyAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        getPickABirdInfo(mId);
    }


    private void getPickABirdInfo(String shopId) {
        mRuntDialog = new RuntCustomProgressDialog(mContext);
        mRuntDialog.setMessage("数据加载中···");
        mRuntDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("shopId", shopId + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BEAN_LIST)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                mRuntDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_BEAN_LIST_result:" + responseString);
                mRuntDialog.dismiss();
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
                                JSONObject data = object.optJSONObject("data");
                                JSONArray jsonArray = data.optJSONArray("list");
                                String count = data.optString("count");


                                if (!TextUtils.isEmpty(count)) {
                                    tv_zhai_dou_number.setText("已有" + count + "人拣元宝");
                                }

                                /**
                                 * "user": "开小飞机的舒克",
                                 "money": "0.0567",
                                 "date": "2019-05-27 13:30:44"
                                 */
                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<ZhaiDouBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            ZhaiDouBean detailBean = new ZhaiDouBean();

                                            detailBean.setUser(jsonArray.optJSONObject(z).optString("user"));
                                            detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));
                                            detailBean.setDate(jsonArray.optJSONObject(z).optString("date"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);

                                        mAdapter.notifyDataSetChanged();

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


    private class MyAdapter extends BaseAdapter {

        private List<ZhaiDouBean> mDataList;

        MyAdapter(List<ZhaiDouBean> mDataList) {
            this.mDataList = mDataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.zhai_dou_item_detail, null);
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final String user = mDataList.get(position).getUser();
            final String money = mDataList.get(position).getMoney();
            final String data = mDataList.get(position).getDate();

            if (!TextUtils.isEmpty(user)) {
                holder.tv_zhai_dou_1.setText(user);
            }
            if (!TextUtils.isEmpty(money)) {
                holder.tv_zhai_dou_2.setText("获取" + money + "元");
            }

            if (!TextUtils.isEmpty(data)) {
                holder.tv_zhai_dou_3.setText(data);
            }


            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.tv_zhai_dou_1)
            TextView tv_zhai_dou_1;//
            @ViewInject(R.id.tv_zhai_dou_2)
            TextView tv_zhai_dou_2;//
            @ViewInject(R.id.tv_zhai_dou_3)
            TextView tv_zhai_dou_3;//

        }

    }
}
