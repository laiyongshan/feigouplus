package com.example.youhe.youhecheguanjiaplus.bean;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class Violation implements Serializable{

    public static final String COLUMN_CARID = "car_id";//车辆id
    private static final long serialVersionUID = -7345973531847463808L;

    private String carId;//车辆id

    public String id;

    public String searchid;

    public String time;//违章时间

    public String location;//违章地点

    public String reason;//违章原因

    public int count;//违章罚款金额

    public String status;//违章记录状态0 末处理  1 己处理(绝大部分情况下，车行易只能返回未处理的违章)

    public int degree;//违章扣分

    public String code;//违章代码

    public String category;//违章分类类别（如该字段显示为“现场单”，请注意提示用户）


    public String locationname;//违章归属地点名

    public String poundage;//手续费（标准价，合作方请无视）

    public String canprocess;//是否可以代办    0  不可以  1 可以

    public String canusepackage;//是否可使用套餐包 0  不可以  1 可以 只有DYQueryIndex2.aspx接口才返回，如果可用套餐包，则手续费返回为 套餐包的使用规则：1）违章条数要小于等于剩余次数违章所在地为车牌所在省份。

    public String secondaryuniquecode;//违章记录ID，用于下单。

    public int price;//代办价格

    public String orderstatus;//处理状态 返回什么显示什么

    public int quotedprice;//1 ：实时报价  2：待报价  -1:不可代办

    public int latefee; //滞纳金

    public int pickone;//可否挑单 1：启用挑单限制  -1：不启用

    public String remark;//违章说明

    public int other;//实际执行扣分

    public int iscommit;//是否可以提交订单 1:可以提交  2：不可提交

    public void setIscommit(int iscommit) {
        this.iscommit = iscommit;
    }

    public int getIscommit() {
        return iscommit;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public int getOther() {
        return other;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setPickone(int pickone) {
        this.pickone = pickone;
    }

    public int getPickone() {
        return pickone;
    }

    public void setLatefee(int latefee) {
        this.latefee = latefee;
    }

    public int getLatefee() {
        return latefee;
    }

    public void setQuotedprice(int quotedprice) {
        this.quotedprice = quotedprice;
    }

    public int getQuotedprice() {
        return quotedprice;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchid() {
        return searchid;
    }

    public void setSearchid(String searchid) {
        this.searchid = searchid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCanprocess() {
        return canprocess;
    }

    public void setCanprocess(String canprocess) {
        this.canprocess = canprocess;
    }

    public String getCanusepackage() {
        return canusepackage;
    }

    public void setCanusepackage(String canusepackage) {
        this.canusepackage = canusepackage;
    }

    public String getSecondaryuniquecode() {
        return secondaryuniquecode;
    }

    public void setSecondaryuniquecode(String secondaryuniquecode) {
        this.secondaryuniquecode = secondaryuniquecode;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage;
    }

    public String toString() {
        return "";
    }
}
