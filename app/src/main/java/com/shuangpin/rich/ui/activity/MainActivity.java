package com.shuangpin.rich.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.shuangpin.R;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.BindPhoneActivity;
import com.shuangpin.rich.ui.fragment.FineFragment;
import com.shuangpin.rich.ui.fragment.FragmentInstanceManager;
import com.shuangpin.rich.ui.fragment.HomeFragment;
import com.shuangpin.rich.ui.fragment.MineFragment;
import com.shuangpin.rich.ui.fragment.StoreFragment;
import com.shuangpin.rich.ui.fragment.TopLineFragment;
import com.shuangpin.rich.ui.html.HtmlStoreActivity;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};

    //    // 要申请的权限
    //    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAMERA,
    //            Manifest.permission.CAMERA, Manifest.permission.CAMERA};
    private AlertDialog dialog;

    private static final String TAG = "MainActivity";
    @InjectView(R.id.rg_content_fragment)
    RadioGroup mRadioGroup;

    @InjectView(R.id.rb_content_fragment_shopping_button)
    Button shoppingButton;
    private FragmentManager mFragmentManager;
    private SoundPool soundPoolOpenApp;//声明一个SoundPool
    private int soundIDOpenApp;//创建某个声音对应的音频ID
    private Context mContext;

    private int mCurrentVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mContext = MainActivity.this;

        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            // 版本号
            mCurrentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        isCheckMethod();

        //         版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            int i1 = ContextCompat.checkSelfPermission(this, permissions[1]);
            int i2 = ContextCompat.checkSelfPermission(this, permissions[2]);
            int i3 = ContextCompat.checkSelfPermission(this, permissions[3]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || i1 != PackageManager.PERMISSION_GRANTED
                    || i2 != PackageManager.PERMISSION_GRANTED || i3 != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }


        mFragmentManager = getSupportFragmentManager();

        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.rb_content_fragment_home);


        shoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                String mUrl = HttpsApi.LOGIN_STORE;
                //                Intent mIntent = new Intent(MainActivity.this, HtmlStoreActivity.class);
                //                mIntent.putExtra("title", " ");
                //                mIntent.putExtra("url", mUrl);
                //
                //                startActivity(mIntent);


                String isPhone = PrefUtils.readIsPhone(MainActivity.this);//0没有绑定手机号  1已经绑定手机号

                LogUtilsxp.e2(TAG, "isPhone:" + isPhone);
                if ("1".equals(isPhone)) {

                    String phone = PrefUtils.readPhone(MainActivity.this);
                    LogUtilsxp.e2(TAG, "phone:" + phone);
                                    /*$time = $data['time'];
                                    $sign = $data['sign'];
                                    $str = $uid.'_'.$data['shopId'].'_'.$time.'_'.$key.'_'.$data['city'];
                                    $str = MD5(MD5($str));*/

                    //获取当前的秒值
                    long time = System.currentTimeMillis() / 1000;
                    String encryptionString = phone + "_" + time + "_" + "pmku91rutSccEQPpsrPgi5ovHzlLrhpl" + "_" + phone;
                    String encStr1 = CommonUtil.md5(encryptionString) + "pmku91rutSccEQPpsrPgi5ovHzlLrhpl";
                    String encStr = CommonUtil.md5(encStr1);


                    CustomTrust customTrust = new CustomTrust(mContext);
                    OkHttpClient okHttpClient = customTrust.client;

                    final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(MainActivity.this);
                    dialog.setMessage("正在加载数据···");
                    dialog.show();


                    String token = PrefUtils.readToken(mContext);
                    RequestBody requestBody = new FormBody.Builder()
                            .add("mobile", phone)
                            .add("password", phone)
                            .add("time", time + "")
                            .add("type", encStr)
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.LOGIN_REGISTER)
                            //                .addHeader("Authorization", token)
                            .post(requestBody)
                            .build();


                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            dialog.dismiss();
                            LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseString = response.body().string();
                            dialog.dismiss();
                            LogUtilsxp.e2(TAG, "URL_IS_CHECK_result:" + responseString);
                            CommonUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {

                                    //{"code":0,"data":{"isCheck":1},"msg":"成功"}  //1审核  0不审核
                                    try {
                                        JSONObject object = new JSONObject(responseString);
                                        String resultcode = object.optString("status");
                                        String info = object.optString("info");
                                        if (resultcode.equals("0")) {
                                            JSONObject jSONObject = object.optJSONObject("data");

                                            //                                    "token": "ATQBMw0yDj8GZ1EzAzADYQFsXT1XbQ==",
                                            //                                            "url": "http:\/\/pickshop.shuangpinkeji.com\/mobile\/index.php?m=user&c=login&a=check"
                                            String url = jSONObject.optString("url");
                                            String token = jSONObject.optString("token");


                                            String mUrl = url + "&token=" + token;
                                            Intent mIntent = new Intent(MainActivity.this, HtmlStoreActivity.class);
                                            mIntent.putExtra("title", " ");
                                            mIntent.putExtra("url", mUrl);

                                            startActivity(mIntent);

                                        } else {
                                            ToastUtils.showToast(MainActivity.this, info);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    });


                } else {

                    //没有绑定手机号,需要
                    Intent mIntent = new Intent(MainActivity.this, BindPhoneActivity.class);
                    mIntent.putExtra("title", "绑定手机号");

                    startActivity(mIntent);
                }


            }
        });
    }

    private void isCheckMethod() {


        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mContext);
        RequestBody requestBody = new FormBody.Builder()
                .add("version", mCurrentVersionCode + "")
                .add("type", "android")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_IS_CHECK)
                //                .addHeader("Authorization", token)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();

                LogUtilsxp.e2(TAG, "URL_IS_CHECK_result:" + responseString);
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        //{"code":0,"data":{"isCheck":1},"msg":"成功"}  //1审核  0不审核
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultcode = object.optInt("result");
                            String msg = object.optString("msg");
                            if (resultcode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                String isCheck = data.optString("isCheck");

                                if (isCheck.equals("1")) {
                                    //审核期间
                                    shoppingButton.setVisibility(View.VISIBLE);

                                } else {
                                    shoppingButton.setVisibility(View.VISIBLE);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    //切换到底部导航的Fragment
    public void switchNavigationFragment(int checkedId) {
        mRadioGroup.check(checkedId);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_content_fragment_home:
                switchFragment(FragmentInstanceManager.getInstance().getFragment(HomeFragment.class));
                //                switchFragment(FragmentInstanceManager.getInstance().getFragment(TwoPagerFragment.class));
                break;
            case R.id.rb_content_fragment_search:
                switchFragment(FragmentInstanceManager.getInstance().getFragment(TopLineFragment.class));
                break;

            case R.id.rb_content_fragment_shopping:
                switchFragment(FragmentInstanceManager.getInstance().getFragment(StoreFragment.class));
                break;

            case R.id.rb_content_fragment_brand:
                switchFragment(FragmentInstanceManager.getInstance().getFragment(FineFragment.class));
                break;

            case R.id.rb_content_fragment_more:
                switchFragment(FragmentInstanceManager.getInstance().getFragment(MineFragment.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String page = intent.getStringExtra("page");
        if (!TextUtils.isEmpty(page)) {
            if (page.equals("1")) {
                //                mRadioGroup.check(R.id.rb_mine);
                //                mPagerList.get(4).initData();// 初始化我的页面的数据
                mRadioGroup.check(R.id.rb_content_fragment_search);
            }
        }
    }

    //提供方法切换Fragment，点击RadioButton的时候需要清空回退栈
    public void switchFragment(Fragment fragment) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //循环的的pop回退栈
        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        while (backStackEntryCount > 0) {
            mFragmentManager.popBackStack();
            backStackEntryCount--;
        }

        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
    }

    private long timeMillis;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                timeMillis = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("由于富硒宝需要获取定位/存储空间/相机权限，为你定位与存储个人信息；\n否则，您将无法正常使用科然e品")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();

        //        startRequestPermission();
    }


    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[2]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[3]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                }
            }
        }
    }


    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限-中，允许富硒宝使用定位/存储空间/相机权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                int i1 = ContextCompat.checkSelfPermission(this, permissions[1]);
                int i2 = ContextCompat.checkSelfPermission(this, permissions[2]);
                int i3 = ContextCompat.checkSelfPermission(this, permissions[3]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i == PackageManager.PERMISSION_GRANTED && i1 == PackageManager.PERMISSION_GRANTED &&
                        i2 == PackageManager.PERMISSION_GRANTED && i3 == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                } else {


                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                }


            }
        }
    }


}
