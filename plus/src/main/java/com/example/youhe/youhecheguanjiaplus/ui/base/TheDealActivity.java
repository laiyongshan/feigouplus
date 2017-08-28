package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Deal;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.utils.ClickUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 交易成功界面
 */
public class TheDealActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView theDealImageView;//显示签名
    private TextView merchant_name_tv,max_flowing_no_tv,merchant_number_tv,card_no_tv,pay_time_tv,pay_money_tv;

    private ImageButton new_pay_back_ib;//返回按钮

    private Deal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_deal);

        deal= (Deal) getIntent().getSerializableExtra("deal");

        if(deal!=null) {
            in(deal);//初始化控件
        }

    }

    private void in(Deal deal) {

        new_pay_back_ib= (ImageButton) findViewById(R.id.new_pay_back_ib);
        new_pay_back_ib.setOnClickListener(this);

        merchant_name_tv= (TextView) findViewById(R.id.merchant_name_tv);//商户名称
        max_flowing_no_tv= (TextView) findViewById(R.id.max_flowing_no_tv);//流水号
        merchant_number_tv= (TextView) findViewById(R.id.merchant_number_tv);//商务编号
        card_no_tv= (TextView) findViewById(R.id.card_no_tv);//卡号
        pay_time_tv= (TextView) findViewById(R.id.pay_time_tv);//支付时间
        pay_money_tv= (TextView) findViewById(R.id.pay_money_tv);//支付金额

        merchant_name_tv.setText(deal.getMerchant_name()+"");
        max_flowing_no_tv.setText(deal.getMax_flowing_no()+"");
        merchant_number_tv.setText(deal.getMerchant_number()+"");
        card_no_tv.setText(deal.getCard_no()+"");
        pay_time_tv.setText(deal.getPay_time()+"");
        pay_money_tv.setText(deal.getPay_money()+"");

        theDealImageView = (ImageView) findViewById(R.id.qq);

        String bas64 = deal.getStrImg();
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(bas64, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray,0, bitmapArray.length);
            if(bitmap!=null) {
                theDealImageView.setImageBitmap(bitmap);//显示签名
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new FirstEvent("ok"));
    }


    @Override
    public void onClick(View v) {
        if (ClickUtils.isFastDoubleClick()){
            return;
        }
        switch (v.getId()){
            case R.id.new_pay_back_ib:
                Intent intent=new Intent(TheDealActivity.this,OrderStyleActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


    /**
     * 返回键点击事件
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            Intent intent=new Intent(TheDealActivity.this,OrderStyleActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 通知刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }
}
