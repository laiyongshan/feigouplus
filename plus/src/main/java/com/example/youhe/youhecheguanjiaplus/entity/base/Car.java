package com.example.youhe.youhecheguanjiaplus.entity.base;

/**
 * Created by Administrator on 2016/9/12 0012.
 */

import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 車輛信息實體類
 * */
public class Car implements Serializable {
    public static final String COLUMN_CARCODE = "car_code";//车身架号
    public static final String COLUMN_CARID = "car_id";//车辆id
    public static final String COLUMN_CARNUMBER = "car_number";//车牌号码
    public static final String COLUMN_CARTYPE = "car_type";
    public static final String COLUMN_CREATE_TIME = "create_time";//添加时间
    public static final String COLUMN_DRIVING_LICENSE_ID = "driving_license_id";//驾驶证id
    public static final String COLUMN_ENGINENUMBER = "engine_number";//发动机号码
    public static final String COLUMN_IS_CAR_CORRECT = "is_car_correct";//车辆是否认证
    private static final long serialVersionUID = -6657651949558839899L;

    private String carId;

    private String cartype;//车辆类型
    private String cartypename;//车辆类型 名称

    private String carbrand;//车品牌

    private String carname;//系列名称

    private String carcode;//车身架号

    private String carnumber;//车牌号码

    private String createTime;

    private String drivingLicenseId;

    private String enginenumber;//发动机号

    private String isCarCorrect;//车辆是否认证

    private String remark;//备注

    private String proprefix;//车牌前缀

    private String carownerlen;//驾驶人姓名

    private String ownercardlen;//车主身份证号

    private String jashizhenghaoLen;//办理人驾驶证号

    private String xingshizhenghaoLen;//行驶证档案编号

    private String cheliangzhengshulen;//车辆登记证书号

    private String danganbianhaolen;//驾驶证档案编号

    private String carownerphonelen;//车主联系电话

    private String tiaoxingmalen;//驾驶证条形码

    private String filephonelen;//车主车管所登记电话

    private String majorviolation;//驾驶证正页复印件图片的URL

    private String majorsecondviolation;//驾驶证副页复印件图片的URL

    private String drivinglicense;//行驶证正页复印件图片的URL

    private String drivingsecondlicense;//行驶证副页复印件图片的URL

    public String getCartypename() {
        return cartypename;
    }

    public void setCartypename(String cartypename) {
        this.cartypename = cartypename;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCarbrand(String carbrand) {
        this.carbrand = carbrand;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public void setCarname(String carname) {
        this.carname = carname;
    }

    public String getCarname() {
        return carname;
    }

    public void setMajorviolation(String majorviolation) {
        this.majorviolation = majorviolation;
    }

    public String getMajorviolation() {
        return majorviolation;
    }

    public void setMajorsecondviolation(String majorsecondviolation) {
        this.majorsecondviolation = majorsecondviolation;
    }

    public String getMajorsecondviolation() {
        return majorsecondviolation;
    }

    public void setDrivinglicense(String drivinglicense) {
        this.drivinglicense = drivinglicense;
    }

    public String getDrivinglicense() {
        return drivinglicense;
    }

    public void setDrivingsecondlicense(String drivingsecondlicense) {
        this.drivingsecondlicense = drivingsecondlicense;
    }

    public String getDrivingsecondlicense() {
        return drivingsecondlicense;
    }

    public void setIsCarCorrect(String isCarCorrect) {
        this.isCarCorrect = isCarCorrect;
    }

    public String getCarownerlen() {
        return carownerlen;
    }

    public void setCarownerlen(String carownerlen) {
        this.carownerlen = carownerlen;
    }

    public String getOwnercardlen() {
        return ownercardlen;
    }

    public void setOwnercardlen(String ownercardlen) {
        this.ownercardlen = ownercardlen;
    }

    public String getJashizhenghaoLen() {
        return jashizhenghaoLen;
    }

    public void setJashizhenghaoLen(String jashizhenghaoLen) {
        this.jashizhenghaoLen = jashizhenghaoLen;
    }

    public String getXingshizhenghaoLen() {
        return xingshizhenghaoLen;
    }

    public void setXingshizhenghaoLen(String xingshizhenghaoLen) {
        this.xingshizhenghaoLen = xingshizhenghaoLen;
    }

    public String getCheliangzhengshulen() {
        return cheliangzhengshulen;
    }

    public void setCheliangzhengshulen(String cheliangzhengshulen) {
        this.cheliangzhengshulen = cheliangzhengshulen;
    }

    public String getDanganbianhaolen() {
        return danganbianhaolen;
    }

    public void setDanganbianhaolen(String danganbianhaolen) {
        this.danganbianhaolen = danganbianhaolen;
    }

    public String getCarownerphonelen() {
        return carownerphonelen;
    }

    public void setCarownerphonelen(String carownerphonelen) {
        this.carownerphonelen = carownerphonelen;
    }

    public String getTiaoxingmalen() {
        return tiaoxingmalen;
    }

    public void setTiaoxingmalen(String tiaoxingmalen) {
        this.tiaoxingmalen = tiaoxingmalen;
    }

    public String getFilephonelen() {
        return filephonelen;
    }

    public void setFilephonelen(String filephonelen) {
        this.filephonelen = filephonelen;
    }

    private List<Violation> violations = new ArrayList();//车辆违章列表

    public String getCarId() {
        return this.carId;
    }


    public String getCarcode() {
        return this.carcode;
    }

    public String getCarnumber() {
        return this.carnumber;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getDrivingLicenseId() {
        return this.drivingLicenseId;
    }

    public String getEnginenumber() {
        return this.enginenumber;
    }

    public void setViolations(List<Violation> paramList) {
        this.violations = paramList;
    }

    public List<Violation> getViolations() {
        return this.violations;
    }

    public String getIsCarCorrect() {
        return this.isCarCorrect;
    }

    public void setCarCorrect(String isCarCorrect) {
        this.isCarCorrect = isCarCorrect;
    }

    public void setCarId(String paramString) {
        this.carId = paramString;
    }


    public void setCarcode(String paramString) {
        this.carcode = paramString;
    }

    public void setCarnumber(String paramString) {
        this.carnumber = paramString;
    }

    public void setCreateTime(String paramString) {
        this.createTime = paramString;
    }

    public void setDrivingLicenseId(String paramString) {
        this.drivingLicenseId = paramString;
    }

    public void setEnginenumber(String paramString) {
        this.enginenumber = paramString;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProprefix() {
        return proprefix;
    }

    public void setProprefix(String proprefix) {
        this.proprefix = proprefix;
    }

    public String toString() {
        return "Car{carId='" + this.carId + '\'' + ", carnumber='" + this.carnumber + '\'' + ", carcode='" + this.carcode + '\'' + ", enginenumber='" + this.enginenumber + '\'' + ", drivingLicenseId='" + this.drivingLicenseId + '\'' + ", createTime='" + this.createTime + '\'' + ", isCarCorrect=" + this.isCarCorrect + ", carType='" + this.cartype + '\'' + ", remark='" + this.remark + '\'' + ", drivingLicense=" + ", violations=" + this.violations + '}'
                +"carownerlen="+this.carownerlen;
    }
}
