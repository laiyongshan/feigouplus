package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class CarOwnerTypeDialog extends Dialog implements View.OnClickListener{

    private Window window = null;
    private Button register_next2_btn;

    private Context context;

    private ImageView car_owner_back_img;

    private LinearLayout emty_layout;


    public CarOwnerTypeDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_type_layout);

        initView();//初始化控件
    }

    private void initView(){
        register_next2_btn= (Button) findViewById(R.id.register_next2_btn);
        register_next2_btn.setOnClickListener(this);

        car_owner_back_img= (ImageView) findViewById(R.id.car_owner_back_img);
        car_owner_back_img.setOnClickListener(this);

        emty_layout= (LinearLayout) findViewById(R.id.emty_layout);
    }


    public void showDialog() {
        windowDeploy();
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(false);
        show();
    }


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
            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register_next2_btn:
                //跳转到验证码
//                VerificationCodeDialog verificationCodeDialog=new VerificationCodeDialog(context,R.style.Dialog);
//                verificationCodeDialog.showDialog();

                break;

            case R.id.car_owner_back_img:
                dismiss();
                break;
        }
    }
}
