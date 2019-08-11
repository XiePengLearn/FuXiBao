package com.shuangpin.rich.ui.activity.mine.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.util.PhoneUtils;
import com.shuangpin.rich.util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutMyselfActivity extends BaseActivity {
    @InjectView(R.id.tv_edition)
     TextView tvEdition;//版本号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_myself);


        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(AboutMyselfActivity.this, R.color.theme_color_title);
        ButterKnife.inject(this);

        tvEdition.setText("版本号"+PhoneUtils.getVersion());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;


        }
    }
}
