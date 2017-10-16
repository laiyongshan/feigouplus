package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.PswDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.AliPayDealActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.NewJoinpayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.P92PayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.PayActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.ScanQrPayActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.PayUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.example.youhe.youhecheguanjiaplus.wxapi.WXPayEntryActivity;
import com.joinpay.sdk.Joinpay;
import com.joinpay.sdk.bean.ConsumeResultData;
import com.joinpay.sdk.cons.MyAction;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/9/26 0026.
 * 支付点单逻辑类
 */
public class IPPWDao {


    private static Activity mActivity;
    private TokenSQLUtils tokenSQLUtils;
    private TextView resubmit;//当订单过期显示提醒

    public static int payType;
    public int order_type = 1;//1违章2年检3车主卡4plus用户激活
    public Bundle customerBundle = null;


    public IPPWDao(Activity activity, int order_type) {

        this.mActivity = activity;
        this.order_type = order_type;

        x.view().inject(mActivity);

        uiDialog = new ProgressDialog(mActivity);
        uiDialog.setTitle("提示");
        uiDialog.setMessage("正在校验支付....");

        if (android.os.Build.MODEL.equals("P92")) {

        } else {
            registerBoradcastReceiver();//如果是P84就是注册汇聚支付广播
        }

        tokenSQLUtils = new TokenSQLUtils(mActivity);
        EventBus.getDefault().register(this);
//        checkPayment();//校验支付

//        uiDialog.show();
//        UIHelper.showPd(mActivity);
//        getPaytype(mActivity,ThePosPay.getOrenumber());//获取支付通道
    }

    public void setCustomerBundle(Bundle bundle) {
        this.customerBundle = bundle;
    }


    /***
     * 判断机器型号
     * 跳转到不同的机型的支付界面
     */
    public void judgeMachineType() {

        String judgeMachineType = android.os.Build.MODEL;
        //判断机器机型
        if (judgeMachineType.equals("P92")) {//智能POS P92支付
//            select();//智能POS P92操作支付方式
            Intent intent = new Intent(mActivity, P92PayActivity.class);
            intent.putExtra("ordercode", ThePosPay.getOrenumber());//订单号
            intent.putExtra("paymoney", ThePosPay.getPrice());//未格式化的总金额
            intent.putExtra("showmoney", ThePosPay.getTotalPrice());//式化的总金额
            intent.putExtra(P92PayActivity.EXTRA_ORDER_TYPE, order_type);
            Log.i("WU", "ThePosPay.getType()====" + ThePosPay.getType());
            if (ThePosPay.getType().equals("5")) {
                intent.putExtra("feng", "违章办理");//总分
            } else {
                intent.putExtra("feng", "" + ThePosPay.getZonfuwu());//总分
            }
            mActivity.startActivity(intent);

        } else {

            gets();//请求终端号 并调起支付
        }
    }


