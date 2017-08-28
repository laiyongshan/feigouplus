package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/7 0007.
 */

public class P92PayBen implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * status : ok
     * code : 0
     * msg : 处理成功
     * data : {"merchant_name":"广州友禾信息科技有限公司","ordercode":"20161207282745","merchant_number":"302440148166004","max_flowing_no":"000492","pay_time":"2016-12-07 18:07:27","merchant_type":"车务办理","pay_money":2,"car_type":"小型汽车","car_number":"粤SY093L","card_no":"623058*********5061","res_code":"00","res_msg":"交易成功"}
     * time : 2016-12-07 18:07:27
     */

    private String status;
    private int code;
    private String msg;
    /**
     * merchant_name : 广州友禾信息科技有限公司
     * ordercode : 20161207282745
     * merchant_number : 302440148166004
     * max_flowing_no : 000492
     * pay_time : 2016-12-07 18:07:27
     * merchant_type : 车务办理
     * pay_money : 2
     * car_type : 小型汽车
     * car_number : 粤SY093L
     * card_no : 623058*********5061
     * res_code : 00
     * res_msg : 交易成功
     */

    private DataBean data;
    private String time;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class DataBean implements Serializable {
        private String merchant_name;
        private String ordercode;
        private String merchant_number;
        private String max_flowing_no;
        private String pay_time;
        private String merchant_type;
        private int pay_money;
        private String car_type;
        private String car_number;
        private String card_no;
        private String res_code;
        private String res_msg;

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getOrdercode() {
            return ordercode;
        }

        public void setOrdercode(String ordercode) {
            this.ordercode = ordercode;
        }

        public String getMerchant_number() {
            return merchant_number;
        }

        public void setMerchant_number(String merchant_number) {
            this.merchant_number = merchant_number;
        }

        public String getMax_flowing_no() {
            return max_flowing_no;
        }

        public void setMax_flowing_no(String max_flowing_no) {
            this.max_flowing_no = max_flowing_no;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getMerchant_type() {
            return merchant_type;
        }

        public void setMerchant_type(String merchant_type) {
            this.merchant_type = merchant_type;
        }

        public int getPay_money() {
            return pay_money;
        }

        public void setPay_money(int pay_money) {
            this.pay_money = pay_money;
        }

        public String getCar_type() {
            return car_type;
        }

        public void setCar_type(String car_type) {
            this.car_type = car_type;
        }

        public String getCar_number() {
            return car_number;
        }

        public void setCar_number(String car_number) {
            this.car_number = car_number;
        }

        public String getCard_no() {
            return card_no;
        }

        public void setCard_no(String card_no) {
            this.card_no = card_no;
        }

        public String getRes_code() {
            return res_code;
        }

        public void setRes_code(String res_code) {
            this.res_code = res_code;
        }

        public String getRes_msg() {
            return res_msg;
        }

        public void setRes_msg(String res_msg) {
            this.res_msg = res_msg;
        }
    }
}
