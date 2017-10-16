package com.example.youhe.youhecheguanjiaplus.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.ui.base.AliPayDealActivity;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2017/7/10 0010.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI iwxapi;
    private TextView tv_description;
    private ImageView iv_icon;
    public static Bundle customerBundle = null;
    public static String orderCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

//        tv_description = (TextView) findViewById(R.id.id_tv_rs_description);
//        iv_icon = (ImageView) findViewById(R.id.id_iv_rs_icon);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, WXPayEntryActivity.this);
        }
        SystemBarUtil.useSystemBarTint(WXPayEntryActivity.this);
        iwxapi = WXAPIFactory.createWXAPI(this, "wx5b85cea18c42b03e");
        iwxapi.handleIntent(getIntent(), this);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            Log.d("TAG", "base" + baseResp.errCode + "," + baseResp.errStr);
            finishTimer.cancel();
            finishTimer.start();
//            Log.d("TAG", customerBundle.toString() + "," + orderCode);
            if (baseResp.errCode == 0) {//成功
                if (!StringUtils.isEmpty(orderCode)) {
                    Intent intent = new Intent(WXPayEntryActivity.this, AliPayDealActivity.class);
                    intent.putExtra("ordercode", orderCode);
                    if (customerBundle != null)
                        intent.putExtra(AliPayDealActivity.EXTRA_CUSTOMER_BUNDLE, customerBundle);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.getShortToastByString(this, "支付成功");
                }
            } else if (baseResp.errCode == -2) {//用户取消
                ToastUtil.getShortToastByString(this, "用户取消");
            } else {//-1错误
                ToastUtil.getShortToastByString(this, "支付失败");
            }
        }


    }

    private CountDownTimer finishTimer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            finish();
        }
    };


}
