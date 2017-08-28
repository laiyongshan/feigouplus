package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author：Administrator on 2016/1/23 0023 10:10
 */
public class NetUtils {
    //判断网络连接状态
    public static boolean isNetworkConnected(Context context){
        if(context!=null){
            ConnectivityManager mConnectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo=mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo!=null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断WiFi的连接状态
    public static boolean iswificonnected(Context context){
        if(context!=null){
            ConnectivityManager mConnectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifiNetworkInfo=mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(mWifiNetworkInfo!=null){
                return mWifiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断移动网络是否连接
    public static boolean isMobileConnected(Context context){
        if(context!=null){
            ConnectivityManager mConnectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileInfo=mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(mMobileInfo!=null){
                return mMobileInfo.isAvailable();
            }
        }
        return false;
    }

    // 获取连接类型
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}
