package com.shuangpin.rich.tool;

/**
 * Created by Administrator on 2019/1/25.
 */

public interface IRaiseNumber {

    void start();

    void setFloat(float fromNum, float toNum);

    void setInteger(int fromNum, int toNum);

    void setDuration(long duration);

    void setOnEndListener(RiseNumberTextView.EndListener callback);
}
