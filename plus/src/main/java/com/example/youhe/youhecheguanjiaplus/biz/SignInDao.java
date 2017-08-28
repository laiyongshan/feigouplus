package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.db.biz.CheckInTimeSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/4 0004.
 * 签到逻辑类
 */

public class SignInDao {
    private Activity mActivity;
    private CheckInTimeSQLUtils checkInTimeSQLUtils;
    private VolleyUtil volleyUtil;//请求网络
    private HashMap<String, String> phonePams;
    private TokenSQLUtils tsl;//拿到已保存的Token值




    public SignInDao(Activity activity){

        this.mActivity = activity;
        phonePams = new HashMap<>();
        volleyUtil = VolleyUtil.getVolleyUtil(activity);//上网请求
        checkInTimeSQLUtils = new CheckInTimeSQLUtils(activity);
        tsl = new TokenSQLUtils(activity);


    }

    /**
     * 请求签到
     */
    private String oldDate;
    public void judgmentSignIn(){
        httpSign();//测试用的签到
        oldDate = checkInTimeSQLUtils.check();//得到之前保存的时间
        Log.i("P92Pay","得到之前保存的时间==="+oldDate);
        if (oldDate.equals("")){//第一次签到
            Log.i("P92Pay","第一次签到"+"==="+oldDate);
            httpSign();

        }else {
            Log.i("P92Pay","开始判断需不需签到，判断是否过期");
            panduan(oldDate);

        }

    }

    /**
     * 判断是否过期
     */
    private String newDate;
    private void panduan(String oldDate){

        SimpleDateFormat   formatter   =   new   SimpleDateFormat    ("yyyy-MM-dd hh:mm:ss");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        newDate    =    formatter.format(curDate);

        try {
            boolean res = jisuan(oldDate,newDate);//判断是否超过24小时
            if (res){
                Log.i("P92Pay","已过期，去签到");
                httpSign();

            }else {
                Log.i("P92Pay","没过期，可以用");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //判断是否超过24小时
     private boolean jisuan(String date1, String date2)  {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
        Date start = null;
        try {
            start = sdf.parse(date1);
            Date end = sdf.parse(date2);
            long cha = end.getTime() - start.getTime();
            long q  = Math.abs(cha);
            if(q > (24* 3600000)){
                return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 上网请求签到
     */
    private void httpSign(){
        String token = tsl.check();
        phonePams.put("token",token);
        Log.i("P92Pay","签到jsonObject=====>"+token);
        volleyUtil.StringRequestPostVolley(URLs.SIGN, phonePams, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("P92Pay","签到jsonObject=====>"+jsonObject.toString());

                jsonAnalysis(jsonObject.toString());//json解析

            }
            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败,无法发送请求");

            }
        });

    }

    /**
     * 解析得到数据
     * @param json
     */
    private void jsonAnalysis(String json) {

        try {
            JSONObject jsonOnject = new JSONObject(json);
            String status = jsonOnject.getString("status");
            if (status.equals("ok")) {

                String data = jsonOnject.getString("data");//密钥

                WORLDKEY = data;
//
                    if (oldDate.equals("")) {//第一次签到
                        Log.i("P92Pay", "第一次签到保存");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        newDate = formatter.format(curDate);
                        checkInTimeSQLUtils.addDate(newDate);
                        Log.i("P92Pay","第一次签到保存的时间=="+newDate);
                    } else {//第二次之后签到

                        checkInTimeSQLUtils.undateApi(newDate);
                        Log.i("P92Pay","第二次之后签到保存的时间=="+newDate);
                    }
            }
            Misidentification.misidentification1(mActivity, status, jsonOnject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到工作密钥
     * @return
     */
    private static String WORLDKEY;
    public static final String  getWorldKey(){


        return WORLDKEY;
    }


}
