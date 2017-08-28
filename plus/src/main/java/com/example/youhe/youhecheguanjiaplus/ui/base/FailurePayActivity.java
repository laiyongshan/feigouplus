package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * 交易失败界面
 */
public class FailurePayActivity extends AppCompatActivity {

    private Button repay_btn;

    private TextView text1,text2;

    private String error_msg;
    private Bundle dataBundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure_pay);

        in();
    }


    private void in() {
        text1 = (TextView) findViewById(R.id.text);
        text2 = (TextView) findViewById(R.id.textdoll);
        repay_btn = (Button) findViewById(R.id.repay_btn);
        Intent intent = getIntent();
        if (intent.hasExtra("bundle"))
            dataBundle = intent.getBundleExtra("bundle");
        String error_msg=intent.getStringExtra("show_msg");

        text2.setText(error_msg+"");
//         String biaoshi = intent.getStringExtra("biaoshi");
//        if (biaoshi.equals("processingiscomplete")){
//            P92PayBen p92PayBen = (P92PayBen) intent.getSerializableExtra("P92PayBen");
//            text2.setText(p92PayBen.getData().getRes_msg());//显示错误原因
//            text2.setTextSize(30);
//            text1.setVisibility(View.GONE);
//        }else if (biaoshi.equals("todealwithfailure")){
//              String P92PayBen= intent.getStringExtra("P92PayBen");
//              text2.setText("错误码:"+P92PayBen);
//        }

        repay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 返回键点击事件
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
//            Intent intent=new Intent(FailurePayActivity.this,OrderStyleActivity.class);
//            startActivity(intent);
//            finish();
            returnHandle();
            return false;
        }
        return false;
    }

    private Intent intent=null;
    private void returnHandle() {
        if (dataBundle != null) {
            try {
                Class className = null;
                if (dataBundle.containsKey(ScanQrPayActivity.EXTRA_RETURN_CLASS))
                    className = Class.forName(dataBundle.getString(ScanQrPayActivity.EXTRA_RETURN_CLASS));

                if (className != null) {
                    intent = new Intent(FailurePayActivity.this, className);
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
                        intent = new Intent(FailurePayActivity.this, OrderStyleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                intent = new Intent(FailurePayActivity.this, OrderStyleActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            intent = new Intent(FailurePayActivity.this, OrderStyleActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
