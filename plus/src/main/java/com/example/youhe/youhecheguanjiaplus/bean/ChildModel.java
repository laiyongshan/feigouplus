package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class ChildModel implements Serializable{

    private static final long serialVersionUID = -3912950654479683061L;

    private String id="";//车辆id
    private String proprefix="";//车牌前缀
    private String carnum="";//车牌号码
    private String carVin="";//车身架号
    private String carEngineNo="";//发动机号
    private String cartype="";//车辆类型
    private int iscommit;//是否可以提交

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getCartype() {
        return cartype;
    }

    public void setIscommit(int iscommit) {
        this.iscommit = iscommit;
    }

    public int getIscommit() {
        return iscommit;
    }

    public void setProprefix(String proprefix) {
        this.proprefix = proprefix;
    }

    public String getProprefix() {
        return proprefix;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarVin(String carVin) {
        this.carVin = carVin;
    }

    public String getCarVin() {
        return carVin;
   }

    public void setCarEngineNo(String carEngineNo) {
        this.carEngineNo = carEngineNo;
    }

    public String getCarEngineNo() {
        return carEngineNo;
    }
}

