package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.P92PayBen;
import com.example.youhe.youhecheguanjiaplus.bean.PayBen;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.FailurePayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.TheDealActivity;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/12 0012.
 * 支付提交数据
 */

public class HttpPayUtils {

    private Activity mActivity;
    private TokenSQLUtils tokenSQLUtils;
    private HashMap<String, String> phonePams;
    private VolleyUtil volleyUtil;//请求网络
    private ProgressBar progressBar;

    public HttpPayUtils(Activity activity){

        this.mActivity = activity;
        tokenSQLUtils = new TokenSQLUtils(activity);
        phonePams = new HashMap<String, String>();
        volleyUtil = VolleyUtil.getVolleyUtil(mActivity);//上网请求
        progressBar = (ProgressBar) mActivity.findViewById(R.id.progressBar);

    }

    /**
     *
     * @param payBen 支付数据存储类
     * @param bitmapBas64 转成bas64签名墙图片
     * @param terminalSn 终端序列号
     */
    public void uploadPayData(PayBen payBen, final String bitmapBas64, String terminalSn){
        progressBar.setVisibility(View.VISIBLE);
        String token = tokenSQLUtils.check();
        Log.i("P92Pay","提交uploadPayData");
        if (payBen!=null){
            phonePams.put("ordercode",payBen.getOrdercode());//订单号
            phonePams.put("token",token);
            phonePams.put("paymoney",payBen.getPaymoney());//支付金额
            if (payBen.getCrdsqn()!=null){
                phonePams.put("cardserialnumber",payBen.getCrdsqn());//卡片序列号(磁条卡不需传)
            }

            phonePams.put("mainaccount",payBen.getCardnumber());//卡号
            if (payBen.getIcdata()!=null){
                phonePams.put("iccardinfo",payBen.getIcdata());//55域数据（磁条卡不要传）
            }
            if (payBen.getTrack3()!=null){
                phonePams.put("trackInfo3",payBen.getTrack3());//3磁道数据（有就传没有就不用传）
            }
            phonePams.put("trackInfo2",payBen.getTrack2());//2磁道数据
            phonePams.put("passwordpin",payBen.getPinBlock());//持卡人输入的加密密码
            phonePams.put("cardvalidity",payBen.getExpired());//卡的有效期(选填)
            phonePams.put("cardtype",payBen.getCardtype());//卡片类型
            phonePams.put("poscode","YH000000001");//智能POS P92序列号
            phonePams.put("signimg",bitmapBas64);//签名图片

            Log.i("P92Pay","ordercode==>"+payBen.getOrdercode());
            Log.i("P92Pay","token==>"+token);
            Log.i("P92Pay","paymoney==>"+payBen.getPaymoney());
            Log.i("P92Pay","mainaccount==>"+payBen.getCardnumber());
            Log.i("P92Pay","cardserialnumber==>"+payBen.getCrdsqn());
            Log.i("P92Pay","iccardinfo==>"+payBen.getIcdata());
            Log.i("P92Pay","trackInfo2==>"+payBen.getTrack2());
            Log.i("P92Pay","trackInfo3==>"+payBen.getTrack3());
            Log.i("P92Pay","passwordpin==>"+payBen.getPinBlock());
            Log.i("P92Pay","cardvalidity==>"+payBen.getExpired());
            Log.i("P92Pay","cardtype==>"+payBen.getCardtype());
            Log.i("P92Pay","P92终端序列号==>"+terminalSn);


//            Log.i("P92Pay","图片==>"+bitmapBas64);
//
            volleyUtil.StringRequestPostVolley(URLs.HTTPSPAY, phonePams, new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    Log.i("P92Pay", "jsonObject.toString==>"+jsonObject.toString());
                    getJsonDate(bitmapBas64,jsonObject.toString());
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(mActivity, "网络连接失败");
                    Log.i("WU", volleyError.getMessage());
                }
            });
        }

    }
    public void getJsonDate(String base64,String jsons){
        try {
            JSONObject jsonObject = new JSONObject(jsons);
            String status = jsonObject.getString("status");
            Log.i("P92Pay","jsonArray====");
            if (status.equals("ok")){//流程处理完
                //解析Json
                P92PayBen p92Pay = GsonImpl.get().toObject(jsons, P92PayBen.class);
                if (p92Pay.getData().getRes_code().equals("00")){//交易完成
                    Intent intent = new Intent(mActivity, TheDealActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("P92PayBen",p92Pay);
                    bundle.putString("bitmapBas64",base64);
                    intent.putExtra("p92",bundle);
                    mActivity.startActivity(intent);
                    Log.i("P92Pay","car_number==>"+p92Pay.getData().getCar_number()
                            +"car_type==>"+p92Pay.getData().getCar_type()
                            +"card_no==>"+p92Pay.getData().getCard_no()
                            +"max_flowing_no==>"+p92Pay.getData().getMax_flowing_no()
                            +"merchant_name===>"+p92Pay.getData().getMerchant_name()
                            +"merchant_number===>"+p92Pay.getData().getMerchant_number()
                            +"merchant_type==>"+p92Pay.getData().getCar_type()
                            +"ordercode==>"+p92Pay.getData().getOrdercode()
                            +"pay_money==>"+p92Pay.getData().getPay_money()
                            +"pay_time==>"+p92Pay.getTime()
                            +"res_code==>"+p92Pay.getCode()
                            +"res_msg==>"+p92Pay.getMsg()
                    );
                }else {//流程处理完成但交易失败
                    Intent intent  = new Intent(mActivity, FailurePayActivity.class);
                    intent.putExtra("P92PayBen",p92Pay);
                    intent.putExtra("biaoshi","processingiscomplete");
                    mActivity.startActivity(intent);

                }


            }else if (status.equals("fail")){//流程处理失败
                Intent intent  = new Intent(mActivity, FailurePayActivity.class);
                intent.putExtra("P92PayBen",jsonObject.getString("code"));
                intent.putExtra("biaoshi","todealwithfailure");
                mActivity.startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mActivity.finish();
    }
}
