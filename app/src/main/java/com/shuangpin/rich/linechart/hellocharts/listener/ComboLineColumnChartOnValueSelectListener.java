package com.shuangpin.rich.linechart.hellocharts.listener;


import com.shuangpin.rich.linechart.hellocharts.model.PointValue;
import com.shuangpin.rich.linechart.hellocharts.model.SubcolumnValue;

public interface ComboLineColumnChartOnValueSelectListener extends OnValueDeselectListener {

    public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value);

    public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value);

}
