package com.example.youhe.youhecheguanjiaplus.bean;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

/**
 * 底部栏 实体类
 * Created by Administrator on 2017/7/17 0017.
 */

public class TabFragmentBean {

    private String name;
    private Drawable icon;
    private Class activityClass;
    private int id;

    private Bundle bundle;

    public TabFragmentBean(){

    }

    public TabFragmentBean(String name, Drawable icon, Class activityClass, int id) {
        this.name = name;
        this.icon = icon;
        this.activityClass = activityClass;
        this.id = id;
    }

    public TabFragmentBean(String name, Drawable icon, Class activityClass, int id, Bundle bundle) {
        this.name = name;
        this.icon = icon;
        this.activityClass = activityClass;
        this.id = id;
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TabFragmentBean{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                ", activityClass=" + activityClass +
                ", id=" + id +
                ", bundle=" + bundle +
                '}';
    }
}
