package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */

public class AnnualDetailBean implements Serializable {
    private static final long serialVersionUID = -186065003996287203L;

    private String id;
    private String ordercode;
    private String totalprice;
    private String name;
    private String proprefix;
    private String carnumber;
    private String carcode;
    private String cardrivenumber;
    private String checkyear_day;
    private String server_address;
    private String status;
    private String statusName;
    private int timeout_status;//过期状态

    private String address;//邮寄地址

    private String serverexpress_name;//回寄快递公司名称
    private String clientexpress_name;//寄出快递公司名称
    private String clientexpress;//寄出快递单号
    private String serverexpress;//回寄快递单号

    private List<OrderDeatilBean.OrderStatusListBean> orderStatusList;

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setTimeout_status(int timeout_status) {
        this.timeout_status = timeout_status;
    }

    public int getTimeout_status() {
        return timeout_status;
    }

    public String getProprefix() {
        return proprefix;
    }

    public void setProprefix(String proprefix) {
        this.proprefix = proprefix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode;
    }

    public String getCardrivenumber() {
        return cardrivenumber;
    }

    public void setCardrivenumber(String cardrivenumber) {
        this.cardrivenumber = cardrivenumber;
    }

    public String getCheckyear_day() {
        return checkyear_day;
    }

    public void setCheckyear_day(String checkyear_day) {
        this.checkyear_day = checkyear_day;
    }

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<OrderDeatilBean.OrderStatusListBean> getOrderStatusList() {
        return orderStatusList;
    }

    public void setOrderStatusList(List<OrderDeatilBean.OrderStatusListBean> orderStatusList) {
        this.orderStatusList = orderStatusList;
    }

    public void setClientexpress_name(String clientexpress_name) {
        this.clientexpress_name = clientexpress_name;
    }

    public String getClientexpress_name() {
        return clientexpress_name;
    }

    public void setServerexpress_name(String serverexpress_name) {
        this.serverexpress_name = serverexpress_name;
    }

    public String getServerexpress_name() {
        return serverexpress_name;
    }

    public String getClientexpress() {
        return clientexpress;
    }

    public void setClientexpress(String clientexpress) {
        this.clientexpress = clientexpress;
    }

    public String getServerexpress() {
        return serverexpress;
    }

    public void setServerexpress(String serverexpress) {
        this.serverexpress = serverexpress;
    }
}
