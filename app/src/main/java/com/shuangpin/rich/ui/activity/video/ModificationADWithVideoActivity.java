package com.shuangpin.rich.ui.activity.video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.shuangpin.R;
import com.shuangpin.rich.adapter.ProducGridAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.dialog.RuntMMAlert;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.mine.business.SelectTheScopeAndPayActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import static com.luck.picture.lib.config.PictureConfig.CHOOSE_REQUEST;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;

public class ModificationADWithVideoActivity extends BaseActivity {

    private static final String TAG = "IndividualMerchantsWithVideoActivity";
    private Context mContext;
    //    @InjectView(R.id.no_scroll_grid_view_publish)
    //    GridView noScrollgridview;//资质的gridView

    @InjectView(R.id.recycler1)
    RecyclerView recyclerView1;//资质的gridView视频
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;//资质的gridView图片
    @InjectView(R.id.btn_next_data_publish)
    Button nextData;//发布
    @InjectView(R.id.et_publish_describe)
    EditText publishDescribe;//发布描述

    @InjectView(R.id.et_publish_product_name1)
    EditText productName1;//发布商品1
    @InjectView(R.id.iv_publish_image_name1)
    ImageView imageName1;//发布商品图片1

    @InjectView(R.id.et_publish_product_name2)
    EditText productName2;//发布商品2
    @InjectView(R.id.iv_publish_image_name2)
    ImageView imageName2;//发布商品图片2

    @InjectView(R.id.et_publish_product_name3)
    EditText productName3;//发布商品3
    @InjectView(R.id.iv_publish_image_name3)
    ImageView imageName3;//发布商品图片3

    @InjectView(R.id.et_publish_product_name4)
    EditText productName4;//发布商品4
    @InjectView(R.id.iv_publish_image_name4)
    ImageView imageName4;//发布商品图片4

    @InjectView(R.id.et_publish_product_name5)
    EditText productName5;//发布商品5
    @InjectView(R.id.iv_publish_image_name5)
    ImageView imageName5;//发布商品图片5

    @InjectView(R.id.et_publish_product_name6)
    EditText productName6;//发布商品6
    @InjectView(R.id.iv_publish_image_name6)
    ImageView imageName6;//发布商品图片6

    @InjectView(R.id.et_publish_product_name7)
    EditText productName7;//发布商品7
    @InjectView(R.id.iv_publish_image_name7)
    ImageView imageName7;//发布商品图片7

    @InjectView(R.id.et_publish_product_name8)
    EditText productName8;//发布商品8
    @InjectView(R.id.iv_publish_image_name8)
    ImageView imageName8;//发布商品图片8

    @InjectView(R.id.et_publish_product_name9)
    EditText productName9;//发布商品9
    @InjectView(R.id.iv_publish_image_name9)
    ImageView imageName9;//发布商品图片9

    @InjectView(R.id.et_publish_product_name10)
    EditText productName10;//发布商品10
    @InjectView(R.id.iv_publish_image_name10)
    ImageView imageName10;//发布商品图片10

    @InjectView(R.id.et_publish_product_name11)
    EditText productName11;//发布商品11
    @InjectView(R.id.iv_publish_image_name11)
    ImageView imageName11;//发布商品图片11

    @InjectView(R.id.et_publish_product_name12)
    EditText productName12;//发布商品12
    @InjectView(R.id.iv_publish_image_name12)
    ImageView imageName12;//发布商品图片12
    private int tag = 0;//tag = 1 第一件商品


    @InjectView(R.id.ll_parent)
    RelativeLayout ll_parent;//跟布局

    //启动activity 资质拍照的请求码
    private static final int REQUEST_IMAGE = 4;
    //    存放加号的list
    List<Bitmap> jiaListBitMap;

