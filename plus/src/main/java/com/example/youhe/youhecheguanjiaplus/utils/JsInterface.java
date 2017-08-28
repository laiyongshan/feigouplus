package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.ui.base.DenLuActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * Created by Administrator on 2017/7/13 0013.
 */

public class JsInterface {

    private Context context;

    public JsInterface(Context context) {
        this.context = context;
    }

    /**
     * html 获取原生token值
     * @return
     */
    @JavascriptInterface
    public String getToken(){
        return TokenSQLUtils.check();
    }


    /**
     * html token失效情况下 跳转到登录页面
     */
    @JavascriptInterface
    public void jumpToLogin(){
         SharedPreferences sp= AppContext.getContext().getSharedPreferences("judges", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sp.edit();


        TokenSQLUtils tokenSQLUtils= new TokenSQLUtils(AppContext.getContext());
        tokenSQLUtils.delete();//清除Token值
        StatusSQLUtils  statusSQLUtils = new StatusSQLUtils(AppContext.getContext());
        statusSQLUtils.undateApi("no");//保存退出状态

//        UserManager.setUserType(UserManager.USER_NORMAL);

        EventBus.getDefault().post(new FirstEvent("no"));

        HashMap params1 = new HashMap();
        params1.put("auth",-1);
        Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//退出成功后更新个人中心数据
        MainService.newTask(ts1);

        editor.putBoolean("judge", false);
        editor.commit();

        Intent intent = new Intent(AppContext.getContext(), DenLuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppContext.getContext().startActivity(intent);

    }



    @JavascriptInterface
    public String netWorkEncrypt(String json) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("timestamp", System.currentTimeMillis() / 1000 + "");
            jsonObject.put("devicetype", "web");


            String keySign="";
            HashMap hashMap=new HashMap();
            for (Iterator<String> iterator = jsonObject.keys(); iterator.hasNext();) {
//                keySign+=iterator.next();
                hashMap.put(iterator,iterator.hasNext());
            }

            String sign = ParamSign.getFromJNI(keySign).toUpperCase();

            jsonObject.put("sign",sign);
            String a="rldstwvhral33brqz9ypetwe";

            byte[] des = TripleDES.encrypt(jsonObject.toString().getBytes(),a.getBytes());


            hashMap.put("data",TripleDES.byte2hex(des));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

}
