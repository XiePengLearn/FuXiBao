package com.shuangpin.rich.ui.activity.mine.business;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.dialog.RuntMMAlert;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.home.LocateTheAddressActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.mine.threelinkage.CityBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.CountryBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.LocationBean;
import com.shuangpin.rich.ui.activity.mine.threelinkage.ThreeMenuDialogArea;
import com.shuangpin.rich.ui.activity.video.IndividualMerchantsWithVideoActivity;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个体商户申请 ,填写资料
 */
public class SelfEmployedBusinessmanActivity extends BaseActivity {

    private static final String TAG = "SelfEmployedBusinessmanActivity";
    private Context mContext;

    @InjectView(R.id.et_business_brand_name)
    EditText businessBrandName; //品牌

    @InjectView(R.id.et_select_city)
    TextView selectCity;//请选择省市区

    @InjectView(R.id.et_location)
    EditText specificLocation; //具体地址
    @InjectView(R.id.iv_picture_of_the_door)
    ImageView pictureOfTheDoor; //门头照

    private String shopImagePath = "";

    @InjectView(R.id.et_business_merchant_name)
    EditText businessMerchantName; //商户姓名
    @InjectView(R.id.et_business_phone_number)
    EditText businessPhoneNumber; //手机号
    @InjectView(R.id.et_business_id_card_no)
    EditText businessIdCardNo; //身份证号码
    @InjectView(R.id.et_business_Invitation_agent_number)
    EditText businessInvitationAgentNumber; //商城链接地址
    @InjectView(R.id.btn_business_submit_audit)
    Button businessSubmitAudit; //提交审核

    @InjectView(R.id.cb_apply_logo)
    CheckBox cbApplyLogo;
    @InjectView(R.id.tv_xieyi)
    TextView xieyi;

