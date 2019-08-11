package com.shuangpin.rich.linechart.hellocharts.model;

import android.graphics.Color;

import com.shuangpin.app.MyApplication;
import com.shuangpin.rich.linechart.hellocharts.util.ChartUtils;
import com.shuangpin.rich.linechart.utils.CommonUtils;

import java.util.Arrays;


/**
 * Single sub-column value for ColumnChart.
 */
public class SubcolumnValue {

    private float value;
    private float originValue;
    private float diff;
    private int color = ChartUtils.DEFAULT_COLOR;
    private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;
    private char[] label;
    private int labelColor= Color.BLACK;
    private int labelTextSize= CommonUtils.dp2px(MyApplication.getApplication(),14);

    public SubcolumnValue() {
        setValue(0);
    }

    public SubcolumnValue(float value) {
        // point and targetPoint have to be different objects
        setValue(value);
    }

    public SubcolumnValue(float value, int color) {
        // point and targetPoint have to be different objects
        setValue(value);
        setColor(color);
    }

    public SubcolumnValue(SubcolumnValue columnValue) {
        setValue(columnValue.value);
        setColor(columnValue.color);
        this.label = columnValue.label;
    }

    public void update(float scale) {
        value = originValue + diff * scale;
    }

    public void finish() {
        setValue(originValue + diff);
    }

    public float getValue() {
        return value;
    }

    public SubcolumnValue setValue(float value) {
        this.value = value;
        this.originValue = value;
        this.diff = 0;
        return this;
    }

    public SubcolumnValue setTarget(float target) {
        setValue(value);
        this.diff = target - originValue;
        return this;
    }

    public int getColor() {
        return color;
    }

    public SubcolumnValue setColor(int color) {
        this.color = color;
        this.darkenColor = ChartUtils.darkenColor(color);
        return this;
    }
    public SubcolumnValue setLabelColor(int color){
        this.labelColor=color;
        return this;
    }
    public int getLabelColor(){
        return labelColor;
    }
    public SubcolumnValue setLabelTextsize(int size){
        this.labelTextSize=size;
        return this;
    }
    public int getLabelTextSize(){
        return labelTextSize;
    }
    public int getDarkenColor() {
        return darkenColor;
    }


    @Deprecated
    public char[] getLabel() {
        return label;
    }

    public SubcolumnValue setLabel(String label) {
        this.label = label.toCharArray();
        return this;
    }

    public char[] getLabelAsChars() {
        return label;
    }

    @Deprecated
    public SubcolumnValue setLabel(char[] label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return "ColumnValue [value=" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubcolumnValue that = (SubcolumnValue) o;

        if (color != that.color) return false;
        if (darkenColor != that.darkenColor) return false;
        if (Float.compare(that.diff, diff) != 0) return false;
        if (Float.compare(that.originValue, originValue) != 0) return false;
        if (Float.compare(that.value, value) != 0) return false;
        if (!Arrays.equals(label, that.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (originValue != +0.0f ? Float.floatToIntBits(originValue) : 0);
        result = 31 * result + (diff != +0.0f ? Float.floatToIntBits(diff) : 0);
        result = 31 * result + color;
        result = 31 * result + darkenColor;
        result = 31 * result + (label != null ? Arrays.hashCode(label) : 0);
        return result;
    }
}
