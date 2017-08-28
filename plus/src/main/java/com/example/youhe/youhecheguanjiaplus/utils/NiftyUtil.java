package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.example.youhe.youhecheguanjiaplus.R;
import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class NiftyUtil {

    public static Effects effect=Effects.slideOnTop;
    public static NiftyNotificationView niftyNotificationView;
    public static void showNotify(Activity activity, String msg){

        Log.i("TAG","显示通知信息栏");

//        niftyNotificationView .setIcon(R.drawable.lion)         //You must call this method if you use ThumbSlider effect
//                .showSticky();
//        You can configure like this
//        The default

        Configuration cfg=new Configuration.Builder()
                .setAnimDuration(1700)
                .setDispalyDuration(3500)
                .setBackgroundColor("#33BDC3C7")
                .setTextColor("#FF444444")
                .setIconBackgroundColor("#FFFFFFFF")
                .setTextPadding(5)                      //dp
                .setViewHeight(48)                      //dp
                .setTextLines(3)                        //You had better use setViewHeight and setTextLines together
                .setTextGravity(Gravity.LEFT)         //only text def  Gravity.CENTER,contain icon Gravity.CENTER_VERTICAL
                .build();

//        NiftyNotificationView.build(activity,msg, effect,R.id.mLyout,cfg)
        niftyNotificationView=NiftyNotificationView.build(activity,msg, effect, R.id.mLyout,cfg);
        niftyNotificationView.setIcon(R.drawable.lion)               //remove this line ,only text
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //add your code
                    }
                }).showSticky();                              //  show(boolean) allow duplicates   or showSticky() sticky notification,you can call removeSticky() method close it
    }

    public static void dismissNotify(){
        if(niftyNotificationView!=null){
            niftyNotificationView.removeSticky();
        }
    }

}
