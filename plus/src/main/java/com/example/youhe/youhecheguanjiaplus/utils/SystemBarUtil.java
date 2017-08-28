package com.example.youhe.youhecheguanjiaplus.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;

import com.example.youhe.youhecheguanjiaplus.manager.SystemBarTintManager;


/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class SystemBarUtil {
    private static SystemBarTintManager mTintManager;

    public  static void useSystemBarTint(Activity activity){
        mTintManager = new SystemBarTintManager(activity);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setTintColor(Color.parseColor("#00a679"));
    }

    @TargetApi(19)
    public static void setTranslucentStatus(boolean on,Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