    private ArrayList<String> mSelectPath;
    private ProducGridAdapter adapter;
    private OSSClient oss;
    private String auther;
    private String token;
    private int initial;


    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int REQUEST_GET_BITMAP1 = 121;
    private static final int REQUEST_GET_BITMAP2 = 122;
    private static final int REQUEST_GET_BITMAP3 = 123;
    private static final int REQUEST_GET_BITMAP4 = 124;
    private static final int REQUEST_GET_BITMAP5 = 125;
    private static final int REQUEST_GET_BITMAP6 = 126;
    private static final int REQUEST_GET_BITMAP7 = 127;
    private static final int REQUEST_GET_BITMAP8 = 128;
    private static final int REQUEST_GET_BITMAP9 = 129;
    private static final int REQUEST_GET_BITMAP10 = 130;
    private static final int REQUEST_GET_BITMAP11 = 131;
    private static final int REQUEST_GET_BITMAP12 = 132;
    private static final int CHOOSE_REQUESTVideo = 142;

    private static final String PHOTO_FILE_NAME = "dataStore.jpg";
    Uri uritempFile;
    private String ossGoodImgUrl1;
    private String ossGoodImgUrl2;
    private String ossGoodImgUrl3;
    private String ossGoodImgUrl4;
    private String ossGoodImgUrl5;
    private String ossGoodImgUrl6;
    private String ossGoodImgUrl7;
    private String ossGoodImgUrl8;
    private String ossGoodImgUrl9;
    private String ossGoodImgUrl10;
    private String ossGoodImgUrl11;
    private String ossGoodImgUrl12;
    private RuntCustomProgressDialog runtDialog;
    private List<String> desImgList1;
    private List<String> desImgList;
    private List<String> desGoodsImgList;
    private List<String> desGoodsNameList;
    private String shopId;
    private String product;
    private String imgJsonString1;
    private String imgJsonString;

