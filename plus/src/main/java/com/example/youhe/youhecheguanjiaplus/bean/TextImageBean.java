package com.example.youhe.youhecheguanjiaplus.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by Administrator on 2017/2/27 0027.
 */

public class TextImageBean  implements Parcelable{

    public TextImageBean(){

    }

    public TextImageBean(String name,Drawable drawable){
        this.name=name;
        this.drawable=drawable;
    }

    public TextImageBean(String name, Drawable drawable, int badgeCount) {
        this.name = name;
        this.drawable = drawable;
        this.badgeCount = badgeCount;
    }

    private String name;
    private Drawable drawable;
    private int badgeCount=-1;//红点数量

    protected TextImageBean(Parcel in) {
        name = in.readString();
        badgeCount = in.readInt();
    }

    public static final Creator<TextImageBean> CREATOR = new Creator<TextImageBean>() {
        @Override
        public TextImageBean createFromParcel(Parcel in) {
            return new TextImageBean(in);
        }

        @Override
        public TextImageBean[] newArray(int size) {
            return new TextImageBean[size];
        }
    };

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(badgeCount);
    }
}
