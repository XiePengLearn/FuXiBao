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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

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
import com.shuangpin.multi.view.MultiImageSelectorActivity;
import com.shuangpin.rich.adapter.ProducGridAdapter;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.dialog.RuntMMAlert;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModificationADActivity extends BaseActivity {


    private static final String TAG = "IndividualMerchantsAdvertiseActivity";
    private Context mContext;
    @InjectView(R.id.no_scroll_grid_view_publish)
    GridView noScrollgridview;//资质的gridView

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
    private List<String> desImgList;
    private List<String> desGoodsImgList;
    private List<String> desGoodsNameList;
    private String shopId;
    private String product;
    private String imgJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_ad);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(ModificationADActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);
        mContext = ModificationADActivity.this;
        nextData.setVisibility(View.VISIBLE);

        Intent mIntent = getIntent();
        shopId = mIntent.getStringExtra("shopId");

        desImgList = new ArrayList<>();
        desGoodsImgList = new ArrayList<>();
        desGoodsNameList = new ArrayList<>();
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
    }


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
                postToOss();

                break;
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
        Intent intent = new Intent(ModificationADActivity.this, MultiImageSelectorActivity.class);
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));

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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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

                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
                    Bitmap bitmap = getFitSampleBitmap(getContentResolver().openInputStream(uritempFile));
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
        if (imageSize == 0) {
            String shopDescribe = publishDescribe.getText().toString();
            CustomTrust customTrust = new CustomTrust(mContext);
            OkHttpClient okHttpClient = customTrust.client;


            RequestBody requestBody = new FormBody.Builder()
                    .add("shopId", shopId)
                    .add("info", shopDescribe)
                    .add("img", "[]")
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

        }
        if (initial < imageSize) {
            String imagePage = Environment.getExternalStorageDirectory().getPath() + "/" + "putBird" + "/" + aptitudePatnList.get(initial);
            //imagePage 本地上传图片的地址
            String fileName = aptitudePatnList.get(initial);


            final String OssPath = "arrive/" + auther + "/" + fileName;
            PutObjectRequest put = new PutObjectRequest(HttpsApi.OSS_OSSBUCKET, OssPath, imagePage);
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
                    if (initial == aptitudePatnList.size()) {
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


}
