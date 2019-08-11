package com.shuangpin.rich.bean;

/**
 * Created by Administrator on 2018/12/17.
 */

public class LocationAddressBean {

    /**
     * addr : 北京市朝阳区西大望路甲12号北京国家广告产业园生活区1
     * name : 北京有怡公寓
     * point : {"x":116.47730172738007,"y":39.90815292640706}
     */

    private String addr;
    private String name;
    private PointBean point;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointBean getPoint() {
        return point;
    }

    public void setPoint(PointBean point) {
        this.point = point;
    }

    public static class PointBean {
        /**
         * x : 116.47730172738007
         * y : 39.90815292640706
         */

        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
