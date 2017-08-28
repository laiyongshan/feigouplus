package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * 检查网络是否断开
 */
public class NetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            boolean isConnected = NetUtils.isNetworkConnected(context);
            if (isConnected) {

            } else {
                Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
