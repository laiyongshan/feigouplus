package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.TripleDES;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/7/5.
 */

public class MyAssetsActivity extends Activity implements View.OnClickListener {

    private static final int INTENT_APPLY_CRASH = 0x01;
    private static final int INTENT_APPLY_FOR_CRASH = 0x02;
    private static final int INTENT_REAL_NAME = 0x03;
    private RelativeLayout request_cash_layput, request_record_layout, real_name_layout;
    private ImageView myassets_back_img;
    private TextView trading_records_tv;

    private TextView figure_tv;//余额金额数字

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myassets);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, MyAssetsActivity.this);
        }
        SystemBarUtil.useSystemBarTint(MyAssetsActivity.this);

        context = this;

        initViews();//初始化控件

        if (AppContext.isLogin) {
            getRemainingSum();//余额查询
            checkClientAuth();//实名检验
        }
    }


    /*
    * 初始化控件
    * */
    private void initViews() {
        request_cash_layput = (RelativeLayout) findViewById(R.id.request_cash_layput);
        request_cash_layput.setOnClickListener(this);
        request_record_layout = (RelativeLayout) findViewById(R.id.request_record_layout);
        request_record_layout.setOnClickListener(this);
        real_name_layout = (RelativeLayout) findViewById(R.id.real_name_layout);
        real_name_layout.setOnClickListener(this);

        myassets_back_img = (ImageView) findViewById(R.id.myassets_back_img);
        myassets_back_img.setOnClickListener(this);

        trading_records_tv = (TextView) findViewById(R.id.trading_records_tv);//交易记录
        trading_records_tv.setOnClickListener(this);

        figure_tv = (TextView) findViewById(R.id.figure_tv);
    }


    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_cash_layput://申请提现
                if (status == 1) {
                    intent = new Intent(MyAssetsActivity.this, ApplyCrashActivity.class);
                    startActivityForResult(intent, INTENT_APPLY_CRASH);
                    MyAssetsActivity.this.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_from_left);
                } else {
                    intent = new Intent(MyAssetsActivity.this, RealNameActivity.class);
                    startActivityForResult(intent, INTENT_REAL_NAME);
                    MyAssetsActivity.this.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_from_left);
                }
                break;

            case R.id.request_record_layout://提现记录
                if (status == 1) {
                    intent = new Intent(MyAssetsActivity.this, Apply_for_Crash_Activity.class);
                    startActivityForResult(intent, INTENT_APPLY_FOR_CRASH);
                    MyAssetsActivity.this.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_from_left);
                } else {
                    intent = new Intent(MyAssetsActivity.this, RealNameActivity.class);
                    startActivityForResult(intent, INTENT_REAL_NAME);
                    MyAssetsActivity.this.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_from_left);
                }

                break;

            case R.id.real_name_layout://实名认证

                if (AppContext.isLogin) {
                    intent = new Intent(MyAssetsActivity.this, RealNameActivity.class);
                    startActivityForResult(intent, INTENT_REAL_NAME);
                    MyAssetsActivity.this.overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_from_left);
                } else {
                    UIHelper.showLoginActivity(MyAssetsActivity.this);
                    UIHelper.ToastMessage(MyAssetsActivity.this, "请先登录");
                }

                break;

            case R.id.myassets_back_img://返回按钮
                finish();
                break;

            case R.id.trading_records_tv://交易记录查询
                intent = new Intent(MyAssetsActivity.this, TradingReco2Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == INTENT_REAL_NAME)
                    checkClientAuth();
                else
                    getRemainingSum();
            } else {
                if (requestCode == INTENT_APPLY_FOR_CRASH)
                    getRemainingSum();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap params;

    //余额显示
    private void getRemainingSum() {

        UIHelper.showPd(context);

        params = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if (token != null) {
            params.put("token", token);
        }

        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.GET_CLIENT_REMAINING_SUM, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "获取余额返回的数据：" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(jsonObject.toString());
                    String data = obj.getString("data");
                    byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                    String json = new String(encrypt, "UTF-8");
                    Log.i("TAG", "解密之后的数据：" + json);

                    obj = new JSONObject(json);
                    JSONObject dataObj = obj.getJSONObject("data");
                    float remaining_sum = dataObj.getInt("remaining_sum");
//                    yu_e_tv.setText(remaining_sum+"元");
                    figure_tv.setText(remaining_sum + "");

                } catch (Exception e) {
                    e.printStackTrace();
                    figure_tv.setText("0.00");
                } finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG", "获取余额返回的错误数据：" + volleyError.toString());
                figure_tv.setText("0.00");
            }
        });
    }


    /*
    * 检验是否已经实名验证
    * */
    int status;

    private void checkClientAuth() {
        params = new HashMap();
        String token = TokenSQLUtils.check();
        if (token != null) {
            params.put("token", token);
        }

        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.CHECK_CLIENT_AUTH, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "实名检验返回的数据：" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), context));
                    status = obj.optJSONObject("data").optInt("status");
                    if (status == 1) {//已验证
                        real_name_layout.setVisibility(View.GONE);
                    } else if (status == -1) {//未验证
                        real_name_layout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    real_name_layout.setVisibility(View.GONE);
                } finally {
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                real_name_layout.setVisibility(View.GONE);
            }
        });

    }

}
