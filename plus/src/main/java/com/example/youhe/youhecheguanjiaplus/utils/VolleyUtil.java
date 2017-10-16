package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Administrator on 2016/9/8 0008.
 * 封装Volley请求框架Get,POST的核心类
 */
public class VolleyUtil {
    private final static String TAG = "VolleyUtil";
    private static RequestQueue requestQueue = null;
    private static VolleyUtil volleyUtil;
    private Context context;
    private static HurlStack hurlStack = null;

    public static synchronized VolleyUtil getVolleyUtil(Context context) {

        if (volleyUtil == null) {
            volleyUtil = new VolleyUtil();
        }

        if (hurlStack == null)
            hurlStack = new HurlStack(null, initSSLSocketFactory(context));
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(AppContext.getContext(),hurlStack);

        HttpsURLConnection.setDefaultHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());

        return volleyUtil;
    }

    private static SSLCertificateSocketFactory initSSLSocketFactory(Context context) {
        //生成证书:Certificate
        CertificateFactory cf = null;
        SSLCertificateSocketFactory factory = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
//		       InputStream caInput = new ByteArrayInputStream(RFC.getBytes());                     // 通过证书生成的RFC格式数据字符串
            InputStream caInput = new BufferedInputStream(context.getResources().getAssets().open("plus.yeohe.com.crt"));       // 证书文件

            Certificate ca = null;

            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //初始化公钥:keyStore
            String keyType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyType);

            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            //初始化TrustManagerFactory
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory managerFactory = TrustManagerFactory.getInstance(algorithm);
            managerFactory.init(keyStore);


            //初始化sslContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, managerFactory.getTrustManagers(), null);
//            factory = (SSLCertificateSocketFactory) sslContext.getSocketFactory();
//            factory.setUseSessionTickets();

            factory=new SSLCertificateSocketFactory(0);
            factory.setTrustManagers(managerFactory.getTrustManagers());
//          Socket socket= factory.createSocket();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return factory;
    }

    /**
     * Post请求 url,HashMap,volleyInterface,Class
     * 以实体类形式返回，然后进行强转
     */
    public void SendVolleyPostBean(String url, HashMap<?, ?> hashMap, final VolleyInterface volleyInterface, final Class<?> aClass) {
        Log.i(TAG, "开始请求");

        JSONObject jsonObject = new JSONObject(hashMap);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                Object userBean = (Object) g.fromJson(jsonObject.toString(),aClass);
//                volleyInterface.ResponseResult(userBean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyInterface.ResponError(volleyError);
                Log.i(TAG, "请求错误");
            }
        });

        AddrequestQueue(jsonObjectRequest, true);
    }

    /**
     * JsonObjectRequest
     * GET请求 url,volleyInterface,Class
     * 以实体类形式返回，然后进行强转
     **/
    public void SendVolleyGetBean(String url, final VolleyInterface volleyInterface) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                Object userBean = (Object) g.fromJson(jsonObject.toString(),aClass);
//                volleyInterface.ResponseResult(userBean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyInterface.ResponError(volleyError);
                Log.i(TAG, "请求错误");
            }
        });
        AddrequestQueue(jsonObjectRequest, true);
    }

    /**
     * JsonObjectRequest
     * POST请求 url,volleyInterface,Class
     * 以JSON形式返回，然后进行强转 为JSONobject
     **/
    public void SendVolleyPostJsonobject(String url, final VolleyInterface volleyInterface, final Class<?> aClass) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                volleyInterface.ResponseResult(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyInterface.ResponError(volleyError);
                Log.i(TAG, "请求错误");
            }
        });
        AddrequestQueue(jsonObjectRequest, true);
    }

    /**
     * JsonObjectRequest
     * GET请求 url,volleyInterface,Class
     * 以JSON形式返回，然后进行强转 为JSONobject
     **/
    public void StringRequestGetVolley(String url, final VolleyInterface volleyInterface) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                volleyInterface.ResponseResult(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyInterface.ResponError(volleyError);
                Log.i(TAG, "请求错误");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return (Map<String, String>)EncryptUtil.encrypt(null);
            }
        };
        //加入队列 及设置 请求时间
        AddrequestQueue(stringRequest, true);
    }


    /**
     * 此方法是 Volley配置方法
     */
    public void AddrequestQueue(Request req, boolean issave) {
        // 设置超时时间
        req.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 2, 1.0f));

        // 是否开启缓存；
//        req.setShouldCache(issave);
        // 将请求加入队列

        requestQueue.add(req);

        // 开始发起请求
//        requestQueue.start();
    }

    //取消所有的请求任务
    public void cancelAllQueue(Context context) {
        requestQueue.cancelAll(context);
    }


    /**
     * StringRequest
     * Post请求
     * url,volleyInterface,hashMap
     * 以JSON形式返回
     */

    public void StringRequestPostVolley(final String url, final HashMap<?, ?> hashMap, final VolleyInterface volleyInterface) {
        if (hashMap!=null&&hashMap.containsKey("token")&& StringUtils.isEmpty(TokenSQLUtils.check())){
            VolleyError volleyError=new VolleyError("token值失效");
            volleyInterface.ResponseResult(volleyError);
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i(TAG, "onResponse: 成功了");
                volleyInterface.ResponseResult(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: 失败了" + volleyError.getMessage() + url);
                volleyInterface.ResponseResult(volleyError);
                Toast.makeText(AppContext.getContext(), "请求服务器端失败，请稍候重试", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap1=hashMap;
                if (hashMap1==null)
                    hashMap1=EncryptUtil.encrypt(null);
                return (Map<String, String>) hashMap1;
            }
        };
        //加入队列 及设置 请求时间
        AddrequestQueue(stringRequest, true);
    }

    public void postRequest(final Context mContext, final String url, final HashMap<?, ?> hashMap, final String defaultError, final OnVolleyInterface onVolleyInterface) {

        if (hashMap!=null&&hashMap.containsKey("token")&& StringUtils.isEmpty(TokenSQLUtils.check())){
            onVolleyInterface.failed(null, "", "token值失效");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i(TAG, "onResponse: 成功了");
                Log.i(TAG, s);
                if (!StringUtils.isEmpty(s)) {
                    try {
                        String result = EncryptUtil.decryptJson(s, mContext);
                        Log.i(TAG, result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("status")) {
                            if (jsonObject.getString("status").equals("ok")) {
                                if (jsonObject.has("data") && jsonObject.getString("data").length() > 5)
                                    onVolleyInterface.success(jsonObject.getJSONObject("data"), result);
                                else
                                    onVolleyInterface.success(null, result);
                            } else {
                                if (jsonObject.has("msg") && jsonObject.has("code")) {
                                    onVolleyInterface.failed(jsonObject, jsonObject.getString("code"), jsonObject.getString("msg"));
                                } else
                                    onVolleyInterface.failed(jsonObject, "", defaultError);
                            }
                        } else {
                            onVolleyInterface.failed(jsonObject, "", defaultError);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        onVolleyInterface.failed(null, "", defaultError);
                    }
                } else {
                    onVolleyInterface.failed(null, "", defaultError);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: 失败了" + volleyError.getMessage() + url);
                onVolleyInterface.failed(null, "", "网络请求失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap1=EncryptUtil.encrypt(hashMap);
                if (hashMap1==null)
                    hashMap1=EncryptUtil.encrypt(null);
                return (Map<String, String>) hashMap1;
//                return (Map<String, String>) EncryptUtil.encrypt(hashMap);
            }
        };
        //加入队列 及设置 请求时间
        AddrequestQueue(stringRequest, true);
    }


}
