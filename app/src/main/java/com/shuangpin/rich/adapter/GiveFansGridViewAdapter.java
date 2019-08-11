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
import com.shuangpin.rich.bean.GiveFansBean;
import com.shuangpin.rich.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/12.
 */

public class GiveFansGridViewAdapter  extends ArrayAdapter<GiveFansBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<GiveFansBean> mGridData = new ArrayList<GiveFansBean>();
    private String token;


    public GiveFansGridViewAdapter(Context context, int resource, List<GiveFansBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<GiveFansBean> mGridData) {
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
            holder.tv_pick_bird1 = (TextView) convertView.findViewById(R.id.tv_pick_bird1);
            holder.tv_pick_bird2 = (TextView) convertView.findViewById(R.id.tv_pick_bird2);
            holder.tv_pick_bird3 = (TextView) convertView.findViewById(R.id.tv_pick_bird3);
            holder.tv_pick_bird4 = (TextView) convertView.findViewById(R.id.tv_pick_bird4);
            holder.tv_pick_bird5 = (TextView) convertView.findViewById(R.id.tv_pick_bird5);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GiveFansBean item = mGridData.get(position);
        /**
         * {
         "toUid": "1044",
         "change": "-5.0000",
         "before": "16.2200",
         "after": "11.2200",
         "createdAt": "1548306946"
         }
         */

        String toUid = item.getToUid();
        String change = item.getChange();
        String before = item.getBefore();
        String after = item.getAfter();
        String createdAt = item.getCreatedAt();


        if (!TextUtils.isEmpty(toUid)) {
            holder.tv_pick_bird1.setText(toUid);
        } else {
            holder.tv_pick_bird1.setText("");
        }

        if (!TextUtils.isEmpty(change)) {
            holder.tv_pick_bird2.setText(change);
        } else {
            holder.tv_pick_bird2.setText("");
        }

        if (!TextUtils.isEmpty(before)) {
            holder.tv_pick_bird3.setText(before);
        } else {
            holder.tv_pick_bird3.setText("");
        }

        if (!TextUtils.isEmpty(after)) {
            holder.tv_pick_bird4.setText(after);
        } else {
            holder.tv_pick_bird4.setText("");
        }

        if (!TextUtils.isEmpty(createdAt)) {

            String month = DateUtils.formatDate("yyyy-MM-dd HH:mm:ss", Long.parseLong(createdAt));
            holder.tv_pick_bird5.setText(month);
        } else {
            holder.tv_pick_bird5.setText("");
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_pick_bird1;
        TextView tv_pick_bird2;
        TextView tv_pick_bird3;
        TextView tv_pick_bird4;
        TextView tv_pick_bird5;

    }

}
