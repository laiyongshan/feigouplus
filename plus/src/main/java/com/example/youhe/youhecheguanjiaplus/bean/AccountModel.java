package com.example.youhe.youhecheguanjiaplus.bean;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class AccountModel {
    private String vehicleAccount="";//车管所账号
    private String userType="";//车管所账号类型
    private String ProvinceName="";//省份名称
    private LinkedList<ChildModel> childModels=new LinkedList<ChildModel>();

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setVehicleAccount(String vehicleAccount) {
        this.vehicleAccount = vehicleAccount;
    }

    public String getVehicleAccount() {
        return vehicleAccount;
    }

    public void setChildModels(LinkedList<ChildModel> childModels) {
        this.childModels = childModels;
    }

    public LinkedList<ChildModel> getChildModels() {
        return childModels;
    }
}
