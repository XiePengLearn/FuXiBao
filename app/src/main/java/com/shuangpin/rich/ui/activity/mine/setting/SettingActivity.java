package com.shuangpin.rich.ui.activity.mine.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.dialog.CancelAccountDialog;
import com.shuangpin.rich.tool.DataCleanManager;
import com.shuangpin.rich.tool.HttpsApi;
import com.shuangpin.rich.ui.activity.logo.LoginActivity;
import com.shuangpin.rich.ui.html.HtmlActivity;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingActivity extends BaseActivity {

    private Context mContext;

    @InjectView(R.id.rl_setting_edit_personal_data)
    RelativeLayout editPersonalData;//编辑个人资料

    @InjectView(R.id.rl_setting_voice)
    RelativeLayout voice;//声音
    @InjectView(R.id.rl_setting_clear_cache)
    RelativeLayout clearCache;//清除缓存
    @InjectView(R.id.tv_clear_cache)
    TextView tvClear;//清除缓存
    @InjectView(R.id.tb1_voice)
    ToggleButton toggleButton;//声音

    @InjectView(R.id.rl_setting_account_binding)
    RelativeLayout accountBinding;//账户绑定
    @InjectView(R.id.rl_setting_about_us)
    RelativeLayout aboutUs;//关于我们
     @InjectView(R.id.rl_setting_cancel_account)
    RelativeLayout cancelAccount;//注销账户
    @InjectView(R.id.rl_tack_out)
    RelativeLayout rlTackOut;//退出登录
    private String headImg;
    private String nickname;
    private static final String RULE_DATE = "RulesDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitleBar(SHOW_LEFT);
        mContext = SettingActivity.this;
        StatusBarUtil.setStatusBar(SettingActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);

        editPersonalData.setOnClickListener(this);
        voice.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        accountBinding.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        rlTackOut.setOnClickListener(this);
        cancelAccount.setOnClickListener(this);

        try {
            String totalCacheSize = DataCleanManager.getTotalCacheSize(mContext);
            tvClear.setText(totalCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean birdVoice = PrefUtils.getBoolean(mContext, "birdVoice", true);
        if (birdVoice) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    PrefUtils.putBoolean(mContext, "birdVoice", true);

                } else {
                    PrefUtils.putBoolean(mContext, "birdVoice", false);


                }
            }
        });

        Intent intent = getIntent();
        headImg = intent.getStringExtra("headImg");
        nickname = intent.getStringExtra("nickname");


    }


    @Override
    public void onClick(View v) {
        Intent mIntent = null;
        switch (v.getId()) {
            case R.id.rl_setting_edit_personal_data:

                //                String headImgUrl = RuntHTTPApi.HEADING_PRE + img;
                mIntent = new Intent(mContext, EditPersonalDataActivity.class);
                mIntent.putExtra("title", "编辑个人资料");
                mIntent.putExtra("headImg", headImg);
                mIntent.putExtra("nickname", nickname);
                startActivity(mIntent);
                break;

            case R.id.rl_setting_voice:


                break;

            case R.id.rl_tack_out:
                UMShareAPI.get(mContext).deleteOauth(SettingActivity.this, SHARE_MEDIA.WEIXIN, authListener);

                break;
            case R.id.rl_setting_clear_cache:
                DataCleanManager.clearAllCache(mContext);
                tvClear.setText("0 K");
                ToastUtils.showToast(mContext, "清理完成!");

                break;
            case R.id.rl_setting_account_binding:


                break;
            case R.id.rl_setting_about_us:
                //                mIntent = new Intent();
                //                mIntent.setClass(mContext, AboutMyselfActivity.class);
                //                mIntent.putExtra("title", "关于我们");
                //                startActivity(mIntent);

                String share_url = HttpsApi.ABOUTUS;
                mIntent = new Intent(mContext, HtmlActivity.class);
                mIntent.putExtra("title", "关于我们");
                mIntent.putExtra("url", share_url);
                startActivity(mIntent);
                break;
            case R.id.rl_setting_cancel_account:
                //注销账户

                FragmentManager manager1 = getSupportFragmentManager();


                CancelAccountDialog dialog = CancelAccountDialog.newInstance("4009600368");
                dialog.show(manager1, RULE_DATE);

                break;
            case R.id.left_btn:
                this.finish();
                break;
        }
    }


    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Toast.makeText(mContext, "退出登录成功", Toast.LENGTH_LONG).show();
            PrefUtils.writeToken("", mContext);
            Intent mIntent = new Intent(mContext, LoginActivity.class);
            mIntent.putExtra("title", "登录");
            mContext.startActivity(mIntent);  //重新启动LoginActivity
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            Toast.makeText(mContext, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
        }
    };

}
