package com.example.youhe.youhecheguanjiaplus.utils;

import org.json.JSONObject;

/**
 *
 * Created by Administrator on 2017/7/17 0017.
 */

public interface OnVolleyInterface {

    void success(JSONObject dataObject,String resultStr);

    void failed(JSONObject resultObject,String code,String msg);

}
