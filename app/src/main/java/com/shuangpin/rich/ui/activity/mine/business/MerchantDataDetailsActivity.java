package com.shuangpin.rich.ui.activity.mine.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.base.BaseActivity;
import com.shuangpin.rich.bean.MerchantChartBean;
import com.shuangpin.rich.certificate.CustomTrust;
import com.shuangpin.rich.dialog.RuntCustomProgressDialog;
import com.shuangpin.rich.linechart.hellocharts.gesture.ContainerScrollType;
import com.shuangpin.rich.linechart.hellocharts.model.Axis;
import com.shuangpin.rich.linechart.hellocharts.model.AxisValue;
import com.shuangpin.rich.linechart.hellocharts.model.Line;
import com.shuangpin.rich.linechart.hellocharts.model.LineChartData;
import com.shuangpin.rich.linechart.hellocharts.model.PointValue;
import com.shuangpin.rich.linechart.hellocharts.model.ValueShape;
import com.shuangpin.rich.linechart.hellocharts.model.Viewport;
import com.shuangpin.rich.linechart.hellocharts.view.LineChartView;
import com.shuangpin.rich.linechart.utils.CommonUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MerchantDataDetailsActivity extends BaseActivity {

    private static final String TAG = "MerchantDataDetailsActivity";
    private Context mContext;
    private String token;

    @InjectView(R.id.txt_shop_name)
    TextView txtShopName;//店名

    @InjectView(R.id.tv_number1)
    TextView number1;//今日浏览
    @InjectView(R.id.tv_number2)
    TextView number2;//进店浏览
    @InjectView(R.id.tv_number3)
    TextView number3;//共计粉丝
    @InjectView(R.id.tv_number4)
    TextView number4;//剩余店铺鸟
    @InjectView(R.id.tv_number5)
    TextView number5;//剩余店内鸟
    @InjectView(R.id.tv_number6)
    TextView number6;//剩余优惠卷


    @InjectView(R.id.lineView)
    LineChartView lineChartView;
    @InjectView(R.id.tv_fa)
    TextView tvFa;//发

    @InjectView(R.id.rg_content_group_merchant)
    RadioGroup contentGroupMerchant;//发

    @InjectView(R.id.tv_function_management)
    TextView tvFunctionManagement;//功能管理
    private RuntCustomProgressDialog runtDialog;
    private String id;
    private List<MerchantChartBean> mDataList;

    private int maxNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_data_details);

        setTitleBar(SHOW_LEFT);
        StatusBarUtil.setStatusBar(MerchantDataDetailsActivity.this, R.color.theme_color);
        ButterKnife.inject(this);
        mContext = MerchantDataDetailsActivity.this;

        token = PrefUtils.readToken(mContext);
        mDataList = new ArrayList<>();
        //                                    "id": "1",
        //                                            "shopName": "拣到",
        //                                            "name": "拣到",
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String shopName = intent.getStringExtra("shopName");
        String name = intent.getStringExtra("name");

        if (!TextUtils.isEmpty(shopName)) {
            txtShopName.setText(shopName);
        }

        contentGroupMerchant.check(R.id.rb_3_day);// 设置默认选项为商户收益
        // 监听RadioGroup的选中事件,对页面进行切换
        contentGroupMerchant.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_3_day:
                        mDataList.clear();
                        getDataFromServer(3);
                        break;
                    case R.id.rb_7_day:
                        mDataList.clear();
                        getDataFromServer(7);
                        break;

                    case R.id.rb_30_day:
                        mDataList.clear();
                        getDataFromServer(30);
                        break;
                    default:
                        break;
                }
            }
        });


        getDataFromServer(3);


        tvFunctionManagement.setOnClickListener(this);
        tvFa.setOnClickListener(this);


    }

    private void drawLine() {
        if (mDataList.size() > 0) {

            String view = mDataList.get(mDataList.size()-1).getView();
            String shopView = mDataList.get(mDataList.size()-1).getShopView();
            String shopFans = mDataList.get(mDataList.size()-1).getShopFans();
            int intView = Integer.parseInt(view);
            int intShopView = Integer.parseInt(shopView);
            int intShopFans = Integer.parseInt(shopFans);

            if (intView > intShopView) {
                if (intView > intShopFans) {
                    maxNumber = intView;
                } else {
                    maxNumber = intShopFans;
                }


            } else {

                if (intShopView > intShopFans) {
                    maxNumber = intShopView;
                } else {
                    maxNumber = intShopFans;
                }
            }


        }

        LogUtilsxp.e2(TAG, "maxNumber:" + maxNumber);
        //        String[] lineLabels = {"09-12", "09-11", "09-10", "09-09", "09-08", "09-07", "09-06", "09-09", "09-08", "09-07", "09-06"};


        int[] chartColors = new int[]{getResources().getColor(R.color.color_FE5E63), getResources().getColor(R.color.color_6CABFA),
                getResources().getColor(R.color.color_FBE382)};
        int maxNumberOfLines = 3;
        //        int numberOfPoints = lineLabels.length;
        int numberOfPoints = mDataList.size();
        ValueShape shape = ValueShape.CIRCLE;
        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
        for (int i = 0; i < maxNumberOfLines; ++i) {

            if (i == 0) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    //                    randomNumbersTab[i][j] = (float) Math.random() * 100f;
                    randomNumbersTab[i][j] = Float.parseFloat(mDataList.get(j).getView());
                }
            } else if (i == 1) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    randomNumbersTab[i][j] = Float.parseFloat(mDataList.get(j).getShopView());
                }
            } else if (i == 2) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    randomNumbersTab[i][j] = Float.parseFloat(mDataList.get(j).getShopFans());
                }
            }

        }


        List<Line> lines = new ArrayList<Line>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < maxNumberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]).setLabelColor(getResources().getColor(R.color.color_FE5E63))
                        .setLabelTextsize(CommonUtils.dp2px(mContext, 15)));
            }
            Line line = new Line(values);
            line.setColor(chartColors[i]);
            line.setShape(shape);
            line.setPointRadius(3);
            line.setStrokeWidth(1);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            //line.setPointColor(R.color.transparent);
            line.setHasGradientToTransparent(true);
            //            if (pointsHaveDifferentColor){
            //                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            //            }
            lines.add(line);

        }

        LineChartData data = new LineChartData(lines);
        for (int i = 0; i < numberOfPoints; i++) {
            //            axisValues.add(new AxisValue(i).setLabel(lineLabels[i]));
            axisValues.add(new AxisValue(i).setLabel(mDataList.get(i).getData()));
        }
        Axis axisX = new Axis(axisValues).setMaxLabelChars(5);
        axisX.setTextColor(getResources().getColor(R.color.color_969696))
                .setTextSize(8).setLineColor(getResources().getColor(R.color.color_e6e6e6))
                .setHasSeparationLineColor(getResources().getColor(R.color.color_e6e6e6)).setHasTiltedLabels(true);
        data.setAxisXBottom(axisX);
        Axis axisY = new Axis().setHasLines(true).setHasSeparationLine(false).setMaxLabelChars(7);
        axisY.setTextColor(getResources().getColor(R.color.color_969696));
        axisY.setTextSize(8);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);


        lineChartView.setZoomEnabled(false);
        lineChartView.setScrollEnabled(true);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setValueSelectionEnabled(true);//点击折线点可以显示label
        lineChartView.setLineChartData(data);
        // Reset viewport height range to (0,100)
        lineChartView.setViewportCalculationEnabled(false);
        //让布局能够水平滑动要设置setCurrentViewport比setMaximumViewport小
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = 0;
        if (maxNumber == 0) {
            v.top = 105;
        } else {
            v.top = maxNumber + maxNumber*0.2f;
        }

        v.left = 0;
        v.right = numberOfPoints - 1 + 0.5f;
        lineChartView.setMaximumViewport(v);
        v.left = 0;
        v.right = Math.min(6, numberOfPoints - 1 + 0.5f);
        lineChartView.setCurrentViewport(v);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.tv_function_management:
                Intent mIntent = new Intent(mContext, MerchantFunctionListActivity.class);
                mIntent.putExtra("title", "功能列表");
                mIntent.putExtra("shopId", id);
                startActivity(mIntent);


                break;
            case R.id.tv_fa:
