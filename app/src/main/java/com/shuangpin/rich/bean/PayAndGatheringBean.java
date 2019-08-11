package com.shuangpin.rich.bean;

/**
 * Created by Administrator on 2019/4/2.
 */

public class PayAndGatheringBean {


    /**
     * money : 0.01
     * discount : 0.00
     * createdAt : 2019-04-02 03:18:39
     * payType : alipay
     * shopName : 拣到互联网科技有限公司
     * paytype : 支付宝
     * amount : 0.01
     */

    private String money;
    private String discount;
    private String createdAt;
    private String payType;
    private String shopName;
    private String paytype;


    private String nickname;


    private double amount;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
