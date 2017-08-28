package com.example.youhe.youhecheguanjiaplus.entity.base;

/**
 * Created by Administrator on 2016/9/12 0012.
 */


import java.io.Serializable;

public class City implements Serializable {
    public static final String COLUMN_CARCODELEN = "car_code_len";
    public static final String COLUMN_CARENGINELEN = "car_engine_len";
    public static final String COLUMN_CARNUMBERPREFIX = "car_number_prefix";
    public static final String COLUMN_CAROWENERLEN = "car_owner_len";
    public static final String COLUMN_CHECKPERION = "check_perion";
    public static final String COLUMN_CITYID = "city_id";
    public static final String COLUMN_CITYNAME = "city_name";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROXYENABLE = "proxy_enable";
    public static final String COLUMN_SHORTNAME = "short_name";
    public static final String PROVINCE_ID_FIELD_NAME = "province_id";
    private static final long serialVersionUID = 7183349525145310327L;

    private String CarCodeLen;//需要输入车身架号长度

    private String CarEngineLen;//需要输入发动机号长度

    private String CarNumberPrefix;//车牌前缀

    private String CarOwnerLen;

    private String CityID;//城市ID

    private String CityName;//城市全称，如广东广州

    private String Name;//城市名称，如广州

    private String ProxyEnable;//可否代办

    private String checkperion;

    private int id;

    private Province province;

    private String shortname;

    public City() {
    }

    public City(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8) {
        this.CityID = paramString1;
        this.CityName = paramString2;
        this.Name = paramString3;
        this.CarNumberPrefix = paramString4;
        this.CarCodeLen = paramString5;
        this.CarEngineLen = paramString6;
        this.CarOwnerLen = paramString7;
        this.ProxyEnable = paramString8;
    }

    public String getCarCodeLen() {
        return this.CarCodeLen;
    }

    public String getCarEngineLen() {
        return this.CarEngineLen;
    }

    public String getCarNumberPrefix() {
        return this.CarNumberPrefix;
    }

    public String getCarOwnerLen() {
        return this.CarOwnerLen;
    }

    public String getCheckperion() {
        return this.checkperion;
    }

    public String getCityID() {
        return this.CityID;
    }

    public String getCityName() {
        return this.CityName;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.Name;
    }

    public Province getProvince() {
        return this.province;
    }

    public String getProxyEnable() {
        return this.ProxyEnable;
    }

    public String getShortname() {
        return this.shortname;
    }

    public void setCarCodeLen(String paramString) {
        this.CarCodeLen = paramString;
    }

    public void setCarEngineLen(String paramString) {
        this.CarEngineLen = paramString;
    }

    public void setCarNumberPrefix(String paramString) {
        this.CarNumberPrefix = paramString;
    }

    public void setCarOwnerLen(String paramString) {
        this.CarOwnerLen = paramString;
    }

    public void setCheckperion(String paramString) {
        this.checkperion = paramString;
    }

    public void setCityID(String paramString) {
        this.CityID = paramString;
    }

    public void setCityName(String paramString) {
        this.CityName = paramString;
    }

    public void setId(int paramInt) {
        this.id = paramInt;
    }

    public void setName(String paramString) {
        this.Name = paramString;
    }

    public void setProvince(Province paramProvince) {
        this.province = paramProvince;
    }

    public void setProxyEnable(String paramString) {
        this.ProxyEnable = paramString;
    }

    public void setShortname(String paramString) {
        this.shortname = paramString;
    }

    public String toString() {
        return "City [CityID=" + this.CityID + "\nCityName=" + this.CityName + "\nName=" + this.Name + "\nCarNumberPrefix=" + this.CarNumberPrefix + "\nCarCodeLen=" + this.CarCodeLen + "\nCarEngineLen=" + this.CarEngineLen + "\nCarOwnerLen=" + this.CarOwnerLen + "\nProxyEnable=" + this.ProxyEnable + "]";
    }
}