//                atOnceChange();

                Intent mIntent1 = new Intent(mContext, HairOutsideTheBirdActivity.class);
                mIntent1.putExtra("title", "发元宝");
                mIntent1.putExtra("shopId", id);
                startActivity(mIntent1);
                break;

        }
    }



    private void atOnceChange() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.at_once_fa_dialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout changeBank = (RelativeLayout) v.findViewById(R.id.rl_wechat_of_v);//微信
        RelativeLayout changeMoneyBag = (RelativeLayout) v.findViewById(R.id.rl_alipay_of_v);//支付宝
        RelativeLayout changeOtherAccount = (RelativeLayout) v.findViewById(R.id.rl_balance_of_v);//余额
        /**
         * if (modePaymentStr.equals("余额")) {
         modePayment = "1";
         } else if (modePaymentStr.equals("微信")) {
         modePayment = "2";
         } else {
         modePayment = "3";
         }
         */
        //发店铺鸟
        changeBank.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, HairOutsideTheBirdActivity.class);
                mIntent.putExtra("title", "发店外元宝");
                mIntent.putExtra("shopId", id);
                startActivity(mIntent);




                dialog.dismiss();
            }
        });
        //发店内鸟
        changeMoneyBag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mContext, HairInsideTheBirdActivity.class);
                mIntent.putExtra("title", "发店内元宝");
                mIntent.putExtra("shopId", id);
                startActivity(mIntent);


                dialog.dismiss();
            }
        });
        //发优惠卷
        changeOtherAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "暂无数据");
