package com.shuangpin.rich.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.shuangpin.R;

/**
 * Created by Administrator on 2019/6/28.
 */

public class RulesFragmentDialog extends DialogFragment implements View.OnClickListener {


    private ImageView mDrawClose;
    private Button mBtnCodingDraw;
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE2 = "DialogDate2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rules_dialog, null);


        mDrawClose = (ImageView) view.findViewById(R.id.iv_draw_close);
        mBtnCodingDraw = (Button) view.findViewById(R.id.btn_again_winning);


        newInstance("");


        mDrawClose.setOnClickListener(this);
        mBtnCodingDraw.setOnClickListener(this);

        return view;
    }

    public static RulesFragmentDialog newInstance(String data) {

        Bundle args = new Bundle();
        args.putString(ARG_DATE, data);
        RulesFragmentDialog rulesFragmentDialog = new RulesFragmentDialog();
        rulesFragmentDialog.setArguments(args);
        return rulesFragmentDialog;

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
            case R.id.iv_draw_close:
                dismiss();
                break;

            case R.id.btn_again_winning:
                //                FragmentManager manager = getFragmentManager();
                //                CashLuckyDrawFragmentDialog drawFragmentDialog = new CashLuckyDrawFragmentDialog();
                //                drawFragmentDialog.show(manager, DIALOG_DATE2);
                dismiss();
                break;


        }
    }


}
