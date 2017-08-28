package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.util.Log;

import com.aidl.utils.HexUtils;
import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.lkl.cloudpos.aidl.pinpad.AidlPinpad;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/28 0028.
 * 下载主密钥
 */

public class GetMasterKey {

    private String ATG="P92Pay";
    private Activity mActivity;
    private VolleyUtil volleyUtil;//请求网络
    private HashMap<String, String> phonePams;//网络请求的参数；
    private TokenSQLUtils tsl;//拿到已保存的Token值；
    private AidlPinpad pinpad = null; // 密码键盘接口；
    private String etTheserialNumber;//违章机终端号；

    public GetMasterKey(Activity activity,AidlPinpad pinpad,String heserialNumber){
        this.mActivity = activity;
        this.pinpad =pinpad;
        this.etTheserialNumber = heserialNumber;
        phonePams = new HashMap<>();
        volleyUtil = VolleyUtil.getVolleyUtil(activity);//上网请求
        tsl = new TokenSQLUtils(activity);

    }
    /**
     * 上网请求得到主密钥
     */
    public void httpSign(){
        phonePams.put("poscode","YH000000001");//请求参数为，智能pos P92机器序列号；
        Log.i(ATG,"poscode===>"+etTheserialNumber);
        volleyUtil.StringRequestPostVolley(URLs.MASTERKEY, phonePams, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i(ATG,"主密钥jsonObject=====>"+jsonObject.toString());

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

//                JSONObject data = jsonOnject.getJSONObject("data");//密钥
                String masterkey = jsonOnject.getString("data");//主密钥密文
//                String checkvalue = data.getString("checkvalue");//主密钥解码值

                String checkvalue ="";
                Log.i("P92Pay","masterkey=====>"+masterkey);
                Log.i("P92Pay","checkvalue=====>"+checkvalue);
                getWorldKey(masterkey,checkvalue);//注入主密钥密文
            }
            Misidentification.misidentification1(mActivity, status, jsonOnject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 注入主密钥密文
     * @return
     */
    private boolean flag;
    private String  w ="";
    private String qw ="";
    public void  getWorldKey(String masterkey,String checkvalue){
        try {

            byte [] a = HexUtils.hexStringToByte(masterkey);

            for (int i = 0; i <a.length ; i++) {
                w += ""+a[i];
            }

            Log.i("P92Pay","masterkey长度=="+a.length);
            Log.i("P92Pay","masterkey解码=="+w);
//
//            byte [] s = HexUtils.hexStringToByte(checkvalue);
//
//            for (int i = 0; i <s.length; i++) {
//                qw += ""+s[i];
//            }
//            Log.i("P92Pay","hex2byte长度=="+s.length);
//            Log.i("P92Pay","hex2byte=="+qw);
//            Log.i(ATG,"开始灌入tek");


            //灌入主密钥明文
            flag = pinpad.loadMainkey(0,HexUtils.hexStringToByte(masterkey),null);

            if(flag){
                Log.i(ATG,"主密钥灌装成功");
                ToastUtil.getLongToastByString(mActivity,"机器准备完成");
            }else{
                Log.i(ATG,"主密钥灌装失败");
                mActivity.finish();
            }



//             boolean tek = pinpad.loadTEK(0,HexUtils.hexStringToByte(checkvalue),null);
//
//            if (tek){
//                Log.i(ATG,"TEK灌入成功");
//                Log.i(ATG,"主密钥灌");
////                //灌入主密钥密文
////                flag = pinpad.loadEncryptMainkey(0,0,HexUtils.hexStringToByte(masterkey),null);
//
//                //灌入主密钥明文
//                pinpad.loadMainkey(0,HexUtils.hexStringToByte(masterkey),null);
//
//                if(flag){
//                    Log.i(ATG,"主密钥灌装成功");
//                    ToastUtil.getLongToastByString(mActivity,"机器准备完成");
//                }else{
//                    Log.i(ATG,"主密钥灌装失败");
//                    ToastUtil.getLongToastByString(mActivity,"机器准备失败，建议重启机器");
//                }
//            }else {
//
//                Log.i(ATG,"TEK灌入失败");
//            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 得到灌入主密钥是否成功状态
     * @return
     */
    public boolean getIntoTheState(){

        return flag;

    }


}
