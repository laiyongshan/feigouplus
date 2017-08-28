package com.example.youhe.youhecheguanjiaplus.manager;

import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.entity.base.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class ProprefixManager {
    public static final String TAG = "ProprefixManager";
    public static List<Province> proprefixList=new ArrayList<Province>();

    public static List<Province> jsonToProprefixList(String json){
        proprefixList.clear();

        try {
            JSONObject obj = new JSONObject(json);
            JSONObject dataObj=obj.optJSONObject("data");
            JSONArray array=dataObj.optJSONArray("provinceList");

            for(int i=0;i<array.length();i++){
                Province province=new Province();
                province.setProvincePrefix(array.optJSONObject(i).optString("provinceprefix"));
                proprefixList.add(province);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("aa",proprefixList.toString());
        return proprefixList;
    }

}
