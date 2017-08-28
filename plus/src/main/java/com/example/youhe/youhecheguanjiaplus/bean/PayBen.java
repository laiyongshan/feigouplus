package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/12 0012.
 * 支付卡片获取数据的Ben类
 */

public class PayBen implements Serializable{

    //订单号
    private String ordercode;

    //卡号
    private  String cardnumber;

    //磁2磁道数据
    private  String track2;

    //磁2磁道数据长度
    private String track2length;

    //3磁道数据
    private String track3;

    //3磁道数据长度
    private String track3length;

    //是否需要输入PIN标识。"01"需输入；"00"不需输入
    private String needpin;

    //ICDATA,55域
    private String icdata;

    //卡片序列号
    private String crdsqn;

    //卡片失效日期
    private String expired;

    //输入的密码
    private String pinBlock;

    //卡片类型
    private String cardtype;

    //总价格
    private String totalPrice;

    //总金额
    private String paymoney;

    //订单类型  1违章2年检3车主卡
    private String order_type;

    public PayBen(){

    }

    public PayBen(String cardtype, String cardnumber, String track2, String track2length, String track3, String track3length, String needpin, String icdata, String crdsqn, String expired, String pinBlock) {
        this.cardnumber = cardnumber;
        this.track2 = track2;
        this.track2length = track2length;
        this.track3 = track3;
        this.track3length = track3length;
        this.needpin = needpin;
        this.icdata = icdata;
        this.crdsqn = crdsqn;
        this.expired = expired;
        this.pinBlock = pinBlock;
        this.cardtype = cardtype;

    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getPaymoney() {
        return paymoney;
    }

    public void setPaymoney(String paymoney) {
        this.paymoney = paymoney;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getPinBlock(){

        return pinBlock;
    }

    public void setPinBlock(String pinBlock){
        this.pinBlock = pinBlock;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getTrack2length() {
        return track2length;
    }

    public void setTrack2length(String track2length) {
        this.track2length = track2length;
    }

    public String getTrack3() {
        return track3;
    }

    public void setTrack3(String track3) {
        this.track3 = track3;
    }

    public String getTrack3length() {
        return track3length;
    }

    public void setTrack3length(String track3length) {
        this.track3length = track3length;
    }

    public String getNeedpin() {
        return needpin;
    }

    public void setNeedpin(String needpin) {
        this.needpin = needpin;
    }

    public String getIcdata() {
        return icdata;
    }

    public void setIcdata(String icdata) {
        this.icdata = icdata;
    }

    public String getCrdsqn() {
        return crdsqn;
    }

    public void setCrdsqn(String crdsqn) {
        this.crdsqn = crdsqn;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }
}
