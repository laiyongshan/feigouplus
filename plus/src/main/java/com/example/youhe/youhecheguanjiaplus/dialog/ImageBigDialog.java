package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

public class ImageBigDialog extends Dialog {
    private Bitmap bitmap;
    private Context context;
    private ImageView big_img;
    private Window window = null;

    public ImageBigDialog(Context context, int themeResId, Bitmap bitmap) {
        super(context, themeResId);
        this.bitmap = bitmap;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_big_img);
        big_img= (ImageView) findViewById(R.id.big_img);
        if(bitmap!=null) {
            big_img.setImageBitmap(bitmap);
        }
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
