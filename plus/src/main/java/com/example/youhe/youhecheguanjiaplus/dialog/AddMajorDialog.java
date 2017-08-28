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

public class AddMajorDialog extends Dialog implements View.OnClickListener{

    private Activity activity;
    private String carId;

    private TextView add_major_no_tv,add_major_yes_tv;

    public AddMajorDialog(Activity activity, int themeResId,String carId) {
        super(activity, themeResId);
        this.activity=activity;
        this.carId=carId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_major);

        initViews();//初始化控件
    }

    private void initViews(){
        add_major_no_tv= (TextView) findViewById(R.id.add_major_no_tv);
        add_major_no_tv.setOnClickListener(this);
        add_major_yes_tv= (TextView) findViewById(R.id.add_major_yes_tv);
        add_major_yes_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.add_major_yes_tv:
                Intent intent=new Intent(activity, AddMajorviolationActivity.class);
                intent.putExtra("carid",carId);
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.add_major_no_tv:
                dismiss();
                break;
        }
    }
}
