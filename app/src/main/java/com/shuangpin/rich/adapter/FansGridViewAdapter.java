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
import com.shuangpin.rich.bean.FansReturnsBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/26.
 */

public class FansGridViewAdapter extends ArrayAdapter<FansReturnsBean> {


    private Context mContext;
    private int layoutResourceId;
    private List<FansReturnsBean> mGridData = new ArrayList<FansReturnsBean>();
    private String token;


    public FansGridViewAdapter(Context context, int resource, List<FansReturnsBean> objects, String token) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.token = token;
    }

    public void setGridData(ArrayList<FansReturnsBean> mGridData) {
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
        FansReturnsBean item = mGridData.get(position);
        /**
         * {
         "uid": 3,
         "grade": 2,
         "nickname": "一闪一闪亮晶晶🌟",
         "money": 0'
         //"type": 1,真  0是假
         }
         */

        String grade = item.getGrade();
        String nickname = item.getNickname();
        String uid = item.getUid();
        String money = item.getMoney();
        String type = item.getType();


        if (!TextUtils.isEmpty(grade)) {

            if ("1".equals(grade)) {
//                holder.name1.setText("一级");
                holder.name1.setVisibility(View.GONE);

            } else {
//                holder.name1.setText("二级");
                holder.name1.setVisibility(View.GONE);
            }

        } else {
            holder.name1.setText("");
        }
        if (!TextUtils.isEmpty(nickname)) {
            holder.name2.setText(nickname);
        } else {
            holder.name2.setText("");
        }

        //        if(type.equals("1")){
        if (!TextUtils.isEmpty(money)) {
            float f = Float.parseFloat(money);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(f);//format 返回的是字符串

            holder.name3.setText(p);
        } else {
            holder.name3.setText("");
        }
        //        }else {
        //            holder.name3.setText("购买粉丝");
        //            holder.name32.setText("升级权限");
        //            holder.name3.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //
        //                    Intent mIntent = new Intent(mContext, BuyFansActivity.class);
        //                    mIntent.putExtra("title", "购买粉丝");
        //                    mContext.startActivity(mIntent);
        //                }
        //            });
        //            holder.name32.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //
        //                    Intent mIntent = new Intent(mContext, BuyFansActivity.class);
        //                    mIntent.putExtra("title", "购买粉丝");
        //                    mContext.startActivity(mIntent);
        //                }
        //            });
        //        }

        return convertView;
    }

    private class ViewHolder {
        TextView name1;
        TextView name2;
        TextView name3;

    }
}
