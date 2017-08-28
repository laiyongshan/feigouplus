package com.example.youhe.youhecheguanjiaplus.bean;

/**
 * Created by Administrator on 2017/6/6.
 */

public class Paytype {
    private int method;//支付方式
    private int is_balance_deductible;//是否可以使用余额支付  1:可以  2：否

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getIs_balance_deductible() {
        return is_balance_deductible;
    }

    public void setIs_balance_deductible(int is_balance_deductible) {
        this.is_balance_deductible = is_balance_deductible;
    }
}
