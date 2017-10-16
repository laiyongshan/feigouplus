package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/7 0007.
 * 加密工具类
 */

public class EncryptUtil {


    public static HashMap parms;
    //加密网络请求参数
    public static HashMap getParams(HashMap pamsMap){

        if (pamsMap==null)
            pamsMap=new HashMap();

        parms=new HashMap<>();
        parms.clear();
        Iterator iter = pamsMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            parms.put(key,val);
        }

        parms.put("timestamp",System.currentTimeMillis()/1000+"");
        parms.put("device_type","Android");
        parms.put("app_type", AppContext.getContext().getString(R.string.apptype));//车违通

//        Log.d("TAGG",parms.toString());
        String sign= ParamSign.getSign(parms);

        parms.put("sign",sign);


        return parms;
//        return ParamSign.getSign(parms);
    }


    public static HashMap encrypt(HashMap map){

//        String data=getParams(map);
//
//        parms=new HashMap<>();
//        parms.clear();
//        parms.put("data",data);
        parms=ParamSign.netWorkEncrypt(getParams(map));

        return parms;

    }

    //网络请求返回的数据解密
    public static String decryptJson(String jsonObject,Context mContext){
        String json="";
        try {
            JSONObject obj=new JSONObject(jsonObject.toString());
            if(obj.has("data")) {
                String data = obj.getString("data");

                byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                json = new String(encrypt, "UTF-8");
                Log.i("TAG", "解密之后的数据：" + json);
                JSONObject object=new JSONObject(json);
                if(object.has("code")) {
                    int code = object.getInt("code");
                    if(code==10001) {
                        UIHelper.showErrTips(code, mContext);
                    }
                }
            }else{
                Toast.makeText(mContext,"请求服务器发生异常，请稍候重试！",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return json;

    }

}
