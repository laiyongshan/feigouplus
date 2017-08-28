package com.example.youhe.youhecheguanjiaplus.logic;

import com.android.volley.VolleyError;

import java.io.IOException;

/**
 * Created by Administrator on 2016/9/8 0008.
 * volleyInterface 是自定义回调接口
 */

public interface VolleyInterface {
    void ResponseResult(Object jsonObject) ;

    void ResponError(VolleyError volleyError);
}