    /**
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(MyAction.ACTION_PAY_CALLBACK);
        //注册广播
        mActivity.registerReceiver(payResultReceiver, myIntentFilter);
    }

    /**
     * 支付成功返回的结果
     */
    private BroadcastReceiver payResultReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MyAction.ACTION_PAY_CALLBACK)) {

                ConsumeResultData payResult = (ConsumeResultData) intent.getExtras().getSerializable(MyAction.PAYRESULT);
                if (payResult != null && payResult.isSuccess()) {//支付成功

                    Log.i("WU", "支付成功");
                    httsData(payResult);//提交支付成功的数据

                } else {//支付失败
                    zhifushib(payResult);//提交支付失败的数据
                    Log.i("WU", "支付失败");
                }
            }
        }
    };

    /**
     * 支付成功布局
     *
     * @param successful
     */
    public void setPayForResults(String successful) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        //创建自定义的布局 View
        View loginView = mActivity.getLayoutInflater().inflate(R.layout.pay_for_results_lay, null);
        TextView textViewS = (TextView) loginView.findViewById(R.id.textdoll);
        TextView textView = (TextView) loginView.findViewById(R.id.text);
        Log.i("WU", ThePosPay.payPrice + "");
        textViewS.setText("￥" + ThePosPay.payPrice);//显示总金额
        RelativeLayout view = (RelativeLayout) loginView.findViewById(R.id.denglijian);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FirstEvent("finish"));//当支付完成通知其他界面关闭
                EventBus.getDefault().post(new FirstEvent("ok"));//通知刷新
                mActivity.finish();
            }
        });
        textView.setText(successful);
        builder.setView(loginView);
        builder.show();
    }

    /**
     * 支付失败布局
     *
     * @param successful
     */
    private AlertDialog dialog;

    public void setPayForFailure(String successful) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        //创建自定义的布局 View
        View loginView = mActivity.getLayoutInflater().inflate(R.layout.pay_for_failure_lay, null);
        TextView textViewS = (TextView) loginView.findViewById(R.id.textdoll);
        TextView textView = (TextView) loginView.findViewById(R.id.text);
        Log.i("WU", ThePosPay.payPrice + "");
        textViewS.setText("￥" + ThePosPay.payPrice);//显示总金额
        RelativeLayout view = (RelativeLayout) loginView.findViewById(R.id.denglijian);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//重新支付点击事件
                gets();
                dialog.dismiss();
            }
        });
        textView.setText(successful);
        builder.setView(loginView);
        dialog = builder.show();
    }


    /**
     * 注销广播
     */
    public void destroy() {
        mActivity.unregisterReceiver(payResultReceiver);
    }

    /**
     * 提交支付完成的信息给后台
     */
    private HashMap phonePams;
    private VolleyUtil volleyUtil;//请求网络

    public void httsData(final ConsumeResultData payResults) {
        phonePams = new HashMap();

        volleyUtil = VolleyUtil.getVolleyUtil(mActivity);//上网请求
        String url = URLs.HUIJUZHIFU;
        String yhorderno = ThePosPay.getOrenumber();//得到订单号
        String token = tokenSQLUtils.check();
        phonePams.put("token", token);
        phonePams.put("yhorderno", yhorderno);//订单号
        phonePams.put("ordermoney", ThePosPay.payPrice);//订单总金额
        phonePams.put("respCode", "" + payResults.respCode);
        phonePams.put("message", "" + payResults.message);
        phonePams.put("merchno", "" + payResults.merchno);
        phonePams.put("termno", "" + payResults.termno);
        phonePams.put("cardno", "" + payResults.cardno);
        phonePams.put("datetime", "" + payResults.datetime);
        phonePams.put("batchno", "" + payResults.batchno);
        phonePams.put("traceno", "" + payResults.traceno);
        phonePams.put("refno", "" + payResults.refno);
        phonePams.put("field55", "" + payResults.field55);

        if (ThePosPay.ordertype == 3) {//年检订单传
            phonePams.put("is_annual_inspection", 1);//年检订单必传
        }

        Log.i("WU", "respCode==>" + payResults.respCode + "===message===>" + payResults.message
                + "===merchno===>" + payResults.merchno + "====termno===>" + payResults.termno

                + "===cardno===>" + payResults.cardno + "===datetime===>" + payResults.datetime
                + "===batchno==>" + payResults.batchno + "===traceno===>" + payResults.traceno
                + "===refno===>" + payResults.refno + "===field55===>" + payResults.field55
                + "===yhorderno==>" + yhorderno + "===token===>" + token + "===ordermoney==>" + ThePosPay.getTotalPrice()
        );


        volleyUtil.StringRequestPostVolley(url, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("WU", jsonObject.toString());
                try {
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), mActivity));

                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        setPayForResults(payResults.message);//传入支付成功信息
                        EventBus.getDefault().post(new FirstEvent("ok"));//提交信息成功通知刷新
                    } else {
                        setPayForFailure("支付成功,但处理失败");//传入支付成功信息
                        Misidentification.misidentification1(mActivity, status, jsonObject1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败");
                Log.i("WU", volleyError.getMessage());
            }
        });
    }


    public void zhifushib(final ConsumeResultData payResults) {
        phonePams = new HashMap<String, String>();

        volleyUtil = VolleyUtil.getVolleyUtil(mActivity);//上网请求
        String url = URLs.HUIJUZHIFU;
        String yhorderno = ThePosPay.getOrenumber();//得到订单号
        String token = tokenSQLUtils.check();
        phonePams.put("token", token);
        phonePams.put("yhorderno", yhorderno);//订单号
        phonePams.put("ordermoney", ThePosPay.getPrice());//订单总金额
        phonePams.put("respCode", "" + payResults.respCode);
        phonePams.put("message", "" + payResults.message);
        phonePams.put("merchno", "" + payResults.merchno);
        phonePams.put("termno", "" + payResults.termno);
        phonePams.put("cardno", "" + payResults.cardno);
        phonePams.put("datetime", "" + payResults.datetime);
        phonePams.put("batchno", "" + payResults.batchno);
        phonePams.put("traceno", "" + payResults.traceno);
        phonePams.put("refno", "" + payResults.refno);
        phonePams.put("field55", "" + payResults.field55);

        if (ThePosPay.ordertype == 3) {//年检订单传
            phonePams.put("is_annual_inspection", 1);//年检订单必传
        }


        Log.i("WU", "respCode==>" + payResults.respCode + "===message===>" + payResults.message
                + "===merchno===>" + payResults.merchno + "====termno===>" + payResults.termno

                + "===cardno===>" + payResults.cardno + "===datetime===>" + payResults.datetime
                + "===batchno==>" + payResults.batchno + "===traceno===>" + payResults.traceno
                + "===refno===>" + payResults.refno + "===field55===>" + payResults.field55
                + "===yhorderno==>" + yhorderno + "===token===>" + token + "===ordermoney==>" + ThePosPay.getTotalPrice()
        );


        volleyUtil.StringRequestPostVolley(url, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("WU", jsonObject.toString());
                try {
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), mActivity));

                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        setPayForFailure(payResults.message);//传入支付成功信息
                        EventBus.getDefault().post(new FirstEvent("ok"));//提交信息成功通知刷新
                    } else {
                        setPayForFailure("支付失败");//传入支付成功信息
                        Misidentification.misidentification1(mActivity, status, jsonObject1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败");
                Log.i("WU", volleyError.getMessage());
            }
        });
    }

    /**
     * 得到序列号和总金额判断并调起支付
     */
    public void gets() {

        VolleyUtil volleyUtil = VolleyUtil.getVolleyUtil(mActivity);//上网请求
        HashMap<String, String> phonePams = new HashMap<>();
        TokenSQLUtils tokenSQLUtils = new TokenSQLUtils(mActivity);
        String token = tokenSQLUtils.check();
        phonePams.put("token", token);

        volleyUtil.StringRequestPostVolley(URLs.GETTERMINALNUMBER, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "得到序列号和总金额判断并调起支付>>>>>>>>" + jsonObject.toString());
                jsons(jsonObject.toString());
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败");
                Log.i("TAG", volleyError.getMessage());
            }
        });

    }

    DecimalFormat df = new DecimalFormat("######0.00"); //保留两位小数点

    public void jsons(String json) {
        try {
            JSONObject jsonObject = new JSONObject(EncryptUtil.decryptJson(json, mActivity));
            String status = jsonObject.getString("status");
            if (status.equals("ok")) {

                String aa = jsonObject.getString("data");//序列号
                JSONObject jsonObject1 = new JSONObject(aa);
                String theSerialNumber = jsonObject1.getString("poscode");

                String zonfuwu = "";
                String info = "";
                if (ThePosPay.ordertype == 1) {
                    if (ThePosPay.getType().equals("5")) {
                        zonfuwu = "违章办理";
                    } else {
                        zonfuwu = ThePosPay.getZonfuwu() + "分";
                    }
                    info = "违章充值";
                    Log.i("WU", "汇聚总金额" + df.format(ThePosPay.payPrice) + "");
                } else if (ThePosPay.ordertype == 2) {//补款订单
                    zonfuwu = "订单补款";
                    info = mActivity.getResources().getString(R.string.app_name);
                } else if (ThePosPay.ordertype == 3) {//年检订单
                    zonfuwu = "车辆年检";
                    info = mActivity.getResources().getString(R.string.app_name);
                }
//                Log.d("TAG","payType"+payType);
                if (payType == 1) {//汇聚支付
                    Joinpay.startPay(mActivity, ThePosPay.payPrice + "", "13266663863", theSerialNumber, zonfuwu, info);
                } else if (payType == 3) {//茂捷二维码支付
                    Intent payintent = new Intent(mActivity, ScanQrPayActivity.class);
                    payintent.putExtra("ordercode", ThePosPay.getOrenumber());//订单号
                    payintent.putExtra("paymoney", df.format(ThePosPay.payPrice) + "");//支付金额
                    payintent.putExtra("totalDegree", zonfuwu);
                    payintent.putExtra("mjOpenType", CommentSetting.mjOpenType);
                    payintent.putExtra("orderstyle", ThePosPay.ordertype);//订单类型
                    payintent.putExtra("order_type", order_type);//订单类型
                    payintent.putExtra("info", info);

                    if (customerBundle != null) {
                        if (customerBundle.containsKey(ScanQrPayActivity.EXTRA_RETURN_CLASS))
                            payintent.putExtra(ScanQrPayActivity.EXTRA_RETURN_CLASS, customerBundle.getString(ScanQrPayActivity.EXTRA_RETURN_CLASS));
                        payintent.putExtra(ScanQrPayActivity.EXTRA_CUSTOMER_BUNDLE, customerBundle);
                    }
                    mActivity.startActivity(payintent);
                    mActivity.finish();
                } else if (payType == 2) {//银嘉支付
                    Intent payintent = new Intent(mActivity, NewJoinpayActivity.class);
                    payintent.putExtra("price", df.format(ThePosPay.payPrice) + "");//支付金额
                    payintent.putExtra("theSerialNumber", theSerialNumber);//机器序列号
                    payintent.putExtra("totalDegree", zonfuwu);//总分
                    payintent.putExtra("ordercode", ThePosPay.getOrenumber());//订单号
                    payintent.putExtra("orderstyle", ThePosPay.ordertype);//订单类型
                    Log.d("TAG", "IPWAO" + order_type);
                    payintent.putExtra(NewJoinpayActivity.EXTRA_ORDER_TYPE, order_type + "");
                    payintent.putExtra("info", info);
                    if (customerBundle != null)
                        payintent.putExtra(NewJoinpayActivity.EXTRA_CUSTOMER_BUNDLE, customerBundle);
                    mActivity.startActivity(payintent);
                    mActivity.finish();
                } else if (payType == 4) {//余额支付

                    final PswDialog pswDialog = new PswDialog(mActivity, R.style.Dialog, 1);
                    pswDialog.show();
                    //回调接口
                    pswDialog.mSureListener = new PswDialog.OnSureListener() {
                        @Override
                        public void onSure(String psw) {

                            HashMap map = new HashMap();
                            map.put("token", TokenSQLUtils.check());
                            map.put("ordercode", ThePosPay.getOrenumber());//订单编号
                            map.put("paymoney", df.format(ThePosPay.payPrice) + "");//支付金额
                            map.put("is_balance_deductible", 1);//是否使用余额抵扣
                            if (ThePosPay.ordertype == 3) {//年检订单传
                                map.put("is_annual_inspection", 1);
                            }
                            map.put("password", ParamSign.getUserPassword(psw));
                            map.put("order_type", order_type);

                            PayUtil.balancePay(mActivity, map, customerBundle);

                            pswDialog.dismiss();
                        }
                    };


                } else if (payType == PayActivity.METHOD_APP_ALIPAY) {//支付宝支付
                    AppPay(2);//1：微信支付  2：支付宝支付3:全部
                } else if (payType == PayActivity.METHOD_APP_WEIXIN) {//微信支付
                    //  weixinPay("1");//1：微信支付  2：支付宝支付3:全部
                    AppPay(1);
                } else {
                    Toast.makeText(mActivity, "支付通道系统维护中，维护完成后通知，敬请谅解", Toast.LENGTH_LONG).show();
                }
            } else {
                Misidentification.misidentification1(mActivity, status, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 支付宝支付
     */
    private void AppPay(final int pay_type) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("token", TokenSQLUtils.check());
        hash.put("ordercode", ThePosPay.getOrenumber());
        hash.put("pay_type", pay_type + "");//1：微信支付  2：支付宝支付3:全部
        hash.put("pay_money", df.format(ThePosPay.payPrice) + "");//支付金额
        if (ThePosPay.is_balance_deductible == 1)
            hash.put("is_balance_deductible", "1");//使用余额抵扣时必填1
        hash.put("order_type", order_type + "");//订单类型  1违章2年检3车主卡

        VolleyUtil volleyUtil = VolleyUtil.getVolleyUtil(mActivity);//上网请求
        volleyUtil.postRequest(mActivity, URLs.APPPAY_INFO, hash, "获取订单信息失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {
                    Log.d("TAG", dataObject.toString());
                    if (pay_type == 2) {//支付宝
                        String ali_pay_info = dataObject.optString("ali_pay_info");
                        if (StringUtils.isEmpty(ali_pay_info))
                            ToastUtil.getShortToastByString(mActivity, "获取订单信息失败");
                        else
                            aliPay(ali_pay_info);
                    } else if (pay_type == 1) {//微信
                        String wx_pay_info = dataObject.optString("wx_pay_info");
                        if (StringUtils.isEmpty(wx_pay_info))
                            ToastUtil.getShortToastByString(mActivity, "获取订单信息失败");
                        else
                            weixinPay(wx_pay_info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.getShortToastByString(mActivity, "获取订单信息失败");
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
//                Log.d("TAG","DDD"+code+","+msg);
                if (StringUtils.isEmpty(msg) || msg.equals("false")) {
                    ToastUtil.getShortToastByString(mActivity, "获取订单信息失败");
                } else
                    ToastUtil.getShortToastByString(mActivity, msg);
            }
        });
    }

    /**
     * 微信支付
     */
    private void weixinPay(String payInfo) {

        Log.d("TAG", "payInfo" + payInfo);
        try {
            JSONObject jsonObject = new JSONObject(payInfo);
            final IWXAPI msgApi = WXAPIFactory.createWXAPI(mActivity, null);
// 将该app注册到微信
            msgApi.registerApp(jsonObject.getString("appid"));

            PayReq request = new PayReq();

            request.appId = jsonObject.getString("appid");
            request.partnerId = jsonObject.getString("partnerid");
            request.prepayId = jsonObject.getString("prepayid");
            request.packageValue = "Sign=WXPay";
            request.nonceStr = jsonObject.getString("noncestr");
            request.timeStamp = jsonObject.getString("timestamp");
            request.sign = jsonObject.getString("sign");
            WXPayEntryActivity.customerBundle = customerBundle;
            WXPayEntryActivity.orderCode = ThePosPay.getOrenumber();

            msgApi.sendReq(request);

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.getShortToastByString(mActivity,"获取订单信息失败");
        }
    }

    private void aliPay(final String payInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(payInfo, true);

                Message msg = new Message();
                msg.what = HANDLER_ALIPAY;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private final int HANDLER_ALIPAY = 4;



    /**
     * 去支付前 校验
     */
    public void checkPayment(final Activity activity, String orderCode, String ordermoney) {

        resubmit = (TextView) activity.findViewById(R.id.resubmit);//当订单无效时提醒客户
        handler.sendEmptyMessage(1);


        TokenSQLUtils tokenSQLUtilss = new TokenSQLUtils(activity);
        HashMap<String, String> phonePamss = new HashMap<>();
        VolleyUtil volleyUtils = VolleyUtil.getVolleyUtil(activity);//上网请求
        final String token = tokenSQLUtilss.check();//得到Token;
//        String yhorderno = ThePosPay.getOrenumber();//得到订单号

        phonePamss.put("token", token);
        phonePamss.put("ordercode", orderCode);
        phonePamss.put("ordermoney", ordermoney);
        if (ThePosPay.is_make_up_money())//是否补款
            phonePamss.put("is_make_up_money", "1");

        Log.i("WU", "ThePosPay.getTotalPrice()===>>" + ThePosPay.getPrice());
        Log.i("WU", "token===>>" + token);
        volleyUtils.StringRequestPostVolley(URLs.ORDER_CHECK, EncryptUtil.encrypt(phonePamss), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("WU", "jsonObject====" + jsonObject.toString());
                try {
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));

                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        handler.sendEmptyMessage(2);
                        ToastUtil.getLongToastByString(activity, "支付校验成功");
                    } else {
                        handler.sendEmptyMessage(2);
                        Button pay_btn = (Button) activity.findViewById(R.id.pay_btn);
                        pay_btn.setClickable(false);
                        pay_btn.setBackgroundResource(R.drawable.gotopay2);
                        EventBus.getDefault().post(new FirstEvent("ok"));//通知刷新界面
                        EventBus.getDefault().post(new FirstEvent("finish"));//通知关闭界面
                        if (status.equals("fail")) {
                            String code = jsonObject1.getString("code");
                            if (code.equals("40015")) {
                                resubmit.setVisibility(View.VISIBLE);
                                ToastUtil.getLongToastByString(activity, "您的订单已过期,请重新提交订单");
                            }
                            Misidentification.misidentification1(activity, status, jsonObject1);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(activity, "网络连接失败");
                Log.i("WU", volleyError.getMessage());
            }
        });
    }


    /**
     * 余额抵扣时订单检测
     */
    public void orderCheck(final Activity activity, int paytype) {
//        Log.d("TAG", "DDDDD" + ThePosPay.payPrice);
        UIHelper.showPd(activity);
        HashMap map = new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("ordercode", ThePosPay.getOrenumber());
//        map.put("ordermoney", ThePosPay.payPrice);
        map.put("ordermoney", df.format(ThePosPay.payPrice) + "");
        if (paytype != 4) {//非余额支付
            map.put("is_balance_deductible", 1);
        }

        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.ORDER_CHECK, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        judgeMachineType();
                    } else {
                        Toast.makeText(activity, "订单校验失败，请退出重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }


    /*
    * 检测年检订单
    * */
    public void annualOrderCheck(final Activity activity, int paytype) {
        UIHelper.showPd(activity);
        HashMap map = new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("ordercode", ThePosPay.getOrenumber());
        map.put("ordermoney", ThePosPay.payPrice);
        if (paytype != 4) {//非余额支付
            map.put("is_balance_deductible", 1);
        }

        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.ANNUAL_ORDER_CHECK, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", jsonObject.toString());
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                    String status = obj.getString("status");
                    if (status.equals("ok")) {
                        judgeMachineType();
                    } else {
                        if (obj.has("show_msg")) {
                            Toast.makeText(activity, "" + obj.optString("show_msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "订单校验失败，请退出重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });

    }


    private ProgressDialog uiDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (uiDialog != null && !mActivity.isFinishing() && !uiDialog.isShowing())
                        uiDialog.show();
                    break;
                case 2:
                    if (uiDialog != null && uiDialog.isShowing())
                        uiDialog.hide();
                    break;
                case HANDLER_ALIPAY:
                    try {
                        Map<String, String> m = (Map<String, String>) msg.obj;
//                        Log.d("TAG","SSs"+m.toString());
                        String status = m.get("resultStatus");
                        if (status.equals("9000")) {
//                            checkAlipayInfo();
                            Intent intent = new Intent(mActivity, AliPayDealActivity.class);
                            intent.putExtra("ordercode", ThePosPay.getOrenumber());
                            if (customerBundle != null)
                                intent.putExtra(AliPayDealActivity.EXTRA_CUSTOMER_BUNDLE, customerBundle);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        } else {
                            String a = m.get("memo");
                            ToastUtil.getShortToastByString(mActivity, a);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };


    /**
     * 通知刷新
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }


    public void plusOrderCheck(final PayActivity payActivity, int payType) {
        UIHelper.showPd(payActivity);
        HashMap<String, String> hash = new HashMap<>();
        hash.put("token", TokenSQLUtils.check());
        hash.put("ordercode", ThePosPay.getOrenumber());
        hash.put("ordermoney", ThePosPay.payPrice + "");

        if (payType != 4) {//非余额支付
            hash.put("is_balance_deductible", 1 + "");
        }

        VolleyUtil.getVolleyUtil(payActivity).postRequest(payActivity, URLs.PLUS_ORDER_CHECK, hash, "订单检测失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                UIHelper.dismissPd();
                judgeMachineType();
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                if (StringUtils.isEmpty(msg))
                    Toast.makeText(payActivity, "订单校验失败，请退出重试", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(payActivity, msg, Toast.LENGTH_SHORT).show();
                UIHelper.dismissPd();
            }
        });
    }
}
