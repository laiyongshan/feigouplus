package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.Deal;
import com.example.youhe.youhecheguanjiaplus.biz.ThePosPay;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.BalancePaySuccActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.FailurePayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.ScanQrPayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.TheDealActivity;
import com.joinpay.sdk.Joinpay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class PayUtil {

    //汇聚支付
    private static void huijuPay(Context mActivity, String price, String phone, String theSerialNumber, String zonfuwu, String tradeName) {
        Joinpay.startPay(mActivity, ThePosPay.getPrice(), "13266663863", theSerialNumber, zonfuwu, "违章充值");
    }


    //银嘉支付
    public static void yinjiaPay(final Activity activity, HashMap map, final String strImg) {
        UIHelper.showPd(activity);
        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.UNIONPAY_CONSUME, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "银嘉支付返回的数据:" + EncryptUtil.decryptJson(jsonObject.toString(), activity));
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                    String status = obj.optString("status");
                    String show_msg = "";
                    Deal deal = new Deal();
                    if (status.equals("ok")) {

                        JSONObject dataObj = obj.getJSONObject("data");
                        String merchant_name = dataObj.optString("merchant_name");
                        String ordercode = dataObj.optString("ordercode");
                        String merchant_number = dataObj.optString("merchant_number");
                        String max_flowing_no = dataObj.optString("max_flowing_no");
                        String pay_time = dataObj.optString("pay_time");
                        String merchant_type = dataObj.optString("merchant_type");
                        String pay_money = dataObj.optString("pay_money");
                        String car_type = dataObj.optString("car_type");
                        String car_number = dataObj.optString("car_number");
                        String card_no = dataObj.optString("card_no");

                        deal.setMerchant_name(merchant_name);
                        deal.setOrdercode(ordercode);
                        deal.setMerchant_number(merchant_number + "");
                        deal.setMax_flowing_no(max_flowing_no);
                        deal.setPay_time(pay_time);
                        deal.setMerchant_type(merchant_type);
                        deal.setPay_money(pay_money);
                        deal.setCar_type(car_type + "");
                        deal.setCar_number(car_number);
                        deal.setCard_no(card_no + "");
                        deal.setStrImg(strImg);

                        Intent intent = new Intent(activity, TheDealActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("deal", deal);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    } else {
                        if (obj.has("show_msg")) {
                            show_msg = obj.optString("show_msg");
                        }
                        Intent intent = new Intent(activity, FailurePayActivity.class);
                        intent.putExtra("show_msg", show_msg);
                        activity.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(activity, FailurePayActivity.class);
                    activity.startActivity(intent);
                } finally {
                    activity.finish();
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG", "银嘉支付返回的错误数据:" + volleyError.toString());
                Intent intent = new Intent(activity, FailurePayActivity.class);
                activity.startActivity(intent);
            }
        });
    }


    public static void getPayStatus(final Activity activity, HashMap map, final Bundle bundle) {
        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.GET_PAY_STATUS, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "获取订单支付结果(茂捷线上二维码通道):" + jsonObject.toString());
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                    String status = obj.optString("status");
                    String show_msg = "";
                    Deal deal = new Deal();
                    if (status.equals("ok")) {

                        JSONObject dataObj = obj.getJSONObject("data");
                        int payStatus = dataObj.optInt("payStatus");

                        if (payStatus == 1) {//未支付

                        } else if (payStatus == 2) {//支付成功

                            ScanQrPayActivity.timer.cancel();

//                            String merchant_name = dataObj.optString("merchant_name");
                            String ordercode = dataObj.optString("ordercode");
                            String merchant_number = "";
                            if (dataObj.has("merchant_number")) {
                                merchant_number = dataObj.optString("merchant_number");
                            }
//                            String max_flowing_no = dataObj.optString("max_flowing_no");
                            String pay_time = dataObj.optString("pay_time");
//                            String merchant_type = dataObj.optString("merchant_type");
                            String pay_money = dataObj.optString("pay_money");
                            String car_type = "";
                            if (dataObj.has("car_type")) {
                                car_type = dataObj.optString("car_type");
                            }
                            String car_number = dataObj.optString("car_number");
                            String card_no = "";
                            if (dataObj.has("card_no")) {
                                card_no = dataObj.optString("card_no");
                            }

//                            deal.setMerchant_name(merchant_name);
                            deal.setOrdercode(ordercode);
                            deal.setMerchant_number(merchant_number + "");
//                            deal.setMax_flowing_no(max_flowing_no);
                            deal.setPay_time(pay_time);
//                            deal.setMerchant_type(merchant_type);
                            deal.setPay_money(pay_money);
                            deal.setCar_type(car_type + "");
                            deal.setCar_number(car_number);
                            deal.setCard_no(card_no + "");

                            Intent intent = new Intent(activity, BalancePaySuccActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("deal", deal);
//                            intent.putExtras(bundle);
                            intent.putExtra("paymoney", pay_money);
                            intent.putExtra("ordercode", ordercode);
                            intent.putExtra("paytime", pay_time);
                            if (bundle != null) {
                                try {
                                    if (bundle.containsKey(ScanQrPayActivity.EXTRA_CUSTOMER_BUNDLE)) {
                                        Bundle customer = bundle.getBundle(ScanQrPayActivity.EXTRA_CUSTOMER_BUNDLE);
                                        if (customer != null && customer.containsKey(UserManager.STRING_USER_STATUS)) {//设置用户激活状态
                                            UserManager.setUserStatus(UserManager.USER_STATUS_YES);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                intent.putExtra("bundle", bundle);
                            }
                            activity.startActivity(intent);
                            activity.finish();
                        } else if (payStatus == -1) {//支付失败

                            ScanQrPayActivity.timer.cancel();

                            if (obj.has("show_msg")) {
                                show_msg = obj.optString("payMsg");
                            }
                            Intent intent = new Intent(activity, FailurePayActivity.class);
                            intent.putExtra("show_msg", show_msg);
                            if (bundle != null)
                                intent.putExtra("bundle", bundle);
                            activity.startActivity(intent);
                            activity.finish();
                        } else if (payStatus == -2) {//过期或未支付
//                            Intent intent=new Intent(activity, FailurePayActivity.class);
//                            intent.putExtra("show_msg","二维码过期或未提交订单，请重新支付");
//                            activity.startActivity(intent);
                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG", "获取订单支付结果" + volleyError.toString());
            }
        });
    }


    /***
     * 获取订单支付结果(茂捷线上二维码通道)
     * **/
    public static void getPayStatus(final Activity activity, HashMap map) {
        getPayStatus(activity, map, null);
    }


    /*
    * 余额支付
    * */
    public static void balancePay(final Activity activity, HashMap map) {

        UIHelper.showPd(activity);

        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.BALANCE_PAYMENT_URL, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "余额支付结果:" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                    String status = obj.optString("status");
                    JSONObject dataObj = new JSONObject();
                    if (status.equals("ok")) {
                        dataObj = obj.optJSONObject("data");
                        int payStatus = 0;
                        if (dataObj.has("payStatus")) {
                            payStatus = dataObj.optInt("payStatus");
                        }
                        if (payStatus == 1) {//支付成功
                            String paymoney = dataObj.optString("paymoney");
                            String ordercode = dataObj.optString("ordercode");
                            String paytime = dataObj.optString("paytime");
                            Intent intent = new Intent(activity, BalancePaySuccActivity.class);
                            intent.putExtra("paymoney", paymoney);
                            intent.putExtra("ordercode", ordercode);
                            intent.putExtra("paytime", paytime);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    } else {//支付失败
                        String show_msg = "";
                        if (dataObj.has("payMsg")) {
                            show_msg = dataObj.optString("payMsg");
                        } else if (obj.has("show_msg")) {
                            show_msg = obj.optString("show_msg");
                        }
                        Intent intent = new Intent(activity, FailurePayActivity.class);
                        intent.putExtra("show_msg", show_msg);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG", "余额支付结果:" + volleyError.toString());
                Intent intent = new Intent(activity, FailurePayActivity.class);
                UIHelper.dismissPd();
            }
        });
    }

}
