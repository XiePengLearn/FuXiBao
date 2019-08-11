package com.shuangpin.rich.linechart.hellocharts.listener;


import com.shuangpin.rich.linechart.hellocharts.model.SliceValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int arcIndex, SliceValue value);

}
