package com.example.youhe.youhecheguanjiaplus.entity.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23 0023.
 * 订单查询实体类
 */
public class OrderInquiryBackup implements Serializable {
    private static final long serialVersionUID = 4226755799531293257L;
    /**
     * code : 0
     * data : [{"carid":"20","clientid":"12","createtime":"1474557372","createtimestr":"2016-09-22 23:16:12","details":[{"activepoundge":"-1","archive":"4419077900186405","canprocess":"0","canprocessmsg":"因违法行为涉及扣分事项，须驾驶者本人前往当地处罚机关接受处理。","canusepackage":"","carid":"20","category":"","code":"1636","cooperpoundge":"0","count":"150","datasourceid":"9992","degree":"6","degreepoundage":"0","department":"东莞市公安局交通警察支队虎门大队","excutedepartment":"","excutelocation":"","id":"15","illegalentry":"","latefine":"0","location":"虎门镇连升南路路段","locationid":"4419","locationname":"广东东莞","orderid":"40","other":"6","poundage":"0","price":"1110","punishmentaccording":"","reason":"驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的","recordtype":"实时数据","searchid":"56","secondaryuniquecode":"876019645","status":"0","telephone":"","time":"2015-06-17 15:55:00","uniquecode":"166d7dd49d8c4d4ce32884f4106cb83e"}],"id":"40","ordercode":"zTO8aGVHA5N6CUdf","poscode":"D020000000010015","status":"1","totalprice":"1110","userid":"16"},{"carid":"20","clientid":"12","createtime":"1474557450","createtimestr":"2016-09-22 23:17:30","details":[{"activepoundge":"-1","archive":"4419077900186405","canprocess":"0","canprocessmsg":"因违法行为涉及扣分事项，须驾驶者本人前往当地处罚机关接受处理。","canusepackage":"","carid":"20","category":"","code":"1636","cooperpoundge":"0","count":"150","datasourceid":"9992","degree":"6","degreepoundage":"0","department":"东莞市公安局交通警察支队虎门大队","excutedepartment":"","excutelocation":"","id":"16","illegalentry":"","latefine":"0","location":"虎门镇连升南路路段","locationid":"4419","locationname":"广东东莞","orderid":"41","other":"6","poundage":"0","price":"1110","punishmentaccording":"","reason":"驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的","recordtype":"实时数据","searchid":"56","secondaryuniquecode":"876019645","status":"0","telephone":"","time":"2015-06-17 15:55:00","uniquecode":"166d7dd49d8c4d4ce32884f4106cb83e"}],"id":"41","ordercode":"EKp9On8m4qEqxljA","poscode":"D020000000010015","status":"1","totalprice":"1110","userid":"16"}]
     * msg : 处理成功
     * status : ok
     */

    private int code;
    private String msg;
    private String status;
    /**
     * carid : 20
     * clientid : 12
     * createtime : 1474557372
     * createtimestr : 2016-09-22 23:16:12
     * details : [{"activepoundge":"-1","archive":"4419077900186405","canprocess":"0","canprocessmsg":"因违法行为涉及扣分事项，须驾驶者本人前往当地处罚机关接受处理。","canusepackage":"","carid":"20","category":"","code":"1636","cooperpoundge":"0","count":"150","datasourceid":"9992","degree":"6","degreepoundage":"0","department":"东莞市公安局交通警察支队虎门大队","excutedepartment":"","excutelocation":"","id":"15","illegalentry":"","latefine":"0","location":"虎门镇连升南路路段","locationid":"4419","locationname":"广东东莞","orderid":"40","other":"6","poundage":"0","price":"1110","punishmentaccording":"","reason":"驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的","recordtype":"实时数据","searchid":"56","secondaryuniquecode":"876019645","status":"0","telephone":"","time":"2015-06-17 15:55:00","uniquecode":"166d7dd49d8c4d4ce32884f4106cb83e"}]
     * id : 40
     * ordercode : zTO8aGVHA5N6CUdf
     * poscode : D020000000010015
     * status : 1
     * totalprice : 1110
     * userid : 16
     */

