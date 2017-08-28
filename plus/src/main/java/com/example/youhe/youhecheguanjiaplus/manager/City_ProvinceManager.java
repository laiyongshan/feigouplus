package com.example.youhe.youhecheguanjiaplus.manager;

import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.entity.base.City;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19 0019.
 * 不同城市的最低查询并下单条件的管理类
 */

public class City_ProvinceManager {
    public static final String TAG = "City_ProvinceManager";
    public static List<City> cityList=new ArrayList<City>();
    public static List<Province> provinceList=new ArrayList<Province>();


    public static List<City> jsonToCityList(String json) throws JSONException {
        cityList.clear();
        JSONArray array=new JSONArray(json);
        JSONObject object;
        JSONObject cityObject;
        JSONArray citiesArray;
        for(int i=0;i<array.length();i++){
            object=array.getJSONObject(i);
            citiesArray=object.getJSONArray("Cities");
            for(int j=0;j<citiesArray.length();j++) {
                City city=new City();
                cityObject = citiesArray.getJSONObject(j);
                city.setCarCodeLen(cityObject.getInt("CarCodeLen") + "");
                city.setCarEngineLen(cityObject.getInt("CarEngineLen") + "");
                city.setCarNumberPrefix(cityObject.getString("CarNumberPrefix"));
                city.setCityName(cityObject.getString("CityName"));
                city.setName(cityObject.getString("Name"));
                city.setProxyEnable(cityObject.getInt("ProxyEnable") + "");
                city.setCityID(cityObject.getInt("CityID") + "");
                cityList.add(city);
            }
        }
        return cityList;
    }

    public static List<Province> jsonToProvinceList(String json) throws JSONException {
        provinceList.clear();
        JSONArray array=new JSONArray(json);
        JSONObject object;
        for(int i=0;i<array.length();i++){
            Province province=new Province();
            object=array.getJSONObject(i);
            province.setProvinceID(object.getInt("ProvinceID")+"");
            province.setProvinceName(object.getString("ProvinceName"));
            province.setProvincePrefix(object.getString("ProvincePrefix"));
            provinceList.add(province);

            Log.i("TAG",province.toString()+"\n");

        }
        return provinceList;
    }
}
