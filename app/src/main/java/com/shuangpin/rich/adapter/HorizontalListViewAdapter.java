package com.shuangpin.rich.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.shuangpin.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2018/12/7.
 */

public class HorizontalListViewAdapter extends BaseAdapter {

    private final Context context;
    private final int screenWidth;
    private final String[] strings;

    public HorizontalListViewAdapter(Context applicationContext, int screenWidth, String[] strings) {
        this.context = applicationContext;
        this.screenWidth = screenWidth;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_horizontallistview, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //        计算每条的宽度
        ViewGroup.LayoutParams layoutParams = holder.rl.getLayoutParams();
        layoutParams.width = screenWidth / 10 * 4;
        holder.rl.setLayoutParams(layoutParams);

        if (position == 0) {
//            holder.rl.setBackgroundResource(R.drawable.home_fa);
        } else if (position == 1) {
//            holder.rl.setBackgroundResource(R.drawable.home_xun);
        } else if (position == 2) {
//            holder.rl.setBackgroundResource(R.drawable.home_guang);
        } else if (position == 3) {
//            holder.rl.setBackgroundResource(R.drawable.home_shang);
        }else if (position == 4) {
//            holder.rl.setBackgroundResource(R.drawable.home_buy_good);
        }



        return convertView;
    }
}

class ViewHolder {

    @InjectView(R.id.rl_home_ad)
    RelativeLayout rl;

    ViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
