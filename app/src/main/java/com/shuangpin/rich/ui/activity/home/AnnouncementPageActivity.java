package com.shuangpin.rich.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.AnnouncementPageBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.DateUtils;
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
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnnouncementPageActivity extends BaseActivity {

    private static final String TAG = "AnnouncementPageActivity";
    private Context mContext;
    private ListView mAnnouncementList;
    private List<AnnouncementPageBean> mDataList;
    private MyAdapter mAdapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_page);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(AnnouncementPageActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = AnnouncementPageActivity.this;
        mAnnouncementList = (ListView) findViewById(R.id.lv_list_announcement_detail);
        token = PrefUtils.readToken(mContext);


        mDataList = new ArrayList<>();
        mAdapter = new MyAdapter(mDataList);
        mAnnouncementList.setAdapter(mAdapter);

        getDataFromServer();

    }

    private void getDataFromServer() {
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_ANNOUNCEMENT_LIST)
                //                .post(requestBody)
                .addHeader("Authorization", token)
                .build();

        //
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_ANNOUNCEMENT_LIST_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {

                                JSONArray jsonArray = object.optJSONArray("data");

                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {

                                        List<AnnouncementPageBean> systemListDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            AnnouncementPageBean detailBean = new AnnouncementPageBean();
                                            detailBean.setId(jsonArray.optJSONObject(z).optString("id"));
                                            detailBean.setTitle(jsonArray.optJSONObject(z).optString("title"));
                                            detailBean.setRemark(jsonArray.optJSONObject(z).optString("remark"));
                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));
                                            systemListDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(systemListDetailBean);
                                        mAdapter.notifyDataSetChanged();

                                        LogUtilsxp.e2(TAG, "mDataList:" + mDataList.toString());
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


    private class MyAdapter extends BaseAdapter {

        private List<AnnouncementPageBean> mDataList;

        MyAdapter(List<AnnouncementPageBean> mDataList) {
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
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.point_item_detail_announcement, null);
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String id = mDataList.get(position).getId();
            String remark = mDataList.get(position).getRemark();
            String title = mDataList.get(position).getTitle();
            String createdAt = mDataList.get(position).getCreatedAt();

            if (!TextUtils.isEmpty(createdAt)) {

                long l = Long.parseLong(createdAt);
                String time = DateUtils.formatDate("yyyy-MM-dd HH:mm:ss", l);
                if (!TextUtils.isEmpty(time)) {
                    holder.tvDes.setText(time);
                }

            }
            holder.tvName.setText(title);


            holder.mrootAddress.setTag(position);
            holder.mrootAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());
                    String id1 = mDataList.get(index).getId();
                    Intent mIntent = new Intent(AnnouncementPageActivity.this, AnnouncementDetailsActivity.class);
                    mIntent.putExtra("title", "公告详情");
                    mIntent.putExtra("id", id1);
                    startActivity(mIntent);
                }
            });


            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.tv_name)
            TextView tvName;//地址名称
            @ViewInject(R.id.tv_des)
            TextView tvDes;// 具体地址

            @ViewInject(R.id.ll_root_address)
            RelativeLayout mrootAddress;// 跟布局

        }


    }
}
