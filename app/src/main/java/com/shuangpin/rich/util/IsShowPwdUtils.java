package com.shuangpin.rich.util;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Xpeng on 2017/9/18 0018.
 */

public class IsShowPwdUtils {
    public IsShowPwdUtils(CheckBox checkBox, EditText editText){
        if (checkBox.isChecked()) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //  密码为明文
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());//  密码为不可见
        }
        editText.postInvalidate();  //刷新editText
        editText.setSelection(editText.getText().length());
    }
}