    private static final int REQUEST_GET_BITMAP = 128;
    private static final int REQUEST_GET_BITMAPB = 129;
    private static final int REQUEST_GET_BITMAPC = 130;

    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final String PHOTO_FILE_NAME = "jiandao.jpg";
    private Uri uritempFile;
    private String auther;
    private String token;
    private OSSClient oss;
    private String area_province;
    private String area_city;
    private String area_county;
    private double latitudePutTheBird;
    private double longitudePutTheBird;
    private String cityAreaName;
    private String cityAreaId;
    private String districtAreaName;
    private String districtAreaId;
    private String provinceAreaName;
    private String provinceAreaId;
    private String stringExtraAdress;
    private Bitmap mBitmap;
    private Intent mIntent;
    private String mShopUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_employed_businessman);
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(SelfEmployedBusinessmanActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = SelfEmployedBusinessmanActivity.this;
        auther = PrefUtils.readUid(mContext);
        token = PrefUtils.readToken(mContext);

        selectCity.setOnClickListener(this);
        pictureOfTheDoor.setOnClickListener(this);
        businessSubmitAudit.setOnClickListener(this);
        xieyi.setOnClickListener(this);

        //初始化OSS 客户端
        getOssConfig();


        mIntent = getIntent();
        mShopUrl = mIntent.getStringExtra("shopUrl");

        if(!TextUtils.isEmpty(mShopUrl)){
            businessInvitationAgentNumber.setText(mShopUrl);
        }

//        AlertDialog.Builder builder = new AlertDialog.Builder(SelfEmployedBusinessmanActivity.this);//MainActivity.this为当前环境
//
//
//        builder.setTitle("代理申请公告");//提示框标题
//
//        builder.setMessage(".....");//提示内容
//
//        builder.setPositiveButton("确认", null);//确定按钮
//
//        builder.create().show();


    }

    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            //升级
            case R.id.et_select_city:
                //                selectCity();
                String longitude = PrefUtils.readLongitude(mContext);
                String latitude = PrefUtils.readLatitude(mContext);

                if (!longitude.equals("0.0") && !latitude.equals("0.0")) {
                    mIntent = new Intent(mContext, LocateTheAddressActivity.class);
                    mIntent.putExtra("title", "");
                    double v1 = Double.parseDouble(latitude);
                    double v2 = Double.parseDouble(longitude);
                    mIntent.putExtra("latitude", v1);//获取纬度信息
                    mIntent.putExtra("longitude", v2);//获取经度信息
                    startActivityForResult(mIntent, 100);

                } else {
                    ToastUtils.showToast(mContext, "定位请求失败,请回到首页重新请求定位");
                }
                break;
            //店铺形象照
            case R.id.iv_picture_of_the_door:
                //                if (PhoneUtils.cameraPermissions(SelfEmployedBusinessmanActivity.this)) {
                chooseCamera();
                //                }
                break;

            case R.id.tv_xieyi:
                String url = HttpsApi.SHANGHU;


                mIntent = new Intent(mContext, HtmlActivity.class);
                mIntent.putExtra("title", "用户登录协议");
                mIntent.putExtra("url", url);
                startActivity(mIntent);

                break;
            //提交审核
            case R.id.btn_business_submit_audit:


                boolean checked = cbApplyLogo.isChecked();
                if (!checked) {
                    ToastUtils.showToast(mContext, "请阅读并同意《合伙人申请协议》");
                    return;
                }

                String brandName = businessBrandName.getText().toString().trim();
                if (TextUtils.isEmpty(brandName)) {
                    ToastUtils.showToast(mContext, "请输入品牌名称");
                    return;
                }
                String chanceCity = selectCity.getText().toString().trim();
                if (TextUtils.isEmpty(chanceCity)) {
                    ToastUtils.showToast(mContext, "请选择省市区");
                    return;
                }

                String specificLocationStr = specificLocation.getText().toString().trim();
                if (TextUtils.isEmpty(specificLocationStr)) {
                    ToastUtils.showToast(mContext, "请输入详细地址");
                    return;
                }


                if (!TextUtils.isEmpty(shopImagePath)) {
                    File tempFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + shopImagePath);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + shopImagePath;
                    if (tempFile.exists()) {

                        //第二次  判断之后进行逻辑的上传
                    } else {
                        ToastUtils.showToast(mContext, "请添加合伙人首张图片");
                        return;
                    }
                } else {
                    ToastUtils.showToast(mContext, "请添加合伙人首张图片");
                    return;
                }

                String businessName = businessMerchantName.getText().toString().trim();
                if (TextUtils.isEmpty(businessName)) {
                    ToastUtils.showToast(mContext, "请输入合伙人姓名");
                    return;
                }

                String phoneNumber = businessPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToast(mContext, "请输入手机号码");
                    return;
                }

