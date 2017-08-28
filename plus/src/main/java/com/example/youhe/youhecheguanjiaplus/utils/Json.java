package com.example.youhe.youhecheguanjiaplus.utils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23 0023.
 * Json解析工具
 */
public abstract class Json {
    private static Json json;


    Json() {
    }

    public static Json get() {
        if (json == null) {
            json = new GsonImpl();
        }
        return json;
    }

    public abstract String toJson(Object src);

    public abstract <T> T toObject(String json, Class<T> claxx);

    public abstract <T> T toObject(byte[] bytes, Class<T> claxx);

    public abstract <T> List<T> toList(String json, Class<T> claxx);
}
