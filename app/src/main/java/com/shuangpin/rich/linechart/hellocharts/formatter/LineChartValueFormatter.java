package com.shuangpin.rich.linechart.hellocharts.formatter;


import com.shuangpin.rich.linechart.hellocharts.model.PointValue;

public interface LineChartValueFormatter {

    public int formatChartValue(char[] formattedValue, PointValue value);
}
