package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * 车主卡类型 实体
 * Created by Administrator on 2017/7/14 0014.
 */

public class PlusRoleBean implements Serializable{

    private static final long serialVersionUID = 1773730922268647725L;
    private String name;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PlusRoleBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }



}
