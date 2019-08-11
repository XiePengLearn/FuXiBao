package com.shuangpin.rich.ui.activity.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.R;
import com.shuangpin.rich.adapter.ProducGridAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.home.LocateTheAddressActivity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;
import static com.shuangpin.rich.util.GlobalParam.WEICHAT_APP_ID;

public class PutTheBirdWithVideoActivity extends BaseActivity implements PhotoPopupWindowXp.OnItemClickListener {


    private List<LocalMedia> selectList = new ArrayList<>();
    private int maxSelectNum = 1;

    private static final String TAG = "PutTheBirdWithVideoActivity";
    private Context mContext;
    //    @InjectView(R.id.noScrollgridview)
    //    GridView noScrollgridview;//资质的gridView
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;//资质的gridView

    @InjectView(R.id.btn_next_data)
    Button nextData;//放鸟喽按钮

    @InjectView(R.id.et_you_want_to_tell_everyone)
    EditText youWantTellEveryone;//你想对 大家说

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

    @InjectView(R.id.ll_parent)
    LinearLayout ll_parent;//跟布局

    @InjectView(R.id.iv_add_pic_video)
    ImageView ivAddPicVideo;//添加照片或者视频

    //启动activity 资质拍照的请求码
    private static final int REQUEST_IMAGE = 4;
    //    存放加号的list
    List<Bitmap> jiaListBitMap;
    //存放图片路径的list
    @InjectView(R.id.et_url)
    EditText et_url;//网址链接

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
    private GridImageAdapter adapterView;
    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();
    private int aspect_ratio_x = 1, aspect_ratio_y = 1;
    private PhotoPopupWindowXp popupWindow;
    private String imgType;
    private String mUrlwangzhi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_the_bird_with_video);
        themeId = R.style.picture_default_style;
        setTitleBar(SHOW_LEFT);
        mContext = PutTheBirdWithVideoActivity.this;
        StatusBarUtil.setStatusBar(PutTheBirdWithVideoActivity.this, R.color.theme_color_title);
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


        //上传 图片到Oss
        getOssConfig();

        nextData.setOnClickListener(this);
        modeOfPaymentRoot.setOnClickListener(this);
        llLocation.setOnClickListener(this);
        ivAddPicVideo.setOnClickListener(this);

        getLocateTheAddress();


        FullyGridLayoutManager manager = new FullyGridLayoutManager(PutTheBirdWithVideoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapterView = new GridImageAdapter(PutTheBirdWithVideoActivity.this, onAddPicClickListener);
        adapterView.setList(selectList);

        recyclerView.setAdapter(adapterView);
        adapterView.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PutTheBirdWithVideoActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PutTheBirdWithVideoActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PutTheBirdWithVideoActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }

            }
        });


        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PutTheBirdWithVideoActivity.this);
                } else {
                    Toast.makeText(PutTheBirdWithVideoActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });


    }


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            if (selectList.size() == 0) {
                popupWindow = new PhotoPopupWindowXp(PutTheBirdWithVideoActivity.this);
                popupWindow.setOnItemClickListener(PutTheBirdWithVideoActivity.this);
                popupWindow.showAsDropDown(ll_parent);
            } else {
                PictureSelector.create(PutTheBirdWithVideoActivity.this)
                        .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(false)// 是否裁剪
                        .compress(true)// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //                        .hideBottomControls(cb_hide.isChecked() ? false : true)// 是否显示uCrop工具栏，默认不显示
                        .isGif(true)// 是否显示gif图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
                        //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .videoMaxSecond(11)
                        .videoMinSecond(1)
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.rotateEnabled(true) // 裁剪是否可旋转图片
                        //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                        .videoQuality(1)// 视频录制质量 0 or 1
                        //.videoSecond()//显示多少秒以内的视频or音频也可适用
                        .recordVideoSecond(10)//录制视频秒数 默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        }

    };


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
                mUrlwangzhi = et_url.getText().toString().trim();
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
                //                int size = selectList.size();
                //                LogUtilsxp.e2(TAG, "size:" + size);


                break;

            //添加照片或者视频
            case R.id.iv_add_pic_video:
                popupWindow = new PhotoPopupWindowXp(this);
                popupWindow.setOnItemClickListener(this);
                popupWindow.showAsDropDown(ll_parent);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true


                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        LogUtilsxp.e2("图片-----》", media.getPath());
                        if (media.isCompressed()) {
                            LogUtilsxp.e2("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                            LogUtilsxp.e2("压缩地址::", media.getCompressPath());
                        }
                    }
                    int size = selectList.size();
                    if (size == 0) {
                        ivAddPicVideo.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        ivAddPicVideo.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    adapterView.setList(selectList);

                    adapterView.notifyDataSetChanged();
                    break;
            }
        }


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



        int imageSize = selectList.size();

        LogUtilsxp.e2(TAG, "imageSize:" + imageSize);

        String putBirdDescribe = youWantTellEveryone.getText().toString().trim();
        String putTheAamount = enteTheAamount.getText().toString().trim();
        String putBirdNumber = theBirdNumber.getText().toString().trim();
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
                    .add("imgType", "0")//没有图片和视频

                    .add("district", districtAreaId)//区
                    .add("province", provinceAreaId)//省
                    .add("url", mUrlwangzhi)//网址链接
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
                                        WXPayHelper.getInstance(PutTheBirdWithVideoActivity.this, new WXPayHelper.WXPayCallBack() {
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
                                                PayTask payTask = new PayTask(PutTheBirdWithVideoActivity.this);
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


            LocalMedia media = selectList.get(initial);

            if (media.isCompressed()) {
                //图片
                imgType = "1";
                LogUtilsxp.e2("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                LogUtilsxp.e2("压缩地址::", media.getCompressPath());
                String picturePath = media.getCompressPath();
                postServerMethod(picturePath);
            } else {
                //视频
                imgType = "2";
                String picturePath = media.getPath();


                if (TextUtils.isEmpty(picturePath)) {
                    ToastUtils.showToast(mContext, "请先选择转码文件！");
                    return;
                }
                //PLShortVideoTranscoder初始化，三个参数，第一个context，第二个要压缩文件的路径，第三个视频压缩后输出的路径
                PLShortVideoTranscoder mShortVideoTranscoder = new PLShortVideoTranscoder(mContext, picturePath, Environment.getExternalStorageDirectory() + "compress/android" + System.currentTimeMillis() + ".mp4");
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                retr.setDataSource(picturePath);
                String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
                String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
                String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
                int transcodingBitrateLevel = 6;//我这里选择的2500*1000压缩，这里可以自己选择合适的压缩比例
                mShortVideoTranscoder.transcode(Integer.parseInt(width), Integer.parseInt(height), getEncodingBitrateLevel(transcodingBitrateLevel), false, new PLVideoSaveListener() {
                    @Override
                    public void onSaveVideoSuccess(String s) {
                        postServerMethod(s);
                    }

                    @Override
                    public void onSaveVideoFailed(final int errorCode) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (errorCode) {
                                    case ERROR_NO_VIDEO_TRACK:
                                        ToastUtils.showToast(mContext, "该文件没有视频信息！");
                                        break;
                                    case ERROR_SRC_DST_SAME_FILE_PATH:
                                        ToastUtils.showToast(mContext, "源文件路径和目标路径不能相同！");
                                        break;
                                    case ERROR_LOW_MEMORY:
                                        ToastUtils.showToast(mContext, "手机内存不足，无法对该视频进行时光倒流！");
                                        break;
                                    default:
                                        ToastUtils.showToast(mContext, "视频压缩失败,错误码:" + errorCode);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSaveVideoCanceled() {
                        //                LogUtil.e("onSaveVideoCanceled");
                    }

                    @Override
                    public void onProgressUpdate(float percentage) {
                        //                LogUtil.e("onProgressUpdate==========" + percentage);
                    }
                });
            }


        }


    }

    private void postServerMethod(String picturePath) {
        final String OssPath = "arrive/" + auther + "/" + "android" + picturePath;
        PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, picturePath);
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

                if (initial == selectList.size()) {

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
                            .add("imgType", imgType)//
                            .add("district", districtAreaId)//区
                            .add("province", provinceAreaId)//省
                            .add("url", mUrlwangzhi)//网址链接
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_SEND)
                            .post(requestBody)
                            .addHeader("Authorization", token)
                            .build();

                    //
                    okHttpClient.newCall(request).enqueue(new Callback() {
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
                                                WXPayHelper.getInstance(PutTheBirdWithVideoActivity.this, new WXPayHelper.WXPayCallBack() {
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
                                                        PayTask payTask = new PayTask(PutTheBirdWithVideoActivity.this);
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

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                // 单独拍照
                maxSelectNum = 9;
                chooseMode = PictureMimeType.ofImage();
                adapterView.setSelectMax(maxSelectNum);
                PictureSelector.create(PutTheBirdWithVideoActivity.this)
                        .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(false)// 是否裁剪
                        .compress(true)// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //                        .hideBottomControls(cb_hide.isChecked() ? false : true)// 是否显示uCrop工具栏，默认不显示
                        .isGif(true)// 是否显示gif图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
                        //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .videoMaxSecond(11)
                        .videoMinSecond(1)
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.rotateEnabled(true) // 裁剪是否可旋转图片
                        //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                        .videoQuality(1)// 视频录制质量 0 or 1
                        //.videoSecond()//显示多少秒以内的视频or音频也可适用
                        .recordVideoSecond(10)//录制视频秒数 默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code


                break;
            case 1:
                // 录视频
                //                startOpenCameraVideo();
                maxSelectNum = 1;
                chooseMode = PictureMimeType.ofVideo();
                adapterView.setSelectMax(maxSelectNum);
                PictureSelector.create(PutTheBirdWithVideoActivity.this)
                        .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(true)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(false)// 是否裁剪
                        .compress(true)// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //                        .hideBottomControls(cb_hide.isChecked() ? false : true)// 是否显示uCrop工具栏，默认不显示
                        .isGif(true)// 是否显示gif图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
                        //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                        .videoMaxSecond(11)
                        .videoMinSecond(1)
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.rotateEnabled(true) // 裁剪是否可旋转图片
                        //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                        .videoQuality(1)// 视频录制质量 0 or 1
                        //.videoSecond()//显示多少秒以内的视频or音频也可适用
                        .recordVideoSecond(10)//录制视频秒数 默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code


                break;
        }
    }


    /**
     * 设置压缩质量
     *
     * @param position
     * @return
     */
    private int getEncodingBitrateLevel(int position) {
        return ENCODING_BITRATE_LEVEL_ARRAY[position];
    }

    /**
     * 选的越高文件质量越大，质量越好
     */
    public static final int[] ENCODING_BITRATE_LEVEL_ARRAY = {
            500 * 1000,
            800 * 1000,
            1000 * 1000,
            1200 * 1000,
            1600 * 1000,
            2000 * 1000,
            2500 * 1000,
            4000 * 1000,
            8000 * 1000,
    };
}
