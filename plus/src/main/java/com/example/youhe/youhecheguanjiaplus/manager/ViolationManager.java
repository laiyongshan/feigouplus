package com.example.youhe.youhecheguanjiaplus.manager;

/**
 * Created by Administrator on 2016/9/13 0013.
 */


import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ViolationManager {
    private static final String TAG = "ViolationListManager";

    /**
     * 将JSON字符串解析成Violation
     * */
    public static List<Violation> json2Violations(String json){
        List<Violation> violations=new ArrayList<Violation>();
        try {
            JSONObject object=new JSONObject(json);
            String status=object.getString("status");
            if(status.equals("ok")){
            JSONArray array=object.getJSONArray("data");
            for(int i=0;i<array.length();i++) {
                Violation violation = new Violation();
                JSONObject aObject = array.getJSONObject(i);
                violation.setId(aObject.getString("id"));
                violation.setCarId(aObject.getString("carid"));
                violation.setSearchid(aObject.getString("searchid"));
                violation.setTime(aObject.getString("time"));
                violation.setLocation(aObject.getString("location"));
                violation.setReason(aObject.getString("reason"));
                violation.setCount(aObject.getInt("count"));
                violation.setStatus(aObject.getString("status"));
                violation.setDegree(aObject.getInt("degree"));
                violation.setCanprocess(aObject.getString("canprocess"));
                violation.setOrderstatus(aObject.getString("orderstatus"));
                violation.setCategory(aObject.getString("category"));
                violation.setLocationname(aObject.getString("locationname"));
                violation.setPoundage(aObject.getString("poundage"));
                violation.setPrice(aObject.getInt("price"));
                violation.setSecondaryuniquecode(aObject.getString("secondaryuniquecode"));
                violations.add(violation);
             }
            }
            return violations;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return violations;
    }
}
