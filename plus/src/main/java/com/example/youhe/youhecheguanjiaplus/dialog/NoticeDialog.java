package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;


/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class NoticeDialog extends Dialog implements View.OnClickListener {

    private ImageView notice_img;
    private TextView notic_tv;
    private TextView iknow_tv;

    private String msg="";
    private String imgUrl="";

    private Window window = null;

    private BitmapManager bmpManager;//图片加载管理工具类
    private Context context;

    public NoticeDialog(Context context, int themeResId, String msg, String imgUrl) {
        super(context, themeResId);
        this.msg=msg;
        this.imgUrl=imgUrl;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notice);

        //设置触摸对话框意外的地方不取消对话框
        setCanceledOnTouchOutside(false);

        bmpManager= new BitmapManager(BitmapFactory.decodeResource(context.getResources(),R.drawable.zhengmian),context);

        initViews();//初始化控件
    }

    private void initViews(){
        notice_img= (ImageView) findViewById(R.id.notice_img);
        notic_tv= (TextView) findViewById(R.id.notic_tv);
        iknow_tv= (TextView) findViewById(R.id.iknow_tv);
        iknow_tv.setOnClickListener(this);
        if(msg!=null) {
            notic_tv.setText(msg);
        }

        if(imgUrl!=null){
            loadBitmap(imgUrl,notice_img);
        }

    }

    public void loadBitmap(String url,ImageView img){
        if (url!=null&&url.endsWith("portrait.gif") || StringUtils.isEmpty(url)) {

        } else {
            if (!url.contains("http")) {
                url = url;
            }
            bmpManager.loadBitmap(url,img);
        }
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
            window.setBackgroundDrawableResource(R.color.lucency); //设置对话框背景为半透明
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
            case R.id.iknow_tv:
                dismiss();
                break;
            case R.id.notice_img:
                break;
            case R.id.notic_tv:

                break;
        }
    }
}
