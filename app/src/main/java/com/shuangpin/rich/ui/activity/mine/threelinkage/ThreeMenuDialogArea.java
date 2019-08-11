package com.shuangpin.rich.ui.activity.mine.threelinkage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shuangpin.R;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.JsonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by wbing on 2017/6/29 0029.
 */

public class ThreeMenuDialogArea extends SecondMenuDialogArea {
    private static final String TAG = "ThreeMenuDialogArea";
    private Context mContext;
    private Activity mActivity;
    private int mWidth;    //宽度
    private MyViewPager mViewPager;
    private View view1, view2, view3;
    private ListView mListView1, mListView2, mListView3;
    private MenuLocationAdapter mListView1Adapter;
    private MenuLocation2Adapter mListView2Adapter;
    private MenuLocation3Adapter mListView3Adapter;
    private List<View> views = new ArrayList<>();
    //获取类目接口
    private List<LocationBean.DataBean> listCatory1;
    private List<CityBean.DataBean> listCatory2;
    private List<CountryBean.DataBean> listCatory3;
    private List<CountryBean> listCatorytemp;
    private String id = "0";
    private MyPagerAdapter myPagerAdapter;
    private LocationBean.DataBean menuData;
    private CityBean.DataBean dictUnit;
    private CountryBean.DataBean menuFineData;
    private RuntCustomProgressDialog dialog;
    private MenuItemClickListener menuItemClickListener;   //接口，点击监听

    public static String province;
    public static String city;
    private String token;

    public ThreeMenuDialogArea(Context context, Activity mActivity) {
        super(context);
        mContext = context;
        mActivity = mActivity;
        mWidth = context.getResources().getDisplayMetrics().widthPixels;//获取屏幕参数
        mContentView = LayoutInflater.from(context).inflate(R.layout.three_menu_dialog, null);
        //初始化控件及对控件操作
        initViews();
        setTitle("请选择省市区");//设置title
    }

