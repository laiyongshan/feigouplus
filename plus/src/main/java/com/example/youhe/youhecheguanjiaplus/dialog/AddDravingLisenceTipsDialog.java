package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.ui.base.UploadProActivity;

/**
 * Created by Administrator on 2016/11/18 0018.
 */

public class AddDravingLisenceTipsDialog extends Dialog implements View.OnClickListener{
    private Activity activity;
    private TextView add_info_cancel_tv,add_info_tv;
    private String carId;

    public static final int UPLOAD_DRAVING_RESULTCODE=110;

    public AddDravingLisenceTipsDialog(Activity activity, int themeResId,String carId) {
        super(activity, themeResId);
        this.activity = activity;
        this.carId=carId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_info_tips);

        //设置触摸对话框意外的地方不取消对话框
        setCanceledOnTouchOutside(false);

        initView();

    }

    private void initView(){
        add_info_cancel_tv= (TextView) findViewById(R.id.add_info_cancel_tv);
        add_info_cancel_tv.setOnClickListener(this);
        add_info_tv= (TextView) findViewById(R.id.add_info_tv);
        add_info_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_info_cancel_tv:
                dismiss();
                break;
            case R.id.add_info_tv:
                Intent intent=new Intent(activity, UploadProActivity.class);
                intent.putExtra("carid",carId);
                activity.startActivityForResult(intent,UPLOAD_DRAVING_RESULTCODE);
                dismiss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            dismiss();
        }
        return super.onKeyDown(keyCode, event);
    }
}
