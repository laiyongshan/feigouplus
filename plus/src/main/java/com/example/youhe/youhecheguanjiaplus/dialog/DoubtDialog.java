package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class DoubtDialog extends Dialog {
    private Context mContext;
    private Window window = null;
    private int drawbleId;
    private ImageView doubt_img;

    public DoubtDialog(Context context, int themeResId, int drawble) {
        super(context, themeResId);
        this.drawbleId = drawble;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_doubt);
        initViews();
        doubt_img.setImageResource(drawbleId);
    }

    private void initViews() {
        doubt_img = (ImageView) findViewById(R.id.doubt_img);
    }

    public void showDialog() {
        windowDeploy();
        //设置触摸对话框意外的地方取消对话框true
        setCanceledOnTouchOutside(true);
        show();
    }

    //设置窗口显示动画
    public void windowDeploy() {
        window = getWindow(); //得到对话框
//        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
//        window.setBackgroundDrawableResource(Color.); //设置对话框背景为半透明
        if(window!=null) {
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
//        wl.x = x; //x小于0左移，大于0右移
//        wl.y = y; //y小于0上移，大于0下移
//        wl.alpha = 0.5f; //设置透明度
            wl.gravity = Gravity.CENTER; //设置重力
            window.setAttributes(wl);
        }
    }
}
