package com.shuangpin.rich.bean;

/**
 * Created by Administrator on 2019/1/7.
 */

public class MerchantChartBean {
//    "data": "20190106",
//            "shopView": "227",
//            "view": "256",
//            "shopFans": 39

    private String data;
    private String shopView;
    private String view;
    private String shopFans;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getShopView() {
        return shopView;
    }

    public void setShopView(String shopView) {
        this.shopView = shopView;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getShopFans() {
        return shopFans;
    }

    public void setShopFans(String shopFans) {
        this.shopFans = shopFans;
    }
}
