package com.example.youhe.youhecheguanjiaplus.entity.base;

/**
 * Created by Administrator on 2016/9/12 0012.
 */


import com.example.youhe.youhecheguanjiaplus.bean.Annual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Province implements Serializable {
    public static final String COLUMN_PROVINCEID = "province_id";
    public static final String COLUMN_PROVINCENAME = "province_name";
    public static final String COLUMN_PROVINCEPREFIX = "province_prefix";
    private static final long serialVersionUID = -1436898785973108723L;

    private Collection<City> Cities;

    private String ProvinceID;

    private String ProvinceName;

    private String provincePrefix;

    private List<Annual> annualList;

    public Province() {
    }

    public Province(String paramString1, String paramString2, String paramString3, ArrayList<City> paramArrayList) {
        this.ProvinceID = paramString1;
        this.ProvinceName = paramString2;
        this.provincePrefix = paramString3;
        this.Cities = paramArrayList;
    }

    public void setAnnualList(List<Annual> annualList) {
        this.annualList = annualList;
    }

    public List<Annual> getAnnualList() {
        return annualList;
    }

    public Collection<City> getCities() {
        return this.Cities;
    }

    public String getProvinceID() {
        return this.ProvinceID;
    }

    public String getProvinceName() {
        return this.ProvinceName;
    }

    public String getProvincePrefix() {
        return this.provincePrefix;
    }

    public void setCities(List<City> paramList) {
        this.Cities = paramList;
    }

    public void setProvinceID(String paramString) {
        this.ProvinceID = paramString;
    }

    public void setProvinceName(String paramString) {
        this.ProvinceName = paramString;
    }

    public void setProvincePrefix(String paramString) {
        this.provincePrefix = paramString;
    }

    public String toString() {
        return "Province [ProvinceID=" + this.ProvinceID + "\nProvinceName=" + this.ProvinceName + "\nProvincePrefix=" + this.provincePrefix + "\nCities=" + this.Cities + "]";
    }
}
