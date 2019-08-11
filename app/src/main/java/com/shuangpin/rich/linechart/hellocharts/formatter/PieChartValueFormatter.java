package com.shuangpin.rich.linechart.hellocharts.formatter;


import com.shuangpin.rich.linechart.hellocharts.model.SliceValue;

public interface PieChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SliceValue value);
}
