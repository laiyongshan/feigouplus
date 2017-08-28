package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class TimeButtoDao {
    private TimeButton timeButton;
    private Activity activity;
    private ClearEditText editText;
    private VolleyUtil volleyUtil;


    public TimeButtoDao(TimeButton timeButton, Activity activity, ClearEditText editText, VolleyUtil volleyUtil) {
        this.activity = activity;
        this.timeButton = timeButton;
        this.editText = editText;
        this.volleyUtil = volleyUtil;
    }



    private HashMap phonePams;
//    //获取验证码参数
//    private String getPams(String phoneNumber){
//
//
//
//        phonePams.put("timestamp",System.currentTimeMillis()/1000+"");
//        String sign= ParamSign.getSign(phonePams);
//        phonePams.put("sign",sign);
//
//        return ParamSign.getSign(phonePams);
//    }



    /**
     * 获取手机验证码
     */
    public void phonePams() {
        String phoneNumber = editText.getText().toString();

        phonePams = new HashMap<>();
        phonePams.put("mobile", phoneNumber);

        if (phoneNumber.equals("")) {
            ToastUtil.getLongToastByString(activity, "手机号码不能为空");
        } else {
            Log.i("WU", phoneNumber);
            volleyUtil.StringRequestPostVolley(URLs.MOBILE_PHONE_ON_THE, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    Log.i("WU", "timeButton=====>" + jsonObject.toString());
//                    json(jsonObject.toString());
                    try {
                        JSONObject obj = new JSONObject( EncryptUtil.decryptJson(jsonObject.toString(),activity));
                        String status = obj.getString("status");
                        if(status.equals("ok")){
                            ToastUtil.getShortToastByString(activity,"请等候验证码,60秒内到达");
                        }
                        Misidentification.misidentification1(activity,status,obj);//判断错误信息

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
                }
            });
        }


    }


    /**
     * 获取手机验证码
     */
    public void loginPhonePams() {
        String phoneNumber = editText.getText().toString();

        phonePams = new HashMap<>();
        phonePams.put("mobile", phoneNumber);

        if (phoneNumber.equals("")) {
            ToastUtil.getLongToastByString(activity, "手机号码不能为空");
        } else {
            Log.i("WU", phoneNumber);
            volleyUtil.StringRequestPostVolley(URLs.PLUS_LOGIN_VERIFYMESSAGE, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    Log.i("WU", "timeButton=====>" + jsonObject.toString());
//                    json(jsonObject.toString());
                    try {
                        JSONObject obj = new JSONObject( EncryptUtil.decryptJson(jsonObject.toString(),activity));
                        String status = obj.getString("status");
                        if(status.equals("ok")){
                            ToastUtil.getShortToastByString(activity,"请等候验证码,60秒内到达");
                        }
                        Misidentification.misidentification1(activity,status,obj);//判断错误信息

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
                }
            });
        }


    }

}