    private GridImageAdapter adapterView;
    private GridImageAdapter adapterView1;
    private int themeId;
    private int chooseMode1 = PictureMimeType.ofVideo();
    private int chooseMode = PictureMimeType.ofImage();
    private int aspect_ratio_x = 1, aspect_ratio_y = 1;
    private String imgType;
    private List<LocalMedia> selectList = new ArrayList<>();
    private List<LocalMedia> selectList1 = new ArrayList<>();
    private int maxSelectNum = 9;
    private int maxSelectNum1 = 1;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_merchants_with_video);
        themeId = R.style.picture_default_style;
        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(ModificationADWithVideoActivity.this, R.color.white);
        ButterKnife.inject(this);
        mContext = ModificationADWithVideoActivity.this;
        nextData.setVisibility(View.VISIBLE);

        Intent mIntent = getIntent();
        shopId = mIntent.getStringExtra("shopId");

        desImgList1 = new ArrayList<>();
        desImgList = new ArrayList<>();
        desGoodsImgList = new ArrayList<>();
        desGoodsNameList = new ArrayList<>();
        initial = 0;
        auther = PrefUtils.readUid(mContext);
        token = PrefUtils.readToken(mContext);


        initView();


        //上传 图片到Oss
        getOssConfig();

        nextData.setOnClickListener(this);
        imageName1.setOnClickListener(this);
        imageName2.setOnClickListener(this);
        imageName3.setOnClickListener(this);
        imageName4.setOnClickListener(this);
        imageName5.setOnClickListener(this);
        imageName6.setOnClickListener(this);
        imageName7.setOnClickListener(this);
        imageName8.setOnClickListener(this);
        imageName9.setOnClickListener(this);
        imageName10.setOnClickListener(this);
        imageName11.setOnClickListener(this);
        imageName12.setOnClickListener(this);

        //视频
        FullyGridLayoutManager manager1 = new FullyGridLayoutManager(ModificationADWithVideoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(manager1);
        adapterView1 = new GridImageAdapter(ModificationADWithVideoActivity.this, onAddPicClickListener1);
        adapterView1.setList(selectList1);
        adapterView1.setSelectMax(maxSelectNum1);
        recyclerView1.setAdapter(adapterView1);
        adapterView1.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList1.size() > 0) {
                    LocalMedia media = selectList1.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(ModificationADWithVideoActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(ModificationADWithVideoActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(ModificationADWithVideoActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }

            }
        });

        //图片
        FullyGridLayoutManager manager = new FullyGridLayoutManager(ModificationADWithVideoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapterView = new GridImageAdapter(ModificationADWithVideoActivity.this, onAddPicClickListener);
        adapterView.setList(selectList);
        adapterView.setSelectMax(maxSelectNum);
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
                            PictureSelector.create(ModificationADWithVideoActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(ModificationADWithVideoActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(ModificationADWithVideoActivity.this).externalPictureAudio(media.getPath());
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
                    PictureFileUtils.deleteCacheDirFile(ModificationADWithVideoActivity.this);
                } else {
                    Toast.makeText(ModificationADWithVideoActivity.this,
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
    //视频

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener1 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            PictureSelector.create(ModificationADWithVideoActivity.this)
                    .openGallery(chooseMode1)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
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
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    .recordVideoSecond(10)//录制视频秒数 默认60s
                    .forResult(CHOOSE_REQUESTVideo);//结果回调onActivityResult code
        }


    };

    //图片

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            PictureSelector.create(ModificationADWithVideoActivity.this)
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
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    .recordVideoSecond(10)//录制视频秒数 默认60s
                    .forResult(CHOOSE_REQUEST);//结果回调onActivityResult code
        }


    };


    @Override
    public void onClick(View v) {
        Intent mIntent = null;

        switch (v.getId()) {
            //升级
            case R.id.btn_next_data_publish:
                initial = 0;


                String shopDescribe = publishDescribe.getText().toString();
                if (TextUtils.isEmpty(shopDescribe)) {
                    ToastUtils.showToast(mContext, "请输入您想对大家说的话");
                    return;

                }


                String goodName1 = productName1.getText().toString().trim();
                String goodName2 = productName2.getText().toString().trim();
                String goodName3 = productName3.getText().toString().trim();
                String goodName4 = productName4.getText().toString().trim();
                String goodName5 = productName5.getText().toString().trim();
                String goodName6 = productName6.getText().toString().trim();
                String goodName7 = productName7.getText().toString().trim();
                String goodName8 = productName8.getText().toString().trim();
                String goodName9 = productName9.getText().toString().trim();
                String goodName10 = productName10.getText().toString().trim();
                String goodName11 = productName11.getText().toString().trim();
                String goodName12 = productName12.getText().toString().trim();

                if (!TextUtils.isEmpty(ossGoodImgUrl1)) {
                    desGoodsImgList.add(ossGoodImgUrl1);
                    desGoodsNameList.add(goodName1);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl2)) {
                    desGoodsImgList.add(ossGoodImgUrl2);
                    desGoodsNameList.add(goodName2);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl3)) {
                    desGoodsImgList.add(ossGoodImgUrl3);
                    desGoodsNameList.add(goodName3);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl4)) {
                    desGoodsImgList.add(ossGoodImgUrl4);
                    desGoodsNameList.add(goodName4);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl5)) {
                    desGoodsImgList.add(ossGoodImgUrl5);
                    desGoodsNameList.add(goodName5);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl6)) {
                    desGoodsImgList.add(ossGoodImgUrl6);
                    desGoodsNameList.add(goodName6);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl7)) {
                    desGoodsImgList.add(ossGoodImgUrl7);
                    desGoodsNameList.add(goodName7);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl8)) {
                    desGoodsImgList.add(ossGoodImgUrl8);
                    desGoodsNameList.add(goodName8);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl9)) {
                    desGoodsImgList.add(ossGoodImgUrl9);
                    desGoodsNameList.add(goodName9);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl10)) {
                    desGoodsImgList.add(ossGoodImgUrl10);
                    desGoodsNameList.add(goodName10);
                }

                if (!TextUtils.isEmpty(ossGoodImgUrl11)) {
                    desGoodsImgList.add(ossGoodImgUrl11);
                    desGoodsNameList.add(goodName11);
                }
                if (!TextUtils.isEmpty(ossGoodImgUrl12)) {
                    desGoodsImgList.add(ossGoodImgUrl12);
                    desGoodsNameList.add(goodName12);
                }


                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject tmpObj = null;
                    int count = desGoodsImgList.size();
                    for (int i = 0; i < count; i++) {
                        tmpObj = new JSONObject();
                        tmpObj.put("name", desGoodsNameList.get(i));
                        tmpObj.put("img", desGoodsImgList.get(i));
                        jsonArray.put(tmpObj);
                        tmpObj = null;
                    }
                    // 将JSONArray转换得到String
                    product = jsonArray.toString();
                    LogUtilsxp.e2(TAG, "product:" + product);

                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

                runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("正在上传");
                runtDialog.show();
                if (selectList1.size() == 0) {
                    //没有选择视频
                    imgJsonString1 = "";
                    postToOss();
                }else {
                    //选择了视频
                    postToOss();
                }

                break;
            //添加照片或者视频

            case R.id.iv_publish_image_name1:
                tag = 1;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name2:
                tag = 2;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name3:
                tag = 3;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name4:
                tag = 4;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name5:
                tag = 5;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name6:
                tag = 6;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name7:
                tag = 7;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name8:
                tag = 8;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name9:
                tag = 9;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name10:
                tag = 10;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name11:
                tag = 11;
                chooseCamera();
                break;
            case R.id.iv_publish_image_name12:
                tag = 12;
                chooseCamera();
                break;

            case R.id.left_btn:
                this.finish();
                break;
        }
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
        startCamera();
    }

    // 拍照后的保存路径
    private void startCamera() {
        Intent intentStartCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intentStartCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        startActivityForResult(intentStartCamera, CAMERA_REQUEST);
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
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + tag + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        if (tag == 1) {
            startActivityForResult(intent, REQUEST_GET_BITMAP1);
        } else if (tag == 2) {
            startActivityForResult(intent, REQUEST_GET_BITMAP2);
        } else if (tag == 3) {
            startActivityForResult(intent, REQUEST_GET_BITMAP3);
        } else if (tag == 4) {
            startActivityForResult(intent, REQUEST_GET_BITMAP4);
        } else if (tag == 5) {
            startActivityForResult(intent, REQUEST_GET_BITMAP5);
        } else if (tag == 6) {
            startActivityForResult(intent, REQUEST_GET_BITMAP6);
        } else if (tag == 7) {
            startActivityForResult(intent, REQUEST_GET_BITMAP7);
        } else if (tag == 8) {
            startActivityForResult(intent, REQUEST_GET_BITMAP8);
        } else if (tag == 9) {
            startActivityForResult(intent, REQUEST_GET_BITMAP9);
        } else if (tag == 10) {
            startActivityForResult(intent, REQUEST_GET_BITMAP10);
        } else if (tag == 11) {
            startActivityForResult(intent, REQUEST_GET_BITMAP11);
        } else if (tag == 12) {
            startActivityForResult(intent, REQUEST_GET_BITMAP12);
        }
    }


    /**
     * 初始化控件
     */
    private void initView() {

        //      放+的list
        jiaListBitMap = new ArrayList<Bitmap>();


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

            case REQUEST_GET_BITMAP1:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));

                    imageName1.setImageBitmap(bitmap);
                    String imageName1Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName1Path;
                    postToOssGoodsImg(imagePage, imageName1Path, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP2:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName2.setImageBitmap(bitmap);
                    String imageName2Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName2Path;
                    postToOssGoodsImg(imagePage, imageName2Path, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP3:
                try {

                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName3.setImageBitmap(bitmap);
                    String imageName3Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName3Path;
                    postToOssGoodsImg(imagePage, imageName3Path, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP4:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName4.setImageBitmap(bitmap);
                    String imageName4Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName4Path;
                    postToOssGoodsImg(imagePage, imageName4Path, 4);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP5:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName5.setImageBitmap(bitmap);
                    String imageName5Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName5Path;
                    postToOssGoodsImg(imagePage, imageName5Path, 5);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP6:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName6.setImageBitmap(bitmap);
                    String imageName6Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName6Path;
                    postToOssGoodsImg(imagePage, imageName6Path, 6);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP7:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName7.setImageBitmap(bitmap);
                    String imageName7Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName7Path;
                    postToOssGoodsImg(imagePage, imageName7Path, 7);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP8:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName8.setImageBitmap(bitmap);
                    String imageName8Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName8Path;
                    postToOssGoodsImg(imagePage, imageName8Path, 8);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP9:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName9.setImageBitmap(bitmap);
                    String imageName9Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName9Path;
                    postToOssGoodsImg(imagePage, imageName9Path, 9);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP10:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName3.setImageBitmap(bitmap);
                    String imageName10Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName10Path;
                    postToOssGoodsImg(imagePage, imageName10Path, 10);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP11:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName11.setImageBitmap(bitmap);
                    String imageName11Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName11Path;
                    postToOssGoodsImg(imagePage, imageName11Path, 11);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_GET_BITMAP12:
                try {
                    bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
                    imageName12.setImageBitmap(bitmap);
                    String imageName12Path = saveImageToGallery(mContext, bitmap);
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + imageName12Path;
                    postToOssGoodsImg(imagePage, imageName12Path, 12);

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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_REQUEST:
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

                    adapterView.setList(selectList);

                    adapterView.notifyDataSetChanged();
                    break;


                case CHOOSE_REQUESTVideo:
                    // 图片选择结果回调
                    selectList1 = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true


                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList1) {
                        LogUtilsxp.e2("图片-----》", media.getPath());
                        if (media.isCompressed()) {
                            LogUtilsxp.e2("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                            LogUtilsxp.e2("压缩地址::", media.getCompressPath());
                        }
                    }

                    adapterView1.setList(selectList1);

                    adapterView1.notifyDataSetChanged();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runtDialog = new RuntCustomProgressDialog(mContext);
                            runtDialog.setMessage("视频正在上传中,请稍等");
                            runtDialog.show();
                            LocalMedia media = selectList1.get(0);

                            //视频
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
                                    postServerMethod1(s, runtDialog);
                                }

                                @Override
                                public void onSaveVideoFailed(final int errorCode) {
                                    runtDialog.dismiss();
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
                                    //                                    runtDialog.dismiss();
                                    //                LogUtil.e("onSaveVideoCanceled");
                                }

                                @Override
                                public void onProgressUpdate(float percentage) {
                                    //                                    runtDialog.dismiss();
                                    //                LogUtil.e("onProgressUpdate==========" + percentage);
                                }
                            });


                        }
                    });


                    break;
            }
        }
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


        int imageSize = selectList.size();

        LogUtilsxp.e2(TAG, "imageSize:" + imageSize);
        if (imageSize == 0) {
            String shopDescribe = publishDescribe.getText().toString();


            CustomTrust customTrust = new CustomTrust(mContext);
            OkHttpClient okHttpClient = customTrust.client;
            //            video	是	json	视频 {[herf:http://asdf.com]}

            RequestBody requestBody = new FormBody.Builder()
                    .add("shopId", shopId)
                    .add("info", shopDescribe)
                    .add("img", "[]")
                    .add("video", imgJsonString1)
                    //                    .add("imgType", "0")//没有图片和视频
                    .add("product", product)

                    .build();
            Request request = new Request.Builder()
                    .url(HttpsApi.SERVER_URL + HttpsApi.URL_PRODUCT)
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

                    LogUtilsxp.e2(TAG, "URL_PRODUCT_result:" + responseString);
                    runtDialog.dismiss();

                    CommonUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {


                            try {
                                JSONObject object = new JSONObject(responseString);
                                int resultCode = object.optInt("code");
                                String msg = object.optString("msg");
                                if (resultCode == 0) {
                                    ToastUtils.showToast(mContext, msg + "请支付");

                                    Intent mIntent = new Intent(ModificationADWithVideoActivity.this, SelectTheScopeAndPayActivity.class);
                                    mIntent.putExtra("title", "支付");
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

    private void postServerMethod1(String picturePath, final RuntCustomProgressDialog runtDialog) {
        final String OssPath = "shop/" + auther + "/" + "android" + picturePath;
        PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, picturePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request1, long currentSize, long totalSize) {
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
                LogUtilsxp.e2(TAG, "returnUrl:" + url);
                desImgList1.add(url);
                runtDialog.dismiss();

                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject tmpObj = null;
                    int count = desImgList1.size();
                    for (int i = 0; i < count; i++) {
                        tmpObj = new JSONObject();
                        tmpObj.put("herf", desImgList1.get(i));
                        jsonArray.put(tmpObj);
                        tmpObj = null;
                    }
                    // 将JSONArray转换得到String
                    imgJsonString1 = jsonArray.toString();
                    LogUtilsxp.e2(TAG, "imgJsonString1:" + imgJsonString1);

                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                runtDialog.dismiss();
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

    private void postServerMethod(String picturePath) {
        final String OssPath = "shop/" + auther + "/" + "android" + picturePath;
        PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, picturePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request1, long currentSize, long totalSize) {
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
                //                    LogUtilsxp.e2(TAG, "returnUrl:" + url);
                desImgList.add(url);
                if (initial == selectList.size()) {
                    runtDialog.dismiss();
                    String shopDescribe = publishDescribe.getText().toString();


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


                    RequestBody requestBody = new FormBody.Builder()
                            .add("shopId", shopId)
                            .add("info", shopDescribe)
                            .add("img", imgJsonString)
                            .add("video", imgJsonString1)
                            .add("product", product)
                            //                            .add("imgType", imgType)//
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_PRODUCT)
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

                            LogUtilsxp.e2(TAG, "URL_PRODUCT_result:" + responseString);
                            runtDialog.dismiss();

                            CommonUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {


                                    try {
                                        JSONObject object = new JSONObject(responseString);
                                        int resultCode = object.optInt("code");
                                        String msg = object.optString("msg");
                                        if (resultCode == 0) {
                                            ToastUtils.showToast(mContext, "修改成功");
                                            finish();

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
                runtDialog.dismiss();
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

    private void postToOssGoodsImg(String path, final String fileName, final int goodImgTag) {
        LogUtilsxp.e2(TAG, "path:" + path);
        LogUtilsxp.e2(TAG, "fileName:" + fileName);


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

                if (goodImgTag == 1) {
                    ossGoodImgUrl1 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl1);
                } else if (goodImgTag == 2) {
                    ossGoodImgUrl2 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl2);
                } else if (goodImgTag == 3) {
                    ossGoodImgUrl3 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl3);
                } else if (goodImgTag == 4) {
                    ossGoodImgUrl4 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl4);
                } else if (goodImgTag == 5) {
                    ossGoodImgUrl5 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl5);
                } else if (goodImgTag == 6) {
                    ossGoodImgUrl6 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl6);
                } else if (goodImgTag == 7) {
                    ossGoodImgUrl7 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl7);
                } else if (goodImgTag == 8) {
                    ossGoodImgUrl8 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl8);
                } else if (goodImgTag == 9) {
                    ossGoodImgUrl9 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl9);
                } else if (goodImgTag == 10) {
                    ossGoodImgUrl10 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl10);
                } else if (goodImgTag == 11) {
                    ossGoodImgUrl11 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl11);
                } else if (goodImgTag == 12) {
                    ossGoodImgUrl12 = getUrl(OssPath);
                    LogUtilsxp.e2(TAG, "returnUrl:" + ossGoodImgUrl12);
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
        task.waitUntilFinished();

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

    public static Bitmap getFitSampleBitmap(InputStream inputStream) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] bytes = readStream(inputStream);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = 2;
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

    @Override
    protected void onStop() {
        super.onStop();
        if (bitmap != null && !bitmap.isRecycled()) {

            bitmap.isRecycled();
            bitmap = null;
        }
        System.gc();
    }
}
