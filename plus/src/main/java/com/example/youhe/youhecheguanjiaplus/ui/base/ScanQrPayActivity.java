package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.biz.ThePosPay;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.PayUtil;
import com.example.youhe.youhecheguanjiaplus.utils.QrCodeUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class ScanQrPayActivity extends YeoheActivity implements View.OnClickListener {

    private int width, height;
    private ImageView pay_qr_code_img;
    private TextView pay_money_tv;
    private TextView type_tips_tv;
    private TextView expired_time_tv;

    private ImageView weixin_pay_img, ali_pay_img;

    private String ordercode;
    private String paymoney;
    private int mjOpenType;
    private int orderstyle;//订单类型

    private VolleyUtil volleyUtil;

    public static Timer timer;
    private TimerTask task;

    private int payInt = 1;

    private String token = TokenSQLUtils.check();//token值
    private boolean isReturnResult = false;
    public static final String EXTRA_IS_RETURN_RESULT = "isReturnResult";//支付完  setResult_ok   权限系数0   暂时不用
    public static final String EXTRA_RETURN_CLASS = "";//支付完之后返回的页面   权限系数10
    private String return_class = "";

    public static final String EXTRA_STRING_ORDER_CODE = "ordercode";//订单号
    public static final String EXTRA_STRING_PAY_MONEY = "paymoney";//支付金额
    public static final String EXTRA_INT_MJ_OPEN_TYPE = "mjOpenType";//
    public static final String EXTRA_INT_ORDER_STYLE = "orderstyle";//
    public static final String EXTRA_INT_ORDER_TYPE = "order_type";//订单类型   1违章2年检3车主卡
    public static final String EXTRA_CUSTOMER_BUNDLE = "customer_bundle";

    public static final int ORDER_TYPE_ILLEGAL = 1;//违章
    public static final int ORDER_TYPE_INSPECTION = 2;//年检
    public static final int ORDER_TYPE_PLUS = 3;//plus
    private int order_type = ORDER_TYPE_ILLEGAL;//订单类型


    private Bundle customerBundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_pay);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        ordercode = getIntent().getStringExtra(EXTRA_STRING_ORDER_CODE);
        paymoney = getIntent().getStringExtra(EXTRA_STRING_PAY_MONEY);
        mjOpenType = getIntent().getIntExtra(EXTRA_INT_MJ_OPEN_TYPE, 0);
        orderstyle = getIntent().getIntExtra(EXTRA_INT_ORDER_STYLE, 0);
        order_type = getIntent().getIntExtra(EXTRA_INT_ORDER_TYPE, 1);

        if (getIntent().hasExtra(EXTRA_CUSTOMER_BUNDLE))
            customerBundle = getIntent().getBundleExtra(EXTRA_CUSTOMER_BUNDLE);

        isReturnResult = getIntent().getBooleanExtra(EXTRA_IS_RETURN_RESULT, false);
        if (getIntent().hasExtra(EXTRA_RETURN_CLASS))
            return_class = getIntent().getStringExtra(EXTRA_RETURN_CLASS);

        volleyUtil = VolleyUtil.getVolleyUtil(this);

        initViews();
