package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class ApplyCrashItem {

    private float money;//金额

    private String pay_flowing;//流水号

    private String remark;//备注

    private String createtimestr;//申请时间

    private String finishtimestr;//完成时间

    private String status;//体现状态

    private String fee;//提现手续费

    private double amount_money;//到账金额

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFee() {
        return fee;
    }

    public void setAmount_money(double amount_money) {
        this.amount_money = amount_money;
    }

    public double getAmount_money() {
        return amount_money;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getPay_flowing() {
        return pay_flowing;
    }

    public void setPay_flowing(String pay_flowing) {
        this.pay_flowing = pay_flowing;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatetimestr() {
        return createtimestr;
    }

    public void setCreatetimestr(String createtimestr) {
        this.createtimestr = createtimestr;
    }

    public String getFinishtimestr() {
        return finishtimestr;
    }

    public void setFinishtimestr(String finishtimestr) {
        this.finishtimestr = finishtimestr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
