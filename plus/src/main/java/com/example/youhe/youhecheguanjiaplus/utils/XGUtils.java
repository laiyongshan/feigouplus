package com.example.youhe.youhecheguanjiaplus.utils;

import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import java.util.HashMap;

/**
 * 信鸽推送工具
 * Created by Administrator on 2017/5/2 0002.
 */

public class XGUtils {


    public static void registered(String mobile) {
        registered(mobile,true);
    }
    public static void registered(final String mobile, final boolean isUpload) {
        // 1.获取设备Token
        // 注册信鸽推送接口
        if (!StringUtils.isEmpty(mobile))
            XGPushManager.registerPush(AppContext.getContext(), mobile, new XGIOperateCallback() {
                @Override
                public void onSuccess(Object data, int flag) {
                    Log.i("xGNotifaction", "注册成功，设备token为：" + data+",mobile:"+mobile);
                    if (isUpload) {
                        HashMap xingeParams = new HashMap();
                        String token = TokenSQLUtils.check();
                        xingeParams.put("token", token);
                        xingeParams.put("xingetoken", data.toString());
                        xingeParams.put("device_type", "android");
                        Task ts3 = new Task(TaskType.TS_TO_XINGE_TOKEN, EncryptUtil.encrypt(xingeParams));//提交信鸽Token
                        MainService.newTask(ts3);
                    }
                }

                @Override
                public void onFail(Object data, int errCode, String msg) {
                    Log.i("xGNotifaction", "注册失败，错误码：" +",mobile:"+mobile+ errCode + ",错误信息：" + msg);
                }
            });
//        else
//            XGPushManager.registerPush(AppContext.getContext(), new XGIOperateCallback() {
//                @Override
//                public void onSuccess(Object o, int i) {
//                    Log.i("xGNotifaction", "注册成功，设备token为：" + o);
//                }
//
//                @Override
//                public void onFail(Object o, int i, String s) {
//                    Log.i("xGNotifaction", "注册失败，错误码：" + i + ",错误信息：" + s);
//                }
//            });
        XGPushManager.setNotifactionCallback(new XGPushNotifactionCallback() {
            @Override
            public void handleNotify(XGNotifaction xGNotifaction) {
                Log.i("xGNotifaction", "处理信鸽通知：" + xGNotifaction.toString());
                // 获取标签、内容、自定义内容
                String title = xGNotifaction.getTitle();
                String content = xGNotifaction.getContent();
                String customContent = xGNotifaction.getCustomContent();


                // 其它的处理
                // 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
                xGNotifaction.doNotify();
                Log.i("xGNotifaction", title + content + customContent);
            }
        });
    }

}
