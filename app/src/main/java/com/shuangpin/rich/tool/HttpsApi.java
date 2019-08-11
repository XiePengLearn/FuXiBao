package com.shuangpin.rich.tool;

/**
 * Created by Administrator on 2018/12/11.
 * 网络请求API
 */

public class HttpsApi {
    public static final String FORMAL_IP =


            "abundant.xjkrfx.net";/* 富硒宝域名接口 */
    //            "www.wodsd.com";/* 正式版APP服务端接口 */

    public final static String IP = FORMAL_IP,
            PORT = "",
            SERVER_URL = "https://" + IP + PORT,
            URL_LOGIN = "/api/m1/auth/login",//登录
            URL_BIRD_LIST = "/api/m1/gold/list",//首页豆苗列表

    URL_BIRD_CLICK = "/api/m1/gold/click",//点豆苗 领红包
            URL_BIRD_SEND = "/api/m1/gold/send",//用户 发豆
            URL_BIRD_MAP = "/api/m1/gold/map",//逛街寻鸟
            URL_SEND_BIRD = "/api/m1/shop/send-gold",//逛街寻鸟看店送豆
            URL_BIRD_FANS = "/api/m1/gold/gold-log",//拣鸟收益
            URL_BEAN_LIST = "/api/m1/shop/gold-list",//已抢豆列表
            URL_IS_CHECK = "/api/m1/user/is-check",//判断是否在审核期间
            URL_PAY_LOG = "/api/m1/user/pay-log",//消费记录
            URL_TURN_PLATE = "/api/m1/main/turnplate",//大转盘接口


    GET_CITY = "/api/m1/city/list",//申请商户,获取城市数据
            URL_CREATE = "/api/m1/shop/create",//申请商户,提交审核

    URL_SHOP_UPDATA = "/api/m1/shop/update",//修改商户,提交审核
            URL_PRODUCT = "/api/m1/shop/product",//申请商户,发表产品
            URL_GETARROUND = "/api/m1/gold/get-around",//放鸟 获取当前位置
            URL_GET_SEARCH = "/api/m1/gold/get-search",//放鸟 搜索位置
            URL_CITY_LOCATION = "/api/m1/city/location",//根据经纬度  获取定位的方法

    URL_USER_INDEX = "/api/m1/user/index",//个人中心  接口
            URL_ANNOUNCEMENT_LIST = "/api/m1/main/announcement-list",//公告列表
            URL_ANNOUNCEMENT = "/api/m1/main/announcement",//公告详情页面
            URL_BIRD_SCOPE = "/api/m1/shop/gold-scope",//商户发广告范围
            URL_BUY_BIRD = "/api/m1/shop/buy-gold",//发店外鸟


    URL_ADV_RECORD = "/api/m1/gold/adv-record",//看广告记录
            URL_SHOP_VIEW = "/api/m1/shop/view",//个人发鸟  查看详情
            URL_PRODUCT_VIEW = "/api/m1/shop/product-view",//商户广告详情  查看详情

    URL_SEND_CAPTCHA = "/api/m1/user/send-captcha",//获取验证码
            URL_ADD_PHONE = "/api/m1/user/add-phone",//绑定手机号
            URL_FANS_LIST = "/api/m1/user/fans-list",//个人中心_粉丝列表
            URL_PRIZE_LOG = "/api/m1/main/prize-log",//中奖纪录
            URL_PROXY_LIST = "/api/m1/user-proxy/list",//个人中心_代理收益
            URL_FANS = "/api/m1/user/fans",//购买粉丝 配置
            URL_INVITE_POSTER = "/api/m1/user/invite-poster",//
            URL_CLEAR = "/api/m1/shop/clear",// 清店外鸟
            URL_BUY_FANS = "/api/m1/user/buy-fans",// 购买粉丝
            URL_USER_PROXY = "/api/m1/user-proxy/create",// 购买代理
            URL_USER_MONEY = "/api/m1/user/money",// 个人中心 余额
            URL_SHOP_INFO = "/api/m1/shop/info",// 个人中心 余额
            URL_SEND_OUT_IN = "/api/m1/shop/send-out-in",// 发店内鸟
            URL_FOCUS = "/api/m1/user/focus",// 关注
            URL_FRIEND = "/api/m1/user/friends",// 好友
            URL_IS_SEND = "/api/m1/user-send/is-send",// 发现 显示加号
            URL_SEND = "/api/m1/user-send/send",// 发朋友圈
            URL_TRANSFER_FANS = "/api/m1/user/transfer-fans",// 转粉丝
            URL_TRANSFER_FANS_LIST = "/api/m1/user/transfer-fans-list",// 转粉丝记录
            URL_QRCODE = "/api/m1/shop/qrcode",// 商户收款二维码
            URL_EDIT_IMG = "/api/m1/user/edit-img",// 修改头像
            URL_EDIT_NICKNAME = "/api/m1/user/edit-nickname",// 修改昵称
            URL_TI_XIAN = "/api/m1/withdrawal/ti-xian",// 提现
            URL_MONEY = "/api/m1/user/money",// 余额
            URL_WITHDRAWAL_LIST = "/api/m1/withdrawal/list",// 提现记录
            URL_SHOP_QRCODE = "/api/m1/shop/shop-qrcode",//商户扫二维码支付
            URL_QRCODE_DATA = "/api/m1/shop/qrcode-data",//商户二维码支付余额和折扣

