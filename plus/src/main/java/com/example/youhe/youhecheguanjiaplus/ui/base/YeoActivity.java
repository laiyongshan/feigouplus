package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;


public class YeoActivity extends AppCompatActivity {
    private TextView textView1,textView2,textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeo_he);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,YeoActivity.this);
        }
        SystemBarUtil.useSystemBarTint(YeoActivity.this);

        textView1 = (TextView) findViewById(R.id.tv_extnumble);
//        textView2 = (TextView) findViewById(R.id.tv);
        textView3 = (TextView) findViewById(R.id.tv5);
    }

    /**
     * 返回键点击事件
     *
     */
    public void fanhui(View view){

        finish();

    }

    /**
     * 拨打客服热线
     * @param view
     */
    public void boda(View view){
         String text = textView1.getText().toString().trim();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+text));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
//    /**
//     * 拨打招商热线
//     * @param view
//     */
//    public void zhao(View view){
//        String text = textView2.getText().toString().trim();
//        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+text));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//
//    }

    /**
     * 加入我们
     * @param view
     */
    public void jiaru(View view){

        Intent  intent = new Intent(this,JoinUsActivity.class);
        startActivity(intent);
    }

}
