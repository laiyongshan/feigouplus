package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2016/9/12 0012.
 */


import java.io.Serializable;

public class City implements Serializable {
    public static final String COLUMN_AREA_CODE = "areaCode";
    public static final String COLUMN_CAR_PREFIX = "carPrefix";
    public static final String COLUMN_ENAME = "ename";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROVINCE_ID = "provinceId";
    public static final String COLUMN_PROVINCE_NAME = "provinceName";
    public static final String COLUMN_UPDATE_TIME = "updateTime";
    private static final long serialVersionUID = -6064907343008071545L;

    private String areaCode;

    private String carPrefix;

    private String ename;

    private String id;

    private String name;

    private String provinceId;

    private String provinceName;

    private String updateTime;

    public String getAreaCode() {
        return this.areaCode;
    }

    public String getCarPrefix() {
        return this.carPrefix;
    }

    public String getEname() {
        return this.ename;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getProvinceId() {
        return this.provinceId;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setAreaCode(String paramString) {
        this.areaCode = paramString;
    }

    public void setCarPrefix(String paramString) {
        this.carPrefix = paramString;
    }

    public void setEname(String paramString) {
        this.ename = paramString;
    }

    public void setId(String paramString) {
        this.id = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setProvinceId(String paramString) {
        this.provinceId = paramString;
    }

    public void setProvinceName(String paramString) {
        this.provinceName = paramString;
    }

    public void setUpdateTime(String paramString) {
        this.updateTime = paramString;
    }

    public String toString() {
        return "City [id=" + this.id + ", updateTime=" + this.updateTime + ", name=" + this.name + ", provinceId=" + this.provinceId + ", provinceName=" + this.provinceName + ", ename=" + this.ename + ", carPrefix=" + this.carPrefix + ", areaCode=" + this.areaCode + "]";
    }
}
