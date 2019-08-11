package com.shuangpin.rich.linechart.hellocharts.formatter;


import com.shuangpin.rich.linechart.hellocharts.model.SubcolumnValue;

public interface ColumnChartValueFormatter {

    public int formatChartValue(char[] formattedValue, SubcolumnValue value);

}