//                String idCardNo = businessIdCardNo.getText().toString().trim();
//                if (TextUtils.isEmpty(idCardNo)) {
//                    ToastUtils.showToast(mContext, "请输入身份证号码");
//                    return;
//                }
//
//                try {
//                    if (!IDCard.IDCardValidate(idCardNo)) {
//                        ToastUtils.showToast(mContext, "请输入正确的身份证号");
//                        return;
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    ToastUtils.showToast(mContext, "请输入正确的身份证号");
//                    return;
//                }

                String agentNumber = businessInvitationAgentNumber.getText().toString().trim();
                if (TextUtils.isEmpty(agentNumber)) {
                    //选填
                    agentNumber = "";
                } else if (!agentNumber.startsWith("http")) {
                    ToastUtils.showToast(mContext, "请输入以http://或者https://开头的正确网址");
                    return;
                }

                if (!TextUtils.isEmpty(shopImagePath)) {
                    File tempFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + shopImagePath);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + shopImagePath;
                    if (tempFile.exists()) {
                        postToOss(imagePage, shopImagePath, brandName, phoneNumber,  agentNumber, businessName);
                    } else {
                        ToastUtils.showToast(mContext, "请添加合伙人首张图片");
                        return;
                    }
                } else {
                    ToastUtils.showToast(mContext, "请添加合伙人首张图片");
                    return;
                }


                break;
        }
    }

    /**
     * 选择城市
     */
    private void selectCity() {


        final ThreeMenuDialogArea dialog = new ThreeMenuDialogArea(mContext, SelfEmployedBusinessmanActivity.this);
        dialog.setonItemClickListener(new ThreeMenuDialogArea.MenuItemClickListener() {
            @Override
            public void onMenuItemClick(CountryBean.DataBean menuData, LocationBean.DataBean locationBean, CityBean.DataBean cityBean) {
                selectCity.setText(locationBean.getArea_name() + " " + cityBean.getArea_name() + " " + menuData.getArea_name());
                area_province = locationBean.getArea_id();
                area_city = cityBean.getArea_id();

                area_county = menuData.getArea_id();
                LogUtilsxp.e2(TAG, "area_province:" + area_province + "area_city:" + area_city + "area_county:" + area_county);

            }
        });
        dialog.show();

    }


    private void chooseCamera() {
        RuntMMAlert.showAlert(this, "", mContext.getResources().getStringArray(R.array.camer_item),
                null, new impOnAlertStore());

    }

    private class impOnAlertStore implements RuntMMAlert.OnAlertSelectId {
        @Override
        public void onClick(int whichButton) {
            switch (whichButton) {
                case 0:
                    getPicFromPhoto();
                    break;
                case 1:
                    getPicFromCamera();
                    break;
                case 2:
                    /* 什么处理不需要做，系统认为是点击在dialog外，使之dismiss */
                    break;
                default:
                    break;
            }
        }
    }

    private void getPicFromPhoto() {
        Intent intentPicFromPhoto = new Intent(Intent.ACTION_PICK, null);
        intentPicFromPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentPicFromPhoto, PHOTO_REQUEST);
    }

    private void getPicFromCamera() {
        //        if (PhoneUtils.cameraPermissions(SelfEmployedBusinessmanActivity.this)) {
        startCamera();
        //        }
    }

    // 拍照后的保存路径
    private void startCamera() {
        Intent intentStartCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intentStartCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        startActivityForResult(intentStartCamera, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PHOTO_REQUEST:
                try {
                    startPhotoZoom(data.getData());
                } catch (Exception e) {
                    return;
                }
                break;

            case REQUEST_GET_BITMAP:
                try {

                    mBitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    pictureOfTheDoor.setImageBitmap(mBitmap);
                    shopImagePath = saveImageToGallery(mContext, mBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CAMERA_REQUEST:
                switch (resultCode) {
                    case -1:// -1表示拍照成功
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + PHOTO_FILE_NAME);
                        if (file.exists()) {
                            startPhotoZoom(Uri.fromFile(file));
                        }

                        break;
                }
                break;


        }
        if (requestCode == 100) {

            if (resultCode == RESULT_OK) {
                stringExtraAdress = data.getStringExtra("returnAddress");
                //用户选择的 经纬度
                latitudePutTheBird = data.getDoubleExtra("latitude", 0.0);
                longitudePutTheBird = data.getDoubleExtra("longitude", 0.0);


                if (!TextUtils.isEmpty(stringExtraAdress)) {
                    selectCity.setText(stringExtraAdress);
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
                                //                                    {
                                //                                        "code": 0,
                                //                                            "data": {
                                //                                        "city": {
                                //                                            "areaName": "北京市",
                                //                                                    "areaId": "110100"
                                //                                        },
                                //                                        "district": {
                                //                                            "areaName": "朝阳区",
                                //                                                    "areaId": "110105"
                                //                                        },
                                //                                        "province": {
                                //                                            "areaName": "北京市",
                                //                                                    "areaId": "110000"
                                //                                        }
                                //                                    },
                                //                                        "msg": ""
                                //                                    }

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

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        //        intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_GET_BITMAP);

    }

    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "putBird");
        if (appDir.exists()) {
            appDir.delete();
        }
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

        //        // 其次把文件插入到系统图库
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


    private void postToOss(String path, final String fileName, final String brandName, final String phoneNumber,
                            final String agentNumber, final String businessName) {
        //        LogUtilsxp.e2(TAG, "path:" + path);
        //        LogUtilsxp.e2(TAG, "fileName:" + fileName);

        final String specificLocationStr = specificLocation.getText().toString().trim();
        final RuntCustomProgressDialog runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("正在上传");
        runtDialog.show();
        final String OssPath = "shop/" + auther + "/" + fileName;
        PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, path);
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
                String url = getUrl(OssPath);
                //                LogUtilsxp.e2(TAG, "returnUrl:" + url);


                CustomTrust customTrust = new CustomTrust(mContext);
                OkHttpClient okHttpClient = customTrust.client;

                //                LogUtilsxp.e2(TAG,"shopName:"+brandName+"province:"+provinceAreaId+
                //                        "city:"+cityAreaId+"county:"+districtAreaId+
                //                        "address:"+stringExtraAdress+"header:"+url+
                //                        "mobile:"+phoneNumber+"identity:"+idCardNo+
                //                        "referrer:"+agentNumber+"name:"+businessName+
                //                        "longitude:"+longitudePutTheBird + ""+"latitude:"+latitudePutTheBird);

                RequestBody requestBody = new FormBody.Builder()
                        .add("shopName", brandName)
                        .add("province", provinceAreaId)
                        .add("city", cityAreaId)
                        .add("county", districtAreaId)
                        .add("address", specificLocationStr)
                        .add("header", url)//oss路径
                        .add("mobile", phoneNumber)
//                        .add("identity", idCardNo)
                        .add("url", agentNumber)
                        .add("imgType", "1")//图片
                        .add("name", businessName)
                        .add("longitude", longitudePutTheBird + "")
                        .add("latitude", latitudePutTheBird + "")
                        .build();
                Request request = new Request.Builder()
                        .url(HttpsApi.SERVER_URL + HttpsApi.URL_CREATE)
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

                        LogUtilsxp.e2(TAG, "URL_CREATE_result:" + responseString);
                        runtDialog.dismiss();

                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    JSONObject object = new JSONObject(responseString);
                                    int resultCode = object.optInt("code");
                                    String msg = object.optString("msg");
                                    if (resultCode == 0) {
                                        ToastUtils.showToast(mContext, msg);
                                        JSONObject data = object.optJSONObject("data");
                                        String shopId = data.optString("id");
                                        String shopName = data.optString("shopName");
                                        String name = data.optString("name");
                                        //                                        Intent mIntent = new Intent(SelfEmployedBusinessmanActivity.this, IndividualMerchantsAdvertiseActivity.class);
                                        Intent mIntent = new Intent(SelfEmployedBusinessmanActivity.this, IndividualMerchantsWithVideoActivity.class);
                                        mIntent.putExtra("title", shopName);
                                        mIntent.putExtra("shopId", shopId);
                                        startActivity(mIntent);

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
        task.waitUntilFinished();

    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10

        //        如果Bucket或Object是公共可读的，那么调用一下接口，获得可公开访问Object的URL：


        long time = 3600l * 1000 * 24 * 365 * 10;
        // 生成URL
        String url = null;
        try {
            //            url = oss.presignConstrainedObjectURL(HttpsApi.OSS_OSSBUCKET, key, time);
            url = oss.presignPublicObjectURL(HttpsApi.OSS_OSSBUCKET, key);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    public static Bitmap getFitSampleBitmap(InputStream inputStream) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] bytes = readStream(inputStream);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 从inputStream中获取字节流 数组大小
     **/
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mBitmap != null && !mBitmap.isRecycled()) {

            mBitmap.isRecycled();
            mBitmap = null;
        }
        System.gc();
    }
}
