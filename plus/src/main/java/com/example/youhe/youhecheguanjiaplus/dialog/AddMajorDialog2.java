package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.ui.base.AddMajorviolationActivity;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class AddMajorDialog2 extends Dialog implements View.OnClickListener{
    private Activity activity;
    private TextView add_major_violation_cancel_tv,add_major_violation_tv;

    private String carId;

    private boolean isNeed[];

    public AddMajorDialog2(Activity activity, int themeResId,boolean[] isNeed,String carId) {
        super(activity, themeResId);
        this.activity=activity;
        this.isNeed=isNeed;
        this.carId=carId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_major_violation);

        //设置触摸对话框意外的地方不取消对话框
        setCanceledOnTouchOutside(false);

        initView();//初始化控件
    }

    private void initView(){
        add_major_violation_cancel_tv= (TextView) findViewById(R.id.add_major_violation_cancel_tv);
        add_major_violation_cancel_tv.setOnClickListener(this);
        add_major_violation_tv= (TextView) findViewById(R.id.add_major_violation_tv);
        add_major_violation_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_major_violation_cancel_tv://取消
                dismiss();
                break;

            case R.id.add_major_violation_tv:
                Intent intent=new Intent(activity, AddMajorviolationActivity.class);
                intent.putExtra("isNeed",isNeed);
                intent.putExtra("carid",carId);
                activity.startActivity(intent);
                dismiss();
                break;
        }
    }
}
