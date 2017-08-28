package com.example.youhe.youhecheguanjiaplus.utils;

/**
 * Created by Administrator on 2016/11/2 0002.
 * 防止多点击
 */

public class ClickUtils {

    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 3000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
