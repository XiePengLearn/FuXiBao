package com.shuangpin.rich.bean;

/**
 * Created by Administrator on 2018/12/26.
 */

public class FansReturnsBean {
    /*
     "uid": 3,
 "grade": 2,
 "nickname": "一闪一闪亮晶晶🌟",
  "money": 0
    */
    private String uid;
    private String grade;
    private String nickname;
    private String money;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

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
}
