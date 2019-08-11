package com.shuangpin.rich.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.ForTheBirdMapBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.mine.ZhaiDouListActivity;
import com.shuangpin.rich.ui.html.ForTheBirdHtmlActivity;
import com.shuangpin.rich.ui.html.ShoppingForBirdActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.ImageLoaderOptions;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.shuangpin.R.drawable.shopping_advertising_board_img1;

public class TopLineFragmentActivity extends BaseActivity {


    MapView mMapView;

    Button bt;
    Button button;
    Button buttons;

    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyTopLineLocationListener myListener = new MyTopLineLocationListener();
    private List<com.baidu.mapapi.map.OverlayOptions> optionses;
    private RuntCustomProgressDialog runtDialog;
    private List<ForTheBirdMapBean> mDataList;
    private String TAG = "TopLineFragment";
    private MyAdapter mAdapter;
    private ListView mListView;
    private WebView mWebView;
    private TextView titleText;
    private Button open_method1;
    private RelativeLayout forTheBirdRoot;
    private String token;
    //全局定义
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 3000;  // 快速点击间隔
    private LinearLayout mAgencyMap;
    private LinearLayout mAgencyList;
    private View mLineMap;
    private View mLineList;
    private ListView mListMapDetail;
    private Context mMainActivity;
    private LinearLayout mLeft_btn;
    private Intent mIntent;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_line_fragment);
        mMainActivity = TopLineFragmentActivity.this;
        StatusBarUtil.setStatusBar(TopLineFragmentActivity.this, R.color.white);
        ButterKnife.inject(this);

        mMapView = (MapView) findViewById(R.id.bmapView);
        bt = (Button) findViewById(R.id.bt);
        button = (Button) findViewById(R.id.button);
        buttons = (Button) findViewById(R.id.buttons);
        mListMapDetail = (ListView) findViewById(R.id.lv_list_map_detail);

        mAgencyMap = (LinearLayout) findViewById(R.id.ll_agency_map);
        mLeft_btn = (LinearLayout) findViewById(R.id.left_btn);


        mAgencyList = (LinearLayout) findViewById(R.id.ll_agency_list);
        mLineMap = findViewById(R.id.line_map);
        mLineList = findViewById(R.id.line_list);


        mAgencyMap.setOnClickListener(this);
        mAgencyList.setOnClickListener(this);


        mDataList = new ArrayList<>();
        runtDialog = new RuntCustomProgressDialog(mMainActivity);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        token = PrefUtils.readToken(mMainActivity);
        optionses = new ArrayList<>();

        bt.setOnClickListener(this);
        button.setOnClickListener(this);
        buttons.setOnClickListener(this);
        mLeft_btn.setOnClickListener(this);


        initMap();


        mAdapter = new MyAdapter(mDataList);
        mListMapDetail.setAdapter(mAdapter);

        mIntent = getIntent();
        mType = mIntent.getStringExtra("type");
        if(!TextUtils.isEmpty(mType)){
            if(mType.equals("map")){
                //地图
                mLineList.setVisibility(View.INVISIBLE);
                mLineMap.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                mListMapDetail.setVisibility(View.GONE);
            }else {
                //列表
                mLineMap.setVisibility(View.INVISIBLE);
                mLineList.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.GONE);

                mListMapDetail.setVisibility(View.VISIBLE);
            }
        }


    }

    private void getBirdFromServer(String latitude, String longitude) {
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mMainActivity);
        RequestBody requestBody = new FormBody.Builder()
                .add("longitude", longitude + "")
                .add("latitude", latitude + "")
                .add("clear", "1")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_MAP)
                .addHeader("Authorization", token)
                .post(requestBody)
                .build();

        LogUtilsxp.e2(TAG, "token:" + token);
        LogUtilsxp.e2(TAG, "longitude:" + longitude);
        LogUtilsxp.e2(TAG, "latitude:" + latitude);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                runtDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                if(mType.equals("list")){
                    runtDialog.dismiss();
                }


                LogUtilsxp.e2(TAG, "URL_BIRD_MAP_result:" + responseString);

                //                CommonUtil.runOnUIThread(new Runnable() {
                //                    @Override
                //                    public void run() {

                //                       isOut 1  展示
                try {
                    JSONObject object = new JSONObject(responseString);
                    int resultCode = object.optInt("code");
                    String msg = object.optString("msg");
                    if (resultCode == 0) {
                        JSONArray jsonArray = object.optJSONArray("data");
                        /**
                         * * id : 1
                         * uid : 1
                         * shopName : 春伟酱香饼
                         * header : http://keran.oss-cn-beijing.aliyuncs.com/shop/1/jpg/1558933575573.jpg
                         * longitude : 116.4823861365665
                         * latitude : 39.91413007233052
                         * address : 大望路地铁
                         * info : 大家好 欢迎品尝
                         * isOut : 0
                         * userHead : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIeiaJP2AFVEOeva2QxlMbBUdrAHhhsJDJcEiaWs34J7ttMl2xPMtM4YMRLia6Oxvnwb1FWKeX8fmRLA/132
                         * advertising : {"id":"1","imgsrc":"http://keran.oss-cn-beijing.aliyuncs.com/AD/20190524145239_80242.jpg","url":"www.baidu.com"}
                         * balance : 218.74
                         * userComment : [{"headImg":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIeiaJP2AFVEOeva2QxlMbBUdrAHhhsJDJcEiaWs34J7ttMl2xPMtM4YMRLia6Oxvnwb1FWKeX8fmRLA/132"}]
                         */
                        if (null == jsonArray) {

                        } else {
                            if (jsonArray.length() > 0) {
                                List<ForTheBirdMapBean> listDetailBean = new ArrayList<>();
                                for (int z = 0; z < jsonArray.length(); z++) {
                                    ForTheBirdMapBean detailBean = new ForTheBirdMapBean();

                                    detailBean.setId(jsonArray.optJSONObject(z).optString("id"));
                                    detailBean.setShopName(jsonArray.optJSONObject(z).optString("shopName"));
                                    detailBean.setHeader(jsonArray.optJSONObject(z).optString("header"));
                                    detailBean.setLongitude(jsonArray.optJSONObject(z).optString("longitude"));
                                    detailBean.setLatitude(jsonArray.optJSONObject(z).optString("latitude"));
                                    detailBean.setIsOut(jsonArray.optJSONObject(z).optString("isOut"));
                                    detailBean.setAddress(jsonArray.optJSONObject(z).optString("address"));

                                    detailBean.setUserHead(jsonArray.optJSONObject(z).optString("userHead"));
                                    detailBean.setInfo(jsonArray.optJSONObject(z).optString("info"));
                                    detailBean.setBalance(jsonArray.optJSONObject(z).optString("balance"));
                                    detailBean.setBean(jsonArray.optJSONObject(z).optString("gold"));
                                    ForTheBirdMapBean.AdvertisingBean advertisingBean = new ForTheBirdMapBean.AdvertisingBean();
                                    JSONObject advertising = jsonArray.optJSONObject(z).optJSONObject("advertising");

                                    if (null != advertising) {
                                        advertisingBean.setId(advertising.optString("id"));
                                        advertisingBean.setImgsrc(advertising.optString("imgsrc"));
                                        advertisingBean.setUrl(advertising.optString("url"));
                                    }

                                    detailBean.setAdvertising(advertisingBean);

                                    JSONArray jsonArrayUserComment = jsonArray.optJSONObject(z).optJSONArray("userComment");

                                    List<ForTheBirdMapBean.UserCommentBean> listUserComment = new ArrayList<>();
                                    if (null == jsonArrayUserComment) {

                                    } else {
                                        if (jsonArrayUserComment.length() > 0) {

                                            for (int y = 0; y < jsonArrayUserComment.length(); y++) {
                                                ForTheBirdMapBean.UserCommentBean commentBean = new ForTheBirdMapBean.UserCommentBean();

                                                commentBean.setHeadImg(jsonArrayUserComment.optJSONObject(y).optString("headImg"));
                                                listUserComment.add(commentBean);
                                            }

                                        }

                                    }

                                    detailBean.setUserComment(listUserComment);


                                    listDetailBean.add(detailBean);
                                }
                                mDataList.addAll(listDetailBean);

                                List<OverlayOptions> Point = new ArrayList<OverlayOptions>();
                                for (int i = 0; i < mDataList.size(); i++) {
                                    String latitude1 = mDataList.get(i).getLatitude();
                                    String longitude1 = mDataList.get(i).getLongitude();
                                    String id = mDataList.get(i).getId();
                                    String shopName = mDataList.get(i).getShopName();
                                    String isOut = mDataList.get(i).getIsOut();
                                    String header = mDataList.get(i).getHeader();
                                    final LatLng pointLat = new LatLng(Double.parseDouble(latitude1), Double.parseDouble(longitude1));

                                    //构建Marker图标
                                    final View viewMarker = View.inflate(mMainActivity.getApplicationContext(), R.layout.view_baidumap, null);
                                    ImageView haveBird = (ImageView) viewMarker.findViewById(R.id.iv_have_bird);
                                    ImageView haveHi = (ImageView) viewMarker.findViewById(R.id.iv_have_hi);
                                    TextView title = (TextView) viewMarker.findViewById(R.id.shopping_advertising_title);
                                    final ImageView shopImg = (ImageView) viewMarker.findViewById(R.id.shopping_advertising_img);

                                    /**
                                     * DiskCacheStrategy.NONE 什么都不缓存
                                     DiskCacheStrategy.SOURCE 只缓存全尺寸图
                                     DiskCacheStrategy.RESULT 只缓存最终的加载图
                                     DiskCacheStrategy.ALL 缓存所有版本图（默认行为）
                                     */


                                    if (!TextUtils.isEmpty(shopName)) {
                                        if (shopName.length() >= 6) {
                                            title.setText(shopName);
                                        } else {
                                            title.setText(shopName);
                                        }

                                    }

                                    if (isOut.equals("1")) {
                                        //有鸟
                                        haveBird.setVisibility(View.VISIBLE);

                                    } else {
                                        haveBird.setVisibility(View.INVISIBLE);
                                    }

                                    //                                            LogUtilsxp.e(TAG, "header:" + header);

                                    final int finalI = i;
                                    ImageLoader.getInstance().loadImage(header, new ImageSize(40, 40), ImageLoaderOptions.round_options, new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {

                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            shopImg.setImageDrawable(getResources().getDrawable(shopping_advertising_board_img1));
                                            Bitmap viewBitmap = getViewBitmap(viewMarker);
                                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(viewBitmap);
                                            OverlayOptions option1 = new MarkerOptions()
                                                    .position(pointLat)
                                                    .icon(bitmapDescriptor)
                                                    .zIndex(finalI);
                                            mBaiduMap.addOverlay(option1);
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                                            //                                                    LogUtilsxp.e(TAG, "加载成功");
                                            final Bitmap bitmap = circleBitmap(loadedImage);


                                            CommonUtil.runOnUIThread(new Runnable() {
                                                @Override

                                                public void run() {


                                                    //                                                            Bitmap bitmap = circleBitmap(loadedImage);
                                                    shopImg.setImageBitmap(bitmap);
                                                    Bitmap viewBitmap = getViewBitmap(viewMarker);
                                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(viewBitmap);
                                                    OverlayOptions option1 = new MarkerOptions()
                                                            .position(pointLat)
                                                            .icon(bitmapDescriptor)
                                                            .zIndex(finalI);
                                                    mBaiduMap.addOverlay(option1);

                                                }
                                            });


                                        }

                                        @Override
                                        public void onLoadingCancelled(String imageUri, View view) {

                                        }
                                    });
                                }
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
                //                    }
                //                });
            }
        });


    }

    public static Bitmap circleBitmap(Bitmap source) {

        int width = source.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        //设置图片相交情况下的处理方式
        //setXfermode：设置当绘制的图像出现相交情况时候的处理方式的,它包含的常用模式有：
        //PorterDuff.Mode.SRC_IN 取两层图像交集部分,只显示上层图像
        //PorterDuff.Mode.DST_IN 取两层图像交集部分,只显示下层图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(source, 0, 0, paint);

        return bitmap;
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();

//        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                //                ToastUtils.showToast(mMainActivity, "LongClick");
//                View view = View.inflate(mMainActivity, R.layout.dialog_goods_sku_layout, null);
//                mListView = (ListView) view.findViewById(R.id.lv_list_dialog_detail);
//                LinearLayout leftClose = (LinearLayout) view.findViewById(R.id.left_btn);
//
//
//                mAdapter = new MyAdapter(mDataList);
//                mListView.setAdapter(mAdapter);
//
//                mListView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (!mListView.canScrollVertically(-1)) {      //canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
//                            mListView.requestDisallowInterceptTouchEvent(false);
//                        } else {
//                            mListView.requestDisallowInterceptTouchEvent(true);
//                        }
//                        return false;
//                    }
//                });
//
//
//                mAdapter.notifyDataSetChanged();
//
//
//                bottomSheetDialog = new BottomSheetDialog(mMainActivity);
//                bottomSheetDialog.setCancelable(false);
//                bottomSheetDialog.setCanceledOnTouchOutside(false);
//                bottomSheetDialog.setContentView(view);
//                bottomSheetDialog.show();
//                leftClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (mWebView != null) {
//                            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
//                            mWebView.clearHistory();
//
//                            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
//                            mWebView.destroy();
//                            mWebView = null;
//                        }
//
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//            }
//        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    ToastUtils.showToast(mMainActivity, "请不要频繁点击");

                } else {


                    // TODO Auto-generated method stub
                    //可以通过arg0.getZIndex()判断Marker
                    int zIndex = arg0.getZIndex();
                    /**
                     * id : 1
                     * shopName : ，
                     * header : http://pickup.76UZBCHr5h/gzgr46M55/AXLXS2wYz/WL+3aw=
                     * longitude : 116.475585963856
                     * latitude : 39.9087271426496
                     * isOut : 0
                     * isIn : 0
                     */

                    final String id = mDataList.get(zIndex).getId();
                    String shopName = mDataList.get(zIndex).getShopName();
                    String header = mDataList.get(zIndex).getHeader();
                    String longitude = mDataList.get(zIndex).getLongitude();
                    String latitude = mDataList.get(zIndex).getLatitude();
                    String isOut = mDataList.get(zIndex).getIsOut();

                    String shoppingForTheBirdUrl = HttpsApi.HTML_SHOP_RED_PACKET;
                    Intent mIntent = new Intent(mMainActivity, ShoppingForBirdActivity.class);
                    mIntent.putExtra("title", " ");
                    mIntent.putExtra("url", shoppingForTheBirdUrl);
                    mIntent.putExtra("shopName", shopName);
                    mIntent.putExtra("longitude", longitude);
                    mIntent.putExtra("latitude", latitude);
                    mIntent.putExtra("isOut", isOut);
                    mIntent.putExtra("id", id);

                    mIntent.putExtra("tagType", "tagType");

                    mMainActivity.startActivity(mIntent);


                    //                    //                @InjectView(R.id.wv_browser)
                    //                    //                WebView mWebView;//浏览器
                    //                    //
                    //                    //                @InjectView(R.id.title)
                    //                    //                TextView title;//
                    //                    //                @InjectView(R.id.open_method)
                    //                    //                Button open_method1;//
                    //                    //
                    //                    //                @InjectView(R.id.ll_for_the_bird_root)
                    //                    //                RelativeLayout forTheBirdRoot;//
                    //
                    //                    View view = View.inflate(mMainActivity, R.layout.dialog_detail_business, null);
                    //                    LinearLayout leftClose = (LinearLayout) view.findViewById(R.id.left_btn);
                    //
                    //                    final ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);
                    //                    mWebView = (WebView) view.findViewById(R.id.wv_browser);
                    //                    titleText = (TextView) view.findViewById(R.id.title);
                    //                    open_method1 = (Button) view.findViewById(R.id.open_method);
                    //                    forTheBirdRoot = (RelativeLayout) view.findViewById(R.id.ll_for_the_bird_root);
                    //
                    //
                    //                    //请求鸟开始
                    //
                    //                    CustomTrust customTrust = new CustomTrust(mMainActivity);
                    //                    OkHttpClient okHttpClient = customTrust.client;
                    //
                    //      /*  $time = $data['time'];
                    //        $sign = $data['sign'];
                    //        $str = $uid.'_'.$data['shopId'].'_'.$time.'_'.$key.'_'.$data['city'];
                    //        $str = MD5(MD5($str));*/
                    //
                    //                    String key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
                    //
                    //                    //请求鸟的金额
                    //                    String uid = PrefUtils.readUid(mMainActivity);
                    //                    String city = PrefUtils.readCity(mMainActivity);
                    //                    //获取当前的秒值
                    //                    long time = System.currentTimeMillis() / 1000;
                    //                    String encryptionString = uid + "_" + id + "_" + time + "_" + key + "_" + city;
                    //                    String encStr1 = CommonUtil.md5(encryptionString);
                    //                    String encStr = CommonUtil.md5(encStr1);
                    //
                    //
                    //                    //                String token = PrefUtils.readToken(mMainActivity);
                    //                    RequestBody requestBody = new FormBody.Builder()
                    //                            .add("shopId", id + "")//店id
                    //                            .add("sign", encStr + "")//签名
                    //                            .add("city", city + "")//城市编号
                    //                            .add("time", time + "")//当前时间
                    //                            .build();
                    //                    Request request = new Request.Builder()
                    //                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND_BIRD)
                    //                            .addHeader("Authorization", token)
                    //                            .post(requestBody)
                    //                            .build();
                    //
                    //                    LogUtilsxp.e2(TAG, "token:" + token);
                    //                    LogUtilsxp.e2(TAG, "shopId:" + id);
                    //                    LogUtilsxp.e2(TAG, "sign:" + encStr);
                    //                    LogUtilsxp.e2(TAG, "token:" + token);
                    //                    LogUtilsxp.e2(TAG, "city:" + city);
                    //                    LogUtilsxp.e2(TAG, "time:" + time + "");
                    //                    LogUtilsxp.e2(TAG, "url:" + HttpsApi.SERVER_URL + HttpsApi.URL_SEND_BIRD);
                    //
                    //                    okHttpClient.newCall(request).enqueue(new Callback() {
                    //                        @Override
                    //                        public void onFailure(Call call, IOException e) {
                    //                            LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                    //                        }
                    //
                    //                        @Override
                    //                        public void onResponse(Call call, Response response) throws IOException {
                    //                            final String responseData = response.body().string();
                    //                            CommonUtil.runOnUIThread(new Runnable() {
                    //                                @Override
                    //                                public void run() {
                    //                                    LogUtilsxp.e2(TAG, "URL_SEND_BIRD_message:" + responseData);
                    //                                    try {
                    //                                        String data = responseData;
                    //                                        JSONObject object = new JSONObject(data);
                    //                                        int code = object.optInt("code");
                    //                                        String message = object.optString("msg");
                    //
                    //                                        //code=403  重新登录
                    //                                        if (code == 0) {
                    //                                            JSONObject dataObj = object.optJSONObject("data");
                    //                                            String isSend = dataObj.optString("isSend");
                    //                                            LogUtilsxp.e2(TAG, "isSend:" + isSend);
                    //                                            //                        "isSend":0 //不送  1 送
                    //
                    //                                            if (!TextUtils.isEmpty(isSend)) {
                    //                                                if (isSend.equals("1")) {
                    //                                                    ToastUtils.showToast(mMainActivity, message);
                    //                                                    LogUtilsxp.e2(TAG, "isSend2:" + isSend);
                    //
                    //                                                    WindowManager wm1 = mMainActivity.getWindowManager();
                    //                                                    int width1 = wm1.getDefaultDisplay().getWidth();
                    //                                                    int widthHalf = wm1.getDefaultDisplay().getWidth() / 2;
                    //                                                    int height1 = wm1.getDefaultDisplay().getHeight() / 2;
                    //
                    //
                    //                                                    Random generate = new Random();
                    //                                                    int nextIntY = generate.nextInt(height1 / 2) + height1;
                    //                                                    int nextIntX = generate.nextInt(width1 - 100);
                    //                                                    final ImageView point = new ImageView(mMainActivity);
                    //                                                    point.setScaleType(ImageView.ScaleType.FIT_XY);
                    //                                                    RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(DensityUtils.dp2px(mMainActivity, 34),
                    //                                                            DensityUtils.dp2px(mMainActivity, 34));
                    //                                                    point.setLayoutParams(paramsImg);
                    //                                                    if (nextIntX - widthHalf > 0) {
                    //                                                        //获取绘制出来鸟的坐标,和屏幕的一半做比较,大于零鸟朝向左边
                    //                                                        point.setBackgroundResource(R.drawable.a_bean_flying);
                    //                                                    } else {
                    //                                                        //获取绘制出来鸟的坐标,和屏幕的一半做比较,小于零鸟朝向右边
                    //                                                        point.setBackgroundResource(R.drawable.a_bean_flying);
                    //                                                    }
                    //
                    //                                                    final RelativeLayout myRelativeLayout = new RelativeLayout(mMainActivity);
                    //                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(mMainActivity, 36), DensityUtils.dp2px(mMainActivity, 36));
                    //                                                    params.setMargins(nextIntX, nextIntY + DensityUtils.dp2px(mMainActivity, 5), 0, 0);
                    //                                                    myRelativeLayout.setLayoutParams(params);
                    //                                                    myRelativeLayout.addView(point);
                    //                                                    final AnimationDrawable mAnimation = (AnimationDrawable) point.getBackground();
                    //                                                    // 为了防止在onCreate方法中只显示第一帧的解决方案之一
                    //                                                    point.post(new Runnable() {
                    //                                                        @Override
                    //                                                        public void run() {
                    //                                                            mAnimation.start();
                    //                                                        }
                    //                                                    });
                    //
                    //                                                    AnimationSet animationSet = new AnimationSet(true);//共用动画补间
                    //                                                    animationSet.setDuration(4000);
                    //                                                    AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.9f);
                    //                                                    alphaAnimation.setDuration(4000);
                    //                                                    TranslateAnimation translateAnimation = null;
                    //                                                    if (nextIntX - widthHalf > 0) {
                    //                                                        translateAnimation = new TranslateAnimation(0, nextIntX - widthHalf * 2, 0, -nextIntY);
                    //                                                    } else {
                    //                                                        translateAnimation = new TranslateAnimation(0, widthHalf - nextIntX, 0, -nextIntY);
                    //                                                    }
                    //
                    //                                                    translateAnimation.setDuration(4000);
                    //                                                    animationSet.addAnimation(translateAnimation);
                    //                                                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                    //
                    //                                                        @Override
                    //                                                        public void onAnimationStart(Animation animation) {
                    //
                    //                                                        }
                    //
                    //                                                        @Override
                    //                                                        public void onAnimationRepeat(Animation animation) {
                    //                                                        }
                    //
                    //                                                        // 动画结束的回调
                    //                                                        @Override
                    //                                                        public void onAnimationEnd(Animation animation) {
                    //                                                            myRelativeLayout.setVisibility(View.GONE);
                    //
                    //
                    //                                                        }
                    //                                                    });
                    //                                                    myRelativeLayout.startAnimation(animationSet);
                    //                                                    forTheBirdRoot.addView(myRelativeLayout);
                    //
                    //
                    //                                                }
                    //                                            }
                    //
                    //
                    //                                        } else if (code == 403) {
                    //                                            ToastUtils.showToast(mMainActivity, message);
                    //                                            Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                    //                                            mIntent.putExtra("title", "登录");
                    //                                            PrefUtils.writeToken("", mMainActivity);
                    //                                            mMainActivity.startActivity(mIntent);  //重新启动LoginActivity
                    //                                            mMainActivity.finish();
                    //
                    //                                        } else {
                    //                                            ToastUtils.showToast(mMainActivity, message);
                    //
                    //
                    //                                        }
                    //                                    } catch (Exception e) {
                    //
                    //
                    //                                    }
                    //                                }
                    //                            });
                    //                        }
                    //                    });
                    //
                    //
                    //                    String shoppingForTheBirdUrl = HttpsApi.HTML_SHOP_RED_PACKET;
                    //                    WebSettings settings = mWebView.getSettings();
                    //
                    //                    final String ua = settings.getUserAgentString();
                    //                    settings.setUserAgentString(ua + "JianDao");
                    //                    mWebView.addJavascriptInterface(new AndroidAndJSInterface(), "Android");
                    //                    settings.setJavaScriptEnabled(true);
                    //                    settings.setUseWideViewPort(true);//设定支持viewport  //设置webview推荐使用的窗口
                    //                    settings.setLoadWithOverviewMode(true);           //设置webview加载的页面的模式
                    //                    settings.setBuiltInZoomControls(false);           // 隐藏显示缩放按钮
                    //                    settings.setSupportZoom(false);                    //设定不支持缩放
                    //                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //                        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    //                    }
                    //                    settings.setDisplayZoomControls(false);           //隐藏webview缩放按钮
                    //                    mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
                    //                    mWebView.setInitialScale(100);
                    //                    settings.setCacheMode(WebSettings.LOAD_DEFAULT);//使用默认缓存
                    //                    settings.setDomStorageEnabled(true);//DOM储存API
                    //                    //        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);//默认缩放模式 设置以上无效 使用
                    //
                    //                    final Message msg = new Message();
                    //                    mWebView.loadUrl(shoppingForTheBirdUrl);
                    //                    mWebView.setWebViewClient(new WebViewClient() {
                    //
                    //                        // 网页跳转
                    //                        @Override
                    //                        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                    //
                    //                            LogUtilsxp.e2(TAG, "ForTheBirdurl:" + url);
                    //
                    //                            view.loadUrl(url);
                    //                            return true;
                    //                        }
                    //
                    //                        // 网页加载结束
                    //                        @Override
                    //                        public void onPageFinished(WebView view, String url) {
                    //                            super.onPageFinished(view, url);
                    //                            //                getUserRedMsg: function(id, token)
                    //                            //                getShopMsg: function(token, id, shopId)
                    //                            //                mWebView.loadUrl("javascript:getinfo('"+id+"','"+token+"')");
                    //                            mWebView.loadUrl("javascript:getShopMsg('" + token + "','" + "" + "','" + id + "')");
                    //
                    //
                    //                        }
                    //
                    //                        @Override
                    //                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    //                            Toast.makeText(mMainActivity, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    //                        }
                    //                    });
                    //                    //        browser.setWebViewClient(new MyWebViewClient());
                    //                    mWebView.setWebChromeClient(new WebChromeClient() {
                    //
                    //                        @Override
                    //                        public void onProgressChanged(WebView view, int newProgress) {
                    //                            if (newProgress == 100) {
                    //                                mProgressBar.setVisibility(View.GONE);
                    //                            } else {
                    //                                if (View.GONE == mProgressBar.getVisibility()) {
                    //                                    mProgressBar.setVisibility(View.GONE);
                    //                                }
                    //                                mProgressBar.setProgress(newProgress);
                    //                            }
                    //                            super.onProgressChanged(view, newProgress);
                    //                        }
                    //
                    //
                    //                        @Override
                    //                        public void onReceivedTitle(WebView view, String title) {
                    //                            super.onReceivedTitle(view, title);
                    //                            //                Message msg = new Message();
                    //                            //                msg.what = 1;
                    //                            //                msg.obj = title;
                    //                            //                mHandler.sendMessage(msg);
                    //                            if (!TextUtils.isEmpty(title)) {
                    //                                titleText.setText(title);
                    //                            }
                    //
                    //                        }
                    //
                    //                    });
                    //                    mWebView.setOnClickListener(new View.OnClickListener() {
                    //                        @Override
                    //                        public void onClick(View v) {
                    //
                    //                        }
                    //                    });
                    //
                    //                    mWebView.getSettings().setSavePassword(false);
                    //
                    //
                    //                    bottomSheetDialog = new BottomSheetDialog(mMainActivity);
                    //                    bottomSheetDialog.setCancelable(false);
                    //                    bottomSheetDialog.setCanceledOnTouchOutside(false);
                    //                    bottomSheetDialog.setContentView(view);
                    //                    bottomSheetDialog.show();
                    //                    leftClose.setOnClickListener(new View.OnClickListener() {
                    //                        @Override
                    //                        public void onClick(View v) {
                    //                            if (mWebView != null) {
                    //                                mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                    //                                mWebView.clearHistory();
                    //
                    //                                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                    //                                mWebView.destroy();
                    //                                mWebView = null;
                    //                            }
                    //                            bottomSheetDialog.dismiss();
                    //                        }
                    //                    });
                    //                    //
                    //
                }
                lastClickTime = System.currentTimeMillis();


                return true;
            }
        });
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
        mLocationClient = new LocationClient(mMainActivity.getApplicationContext());     //声明LocationClient类
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

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();


            String loStr = PrefUtils.readLongitude(mMainActivity);
            String laStr = PrefUtils.readLatitude(mMainActivity);
            if (!loStr.equals("0.0") && !laStr.equals("0.0")) {

                getBirdFromServer(laStr, loStr);
                LogUtilsxp.e2(TAG, "加载首页定位:" + "加载首页定位");
            } else {
                getBirdFromServer(latitude + "", longitude + "");
            }

            latLng = new LatLng(latitude, longitude);
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
            //            if (isFirstLoc) {
            //                isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(12.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            //                if (location.getLocType() == BDLocation.TypeGpsLocation) {
            //                    // GPS定位结果
            //                    Toast.makeText(mMainActivity, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            //                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            //                    // 网络定位结果
            //                    Toast.makeText(mMainActivity, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            //
            //                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            //                    // 离线定位结果
            //                    Toast.makeText(mMainActivity, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            //
            //                } else if (location.getLocType() == BDLocation.TypeServerError) {
            //                    Toast.makeText(mMainActivity, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
            //                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            //                    Toast.makeText(mMainActivity, "网络错误，请检查", Toast.LENGTH_SHORT).show();
            //                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            //                    Toast.makeText(mMainActivity, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
            //                }
            //            }
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

            case R.id.ll_agency_map:
                mLineList.setVisibility(View.INVISIBLE);
                mLineMap.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                mListMapDetail.setVisibility(View.GONE);
                break;
            case R.id.ll_agency_list:
                mLineMap.setVisibility(View.INVISIBLE);
                mLineList.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.GONE);

                mListMapDetail.setVisibility(View.VISIBLE);
                break;

            case R.id.left_btn:
                finish();
                break;
        }
    }




    public Bitmap getViewBitmap(View addViewContent) {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return bitmap;
    }

    class MyAdapter extends BaseAdapter {

        private List<ForTheBirdMapBean> mDataList;

        MyAdapter(List<ForTheBirdMapBean> mDataList) {
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
                convertView = LayoutInflater.from(mMainActivity).inflate(R.layout.dialog_map_detail, null);


                holder.tvShopHead = (ImageView) convertView.findViewById(R.id.tv_shop_head);
                holder.tvAddressName = (TextView) convertView.findViewById(R.id.tv_address_name);
                holder.ivHaveBird = (ImageView) convertView.findViewById(R.id.iv_have_bird);


                holder.address_des = (TextView) convertView.findViewById(R.id.tv_address_des);
                holder.agency_picture = (ImageView) convertView.findViewById(R.id.iv_agency_picture);
                holder.tv_address_number = (TextView) convertView.findViewById(R.id.tv_address_number);


                holder.llRootItem = (LinearLayout) convertView.findViewById(R.id.ll_root_address_bird);

                holder.iv_adv_picture = (ImageView) convertView.findViewById(R.id.iv_adv_picture);
                holder.ll_adv_root = (LinearLayout) convertView.findViewById(R.id.ll_adv_root);


                holder.rl_item_bottom_tab = (RelativeLayout) convertView.findViewById(R.id.rl_item_bottom_tab);


                holder.tv_money_shop = (TextView) convertView.findViewById(R.id.tv_money_shop);
                holder.iv_user_photo_1 = (ImageView) convertView.findViewById(R.id.iv_user_photo_1);
                holder.iv_user_photo_2 = (ImageView) convertView.findViewById(R.id.iv_user_photo_2);
                holder.iv_user_photo_3 = (ImageView) convertView.findViewById(R.id.iv_user_photo_3);
                holder.iv_user_photo_4 = (ImageView) convertView.findViewById(R.id.iv_user_photo_4);
                holder.iv_user_photo_5 = (ImageView) convertView.findViewById(R.id.iv_user_photo_5);
                holder.iv_user_photo_6 = (ImageView) convertView.findViewById(R.id.iv_user_photo_6);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            /**
             * id : 1
             * shopName : ，
             * header : http://pickup.76UZBCHr5h/gzgr46M55/AXLXS2wYz/WL+3aw=
             * longitude : 116.475585963856
             * latitude : 39.9087271426496
             * isOut : 0
             * isIn : 0
             */
            String id = mDataList.get(position).getId();
            String shopName = mDataList.get(position).getShopName();
            String header = mDataList.get(position).getHeader();
            String longitude = mDataList.get(position).getLongitude();
            String latitude = mDataList.get(position).getLatitude();
            String isOut = mDataList.get(position).getIsOut();
            String address = mDataList.get(position).getAddress();

            String info = mDataList.get(position).getInfo();
            String userHead = mDataList.get(position).getUserHead();
            String balance = mDataList.get(position).getBalance();


            ForTheBirdMapBean.AdvertisingBean advertising = mDataList.get(position).getAdvertising();
            String advertisingId = advertising.getId();
            String advertisingImgsrc = advertising.getImgsrc();
            String advertisingUrl = advertising.getUrl();


            List<ForTheBirdMapBean.UserCommentBean> userComment = mDataList.get(position).getUserComment();
            LogUtilsxp.e2(TAG,"size:----"+userComment.size());
            if(userComment.size()>0){

                int size = userComment.size();
                if(size == 1){

                    String headImg1 = userComment.get(0).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg1, holder.iv_user_photo_5, ImageLoaderOptions.fadein_options);


                    holder.iv_user_photo_1.setVisibility(View.GONE);
                    holder.iv_user_photo_2.setVisibility(View.GONE);
                    holder.iv_user_photo_3.setVisibility(View.GONE);
                    holder.iv_user_photo_4.setVisibility(View.GONE);
                    holder.iv_user_photo_5.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_6.setVisibility(View.GONE);
                }

                if(size == 2){
                    String headImg1 = userComment.get(0).getHeadImg();
                    LogUtilsxp.e2(TAG,"headImg1:----"+headImg1);
                    ImageLoader.getInstance().displayImage(headImg1, holder.iv_user_photo_5, ImageLoaderOptions.fadein_options);

                    String headImg2 = userComment.get(1).getHeadImg();
                    LogUtilsxp.e2(TAG,"headImg2:----"+headImg2);
                    ImageLoader.getInstance().displayImage(headImg2, holder.iv_user_photo_4, ImageLoaderOptions.fadein_options);
                    holder.iv_user_photo_1.setVisibility(View.GONE);
                    holder.iv_user_photo_2.setVisibility(View.GONE);
                    holder.iv_user_photo_3.setVisibility(View.GONE);
                    holder.iv_user_photo_4.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_5.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_6.setVisibility(View.GONE);
                }

                if(size == 3){
                    String headImg1 = userComment.get(0).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg1, holder.iv_user_photo_5, ImageLoaderOptions.fadein_options);

                    String headImg2 = userComment.get(1).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg2, holder.iv_user_photo_4, ImageLoaderOptions.fadein_options);

                    String headImg3 = userComment.get(2).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg3, holder.iv_user_photo_3, ImageLoaderOptions.fadein_options);

                    holder.iv_user_photo_1.setVisibility(View.GONE);
                    holder.iv_user_photo_2.setVisibility(View.GONE);
                    holder.iv_user_photo_3.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_4.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_5.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_6.setVisibility(View.GONE);
                }

                if(size == 4){

                    String headImg1 = userComment.get(0).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg1, holder.iv_user_photo_5, ImageLoaderOptions.fadein_options);

                    String headImg2 = userComment.get(1).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg2, holder.iv_user_photo_4, ImageLoaderOptions.fadein_options);

                    String headImg3 = userComment.get(2).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg3, holder.iv_user_photo_3, ImageLoaderOptions.fadein_options);

                    String headImg4 = userComment.get(3).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg4, holder.iv_user_photo_2, ImageLoaderOptions.fadein_options);

                    holder.iv_user_photo_1.setVisibility(View.GONE);
                    holder.iv_user_photo_2.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_3.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_4.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_5.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_6.setVisibility(View.GONE);
                }

                if(size >= 5){
                    String headImg1 = userComment.get(0).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg1, holder.iv_user_photo_5, ImageLoaderOptions.fadein_options);

                    String headImg2 = userComment.get(1).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg2, holder.iv_user_photo_4, ImageLoaderOptions.fadein_options);

                    String headImg3 = userComment.get(2).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg3, holder.iv_user_photo_3, ImageLoaderOptions.fadein_options);

                    String headImg4 = userComment.get(3).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg4, holder.iv_user_photo_2, ImageLoaderOptions.fadein_options);

                    String headImg5 = userComment.get(4).getHeadImg();
                    ImageLoader.getInstance().displayImage(headImg5, holder.iv_user_photo_1, ImageLoaderOptions.fadein_options);
                    holder.iv_user_photo_1.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_2.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_3.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_4.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_5.setVisibility(View.VISIBLE);
                    holder.iv_user_photo_6.setVisibility(View.VISIBLE);
                }

            }else {
                holder.iv_user_photo_1.setVisibility(View.GONE);
                holder.iv_user_photo_2.setVisibility(View.GONE);
                holder.iv_user_photo_3.setVisibility(View.GONE);
                holder.iv_user_photo_4.setVisibility(View.GONE);
                holder.iv_user_photo_5.setVisibility(View.GONE);
                holder.iv_user_photo_6.setVisibility(View.GONE);
            }



            if (!TextUtils.isEmpty(advertisingImgsrc)) {
                holder.ll_adv_root.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(advertisingImgsrc, holder.iv_adv_picture, ImageLoaderOptions.fadein_options_10);
            } else {
                holder.ll_adv_root.setVisibility(View.GONE);
            }

            if (isOut.equals("1")) {
                //有鸟
                holder.ivHaveBird.setVisibility(View.VISIBLE);

            } else {
                holder.ivHaveBird.setVisibility(View.INVISIBLE);
            }


            ImageLoader.getInstance().displayImage(userHead, holder.tvShopHead, ImageLoaderOptions.fadein_options);
            ImageLoader.getInstance().displayImage(header, holder.agency_picture, ImageLoaderOptions.round_options);

            if (!TextUtils.isEmpty(shopName)) {
                holder.tvAddressName.setText(shopName);
            }
            if (!TextUtils.isEmpty(info)) {
                holder.address_des.setText(info);
            }

            if (!TextUtils.isEmpty(address)) {
                holder.tv_address_number.setText(address);
            }

            if (!TextUtils.isEmpty(balance)) {
                holder.tv_money_shop.setText("¥" + balance);
            }


            holder.llRootItem.setTag(position);
            holder.llRootItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());

                    String id = mDataList.get(index).getId();
                    String shopName = mDataList.get(index).getShopName();
                    String header = mDataList.get(index).getHeader();
                    String longitude = mDataList.get(index).getLongitude();
                    String latitude = mDataList.get(index).getLatitude();
                    String isOut = mDataList.get(index).getIsOut();


                    String shoppingForTheBirdUrl = HttpsApi.HTML_SHOP_RED_PACKET;
                    Intent mIntent = new Intent(mMainActivity, ShoppingForBirdActivity.class);
                    mIntent.putExtra("title", " ");
                    mIntent.putExtra("url", shoppingForTheBirdUrl);
                    mIntent.putExtra("shopName", shopName);
                    mIntent.putExtra("longitude", longitude);
                    mIntent.putExtra("latitude", latitude);
                    mIntent.putExtra("isOut", isOut);
                    mIntent.putExtra("id", id);

                    mIntent.putExtra("tagType", "tagType");

                    mMainActivity.startActivity(mIntent);
                }
            });


            holder.ll_adv_root.setTag(position);
            holder.ll_adv_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());
                    String url = mDataList.get(index).getAdvertising().getUrl();
                    String bean = mDataList.get(index).getBean();
                    String aid = mDataList.get(index).getId();
                    String forTheBirdUrl = url;


                    Intent mIntent = new Intent(mMainActivity, ForTheBirdHtmlActivity.class);
                    mIntent.putExtra("title", " ");
                    mIntent.putExtra("url", forTheBirdUrl);
                    mIntent.putExtra("gold", bean);
                    mIntent.putExtra("aid", aid);
                    mMainActivity.startActivity(mIntent);
                }
            });

            holder.rl_item_bottom_tab .setTag(position);
            holder.rl_item_bottom_tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(v.getTag().toString());
                    String id = mDataList.get(index).getId();
                    Intent mIntent = new Intent(mMainActivity, ZhaiDouListActivity.class);
                    mIntent.putExtra("title", "拣元宝动态");
                    mIntent.putExtra("id", id);

                    mMainActivity.startActivity(mIntent);
                }
            });



            return convertView;
        }


        private class ViewHolder {
            ImageView tvShopHead;//店铺头像

            TextView tvAddressName;// 商店名称
            ImageView ivHaveBird;// 有鸟

            ImageView agency_picture;// 代理照片

            TextView address_des;// 描述

            TextView tv_address_number;// 店地址

            LinearLayout llRootItem;// 条目根布局

            ImageView iv_adv_picture;// 广告图片
            LinearLayout ll_adv_root;// 广告根布局
            RelativeLayout rl_item_bottom_tab;// 灰色背景条

            TextView tv_money_shop;// 金额
            ImageView iv_user_photo_1;// 摘豆人头像
            ImageView iv_user_photo_2;// 摘豆人头像
            ImageView iv_user_photo_3;// 摘豆人头像
            ImageView iv_user_photo_4;// 摘豆人头像
            ImageView iv_user_photo_5;// 摘豆人头像
            ImageView iv_user_photo_6;// 摘豆人头像
        }

    }

    private void addBirdToHomeBirdList() {

    }

    class AndroidAndJSInterface {

        @JavascriptInterface
        public void call(String phone) {

            ToastUtils.showToast(mMainActivity, phone);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            //            if (ActivityCompat.checkSelfPermission(JSLHomeHtmlActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //                return;
            //            }
            startActivity(intent);

        }
    }

}
