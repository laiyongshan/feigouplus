package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

public class WaitPayTipsDialog extends Dialog implements View.OnClickListener {
    TextView wait_pay_dialog_cancel_tv,wait_pay_dialog_sure_tv,wai_pay_tips_tv;


    public WaitPayTipsDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait_pay_tips);

        wai_pay_tips_tv= (TextView) findViewById(R.id.wai_pay_tips_tv);
        wait_pay_dialog_sure_tv= (TextView) findViewById(R.id.wait_pay_dialog_sure_tv);
        wait_pay_dialog_sure_tv.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wait_pay_dialog_sure_tv:
                dismiss();
                break;
        }
    }

    /**
     * 通知显示MainActivity那个页面
     */
    @Subscribe
    public void onEve(FirstEvent firstEvent){


    }
}
