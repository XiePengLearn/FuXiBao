package com.shuangpin.rich.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuangpin.R;
import com.shuangpin.rich.bean.UserClichBean;
import com.shuangpin.rich.util.ImageLoaderOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/21.
 */

public class UserGridViewAdapter extends ArrayAdapter<UserClichBean> {

    private Context mContext;
    private int layoutResourceId;
    private List<UserClichBean> mGridData = new ArrayList<UserClichBean>();
    private String token;


    public UserGridViewAdapter(Context context, int resource, List<UserClichBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<UserClichBean> mGridData) {
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
            holder.name1Url = (ImageView) convertView.findViewById(R.id.goods_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserClichBean item = mGridData.get(position);
        /**
         * {
         "createdAt": "2018-12-20 20:16:10",
         "money": 0.0496,
         "referrer": "",
         "topReferrer": "",
         "referrerMoney": 0,
         "topReferrerMoney": 0,
         "AllMoney": 0.0496
         }
         */
        String createdAt = item.getName();

        ImageLoader.getInstance().displayImage(createdAt, holder.name1Url, ImageLoaderOptions.fadein_options);


        return convertView;
    }

    private class ViewHolder {
        ImageView name1Url;

    }
}
