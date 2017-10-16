package com.example.youhe.youhecheguanjiaplus.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;

/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class alipayUtils {

    public static void pay(final Activity activity, final Handler handler, final String orderInfo){
        Runnable payRunnable=new Runnable() {
            @Override
            public void run() {
                PayTask alipay=new PayTask(activity);
                String result=alipay.pay(orderInfo,true);

                Message msg=new Message();
                msg.what=1;
                msg.obj=result;
                handler.sendMessage(msg);
            }
        };
    }

}
