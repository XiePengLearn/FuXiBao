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
import com.shuangpin.rich.bean.WinningRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/26.
 */

public class WinningRecordGridViewAdapter extends ArrayAdapter<WinningRecordBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<WinningRecordBean> mGridData = new ArrayList<WinningRecordBean>();
    private String token;


    public WinningRecordGridViewAdapter(Context context, int resource, List<WinningRecordBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<WinningRecordBean> mGridData) {
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
            holder.name1 = (TextView) convertView.findViewById(R.id.tv_winning1);
            holder.name2 = (TextView) convertView.findViewById(R.id.tv_winning2);
            holder.name3 = (TextView) convertView.findViewById(R.id.tv_winning3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WinningRecordBean item = mGridData.get(position);
        /**
         *  "createdAt": "2019-06-28 13:48:30",
         "prize": "1罐富硒宝",
         "source": "余额抽奖",
         */

        String createdAt = item.getCreatedAt();
        String prize = item.getPrize();
        String source = item.getSource();


        if (!TextUtils.isEmpty(createdAt)) {

            holder.name1.setText(createdAt);

        } else {
            holder.name1.setText("");
        }

        if (!TextUtils.isEmpty(prize)) {
            holder.name2.setText(prize);
        } else {
            holder.name2.setText("");
        }

        if (!TextUtils.isEmpty(source)) {

            holder.name3.setText(source);
        } else {
            holder.name3.setText("");
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name1;
        TextView name2;
        TextView name3;

    }
}
