package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public class PayFirstEvent {
    private String mMsg;
    private String mPaymsg;


    /**
     * 用来显示密码输入框
     * @param msg
     * @param paymsg
     */
    public PayFirstEvent(String msg,String paymsg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
        mPaymsg = paymsg;
    }

    public String getMsg() {
        return mMsg;
    }

    public String getMPaymsg(){
        return mPaymsg;
    }





}
