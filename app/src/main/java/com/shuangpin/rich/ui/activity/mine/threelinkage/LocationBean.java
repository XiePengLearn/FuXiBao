package com.shuangpin.rich.ui.activity.mine.threelinkage;

import java.util.List;

/**
 * Created by Xpeng on 2017/7/18 0018.
 */

public class LocationBean {


        /**
         * data : [{"area_id":"1","area_name":"北京","area_parent_id":"0","area_sort":"0","area_deep":"1","area_region":"华北"},{"area_id":"2","area_name":"天津","area_parent_id":"0","area_sort":"0","area_deep":"1","area_region":"华北"}]
         * result : 1
         * msg : 获取成功
         */

        private int result;
        private String msg;
        private List<DataBean> data;

        public int getResult() {
                return result;
        }

        public void setResult(int result) {
                this.result = result;
        }

        public String getMsg() {
                return msg;
        }

        public void setMsg(String msg) {
                this.msg = msg;
        }

        public List<DataBean> getData() {
                return data;
        }

        public void setData(List<DataBean> data) {
                this.data = data;
        }

        public static class DataBean {
                /**
                 * area_id : 1
                 * area_name : 北京
                 * area_parent_id : 0
                 * area_sort : 0
                 * area_deep : 1
                 * area_region : 华北
                 */

                private String area_id;
                private String area_name;
                private String area_parent_id;
                private String area_sort;
                private String area_deep;
                private String area_region;

                public String getArea_id() {
                        return area_id;
                }

                public void setArea_id(String area_id) {
                        this.area_id = area_id;
                }

                public String getArea_name() {
                        return area_name;
                }

                public void setArea_name(String area_name) {
                        this.area_name = area_name;
                }

                public String getArea_parent_id() {
                        return area_parent_id;
                }

                public void setArea_parent_id(String area_parent_id) {
                        this.area_parent_id = area_parent_id;
                }

                public String getArea_sort() {
                        return area_sort;
                }

                public void setArea_sort(String area_sort) {
                        this.area_sort = area_sort;
                }

                public String getArea_deep() {
                        return area_deep;
                }

                public void setArea_deep(String area_deep) {
                        this.area_deep = area_deep;
                }

                public String getArea_region() {
                        return area_region;
                }

                public void setArea_region(String area_region) {
                        this.area_region = area_region;
                }
        }
}