    private List<DataBean> data=new ArrayList<DataBean>();

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public class TimeBean implements Serializable{
        private String timeName;//时间名称
        private String time;

        public String getTimeName() {
            return timeName;
        }

        public void setTimeName(String timeName) {
            this.timeName = timeName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class DataBean implements Serializable {
        private String carid;
        private String clientid;
        private String createtime;
        private String createtimestr;
        private String id;
        private String ordercode;
        private String poscode;
        private String status;
        private String totalprice;
        private String userid;
        private String proprefix;
        private String carnumber;
        private String type;
        private String detailurl;//详情id

        //by jian 2017-5-3
        private String remark;//备注
        private String paytime;//下单时间
        private String baojiatimestr;//报价时间
        private String finishtimestr;//完成时间
        private String failremark;//失败原因
        private ArrayList<TimeBean> timeList;
        private String iscommont;//是否允许支付  1是  -1否
        private String timeout_status;//订单是否过期

        public String getIscommont() {
            return iscommont;
        }

        public void setIscommont(String iscommont) {
            this.iscommont = iscommont;
        }

        public String getTimeout_status() {
            return timeout_status;
        }

        public void setTimeout_status(String timeout_status) {
            this.timeout_status = timeout_status;
        }

        public ArrayList<TimeBean> getTimeList() {
            return timeList;
        }

        public void setTimeList(ArrayList<TimeBean> timeList) {
            this.timeList = timeList;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getPaytime() {
            return paytime;
        }

        public void setPaytime(String paytime) {
            this.paytime = paytime;
        }

        public String getBaojiatimestr() {
            return baojiatimestr;
        }

        public void setBaojiatimestr(String baojiatimestr) {
            this.baojiatimestr = baojiatimestr;
        }

        public String getFinishtimestr() {
            return finishtimestr;
        }

        public void setFinishtimestr(String finishtimestr) {
            this.finishtimestr = finishtimestr;
        }

        public String getFailremark() {
            return failremark;
        }

        public void setFailremark(String failremark) {
            this.failremark = failremark;
        }
        //end by jian 2017-5-2

        /**
         * activepoundge : -1
         * archive : 4419077900186405
         * canprocess : 0
         * canprocessmsg : 因违法行为涉及扣分事项，须驾驶者本人前往当地处罚机关接受处理。
         * canusepackage :
         * carid : 20
         * category :
         * code : 1636
         * cooperpoundge : 0
         * count : 150
         * datasourceid : 9992
         * degree : 6
         * degreepoundage : 0
         * department : 东莞市公安局交通警察支队虎门大队
         * excutedepartment :
         * excutelocation :
         * id : 15
         * illegalentry :
         * latefine : 0
         * location : 虎门镇连升南路路段
         * locationid : 4419
         * locationname : 广东东莞
         * orderid : 40
         * other : 6
         * poundage : 0
         * price : 1110
         * punishmentaccording :
         * reason : 驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的
         * recordtype : 实时数据
         * searchid : 56
         * secondaryuniquecode : 876019645
         * status : 0
         * telephone :
         * time : 2015-06-17 15:55:00
         * uniquecode : 166d7dd49d8c4d4ce32884f4106cb83e
         * type :订单类型
         * url:其他订单类型 才会有这个参数
         */

        private List<DetailsBean> details;

        public String getDetailurl() {
            return detailurl;
        }

        public void setDetailurl(String detailurl) {
            this.detailurl = detailurl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setProprefix(String proprefix){
            this.proprefix = proprefix;
        }
        public String getProprefix(){

            return proprefix;
        }

        public String getCarid() {
            return carid;
        }

        public void setCarnumber(String carnumber){
            this.carnumber = carnumber;
        }
        public String getCarnumber(){

            return carnumber;
        }

        public void setCarid(String carid) {
            this.carid = carid;
        }

        public String getClientid() {
            return clientid;
        }

        public void setClientid(String clientid) {
            this.clientid = clientid;
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

        public String getPoscode() {
            return poscode;
        }

        public void setPoscode(String poscode) {
            this.poscode = poscode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotalprice() {
            return totalprice;
        }

        public void setTotalprice(String totalprice) {
            this.totalprice = totalprice;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public List<DetailsBean> getDetails() {
            return details;
        }

        public void setDetails(List<DetailsBean> details) {
            this.details = details;
        }

        public static class DetailsBean implements Serializable {
            private String activepoundge;
            private String archive;
            private String canprocess;
            private String canprocessmsg;
            private String canusepackage;
            private String carid;
            private String category;
            private String code;
            private String cooperpoundge;
            private String count;
            private String datasourceid;
            private String degree;
            private String degreepoundage;
            private String department;
            private String excutedepartment;
            private String excutelocation;
            private String id;
            private String illegalentry;
            private String latefine;
            private String location;
            private String locationid;
            private String locationname;
            private String orderid;
            private String other;
            private String poundage;
            private String price;
            private String punishmentaccording;
            private String reason;
            private String recordtype;
            private String searchid;
            private String secondaryuniquecode;
            private String status;
            private String telephone;
            private String time;
            private String uniquecode;
            private String latefee;




            public String getActivepoundge() {
                return activepoundge;
            }

            public void setActivepoundge(String activepoundge) {
                this.activepoundge = activepoundge;
            }

            public String getLatefee() {
                return latefee;
            }

            public void setLatefee(String latefee) {
                this.latefee = latefee;
            }

            public String getArchive() {
                return archive;
            }

            public void setArchive(String archive) {
                this.archive = archive;
            }

            public String getCanprocess() {
                return canprocess;
            }

            public void setCanprocess(String canprocess) {
                this.canprocess = canprocess;
            }

            public String getCanprocessmsg() {
                return canprocessmsg;
            }

            public void setCanprocessmsg(String canprocessmsg) {
                this.canprocessmsg = canprocessmsg;
            }

            public String getCanusepackage() {
                return canusepackage;
            }

            public void setCanusepackage(String canusepackage) {
                this.canusepackage = canusepackage;
            }

            public String getCarid() {
                return carid;
            }

            public void setCarid(String carid) {
                this.carid = carid;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCooperpoundge() {
                return cooperpoundge;
            }

            public void setCooperpoundge(String cooperpoundge) {
                this.cooperpoundge = cooperpoundge;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getDatasourceid() {
                return datasourceid;
            }

            public void setDatasourceid(String datasourceid) {
                this.datasourceid = datasourceid;
            }

            public String getDegree() {
                return degree;
            }


            public void setDegree(String degree) {
                this.degree = degree;
            }

            public String getDegreepoundage() {
                return degreepoundage;
            }

            public void setDegreepoundage(String degreepoundage) {
                this.degreepoundage = degreepoundage;
            }

            public String getDepartment() {
                return department;
            }

            public void setDepartment(String department) {
                this.department = department;
            }

            public String getExcutedepartment() {
                return excutedepartment;
            }

            public void setExcutedepartment(String excutedepartment) {
                this.excutedepartment = excutedepartment;
            }

            public String getExcutelocation() {
                return excutelocation;
            }

            public void setExcutelocation(String excutelocation) {
                this.excutelocation = excutelocation;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIllegalentry() {
                return illegalentry;
            }

            public void setIllegalentry(String illegalentry) {
                this.illegalentry = illegalentry;
            }

            public String getLatefine() {
                return latefine;
            }

            public void setLatefine(String latefine) {
                this.latefine = latefine;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getLocationid() {
                return locationid;
            }

            public void setLocationid(String locationid) {
                this.locationid = locationid;
            }

            public String getLocationname() {
                return locationname;
            }

            public void setLocationname(String locationname) {
                this.locationname = locationname;
            }

            public String getOrderid() {
                return orderid;
            }

            public void setOrderid(String orderid) {
                this.orderid = orderid;
            }

            public String getOther() {
                return other;
            }

            public void setOther(String other) {
                this.other = other;
            }

            public String getPoundage() {
                return poundage;
            }

            public void setPoundage(String poundage) {
                this.poundage = poundage;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getPunishmentaccording() {
                return punishmentaccording;
            }

            public void setPunishmentaccording(String punishmentaccording) {
                this.punishmentaccording = punishmentaccording;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
            }

            public String getRecordtype() {
                return recordtype;
            }

            public void setRecordtype(String recordtype) {
                this.recordtype = recordtype;
            }

            public String getSearchid() {
                return searchid;
            }

            public void setSearchid(String searchid) {
                this.searchid = searchid;
            }

            public String getSecondaryuniquecode() {
                return secondaryuniquecode;
            }

            public void setSecondaryuniquecode(String secondaryuniquecode) {
                this.secondaryuniquecode = secondaryuniquecode;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTelephone() {
                return telephone;
            }

            public void setTelephone(String telephone) {
                this.telephone = telephone;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUniquecode() {
                return uniquecode;
            }

            public void setUniquecode(String uniquecode) {
                this.uniquecode = uniquecode;
            }
        }
    }
}
