package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class Deal implements Serializable{

    private static final long serialVersionUID = 2610745963889407425L;

    private String merchant_name;//商户名称
    private String ordercode;//订单编号
    private String merchant_number;//商户编号
    private String max_flowing_no;//交易流水
    private String pay_time;//支付时间
    private String merchant_type;//商户类型
    private String pay_money;//交易金额
    private String car_type;//车辆类型
    private String car_number;//车牌号码
    private String card_no;//卡号

    private String strImg;//签名图片Base64

    public void setStrImg(String strImg) {
        this.strImg = strImg;
    }

    public String getStrImg() {
        return strImg;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getMerchant_number() {
        return merchant_number;
    }

    public void setMerchant_number(String merchant_number) {
        this.merchant_number = merchant_number;
    }

    public String getMax_flowing_no() {
        return max_flowing_no;
    }

    public void setMax_flowing_no(String max_flowing_no) {
        this.max_flowing_no = max_flowing_no;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getMerchant_type() {
        return merchant_type;
    }

    public void setMerchant_type(String merchant_type) {
        this.merchant_type = merchant_type;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }
}
