package com.shuangpin.rich.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shuangpin.R;

/**
 * Created by Administrator on 2019/7/4.
 */

public class CancelAccountDialog extends DialogFragment implements View.OnClickListener {


    private TextView dialog_tv2;
    private Button dialog_qx;
    private Button dialog_qr;
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE2 = "DialogDate2";
    private String mPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cancel_account_dialog, null);
        mPhone = getArguments().getString(ARG_DATE);

        dialog_tv2 = (TextView) view.findViewById(R.id.dialog_tv2);
        dialog_qx = (Button) view.findViewById(R.id.dialog_qx_cancel_account);
        dialog_qr = (Button) view.findViewById(R.id.dialog_qr_cancel_account);

        dialog_tv2.setText("账号注销后所有信息将无法恢复,如需注销账号,请拨打客服电话" + mPhone);


        dialog_qx.setOnClickListener(this);
        dialog_qr.setOnClickListener(this);

        return view;
    }


    public static CancelAccountDialog newInstance(String data) {

        Bundle args = new Bundle();
        args.putString(ARG_DATE, data);
        CancelAccountDialog cancelAccountDialog = new CancelAccountDialog();
        cancelAccountDialog.setArguments(args);
        return cancelAccountDialog;

    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景半透明
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_qx_cancel_account:
                dismiss();
                break;

            case R.id.dialog_qr_cancel_account:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);

                dismiss();
                break;


        }
    }


}
