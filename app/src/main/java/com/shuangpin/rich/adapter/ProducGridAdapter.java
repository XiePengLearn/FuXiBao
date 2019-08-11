package com.shuangpin.rich.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shuangpin.R;

import java.util.List;

/**
 * Created by Administrator on 2018/12/7.
 */

public class ProducGridAdapter extends BaseAdapter {
    private List<Bitmap> gridData;
    private LayoutInflater inflater;

    public ProducGridAdapter(List<Bitmap> gridData, Context context) {
        this.gridData = gridData;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gridData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderGrid holer = null;
        View view = inflater.inflate(R.layout.item_know_gridview, null);
        if (holer == null) {
            holer = new ViewHolderGrid();
            holer.item_know_grid_img = (ImageView) view.findViewById(R.id.item_know_grid_img);
            view.setTag(holer);
        } else {
            holer = (ViewHolderGrid) view.getTag();
        }
        holer.item_know_grid_img.setImageBitmap(gridData.get(position));


        return view;
    }

    //  适配器中的GridView缓存类
    class ViewHolderGrid {
        ImageView item_know_grid_img;

    }

}
