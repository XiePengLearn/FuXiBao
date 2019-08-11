package com.shuangpin.rich.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.LocationAddressBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.widget.MySearchView;
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
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocateTheAddressActivity extends BaseActivity {
    private static final String TAG = "LocateTheAddressActivity";
    MapView mMapView;

    Button bt;
    Button button;
    Button buttons;
    ListView mLocationList;
    MySearchView mySearchView;

    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyTopLineLocationListener myListener = new MyTopLineLocationListener();
    private List<OverlayOptions> optionses;
    private RuntCustomProgressDialog runtDialog;
    private Context mContext;
    private String token;
    private double latitude;
    private double longitude;
    private double latitudeSearch = 0.0;
    private double longitudeSearch = 0.0;
    private List<LocationAddressBean> mDataList;
    private MyAdapter mAdapter;
    private LinearLayout ll_search_root;
    private TextView titleConfirm;
    private String returnAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_the_address);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(LocateTheAddressActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = LocateTheAddressActivity.this;
        token = PrefUtils.readToken(mContext);

        Intent mIntent = getIntent();
        //获取纬度信息
        latitude = mIntent.getDoubleExtra("latitude", 0.0);
        //获取经度信息
        longitude = mIntent.getDoubleExtra("longitude", 0.0);

        mMapView = (MapView) findViewById(R.id.bmapView);
        bt = (Button) findViewById(R.id.bt);
        button = (Button) findViewById(R.id.button);
        buttons = (Button) findViewById(R.id.buttons);
        mLocationList = (ListView) findViewById(R.id.lv_list_location_detail);
        mySearchView = (MySearchView) findViewById(R.id.search_order_layout);
        ll_search_root = (LinearLayout) findViewById(R.id.ll_search_root);
        titleConfirm = (TextView) findViewById(R.id.tv_title_confirm);
        ll_search_root.setVisibility(View.VISIBLE);
        titleConfirm.setVisibility(View.VISIBLE);


        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();

        optionses = new ArrayList<>();

        bt.setOnClickListener(this);
        button.setOnClickListener(this);
        buttons.setOnClickListener(this);
        titleConfirm.setOnClickListener(this);


        mDataList = new ArrayList<>();
        mAdapter = new MyAdapter(mDataList);
        mLocationList.setAdapter(mAdapter);


        mySearchView.etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    String string = mySearchView.etInput.getText().toString();
                    //自己的搜索逻辑实现
                    //                    if(){
                    //
                    //                    }
                    getSearchOrder(longitude + "", latitude + "", mySearchView.etInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        initMap();
        getLocateTheAddress();

    }

    private void getSearchOrder(String longitude, String latitude, String searchStr) {
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()

                .add("longitude", longitude)//经度
                .add("latitude", latitude)//纬度
                .add("search", searchStr)//纬度


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_GET_SEARCH)
                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_GETARROUND_result:" + responseString);

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
                                        List<LocationAddressBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            LocationAddressBean detailBean = new LocationAddressBean();
                                            detailBean.setAddr(jsonArray.optJSONObject(z).optString("addr"));
                                            if (z == 0) {
                                                detailBean.setChecked(true);
                                            } else {
                                                detailBean.setChecked(false);
                                            }

                                            detailBean.setName(jsonArray.optJSONObject(z).optString("name"));
                                            LocationAddressBean.PointBean pointBean = new LocationAddressBean.PointBean();
                                            pointBean.setX(jsonArray.optJSONObject(z).optJSONObject("point").optDouble("x"));
                                            //longitude 获取经度信息116.477316
                                            pointBean.setY(jsonArray.optJSONObject(z).optJSONObject("point").optDouble("y"));
                                            //latitude  维度 39.907877

                                            detailBean.setPoint(pointBean);

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.clear();
                                        mDataList.addAll(listDetailBean);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                }
                                //                                finish();

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


    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();

        //定义Maker坐标点


        //构建Marker图标

        //        BitmapDescriptor bitmap = BitmapDescriptorFactory
        //                .fromResource(R.drawable.icon_marka);

        View view = View.inflate(getApplicationContext(), R.layout.view_baidumap, null);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);//采用的是自定义覆盖物


        //地图加载完成回调

        BaiduMap.OnMapLoadedCallback mOnMapLoadedCallback = new BaiduMap.OnMapLoadedCallback() {
            /**
             * 地图加载完成回调函数
             */
            public void onMapLoaded() {
                runtDialog.dismiss();
            }
        };
        mBaiduMap.setOnMapLoadedCallback(mOnMapLoadedCallback);

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        //mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    private class MyTopLineLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                //把定位点再次显现出来
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                break;
            case R.id.button:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.buttons:
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.tv_title_confirm:

                Intent it = new Intent();
                if (latitudeSearch == 0.0) {
                    it.putExtra("latitude", latitude);
                } else {
                    it.putExtra("latitude", latitudeSearch);
                }
                if (longitudeSearch == 0.0) {
                    it.putExtra("longitude", longitude);
                } else {
                    it.putExtra("longitude", longitudeSearch);
                }
                if (!TextUtils.isEmpty(returnAddress)) {
                    it.putExtra("returnAddress", returnAddress);
                } else {
                    it.putExtra("returnAddress", "");
                }

                LogUtilsxp.e2(TAG, "latitude:" + latitudeSearch + "longitude:" + longitudeSearch + "returnAddress:" + returnAddress);
                setResult(Activity.RESULT_OK, it);
                finish();

                break;
        }
    }
    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    //        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
    //            Intent it = new Intent();
    //            if(latitudeSearch == 0.0){
    //                it.putExtra("latitude", latitude);
    //            }else {
    //                it.putExtra("latitude", latitudeSearch);
    //            }
    //            if(longitudeSearch == 0.0){
    //                it.putExtra("longitude", longitude);
    //            }else {
    //                it.putExtra("longitude", longitudeSearch);
    //            }
    //            if(!TextUtils.isEmpty(returnAddress)){
    //                it.putExtra("returnAddress", returnAddress);
    //            }else {
    //                it.putExtra("returnAddress", "");
    //            }
    //
    //            LogUtilsxp.e2(TAG,"latitude:"+latitudeSearch+"longitude:"+longitudeSearch+"returnAddress:"+returnAddress);
    //            setResult(Activity.RESULT_OK, it);
    //            finish();
    //            return true;
    //        }
    //        return super.onKeyDown(keyCode, event);
    //    }

    private void getLocateTheAddress() {
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()

                .add("longitude", longitude + "")//经度
                .add("latitude", latitude + "")//纬度


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_GETARROUND)
                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_GETARROUND_result:" + responseString);

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
                                        List<LocationAddressBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            LocationAddressBean detailBean = new LocationAddressBean();
                                            detailBean.setAddr(jsonArray.optJSONObject(z).optString("addr"));
                                            if (z == 0) {
                                                detailBean.setChecked(true);
                                            } else {
                                                detailBean.setChecked(false);
                                            }

                                            detailBean.setName(jsonArray.optJSONObject(z).optString("name"));
                                            LocationAddressBean.PointBean pointBean = new LocationAddressBean.PointBean();
                                            pointBean.setX(jsonArray.optJSONObject(z).optJSONObject("point").optDouble("x"));
                                            //longitude 获取经度信息116.477316
                                            pointBean.setY(jsonArray.optJSONObject(z).optJSONObject("point").optDouble("y"));
                                            //latitude  维度 39.907877

                                            detailBean.setPoint(pointBean);

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                }
                                //                                finish();

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

        private List<LocationAddressBean> mDataList;

        MyAdapter(List<LocationAddressBean> mDataList) {
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.point_item_detail, null);
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String address = mDataList.get(position).getAddr();
            String name = mDataList.get(position).getName();
            boolean isCheck = mDataList.get(position).isChecked();

            holder.mCheckBox.setEnabled(false);
            holder.addressName.setText(name);
            holder.specificAddress.setText(address);
            holder.mCheckBox.setChecked(true);


            holder.mCheckBox.setChecked(isCheck);

            if (isCheck) {
                if (mDataList.size() > 0) {
                    if (position == 0) {
                        returnAddress = mDataList.get(0).getAddr();
                        LocationAddressBean.PointBean point = mDataList.get(0).getPoint();
                        longitudeSearch = point.getX();//longitude 获取经度信息116.477316
                        latitudeSearch = point.getY();//latitude  维度 39.907877

                        latLng = new LatLng(latitudeSearch, longitudeSearch);
                        MyLocationData locData = new MyLocationData.Builder()
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(100)
                                .latitude(latitudeSearch)
                                .longitude(longitudeSearch).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);


                        LatLng ll = new LatLng(latitudeSearch, longitudeSearch);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(16.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


                        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                        mBaiduMap.animateMapStatus(mapStatusUpdate);
                    }

                }
            }

            holder.mrootAddress.setTag(position);
            holder.mrootAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());

                    returnAddress = mDataList.get(index).getAddr();
                    LogUtilsxp.e2(TAG, "index:" + index + "returnAddress:" + returnAddress);
                    LocationAddressBean.PointBean point = mDataList.get(index).getPoint();
                    longitudeSearch = point.getX();//longitude 获取经度信息116.477316
                    latitudeSearch = point.getY();//latitude  维度 39.907877

                    latLng = new LatLng(latitudeSearch, longitudeSearch);
                    MyLocationData locData = new MyLocationData.Builder()
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(100)
                            .latitude(latitudeSearch)
                            .longitude(longitudeSearch).build();
                    // 设置定位数据
                    mBaiduMap.setMyLocationData(locData);


                    LatLng ll = new LatLng(latitudeSearch, longitudeSearch);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(16.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.animateMapStatus(mapStatusUpdate);


                    for (int i = 0; i < mDataList.size(); i++) {
                        if (i == index) {
                            holder.mCheckBox.setChecked(true);
                            mDataList.get(i).setChecked(true);
                        } else {
                            holder.mCheckBox.setChecked(false);
                            mDataList.get(i).setChecked(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            });


            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.tv_address_name)
            TextView addressName;//地址名称
            @ViewInject(R.id.tv_specific_address)
            TextView specificAddress;// 具体地址
            @ViewInject(R.id.cb_apply_read)
            CheckBox mCheckBox;// 选中
            @ViewInject(R.id.ll_root_address)
            RelativeLayout mrootAddress;// 跟布局

        }


    }

}
