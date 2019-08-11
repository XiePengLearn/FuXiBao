package com.shuangpin.rich.ui.activity.guide;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shuangpin.R;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.splash.SplashActivity;
import com.shuangpin.rich.util.DensityUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.utilsbar.XpStatusBarUtil;

import java.util.ArrayList;

public class GuideActivity extends Activity implements View.OnClickListener {

    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};

//    // 要申请的权限
//    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAMERA,
//            Manifest.permission.CAMERA, Manifest.permission.CAMERA};
    private AlertDialog dialog;


    protected static final String TAG = GuideActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private ArrayList<ImageView> mImageList;// 引导页的ImageView集合
    private LinearLayout llPointGroup;// 点的集合
    private View viewRedPoint;// 红点
    private int mPointWidth; // 两点间距
    private Button btnStart;// 开始体验

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除标题,
        //        // 必须在setContentView之前调用
        setContentView(R.layout.activity_guide);
//        StatusBarUtil.setStatusBar(this, R.color.theme_color_title_them);

        XpStatusBarUtil.setImmersiveStatusBar(this,true);
        initViews();
        //启动页面播放鸟叫的声音
        //        PhoneUtils.cameraPermissions(GuideActivity.this);//请求拍照和储存权限


        //         版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            int i1 = ContextCompat.checkSelfPermission(this, permissions[1]);
            int i2 = ContextCompat.checkSelfPermission(this, permissions[2]);
            int i3 = ContextCompat.checkSelfPermission(this, permissions[3]);
            int i4 = ContextCompat.checkSelfPermission(this, permissions[4]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || i1 != PackageManager.PERMISSION_GRANTED
                    || i2 != PackageManager.PERMISSION_GRANTED || i3 != PackageManager.PERMISSION_GRANTED|| i4 != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        llPointGroup = (LinearLayout) findViewById(R.id.ll_point_group);
        viewRedPoint = findViewById(R.id.view_red_point);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        initData();
        mViewPager.setAdapter(new GuideAdapter());

        // measure -> layout -> draw
        // 获得视图树观察者, 观察当整个布局的layout时的事件
        viewRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    // 完成布局后会回调改方法, 改方法可能会被回调多次
                    @Override
                    public void onGlobalLayout() {
                        // 此方法只需要执行一次就可以: 把当前的监听事件从视图树中移除掉, 以后就不会在回调此事件了.
                        viewRedPoint.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);

                        mPointWidth = llPointGroup.getChildAt(1).getLeft()
                                - llPointGroup.getChildAt(0).getLeft();

                        System.out.println("间距: " + mPointWidth);
                    }
                });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // 页面选中
            @Override
            public void onPageSelected(int position) {
                // Log.d(TAG, "onPageSelected:" + position);
                if (position == mImageList.size() - 1) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            /**
             * 页面滑动监听
             *
             * @params position 当前选中的位置
             * @params positionOffset 偏移百分比
             * @params positionOffsetPixels 页面偏移长度
             */
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                int leftMargin = (int) (mPointWidth * (positionOffset + position));
                // Log.d(TAG, "当前位置:" + position + ";偏移比例:" + positionOffset
                // + ";点偏移:" + leftMargin);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewRedPoint
                        .getLayoutParams();
                lp.leftMargin = leftMargin;
                viewRedPoint.setLayoutParams(lp);
            }

            // 状态改变
            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.d(TAG, "onPageScrollStateChanged:" + state);
            }
        });
    }

    // 初始化ViewPager的数据
    private void initData() {
        int[] imageResIDs = {R.drawable.guide_1, R.drawable.guide_2,
                R.drawable.guide_3};

        mImageList = new ArrayList<ImageView>();
        for (int i = 0; i < imageResIDs.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(imageResIDs[i]);// 注意设置背景, 才可以填充屏幕
            mImageList.add(image);

            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_guide_point_default);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DensityUtils.dp2px(this, 10), DensityUtils.dp2px(this, 10));

            if (i != 0) {
                params.leftMargin = DensityUtils.dp2px(this, 10);
            }

            point.setLayoutParams(params);
            llPointGroup.addView(point);
        }
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageList.get(position));
            return mImageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                // 开始体验
                PrefUtils.putBoolean(this, SplashActivity.PREF_IS_USER_GUIDE_SHOWED, true);// 记录已经展现过了新手引导页
                PrefUtils.putBoolean(this, SplashActivity.PREF_IS_ENTER_LOG_OR_MAIN, false);
                // 记录已经展现过了新手引导页,为false就是判断进入主页面还是登录页面,为true就是进入登录界面

                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("title", "登录");
                startActivity(intent);

                finish();

                break;

            default:
                break;
        }
    }


    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("需要定位/存储/相机权限，为你定位与存储信息；\n否则，您将无法正常使用富硒宝")
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

                    LogUtilsxp.e2(TAG,"xp0:----");
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    LogUtilsxp.e2(TAG,"xp1:----");
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    LogUtilsxp.e2(TAG,"xp2:----");
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[2]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    LogUtilsxp.e2(TAG,"xp3:----");
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[3]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                }else if (grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                    LogUtilsxp.e2(TAG,"xp3:----");
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[4]);
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
                int i4 = ContextCompat.checkSelfPermission(this, permissions[4]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i == PackageManager.PERMISSION_GRANTED && i1 == PackageManager.PERMISSION_GRANTED &&
                        i2 == PackageManager.PERMISSION_GRANTED && i3 == PackageManager.PERMISSION_GRANTED&& i4 == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                } else {


                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                }

                //                if (i1 != PackageManager.PERMISSION_GRANTED) {
                //                    // 提示用户应该去应用设置界面手动开启权限
                //                    showDialogTipUserGoToAppSettting();
                //                } else {
                //                    if (dialog != null && dialog.isShowing()) {
                //                        dialog.dismiss();
                //                    }
                //                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                //                }
                //
                //                if (i2 != PackageManager.PERMISSION_GRANTED) {
                //                    // 提示用户应该去应用设置界面手动开启权限
                //                    showDialogTipUserGoToAppSettting();
                //                } else {
                //                    if (dialog != null && dialog.isShowing()) {
                //                        dialog.dismiss();
                //                    }
                //                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                //                }
            }
        }
    }
}
