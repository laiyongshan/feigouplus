package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class OrderDeatilBean implements Serializable {


    /**
     * orderId : 3657
     * ordercode : 20170509096792
     * totalprice : 3100
     * orderStatus : 已完成
     * carnumber : 闽J13020
     * orderStatusList : [{"statusName":"提交订单","statusTime":"2017-05-09 10:07:07","finishStatus":1},{"statusName":"订单支付","statusTime":"","finishStatus":1},{"statusName":"订单完成","statusTime":"","finishStatus":-1}]
     * peccancyList : [{"time":"2017-04-23 22:55:23","location":"S26诸永高速温州方向163KM+50M","reason":"驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的","count":"100","degree":"6","code":"1636","latefine":"0","price":"3000"}]
     */

    private String orderId;
    private String ordercode;
    private String totalprice;
    private String status;//23待补款24补款处理中
    private String carnumber;

    private String allDegree;//总扣分
    private String allLatefine;//总滞纳金
    private String allCount;//总罚款
    private String allPrice;//



    /**
     * statusName : 提交订单
     * statusTime : 2017-05-09 10:07:07
     * finishStatus : 1
     */

    private List<OrderStatusListBean> orderStatusList;
    /**
     * time : 2017-04-23 22:55:23
     * location : S26诸永高速温州方向163KM+50M
     * reason : 驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的
     * count : 100
     * degree : 6
     * code : 1636
     * latefine : 0
     * price : 3000
     */


    private List<PeccancyListBean> peccancyList;//违章

    private List<MakeUpMoneyBean> makeUpMoneyList;//补款

    private String paid_money;//已支付金额  orderStatus为23时才有  int
    private String to_make_money;//待补款总金额  orderStatus为23时才有 double
    private String account_balance;//个人账户余额 orderStatus为23时才有 double

    private String has_made_money;//Status为24时才有

    public String getHas_made_money() {
        return has_made_money;
    }

    public void setHas_made_money(String has_made_money) {
        this.has_made_money = has_made_money;
    }

    public String getPaid_mony() {
        return paid_money;
    }

    public void setPaid_mony(String paid_mony) {
        this.paid_money = paid_mony;
    }

    public String getTo_make_money() {
        return to_make_money;
    }

    public void setTo_make_money(String to_make_money) {
        this.to_make_money = to_make_money;
    }

    public String getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(String account_balance) {
        this.account_balance = account_balance;
    }

    public List<MakeUpMoneyBean> getMakeUpMoneyList() {
        return makeUpMoneyList;
    }

    public void setMakeUpMoneyList(List<MakeUpMoneyBean> makeUpMoneyList) {
        this.makeUpMoneyList = makeUpMoneyList;
    }

    public String getAllDegree() {
        return allDegree;
    }

    public void setAllDegree(String allDegree) {
        this.allDegree = allDegree;
    }

    public String getAllLatefine() {
        return allLatefine;
    }

    public void setAllLatefine(String allLatefine) {
        this.allLatefine = allLatefine;
    }

    public String getAllCount() {
        return allCount;
    }

    public void setAllCount(String allCount) {
        this.allCount = allCount;
    }

    public String getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(String allPrice) {
        this.allPrice = allPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getOrderStatus() {
        return status;
    }

    public void setOrderStatus(String orderStatus) {
        this.status = orderStatus;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public List<OrderStatusListBean> getOrderStatusList() {
        return orderStatusList;
    }

    public void setOrderStatusList(List<OrderStatusListBean> orderStatusList) {
        this.orderStatusList = orderStatusList;
    }

    public List<PeccancyListBean> getPeccancyList() {
        return peccancyList;
    }

    public void setPeccancyList(List<PeccancyListBean> peccancyList) {
        this.peccancyList = peccancyList;
    }

    /**
     * 订单状态信息
     */
    public static class OrderStatusListBean implements Serializable{
        private static final long serialVersionUID = -8664426995688651267L;
        private String statusName;
        private String statusTime;
        private int finishStatus;

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public String getStatusTime() {
            return statusTime;
        }

        public void setStatusTime(String statusTime) {
            this.statusTime = statusTime;
        }

        public int getFinishStatus() {
            return finishStatus;
        }

        public void setFinishStatus(int finishStatus) {
            this.finishStatus = finishStatus;
        }
    }

    /**
     * 违章信息
     */
    public static class PeccancyListBean implements Serializable {
        private static final long serialVersionUID = 6244099778794265761L;
        private String time;
        private String location;
        private String reason;
        private String count;
        private String degree;
        private String code;
        private String latefine;
        private String price;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLatefine() {
            return latefine;
        }

        public void setLatefine(String latefine) {
            this.latefine = latefine;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }


    /**
     * 补款信息
     */

    public static class MakeUpMoneyBean implements Serializable{
        private static final long serialVersionUID = -1248249535540720796L;
        private String id;//补款id
        private String extra_money;//补款金额
        private String remark;//原因
        private String createtimestr;//补款创建时间
        private String status;//补款状态
        private String paytimestr;//补款支付时间

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getExtra_money() {
            return extra_money;
        }

        public void setExtra_money(String extra_money) {
            this.extra_money = extra_money;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaytimestr() {
            return paytimestr;
        }

        public void setPaytimestr(String paytimestr) {
            this.paytimestr = paytimestr;
        }
    }

    @Override
    public String toString() {
        return "OrderDeatilBean{" +
                "orderId='" + orderId + '\'' +
                ", ordercode='" + ordercode + '\'' +
                ", totalprice='" + totalprice + '\'' +
                ", orderStatus='" + status + '\'' +
                ", carnumber='" + carnumber + '\'' +
                ", orderStatusList=" + orderStatusList +
                ", makeUpMoneyList=" + makeUpMoneyList +
                ", peccancyList=" + peccancyList +
                '}';
    }
}
