package com.shuangpin.rich.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/12/20.
 */

public class ForTheBirdMapBean {


    /**
     * id : 1
     * uid : 1
     * shopName : 春伟酱香饼
     * header : http://keran.oss-cn-beijing.aliyuncs.com/shop/1/jpg/1558933575573.jpg
     * longitude : 116.4823861365665
     * latitude : 39.91413007233052
     * address : 大望路地铁
     * info : 大家好 欢迎品尝
     * isOut : 0
     * userHead : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIeiaJP2AFVEOeva2QxlMbBUdrAHhhsJDJcEiaWs34J7ttMl2xPMtM4YMRLia6Oxvnwb1FWKeX8fmRLA/132
     * advertising : {"id":"1","imgsrc":"http://keran.oss-cn-beijing.aliyuncs.com/AD/20190524145239_80242.jpg","url":"www.baidu.com"}
     * balance : 218.74
     * userComment : [{"headImg":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIeiaJP2AFVEOeva2QxlMbBUdrAHhhsJDJcEiaWs34J7ttMl2xPMtM4YMRLia6Oxvnwb1FWKeX8fmRLA/132"}]
     */

    private String id;
    private String uid;
    private String shopName;
    private String header;
    private String longitude;
    private String latitude;
    private String address;
    private String info;
    private String isOut;
    private String userHead;
    private AdvertisingBean advertising;
    private String balance;
    private String bean;
    private List<UserCommentBean> userComment;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIsOut() {
        return isOut;
    }

    public void setIsOut(String isOut) {
        this.isOut = isOut;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public AdvertisingBean getAdvertising() {
        return advertising;
    }

    public void setAdvertising(AdvertisingBean advertising) {
        this.advertising = advertising;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public List<UserCommentBean> getUserComment() {
        return userComment;
    }

    public void setUserComment(List<UserCommentBean> userComment) {
        this.userComment = userComment;
    }

    public static class AdvertisingBean {
        /**
         * id : 1
         * imgsrc : http://keran.oss-cn-beijing.aliyuncs.com/AD/20190524145239_80242.jpg
         * url : www.baidu.com
         */

        private String id;
        private String imgsrc;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImgsrc() {
            return imgsrc;
        }

        public void setImgsrc(String imgsrc) {
            this.imgsrc = imgsrc;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class UserCommentBean {
        /**
         * headImg : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIeiaJP2AFVEOeva2QxlMbBUdrAHhhsJDJcEiaWs34J7ttMl2xPMtM4YMRLia6Oxvnwb1FWKeX8fmRLA/132
         */

        private String headImg;

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }
    }
}
