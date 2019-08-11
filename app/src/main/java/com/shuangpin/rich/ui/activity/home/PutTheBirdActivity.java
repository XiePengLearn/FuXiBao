package com.shuangpin.rich.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.multi.view.MultiImageSelectorActivity;
import com.shuangpin.rich.adapter.ProducGridAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.shuangpin.rich.wxapi.WXPayHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class PutTheBirdActivity extends BaseActivity {

    private static final String TAG = "PutTheBirdActivity";
    private Context mContext;
    @InjectView(R.id.noScrollgridview)
    GridView noScrollgridview;//资质的gridView

    @InjectView(R.id.btn_next_data)
    Button nextData;//放鸟喽按钮

    @InjectView(R.id.et_you_want_to_tell_everyone)
    EditText youWantTellEveryone;//你想对 大家说

    @InjectView(R.id.et_url)
    EditText et_url;//网址链接

    @InjectView(R.id.et_enter_the_amount)
    EditText enteTheAamount;//放鸟 金额

    @InjectView(R.id.et_the_bird_number)
    EditText theBirdNumber;//放鸟 个数
    @InjectView(R.id.tv_location)
    TextView location;//具体位置
    @InjectView(R.id.ll_location)
    LinearLayout llLocation;//具体位置 跟布局

    @InjectView(R.id.tv_mode_of_payment)
    TextView modeOfPayment;//支付方式
    @InjectView(R.id.ll_mode_of_payment)
    LinearLayout modeOfPaymentRoot;//支付方式跟布局

    //启动activity 资质拍照的请求码
    private static final int REQUEST_IMAGE = 4;
    //    存放加号的list
    List<Bitmap> jiaListBitMap;
    //存放图片路径的list

    //   存放所有图片的list(不包括加号)
    List<Bitmap> listBitMap;
    List<Bitmap> listBitMapTemp;
    private List<String> aptitudePatnList;//资质图片路径

    private ArrayList<String> mSelectPath;
    private ProducGridAdapter adapter;
    private OSSClient oss;
    private String auther;
    private String token;
    private int initial;
    private String addr;
    private String city;
    private String district;
    private double latitude;
    private double longitude;

    private View contentView3;
    private PopupWindow popupWindow3 = null;
    private ArrayAdapter adapter3;
    private RuntCustomProgressDialog runtDialog;
    private List<String> desImgList;
    private String imgJsonString;
    private String cityAreaName;
    private String cityAreaId;
    private String districtAreaName;
    private String districtAreaId;
    private double latitudePutTheBird = 0.0;
    private double longitudePutTheBird = 0.0;
    private String modePayment;
    private IWXAPI api;
    private Toast toast;
    public static String ALIPAY_JSON_DATA;
    private String provinceAreaName;
    private String provinceAreaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_the_bird);
        setTitleBar(SHOW_LEFT);
        mContext = PutTheBirdActivity.this;
        StatusBarUtil.setStatusBar(PutTheBirdActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);

        desImgList = new ArrayList<>();
        String[] list3 = new String[]{"微信", "支付宝", "余额"};
        adapter3 = new ArrayAdapter<String>(mContext, R.layout.item_textview, list3);
        Intent mIntent = getIntent();
        //具体地址
        addr = mIntent.getStringExtra("addr");
        //城市
        city = mIntent.getStringExtra("city");
        //区域
        district = mIntent.getStringExtra("district");
        //获取纬度信息
        latitude = mIntent.getDoubleExtra("latitude", 0.0);
        //获取经度信息
        longitude = mIntent.getDoubleExtra("longitude", 0.0);


        initial = 0;
        auther = PrefUtils.readUid(mContext);
        token = PrefUtils.readToken(mContext);

        listBitMap = new ArrayList<Bitmap>();
        aptitudePatnList = new ArrayList<>();//资质图片路径

        initView();
        myOnclick();
        bindGridView();

        //上传 图片到Oss
        getOssConfig();

        nextData.setOnClickListener(this);
        modeOfPaymentRoot.setOnClickListener(this);
        llLocation.setOnClickListener(this);

        getLocateTheAddress();

    }

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
                                //                                ToastUtils.showToast(mContext, msg);
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


    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            //放鸟
            case R.id.btn_next_data:
                initial = 0;

                desImgList.clear();

                String youWantTooSay = youWantTellEveryone.getText().toString().trim();
                if (TextUtils.isEmpty(youWantTooSay)) {
                    ToastUtils.showToast(mContext, "请输入你想对大家说的话");
                    return;
                }
                String putTheAamount = enteTheAamount.getText().toString().trim();
                if (TextUtils.isEmpty(putTheAamount)) {
                    ToastUtils.showToast(mContext, "请输入金额");
                    return;
                }
                String putBirdNumber = theBirdNumber.getText().toString().trim();
                if (TextUtils.isEmpty(putBirdNumber)) {
                    ToastUtils.showToast(mContext, "请输入元宝个数");
                    return;
                }

                double putMoney = Double.parseDouble(putTheAamount);
                double putNumBird = Double.parseDouble(putBirdNumber + ".0");
                double putMoneyTen = putMoney / putNumBird;
                if (putMoneyTen < 0.1) {
                    //发送鸟的金额 单个不能低于0.1元
                    ToastUtils.showToast(mContext, "元宝的金额最低为0.1元");
                    return;
                }

                if (latitudePutTheBird == 0.0) {
                    ToastUtils.showToast(mContext, "请选择位置");
                    return;
                }
                if (longitudePutTheBird == 0.0) {
                    ToastUtils.showToast(mContext, "请选择位置");
                    return;
                }


                if (TextUtils.isEmpty(cityAreaId)) {//市 id
                    ToastUtils.showToast(mContext, "请选择位置");
                    return;
                }
                if (TextUtils.isEmpty(districtAreaId)) {//区id
                    ToastUtils.showToast(mContext, "请选择位置");
                    return;
                }
                if (TextUtils.isEmpty(provinceAreaId)) {//区id
                    ToastUtils.showToast(mContext, "请选择位置");
                    return;
                }

                runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("正在上传");
                runtDialog.show();

                postToOss();


                break;
            case R.id.left_btn:
                this.finish();
                break;
            //选择支付方式
            case R.id.ll_mode_of_payment:

                classifyMethod();

                break;
            //选择 具体位置
            case R.id.ll_location:

                mIntent = new Intent(mContext, LocateTheAddressActivity.class);
                mIntent.putExtra("title", "");
                mIntent.putExtra("latitude", latitude);//获取纬度信息
                mIntent.putExtra("longitude", longitude);//获取经度信息
                startActivityForResult(mIntent, 100);

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
        popupWindow3.showAsDropDown(modeOfPayment, 0, 0);
    }

    private View create3Classificationview() {
        ListView listView = (ListView) View.inflate(mContext, R.layout.listview, null);
        listView.setAdapter(adapter3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkPayment = (String) parent.getItemAtPosition(position);
                modeOfPayment.setText(checkPayment);
                popupWindow3.dismiss();


            }
        });
        return listView;
    }

    /**
     * 初始化控件
     */
    private void initView() {

        //      放+的list
        jiaListBitMap = new ArrayList<Bitmap>();


    }

    /**
     * 点击事件
     */
    private void myOnclick() {

        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == jiaListBitMap.size() - 1) {
                    setImgMode();
                }
            }
        });
    }

    //  相册，相机模式设置
    private void setImgMode() {
        Intent intent = new Intent(PutTheBirdActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        // 选择模式，1表示多选,0表示单选
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 1);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * 绑定加号
     */
    private void bindGridView() {
        //              把加号放到list中并绑定，到gridView中
        jiaListBitMap = bindJia();
        hengping();
        //      把数据绑定一下
        adapter = new ProducGridAdapter(jiaListBitMap, this);
        noScrollgridview.setAdapter(adapter);
    }

    private void hengping() {
        ViewGroup.LayoutParams params = noScrollgridview.getLayoutParams();
        // dishtype，welist为ArrayList
        int dishtypes = jiaListBitMap.size();
        //      图片之间的距离
        params.width = CommonUtil.dip2px(mContext, 90) * dishtypes;
        Log.d("看看这个宽度", params.width + "" + jiaListBitMap.size());
        noScrollgridview.setLayoutParams(params);
        //设置列数为得到的list长度
        noScrollgridview.setNumColumns(jiaListBitMap.size());
    }

    /**
     * 拍照和相册返回的数据
     */

    //  绑定加号
    private List<Bitmap> bindJia() {
        //              把加号放到list中并绑定，到gridView中
        InputStream is = getResources().openRawResource(R.raw.icon_addpic_unfocused1);

        Bitmap myBitmapJia = BitmapFactory.decodeStream(is);
        jiaListBitMap.add(myBitmapJia);
        return jiaListBitMap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            if (resultCode == RESULT_OK) {
                String stringExtra = data.getStringExtra("returnAddress");
                //用户选择的 经纬度
                latitudePutTheBird = data.getDoubleExtra("latitude", 0.0);
                longitudePutTheBird = data.getDoubleExtra("longitude", 0.0);


                if (!TextUtils.isEmpty(stringExtra)) {
                    location.setText(stringExtra);
                }


                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;


                RequestBody requestBody = new FormBody.Builder()
                        .add("longitude", longitudePutTheBird + "")//经度
                        .add("latitude", latitudePutTheBird + "")//纬度

                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_CITY_LOCATION)
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

                        LogUtilsxp.e2(TAG, "URL_CITY_LOCATION_result:" + responseString);

                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                //                           {
                                //                            "code": 0,
                                //                                    "data": {
                                //                                "city": {
                                //                                    "areaName": "北京市",
                                //                                            "areaId": "110100"
                                //                                },
                                //                                "district": {
                                //                                    "areaName": "朝阳区",
                                //                                            "areaId": "110105"
                                //                                }
                                //                            },
                                //                            "msg": ""
                                //                        }

                                try {
                                    JSONObject object = new JSONObject(responseString);
                                    int resultCode = object.optInt("code");
                                    String msg = object.optString("msg");
                                    if (resultCode == 0) {
                                        JSONObject data = object.optJSONObject("data");
                                        JSONObject city = data.optJSONObject("city");
                                        cityAreaName = city.optString("areaName");
                                        cityAreaId = city.optString("areaId");

                                        JSONObject district = data.optJSONObject("district");
                                        districtAreaName = district.optString("areaName");
                                        districtAreaId = district.optString("areaId");

                                        JSONObject province = data.optJSONObject("province");
                                        provinceAreaName = province.optString("areaName");
                                        provinceAreaId = province.optString("areaId");

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
        if (requestCode == REQUEST_IMAGE) {

            if (resultCode == RESULT_OK) {
                listBitMap.clear();
                aptitudePatnList.clear();
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                //              把数据转化成Bitmap,放选中的图片
                listBitMapTemp = new ArrayList<Bitmap>();
                for (String path : mSelectPath) {
                    if (aptitudePatnList.size() > 9) {

                    } else {
                        Bitmap bitmap = decodeSampledBitmapFromFd(path, 400, 400);
                        //                  把bitmap放进去list中
                        listBitMapTemp.add(bitmap);
                        //                    将图片保存到sd卡中 得到图片名 放在集合中
                        String aptitudePath = saveImageToGallery(mContext, bitmap);

                        LogUtilsxp.e2(TAG, "aptitudePath:" + aptitudePath);
                        aptitudePatnList.add(aptitudePath);
                    }


                }
                listBitMap.addAll(listBitMapTemp);
                if (listBitMap.size() == 10) {
                    listBitMap.remove(9);
                    aptitudePatnList.remove(9);

                }
                if (listBitMap.size() == 11) {
                    listBitMap.remove(9);
                    listBitMap.remove(10);
                    aptitudePatnList.remove(9);
                    aptitudePatnList.remove(10);
                }
                if (listBitMap.size() == 12) {
                    listBitMap.remove(9);
                    listBitMap.remove(10);
                    listBitMap.remove(11);
                    aptitudePatnList.remove(9);
                    aptitudePatnList.remove(10);
                    aptitudePatnList.remove(11);
                }
                if (listBitMap.size() == 13) {
                    listBitMap.remove(9);
                    listBitMap.remove(10);
                    listBitMap.remove(11);
                    listBitMap.remove(12);
                    aptitudePatnList.remove(9);
                    aptitudePatnList.remove(10);
                    aptitudePatnList.remove(11);
                    aptitudePatnList.remove(12);
                }


                LogUtilsxp.e2(TAG, "listBitMap.size()" + listBitMap.size());
                //              清空所有数据
                jiaListBitMap.clear();
                bindJia();
                //              把加号和图片放一起
                jiaListBitMap.addAll(0, listBitMap);
                //              当选择9张图片时，删除加号
                if (jiaListBitMap.size() == 10) {
                    //                  第10张图片下标是9
                    jiaListBitMap.remove(9);
                }
                if (jiaListBitMap.size() == 11) {
                    //                  第10张图片下标是9
                    jiaListBitMap.remove(9);
                    jiaListBitMap.remove(10);
                }
                if (jiaListBitMap.size() == 12) {
                    //                  第10张图片下标是9
                    jiaListBitMap.remove(9);
                    jiaListBitMap.remove(10);
                    jiaListBitMap.remove(11);
                }
                if (jiaListBitMap.size() == 13) {
                    //                  第10张图片下标是9
                    jiaListBitMap.remove(9);
                    jiaListBitMap.remove(10);
                    jiaListBitMap.remove(11);
                    jiaListBitMap.remove(12);
                }
                hengping();
                //      把数据绑定一下
                adapter.notifyDataSetChanged();
            }
        }

    }


    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName,
                                                   int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "putBird");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";

        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        //        try {
        //            MediaStore.Images.Media.insertImage(context.getContentResolver(),
        //                    file.getAbsolutePath(), fileName, null);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
        // 最后通知图库更新
        //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return fileName;
    }


    //上传图片到OSS
    //    首先在初始化页面的时候 初始化oss 服务器sdk
    private void getOssConfig() {
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .build();
        Request request = new Request.Builder()
                .url("https://abundant.xjkrfx.net/sts.php")
                //                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "GET_OSS_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject objectd = new JSONObject(responseString);
                            int resultCode = objectd.optInt("code");
                            String msg = objectd.optString("msg");
                            if (resultCode == 0) {
                                JSONObject object = objectd.optJSONObject("data");

                                JSONObject credentials = object.optJSONObject("Credentials");
                                String AccessKeySecret = credentials.optString("AccessKeySecret");
                                String AccessKeyId = credentials.optString("AccessKeyId");
                                String Expiration = credentials.optString("Expiration");
                                String SecurityToken = credentials.optString("SecurityToken");
                                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(AccessKeyId, AccessKeySecret, SecurityToken);

                                //                String stsServer = "STS应用服务器地址，例如http://abc.com";
                                //                OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
                                ClientConfiguration conf = new ClientConfiguration();
                                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                                conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
                                conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
                                oss = new OSSClient(getApplicationContext(), HttpsApi.OSS_ENDOPINT, credentialProvider, conf);
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


    private void postToOss() {
        int imageSize = aptitudePatnList.size();
        LogUtilsxp.e2(TAG, "imageSize:" + imageSize);

        String putBirdDescribe = youWantTellEveryone.getText().toString().trim();
        String putTheAamount = enteTheAamount.getText().toString().trim();
        String putBirdNumber = theBirdNumber.getText().toString().trim();
        String url = et_url.getText().toString().trim();
        //        支付方式 1 余额 2 微信 3 支付宝
        final String modePaymentStr = modeOfPayment.getText().toString().trim();
        modePayment = "2";
        if (modePaymentStr.equals("余额")) {
            modePayment = "1";
        } else if (modePaymentStr.equals("微信")) {
            modePayment = "2";
        } else {
            modePayment = "3";
        }

        if (imageSize == 0) {
            CustomTrust customTrust = new CustomTrust(mContext);
            OkHttpClient okHttpClient = customTrust.client;


            RequestBody requestBody = new FormBody.Builder()
                    .add("money", putTheAamount)//金额
                    .add("info", putBirdDescribe)//描述
                    .add("img", "[]")//图片 {[herf:http://asdf.com]}
                    .add("numbers", putBirdNumber)//数量
                    .add("payType", modePayment)//支付方式 1 余额 2 微信 3 支付宝
                    .add("longitude", longitudePutTheBird + "")//经度
                    .add("latitude", latitudePutTheBird + "")//纬度
                    .add("city", cityAreaId)//城市
                    .add("district", districtAreaId)//区
                    .add("province", provinceAreaId)//省
                    .add("url", url)//网址链接

                    .build();
            Request request = new Request.Builder()
                    .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_SEND)
                    .post(requestBody)
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

                    LogUtilsxp.e2(TAG, "URL_BIRD_SEND_result:" + responseString);
                    runtDialog.dismiss();

                    CommonUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                JSONObject object = new JSONObject(responseString);
                                int resultCode = object.optInt("code");
                                String msg = object.optString("msg");
                                if (resultCode == 0) {

                                    //支付方式 1 余额 2 微信 3 支付宝
                                    String modePaymentStr = modeOfPayment.getText().toString().trim();
                                    if (modePaymentStr.equals("余额")) {

                                        ToastUtils.showToast(mContext, "余额支付成功");
                                        finish();

                                    } else if (modePaymentStr.equals("微信")) {


                                        api = WXAPIFactory.createWXAPI(mContext, WEICHAT_APP_ID);
                                        api.registerApp(WEICHAT_APP_ID);
                /* 自定义获取订单toast */
                                        LayoutInflater inflater = getLayoutInflater();
                                        final View layout = inflater.inflate(R.layout.dialog_ordering,
                                                (RelativeLayout) findViewById(R.id.dialog_getordering));
                                        toast = new Toast(getApplicationContext());
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();

                                        JSONObject inObj = object.getJSONObject("data");
                                        PayReq req = new PayReq();
                                        req.sign = inObj.getString("sign");
                                        req.timeStamp = inObj.getString("timestamp");
                                        req.packageValue = inObj.getString("package");
                                        req.nonceStr = inObj.getString("noncestr");
                                        req.partnerId = inObj.getString("partnerid");
                                        req.appId = inObj.getString("appid");
                                        req.prepayId = inObj.getString("prepayid");
                                        req.extData = "app data"; // optional
                                        api.sendReq(req);
                                        WXPayHelper.getInstance(PutTheBirdActivity.this, new WXPayHelper.WXPayCallBack() {
                                            @Override
                                            public void success() {
                                                ToastUtils.showToast(mContext, "支付成功");
                                                finish();
                                            }

                                            @Override
                                            public void cancel() {
                                                ToastUtils.showToast(mContext, "取消了微信支付");
                                            }

                                            @Override
                                            public void falure(String msg) {
                                                ToastUtils.showToast(mContext, "支付失败");
                                            }
                                        });


                                    } else {
                                        //支付宝
                                        JSONObject obj = new JSONObject(responseString);
                                        ALIPAY_JSON_DATA = obj.getString("data");
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                PayTask payTask = new PayTask(PutTheBirdActivity.this);
                                                String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                                LogUtilsxp.e2(TAG, "支付结果:" + result);
                                                if (result.contains("resultStatus={9000}")) {
                                                    ToastUtils.showToast(mContext, "支付成功");
                                                    finish();

                                                } else {
                                                    ToastUtils.showToast(mContext, "支付失败");


                                                }

                                            }
                                        }.start();

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

        if (initial < imageSize) {
            String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + aptitudePatnList.get(initial);
            //imagePage 本地上传图片的地址

            long fileName = System.currentTimeMillis();


            final String OssPath = "arrive/" + auther + "/" + fileName;
            PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, imagePage);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    if (currentSize == totalSize) {
                        LogUtilsxp.e2("PutObject", "总大小:" + totalSize);
                    }

                }
            });


            OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request1, PutObjectResult result) {
                    LogUtilsxp.e2(TAG, "UploadSuccess");
                    initial = initial + 1;

                    //请求服务器
                    String url = getUrl(OssPath);
                    desImgList.add(url);

                    if (initial == aptitudePatnList.size()) {

                        String putBirdDescribe = youWantTellEveryone.getText().toString().trim();
                        String putTheAamount = enteTheAamount.getText().toString().trim();
                        String putBirdNumber = theBirdNumber.getText().toString().trim();

                        runtDialog.dismiss();


                        try {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject tmpObj = null;
                            int count = desImgList.size();
                            for (int i = 0; i < count; i++) {
                                tmpObj = new JSONObject();
                                tmpObj.put("herf", desImgList.get(i));
                                jsonArray.put(tmpObj);
                                tmpObj = null;
                            }
                            // 将JSONArray转换得到String
                            imgJsonString = jsonArray.toString();
                            LogUtilsxp.e2(TAG, "imgJsonString:" + imgJsonString);

                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }

                        //                        LogUtilsxp.e2(TAG, desImgList.toString());
                        //                        LogUtilsxp.e2(TAG, "desImgList:" + desImgList.size());

                        CustomTrust customTrust = new CustomTrust(mContext);
                        OkHttpClient okHttpClient = customTrust.client;

                        LogUtilsxp.e2(TAG, "img:" + imgJsonString);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("money", putTheAamount)//金额
                                .add("info", putBirdDescribe)//描述
                                .add("img", imgJsonString)//图片 {[herf:http://asdf.com]}
                                .add("numbers", putBirdNumber)//数量
                                .add("payType", modePayment)//支付方式 1 余额 2 微信 3 支付宝
                                .add("longitude", longitudePutTheBird + "")//经度
                                .add("latitude", latitudePutTheBird + "")//纬度
                                .add("city", cityAreaId)//城市
                                .add("district", districtAreaId)//区
                                .add("province", provinceAreaId)//省
                                .add("url", url)//网址链接
                                .build();
                        Request request = new Request.Builder()
                                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_SEND)
                                .post(requestBody)
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

                                LogUtilsxp.e2(TAG, "URL_BIRD_SEND_result:" + responseString);
                                runtDialog.dismiss();

                                CommonUtil.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        try {
                                            JSONObject object = new JSONObject(responseString);
                                            int resultCode = object.optInt("code");
                                            String msg = object.optString("msg");
                                            if (resultCode == 0) {

                                                //支付方式 1 余额 2 微信 3 支付宝
                                                String modePaymentStr = modeOfPayment.getText().toString().trim();
                                                if (modePaymentStr.equals("余额")) {
                                                    ToastUtils.showToast(mContext, "余额支付成功");
                                                    finish();
                                                } else if (modePaymentStr.equals("微信")) {

                                                    //2 微信
                                                    //                                                    ToastUtils.showToast(mContext, "微信");

                                                    api = WXAPIFactory.createWXAPI(mContext, WEICHAT_APP_ID);
                                                    api.registerApp(WEICHAT_APP_ID);
                /* 自定义获取订单toast */
                                                    LayoutInflater inflater = getLayoutInflater();
                                                    final View layout = inflater.inflate(R.layout.dialog_ordering,
                                                            (RelativeLayout) findViewById(R.id.dialog_getordering));
                                                    toast = new Toast(getApplicationContext());
                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                    toast.setView(layout);
                                                    toast.show();

                                                    JSONObject inObj = object.getJSONObject("data");
                                                    PayReq req = new PayReq();
                                                    req.sign = inObj.getString("sign");
                                                    req.timeStamp = inObj.getString("timestamp");
                                                    req.packageValue = inObj.getString("package");
                                                    req.nonceStr = inObj.getString("noncestr");
                                                    req.partnerId = inObj.getString("partnerid");
                                                    req.appId = inObj.getString("appid");
                                                    req.prepayId = inObj.getString("prepayid");
                                                    req.extData = "app data"; // optional
                                                    api.sendReq(req);
                                                    WXPayHelper.getInstance(PutTheBirdActivity.this, new WXPayHelper.WXPayCallBack() {
                                                        @Override
                                                        public void success() {
                                                            ToastUtils.showToast(mContext, "支付成功");
                                                            finish();
                                                        }

                                                        @Override
                                                        public void cancel() {
                                                            ToastUtils.showToast(mContext, "取消了微信支付");
                                                        }

                                                        @Override
                                                        public void falure(String msg) {
                                                            ToastUtils.showToast(mContext, "支付失败");
                                                        }
                                                    });


                                                } else {
                                                    //支付宝
                                                    JSONObject obj = new JSONObject(responseString);
                                                    ALIPAY_JSON_DATA = obj.getString("data");
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            super.run();
                                                            PayTask payTask = new PayTask(PutTheBirdActivity.this);
                                                            String result = payTask.pay(ALIPAY_JSON_DATA, true);

                                                            LogUtilsxp.e2(TAG, "支付结果:" + result);
                                                            if (result.contains("resultStatus={9000}")) {
                                                                ToastUtils.showToast(mContext, "支付成功");
                                                                finish();

                                                            } else {
                                                                ToastUtils.showToast(mContext, "支付失败");


                                                            }

                                                        }
                                                    }.start();

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


                    } else {
                        postToOss();
                        //                        递归调用上传图片到OSS
                    }


                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        LogUtilsxp.e2(TAG, "本地异常如网络异常等");
                        LogUtilsxp.e2(TAG, "本地异常如网络异常等:" + clientExcepion.getMessage().toString());
                        clientExcepion.printStackTrace();

                    }
                    if (serviceException != null) {
                        // 服务异常
                        LogUtilsxp.e2(TAG, "服务异常");
                        LogUtilsxp.e2("ErrorCode", serviceException.getErrorCode());
                        LogUtilsxp.e2("RequestId", serviceException.getRequestId());
                        LogUtilsxp.e2("HostId", serviceException.getHostId());
                        LogUtilsxp.e2("RawMessage", serviceException.getRawMessage());
                    }
                }
            });

        }


    }


    public String getUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10

        long time = 3600l * 1000 * 24 * 365 * 10;
        // 生成URL
        String url = null;
        try {
            url = oss.presignPublicObjectURL(HttpsApi.OSS_OSSBUCKET, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (url != null) {
            return url.toString();
        }
        return null;
    }

}
