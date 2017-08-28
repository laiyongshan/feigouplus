package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtils;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.linkpay.loansdk.loan.Loan;
import com.linkpay.loansdk.loan.LoanOpenListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.functions.Consumer;


/**
 * Created by Administrator on 2017/7/18.
 */

public class LoanActivity extends Activity {

    private int loanType;

    private String phoneNum="";
    private String userId="";
    private String access_key_id;
    private String access_key_secret;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loanType=getIntent().getIntExtra("loanType",0);

        //请联系SDK提供者获取access_key_id与access_key_secret
        access_key_id = "mGeXndNnRZs0z4Lp";
        access_key_secret = "kTB3xGOIDdoL98wqYWggmCwO7vBIUu9A";

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            Toast.makeText(LoanActivity.this, "拒绝权限可能会影响使用哦", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        getInitInfo();

    }

    /**
     * 初始化sdk
     *
     * @param phoneNum
     * @param userId
     */
    private void initLoan(String phoneNum, String userId) {

        UIHelper.showPd(this);

        if (access_key_secret == null || access_key_id == null) {
            Toast.makeText(getApplicationContext(), "请联系SDK提供者获取access_key_id与access_key_secret", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        boolean init = Loan.newInstance(getApplicationContext())
                .setLog(true) //开启日志打印 可选配置
                .setHeaderBg(Color.rgb(0,166,121)) //设置头部背景色 可选配置
                .init(access_key_id, userId, phoneNum, getSign(userId, phoneNum));

        if (init) {
            loanOpen(loanType);
        } else {
            Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
        }
    }


    private void loanOpen(int loanType){
        if(loanType==1){//口袋借呗
            Loan.newInstance(getApplicationContext()).openLoan(new LoanOpenListener() {
                @Override
                public void onOpenListener(int i, String s) {
                    switch (i) {
                        case 1:
                            Log.i("Demo", "成功！");
                            break;

                        case 2:
                            Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Log.i("Demo", s);
                            break;
                    }

                    LoanActivity.this.finish();
                }
            });

        }else if(loanType==2) {//急用贷
            Loan.newInstance(getApplicationContext()).openJYD(new LoanOpenListener() {
                @Override
                public void onOpenListener(int i, String s) {
                    switch (i) {
                        case 1:
                            Log.i("Demo", "成功！");
                            break;

                        case 2:
                            Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Log.i("Demo", s);
                            break;
                    }

                    LoanActivity.this.finish();
                    UIHelper.dismissPd();
                }
            });
        }

    }


    /*
    * 获取贷款初始化信息
    * */
    private void getInitInfo(){

        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(LoanActivity.this).StringRequestPostVolley(URLs.GET_DAIKUANG_INFO, EncryptUtil.encrypt(map), new VolleyInterface(){
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","获取用户初始化信息："+ EncryptUtil.decryptJson(jsonObject.toString(),LoanActivity.this));
                try {
                    JSONObject object=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),LoanActivity.this));
                    JSONObject dataObj=object.optJSONObject("data");
                    phoneNum=dataObj.optString("mobile");
                    userId=dataObj.optString("registerNo");
//                    userId="123";
                    int isFirst=dataObj.optInt("isFirst");

                    if(isFirst==1){//首次注册
                        duaikuanRegister(phoneNum,userId);
                    }else{
                        initLoan(phoneNum,userId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * 保存贷款注册信息
    * */
    private void duaikuanRegister(final String phoneNum, final String userId){
        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(LoanActivity.this).StringRequestPostVolley(URLs.REGISTER_DAIKUAN, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","保存贷款注册信息："+ EncryptUtil.decryptJson(jsonObject.toString(),LoanActivity.this));
                JSONObject object= null;
                try {
                    object = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),LoanActivity.this));
                    JSONObject dataObj=object.optJSONObject("data");
                    String status=object.optString("status");
                    if(status.equals("ok")){
                        initLoan(phoneNum,userId);
                    }else{
                        LoanActivity.this.finish();
                        Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(LoanActivity.this, "初始化失败 ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取加密sign
     *
     * @return
     */
    private String getSign(String userId, String phoneNum) {
        return EncryptUtils.md5(access_key_id + access_key_secret + userId + phoneNum).toLowerCase();
    }

}
