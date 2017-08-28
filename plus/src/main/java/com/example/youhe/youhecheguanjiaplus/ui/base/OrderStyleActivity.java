package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * Created by Administrator on 2017/5/31.
 */

public class OrderStyleActivity extends Activity implements View.OnClickListener {

    private RelativeLayout annual_order_layout,illegal_order_layout;
    private ImageView back_iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,OrderStyleActivity.this);
        }
        SystemBarUtil.useSystemBarTint(OrderStyleActivity.this);

        setContentView(R.layout.activity_order_style);

        initViews();//初始化控件

    }

    private void initViews(){
        annual_order_layout= (RelativeLayout) findViewById(R.id.annual_order_layout);
        annual_order_layout.setOnClickListener(this);
        illegal_order_layout= (RelativeLayout) findViewById(R.id.illegal_order_layout);
        illegal_order_layout.setOnClickListener(this);

        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this);
    }

    Intent intent;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.annual_order_layout:
                intent=new Intent(OrderStyleActivity.this,OrderActivity.class);
                intent.putExtra("title","年检订单");
                intent.putExtra("orderListType",2);
                startActivity(intent);
                finish();
                break;

            case R.id.illegal_order_layout:
                intent=new Intent(OrderStyleActivity.this,OrderActivity.class);
                intent.putExtra("orderListType",1);
                intent.putExtra("title","普通订单");
                startActivity(intent);
                finish();
                break;

            case R.id.back_iv:
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

}
