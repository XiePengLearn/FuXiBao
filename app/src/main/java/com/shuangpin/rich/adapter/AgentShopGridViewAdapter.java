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
import com.shuangpin.rich.bean.AgentShopListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/3.
 */

public class AgentShopGridViewAdapter extends ArrayAdapter<AgentShopListBean> {

    private Context mContext;
    private int layoutResourceId;
    private List<AgentShopListBean> mGridData = new ArrayList<AgentShopListBean>();
    private String token;


    public AgentShopGridViewAdapter(Context context, int resource, List<AgentShopListBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<AgentShopListBean> mGridData) {
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
            holder.name1 = (TextView) convertView.findViewById(R.id.tv_fans1);
            holder.name2 = (TextView) convertView.findViewById(R.id.tv_fans2);
            holder.name3 = (TextView) convertView.findViewById(R.id.tv_fans3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AgentShopListBean item = mGridData.get(position);
        /**
         *  private String id;
         private String name;
         private String county;
         private String toReferrer;
         */

        String id = item.getId();
        String name = item.getName();
        String county = item.getCounty();
        String toReferrer = item.getToReferrer();


        if (!TextUtils.isEmpty(name)) {

            holder.name1.setText(name);

        } else {
            holder.name1.setText("");
        }
        if (!TextUtils.isEmpty(county)) {
            holder.name2.setText(county);
        } else {
            holder.name2.setText("");
        }
        if (!TextUtils.isEmpty(toReferrer)) {
            holder.name3.setText(toReferrer);
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
