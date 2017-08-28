package com.example.youhe.youhecheguanjiaplus.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 交易记录
 * Created by Administrator on 2017/7/11.
 */

public class TradingModel implements Serializable{

    private static final long serialVersionUID = -1342833789182807601L;
    private String yearMonth;//年月时间戳
//    private String yearMonthStr;

    private ArrayList<TradingSubModel> detailList;

    public TradingModel(String yearMonth, ArrayList<TradingSubModel> detailList) {
        this.yearMonth = yearMonth;
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "TradingModel{" +
                "yearMonth='" + yearMonth + '\'' +
                ", detailList=" + detailList +
                '}';
    }

    public TradingModel() {

    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public ArrayList<TradingSubModel> getDetailList() {
        return detailList;
    }

    public void setDetailList(ArrayList<TradingSubModel> detailList) {
        this.detailList = detailList;
    }

    public class TradingSubModel implements Serializable{

        private static final long serialVersionUID = 8695999678623334347L;

        private String id;//交易记录ID
        private String title;
        private String plus_minus="1";//（以客户的角度）加减： 1：加 2：减
        private String createtime;//时间戳
        private String createtimestr;
        private String money;//金额
        private String pay_flowing;//交易流水号
        private String order_type;//交易类型  0违章1年检2车主卡
        private String pay_action;//交易记录的操作   pay:支付 ；refund：退款；withdrawals：提现 ；untread：退还 ；make_up_money:补款 ；rake_back:返佣；
        private String listurl;//交易详情地址，可直接使用

        private String yearMonth;//年月时间戳

        @Override
        public String toString() {
            return "TradingSubModel{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", plus_minus='" + plus_minus + '\'' +
                    ", createtime='" + createtime + '\'' +
                    ", createtimestr='" + createtimestr + '\'' +
                    ", money='" + money + '\'' +
                    ", pay_flowing='" + pay_flowing + '\'' +
                    ", order_type='" + order_type + '\'' +
                    ", pay_action='" + pay_action + '\'' +
                    ", listurl='" + listurl + '\'' +
                    ", yearMonth='" + yearMonth + '\'' +
                    '}';
        }

        public String getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPlus_minus() {
            return plus_minus;
        }

        public void setPlus_minus(String plus_minus) {
            this.plus_minus = plus_minus;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getCreatetimestr() {
            return createtimestr;
        }

        public void setCreatetimestr(String createtimestr) {
            this.createtimestr = createtimestr;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getPay_flowing() {
            return pay_flowing;
        }

        public void setPay_flowing(String pay_flowing) {
            this.pay_flowing = pay_flowing;
        }

        public String getOrder_type() {
            return order_type;
        }

        public void setOrder_type(String order_type) {
            this.order_type = order_type;
        }

        public String getPay_action() {
            return pay_action;
        }

        public void setPay_action(String pay_action) {
            this.pay_action = pay_action;
        }

        public String getListurl() {
            return listurl;
        }

        public void setListurl(String listurl) {
            this.listurl = listurl;
        }
    }

}
