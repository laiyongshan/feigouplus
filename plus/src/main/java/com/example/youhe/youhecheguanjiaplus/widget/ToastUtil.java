package com.example.youhe.youhecheguanjiaplus.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/7 0007.
 *
 */
public class ToastUtil {

    private static Context context = null;
    private static Toast toast = null;


    public static void getShortToast(Context context, int retId){
        toast = Toast.makeText(context, retId, Toast.LENGTH_SHORT);
        toast.setText(retId);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void getShortToastByString(Context context,String hint){
        toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
        toast.setText(hint);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void getLongToast(Context context,int retId){
        toast = Toast.makeText(context, retId, Toast.LENGTH_LONG);
        toast.setText(retId);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }


    public static void getLongToastByString(Context context,String hint){
        toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
        toast.setText(hint);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
