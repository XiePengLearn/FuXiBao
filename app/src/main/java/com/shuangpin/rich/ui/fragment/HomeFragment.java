package com.shuangpin.rich.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.shuangpin.R;
import com.shuangpin.app.MyApplication;
import com.shuangpin.rich.bean.AnnouncementPageBean;
import com.shuangpin.rich.bean.SystemBirdListBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.CashLuckyDrawFragmentDialog;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.service.NotificationUpdateActivity;
import com.shuangpin.rich.service.yypService;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.tool.RiseNumberTextView;
import com.shuangpin.rich.ui.activity.home.AnnouncementDetailsActivity;
import com.shuangpin.rich.ui.activity.home.AnnouncementPageActivity;
import com.shuangpin.rich.ui.activity.home.HowToPlayActivity;
import com.shuangpin.rich.ui.activity.logo.BindPhoneActivity;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.activity.video.PutTheBirdWithVideoActivity;
import com.shuangpin.rich.ui.html.ForTheBirdHtmlActivity;
import com.shuangpin.rich.ui.html.HtmlStorePayActivity;
import com.shuangpin.rich.ui.html.ShopAdHtmlActivity;
import com.shuangpin.rich.ui.widget.NoticeView;
import com.shuangpin.rich.util.CommonUtil;
import com.shuangpin.rich.util.DensityUtils;
import com.shuangpin.rich.util.LogUtilsxp;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    RiseNumberTextView birdMoney;//总金额
    TextView areaAddress;

    NoticeView mNoticeView;//公告
    //    @InjectView(R.id.iv_home_bird)
    //    ImageView homeBird;
    RelativeLayout homeRoot;
    private AnimationDrawable mAnimation;
    private String TAG = "HomeFragment";

    private SoundPool soundPool;//声明一个SoundPool

    private int soundID;//创建某个声音对应的音频ID
    private boolean isFirstEnterPager = true;
    private boolean isCashDraw = true;


    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
    //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    private List<SystemBirdListBean> mDataListSystem;

    private List<SystemBirdListBean> mDataListSystemTemp;
    private MyApplication app;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {

                if (!TextUtils.isEmpty(userMoneyAll)) {
                    float f = Float.parseFloat(userMoneyAll);

                    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String p = decimalFormat.format(f);//format 返回的是字符串

                    temporaryMoney = f;


                    float numMoney = Float.parseFloat(p);
                    birdMoney.setFloat(0.00f, numMoney);
                    birdMoney.start();


                }
                //                runtDialog.dismiss();
                //平台放置豆苗的方法
                systemPutBirdMethod(width1, widthHalf, height1);

                if (mDataListSystemTemp.size() == 0) {
                    mTv_map.setVisibility(View.VISIBLE);
                    mTv_list.setVisibility(View.VISIBLE);
                } else {

                    mTv_map.setVisibility(View.GONE);
                    mTv_list.setVisibility(View.GONE);
                }
            }
        }
    };
    private int width1;
    private int widthHalf;
    private int height1;
    private int height;
    private String userMoneyAll;
    private RuntCustomProgressDialog runtDialog;
    private String key;
    private float temporaryMoney;
    private int currentVersionCode;
    private String city;
    private String district;
    private String addr;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private RelativeLayout rlAnnouncement;
    private List<AnnouncementPageBean> mDataList;
    private ImageView tvHomeRefresh;
    private String token;
    private JSONObject shop;

    private RelativeLayout mRl_bean_seedling_1_1;
    private ImageView mIv_bean_seedling_1_1;
    private TextView mTv_bean_seedling_1_1;
    private RelativeLayout mRl_bean_seedling_2_1;
    private ImageView mIv_bean_seedling_2_1;
    private TextView mTv_bean_seedling_2_1;
    private RelativeLayout mRl_bean_seedling_2_2;
    private ImageView mIv_bean_seedling_2_2;
    private TextView mTv_bean_seedling_2_2;
    private RelativeLayout mRl_bean_seedling_3_1;
    private ImageView mIv_bean_seedling_3_1;
    private TextView mTv_bean_seedling_3_1;
    private RelativeLayout mRl_bean_seedling_3_2;
    private ImageView mIv_bean_seedling_3_2;
    private TextView mTv_bean_seedling_3_2;
    private RelativeLayout mRl_bean_seedling_3_3;
    private ImageView mIv_bean_seedling_3_3;
    private TextView mTv_bean_seedling_3_3;
    private RelativeLayout mRl_bean_seedling_4_1;
    private ImageView mIv_bean_seedling_4_1;
    private TextView mTv_bean_seedling_4_1;
    private RelativeLayout mRl_bean_seedling_4_2;
    private ImageView mIv_bean_seedling_4_2;
    private TextView mTv_bean_seedling_4_2;

    private RelativeLayout mRl_bean_seedling_4_3;
    private ImageView mIv_bean_seedling_4_3;
    private TextView mTv_bean_seedling_4_3;

    private RelativeLayout mRl_bean_seedling_4_4;
    private ImageView mIv_bean_seedling_4_4;
    private TextView mTv_bean_seedling_4_4;

    private RelativeLayout mRl_bean_seedling_5_1;
    private ImageView mIv_bean_seedling_5_1;
    private TextView mTv_bean_seedling_5_1;
    private RelativeLayout mRl_bean_seedling_5_2;
    private ImageView mIv_bean_seedling_5_2;
    private TextView mTv_bean_seedling_5_2;
    private RelativeLayout mRl_bean_seedling_5_3;
    private ImageView mIv_bean_seedling_5_3;
    private TextView mTv_bean_seedling_5_3;
    private RelativeLayout mRl_bean_seedling_6_1;
    private ImageView mIv_bean_seedling_6_1;
    private TextView mTv_bean_seedling_6_1;
    private RelativeLayout mRl_bean_seedling_6_2;
    private ImageView mIv_bean_seedling_6_2;
    private TextView mTv_bean_seedling_6_2;

    private RelativeLayout mRl_bean_seedling_6_3;
    private ImageView mIv_bean_seedling_6_3;
    private TextView mTv_bean_seedling_6_3;

    private RelativeLayout mRl_bean_seedling_6_4;
    private ImageView mIv_bean_seedling_6_4;
    private TextView mTv_bean_seedling_6_4;

    private RelativeLayout mRl_bean_seedling_7_1;
    private ImageView mIv_bean_seedling_7_1;
    private TextView mTv_bean_seedling_7_1;

    private RelativeLayout mRl_bean_seedling_7_2;
    private ImageView mIv_bean_seedling_7_2;
    private TextView mTv_bean_seedling_7_2;

    private RelativeLayout mRl_bean_seedling_7_3;
    private ImageView mIv_bean_seedling_7_3;
    private TextView mTv_bean_seedling_7_3;

    private RelativeLayout mRl_bean_seedling_7_4;
    private ImageView mIv_bean_seedling_7_4;
    private TextView mTv_bean_seedling_7_4;

    private RelativeLayout mRl_bean_seedling_7_5;
    private ImageView mIv_bean_seedling_7_5;
    private TextView mTv_bean_seedling_7_5;

    private RelativeLayout mRl_bean_seedling_8_1;
    private ImageView mIv_bean_seedling_8_1;
    private TextView mTv_bean_seedling_8_1;

    private RelativeLayout mRl_bean_seedling_8_2;
    private ImageView mIv_bean_seedling_8_2;
    private TextView mTv_bean_seedling_8_2;

    private RelativeLayout mRl_bean_seedling_8_3;
    private ImageView mIv_bean_seedling_8_3;
    private TextView mTv_bean_seedling_8_3;

    private RelativeLayout mRl_bean_seedling_8_4;
    private ImageView mIv_bean_seedling_8_4;
    private TextView mTv_bean_seedling_8_4;


    private RelativeLayout mRl_bean_seedling_9_1;
    private ImageView mIv_bean_seedling_9_1;
    private TextView mTv_bean_seedling_9_1;

    private RelativeLayout mRl_bean_seedling_9_2;
    private ImageView mIv_bean_seedling_9_2;
    private TextView mTv_bean_seedling_9_2;

    private RelativeLayout mRl_bean_seedling_9_3;
    private ImageView mIv_bean_seedling_9_3;
    private TextView mTv_bean_seedling_9_3;

    private RelativeLayout mRl_bean_seedling_9_4;
    private ImageView mIv_bean_seedling_9_4;
    private TextView mTv_bean_seedling_9_4;

    private RelativeLayout mRl_bean_seedling_9_5;
    private ImageView mIv_bean_seedling_9_5;
    private TextView mTv_bean_seedling_9_5;


    private RelativeLayout mRl_bean_seedling_10_1;
    private ImageView mIv_bean_seedling_10_1;
    private TextView mTv_bean_seedling_10_1;

    private RelativeLayout mRl_bean_seedling_10_2;
    private ImageView mIv_bean_seedling_10_2;
    private TextView mTv_bean_seedling_10_2;

    private RelativeLayout mRl_bean_seedling_10_3;
    private ImageView mIv_bean_seedling_10_3;
    private TextView mTv_bean_seedling_10_3;

    private RelativeLayout mRl_bean_seedling_10_4;
    private ImageView mIv_bean_seedling_10_4;
    private TextView mTv_bean_seedling_10_4;


    private RelativeLayout mRl_bean_seedling_11_1;
    private ImageView mIv_bean_seedling_11_1;
    private TextView mTv_bean_seedling_11_1;

    private RelativeLayout mRl_bean_seedling_11_2;
    private ImageView mIv_bean_seedling_11_2;
    private TextView mTv_bean_seedling_11_2;

    private RelativeLayout mRl_bean_seedling_11_3;
    private ImageView mIv_bean_seedling_11_3;
    private TextView mTv_bean_seedling_11_3;

    private RelativeLayout mRl_bean_seedling_11_4;
    private ImageView mIv_bean_seedling_11_4;
    private TextView mTv_bean_seedling_11_4;

    private RelativeLayout mRl_bean_seedling_11_5;
    private ImageView mIv_bean_seedling_11_5;
    private TextView mTv_bean_seedling_11_5;

    private Handler handler;
    private ImageButton mIb_home_put_the_bird;
    private ImageButton mTv_map;
    private ImageButton mTv_list;
    private ImageView ivGongLv;
    private ImageView ivCashLuckyDraw;
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private RelativeLayout mRlRootGold;
    private ImageButton mMakePartner;
    private ImageButton mBecomeShareholder;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, null);

        mRlRootGold = (RelativeLayout) view.findViewById(R.id.rl_root_gold);


        birdMoney = (RiseNumberTextView) view.findViewById(R.id.tv_bird_money);
        areaAddress = (TextView) view.findViewById(R.id.tv_area_address);
        mNoticeView = (NoticeView) view.findViewById(R.id.notice_view);
        homeRoot = (RelativeLayout) view.findViewById(R.id.rl_home_root);
        rlAnnouncement = (RelativeLayout) view.findViewById(R.id.rl_announcement);

        tvHomeRefresh = (ImageView) view.findViewById(R.id.tv_home_refresh);
        ivGongLv = (ImageView) view.findViewById(R.id.iv_gong_lv);
        ivCashLuckyDraw = (ImageView) view.findViewById(R.id.iv_cash_lucky_draw);
        //发豆按钮
        mIb_home_put_the_bird = (ImageButton) view.findViewById(R.id.ib_home_put_the_bird);
        mIb_home_put_the_bird.setOnClickListener(this);
        token = PrefUtils.readToken(mMainActivity);
        handler = MyApplication.getHandler();
        StatusBarUtil.setStatusBar(mMainActivity, R.color.theme_color_8bd4fc);
        //        XpStatusBarUtil.setStatusBarColor(mMainActivity,R.color.theme_color);

        key = "prekNmSWM2b0d6NUJjRGJUWmtHVTNnNXZHT0lYM";
        runtDialog = new RuntCustomProgressDialog(mMainActivity);
        runtDialog.setMessage("数据加载中···");




        mMakePartner = (ImageButton) view.findViewById(R.id.ib_home_make_partner);
        mBecomeShareholder = (ImageButton) view.findViewById(R.id.ib_home_become_shareholder);

        mMakePartner.setOnClickListener(this);
        mBecomeShareholder.setOnClickListener(this);





        // <!--第一行 一个豆苗-->
        mRl_bean_seedling_1_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_1_1);
        mIv_bean_seedling_1_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_1_1);
        mTv_bean_seedling_1_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_1_1);
        mRl_bean_seedling_1_1.setOnClickListener(this);
        mRl_bean_seedling_1_1.setFocusable(false);
        //         <!--第二行 2个豆苗-->
        mRl_bean_seedling_2_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_2_1);
        mIv_bean_seedling_2_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_2_1);
        mTv_bean_seedling_2_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_2_1);
        mRl_bean_seedling_2_1.setOnClickListener(this);
        mRl_bean_seedling_2_1.setEnabled(false);

        mRl_bean_seedling_2_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_2_2);
        mIv_bean_seedling_2_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_2_2);
        mTv_bean_seedling_2_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_2_2);
        mRl_bean_seedling_2_2.setOnClickListener(this);
        mRl_bean_seedling_2_2.setEnabled(false);

        //        <!--第3行 3个豆苗-->
        mRl_bean_seedling_3_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_3_1);
        mIv_bean_seedling_3_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_3_1);
        mTv_bean_seedling_3_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_3_1);
        mRl_bean_seedling_3_1.setOnClickListener(this);
        mRl_bean_seedling_3_1.setEnabled(false);

        mRl_bean_seedling_3_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_3_2);
        mIv_bean_seedling_3_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_3_2);
        mTv_bean_seedling_3_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_3_2);
        mRl_bean_seedling_3_2.setOnClickListener(this);
        mRl_bean_seedling_3_2.setEnabled(false);

        mRl_bean_seedling_3_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_3_3);
        mIv_bean_seedling_3_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_3_3);
        mTv_bean_seedling_3_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_3_3);
        mRl_bean_seedling_3_3.setOnClickListener(this);
        mRl_bean_seedling_3_3.setEnabled(false);

        //        <!--第4行 3个豆苗 + 1-->
        mRl_bean_seedling_4_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_4_1);
        mIv_bean_seedling_4_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_4_1);
        mTv_bean_seedling_4_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_4_1);
        mRl_bean_seedling_4_1.setOnClickListener(this);
        mRl_bean_seedling_4_1.setEnabled(false);

        mRl_bean_seedling_4_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_4_2);
        mIv_bean_seedling_4_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_4_2);
        mTv_bean_seedling_4_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_4_2);
        mRl_bean_seedling_4_2.setOnClickListener(this);
        mRl_bean_seedling_4_2.setEnabled(false);

        mRl_bean_seedling_4_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_4_3);
        mIv_bean_seedling_4_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_4_3);
        mTv_bean_seedling_4_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_4_3);
        mRl_bean_seedling_4_3.setOnClickListener(this);
        mRl_bean_seedling_4_3.setEnabled(false);

        mRl_bean_seedling_4_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_4_4);
        mIv_bean_seedling_4_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_4_4);
        mTv_bean_seedling_4_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_4_4);
        mRl_bean_seedling_4_4.setOnClickListener(this);
        mRl_bean_seedling_4_4.setEnabled(false);

        //        <!--第5行 3个豆苗-->
        mRl_bean_seedling_5_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_5_1);
        mIv_bean_seedling_5_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_5_1);
        mTv_bean_seedling_5_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_5_1);
        mRl_bean_seedling_5_1.setOnClickListener(this);
        mRl_bean_seedling_5_1.setEnabled(false);

        mRl_bean_seedling_5_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_5_2);
        mIv_bean_seedling_5_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_5_2);
        mTv_bean_seedling_5_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_5_2);
        mRl_bean_seedling_5_2.setOnClickListener(this);
        mRl_bean_seedling_5_2.setEnabled(false);

        mRl_bean_seedling_5_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_5_3);
        mIv_bean_seedling_5_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_5_3);
        mTv_bean_seedling_5_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_5_3);
        mRl_bean_seedling_5_3.setOnClickListener(this);
        mRl_bean_seedling_5_3.setEnabled(false);


        //        <!--第6行 3个豆苗+ 1-->

        mRl_bean_seedling_6_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_6_1);
        mIv_bean_seedling_6_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_6_1);
        mTv_bean_seedling_6_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_6_1);
        mRl_bean_seedling_6_1.setOnClickListener(this);
        mRl_bean_seedling_6_1.setEnabled(false);

        mRl_bean_seedling_6_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_6_2);
        mIv_bean_seedling_6_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_6_2);
        mTv_bean_seedling_6_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_6_2);
        mRl_bean_seedling_6_2.setOnClickListener(this);
        mRl_bean_seedling_6_2.setEnabled(false);

        mRl_bean_seedling_6_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_6_3);
        mIv_bean_seedling_6_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_6_3);
        mTv_bean_seedling_6_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_6_3);
        mRl_bean_seedling_6_3.setOnClickListener(this);
        mRl_bean_seedling_6_3.setEnabled(false);

        mRl_bean_seedling_6_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_6_4);
        mIv_bean_seedling_6_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_6_4);
        mTv_bean_seedling_6_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_6_4);
        mRl_bean_seedling_6_4.setOnClickListener(this);
        mRl_bean_seedling_6_4.setEnabled(false);

        // <!--第7行 2个豆苗+ 3.4.5-->
        mRl_bean_seedling_7_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_7_1);
        mIv_bean_seedling_7_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_7_1);
        mTv_bean_seedling_7_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_7_1);
        mRl_bean_seedling_7_1.setOnClickListener(this);
        mRl_bean_seedling_7_1.setEnabled(false);

        mRl_bean_seedling_7_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_7_2);
        mIv_bean_seedling_7_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_7_2);
        mTv_bean_seedling_7_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_7_2);
        mRl_bean_seedling_7_2.setOnClickListener(this);
        mRl_bean_seedling_7_2.setEnabled(false);

        mRl_bean_seedling_7_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_7_3);
        mIv_bean_seedling_7_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_7_3);
        mTv_bean_seedling_7_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_7_3);
        mRl_bean_seedling_7_3.setOnClickListener(this);
        mRl_bean_seedling_7_3.setEnabled(false);

        mRl_bean_seedling_7_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_7_4);
        mIv_bean_seedling_7_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_7_4);
        mTv_bean_seedling_7_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_7_4);
        mRl_bean_seedling_7_4.setOnClickListener(this);
        mRl_bean_seedling_7_4.setEnabled(false);

        mRl_bean_seedling_7_5 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_7_5);
        mIv_bean_seedling_7_5 = (ImageView) view.findViewById(R.id.iv_bean_seedling_7_5);
        mTv_bean_seedling_7_5 = (TextView) view.findViewById(R.id.tv_bean_seedling_7_5);
        mRl_bean_seedling_7_5.setOnClickListener(this);
        mRl_bean_seedling_7_5.setEnabled(false);

        // <!--第8行 1个豆苗+2.3.4-->

        mRl_bean_seedling_8_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_8_1);
        mIv_bean_seedling_8_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_8_1);
        mTv_bean_seedling_8_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_8_1);
        mRl_bean_seedling_8_1.setOnClickListener(this);
        mRl_bean_seedling_8_1.setEnabled(false);

        mRl_bean_seedling_8_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_8_2);
        mIv_bean_seedling_8_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_8_2);
        mTv_bean_seedling_8_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_8_2);
        mRl_bean_seedling_8_2.setOnClickListener(this);
        mRl_bean_seedling_8_2.setEnabled(false);

        mRl_bean_seedling_8_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_8_3);
        mIv_bean_seedling_8_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_8_3);
        mTv_bean_seedling_8_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_8_3);
        mRl_bean_seedling_8_3.setOnClickListener(this);
        mRl_bean_seedling_8_3.setEnabled(false);

        mRl_bean_seedling_8_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_8_4);
        mIv_bean_seedling_8_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_8_4);
        mTv_bean_seedling_8_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_8_4);
        mRl_bean_seedling_8_4.setOnClickListener(this);
        mRl_bean_seedling_8_4.setEnabled(false);


        // <!--第9行 5个豆苗-->

        mRl_bean_seedling_9_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_9_1);
        mIv_bean_seedling_9_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_9_1);
        mTv_bean_seedling_9_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_9_1);
        mRl_bean_seedling_9_1.setOnClickListener(this);
        mRl_bean_seedling_9_1.setEnabled(false);

        mRl_bean_seedling_9_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_9_2);
        mIv_bean_seedling_9_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_9_2);
        mTv_bean_seedling_9_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_9_2);
        mRl_bean_seedling_9_2.setOnClickListener(this);
        mRl_bean_seedling_9_2.setEnabled(false);

        mRl_bean_seedling_9_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_9_3);
        mIv_bean_seedling_9_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_9_3);
        mTv_bean_seedling_9_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_9_3);
        mRl_bean_seedling_9_3.setOnClickListener(this);
        mRl_bean_seedling_9_3.setEnabled(false);

        mRl_bean_seedling_9_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_9_4);
        mIv_bean_seedling_9_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_9_4);
        mTv_bean_seedling_9_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_9_4);
        mRl_bean_seedling_9_4.setOnClickListener(this);
        mRl_bean_seedling_9_4.setEnabled(false);

        mRl_bean_seedling_9_5 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_9_5);
        mIv_bean_seedling_9_5 = (ImageView) view.findViewById(R.id.iv_bean_seedling_9_5);
        mTv_bean_seedling_9_5 = (TextView) view.findViewById(R.id.tv_bean_seedling_9_5);
        mRl_bean_seedling_9_5.setOnClickListener(this);
        mRl_bean_seedling_9_5.setEnabled(false);


        // <!--第10行 4个豆苗-->

        mRl_bean_seedling_10_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_10_1);
        mIv_bean_seedling_10_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_10_1);
        mTv_bean_seedling_10_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_10_1);
        mRl_bean_seedling_10_1.setOnClickListener(this);
        mRl_bean_seedling_10_1.setEnabled(false);

        mRl_bean_seedling_10_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_10_2);
        mIv_bean_seedling_10_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_10_2);
        mTv_bean_seedling_10_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_10_2);
        mRl_bean_seedling_10_2.setOnClickListener(this);
        mRl_bean_seedling_10_2.setEnabled(false);

        mRl_bean_seedling_10_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_10_3);
        mIv_bean_seedling_10_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_10_3);
        mTv_bean_seedling_10_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_10_3);
        mRl_bean_seedling_10_3.setOnClickListener(this);
        mRl_bean_seedling_10_3.setEnabled(false);

        mRl_bean_seedling_10_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_10_4);
        mIv_bean_seedling_10_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_10_4);
        mTv_bean_seedling_10_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_10_4);
        mRl_bean_seedling_10_4.setOnClickListener(this);
        mRl_bean_seedling_10_4.setEnabled(false);


        // <!--第11行 5个豆苗-->

        mRl_bean_seedling_11_1 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_11_1);
        mIv_bean_seedling_11_1 = (ImageView) view.findViewById(R.id.iv_bean_seedling_11_1);
        mTv_bean_seedling_11_1 = (TextView) view.findViewById(R.id.tv_bean_seedling_11_1);
        mRl_bean_seedling_11_1.setOnClickListener(this);
        mRl_bean_seedling_11_1.setEnabled(false);

        mRl_bean_seedling_11_2 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_11_2);
        mIv_bean_seedling_11_2 = (ImageView) view.findViewById(R.id.iv_bean_seedling_11_2);
        mTv_bean_seedling_11_2 = (TextView) view.findViewById(R.id.tv_bean_seedling_11_2);
        mRl_bean_seedling_11_2.setOnClickListener(this);
        mRl_bean_seedling_11_2.setEnabled(false);

        mRl_bean_seedling_11_3 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_11_3);
        mIv_bean_seedling_11_3 = (ImageView) view.findViewById(R.id.iv_bean_seedling_11_3);
        mTv_bean_seedling_11_3 = (TextView) view.findViewById(R.id.tv_bean_seedling_11_3);
        mRl_bean_seedling_11_3.setOnClickListener(this);
        mRl_bean_seedling_11_3.setEnabled(false);

        mRl_bean_seedling_11_4 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_11_4);
        mIv_bean_seedling_11_4 = (ImageView) view.findViewById(R.id.iv_bean_seedling_11_4);
        mTv_bean_seedling_11_4 = (TextView) view.findViewById(R.id.tv_bean_seedling_11_4);
        mRl_bean_seedling_11_4.setOnClickListener(this);
        mRl_bean_seedling_11_4.setEnabled(false);

        mRl_bean_seedling_11_5 = (RelativeLayout) view.findViewById(R.id.rl_bean_seedling_11_5);
        mIv_bean_seedling_11_5 = (ImageView) view.findViewById(R.id.iv_bean_seedling_11_5);
        mTv_bean_seedling_11_5 = (TextView) view.findViewById(R.id.tv_bean_seedling_11_5);
        mRl_bean_seedling_11_5.setOnClickListener(this);
        mRl_bean_seedling_11_5.setEnabled(false);

        //代理地图
        mTv_map = (ImageButton) view.findViewById(R.id.tv_map);
        //代理列表
        mTv_list = (ImageButton) view.findViewById(R.id.tv_list);

        mTv_map.setOnClickListener(this);
        mTv_list.setOnClickListener(this);

        //定位开始
        mLocationClient = new LocationClient(mMainActivity.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明


        mLocationClient.start();
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        //定位结束


        mDataListSystem = new ArrayList<>();

        mDataListSystemTemp = new ArrayList<>();

        boolean birdVoice = PrefUtils.getBoolean(mMainActivity, "birdVoice", true);
        //        if (birdVoice) {
        //            //开启开屏声音
        //            Intent intent = new Intent(mMainActivity, yypService.class);
        //            mMainActivity.startService(intent);
        //
        //
        //        } else {
        //            //不开启开屏声音
        //        }


        //开启鸟飞的声音

        initSoundMethod();

        mDataList = new ArrayList<>();
        //公告的方法  富硒宝  暂时隐藏
        //        noticesMethod();


        //        homeBird.setBackgroundResource(R.drawable.a_bird_flying);
        //        // 通过ImageView对象拿到背景显示的AnimationDrawable
        //        mAnimation = (AnimationDrawable) homeBird.getBackground();
        //        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        //        homeBird.post(new Runnable() {
        //            @Override
        //            public void run() {
        //                mAnimation.start();
        //            }
        //        });
        //        homeBird.setOnClickListener(this);

        WindowManager wm1 = mMainActivity.getWindowManager();
        width1 = wm1.getDefaultDisplay().getWidth();
        widthHalf = wm1.getDefaultDisplay().getWidth() / 2;
        height1 = wm1.getDefaultDisplay().getHeight() / 3;
        height = wm1.getDefaultDisplay().getHeight();


        //布局元宝的跟布局
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width1, DensityUtils.dp2px(mMainActivity, 300));
        long round = Math.round(height * 0.265);
        int distanceTopHeight = Integer.parseInt(round + "");
        int dipToPx = DensityUtils.dp2px(mMainActivity, 16);

        params.setMargins(0, distanceTopHeight - dipToPx, 0, 0);
        mRlRootGold.setLayoutParams(params);


        rlAnnouncement.setOnClickListener(this);
        tvHomeRefresh.setOnClickListener(this);
        ivGongLv.setOnClickListener(this);
        ivCashLuckyDraw.setOnClickListener(this);

        //启动页面播放鸟叫的声音
        //        PhoneUtils.cameraPermissions(mMainActivity);//请求拍照和储存权限
        checkUpdateMain();//富硒宝  自动更新
        return view;
    }

    //经度 longitude  维度 latitude
    private void requestBirdListData(double longitude, double latitude) {
        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mMainActivity);
        RequestBody requestBody = new FormBody.Builder()
                .add("longitude", longitude + "")
                .add("latitude", latitude + "")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_LIST)
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
                String responseData = response.body().string();
                runtDialog.dismiss();
                LogUtilsxp.e2(TAG, "URL_BIRD_LIST_message:" + responseData);
                try {
                    String data = responseData;
                    JSONObject object = new JSONObject(data);
                    int code = object.optInt("code");
                    String message = object.optString("msg");

                    //code=403  重新登录
                    if (code == 0) {

                        JSONObject dataObject = object.optJSONObject("data");
                        userMoneyAll = dataObject.optString("user_money");

                        JSONArray jsonArray = dataObject.optJSONArray("list");

                        if (null == jsonArray) {

                        } else {
                            if (jsonArray.length() > 0) {

                                List<SystemBirdListBean> systemListDetailBean = new ArrayList<>();
                                List<SystemBirdListBean> crop1List = new ArrayList<>();
                                for (int z = 0; z < jsonArray.length(); z++) {

                                    /**
                                     * "id": 1,
                                     "bean": "7ea160ea7e826470b4e09f10ea349cb3",
                                     "type": 0,
                                     "sys": 1,
                                     "money": 11.6639,
                                     "crop": 1
                                     */


                                    SystemBirdListBean detailBean = new SystemBirdListBean();
                                    detailBean.setId(jsonArray.optJSONObject(z).optString("id"));

                                    detailBean.setBean(jsonArray.optJSONObject(z).optString("gold"));
                                    detailBean.setMoney(jsonArray.optJSONObject(z).optString("money"));
                                    detailBean.setType(jsonArray.optJSONObject(z).optString("type"));
                                    detailBean.setSys(jsonArray.optJSONObject(z).optString("sys"));
                                    detailBean.setCrop(jsonArray.optJSONObject(z).optString("crop"));
                                    String crop = jsonArray.optJSONObject(z).optString("crop");
                                    if (crop.equals("1")) {
                                        crop1List.add(detailBean);
                                    }

                                    systemListDetailBean.add(detailBean);


                                }
                                mDataListSystem.addAll(systemListDetailBean);

                                mDataListSystemTemp.addAll(crop1List);
                                //                                if(mDataListSystemTemp.size()==0){
                                //                                    mTv_map.setVisibility(View.VISIBLE);
                                //                                    mTv_list.setVisibility(View.VISIBLE);
                                //                                }else {
                                //
                                //                                    mTv_map.setVisibility(View.GONE);
                                //                                    mTv_list.setVisibility(View.GONE);
                                //                                }

                                //                                mAdapter.notifyDataSetChanged();


                            }
                        }
                        mHandler.sendEmptyMessage(100);

                    } else if (code == 403) {
                        ToastUtils.showToast(mMainActivity, message);
                        Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                        mIntent.putExtra("title", "登录");
                        PrefUtils.writeToken("", mMainActivity);
                        mMainActivity.startActivity(mIntent);  //重新启动LoginActivity
                        mMainActivity.finish();

                    } else {
                        ToastUtils.showToast(mMainActivity, message);


                    }
                } catch (Exception e) {


                }
            }
        });
    }


    private void systemPutBirdMethod(int width1, final int widthHalf, int height1) {
        //1行 1
        String crop1 = mDataListSystem.get(0).getCrop();
        if (crop1.equals("0")) {

            mRl_bean_seedling_1_1.setEnabled(false);
            mIv_bean_seedling_1_1.setBackgroundResource(R.drawable.shumiao);


            mTv_bean_seedling_1_1.setVisibility(View.GONE);

        } else {
            String money1 = mDataListSystem.get(0).getMoney();
            mRl_bean_seedling_1_1.setEnabled(true);
            //            mIv_bean_seedling_1_1.setBackgroundResource(R.drawable.chengshu);
            mIv_bean_seedling_1_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_1_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_1_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();


                }
            });


            mTv_bean_seedling_1_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money1)) {
                mTv_bean_seedling_1_1.setText(money1);
            }

        }
        //2行 2-3
        String crop2 = mDataListSystem.get(1).getCrop();
        if (crop2.equals("0")) {

            mRl_bean_seedling_2_1.setEnabled(false);
            mIv_bean_seedling_2_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_2_1.setVisibility(View.GONE);

        } else {
            String money2 = mDataListSystem.get(1).getMoney();
            mRl_bean_seedling_2_1.setEnabled(true);
            //            mIv_bean_seedling_2_1.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_2_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_2_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_2_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_2_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money2)) {
                mTv_bean_seedling_2_1.setText(money2);
            }

        }
        String crop3 = mDataListSystem.get(2).getCrop();
        if (crop3.equals("0")) {

            mRl_bean_seedling_2_2.setEnabled(false);
            mIv_bean_seedling_2_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_2_2.setVisibility(View.GONE);

        } else {
            String money3 = mDataListSystem.get(2).getMoney();
            mRl_bean_seedling_2_2.setEnabled(true);
            //            mIv_bean_seedling_2_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_2_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_2_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_2_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_2_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money3)) {
                mTv_bean_seedling_2_2.setText(money3);
            }

        }

        //3行 4-6
        String crop4 = mDataListSystem.get(3).getCrop();
        if (crop4.equals("0")) {

            mRl_bean_seedling_3_1.setEnabled(false);
            mIv_bean_seedling_3_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_3_1.setVisibility(View.GONE);

        } else {
            String money4 = mDataListSystem.get(3).getMoney();
            mRl_bean_seedling_3_1.setEnabled(true);
            //            mIv_bean_seedling_3_1.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_3_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_3_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_3_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_3_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money4)) {
                mTv_bean_seedling_3_1.setText(money4);
            }

        }
        String crop5 = mDataListSystem.get(4).getCrop();
        if (crop5.equals("0")) {

            mRl_bean_seedling_3_2.setEnabled(false);
            mIv_bean_seedling_3_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_3_2.setVisibility(View.GONE);

        } else {
            String money5 = mDataListSystem.get(4).getMoney();
            mRl_bean_seedling_3_2.setEnabled(true);
            //            mIv_bean_seedling_3_2.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_3_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_3_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_3_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_3_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money5)) {
                mTv_bean_seedling_3_2.setText(money5);
            }

        }

        String crop6 = mDataListSystem.get(5).getCrop();
        if (crop6.equals("0")) {

            mRl_bean_seedling_3_3.setEnabled(false);
            mIv_bean_seedling_3_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_3_3.setVisibility(View.GONE);

        } else {
            String money6 = mDataListSystem.get(5).getMoney();
            mRl_bean_seedling_3_3.setEnabled(true);
            //            mIv_bean_seedling_3_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_3_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_3_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_3_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_3_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money6)) {
                mTv_bean_seedling_3_3.setText(money6);
            }

        }


        //4行 7-9
        String crop7 = mDataListSystem.get(6).getCrop();
        if (crop7.equals("0")) {

            mRl_bean_seedling_4_1.setEnabled(false);
            mIv_bean_seedling_4_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_4_1.setVisibility(View.GONE);

        } else {
            String money7 = mDataListSystem.get(6).getMoney();
            mRl_bean_seedling_4_1.setEnabled(true);
            //            mIv_bean_seedling_4_1.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_4_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_4_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_4_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_4_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money7)) {
                mTv_bean_seedling_4_1.setText(money7);
            }

        }
        String crop8 = mDataListSystem.get(7).getCrop();
        if (crop8.equals("0")) {

            mRl_bean_seedling_4_2.setEnabled(false);
            mIv_bean_seedling_4_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_4_2.setVisibility(View.GONE);

        } else {
            String money7 = mDataListSystem.get(7).getMoney();
            mRl_bean_seedling_4_2.setEnabled(true);
            //            mIv_bean_seedling_4_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_4_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_4_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_4_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_4_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money7)) {
                mTv_bean_seedling_4_2.setText(money7);
            }

        }

        String crop9 = mDataListSystem.get(8).getCrop();
        if (crop9.equals("0")) {

            mRl_bean_seedling_4_3.setEnabled(false);
            mIv_bean_seedling_4_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_4_3.setVisibility(View.GONE);

        } else {
            String money9 = mDataListSystem.get(8).getMoney();
            mRl_bean_seedling_4_3.setEnabled(true);
            //            mIv_bean_seedling_4_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_4_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_4_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_4_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_4_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money9)) {
                mTv_bean_seedling_4_3.setText(money9);
            }

        }

        //5行 10-12
        String crop10 = mDataListSystem.get(9).getCrop();
        if (crop10.equals("0")) {

            mRl_bean_seedling_5_1.setEnabled(false);
            mIv_bean_seedling_5_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_5_1.setVisibility(View.GONE);

        } else {
            String money10 = mDataListSystem.get(9).getMoney();
            mRl_bean_seedling_5_1.setEnabled(true);
            //            mIv_bean_seedling_5_1.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_5_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_5_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_5_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_5_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money10)) {
                mTv_bean_seedling_5_1.setText(money10);
            }

        }
        String crop11 = mDataListSystem.get(10).getCrop();
        if (crop11.equals("0")) {

            mRl_bean_seedling_5_2.setEnabled(false);
            mIv_bean_seedling_5_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_5_2.setVisibility(View.GONE);

        } else {
            String money11 = mDataListSystem.get(10).getMoney();
            mRl_bean_seedling_5_2.setEnabled(true);
            //            mIv_bean_seedling_5_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_5_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_5_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_5_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_5_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money11)) {
                mTv_bean_seedling_5_2.setText(money11);
            }

        }

        String crop12 = mDataListSystem.get(11).getCrop();
        if (crop12.equals("0")) {

            mRl_bean_seedling_5_3.setEnabled(false);
            mIv_bean_seedling_5_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_5_3.setVisibility(View.GONE);

        } else {
            String money12 = mDataListSystem.get(11).getMoney();
            mRl_bean_seedling_5_3.setEnabled(true);
            //            mIv_bean_seedling_5_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_5_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_5_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_5_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_5_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money12)) {
                mTv_bean_seedling_5_3.setText(money12);
            }

        }
        //6行 13-15
        String crop13 = mDataListSystem.get(12).getCrop();
        if (crop13.equals("0")) {

            mRl_bean_seedling_6_1.setEnabled(false);
            mIv_bean_seedling_6_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_6_1.setVisibility(View.GONE);

        } else {
            String money13 = mDataListSystem.get(12).getMoney();
            mRl_bean_seedling_6_1.setEnabled(true);
            //            mIv_bean_seedling_6_1.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_6_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_6_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_6_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_6_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money13)) {
                mTv_bean_seedling_6_1.setText(money13);
            }

        }
        String crop14 = mDataListSystem.get(13).getCrop();
        if (crop14.equals("0")) {

            mRl_bean_seedling_6_2.setEnabled(false);
            mIv_bean_seedling_6_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_6_2.setVisibility(View.GONE);

        } else {
            String money14 = mDataListSystem.get(13).getMoney();
            mRl_bean_seedling_6_2.setEnabled(true);
            //            mIv_bean_seedling_6_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_6_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_6_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_6_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_6_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money14)) {
                mTv_bean_seedling_6_2.setText(money14);
            }

        }

        String crop15 = mDataListSystem.get(14).getCrop();
        if (crop15.equals("0")) {

            mRl_bean_seedling_6_3.setEnabled(false);
            mIv_bean_seedling_6_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_6_3.setVisibility(View.GONE);

        } else {
            String money15 = mDataListSystem.get(14).getMoney();
            mRl_bean_seedling_6_3.setEnabled(true);
            //            mIv_bean_seedling_6_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_6_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_6_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_6_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_6_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money15)) {
                mTv_bean_seedling_6_3.setText(money15);
            }

        }


        //7行 16-17
        String crop16 = mDataListSystem.get(15).getCrop();
        if (crop16.equals("0")) {

            mRl_bean_seedling_7_1.setEnabled(false);
            mIv_bean_seedling_7_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_7_1.setVisibility(View.GONE);

        } else {
            String money16 = mDataListSystem.get(15).getMoney();
            mRl_bean_seedling_7_1.setEnabled(true);
            //            mIv_bean_seedling_7_1.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_7_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_7_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_7_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_7_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money16)) {
                mTv_bean_seedling_7_1.setText(money16);
            }

        }
        String crop17 = mDataListSystem.get(16).getCrop();
        if (crop17.equals("0")) {

            mRl_bean_seedling_7_2.setEnabled(false);
            mIv_bean_seedling_7_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_7_2.setVisibility(View.GONE);

        } else {
            String money17 = mDataListSystem.get(16).getMoney();
            mRl_bean_seedling_7_2.setEnabled(true);
            //            mIv_bean_seedling_7_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_7_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_7_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_7_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_7_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money17)) {
                mTv_bean_seedling_7_2.setText(money17);
            }

        }


        //8行 18
        String crop18 = mDataListSystem.get(17).getCrop();
        if (crop18.equals("0")) {

            mRl_bean_seedling_8_1.setEnabled(false);
            mIv_bean_seedling_8_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_8_1.setVisibility(View.GONE);

        } else {
            String money18 = mDataListSystem.get(17).getMoney();
            mRl_bean_seedling_8_1.setEnabled(true);
            //            mIv_bean_seedling_8_1.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_8_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_8_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_8_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_8_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money18)) {
                mTv_bean_seedling_8_1.setText(money18);
            }

        }


        //4行 4  19个
        String crop19 = mDataListSystem.get(18).getCrop();
        if (crop19.equals("0")) {

            mRl_bean_seedling_4_4.setEnabled(false);
            mIv_bean_seedling_4_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_4_4.setVisibility(View.GONE);

        } else {
            String money19 = mDataListSystem.get(18).getMoney();
            mRl_bean_seedling_4_4.setEnabled(true);
            //            mIv_bean_seedling_4_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_4_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_4_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_4_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_4_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money19)) {
                mTv_bean_seedling_4_4.setText(money19);
            }

        }

        //6行 4  20个
        String crop20 = mDataListSystem.get(19).getCrop();
        if (crop20.equals("0")) {

            mRl_bean_seedling_6_4.setEnabled(false);
            mIv_bean_seedling_6_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_6_4.setVisibility(View.GONE);

        } else {
            String money20 = mDataListSystem.get(19).getMoney();
            mRl_bean_seedling_6_4.setEnabled(true);
            //            mIv_bean_seedling_6_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_6_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_6_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_6_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_6_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money20)) {
                mTv_bean_seedling_6_4.setText(money20);
            }

        }


        //7行 +3.4.5   索引21.22.23
        String crop21 = mDataListSystem.get(20).getCrop();
        if (crop21.equals("0")) {

            mRl_bean_seedling_7_3.setEnabled(false);
            mIv_bean_seedling_7_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_7_3.setVisibility(View.GONE);

        } else {
            String money21 = mDataListSystem.get(20).getMoney();
            mRl_bean_seedling_7_3.setEnabled(true);
            //            mIv_bean_seedling_7_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_7_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_7_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_7_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_7_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money21)) {
                mTv_bean_seedling_7_3.setText(money21);
            }

        }
        String crop22 = mDataListSystem.get(21).getCrop();
        if (crop22.equals("0")) {

            mRl_bean_seedling_7_4.setEnabled(false);
            mIv_bean_seedling_7_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_7_4.setVisibility(View.GONE);

        } else {
            String money22 = mDataListSystem.get(21).getMoney();
            mRl_bean_seedling_7_4.setEnabled(true);
            //            mIv_bean_seedling_7_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_7_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_7_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_7_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_7_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money22)) {
                mTv_bean_seedling_7_4.setText(money22);
            }

        }


        String crop23 = mDataListSystem.get(22).getCrop();
        if (crop23.equals("0")) {

            mRl_bean_seedling_7_5.setEnabled(false);
            mIv_bean_seedling_7_5.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_7_5.setVisibility(View.GONE);

        } else {
            String money23 = mDataListSystem.get(22).getMoney();
            mRl_bean_seedling_7_5.setEnabled(true);
            //            mIv_bean_seedling_7_5.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_7_5.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_7_5.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_7_5.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_7_5.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money23)) {
                mTv_bean_seedling_7_5.setText(money23);
            }

        }


        //8行 +2.3.4.   索引24.25.26


        String crop24 = mDataListSystem.get(23).getCrop();
        if (crop24.equals("0")) {

            mRl_bean_seedling_8_2.setEnabled(false);
            mIv_bean_seedling_8_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_8_2.setVisibility(View.GONE);

        } else {
            String money24 = mDataListSystem.get(23).getMoney();
            mRl_bean_seedling_8_2.setEnabled(true);
            //            mIv_bean_seedling_8_2.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_8_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_8_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_8_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_8_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money24)) {
                mTv_bean_seedling_8_2.setText(money24);
            }

        }


        String crop25 = mDataListSystem.get(24).getCrop();
        if (crop25.equals("0")) {

            mRl_bean_seedling_8_3.setEnabled(false);
            mIv_bean_seedling_8_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_8_3.setVisibility(View.GONE);

        } else {
            String money25 = mDataListSystem.get(24).getMoney();
            mRl_bean_seedling_8_3.setEnabled(true);
            //            mIv_bean_seedling_8_3.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_8_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_8_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_8_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_8_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money25)) {
                mTv_bean_seedling_8_3.setText(money25);
            }

        }
        String crop26 = mDataListSystem.get(25).getCrop();
        if (crop26.equals("0")) {

            mRl_bean_seedling_8_4.setEnabled(false);
            mIv_bean_seedling_8_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_8_4.setVisibility(View.GONE);

        } else {
            String money26 = mDataListSystem.get(25).getMoney();
            mRl_bean_seedling_8_4.setEnabled(true);
            //            mIv_bean_seedling_8_4.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_8_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_8_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_8_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_8_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money26)) {
                mTv_bean_seedling_8_4.setText(money26);
            }

        }


        //9行 +5.   索引27.28.29 30 31

        String crop27 = mDataListSystem.get(26).getCrop();
        if (crop27.equals("0")) {

            mRl_bean_seedling_9_1.setEnabled(false);
            mIv_bean_seedling_9_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_9_1.setVisibility(View.GONE);

        } else {
            String money27 = mDataListSystem.get(26).getMoney();
            mRl_bean_seedling_9_1.setEnabled(true);
            //            mIv_bean_seedling_9_1.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_9_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_9_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_9_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_9_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money27)) {
                mTv_bean_seedling_9_1.setText(money27);
            }

        }

        String crop28 = mDataListSystem.get(27).getCrop();
        if (crop28.equals("0")) {

            mRl_bean_seedling_9_2.setEnabled(false);
            mIv_bean_seedling_9_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_9_2.setVisibility(View.GONE);

        } else {
            String money28 = mDataListSystem.get(27).getMoney();
            mRl_bean_seedling_9_2.setEnabled(true);
            //            mIv_bean_seedling_9_2.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_9_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_9_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_9_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_9_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money28)) {
                mTv_bean_seedling_9_2.setText(money28);
            }

        }


        String crop29 = mDataListSystem.get(28).getCrop();
        if (crop29.equals("0")) {

            mRl_bean_seedling_9_3.setEnabled(false);
            mIv_bean_seedling_9_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_9_3.setVisibility(View.GONE);

        } else {
            String money29 = mDataListSystem.get(28).getMoney();
            mRl_bean_seedling_9_3.setEnabled(true);
            //            mIv_bean_seedling_9_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_9_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_9_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_9_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });

            mTv_bean_seedling_9_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money29)) {
                mTv_bean_seedling_9_3.setText(money29);
            }

        }
        String crop30 = mDataListSystem.get(29).getCrop();
        if (crop30.equals("0")) {

            mRl_bean_seedling_9_4.setEnabled(false);
            mIv_bean_seedling_9_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_9_4.setVisibility(View.GONE);

        } else {
            String money30 = mDataListSystem.get(29).getMoney();
            mRl_bean_seedling_9_4.setEnabled(true);
            //            mIv_bean_seedling_9_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_9_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_9_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_9_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_9_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money30)) {
                mTv_bean_seedling_8_4.setText(money30);
            }

        }


        String crop31 = mDataListSystem.get(30).getCrop();
        if (crop31.equals("0")) {

            mRl_bean_seedling_9_5.setEnabled(false);
            mIv_bean_seedling_9_5.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_9_5.setVisibility(View.GONE);

        } else {
            String money31 = mDataListSystem.get(30).getMoney();
            mRl_bean_seedling_9_5.setEnabled(true);
            //            mIv_bean_seedling_9_5.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_9_5.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_9_5.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_9_5.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_9_5.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money31)) {
                mTv_bean_seedling_9_5.setText(money31);
            }

        }


        //10行 +4 .   索引32.33.34 35

        String crop32 = mDataListSystem.get(31).getCrop();
        if (crop32.equals("0")) {

            mRl_bean_seedling_10_1.setEnabled(false);
            mIv_bean_seedling_10_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_10_1.setVisibility(View.GONE);

        } else {
            String money32 = mDataListSystem.get(31).getMoney();
            mRl_bean_seedling_10_1.setEnabled(true);
            //            mIv_bean_seedling_10_1.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_10_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_10_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_10_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_10_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money32)) {
                mTv_bean_seedling_10_1.setText(money32);
            }

        }

        String crop33 = mDataListSystem.get(32).getCrop();
        if (crop33.equals("0")) {

            mRl_bean_seedling_10_2.setEnabled(false);
            mIv_bean_seedling_10_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_10_2.setVisibility(View.GONE);

        } else {
            String money33 = mDataListSystem.get(32).getMoney();
            mRl_bean_seedling_10_2.setEnabled(true);
            //            mIv_bean_seedling_10_2.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_10_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_10_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_10_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_10_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money33)) {
                mTv_bean_seedling_10_2.setText(money33);
            }

        }


        String crop34 = mDataListSystem.get(33).getCrop();
        if (crop34.equals("0")) {

            mRl_bean_seedling_10_3.setEnabled(false);
            mIv_bean_seedling_10_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_10_3.setVisibility(View.GONE);

        } else {
            String money34 = mDataListSystem.get(33).getMoney();
            mRl_bean_seedling_10_3.setEnabled(true);
            //            mIv_bean_seedling_10_3.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_10_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_10_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_10_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_10_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money34)) {
                mTv_bean_seedling_10_3.setText(money34);
            }

        }
        String crop35 = mDataListSystem.get(34).getCrop();
        if (crop35.equals("0")) {

            mRl_bean_seedling_10_4.setEnabled(false);
            mIv_bean_seedling_10_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_10_4.setVisibility(View.GONE);

        } else {
            String money35 = mDataListSystem.get(34).getMoney();
            mRl_bean_seedling_10_4.setEnabled(true);
            //            mIv_bean_seedling_10_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_10_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_10_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_10_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_10_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money35)) {
                mTv_bean_seedling_10_4.setText(money35);
            }

        }


        //11行 +5 .   索引36.37.38 39  40

        String crop36 = mDataListSystem.get(35).getCrop();
        if (crop36.equals("0")) {

            mRl_bean_seedling_11_1.setEnabled(false);
            mIv_bean_seedling_11_1.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_11_1.setVisibility(View.GONE);

        } else {
            String money36 = mDataListSystem.get(35).getMoney();
            mRl_bean_seedling_11_1.setEnabled(true);
            //            mIv_bean_seedling_11_1.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_11_1.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_11_1.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_11_1.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_11_1.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money36)) {
                mTv_bean_seedling_11_1.setText(money36);
            }

        }

        String crop37 = mDataListSystem.get(36).getCrop();
        if (crop37.equals("0")) {

            mRl_bean_seedling_11_2.setEnabled(false);
            mIv_bean_seedling_11_2.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_11_2.setVisibility(View.GONE);

        } else {
            String money37 = mDataListSystem.get(36).getMoney();
            mRl_bean_seedling_11_2.setEnabled(true);
            //            mIv_bean_seedling_11_2.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_11_2.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_11_2.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_11_2.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_11_2.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money37)) {
                mTv_bean_seedling_11_2.setText(money37);
            }

        }


        String crop38 = mDataListSystem.get(37).getCrop();
        if (crop38.equals("0")) {

            mRl_bean_seedling_11_3.setEnabled(false);
            mIv_bean_seedling_11_3.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_11_3.setVisibility(View.GONE);

        } else {
            String money38 = mDataListSystem.get(37).getMoney();
            mRl_bean_seedling_11_3.setEnabled(true);
            //            mIv_bean_seedling_11_3.setImageResource(R.drawable.chengshu);

            mIv_bean_seedling_11_3.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_11_3.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_11_3.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_11_3.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money38)) {
                mTv_bean_seedling_11_3.setText(money38);
            }

        }
        String crop39 = mDataListSystem.get(38).getCrop();
        if (crop39.equals("0")) {

            mRl_bean_seedling_11_4.setEnabled(false);
            mIv_bean_seedling_11_4.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_11_4.setVisibility(View.GONE);

        } else {
            String money39 = mDataListSystem.get(38).getMoney();
            mRl_bean_seedling_11_4.setEnabled(true);
            //            mIv_bean_seedling_11_4.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_11_4.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_11_4.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_11_4.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_11_4.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money39)) {
                mTv_bean_seedling_11_4.setText(money39);
            }

        }


        String crop40 = mDataListSystem.get(39).getCrop();
        if (crop40.equals("0")) {

            mRl_bean_seedling_11_5.setEnabled(false);
            mIv_bean_seedling_11_5.setBackgroundResource(R.drawable.shumiao);
            mTv_bean_seedling_11_5.setVisibility(View.GONE);

        } else {
            String money40 = mDataListSystem.get(39).getMoney();
            mRl_bean_seedling_11_5.setEnabled(true);
            //            mIv_bean_seedling_11_5.setImageResource(R.drawable.chengshu);


            mIv_bean_seedling_11_5.setBackgroundResource(R.drawable.a_bird_flying_right);

            // 通过ImageView对象拿到背景显示的AnimationDrawable
            final AnimationDrawable mAnimation = (AnimationDrawable) mIv_bean_seedling_11_5.getBackground();
            // 为了防止在onCreate方法中只显示第一帧的解决方案之一
            mIv_bean_seedling_11_5.post(new Runnable() {
                @Override
                public void run() {
                    mAnimation.start();
                }
            });


            mTv_bean_seedling_11_5.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(money40)) {
                mTv_bean_seedling_11_5.setText(money40);
            }

        }


    }

    private void ViewClickMethod(final View v, int widthHalf, int position) {
        final String id = mDataListSystem.get(position).getId();
        String money = mDataListSystem.get(position).getMoney();
        final String bean = mDataListSystem.get(position).getBean();
        final String type = mDataListSystem.get(position).getType();
        final String sys = mDataListSystem.get(position).getSys();

        boolean birdVoice = PrefUtils.getBoolean(mMainActivity, "birdVoice", true);
        //sys  1 在树上系统  0 飞商家
        if (sys.equals("0")) {

            if (type.equals("1")) {
                if (mDataListSystemTemp.size() > 0) {
                    mDataListSystemTemp.remove(0);
                }

                isFirstEnterPager = false;


                String forTheBirdUrl = HttpsApi.HTML_USER_RED_PACKET;
                Intent mIntent = new Intent(mMainActivity, ShopAdHtmlActivity.class);
                mIntent.putExtra("title", " ");
                mIntent.putExtra("url", forTheBirdUrl);
                mIntent.putExtra("id", id);
                mIntent.putExtra("bird", bean);
                mIntent.putExtra("type", type);
                mIntent.putExtra("sys", sys);
                mIntent.putExtra("UserOrShop", "1");


                mIntent.putExtra("tagType", "tagType");
                mMainActivity.startActivity(mIntent);


            } else {
                if (mDataListSystemTemp.size() > 0) {
                    mDataListSystemTemp.remove(0);
                }
                isFirstEnterPager = false;
                String forTheBirdUrl = HttpsApi.HTML_SHOP_RED_PACKET;
                Intent mIntent = new Intent(mMainActivity, ShopAdHtmlActivity.class);
                mIntent.putExtra("title", " ");
                mIntent.putExtra("url", forTheBirdUrl);
                mIntent.putExtra("id", id);
                mIntent.putExtra("bird", bean);
                mIntent.putExtra("type", type);
                mIntent.putExtra("sys", sys);
                mIntent.putExtra("UserOrShop", "2");


                mIntent.putExtra("tagType", "tagType");
                mMainActivity.startActivity(mIntent);
            }

        } else {


            if (birdVoice) {
                //开启鸟飞的声音  富硒宝暂时不开启
                soundPool.play(
                        soundID,
                        0.1f,      //左耳道音量【0~1】
                        0.5f,      //右耳道音量【0~1】
                        0,         //播放优先级【0表示最低优先级】
                        0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                        1          //播放速度【1是正常，范围从0~2】
                );


            }


            int[] viewLocation = new int[2];
            //获取控件的X,Y轴坐标
            v.getLocationInWindow(viewLocation);
            int viewX = viewLocation[0]; // x 坐标
            int viewY = viewLocation[1]; // y 坐标
            //x坐标和屏幕的一半做对比/2
            int transX = (widthHalf - viewX) / 2;

            for (int i = 0; i < 3; i++) {

                final RelativeLayout systemRelaTemp = new RelativeLayout(mMainActivity);
                systemRelaTemp.setBackgroundResource(R.drawable.douzi);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(mMainActivity, 30), DensityUtils.dp2px(mMainActivity, 30));
                params.setMargins(viewX + 10, viewY - 90, 0, 0);
                systemRelaTemp.setLayoutParams(params);

                AnimationSet animationSet = new AnimationSet(true);//共用动画补间
                Random generate = new Random();
                final int nextInt = generate.nextInt(1500) + 1500;
                animationSet.setDuration(nextInt);

                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0);
                alphaAnimation.setDuration(nextInt);
                //                    animationSet.addAnimation(alphaAnimation);

                TranslateAnimation translateAnimation = new TranslateAnimation(0, transX, 0, -viewY + 30);
                translateAnimation.setDuration(nextInt);
                animationSet.addAnimation(translateAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    // 动画结束的回调
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        systemRelaTemp.setVisibility(View.GONE);
                    }
                });
                systemRelaTemp.startAnimation(animationSet);
                homeRoot.addView(systemRelaTemp);

            }


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //请求鸟的金额
                    String uid = PrefUtils.readUid(mMainActivity);
                    //获取当前的秒值
                    long time = System.currentTimeMillis() / 1000;

                    String idSign = id;
                    String birdSign = bean;

                    String encryptionString = uid + "_" + time + "_" + idSign + "_" + birdSign;
                    String encStr1 = CommonUtil.md5(encryptionString) + key;
                    String encStr = CommonUtil.md5(encStr1);
                    //                            String encStr22 = CommonUtil.md5("123");

                    /**
                     * id	是	int	id
                     bean	是	string	bean
                     sign	是	string	签名
                     type	是	int	类型
                     time	是	int	时间戳(秒)
                     */

                    //                            LogUtilsxp.e(TAG, "URL_BIRD_CLICK_message:  测试"+encStr22 );
                    LogUtilsxp.e(TAG, "URL_BIRD_CLICK_message:  测试----" + "id:" + id + "bean:" + birdSign +
                            "sign:" + encStr + "type:" + type + "time:" + time);
                    CustomTrust customTrust = new CustomTrust(mMainActivity);
                    OkHttpClient okHttpClient = customTrust.client;

                    String token = PrefUtils.readToken(mMainActivity);
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id", idSign)
                            .add("gold", birdSign)
                            .add("sign", encStr)
                            .add("type", type)
                            .add("time", time + "")
                            .build();
                    Request request = new Request.Builder()
                            .url(HttpsApi.SERVER_URL + HttpsApi.URL_BIRD_CLICK)
                            .addHeader("Authorization", token)
                            .post(requestBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtilsxp.e(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();

                            LogUtilsxp.e2(TAG, "URL_BIRD_CLICK_message:" + responseData);
                            try {
                                String data = responseData;
                                JSONObject object = new JSONObject(data);
                                int code = object.optInt("code");
                                String message = object.optString("msg");

                                //code=403  重新登录
                                if (code == 0) {


                                    JSONObject dataObject = object.optJSONObject("data");
                                    final String clickBirdGetmoney = dataObject.optString("money");
                                    CommonUtil.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(userMoneyAll)) {

                                                float fbm = Float.parseFloat(clickBirdGetmoney);

                                                temporaryMoney = temporaryMoney + fbm;

                                                DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                                String p = decimalFormat.format(temporaryMoney);//format 返回的是字符串
                                                float numMoney = Float.parseFloat(p);
                                                birdMoney.setFloat(temporaryMoney - fbm, numMoney);
                                                birdMoney.start();


                                            }
                                            if (mDataListSystemTemp.size() > 0) {
                                                mDataListSystemTemp.remove(0);
                                            }


                                            if (mDataListSystemTemp.size() == 0) {
                                                mDataListSystem.clear();
                                                homeRoot.removeAllViews();
                                                requestBirdListData(longitude, latitude);
                                            }
                                        }
                                    });

                                    mHandler.sendEmptyMessage(101);

                                } else if (code == 403) {
                                    ToastUtils.showToast(mMainActivity, message);
                                    Intent mIntent = new Intent(mMainActivity, LoginActivity.class);
                                    mIntent.putExtra("title", "登录");
                                    PrefUtils.writeToken("", mMainActivity);
                                    mMainActivity.startActivity(mIntent);  //重新启动LoginActivity
                                    mMainActivity.finish();

                                } else {
                                    ToastUtils.showToast(mMainActivity, message);


                                }
                            } catch (Exception e) {


                            }
                        }
                    });


                }
            }, 2500);


            //
            //        int[] viewLocation = new int[2];
            //        //获取控件的X,Y轴坐标
            //        v.getLocationInWindow(viewLocation);
            //        int viewX = viewLocation[0]; // x 坐标
            //        int viewY = viewLocation[1] / 2; // y 坐标
            //        //x坐标和屏幕的一半做对比/2
            //        int transX = (widthHalf - viewX) / 2;
            //
            //        //判断鸟是否翻转180度 ,大于零 翻转,小于零不翻转
            //        int isOverturn = widthHalf - viewX;
            //        //判断那一只鸟 被点击了
            //        //                    ToastUtils.showToast(mMainActivity, "我的金额" + money + ",我准备起飞");
            //
            //        AnimationSet animationSet = new AnimationSet(true);//共用动画补间
            //        animationSet.setDuration(3000);
            //
            //        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 1);
            //        alphaAnimation.setDuration(3000);
            //        //                    animationSet.addAnimation(alphaAnimation);
            //
            //        TranslateAnimation translateAnimation = new TranslateAnimation(0, transX, 0, -viewY + 30);
            //        translateAnimation.setDuration(3000);
            //        animationSet.addAnimation(translateAnimation);
            //        animationSet.addAnimation(translateAnimation);
            //        animationSet.setAnimationListener(new Animation.AnimationListener() {
            //
            //            @Override
            //            public void onAnimationStart(Animation animation) {
            //                v.setEnabled(false);
            //
            //                //                            String forTheBirdUrl = HttpsApi.HTML_SHOP_AD;
            //                //                            Intent mIntent = new Intent(mMainActivity, ShopAdHtmlActivity.class);
            //                //                            mIntent.putExtra("title", " ");
            //                //                            mIntent.putExtra("url", forTheBirdUrl);
            //                //                            mIntent.putExtra("id", id);
            //                //                            mIntent.putExtra("bird", bird);
            //                //
            //                //                            mMainActivity.startActivity(mIntent);
            //            }
            //
            //            @Override
            //            public void onAnimationRepeat(Animation animation) {
            //            }
            //
            //            // 动画结束的回调
            //            @Override
            //            public void onAnimationEnd(Animation animation) {
            //                //                            ToastUtils.showToast(mMainActivity, "动画结束");
            //                v.setVisibility(View.GONE);
            //
            //            }
            //        });
            //        v.startAnimation(animationSet);
        }


    }

    private void noticesMethod() {

        String token = PrefUtils.readToken(mMainActivity);
        //获取 当前的地理位置
        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;


        RequestBody requestBody = new FormBody.Builder()


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_ANNOUNCEMENT_LIST)
                //                .post(requestBody)
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

                LogUtilsxp.e2(TAG, "URL_ANNOUNCEMENT_LIST_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {

                                JSONArray jsonArray = object.optJSONArray("data");

                                if (null == jsonArray) {

                                } else {
                                    if (jsonArray.length() > 0) {

                                        List<AnnouncementPageBean> systemListDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            AnnouncementPageBean detailBean = new AnnouncementPageBean();
                                            detailBean.setId(jsonArray.optJSONObject(z).optString("id"));
                                            detailBean.setTitle(jsonArray.optJSONObject(z).optString("title"));
                                            detailBean.setRemark(jsonArray.optJSONObject(z).optString("remark"));
                                            detailBean.setCreatedAt(jsonArray.optJSONObject(z).optString("createdAt"));
                                            systemListDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(systemListDetailBean);
                                        final List<String> notices = new ArrayList<>();
                                        final List<String> idList = new ArrayList<>();
                                        for (int i = 0; i < mDataList.size(); i++) {
                                            notices.add(mDataList.get(i).getTitle());
                                            idList.add(mDataList.get(i).getId());
                                        }


                                        mNoticeView.addNotice(notices, idList);
                                        mNoticeView.startFlipping();
                                        mNoticeView.setOnNoticeClickListener(new NoticeView.OnNoticeClickListener() {
                                            @Override
                                            public void onNotieClick(int position, String notice, String noticeId) {

                                                //                                                String id = idList.get(position);
                                                Intent mIntent = new Intent(mMainActivity, AnnouncementDetailsActivity.class);
                                                mIntent.putExtra("title", "公告详情");
                                                mIntent.putExtra("id", noticeId);
                                                startActivity(mIntent);

                                            }
                                        });
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
                    }
                });

            }
        });


    }


    private void initSoundMethod() {

        // 5.0 及 之后
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().build();
            soundID = soundPool.load(mMainActivity, R.raw.bird_flying, 1);
        } else { // 5.0 以前
            soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
        }


    }


    private ViewGroup.LayoutParams setViewMargin(View view, int x, int y) {
        if (view == null) {
            return null;
        }


        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = DensityUtils.dp2px(mMainActivity, 60);
        params.width = DensityUtils.dp2px(mMainActivity, 60);
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }

        //设置margin
        LogUtilsxp.e2(TAG, "marginParams.width:" + marginParams.width + "marginParams.height:" + marginParams.height);
        //        marginParams.setMargins(x, y, x + marginParams.width, y + marginParams.height);
        marginParams.setMargins(x, y, 0, 0);
        view.setLayoutParams(marginParams);
        return marginParams;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onClick(View view) {
        Intent mIntent = null;
        switch (view.getId()) {




            case R.id.ib_home_make_partner:


                JumpShareholderOrPartner("partner");

                break;

            case R.id.ib_home_become_shareholder:
                JumpShareholderOrPartner("shareholder");



                break;


            case R.id.rl_bean_seedling_1_1:
                mRl_bean_seedling_1_1.setEnabled(false);
                mIv_bean_seedling_1_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_1_1.setVisibility(View.GONE);


                ViewClickMethod(view, widthHalf, 0);

                break;
            case R.id.rl_bean_seedling_2_1:

                mRl_bean_seedling_2_1.setEnabled(false);
                mIv_bean_seedling_2_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_2_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 1);

                break;
            case R.id.rl_bean_seedling_2_2:
                mRl_bean_seedling_2_2.setEnabled(false);
                mIv_bean_seedling_2_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_2_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 2);
                break;
            case R.id.rl_bean_seedling_3_1:
                mRl_bean_seedling_3_1.setEnabled(false);
                mIv_bean_seedling_3_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_3_1.setVisibility(View.GONE);

                ViewClickMethod(view, widthHalf, 3);
                break;
            case R.id.rl_bean_seedling_3_2:

                mRl_bean_seedling_3_2.setEnabled(false);
                mIv_bean_seedling_3_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_3_2.setVisibility(View.GONE);

                ViewClickMethod(view, widthHalf, 4);

                break;
            case R.id.rl_bean_seedling_3_3:
                mRl_bean_seedling_3_3.setEnabled(false);
                mIv_bean_seedling_3_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_3_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 5);
                break;
            case R.id.rl_bean_seedling_4_1:
                mRl_bean_seedling_4_1.setEnabled(false);
                mIv_bean_seedling_4_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_4_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 6);
                break;
            case R.id.rl_bean_seedling_4_2:
                mRl_bean_seedling_4_2.setEnabled(false);
                mIv_bean_seedling_4_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_4_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 7);

                break;
            case R.id.rl_bean_seedling_4_3:
                mRl_bean_seedling_4_3.setEnabled(false);
                mIv_bean_seedling_4_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_4_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 8);

                break;
            case R.id.rl_bean_seedling_5_1:
                mRl_bean_seedling_5_1.setEnabled(false);
                mIv_bean_seedling_5_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_5_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 9);
                break;
            case R.id.rl_bean_seedling_5_2:
                mRl_bean_seedling_5_2.setEnabled(false);
                mIv_bean_seedling_5_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_5_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 10);
                break;
            case R.id.rl_bean_seedling_5_3:
                mRl_bean_seedling_5_3.setEnabled(false);
                mIv_bean_seedling_5_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_5_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 11);
                break;
            case R.id.rl_bean_seedling_6_1:
                mRl_bean_seedling_6_1.setEnabled(false);
                mIv_bean_seedling_6_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_6_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 12);
                break;
            case R.id.rl_bean_seedling_6_2:
                mRl_bean_seedling_6_2.setEnabled(false);
                mIv_bean_seedling_6_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_6_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 13);
                break;
            case R.id.rl_bean_seedling_6_3:
                mRl_bean_seedling_6_3.setEnabled(false);
                mIv_bean_seedling_6_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_6_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 14);

                break;
            case R.id.rl_bean_seedling_7_1:
                mRl_bean_seedling_7_1.setEnabled(false);
                mIv_bean_seedling_7_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_7_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 15);
                break;
            case R.id.rl_bean_seedling_7_2:
                mRl_bean_seedling_7_2.setEnabled(false);
                mIv_bean_seedling_7_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_7_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 16);
                break;
            case R.id.rl_bean_seedling_8_1:
                mRl_bean_seedling_8_1.setEnabled(false);
                mIv_bean_seedling_8_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_8_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 17);

                break;


            case R.id.rl_bean_seedling_4_4:
                mRl_bean_seedling_4_4.setEnabled(false);
                mIv_bean_seedling_4_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_4_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 18);

                break;


            case R.id.rl_bean_seedling_6_4:
                mRl_bean_seedling_6_4.setEnabled(false);
                mIv_bean_seedling_6_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_6_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 19);

                break;

            case R.id.rl_bean_seedling_7_3:
                mRl_bean_seedling_7_3.setEnabled(false);
                mIv_bean_seedling_7_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_7_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 20);

                break;


            case R.id.rl_bean_seedling_7_4:
                mRl_bean_seedling_7_4.setEnabled(false);
                mIv_bean_seedling_7_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_7_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 21);

                break;


            case R.id.rl_bean_seedling_7_5:
                mRl_bean_seedling_7_5.setEnabled(false);
                mIv_bean_seedling_7_5.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_7_5.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 22);

                break;

            case R.id.rl_bean_seedling_8_2:
                mRl_bean_seedling_8_2.setEnabled(false);
                mIv_bean_seedling_8_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_8_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 23);

                break;

            case R.id.rl_bean_seedling_8_3:
                mRl_bean_seedling_8_3.setEnabled(false);
                mIv_bean_seedling_8_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_8_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 24);

                break;

            case R.id.rl_bean_seedling_8_4:
                mRl_bean_seedling_8_4.setEnabled(false);
                mIv_bean_seedling_8_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_8_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 25);

                break;


            case R.id.rl_bean_seedling_9_1:
                mRl_bean_seedling_9_1.setEnabled(false);
                mIv_bean_seedling_9_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_9_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 26);

                break;
            case R.id.rl_bean_seedling_9_2:
                mRl_bean_seedling_9_2.setEnabled(false);
                mIv_bean_seedling_9_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_9_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 27);

                break;

            case R.id.rl_bean_seedling_9_3:
                mRl_bean_seedling_9_3.setEnabled(false);
                mIv_bean_seedling_9_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_9_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 28);

                break;
            case R.id.rl_bean_seedling_9_4:
                mRl_bean_seedling_9_4.setEnabled(false);
                mIv_bean_seedling_9_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_9_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 29);

                break;
            case R.id.rl_bean_seedling_9_5:
                mRl_bean_seedling_9_5.setEnabled(false);
                mIv_bean_seedling_9_5.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_9_5.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 30);

                break;

            case R.id.rl_bean_seedling_10_1:
                mRl_bean_seedling_10_1.setEnabled(false);
                mIv_bean_seedling_10_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_10_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 31);

                break;
            case R.id.rl_bean_seedling_10_2:
                mRl_bean_seedling_10_2.setEnabled(false);
                mIv_bean_seedling_10_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_10_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 32);

                break;
            case R.id.rl_bean_seedling_10_3:
                mRl_bean_seedling_10_3.setEnabled(false);
                mIv_bean_seedling_10_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_10_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 33);

                break;
            case R.id.rl_bean_seedling_10_4:
                mRl_bean_seedling_10_4.setEnabled(false);
                mIv_bean_seedling_10_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_10_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 34);

                break;


            case R.id.rl_bean_seedling_11_1:
                mRl_bean_seedling_11_1.setEnabled(false);
                mIv_bean_seedling_11_1.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_11_1.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 35);

                break;

            case R.id.rl_bean_seedling_11_2:
                mRl_bean_seedling_11_2.setEnabled(false);
                mIv_bean_seedling_11_2.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_11_2.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 36);

                break;

            case R.id.rl_bean_seedling_11_3:
                mRl_bean_seedling_11_3.setEnabled(false);
                mIv_bean_seedling_11_3.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_11_3.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 37);

                break;
            case R.id.rl_bean_seedling_11_4:
                mRl_bean_seedling_11_4.setEnabled(false);
                mIv_bean_seedling_11_4.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_11_4.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 38);

                break;

            case R.id.rl_bean_seedling_11_5:
                mRl_bean_seedling_11_5.setEnabled(false);
                mIv_bean_seedling_11_5.setBackgroundResource(R.drawable.shumiao);
                mTv_bean_seedling_11_5.setVisibility(View.GONE);
                ViewClickMethod(view, widthHalf, 39);

                break;

            case R.id.tv_home_put_the_bird:
                // 放鸟

                isFirstEnterPager = false;
                if (longitude != 0.0 && latitude != 0.0) {
                    mIntent = new Intent(mMainActivity, PutTheBirdWithVideoActivity.class);
                    //                    mIntent = new Intent(mMainActivity, PutTheBirdActivity.class);
                    mIntent.putExtra("title", "发元宝广告");
                    mIntent.putExtra("addr", addr);//具体地址
                    mIntent.putExtra("city", city);//城市
                    mIntent.putExtra("district", district);//区域
                    mIntent.putExtra("latitude", latitude);//获取纬度信息
                    mIntent.putExtra("longitude", longitude);//获取经度信息

                    mMainActivity.startActivity(mIntent);
                } else {
                    ToastUtils.showToast(mMainActivity, "正在请求定位,请稍后再次点击");
                }


                break;

            case R.id.ib_home_put_the_bird:
                // 发豆

                isFirstEnterPager = false;
                if (longitude != 0.0 && latitude != 0.0) {
                    mIntent = new Intent(mMainActivity, PutTheBirdWithVideoActivity.class);
                    //                    mIntent = new Intent(mMainActivity, PutTheBirdActivity.class);
                    mIntent.putExtra("title", "发元宝");
                    mIntent.putExtra("addr", addr);//具体地址
                    mIntent.putExtra("city", city);//城市
                    mIntent.putExtra("district", district);//区域
                    mIntent.putExtra("latitude", latitude);//获取纬度信息
                    mIntent.putExtra("longitude", longitude);//获取经度信息

                    mMainActivity.startActivity(mIntent);
                } else {
                    ToastUtils.showToast(mMainActivity, "正在请求定位,请稍后再次点击");
                }


                break;
            case R.id.tv_home_for_the_bird:
                //寻鸟
                isFirstEnterPager = false;
                long time = System.currentTimeMillis();
                String forTheBirdUrl = HttpsApi.HTML_FOR_THE_BIRD + "?t=" + time;
                mIntent = new Intent(mMainActivity, ForTheBirdHtmlActivity.class);
                mIntent.putExtra("title", " ");
                mIntent.putExtra("url", forTheBirdUrl);

                mMainActivity.startActivity(mIntent);
                break;

            case R.id.tv_home_refresh:
                //请求鸟的列表
                mDataListSystemTemp.clear();

                mDataListSystem.clear();
                homeRoot.removeAllViews();
                requestBirdListData(longitude, latitude);

                break;
            case R.id.rl_announcement:
                mIntent = new Intent(mMainActivity, AnnouncementPageActivity.class);
                mIntent.putExtra("title", "公告");

                mMainActivity.startActivity(mIntent);

                break;
            case R.id.tv_map:
                mIntent = new Intent(mMainActivity, TopLineFragmentActivity.class);
                mIntent.putExtra("type", "map");

                mMainActivity.startActivity(mIntent);

                break;
            case R.id.tv_list:
                mIntent = new Intent(mMainActivity, TopLineFragmentActivity.class);
                mIntent.putExtra("type", "list");

                mMainActivity.startActivity(mIntent);

                break;
            //攻略
            case R.id.iv_gong_lv:
                mIntent = new Intent(mMainActivity, HowToPlayActivity.class);
                mIntent.putExtra("title", "攻略");
                mMainActivity.startActivity(mIntent);

                break;
            //转盘
            case R.id.iv_cash_lucky_draw:
                isCashDraw = false;

                String birdMoneyNumber = birdMoney.getText().toString();
                LogUtilsxp.e2(TAG, "birdMoneyNumber:" + birdMoneyNumber);


                FragmentManager manager = getFragmentManager();

                CashLuckyDrawFragmentDialog drawFragmentDialog = CashLuckyDrawFragmentDialog.newInstance(birdMoneyNumber + "");
                drawFragmentDialog.setTargetFragment(HomeFragment.this, REQUEST_DATE);
                drawFragmentDialog.show(manager, DIALOG_DATE);

                break;
        }
    }

    private void JumpShareholderOrPartner(final String tagMsg) {
        String isPhone = PrefUtils.readIsPhone(mMainActivity);//0没有绑定手机号  1已经绑定手机号

        LogUtilsxp.e2(TAG, "isPhone:" + isPhone);
        if ("1".equals(isPhone)) {

            String phone = PrefUtils.readPhone(mMainActivity);
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


            CustomTrust customTrust = new CustomTrust(mMainActivity);
            OkHttpClient okHttpClient = customTrust.client;

            final RuntCustomProgressDialog dialog = new RuntCustomProgressDialog(mMainActivity);
            dialog.setMessage("正在加载数据···");
            dialog.show();


            String token = PrefUtils.readToken(mMainActivity);
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


                                    String mUrl = url + "&token=" + token+ "&tagMsg=" + tagMsg;
                                    Intent mIntent = new Intent(mMainActivity, HtmlStorePayActivity.class);
                                    mIntent.putExtra("title", " ");
                                    mIntent.putExtra("url", mUrl);

                                    startActivity(mIntent);

                                } else {
                                    ToastUtils.showToast(mMainActivity, info);
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
            Intent mIntent1 = new Intent(mMainActivity, BindPhoneActivity.class);
            mIntent1.putExtra("title", "绑定手机号");

            startActivity(mIntent1);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        //关闭启动页面播放鸟叫的声音
        Intent intent = new Intent(mMainActivity, yypService.class);
        mMainActivity.stopService(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtilsxp.e2(TAG, "有返回值");

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            String stringExtra = data.getStringExtra(CashLuckyDrawFragmentDialog.EXTRA_DATA);

            if (!TextUtils.isEmpty(stringExtra)) {
                float numMoney = Float.parseFloat(stringExtra);
                birdMoney.setFloat(0.00f, numMoney);
                birdMoney.start();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //用isFirstEnterPager刷新首页列表
        if (!isFirstEnterPager) {
            isFirstEnterPager = true;
            if (mDataListSystemTemp.size() == 0) {
                mDataListSystem.clear();
                homeRoot.removeAllViews();
                requestBirdListData(longitude, latitude);
            }
        }

        //        打开现金抽奖 刷新列表

        if (!isCashDraw) {
            isCashDraw = true;

            mDataListSystemTemp.clear();
            mDataListSystem.clear();
            homeRoot.removeAllViews();
            requestBirdListData(longitude, latitude);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取详细地址信息
            addr = location.getAddrStr();
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            //获取城市
            city = location.getCity();
            //获取区县
            district = location.getDistrict();
            String street = location.getStreet();    //获取街道信息

            //获取纬度信息
            latitude = location.getLatitude();
            //获取经度信息
            longitude = location.getLongitude();
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            PrefUtils.writeCity(city, mMainActivity);

            //请求鸟的列表
            PrefUtils.writeLongitude(longitude + "", mMainActivity);
            PrefUtils.writeLatitude(latitude + "", mMainActivity);
            requestBirdListData(longitude, latitude);

            LogUtilsxp.e2("MyLocationListener", "country:" + country +
                    "province:" + province + "city:" + city +
                    "district:" + district + "street:" + street +
                    "latitude:" + latitude + "longitude:" + longitude);
            LogUtilsxp.e("MyLocationListener", "addr:" + addr);

            if (!TextUtils.isEmpty(city)) {
                if (areaAddress != null) {
                    areaAddress.setText(city);
                }

            }


            mLocationClient.stop();

        }


    }


    // 检查版本接口
    public void checkUpdateMain() {
        app = (MyApplication) MyApplication.getApplication();
        PackageManager manager = mMainActivity.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mMainActivity.getPackageName(), 0);
            // 版本号
            currentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        CustomTrust customTrust = new CustomTrust(mMainActivity);
        OkHttpClient okHttpClient = customTrust.client;

        String token = PrefUtils.readToken(mMainActivity);
        RequestBody requestBody = new FormBody.Builder()
                .add("ver", currentVersionCode + "")
                .add("name", "FuXiBao")
                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.ISNEW)
                .addHeader("Authorization", token)
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

                LogUtilsxp.e2(TAG, "URL_update_result:" + responseString);
                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultcode = object.optInt("result");
                            String msg = object.optString("msg");
                            if (resultcode == 0) {
                                String data = object.optString("data");
                                LogUtilsxp.e2(TAG, "data" + data);
                                if (!TextUtils.isEmpty(data)) {
                                    JSONArray jsonArray = object.optJSONArray("data");
                                    if (jsonArray.length() > 0) {
                                        String downLoadPackage = (String) jsonArray.get(0);

                                        //                                        String serverUrl = HttpsApi.SERVER_URL;
                                        PrefUtils.writeDownloadUrl(downLoadPackage, mMainActivity);

                                        LogUtilsxp.e(TAG, "downLoadPackage:" + downLoadPackage);
                                        showDialogMain();
                                    }
                                }


                            } else {

                                //                        initViews();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(mMainActivity, "请求自动更新失败,请联系客服");
                        }

                    }
                });
            }
        });


    }

    // 显示更新窗口
    private void showDialogMain() {
        View view = mMainActivity.getLayoutInflater().inflate(R.layout.update_dialog, null);
        final Dialog dialog = new Dialog(mMainActivity, R.style.MyDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Button qd = (Button) view.findViewById(R.id.dialog_qd);
        Button qx = (Button) view.findViewById(R.id.dialog_qx);
        qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mMainActivity, NotificationUpdateActivity.class);
                intent.putExtra("title", "下载管理");
                mMainActivity.startActivity(intent);
                app.setDownload(true);
                dialog.dismiss();
            }
        });
        qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//强制更新
                //                dialog.dismiss();
                //                ToastUtils.show(mActivity,"新版本才能给您带来更好的体验！");
                System.exit(0);
            }
        });
        // 设置点击外围解散
        dialog.show();

    }


}
