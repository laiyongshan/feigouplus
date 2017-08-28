package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/19 0019.
 * 开启APP时请求Token
 */
public class TokenDaoOpinion {

    private Activity mActivity;
    private TokenSQLUtils tsu;
    private VolleyUtil volleyUtil;
    private HashMap<String, String> param;
    private SharedPreferences  sp1;
    private StatusSQLUtils statusSQLUtils;//得到保存状态

    public TokenDaoOpinion(Activity activity) {
        this.mActivity = activity;
        param = new HashMap<>();
        tsu = new TokenSQLUtils(mActivity);
        volleyUtil = VolleyUtil.getVolleyUtil(mActivity);
        sp1 = mActivity.getSharedPreferences("judges", Context.MODE_PRIVATE);
        statusSQLUtils = new StatusSQLUtils(mActivity);
    }

    private String token;

    /**
     * 请求Token
     */
    public void getToken() {
        token = TokenSQLUtils.check();//重数据库中拿到Token值
        if ( StringUtils.isEmpty(token)){
            return;
        }
        param.put("token", token);
//        Log.i("WU",token);
        volleyUtil.StringRequestPostVolley(URLs.TOKENDEMAND, EncryptUtil.encrypt(param), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                getJson(EncryptUtil.decryptJson(jsonObject.toString(),mActivity));
                Log.i("TAG","刷新Token:"+jsonObject.toString());
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败");
            }
        });
    }

    /**
     * 刷新Token
     */
    private void getJson(String json) {

        String statu =  statusSQLUtils.check();
        if (statu.equals("yes")||statu.equals("")){//本来就有登录的就去网络判断Token
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                String status = jsonObject.getString("status");
                Log.i("WU","判断TOKEN");

                if (status.equals("ok")){//本地的Token值与后台的Token值相同时
                    JSONObject dataObject2 = jsonObject.getJSONObject("data");
                    String tokens = dataObject2.getString("token");
                    tsu.addDate(tokens);//保存新的Token值
                    if (statu.equals("")){
                        statusSQLUtils.addDate("yes");
                    }else {
                        statusSQLUtils.undateApi("yes");
                    }

                    Log.i("WU","本地的Token值与后台的Token值相同时");

                    AppContext.isLogin=true;
                } else if (status.equals("fail")){//本地的Token值与后台的Token值不相同时
                    Log.i("WU","本地的Token值与后台的Token值不相同时");
                    AppContext.isLogin=false;
//                    ToastUtil.getShortToastByString(mActivity, "Token校验失败,请等后台处理");
                    if (statu.equals("")){
                        statusSQLUtils.addDate("no");//设置为未登录状态
                    }else {
                        statusSQLUtils.undateApi("no");

                    }
                }
            } catch (Exception e) {
                AppContext.isLogin=false;
                e.printStackTrace();
            }
        } else {//本来没有登录的

            AppContext.isLogin=false;

            if (statu.equals("")){
                statusSQLUtils.addDate("no");//设置为未登录状态
            }else {
                statusSQLUtils.undateApi("no");
            }
            Log.i("WU","本来就没有登录");
        }


    }


}
