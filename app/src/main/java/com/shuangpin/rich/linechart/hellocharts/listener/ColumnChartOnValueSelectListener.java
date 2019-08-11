package com.shuangpin.rich.linechart.hellocharts.listener;


import com.shuangpin.rich.linechart.hellocharts.model.SubcolumnValue;

public interface ColumnChartOnValueSelectListener extends OnValueDeselectListener {

    public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value);

}
