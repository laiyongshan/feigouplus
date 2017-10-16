package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * Created by Administrator on 2017/6/9.
 */

public class BalancePaySuccActivity extends Activity implements View.OnClickListener {

    private TextView pay_time_tv, order_code_tv, succ_pay_money_tv;
    private Button finish_btn;
    private ImageButton balance_pay_back_ib;

    private String paymoney;
    private String ordercode;
    private String paytime;
    private Bundle dataBundle = null;

    public static final String EXTRA_BUNDLE="customer_bundle";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_pay_succ);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, BalancePaySuccActivity.this);
        }
        SystemBarUtil.useSystemBarTint(BalancePaySuccActivity.this);

        Intent intent = getIntent();
        paymoney = intent.getStringExtra("paymoney");
        ordercode = intent.getStringExtra("ordercode");
        paytime = intent.getStringExtra("paytime");
        if (intent.hasExtra(EXTRA_BUNDLE))
            dataBundle = intent.getBundleExtra(EXTRA_BUNDLE);
        initViews();//初始化控件
    }

    /**
     * 初始化控件
     */
    private void initViews() {

        succ_pay_money_tv = (TextView) findViewById(R.id.succ_pay_money_tv);
        succ_pay_money_tv.setText("您已使用成功付款" + paymoney + "元！");

        pay_time_tv = (TextView) findViewById(R.id.pay_time_tv);
        pay_time_tv.setText(paytime);
        order_code_tv = (TextView) findViewById(R.id.order_code_tv);
        order_code_tv.setText(ordercode);
        finish_btn = (Button) findViewById(R.id.finish_btn);
        finish_btn.setOnClickListener(this);

        balance_pay_back_ib = (ImageButton) findViewById(R.id.balance_pay_back_ib);
        balance_pay_back_ib.setOnClickListener(this);
    }


    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_btn:
//                intent=new Intent(BalancePaySuccActivity.this,OrderStyleActivity.class);
//                startActivity(intent);
//                finish();
                returnHandle();
                break;
            case R.id.balance_pay_back_ib:
//                intent=new Intent(BalancePaySuccActivity.this,OrderStyleActivity.class);
//                startActivity(intent);
                returnHandle();
                break;
        }
    }

    private void returnHandle() {
        if (dataBundle != null) {
            try {
                Class className = null;
                if (dataBundle.containsKey(ScanQrPayActivity.EXTRA_RETURN_CLASS))
                    className = Class.forName(dataBundle.getString(ScanQrPayActivity.EXTRA_RETURN_CLASS));

                if (className != null) {
                    intent = new Intent(BalancePaySuccActivity.this, className);
                    if (dataBundle.getString(ScanQrPayActivity.EXTRA_RETURN_CLASS).contains("MainActivity"))
                        intent.putExtra(MainActivity.EXTRA_IS_NEW, true);
                    startActivity(intent);
                    finish();
                } else {
                    Boolean is = false;
                    if (dataBundle.containsKey(ScanQrPayActivity.EXTRA_IS_RETURN_RESULT))
                        is = dataBundle.getBoolean(ScanQrPayActivity.EXTRA_IS_RETURN_RESULT);
                    if (is) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        intent = new Intent(BalancePaySuccActivity.this, OrderStyleActivity.class);

                        startActivity(intent);
                        finish();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                intent = new Intent(BalancePaySuccActivity.this, OrderStyleActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            intent = new Intent(BalancePaySuccActivity.this, OrderStyleActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 返回键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            Intent intent = new Intent(BalancePaySuccActivity.this, OrderStyleActivity.class);
//            startActivity(intent);
//            finish();
            returnHandle();
            return false;
        }
        return false;
    }
}
