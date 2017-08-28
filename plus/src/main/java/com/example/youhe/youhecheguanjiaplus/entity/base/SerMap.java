package com.example.youhe.youhecheguanjiaplus.entity.base;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/22 0022.
 * HashMap序列化
 */

public class SerMap implements Serializable {
    private static final long serialVersionUID = -6589151265283402584L;

    public HashMap<String,Object> map;
    public  SerMap(){

    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;

    }
}
