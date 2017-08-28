package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.utils.AnimationEffects;
import com.lkl.cloudpos.aidl.iccard.AidlICCard;
import com.lkl.cloudpos.aidl.magcard.AidlMagCard;
import com.lkl.cloudpos.aidl.rfcard.AidlRFCard;
import com.smartpost.postregistered.dao.AMagneticStripeCardOperation;
import com.smartpost.postregistered.dao.CardWay;
import com.smartpost.postregistered.dao.NonContactICCardOperation;
import com.smartpost.postregistered.dao.TImeTheCountdown;
import com.smartpost.postregistered.postergistered.PostRegisteredUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 操作方式
 */
public class SmarPOStActivity extends AppCompatActivity{
    private ImageView twoWayCreditCard,img_chaka3,img_shuaka2,img_shuak;
    private RelativeLayout sha,sha2,sha3;
    private PostRegisteredUtils postRegisteredUtils;//卡操作服务绑定工具
    private AidlMagCard magCardDev;//磁条卡操作对象
    private AidlICCard iccard = null;//接触式IC卡
    private AidlRFCard rfcard = null;//非接触IC卡
    private AMagneticStripeCardOperation aMagneticStripeCardOperation;//磁条卡操作逻辑类
    private NonContactICCardOperation nonContactICCardOperation;//非接触IC卡逻辑类
    private CardWay cardWay;//插卡方式逻辑类
    private UIDialog uiDialog;
    private TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smar_post);
        in();
    }

    private void in() {

        uiDialog = new UIDialog(this,"正在准备中...");
        uiDialog.show();
        EventBus.getDefault().register(this);
        sha = (RelativeLayout) findViewById(R.id.sha);//刷卡
        sha2 = (RelativeLayout) findViewById(R.id.sha2);//滴卡
        sha3 = (RelativeLayout) findViewById(R.id.sha3);//插卡

        time = (TextView) findViewById(R.id.text_shijian);//超时
        TImeTheCountdown tImeTheCountdown = new TImeTheCountdown(time);//开启动到时

        twoWayCreditCard = (ImageView) findViewById(R.id.img_shuaka);//刷卡上
        img_shuak = (ImageView) findViewById(R.id.img_shuak);//刷卡下

        img_chaka3 = (ImageView) findViewById(R.id.img_chaka3);//滴卡
        img_shuaka2 = (ImageView) findViewById(R.id.img_shuaka2);//插卡

        way();//操作动画

    }

    /**
     * 操作动画
     */
    private TextView text;
    private String way;
    public void way(){
        TextView zonjinew = (TextView) findViewById(R.id.zonjinew);//总服务费
        TextView shoukuanfang = (TextView) findViewById(R.id.shoukuanfang);//总金额
        text = (TextView) findViewById(R.id.text_shuaka);
        Intent intent = getIntent();
        way = intent.getStringExtra("way");//刷卡方式
        String totalPrice  =intent.getStringExtra("totalPrice");//总价格
        String zonfu = intent.getStringExtra("zonfu");//总服务费
        text.setText(intent.getStringExtra("fangshi"));//提示
        zonjinew.setText(zonfu+"￥");
        shoukuanfang.setText(totalPrice+"￥");
        postRegisteredUtils = new PostRegisteredUtils(this,way);//选择注册对象
        if (way.equals("acreditcard")){//双向刷卡方式
            sha.setVisibility(View.VISIBLE);
            sha2.setVisibility(View.GONE);
            sha3.setVisibility(View.GONE);
            AnimationEffects.twoWayCreditCard(twoWayCreditCard);//向上刷卡动画

            handler.sendEmptyMessageDelayed(1,4200);

        }else if(way.equals("insertacard")){//插卡方式
            sha2.setVisibility(View.VISIBLE);
            sha3.setVisibility(View.GONE);
            sha.setVisibility(View.GONE);
            text.setText(intent.getStringExtra("fangshi"));//提示
            AnimationEffects.cardWay(img_shuaka2);//插卡刷卡动画

        }else if (way.equals("dropofcard")){//滴卡方式
            sha3.setVisibility(View.VISIBLE);
            sha.setVisibility(View.GONE);
            sha2.setVisibility(View.GONE);
            img_shuak.setVisibility(View.GONE);
            text.setText(intent.getStringExtra("fangshi"));//提示
            AnimationEffects.DropsOfCard(img_chaka3);//滴卡刷卡动画
        }

    }

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    twoWayCreditCard.setVisibility(View.GONE);
                    img_shuak.setVisibility(View.VISIBLE);
                    AnimationEffects.twoWayXiaCreditCard(img_shuak);//向下
                    sendEmptyMessageDelayed(2,4200);
                    break;
                case 2:
                    img_shuak.setVisibility(View.GONE);
                    twoWayCreditCard.setVisibility(View.VISIBLE);
                    AnimationEffects.twoWayCreditCard(twoWayCreditCard);//向上刷卡动画
                    sendEmptyMessageDelayed(1,4200);
                    break;
            }
            super.handleMessage(msg);

        }
    };

    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        postRegisteredUtils.resume();//磁条卡绑定服务

    }

    @Override
    protected void onPause() {
        postRegisteredUtils.pause();//解绑服务
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (way.equals("acreditcard")){
            aMagneticStripeCardOperation.stopSearch();//磁条卡取消
        }else if(way.equals("dropofcard")){
            nonContactICCardOperation.shutSDdown();//关闭非接触操作
        }else if(way.equals("insertacard")){//关闭插卡方式操作
            cardWay.guanbi();
        }
        super.onDestroy();


    }

    /**
     *
     * 通知服务绑定成功
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        if (event!=null){
            if(event.getMsg().equals("TheConnectionIsSuccessful")){//通知磁条卡绑定成功

                magCardDev = postRegisteredUtils.getAidlMagCardObject();//得到AidlMagCard对象;

                aMagneticStripeCardOperation = new AMagneticStripeCardOperation(magCardDev,this);//刷卡操作逻辑类
                aMagneticStripeCardOperation.operation();//磁条卡操作
                text.setVisibility(View.VISIBLE);
                uiDialog.hide();

            }else if(event.getMsg().equals("contact")){//通知接触式IC条卡绑定成功
                iccard = postRegisteredUtils.getAidlICCardObject();//得到触式IC卡对象;
                cardWay = new CardWay(iccard,this);//插卡逻辑
                cardWay.opens();
                text.setVisibility(View.VISIBLE);
                uiDialog.hide();


            }else if(event.getMsg().equals("noncontact")){//通知非接触式IC条卡绑定成功
                rfcard = postRegisteredUtils.getAidlRFCard();//得到非接触式IC卡对象
                nonContactICCardOperation = new NonContactICCardOperation(rfcard,this);//非接触IC卡逻辑类
                nonContactICCardOperation.openNoContacOperation();
                text.setVisibility(View.VISIBLE);
                uiDialog.hide();
            }
        }
    }

}
