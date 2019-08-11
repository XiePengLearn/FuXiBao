package com.shuangpin.rich.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuangpin.R;
import com.shuangpin.rich.ui.activity.mine.WalletActivity;
import com.shuangpin.rich.ui.cashluckdraw.LuckPanLayout;
import com.shuangpin.rich.util.ToastUtils;

/**
 * Created by Administrator on 2019/6/20.
 */

public class WinningFragmentDialog extends DialogFragment implements LuckPanLayout.AnimationEndListener, View.OnClickListener {

    private ImageView mDrawClose;
    private Button mBtnCodingDraw;
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE2 = "DialogDate2";
    private String mPosition;
    private LinearLayout mLlImRoot;
    private LinearLayout mLlIm2Root;
    private ImageView mPrize;
    private ImageView mPrize2;
    private TextView mViewRules;
    private LinearLayout mLookWinning;
    private static final String RULE_DATE = "RulesDate";
    private TextView mTv_miaoshu1;
    private TextView mTv_miaoshu2;
    private ImageView mLuckpan_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.winning_dialog, null);

        mPosition = getArguments().getString(ARG_DATE);

        mDrawClose = (ImageView) view.findViewById(R.id.iv_draw_close);
        mBtnCodingDraw = (Button) view.findViewById(R.id.btn_again_winning);
        mLlImRoot = (LinearLayout) view.findViewById(R.id.ll_im_root);//现金跟布局
        mLlIm2Root = (LinearLayout) view.findViewById(R.id.ll_im2_root);//实物跟布局
        mPrize = (ImageView) view.findViewById(R.id.iv_prize);
        mPrize2 = (ImageView) view.findViewById(R.id.iv_prize2);
        mLuckpan_layout = (ImageView) view.findViewById(R.id.luckpan_layout);

        mViewRules = (TextView) view.findViewById(R.id.tv_view_rules);

        mTv_miaoshu1 = (TextView) view.findViewById(R.id.tv_miaoshu1);

        mTv_miaoshu2 = (TextView) view.findViewById(R.id.tv_miaoshu2);


        mLookWinning = (LinearLayout) view.findViewById(R.id.ll_look_winning);

        if (mPosition.equals("0")) {

            //1元现金
            mLlImRoot.setVisibility(View.VISIBLE);
            mPrize.setImageResource(R.drawable.one_yuan);
            mLlIm2Root.setVisibility(View.GONE);

            mLookWinning.setVisibility(View.VISIBLE);

        } else if (mPosition.equals("1")) {
            //3元现金

            mLlImRoot.setVisibility(View.VISIBLE);
            mPrize.setImageResource(R.drawable.three_yuan);
            mLlIm2Root.setVisibility(View.GONE);

            mLookWinning.setVisibility(View.VISIBLE);

        } else if (mPosition.equals("2")) {
            //实物
            mLlImRoot.setVisibility(View.GONE);
            mPrize2.setImageResource(R.drawable.gift_pic);
            mLlIm2Root.setVisibility(View.VISIBLE);

            mLookWinning.setVisibility(View.VISIBLE);

        } else if (mPosition.equals("3")) {
            //9元现金
            mLlImRoot.setVisibility(View.VISIBLE);
            mPrize.setImageResource(R.drawable.nine_yuan);
            mLlIm2Root.setVisibility(View.GONE);

            mLookWinning.setVisibility(View.VISIBLE);
        } else if (mPosition.equals("4")) {

            //谢谢参与
            mLuckpan_layout.setImageResource(R.drawable.thanks);
            mLlImRoot.setVisibility(View.GONE);
            mPrize2.setImageResource(R.drawable.no_winning);
            mLlIm2Root.setVisibility(View.VISIBLE);

            mLookWinning.setVisibility(View.GONE);
            mTv_miaoshu1.setText("很遗憾,本次参与未中奖,相信下次抽");
            mTv_miaoshu2.setText("奖会有好运气呦!");

        } else if (mPosition.equals("5")) {
            //1元现金
            mLlImRoot.setVisibility(View.VISIBLE);
            mPrize.setImageResource(R.drawable.one_yuan);
            mLlIm2Root.setVisibility(View.GONE);

            mLookWinning.setVisibility(View.VISIBLE);

        } else if (mPosition.equals("6")) {
            //实物
            mLlImRoot.setVisibility(View.GONE);
            mPrize2.setImageResource(R.drawable.gift_pic);
            mLlIm2Root.setVisibility(View.VISIBLE);

            mLookWinning.setVisibility(View.VISIBLE);

        } else if (mPosition.equals("7")) {
            //6元现金
            mLlImRoot.setVisibility(View.VISIBLE);
            mPrize.setImageResource(R.drawable.six_yuan);
            mLlIm2Root.setVisibility(View.GONE);

            mLookWinning.setVisibility(View.VISIBLE);
        }

        newInstance("");


        mDrawClose.setOnClickListener(this);
        mBtnCodingDraw.setOnClickListener(this);
        mLookWinning.setOnClickListener(this);
        mViewRules.setOnClickListener(this);

        return view;
    }

    public static WinningFragmentDialog newInstance(String data) {

        Bundle args = new Bundle();
        args.putString(ARG_DATE, data);
        WinningFragmentDialog winningFragmentDialog = new WinningFragmentDialog();
        winningFragmentDialog.setArguments(args);
        return winningFragmentDialog;

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
    public void endAnimation(int position) {
        ToastUtils.showToast(getActivity(), "Position = " + position + ",");
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

            case R.id.ll_look_winning:
                Intent mIntent = new Intent(getActivity(), WalletActivity.class);
                mIntent.putExtra("title", "钱包");
                startActivity(mIntent);
                break;
            case R.id.tv_view_rules:
                FragmentManager manager = getFragmentManager();


                RulesFragmentDialog winningFragmentDialog = RulesFragmentDialog.newInstance(1 + "");
                winningFragmentDialog.show(manager, RULE_DATE);
                break;
        }
    }


}
