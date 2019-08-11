package com.shuangpin.rich.ui.activity.mine.setting;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.dialog.RuntMMAlert;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.ImageLoaderOptions;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PhoneUtils;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditPersonalDataActivity extends BaseActivity {

    private Context mContext;

    private static final String TAG = "PersonalDataActivity";
    @InjectView(R.id.iv_header)
    ImageView ivHeader;//头像 图片

    @InjectView(R.id.tv_setting_nickname)
    TextView nickname;//头像昵称
    @InjectView(R.id.line_top)
    RelativeLayout lineTop;//头像修改相对布局
    @InjectView(R.id.rl_nick_name)
    RelativeLayout rl_nick_name;//昵称修改相对布局

    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final String PHOTO_FILE_NAME = "idCard.jpg";
    private Uri uritempFile;
    private String shopImagePath = "";
    private static final int REQUEST_GET_BITMAP = 128;
    private OSSClient oss;
    private String auther;
    private String token;
    private String headImg;
    private String nickname1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_data);
        setTitleBar(SHOW_LEFT);
        mContext = EditPersonalDataActivity.this;
        StatusBarUtil.setStatusBar(EditPersonalDataActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        auther = PrefUtils.readUid(mContext);

        token = PrefUtils.readToken(mContext);
        getOssConfig();

        Intent intent = getIntent();
        headImg = intent.getStringExtra("headImg");
        nickname1 = intent.getStringExtra("nickname");
        nickname.setText(nickname1);

        ImageLoader.getInstance().displayImage(headImg, ivHeader, ImageLoaderOptions.fadein_options);
        lineTop.setOnClickListener(this);
        rl_nick_name.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.line_top:
                chooseCamera();

                break;
            case R.id.rl_nick_name:
               Intent mIntent = new Intent(mContext, NickNameInputActivity.class);
                mIntent.putExtra("title", "修改昵称");
                startActivityForResult(mIntent,1);
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
                    if (PhoneUtils.cameraPermissions(EditPersonalDataActivity.this)) {
                        getPicFromPhoto();
                    }

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
        if (PhoneUtils.cameraPermissions(EditPersonalDataActivity.this)) {
            startCamera();
        }
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

        if(data!=null){
            if(requestCode==1){
                String returnMsg = data.getStringExtra("returnMsg");
                if(!TextUtils.isEmpty(returnMsg)){
                    nickname.setText(returnMsg);
                }
            }

        }
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
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    ivHeader.setImageBitmap(bitmap);
                    shopImagePath = saveImageToGallery(mContext, bitmap);


                    final RuntCustomProgressDialog runtDialog = new RuntCustomProgressDialog(mContext);
                    runtDialog.setMessage("正在上传");
                    runtDialog.show();
                    //                    user/headimg
                    String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + shopImagePath;
                    final String OssPath = "user/headimg/" + auther + "/" + shopImagePath;
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
                                    .add("headImg", url)

                                    .build();
                            Request request = new Request.Builder()
                                    .url(HttpsApi.SERVER_URL + HttpsApi.URL_EDIT_IMG)
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
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtilsxp.e2(TAG, "保存图片错误:" + e.getMessage().toString());
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
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
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
        //        if (appDir.exists()) {
        //            appDir.delete();
        //        }
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

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
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
}