    private void initViews() {

        token = PrefUtils.readToken(mContext);
        //一级
        mViewPager = (MyViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view1 = inflater.inflate(R.layout.pager_number, null);
        view2 = inflater.inflate(R.layout.pager_number, null);
        view3 = inflater.inflate(R.layout.pager_number, null);
        mListView1 = (ListView) view1.findViewById(R.id.listview);
        mListView2 = (ListView) view2.findViewById(R.id.listview);
        mListView3 = (ListView) view3.findViewById(R.id.listview);
        listCatorytemp = new ArrayList<>();
        String LocationDate = PrefUtils.readCacheDate("ThreeMenuDialogArea", mContext);


        getHttpCatory(id);//第一组（类目）
    }

    private void getHttpCatory(String id) {


        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
        dialog.setMessage("数据正在提交中···");
        dialog.show();

        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("id", id)
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL+HttpsApi.GET_CITY)
                .post(requestBody)
                .addHeader("Authorization", token)
                .build();

//
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "GET_CITY_result:" + responseString);
                dialog.dismiss();

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
                                    ToastUtils.showToast(mContext, "出现未知错误");
                                } else {
                                    LocationBean locationEntity = JsonUtil.parseJsonToBean(responseString, LocationBean.class);
                                    //
                                    PrefUtils.writeCacheDate("ThreeMenuDialogArea", responseString, mContext);
                                    listCatory1 = locationEntity.getData();


                                    mListView1Adapter = new MenuLocationAdapter(mContext, listCatory1);
                                    mListView1Adapter.setSelectedBackgroundResource(R.drawable.select_white);//选中时
                                    mListView1Adapter.setHasDivider(false);
                                    mListView1Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                                    mListView1.setAdapter(mListView1Adapter);

                                    views.add(view1);
                                    views.add(view2);//加载了一二级菜单
                                    myPagerAdapter = new MyPagerAdapter(views);
                                    mViewPager.setAdapter(myPagerAdapter);

                                    mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if (mListView1Adapter != null)
                                                mListView1Adapter.setSelectedPos(position);
                                            if (mListView2Adapter != null)
                                                mListView2Adapter.setSelectedPos(-1);

                                            if (views.contains(view3)) {
                                                views.remove(view3);
                                                mViewPager.getAdapter().notifyDataSetChanged();
                                            }
                                            menuData = (LocationBean.DataBean) parent.getItemAtPosition(position);


                                            getHttpCatory2(menuData.getArea_id(), menuData.getArea_name());

                                        }
                                    });

                                    //二级
                                    mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                            if (mListView2Adapter != null) {
                                                mListView2Adapter.setSelectedPos(position);
                                                mListView2Adapter.setSelectedBackgroundResource(R.drawable.select_gray);//选中时
                                            }
                                            if (views.contains(view3)) {
                                                views.remove(view3);
                                                myPagerAdapter.notifyDataSetChanged();
                                            }
                                            dictUnit = (CityBean.DataBean) parent.getItemAtPosition(position);

                                            getHttpCatory3(dictUnit.getArea_id());

                                        }
                                    });
                                    mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            menuFineData = (CountryBean.DataBean) parent.getItemAtPosition(position);

                                            setDictItemClickListener(menuFineData, menuData, dictUnit);//选中点击的子菜单，去设置titleName
                                        }
                                    });

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

    private void getHttpCatory2(String id, String province) {



        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
        dialog.setMessage("数据正在提交中···");
        dialog.show();

        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("id", id)
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL+HttpsApi.GET_CITY)
                .post(requestBody)
                .addHeader("Authorization", token)
                .build();

        //
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "GET_CITY_result:" + responseString);
                dialog.dismiss();

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
                                    ToastUtils.showToast(mContext, "出现未知错误");
                                } else {
                                    CityBean cityBean = JsonUtil.parseJsonToBean(responseString, CityBean.class);
                                    listCatory2 = cityBean.getData();
                                    if (mListView2Adapter == null) {
                                        mListView2Adapter = new MenuLocation2Adapter(mContext, listCatory2);
                                        mListView2Adapter.setNormalBackgroundResource(R.color.white);
                                        mListView2.setAdapter(mListView2Adapter);
                                    } else {
                                        mListView2Adapter.setData(listCatory2);
                                        mListView2Adapter.notifyDataSetChanged();
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










//
//
//        RequestParams params = new RequestParams(HttpsApi.SERVER_URL + HttpsApi.GET_CITY);
//        params.addQueryStringParameter("token", PrefUtils.readToken(mContext));
//        params.addQueryStringParameter("id", id);
//        final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
//        dialog.setMessage("数据正在提交中···");
//        dialog.show();
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                dialog.dismiss();
//                LogUtilsxp.e2(TAG, "GET_CITY_result_two" + result);
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int resultCode = object.optInt("result");
//                    String msg = object.optString("msg");
//                    if (resultCode == 1) {
//                        JSONArray jsonArray = object.optJSONArray("data");
//                        if (null == jsonArray) {
//                            ToastUtils.showToast(mContext, "出现未知错误");
//                        } else {
//                            CityBean cityBean = JsonUtil.parseJsonToBean(result, CityBean.class);
//                            listCatory2 = cityBean.getData();
//                            if (mListView2Adapter == null) {
//                                mListView2Adapter = new MenuLocation2Adapter(mContext, listCatory2);
//                                mListView2Adapter.setNormalBackgroundResource(R.color.white);
//                                mListView2.setAdapter(mListView2Adapter);
//                            } else {
//                                mListView2Adapter.setData(listCatory2);
//                                mListView2Adapter.notifyDataSetChanged();
//                            }
//                        }
//                    } else if (resultCode == -9) {//token失效 重新登录
//                        ToastUtils.showToast(mContext, msg);
//                        Intent mIntent = new Intent(mContext, LoginActivity.class);
//                        mIntent.putExtra("title", "登录");
//                        PrefUtils.writePassword("", mContext);
//                        PrefUtils.writeToken("", mContext);
//                        mContext.startActivity(mIntent);  //重新启动LoginActivity
//
//                    } else {
//                        ToastUtils.showToast(mContext, msg);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                ToastUtils.showToast(mContext, ex.getMessage());
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                ToastUtils.showToast(mContext, "cancelled");
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });


    }

    private void getHttpCatory3(String id) {





        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
        dialog.setMessage("数据正在提交中···");
        dialog.show();

        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("id", id)
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL+HttpsApi.GET_CITY)
                .post(requestBody)
                .addHeader("Authorization", token)
                .build();

        //
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "GET_CITY_result:" + responseString);
                dialog.dismiss();

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
                                    ToastUtils.showToast(mContext, "出现未知错误");
                                } else {
                                    CountryBean cityBean = JsonUtil.parseJsonToBean(responseString, CountryBean.class);
                                    listCatory3 = cityBean.getData();
                                    if (mListView3Adapter == null) {
                                        mListView3Adapter = new MenuLocation3Adapter(mContext, listCatory3);
                                        mListView3Adapter.setHasDivider(false);
                                        mListView3Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                                        mListView3.setAdapter(mListView3Adapter);
                                    } else {
                                        mListView3Adapter.setData(listCatory3);
                                        mListView3Adapter.notifyDataSetChanged();
                                    }

                                    views.add(view3);
                                    //                    mViewPager.getAdapter().notifyDataSetChanged();
                                    myPagerAdapter.notifyDataSetChanged();
                                    mViewPager.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mViewPager.setCurrentItem(views.size() - 1);
                                        }
                                    }, 0);
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









