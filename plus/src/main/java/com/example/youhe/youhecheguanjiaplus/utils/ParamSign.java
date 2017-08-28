package com.example.youhe.youhecheguanjiaplus.utils;


import android.text.TextUtils;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * <p/>
 * 参数签名工具类
 */
public class ParamSign {

    public native static String stringFromJNI();

    public native static String getFromJNI(String bytes);//url 签名加密
//    public native static String getTestJNI(String key,String data);

    public native static String encryptDES(String data);//des 加密

    public static String   encryptDESDemo(String data){
        return encryptDES(data);
    }

    static {
        System.loadLibrary("native-lib");
    }
    public  native static String getUserPasswordFromJNI(String str);

//    public static String getText(){
//
//        return getTestJNI("01110A011010110101401210010110101401210221122112","323035356740676776762e2a2d362c363636353635303030");
//    }

    /**
     * 用户密码加密
     * @param str
     * @return
     */
    public static String getUserPassword(String str){
        String a="rldstwvhral33brqz9ypetwe";
        return UnionDes.MyEncryptData(UnionDes.byte2hex(a.getBytes()),str);
    }

    /**
     * url 签名加密
     * @param map
     * @return
     */
    public static String getSign(HashMap map) {
//        Class c = RequestParams.class;
        StringBuffer sb = new StringBuffer();
//        try {
//            Field field = RequestParams.class.getDeclaredField("urlParamsWithObjects");
//            field.setAccessible(true);
//            Object o = field.get(params);
//            ConcurrentHashMap map = (ConcurrentHashMap) o;
//
            TreeSet<String> set = new TreeSet<>(map.keySet());
            for (String key : set) {
                Object a = map.get(key);
                if (a == null)
                    continue;
//                String values = a.toString().substring(1, a.toString().length()
//                        - 1);
//
//                if (TextUtils.isEmpty(values))
//                    continue;
                sb.append(a.toString());
            }
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        LogUtils.d(stringFromJNI()+"......");
//        LogUtils.d(sb.toString()+"......");
        String a=getFromJNI(sb.toString());
        return a.toUpperCase();
    }

    /**
     * 数据传输加密
     * @param map
     * @return
     */
    public static HashMap netWorkEncrypt(HashMap map) {
//        Class c = RequestParams.class;
        StringBuffer sb = new StringBuffer();
        JSONObject jsonObject=new JSONObject();

        HashMap params=new HashMap();
        params.clear();
//        RequestParams resultParams=new RequestParams();
//
        try {
//            //获取urlParamsWithObjects
//            Field field = RequestParams.class.getDeclaredField("urlParamsWithObjects");
//            field.setAccessible(true);
//            Object o = field.get(params);
//            ConcurrentHashMap map = (ConcurrentHashMap) o;

            TreeSet<String> set = new TreeSet<String>(map.keySet());
            for (String key : set) {
                Object a = map.get(key);
                if (a == null)
                    continue;
//                String values = a.toString().substring(1, a.toString().length()
//                        - 1);

                if (TextUtils.isEmpty(a.toString()))
                    continue;
//                sb.append(values);
                jsonObject.put(key,a.toString());
            }
            String a="rldstwvhral33brqz9ypetwe";
//        return UnionDes.MyEncryptData(UnionDes.byte2hex(a.getBytes()),str);
//            LogUtils.d("json:"+jsonObject.toString());
            byte[] des = TripleDES.encrypt(jsonObject.toString().getBytes(),a.getBytes());

            params.put("data",TripleDES.byte2hex(des));
//            resultParams.add("data", TripleDES.byte2hex(des));


//            //获取urlParamsWithObjects
//            Field fieldUrlParams = RequestParams.class.getDeclaredField("urlParams");
//            fieldUrlParams.setAccessible(true);
//            Object oFieldUrlParams = fieldUrlParams.get(params);
//            ConcurrentHashMap mapFieldUrlParams = (ConcurrentHashMap) oFieldUrlParams;
//
//            TreeSet<String> setUrlParams = new TreeSet<>(mapFieldUrlParams.keySet());
//            for (String key : setUrlParams) {
//                String b = (String)mapFieldUrlParams.get(key);
//                if (b == null)
//                    continue;

//                params.put(key,b);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return params;
    }

}