    URL_WITHDRAW_DATA = "/api/m1/withdrawal/withdraw-data",//是否设置余额抵扣
            URL_WITHDRAW_WALLET = "/api/m1/withdrawal/wallet",//抵扣比例设置
            URL_SHOP_LISTS = "/api/m1/shop/lists",//商户收款和用户支付记录


    //            拣到商户红包
    //    https://pick.shuangpinkeji.com/jiandao/html/shop_red/shop_red.html
    //    拣到用户红包
    //    https://pick.shuangpinkeji.com/jiandao/html/user_red/user_red.html
    /**
     * 以下是html链接
     */
    //https://pick.shuangpinkeji.com/newjiandao/html/find/find.html

    HTML_DISCOVER = SERVER_URL + "/newjiandao/html/find/find.html",//发现链接
            HTML_FOR_THE_BIRD = SERVER_URL + "/jiandao/html/pick_bird/pick_bird.html",//首页寻鸟 链接
    //            HTML_SHOP_RED_PACKET = SERVER_URL + "/newjiandao/html/shop_red/shop_red.html",//拣到商户红包
    HTML_SHOP_RED_PACKET = SERVER_URL + "/newjiandao/html/newShop/newShop.html",//拣到商户红包
            HTML_USER_RED_PACKET = SERVER_URL + "/newjiandao/html/user_red/user_red.html",//拣到用户红包

    HTML_SHARE_SHOP = "https://abundant.xjkrfx.net/newjiandao/html/shareShop/shareShop.html?",//商户详情分享出去的页面
            HTML_DYNAMIC = SERVER_URL + "/newjiandao/html/dynamic/dynamic.html",//动态

    SHANGHU = SERVER_URL + "/jiandao/html/register/shanghu.html",//商户协议
            REGISTER = SERVER_URL + "/jiandao/html/register/register.html",//登录协议
            DAILIXIEYI = SERVER_URL + "/jiandao/html/register/dailixieyi.html",//代理协议
            ABOUTUS = SERVER_URL + "/jiandao/html/aboutus/aboutus.html",//关于我们
            GONGLUE = SERVER_URL + "/jiandao/html/register/gonglue.html",//攻略


    SHOPHTML = "http://pickshop.shuangpinkeji.com/mobile",//商城


    ONLINE_SERVICE = SERVER_URL + "/jiandao/html/personalCenter/onlineService/onlineService.html",//在线客服
            SHARE_SHARE = SERVER_URL + "/jiandao/html/personalCenter/share/share.html",//分享页
            DYNAMIC = SERVER_URL + "/jiandao/html/user_red/user_red.html",//放鸟  点击商户鸟详情 朋友圈 动态页
    /**
     * 阿里云 OSS地址链接
     */
    OSS_ENDOPINT = "http://oss-cn-beijing.aliyuncs.com",//阿里云OSS ENDOPINT 阿里云地址
            OSS_OSSBUCKET = "abundant",//阿里云OSS OSSBUCKET 仓库名字

    ISNEW = "/api/m1/version/update",//自动更新都是是这个接口
            LOGIN_REGISTER = "http://shop.xjkrfx.net/mobile/index.php?m=user&c=login&a=autologin",//判断商城注册
                SHARE_SHOP_URL = "http://shop.xjkrfx.net/mobile/index.php",//判断商城注册
    LOGIN_STORE = "http://shop.xjkrfx.net/mobile/index.php?m=user&c=login&a=autologin"//判断商城注册

            ;


}
