package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommentWebActivity;

/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class AlipayAuthentDialog extends Dialog implements View.OnClickListener{

    private TextView cancel_tv,alipay_authent_sure_tv,alipay_authent_msg_tv;
    private Activity context;
    private String userIdentityUrl;
    private String host;
    private String[] cookieArray;
    private String show_msg;

    public AlipayAuthentDialog(Activity context, int themeResId, String userIdentityUrl, String host, String[] cookieArray, String show_msg) {
        super(context, themeResId);
        this.context=context;
        this.userIdentityUrl=userIdentityUrl;
        this.host=host;
        this.cookieArray=cookieArray;

        this.show_msg=show_msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alipay_authent);

        initView();//初始化控件
    }

    private void initView(){
        cancel_tv= (TextView) findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);
        alipay_authent_sure_tv= (TextView) findViewById(R.id.alipay_authent_sure_tv);
        alipay_authent_sure_tv.setOnClickListener(this);

        alipay_authent_msg_tv= (TextView) findViewById(R.id.alipay_authent_msg_tv);
        alipay_authent_msg_tv.setText(show_msg+"");
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
            window.setBackgroundDrawableResource(R.color.lucency); //设置对话框背景为透明
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
        switch (view.getId()){
            case R.id.cancel_tv:
                dismiss();
                break;

            case R.id.alipay_authent_sure_tv:
                Intent intent=new Intent(context, CommentWebActivity.class);
                intent.putExtra("url",userIdentityUrl);
                intent.putExtra("title","支付宝认证");
                intent.putExtra("type",2);
                intent.putExtra("host",host);
                intent.putExtra("cookieArray",cookieArray);
//                context.startActivity(intent);
                context.startActivityForResult(intent,01);
                dismiss();
                break;
        }
    }
}
