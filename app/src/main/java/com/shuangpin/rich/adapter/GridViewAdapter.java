package com.shuangpin.rich.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.bean.PickABirdReturnsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/20.
 */

public class GridViewAdapter extends ArrayAdapter<PickABirdReturnsBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<PickABirdReturnsBean> mGridData = new ArrayList<PickABirdReturnsBean>();
    private String token;


    public GridViewAdapter(Context context, int resource, List<PickABirdReturnsBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<PickABirdReturnsBean> mGridData) {
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
            holder.name1 = (TextView) convertView.findViewById(R.id.tv_pick_bird1);
            holder.name31 = (TextView) convertView.findViewById(R.id.tv_pick_bird31);
            holder.name32 = (TextView) convertView.findViewById(R.id.tv_pick_bird32);
            holder.name5 = (TextView) convertView.findViewById(R.id.tv_pick_bird5);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PickABirdReturnsBean item = mGridData.get(position);
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
        String createdAt = item.getCreatedAt();
        String money = item.getMoney();
        String referrer = item.getReferrer();
        String topReferrer = item.getTopReferrer();
        String referrerMoney = item.getReferrerMoney();
        String topReferrerMoney = item.getTopReferrerMoney();
        String AllMoney = item.getAllMoney();

        if (!TextUtils.isEmpty(createdAt)) {
            holder.name1.setText(createdAt);
        }

        if (!TextUtils.isEmpty(referrer)) {
            holder.name31.setText(referrer);
        }
        if (!TextUtils.isEmpty(topReferrer)) {
            holder.name32.setText(topReferrer);
        }

        if (!TextUtils.isEmpty(money)) {
            holder.name5.setText(money);
        }


        return convertView;
    }

    private class ViewHolder {
        TextView name1;
        TextView name31;
        TextView name32;
        TextView name5;
    }
}
