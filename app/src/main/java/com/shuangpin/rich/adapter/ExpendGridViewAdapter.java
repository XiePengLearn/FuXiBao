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
import com.shuangpin.rich.bean.ExpendDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/13.
 */

public class ExpendGridViewAdapter extends ArrayAdapter<ExpendDetailBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<ExpendDetailBean> mGridData = new ArrayList<ExpendDetailBean>();
    private String token;


    public ExpendGridViewAdapter(Context context, int resource, List<ExpendDetailBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<ExpendDetailBean> mGridData) {
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
            holder.expend1 = (TextView) convertView.findViewById(R.id.tv_expend1);
            holder.expend2 = (TextView) convertView.findViewById(R.id.tv_expend2);
            holder.expend3 = (TextView) convertView.findViewById(R.id.tv_expend3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ExpendDetailBean item = mGridData.get(position);
        final String createdAt = item.getCreatedAt();
        final String type = item.getType();
        final String change = item.getChange();

        if (!TextUtils.isEmpty(createdAt)) {
            holder.expend1.setText(createdAt);
        }
        if (!TextUtils.isEmpty(type)) {
            holder.expend2.setText(type);
        }
        if (!TextUtils.isEmpty(change)) {
            holder.expend3.setText(change);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView expend1;//时间
        TextView expend2;// 类型
        TextView expend3;// 金额
    }
}
