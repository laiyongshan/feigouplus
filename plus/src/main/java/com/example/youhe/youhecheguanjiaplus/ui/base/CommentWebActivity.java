package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.utils.JsInterface;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class CommentWebActivity extends Activity implements View.OnClickListener{
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private WebView mWebView;
    private String url="";
    private String title="";
    private int type;
    private TextView webview_topbar_tv,webview_back_img;
    private RelativeLayout topBar;

    private LinearLayout load_err_ll;

    ProgressBar pb;

    NetReceiver mNetReceiver;
    IntentFilter mNetFilter;

    AppContext appContext;

    private String[] cookieArray;
    private String host;

    private final int AUTH_FAIL_RESULTCODE=122201;
    private final int AUTH_SUCCESS_RESULTCODE=122202;
    public static String EXTRA_URL="url";
    public static String EXTRA_TITLE="title";
    public static String EXTRA_TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url=getIntent().getStringExtra(EXTRA_URL)+"";
        title=getIntent().getStringExtra(EXTRA_TITLE)+"";
        type=getIntent().getIntExtra(EXTRA_TYPE,0);

        if(type==1) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        }else if(type==2){

            cookieArray=getIntent().getStringArrayExtra("cookieArray");
            String[] cookieKeyArray={"tmri_csfr_token","JSESSIONID-L","userpub","insert_cookie"};

            host=getIntent().getStringExtra("host");

            CookieSyncManager.createInstance(this);
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            for(int i=0;i<cookieArray.length;i++){
                cookieManager.setCookie(host,cookieKeyArray[i]+"="+cookieArray[i]);
            }

            CookieSyncManager.getInstance().sync();
        }

        setContentView(R.layout.activity_webview);
        appContext=(AppContext)getApplicationContext();

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CommentWebActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CommentWebActivity.this);

        mNetReceiver= new NetReceiver();//网络连接情况广播接收者
        mNetFilter= new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);

        pb = (ProgressBar) findViewById(R.id.pb_progress);
        pb.setMax(100);
        findView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetReceiver);
    }

    private void findView() {

        topBar= (RelativeLayout) findViewById(R.id.topBar);

        webview_topbar_tv= (TextView) findViewById(R.id.webview_topbar_tv);
        if(title!=null&&(!title.trim().equals("null"))&&(!title.equals(""))) {
            webview_topbar_tv.setText(title);
            topBar.setVisibility(View.VISIBLE);
        }else{
            topBar.setVisibility(View.GONE);
        }
        webview_back_img= (TextView) findViewById(R.id.webview_back_img);
        webview_back_img.setOnClickListener(this);

//        mWebView=(WebView)findViewById(R.id.webView);
//        //设置视图客户端
//        mWebView.setWebViewClient(new MyWebViewClient());

        initWebView();

        load_err_ll=(LinearLayout)findViewById(R.id.load_err_ll);
        load_err_ll.setOnClickListener(this);
        if(url!=null&&(!url.equals(""))) {
            mWebView.loadUrl(url);
        }else{
            finish();
//            Toast.makeText(CommentWebActivity.this,"网络地址为空",Toast.LENGTH_LONG).show();
        }
    }

    private void initWebView() {
        mWebView = (WebView)findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        String cacheDirPath = CommentWebActivity.this.getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        mWebView.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String ua=mWebView.getSettings().getUserAgentString();
        // 打印结果
        Log.i("TAG", "User Agent:" + ua);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36");
//        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        //设置视图客户端
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(newProgress);
                if(newProgress==100){
                    pb.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

        });

        if (Build.VERSION.SDK_INT>17){
            mWebView.addJavascriptInterface(new JsInterface(this),"Native");
        }else {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        }

    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.i("TAG" ,"跳转的页面："+url);
            super.onLoadResource(view, url);
            if(url.contains("122.gov.cn")) {
                if (url.contains("m/tips/error")) {//支付宝认证失败
                    Toast.makeText(CommentWebActivity.this,"用户在第三方实名认证结果不符合办理该业务的条件",Toast.LENGTH_LONG).show();
                    Intent resultIntent1=new Intent("android.net.conn.ALIPAY_AUTHENT");
                    resultIntent1.putExtra("resultCode",AUTH_FAIL_RESULTCODE);
//                    setResult(AUTH_FAIL_RESULTCODE);
                    sendBroadcast(resultIntent1);
                    finish();
                } else if(url.contains("identitysmscode.html")){//支付宝验证成功
//                  setResult(AUTH_SUCCESS_RESULTCODE);
                    Intent resultIntent2=new Intent("android.net.conn.ALIPAY_AUTHENT");
                    resultIntent2.putExtra("resultCode",AUTH_SUCCESS_RESULTCODE);
                    sendBroadcast(resultIntent2);
                  finish();
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
//            if(progressBar.isShowing()){
//                progressBar.dismiss();
                mWebView.setVisibility(View.VISIBLE);
                load_err_ll.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Toast.makeText(CommentWebActivity.this, "请检查网络连接设置！", Toast.LENGTH_LONG).show();
            Message msg=handler.obtainMessage();//发送通知，加入线程
            msg.what=1;//通知加载自定义404页面
            handler.sendMessage(msg);//通知发送！
        }
    }



    protected Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what) {
                case 1:
                    mWebView.setVisibility(View.GONE);
                    load_err_ll.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    break;
            }
        }
    };


    /**
     * 重新加载
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.load_err_ll:
//                progressBar.show();
                if(!appContext.isNetworkConnected()){
					mWebView.setVisibility(View.GONE);
	        		load_err_ll.setVisibility(View.VISIBLE);
                }
                mWebView.loadUrl(url);
                break;
            case R.id.webview_back_img:
                if(mWebView.canGoBack()) {
                    mWebView.goBack();
                }else{
                    finish();
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                if (isConnected) {
                    if (url != null && !url.equals(""))
                        mWebView.loadUrl(url);
                }
            } else {
                    Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_LONG).show();
                }
            }
        }
}
