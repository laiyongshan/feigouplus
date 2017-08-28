package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.fragment.RealTimeFragment;

/**
 * Created by Administrator on 2017/6/27.
 */

public class KonwDialog extends Dialog implements View.OnClickListener{

    private TextView konw_cancel_tv,konw_sure_tv;
    private RealTimeFragment fragment;

    public KonwDialog(@NonNull Context context,Fragment fragment,@StyleRes int themeResId) {
        super(context, themeResId);
        this.fragment= (RealTimeFragment) fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_konw);

        initView();
    }


    /*
    * 初始化控件
    * */
    private void initView(){
        konw_cancel_tv= (TextView) findViewById(R.id.konw_cancel_tv);
        konw_cancel_tv.setOnClickListener(this);
        konw_sure_tv= (TextView) findViewById(R.id.konw_sure_tv);
        konw_sure_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.konw_cancel_tv:
                dismiss();
                break;

            case R.id.konw_sure_tv:
                fragment.commitOrder();
                dismiss();
                break;
        }
    }
}
