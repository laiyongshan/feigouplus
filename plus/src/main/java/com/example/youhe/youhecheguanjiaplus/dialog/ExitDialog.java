package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;


/**
 * Created by Administrator on 2017/2/21 0021.
 */

public class ExitDialog extends Dialog implements View.OnClickListener{

    private TextView exit_cancel_tv,exit_sure_tv;
    private Activity activity;

    public ExitDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit);

        initView();
    }

    private void initView(){
        exit_cancel_tv= (TextView) findViewById(R.id.exit_cancel_tv);
        exit_cancel_tv.setOnClickListener(this);
        exit_sure_tv= (TextView) findViewById(R.id.exit_sure_tv);
        exit_sure_tv.setOnClickListener(this);
    }

    public void showDialog() {
        windowDeploy();
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(false);
        show();
    }


    private Window window = null;
    //设置窗口显示动画
    public void windowDeploy() {
        window = getWindow(); //得到对话框
        if(window!=null) {
            window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
            window.setBackgroundDrawableResource(R.color.lucency2); //设置对话框背景为半透明
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
//            wl.x = x; //x小于0左移，大于0右移
//            wl.y = y; //y小于0上移，大于0下移
            // wl.alpha = 0.5f; //设置透明度
            wl.gravity = Gravity.CENTER; //设置重力
            window.setAttributes(wl);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.exit_cancel_tv:
                dismiss();
                break;
            case R.id.exit_sure_tv:
                System.exit(0);
                break;
        }
    }
}
