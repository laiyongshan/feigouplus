package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class IllegalCode {
    public String scop;//扣分数
    public String illegalCode;//违章代码
    public String fakuan;//罚款
    public String illegalDetail;//违章代码详情
    public String punishment;//惩罚

    public String getPunishment() {
        return punishment;
    }

    public void setPunishment(String punishment) {
        this.punishment = punishment;
    }

    public String getScop() {
        return scop;
    }

    public void setScop(String scop) {
        this.scop = scop;
    }

    public String getIllegalCode() {
        return illegalCode;
    }

    public void setIllegalCode(String illegalCode) {
        this.illegalCode = illegalCode;
    }

    public String getFakuan() {
        return fakuan;
    }

    public void setFakuan(String fakuan) {
        this.fakuan = fakuan;
    }

    public String getIllegalDetail() {
        return illegalDetail;
    }

    public void setIllegalDetail(String illegalDetail) {
        this.illegalDetail = illegalDetail;
    }
}
