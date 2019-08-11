package com.shuangpin.rich.linechart.hellocharts.listener;


import com.shuangpin.rich.linechart.hellocharts.model.PointValue;

public interface LineChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int lineIndex, int pointIndex, PointValue value);

}
