package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.ui.base.OrderStyleActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.PayActivity;

/**
 * Created by Administrator on 2017/6/20.
 */

public class CommitSucessDialog extends Dialog {

    private Button pay_btn;
    private String ordernumber,price;
    private int ordertype;

    private Activity activity;

    public CommitSucessDialog(@NonNull Activity activity, @StyleRes int themeResId,String ordernumber,int ordertype,String price) {
        super(activity, themeResId);
        this.ordernumber=ordernumber;
        this.ordertype=ordertype;
        this.price=price;
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ccommit_sucss);

        pay_btn= (Button) findViewById(R.id.pay_btn);
        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,PayActivity.class);
                intent.putExtra("ordernumber",ordernumber);
                intent.putExtra("ordertype",ordertype);
                intent.putExtra("zonfakuan",price);
                intent.putExtra("ordertype",2);
                intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_INSPECTION);
                intent.putExtra("zonfuwu","车辆年检");
                activity.startActivity(intent);
                activity.finish();
            }
        });

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
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            Intent intent=new Intent(activity, OrderStyleActivity.class);
            activity.startActivity(intent);
            activity.finish();
            return false;
        }
        return false;
    }
}
