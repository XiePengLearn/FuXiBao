package com.shuangpin.rich.ui.activity.find;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.video.FullyGridLayoutManager;
import com.shuangpin.rich.ui.activity.video.GridImageAdapter;
import com.shuangpin.rich.ui.activity.video.PhotoPopupWindowXp;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

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

public class PutDynamicStateActivity extends BaseActivity implements PhotoPopupWindowXp.OnItemClickListener {


    private List<LocalMedia> selectList = new ArrayList<>();
    private int maxSelectNum = 1;

    private static final String TAG = "PutDynamicStateActivity";
    private Context mContext;
    //    @InjectView(R.id.noScrollgridview)
    //    GridView noScrollgridview;//资质的gridView
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;//资质的gridView

    @InjectView(R.id.btn_next_data)
    Button nextData;//放鸟喽按钮

    @InjectView(R.id.et_you_want_to_tell_everyone)
    EditText youWantTellEveryone;//你想对 大家说
    @InjectView(R.id.et_url)
    EditText et_url;//网址链接

    @InjectView(R.id.ll_parent)
    LinearLayout ll_parent;//跟布局

    @InjectView(R.id.iv_add_pic_video)
    ImageView ivAddPicVideo;//添加照片或者视频
    private OSSClient oss;
    private String auther;
    private String token;
    private int initial;

    private RuntCustomProgressDialog runtDialog;
    private List<String> desImgList;
    private String imgJsonString;
    private GridImageAdapter adapterView;
    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();
    private int aspect_ratio_x = 1, aspect_ratio_y = 1;
    private PhotoPopupWindowXp popupWindow;
    private String imgType;

    private PLMediaFile mMediaFile;
    private String mUrlwangzhi;
    private String mShopUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_dynamic_state);
        themeId = R.style.picture_default_style;
        setTitleBar(SHOW_LEFT);
        mContext = PutDynamicStateActivity.this;
        StatusBarUtil.setStatusBar(PutDynamicStateActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);

        desImgList = new ArrayList<>();


        initial = 0;
        auther = PrefUtils.readUid(mContext);
        token = PrefUtils.readToken(mContext);


        mShopUrl = getIntent().getStringExtra("shopUrl");

        if(!TextUtils.isEmpty(mShopUrl)){
            et_url.setVisibility(View.VISIBLE);
            et_url.setText(mShopUrl);
            et_url.setFocusable(false);

            et_url.setFocusableInTouchMode(false);
        }else {
            et_url.setVisibility(View.GONE);
        }
        //上传 图片到Oss
        getOssConfig();

        nextData.setOnClickListener(this);
        ivAddPicVideo.setOnClickListener(this);


        FullyGridLayoutManager manager = new FullyGridLayoutManager(PutDynamicStateActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapterView = new GridImageAdapter(PutDynamicStateActivity.this, onAddPicClickListener);
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
                            PictureSelector.create(PutDynamicStateActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PutDynamicStateActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PutDynamicStateActivity.this).externalPictureAudio(media.getPath());
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
                    PictureFileUtils.deleteCacheDirFile(PutDynamicStateActivity.this);
                } else {
                    Toast.makeText(PutDynamicStateActivity.this,
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
                popupWindow = new PhotoPopupWindowXp(PutDynamicStateActivity.this);
                popupWindow.setOnItemClickListener(PutDynamicStateActivity.this);
                popupWindow.showAsDropDown(ll_parent);
            } else {
                PictureSelector.create(PutDynamicStateActivity.this)
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


                runtDialog = new RuntCustomProgressDialog(mContext);
                runtDialog.setMessage("正在上传");
                runtDialog.show();

                postToOss();


                break;
            case R.id.left_btn:
                this.finish();


                break;

            //添加照片或者视频
            case R.id.iv_add_pic_video:
                popupWindow = new PhotoPopupWindowXp(this);
                popupWindow.setOnItemClickListener(this);
                popupWindow.showAsDropDown(ll_parent);
                break;


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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


        if (imageSize == 0) {
            CustomTrust customTrust = new CustomTrust(mContext);
            OkHttpClient okHttpClient = customTrust.client;


            RequestBody requestBody = new FormBody.Builder()
                    .add("info", putBirdDescribe)//描述
                    .add("img", "[]")//图片 {[herf:http://asdf.com]}
                    .add("imgType", "0")//没有图片和视频
                    .add("url", mUrlwangzhi)//网址链接

                    .build();
            Request request = new Request.Builder()
                    .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND)
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
                                    ToastUtils.showToast(mContext, "发布成功");
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

                mMediaFile = new PLMediaFile(picturePath);

                int videoWidthRaw = mMediaFile.getVideoWidth();//宽度
                int videoHeightRaw = mMediaFile.getVideoHeight();//高度
                int videoFrameRate = mMediaFile.getVideoFrameRate();//帧率


                LogUtilsxp.e2(TAG,"videoWidthRaw:----"+videoWidthRaw+"videoHeightRaw:----"+videoHeightRaw+"videoFrameRate:---"+videoFrameRate);

                mShortVideoTranscoder.setMaxFrameRate(videoFrameRate);

//                MediaMetadataRetriever retr = new MediaMetadataRetriever();
//                retr.setDataSource(picturePath);
//                String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
//                String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
//                String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
                int transcodingBitrateLevel = 4;//我这里选择的 1600 * 1000,压缩，这里可以自己选择合适的压缩比例
                mShortVideoTranscoder.transcode(videoWidthRaw, videoHeightRaw, getEncodingBitrateLevel(transcodingBitrateLevel),
                        false, new PLVideoSaveListener() {
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
                            .add("info", putBirdDescribe)//描述
                            .add("img", imgJsonString)//图片 {[herf:http://asdf.com]}
                            .add("imgType", imgType)//
                            .add("url", mUrlwangzhi)//网址链接
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_SEND)
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
                                            ToastUtils.showToast(mContext, "发布成功");
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
                PictureSelector.create(PutDynamicStateActivity.this)
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
                PictureSelector.create(PutDynamicStateActivity.this)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaFile != null) {
            mMediaFile.release();
        }
    }
}
