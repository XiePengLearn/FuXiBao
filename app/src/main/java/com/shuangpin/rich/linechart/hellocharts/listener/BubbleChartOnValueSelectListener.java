package com.shuangpin.rich.linechart.hellocharts.listener;


import com.shuangpin.rich.linechart.hellocharts.model.BubbleValue;

public interface BubbleChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int bubbleIndex, BubbleValue value);

}
