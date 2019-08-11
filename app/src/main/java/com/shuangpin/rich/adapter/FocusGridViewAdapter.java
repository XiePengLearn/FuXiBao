package com.shuangpin.rich.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuangpin.R;
import com.shuangpin.rich.bean.FacusBean;
import com.shuangpin.rich.util.ImageLoaderOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/17.
 */

public class FocusGridViewAdapter extends ArrayAdapter<FacusBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<FacusBean> mGridData = new ArrayList<FacusBean>();
    private String token;


    public FocusGridViewAdapter(Context context, int resource, List<FacusBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<FacusBean> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.shopImg = (ImageView) convertView.findViewById(R.id.tv_shop_head);
            holder.shopName = (TextView) convertView.findViewById(R.id.tv_address_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FacusBean item = mGridData.get(position);
        /**
         * {
         "headImg
         "nickname": "一闪一闪亮晶晶🌟",
         }
         */

        String headImg = item.getHeadImg();
        String nickname = item.getNickname();


        ImageLoader.getInstance().displayImage(headImg, holder.shopImg, ImageLoaderOptions.fadein_options);
        if (!TextUtils.isEmpty(nickname)) {
            holder.shopName.setText(nickname);
        } else {
            holder.shopName.setText("");
        }


        return convertView;
    }

    private class ViewHolder {
        ImageView shopImg;
        TextView shopName;

    }
}