//
//
//
//
//        RequestParams params = new RequestParams(HttpsApi.SERVER_URL + HttpsApi.GET_CITY);
//        params.addQueryStringParameter("token", PrefUtils.readToken(mContext));
//        params.addQueryStringParameter("id", id);
//        final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mContext);
//        dialog.setMessage("数据正在提交中···");
//        dialog.show();
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                dialog.dismiss();
//                LogUtilsxp.e2(TAG, "GET_CITY_result_three" + result);
//                try {
//                    JSONObject object = new JSONObject(result);
//                    int resultCode = object.optInt("result");
//                    String msg = object.optString("msg");
//                    if (resultCode == 1) {
//                        JSONArray jsonArray = object.optJSONArray("data");
//                        if (null == jsonArray) {
//                            ToastUtils.showToast(mContext, "出现未知错误");
//                        } else {
//                            CountryBean cityBean = JsonUtil.parseJsonToBean(result, CountryBean.class);
//                            listCatory3 = cityBean.getData();
//                            if (mListView3Adapter == null) {
//                                mListView3Adapter = new MenuLocation3Adapter(mContext, listCatory3);
//                                mListView3Adapter.setHasDivider(false);
//                                mListView3Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
//                                mListView3.setAdapter(mListView3Adapter);
//                            } else {
//                                mListView3Adapter.setData(listCatory3);
//                                mListView3Adapter.notifyDataSetChanged();
//                            }
//
//                            views.add(view3);
//                            //                    mViewPager.getAdapter().notifyDataSetChanged();
//                            myPagerAdapter.notifyDataSetChanged();
//                            mViewPager.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mViewPager.setCurrentItem(views.size() - 1);
//                                }
//                            }, 0);
//                        }
//                    } else if (resultCode == -9) {//token失效 重新登录
//                        ToastUtils.showToast(mContext, msg);
//                        Intent mIntent = new Intent(mContext, LoginActivity.class);
//                        mIntent.putExtra("title", "登录");
//                        PrefUtils.writePassword("", mContext);
//                        PrefUtils.writeToken("", mContext);
//                        mContext.startActivity(mIntent);  //重新启动LoginActivity
//
//                    } else {
//                        ToastUtils.showToast(mContext, msg);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                ToastUtils.showToast(mContext, ex.getMessage());
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                ToastUtils.showToast(mContext, "cancelled");
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });


    }

    //传递值
    //    private void setResultDate() {
    //        Intent intent = new Intent();
    //        String id1 = menuData.getId();
    //        String id2 = dictUnit.getId();
    //        String id3 = menuFineData.getId();
    //        String categoryName = menuFineData.getName();
    //        String categoryId = id1 + "," + id2 + "," + id3;
    //
    //        intent.putExtra("categoryId", categoryId);
    //        intent.putExtra("categoryName", categoryName);
    //
    //        setResult(0101, intent);
    //        finish();
    //    }
    private void setDictItemClickListener(CountryBean.DataBean menuData, LocationBean.DataBean locationBean, CityBean.DataBean cityBean) {
        if (menuItemClickListener != null) {
            menuItemClickListener.onMenuItemClick(menuData, locationBean, cityBean);
        }
        dismiss();
    }

    public final void setonItemClickListener(MenuItemClickListener listener) {
        menuItemClickListener = listener;

    }

    public interface MenuItemClickListener {
        public void onMenuItemClick(CountryBean.DataBean menuData, LocationBean.DataBean locationBean, CityBean.DataBean cityBean);
    }


}
