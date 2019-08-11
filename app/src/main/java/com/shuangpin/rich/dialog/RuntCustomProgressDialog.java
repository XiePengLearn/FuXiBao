package com.shuangpin.rich.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.shuangpin.R;

/**
 * Created by Administrator on 2018/12/11.
 */

public class RuntCustomProgressDialog extends Dialog {
    private TextView mMessageView;

    public RuntCustomProgressDialog(Context context){
        this(context, "");
    }

    public RuntCustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.loading_dialog, strMessage);
    }

    public RuntCustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.runt_custom_progress_dialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        mMessageView = (TextView) this.findViewById(R.id.tipTextView);
        if ( mMessageView != null) {
            mMessageView.setText(strMessage);
        }
    }


    public void setMessage(String message){
        mMessageView.setText(message);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }
}
