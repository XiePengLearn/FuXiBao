package com.shuangpin.rich;

/**
 * 常量类
 * 用来保存一些几乎从不轻易改动的变量
 *
 * Created by xiongmc on 2016/4/28.
 */
public class RBConstants {

//    public static final String URL_SERVER = "http://192.168.11.71:8080/RedBabyServer/";
    public static final String URL_SERVER = "http://192.168.0.103:8080/RedBabyServer/";

    /**
     * 促销快报
     */
    public static final String URL_TOPIC = URL_SERVER + "topic";
    public static final int REQUEST_CODE_TOPIC = 1;

    /**
     * 购物车
     */
    public static final String URL_CART = URL_SERVER + "cart";
    public static final int REQUEST_CODE_CART = 2;

    /**
     * 结算中心
     */
    public static final String URL_CHECKOUT = URL_SERVER + "checkout";
    public static final int REQUEST_CODE_CHECKOUT = 3;

    /**
     * 登录
     */
    public static final String URL_LOGIN = URL_SERVER + "login";
    public static final int REQUEST_CODE_LOGIN = 4;


    /**
     * 登录
     */
    public static final String URL_ADDRESS_LIST = URL_SERVER + "addresslist";
    public static final int REQUEST_CODE_ADDRESS_LIST = 5;

}
