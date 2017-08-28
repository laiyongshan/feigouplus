package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * WEBView界面
 */
public class DisclaimerActivity extends AppCompatActivity {
    private static TokenSQLUtils tsu;
    private WebView webView;
    private TextView text;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,DisclaimerActivity.this);
        }
        SystemBarUtil.useSystemBarTint(DisclaimerActivity.this);

        in();
    }


    private void in() {
        Intent intents = getIntent();
        bar = (ProgressBar)findViewById(R.id.myProgressBar);
        text = (TextView) findViewById(R.id.textView);
        String dias = intents.getStringExtra("mianzhe");
        String title = intents.getStringExtra("title");
        text.setText(title);//设置标题
        webView = (WebView) findViewById(R.id.webView);

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
        webView.loadUrl(dias);
    }
    public void fanhui(View view){
        finish();
    }
}
