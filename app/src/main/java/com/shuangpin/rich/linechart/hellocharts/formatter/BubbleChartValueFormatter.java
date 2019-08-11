package com.shuangpin.rich.linechart.hellocharts.formatter;


import com.shuangpin.rich.linechart.hellocharts.model.BubbleValue;

public interface BubbleChartValueFormatter {

    public int formatChartValue(char[] formattedValue, BubbleValue value);
}
