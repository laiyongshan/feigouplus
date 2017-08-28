package com.example.youhe.youhecheguanjiaplus.manager;

import com.example.youhe.youhecheguanjiaplus.entity.base.CarOpenCity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24 0024.
 */

public class CarOpenCitysMenager {
    public static final String TAG = "CarOpenCitysMenager";

    //开放本人本车城市json解释成集合类
    public static List<CarOpenCity> carOpenCityList=new ArrayList<CarOpenCity>();
    public static List<CarOpenCity> citysJson2list(String jsonStr) {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            String status=jsonObject.getString("status");
            if(status.equals("ok")) {
                JSONObject dataObj = jsonObject.getJSONObject("data");
                JSONArray array=dataObj.getJSONArray("citys");
                CarOpenCity carOpenCity;
                for (int i = 0; i < array.length(); i++) {
                    carOpenCity = new CarOpenCity();
                    if (array.getJSONObject(i).optString("prefix")!=null) {
                        carOpenCity.setPrefix(array.getJSONObject(i).optString("prefix"));
                        carOpenCityList.add(carOpenCity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carOpenCityList;
    }
}
