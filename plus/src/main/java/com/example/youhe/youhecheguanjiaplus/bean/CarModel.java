package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CarModel {

    public String id;
    public String name;
    public String logo;
    public String pinyi;
    public String fullname;

    public CarModel(String id,String name, String logo,String pinyi,String fullname) {
        super();
        this.id=id;
        this.name = name;
        this.logo=logo;
        this.pinyi = pinyi;
        this.fullname=fullname;
    }

    public CarModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
    }

    public String getPinyi() {
        return pinyi;
    }

    public void setPinyi(String pinyi) {
        this.pinyi = pinyi;
    }
}
