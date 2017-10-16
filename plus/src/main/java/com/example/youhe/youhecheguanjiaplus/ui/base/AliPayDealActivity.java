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
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.biz.ThePosPay;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/9.
 */

public class AliPayDealActivity extends Activity implements View.OnClickListener {

    private TextView pay_time_tv, order_code_tv, succ_pay_money_tv;
    private Button finish_btn;
    private ImageButton balance_pay_back_ib;

    private String paymoney;
    private String ordercode;
    private String paytime;
    private Bundle dataBundle = null;

    private long startTime;

    public static final String EXTRA_CUSTOMER_BUNDLE = "customer_bundle";
    public Bundle customerBundle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_pay_deal);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, AliPayDealActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AliPayDealActivity.this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_CUSTOMER_BUNDLE))
            customerBundle = intent.getBundleExtra(EXTRA_CUSTOMER_BUNDLE);
        ordercode = intent.getStringExtra("ordercode");

        initViews();//初始化控件
        startTime = System.currentTimeMillis();
        checkAlipayInfo();
    }

    private void checkAlipayInfo() {

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (System.currentTimeMillis() - startTime > 60000) {
            redrectToFailActivity("获取支付状态失败");
            return;

        }

        HashMap<String, String> hash = new HashMap<>();
        hash.put("token", TokenSQLUtils.check());
        hash.put("ordercode", ThePosPay.getOrenumber());
        VolleyUtil volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        volleyUtil.postRequest(AliPayDealActivity.this, URLs.APPPAY_STATUS, hash, "获取支付状态失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {
//                payStatus    1:未支付，2：支付成功，-1：支付失败 -2：过期或未提交订单
                    String payStatus = dataObject.getString("payStatus");
                    if (payStatus.equals("2")) {
                        String paytime = dataObject.optString("paytime");
                        String paymoney = dataObject.optString("paymoney");
                        String ordercode = dataObject.optString("ordercode");
                        Intent intent = new Intent(AliPayDealActivity.this, BalancePaySuccActivity.class);
                        intent.putExtra("paymoney", StringUtils.isEmpty(paymoney) ? "未知" : paymoney);
                        intent.putExtra("ordercode", StringUtils.isEmpty(ordercode) ? "未知" : ordercode);
                        intent.putExtra("paytime", StringUtils.isEmpty(paytime) ? "未知" : paytime);
                        intent.putExtra(BalancePaySuccActivity.EXTRA_BUNDLE, customerBundle);

                        if (customerBundle != null && customerBundle.containsKey(UserManager.STRING_USER_STATUS)) {
                            UserManager.setUserStatus(UserManager.USER_STATUS_YES);
                        }

                        AliPayDealActivity.this.startActivity(intent);
                        AliPayDealActivity.this.finish();
                    } else if (payStatus.equals("1")) {
                        checkAlipayInfo();
                    } else {
                        if (payStatus.equals("-1"))
                            redrectToFailActivity("该订单支付失败");
                        else if (payStatus.equals("-2"))
                            redrectToFailActivity("该订单过期或未提交");
                        else
                            redrectToFailActivity("获取支付状态失败");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    checkAlipayInfo();
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                checkAlipayInfo();
            }
        });
    }

    private void redrectToFailActivity(String msg) {
        Intent intent = new Intent(AliPayDealActivity.this, FailurePayActivity.class);
        intent.putExtra("show_msg", msg);
        if (customerBundle != null)
            intent.putExtra(BalancePaySuccActivity.EXTRA_BUNDLE, customerBundle);

        AliPayDealActivity.this.startActivity(intent);
//        if (bundle != null)
//            intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    /**
     * 初始化控件
     */
    private void initViews() {

        succ_pay_money_tv = (TextView) findViewById(R.id.succ_pay_money_tv);
        succ_pay_money_tv.setText("订单正在处理，请耐心等待");

        order_code_tv = (TextView) findViewById(R.id.order_code_tv);
        order_code_tv.setText(ordercode);

        balance_pay_back_ib = (ImageButton) findViewById(R.id.balance_pay_back_ib);
        balance_pay_back_ib.setOnClickListener(this);
    }


    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_btn:

                finish();
                break;
        }
    }


    /**
     * 返回键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return false;
        }
        return false;
    }
}
