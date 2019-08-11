package com.shuangpin.rich.bean;

/**
 * Created by Administrator on 2018/12/11.
 */

public class SystemBirdListBean {
//    "id": 0,
//            "sign": "52f32d5c06e1e1aea5465c7b303a104a",
//            "money": 0.0704

    /**
     *  "id": 1,
     "bean": "7ea160ea7e826470b4e09f10ea349cb3",
     "type": 0,
     "sys": 1,
     "money": 11.6639,
     "crop": 1
     */
    private String id;
    private String crop;
    private String bean;
    private String bird;
    private String money;
    private String type;
    private String sys;


    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBird() {
        return bird;
    }

    public void setBird(String bird) {
        this.bird = bird;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }
}