//        payType(1);

        task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(task, 3000, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        UIHelper.dismissPd();
    }

    /*
        * 初始化控件
        * */
    private void initViews() {

        weixin_pay_img = (ImageView) findViewById(R.id.weixin_pay_img);
        weixin_pay_img.setOnClickListener(this);
        ali_pay_img = (ImageView) findViewById(R.id.ali_pay_img);
        ali_pay_img.setOnClickListener(this);

        type_tips_tv = (TextView) findViewById(R.id.type_tips_tv);

        expired_time_tv = (TextView) findViewById(R.id.expired_time_tv);

        pay_qr_code_img = (ImageView) findViewById(R.id.pay_qr_code_img);
        ViewGroup.LayoutParams lp;
        lp = pay_qr_code_img.getLayoutParams();
        lp.width = width * 4 / 7;
        lp.height = width * 4 / 7;
        pay_qr_code_img.setLayoutParams(lp);

        pay_money_tv = (TextView) findViewById(R.id.pay_money_tv);
        pay_money_tv.setText(paymoney + "");

        if (mjOpenType == 1) {
            weixin_pay_img.setClickable(false);
            ali_pay_img.setClickable(false);
            payType(1);

        } else if (mjOpenType == 2) {
            weixin_pay_img.setClickable(false);
            ali_pay_img.setClickable(true);
            payType(2);
        } else if (mjOpenType == 3) {
            weixin_pay_img.setClickable(true);
            ali_pay_img.setClickable(true);
            payType(1);

        } else if (mjOpenType == 0) {
            weixin_pay_img.setClickable(false);
            ali_pay_img.setClickable(false);
            expired_time_tv.setText("获取支付二维码有误，请退出重试");
        } else {
            weixin_pay_img.setClickable(false);
            ali_pay_img.setClickable(false);
            expired_time_tv.setText("获取支付二维码有误，请退出重试");
        }

    }


    private void payType(int type) {
        if (type == 1) {
            weixin_pay_img.setImageResource(R.drawable.weixinzhifu);
            ali_pay_img.setImageResource(R.drawable.zhifubao2);
            type_tips_tv.setText("微信扫一扫，向我付款");

            getQrCodeUrl(getQrcodeUrlParam(1));

            payInt = 1;

        } else if (type == 2) {
            weixin_pay_img.setImageResource(R.drawable.weixinzhifu2);
            ali_pay_img.setImageResource(R.drawable.zhifubao);
            type_tips_tv.setText("支付宝扫一扫，向我付款");

            getQrCodeUrl(getQrcodeUrlParam(2));

            payInt = 2;
        }
    }


    HashMap map = new HashMap();

    private HashMap getQrcodeUrlParam(int paytype) {
        map = new HashMap();
        if (token != null) {
            map.put("token", token);
        } else {
            map.put("token", "");
        }
        map.put("ordercode", ordercode);
        map.put("paytype", paytype);
        map.put("paymoney", paymoney);

        map.put("is_balance_deductible", ThePosPay.is_balance_deductible);
        if (orderstyle == 3) {//年检订单传
            map.put("is_annual_inspection", ORDER_TYPE_INSPECTION);
            order_type = 2;
        }
        map.put(EXTRA_INT_ORDER_TYPE, order_type);
        return map;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weixin_pay_img://微信扫码支付
                payType(1);
                weixin_pay_img.setClickable(false);
                ali_pay_img.setClickable(true);
                break;

            case R.id.ali_pay_img://支付宝扫码支付
                payType(2);
                weixin_pay_img.setClickable(true);
                ali_pay_img.setClickable(false);
                break;
        }
    }

    /**
     * 订单支付(茂捷线上二维码通道)
     */
    int expiredTime;//二维码过期时间
    String qrcode_url;

    public void getQrCodeUrl(HashMap map) {

        UIHelper.showPd(ScanQrPayActivity.this);

        volleyUtil.StringRequestPostVolley(URLs.GET_QR_CODE_URL, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject object = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), ScanQrPayActivity.this));

                    String status = object.optString("status");
                    if (status.equals("ok")) {
                        JSONObject dataObj = object.optJSONObject("data");
                        if (dataObj.has("qrcode_text")) {
                            qrcode_url = dataObj.optString("qrcode_text");//二维码图片URL
                        }
                        if (dataObj.has("expiredTime")) {
                            expiredTime = dataObj.optInt("expiredTime") * 1000;//过期时间 单位：ms
                        }
                        if (qrcode_url != null) {
                            Bitmap qrCodeBitmap = QrCodeUtil.generateBitmap(qrcode_url, width + 10, width + 10);
                            if (qrCodeBitmap != null) {
                                pay_qr_code_img.setImageBitmap(qrCodeBitmap);
                            }
                        }

                        expired_time_tv.setText("二维码有效期为" + expiredTime / 1000 / 60 + "分钟，过期后请刷新重试");
                    } else {
                        if (object.has("show_msg")) {
                            Toast.makeText(ScanQrPayActivity.this, "" + object.optString("show_msg"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ScanQrPayActivity.this, "获取支付二维码异常，请稍候重试", Toast.LENGTH_LONG).show();
                        }
                        ScanQrPayActivity.this.finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                UIHelper.dismissPd();
                ScanQrPayActivity.this.finish();
                Toast.makeText(ScanQrPayActivity.this, "获取支付二维码异常，请稍候重试", Toast.LENGTH_LONG).show();
            }
        });
    }


    private HashMap getStatusParams() {
        map = new HashMap();
        if (token != null) {
            map.put("token", token);
        } else {
            map.put("token", "");
        }
        map.put("ordercode", ordercode);
        return map;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            switch (msg.what) {
                case 1:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(EXTRA_IS_RETURN_RESULT, isReturnResult);
                    bundle.putString(EXTRA_RETURN_CLASS, return_class);
                    if (customerBundle != null)
                        bundle.putBundle(EXTRA_CUSTOMER_BUNDLE, customerBundle);
                    PayUtil.getPayStatus(ScanQrPayActivity.this, getStatusParams(), bundle);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {
    }


    /**
     * 第一个参数表示要执行的任务，通常是网络的路径；第二个参数表示进度的刻度，第三个参数表示任务执行的返回结果
     */
    public class GetQRCodeTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 表示任务执行之前的操作
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            UIHelper.showPd(ScanQrPayActivity.this);
        }

        /**
         * 主要是完成耗时的操作
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // 使用网络连接类HttpClient类王城对网络数据的提取

            Bitmap bitmap = HttpUtil.getNetBitmap(arg0[0]);
            return bitmap;
        }

        /**
         * 主要是更新UI的操作
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                pay_qr_code_img.setImageBitmap(result);
            }
            UIHelper.dismissPd();

            MyCount mc = new MyCount(expiredTime, 1000);
            mc.start();
        }
    }


    class MyCount extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            payType(payInt);//二维码过期，自动刷新二维码？
        }
    }


    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }
}
