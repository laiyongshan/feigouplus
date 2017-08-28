package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * 车主卡 信息
 * Created by Administrator on 2017/7/5 0005.
 */

public class PlusBean  implements Serializable{
    private static final long serialVersionUID = 3500401901946681339L;

    public static final int STATE_UNAUTHORIZED=1;//未授权
    public static final int STATE_AUTHORIZED=2;//已授权
    public static final int STATE_ACTIVATE=3;//已激活
    public static final int STATE_EXPIRED=4;//已过期

    private int status =STATE_UNAUTHORIZED;//状态   1未授权2已授权3已激活4已过期
    private String status_name;//车主卡类型  2分销车主卡3普通车主卡
    private String card_number;//车主卡号
//    private String create_time_str;//创建时间
    private String expire_time;//过期时间
    private String price;//车主卡价格
    private int regist;//是否注册 0未注册1已注册
    private int client_type;//车主卡类型  2分销车主卡3普通车主卡

    private String content;//分享内容
    private String url;//分享url
    private String title;//分享url

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }



    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time_str) {
        this.expire_time = expire_time_str;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getRegist() {
        return regist;
    }

    public void setRegist(int regist) {
        this.regist = regist;
    }

    public int getClient_type() {
        return client_type;
    }

    public void setClient_type(int client_type) {
        this.client_type = client_type;
    }

    @Override
    public String toString() {
        return "PlusBean{" +
                "status=" + status +
                ", status_name='" + status_name + '\'' +
                ", card_number='" + card_number + '\'' +
                ", expire_time='" + expire_time + '\'' +
                ", price='" + price + '\'' +
                ", regist=" + regist +
                ", client_type=" + client_type +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
