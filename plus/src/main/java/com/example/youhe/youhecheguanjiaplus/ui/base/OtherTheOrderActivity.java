package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 其他类型点单详情页
 */
public class OtherTheOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webView;
    private ProgressBar bar;
    private OrderInquiry.DataBean bean;
    private Button button;
    private TextView title_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_the_order);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,OtherTheOrderActivity.this);
        }

        SystemBarUtil.useSystemBarTint(OtherTheOrderActivity.this);

        in();
    }

    private void in() {
        Intent intents = getIntent();
        Bundle bundle = intents.getBundleExtra("bundle");
        bean = (OrderInquiry.DataBean) bundle.getSerializable("datalist");
        Log.i("WU","URL================="+bean.getDetailurl());

        title_textView= (TextView) findViewById(R.id.title_textView);
        title_textView.setText("订单详情");

        bar = (ProgressBar)findViewById(R.id.myProgressBar);
        webView = (WebView) findViewById(R.id.webView);

        button = (Button) findViewById(R.id.quzhifu);
        button.setOnClickListener(this);

        if(!bean.getStatus().equals("1")) {
            button.setBackgroundColor(Color.GRAY);
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }

        webView.setWebViewClient(new WebViewClient(){//不用调用第三放浏览
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                bar.setProgress(newProgress);
//                if (newProgress == 100) {
//                    bar.setVisibility(View.GONE);
//                } else {
////                    if (View.INVISIBLE == bar.getVisibility()) {
////                        bar.setVisibility(View.VISIBLE);
////                    }
////                    bar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);
            }

        });
        webView.loadUrl(bean.getDetailurl());
//        jishuan(bean);//计算总价

    }

    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    /**
     * 去支付
     */
    private int zonfuwu = 0;
    private int zenfen= 0;
    private String zo;//格式化总价格

    /**
     * 计算总价
     */
    public void jishuan(OrderInquiry.DataBean beanss){

//        for (int i = 0; i <beanss.getDetails().size() ; i++) {//计算总罚金额和总服务费
//            int zhonfw = Integer.parseInt(beanss.getDetails().get(i).getDegree());//总扣分
//            int fuwufei = Integer.parseInt(beanss.getDetails().get(i).getPrice());//总服务费
//            zenfen +=zhonfw;//总分
//            zonfuwu += fuwufei;//总服务费
//        }
//
    }

    @Override
    public void onClick(View v) {

            NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);//数字金币格式化
            zo = usFormat.format(Integer.parseInt(bean.getTotalprice()));
            Intent intent = new Intent(this, PayActivity.class);
            intent.putExtra("ordernumber", bean.getOrdercode());//发送订单号
            intent.putExtra("integerzonjine", bean.getTotalprice());//发送总String类型价格
            intent.putExtra("zonfakuan", zo);//发送总类型价格
//        intent.putExtra("zonfuwu", zenfen);//总扣分
            intent.putExtra("totalprice", bean.getTotalprice());//服务费
            intent.putExtra("type", "5");
            intent.putExtra("ordertype",1);//违章订单
        intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_ILLEGEL);
            startActivity(intent);
            finish();

    }
}
