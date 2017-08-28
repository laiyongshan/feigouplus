package com.example.youhe.youhecheguanjiaplus.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class CarCity {
    public List<Cities> carlist;

    public void setCarlist(List<Cities> carlist) {
        this.carlist = carlist;
    }

    public List<Cities> getCarlist() {
        return carlist;
    }

    public class Cities {
        public long CityID;//城市ID，除京，沪，津，渝的代码为2位之外，其他城市代码为4位
        public String CityName;//城市全名（省+市）
        public String Name;//城市名称 ,如“廣州”
        public String CarNumberPrefix;//城市所对应的车牌前缀，除京，沪，津，渝的代码为1位之外，其他城市代码为2位（汉字+字母）
        public int CarCodeLen;//查询所需车架号长度
        //其中：0 代表不需要   99 代表完整  具体数字代表 后多少位  如 6 代表需要车架号后6位  99 代表需要完整车架号（下同）
        public int CarEngineLen;//查询所需发动机号长度（同上)
        public int CarOwnerLen;//查询所需车辆所有人信息长度（同上）
        public int ProxyEnable;//是否开通代办。 1表示开通代办，0表示未开通代办

        public long getCityID() {
            return CityID;
        }

        public void setCityID(long cityID) {
            CityID = cityID;
        }

        public String getCityName() {
            return CityName;
        }

        public void setCityName(String cityName) {
            CityName = cityName;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getCarNumberPrefix() {
            return CarNumberPrefix;
        }

        public void setCarNumberPrefix(String carNumberPrefix) {
            CarNumberPrefix = carNumberPrefix;
        }

        public int getCarCodeLen() {
            return CarCodeLen;
        }

        public void setCarCodeLen(int carCodeLen) {
            CarCodeLen = carCodeLen;
        }

        public int getCarEngineLen() {
            return CarEngineLen;
        }

        public void setCarEngineLen(int carEngineLen) {
            CarEngineLen = carEngineLen;
        }

        public int getCarOwnerLen() {
            return CarOwnerLen;
        }

        public void setCarOwnerLen(int carOwnerLen) {
            CarOwnerLen = carOwnerLen;
        }

        public int getProxyEnable() {
            return ProxyEnable;
        }

        public void setProxyEnable(int proxyEnable) {
            ProxyEnable = proxyEnable;
        }
    }
}
