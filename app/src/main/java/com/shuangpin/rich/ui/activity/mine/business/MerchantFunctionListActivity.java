package com.shuangpin.rich.ui.activity.mine.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.ui.activity.mine.PaymentCodeActivity;
import com.shuangpin.rich.ui.activity.video.ModificationADWithVideoActivity;
import com.shuangpin.rich.util.PrefUtils;
import com.shuangpin.rich.util.StatusBarUtil;
import com.shuangpin.rich.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MerchantFunctionListActivity extends BaseActivity {

    private String token;
    private Context mContext;
    private String shopId;


    @InjectView(R.id.rl_merchant_function1)
    RelativeLayout function1;//发布广告
    @InjectView(R.id.rl_merchant_function2)
    RelativeLayout function2;//商户修改信息
    @InjectView(R.id.rl_merchant_function3)
    RelativeLayout function3;//商户收款
    @InjectView(R.id.rl_merchant_function4)
    RelativeLayout function4;//线下商城

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_function_list);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(MerchantFunctionListActivity.this, R.color.theme_color);
        ButterKnife.inject(this);
        mContext = MerchantFunctionListActivity.this;
        token = PrefUtils.readToken(mContext);

        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");

        function1.setOnClickListener(this);
        function2.setOnClickListener(this);
        function3.setOnClickListener(this);
        function4.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent mIntent = null;
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;

            case R.id.rl_merchant_function1:
                //                mIntent = new Intent(mContext, ModificationADActivity.class);
                mIntent = new Intent(mContext, ModificationADWithVideoActivity.class);
                mIntent.putExtra("title", "发布广告");
                mIntent.putExtra("shopId", shopId);
                startActivity(mIntent);

                break;
            case R.id.rl_merchant_function2:

                mIntent = new Intent(mContext, ModificationMerchantActivity.class);
                mIntent.putExtra("title", "合伙人广告位信息修改");
                mIntent.putExtra("shopId", shopId);
                startActivity(mIntent);
                break;
            case R.id.rl_merchant_function3:
                mIntent = new Intent(mContext, PaymentCodeActivity.class);
                mIntent.putExtra("title", "合伙人收款");
                mIntent.putExtra("shopId", shopId);
                startActivity(mIntent);

                break;
            case R.id.rl_merchant_function4:
                ToastUtils.showToast(mContext, "暂无数据");

                break;
        }
    }
}