//
//                Intent mIntent = new Intent(mContext, SendCouponsActivity.class);
//                mIntent.putExtra("title", "发优惠卷");
//                mIntent.putExtra("shopId", id);
//                startActivity(mIntent);


                dialog.dismiss();
            }
        });

        dialog.show();
    }





    private void getDataFromServer(int day) {

        runtDialog = new RuntCustomProgressDialog(mContext);
        runtDialog.setMessage("数据加载中···");
        runtDialog.show();
        CustomTrust customTrust = new CustomTrust(mContext);
        OkHttpClient okHttpClient = customTrust.client;
        //        参数名	是否必须	类型	说明
        //        id	是	int	商户Id
        //        day	否	int	天数

        RequestBody requestBody = new FormBody.Builder()

                .add("id", id + "")//
                .add("day", day + "")//


                .build();
        Request request = new Request.Builder()
                .url(HttpsApi.SERVER_URL + HttpsApi.URL_SHOP_INFO)
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
                runtDialog.dismiss();
                LogUtilsxp.e2(TAG, "URL_SHOP_INFO_result:" + responseString);

                CommonUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {


                        /**
                         *  {
                         "code": 0,
                         "data": {
                         "list": [{
                         "data": "20190106",
                         "shopView": "227",
                         "view": "256",
                         "shopFans": 39
                         }],
                         "shop": {
                         "id": "16",
                         "view": "287",
                         "shopView": "258",
                         "fans": 45,
                         "outBird": "3604",
                         "inBird": 0,
                         "coupon": 0
                         }
                         },
                         "msg": "成功"
                         }
                         */
                        try {
                            JSONObject object = new JSONObject(responseString);
                            int resultCode = object.optInt("code");
                            String msg = object.optString("msg");
                            if (resultCode == 0) {
                                JSONObject data = object.optJSONObject("data");
                                JSONObject shop = data.optJSONObject("shop");
                                /**
                                 * 	"id": "1",
                                 "view": "1869",
                                 "shopView": "538",
                                 "fans": 76,
                                 "outBird": "364589",
                                 "inBird": 0,
                                 "coupon": 0
                                 */

                                String view = shop.optString("view");
                                String shopView = shop.optString("shopView");
                                String fans = shop.optString("fans");
                                String outBird = shop.optString("outBird");
                                String inBird = shop.optString("inBird");
                                String coupon = shop.optString("coupon");

                                if (!TextUtils.isEmpty(view)) {
                                    number1.setText(view + "次");
                                }
                                if (!TextUtils.isEmpty(shopView)) {
                                    number2.setText(shopView + "次");
                                }
                                if (!TextUtils.isEmpty(fans)) {
                                    number3.setText(fans + "人");
                                }
                                if (!TextUtils.isEmpty(outBird)) {
                                    number4.setText(outBird + "只");
                                }
                                if (!TextUtils.isEmpty(inBird)) {
                                    number5.setText(inBird + "只");
                                }
                                if (!TextUtils.isEmpty(coupon)) {
                                    number6.setText(coupon + "张");
                                }

                                JSONArray jsonArray = data.optJSONArray("list");

                                if (null == jsonArray) {
                                    /**
                                     *  "data": "20190106",
                                     "shopView": "227",
                                     "view": "256",
                                     "shopFans": 39
                                     */
                                } else {
                                    if (jsonArray.length() > 0) {
                                        List<MerchantChartBean> listDetailBean = new ArrayList<>();
                                        for (int z = 0; z < jsonArray.length(); z++) {
                                            MerchantChartBean detailBean = new MerchantChartBean();

                                            detailBean.setData(jsonArray.optJSONObject(z).optString("data"));
                                            detailBean.setShopView(jsonArray.optJSONObject(z).optString("shopView"));
                                            detailBean.setView(jsonArray.optJSONObject(z).optString("view"));
                                            detailBean.setShopFans(jsonArray.optJSONObject(z).optString("shopFans"));

                                            listDetailBean.add(detailBean);
                                        }
                                        mDataList.addAll(listDetailBean);


                                    }
                                    drawLine();
                                    //

                                }

                            } else if (resultCode == 403) {//token失效 重新登录
                                ToastUtils.showToast(mContext, msg);
                                Intent mIntent = new Intent(mContext, LoginActivity.class);
                                mIntent.putExtra("title", "登录");
                                PrefUtils.writeToken("", mContext);
                                startActivity(mIntent);  //重新启动LoginActivity

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
}
