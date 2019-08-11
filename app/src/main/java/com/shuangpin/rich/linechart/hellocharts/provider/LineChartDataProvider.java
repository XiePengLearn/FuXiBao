package com.shuangpin.rich.linechart.hellocharts.provider;


import com.shuangpin.rich.linechart.hellocharts.model.LineChartData;

public interface LineChartDataProvider {

    public LineChartData getLineChartData();

    public void setLineChartData(LineChartData data);

}
