package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.fragment.RealTimeFragment;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class Commit12OrderTipsDialog extends Dialog implements View.OnClickListener{

    private TextView commit12_tips_cancel_tv;
    private TextView commit12_tips_sure_tv;

    private TextView commit_order_tips_tv;

    private RealTimeFragment fragment;

    private Context context;

    private String tips;

    public Commit12OrderTipsDialog(Context context,Fragment fragment, int themeResId, String tips) {
        super(context, themeResId);
        this.fragment= (RealTimeFragment) fragment;

        this.tips=tips;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_commit12_tips);

        initView();//初始化控件
    }

    private void initView(){
        commit12_tips_cancel_tv= (TextView) findViewById(R.id.commit12_tips_cancel_tv);
        commit12_tips_cancel_tv.setOnClickListener(this);
        commit12_tips_sure_tv= (TextView) findViewById(R.id.commit12_tips_sure_tv);
        commit12_tips_sure_tv.setOnClickListener(this);

        commit_order_tips_tv= (TextView) findViewById(R.id.commit_order_tips_tv);
        if(tips!=null) {
            commit_order_tips_tv.setText(tips);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit12_tips_cancel_tv:
                dismiss();
                break;

            case R.id.commit12_tips_sure_tv:
                fragment.commitOrder();
                dismiss();
                break;
        }
    }
}
