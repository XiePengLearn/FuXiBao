package com.shuangpin.rich.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.shuangpin.rich.util.GlobalParam;
import com.shuangpin.rich.util.LogUtilsxp;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, GlobalParam.WEICHAT_APP_SECRET);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }


    @Override
    public void onResp(final BaseResp resp) {
        if(resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            Log.e("TAG", "onResp:执行了! ");
            int code = resp.errCode;
            switch(code){
                case 0:
                    LogUtilsxp.e2("TAG", "onResp: 成功!" );
                    WXPayHelper.callBack.success();
                    break;
                case -1:
                    LogUtilsxp.e2("TAG", "onResp: 失败" );
                    WXPayHelper.callBack.falure("支付失败");
                    break;
                case -2:
                    LogUtilsxp.e2("TAG", "onResp: 用户取消");
                    //this.finish();
                    WXPayHelper.callBack.cancel();
                    break;
            }
            this.finish();
        }


    }
}
