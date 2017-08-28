package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class BankCard {

    private String bank_name;
    private String bank_code;
    private String bank_address;
    private String createtimestr;
    private String bankid;

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public String getBankid() {
        return bankid;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_address() {
        return bank_address;
    }

    public void setBank_address(String bank_address) {
        this.bank_address = bank_address;
    }

    public String getCreatetimestr() {
        return createtimestr;
    }

    public void setCreatetimestr(String createtimestr) {
        this.createtimestr = createtimestr;
    }
}
