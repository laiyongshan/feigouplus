/**
 *
 */
package com.example.youhe.youhecheguanjiaplus.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;

public class HttpUtil {
    public static final String UTF_8 = "UTF-8";
    private final static int TIMEOUT_CONNECTION = 20000;
    private final static int TIMEOUT_SOCKET = 20000;
    private final static int RETRY_TIME = 3;
    private static String appCookie;
    private static String appUserAgent;
    private static HttpURLConnection conn;

    /*
     * get 请求网络数据
     * */
    public static String getHttpData(String urlpath, String enCodeType) {
        String html = "";
        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("contentType", enCodeType);
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                html = readStream(inputStream, enCodeType);
                return html;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "网络请求数据有错误");
        }
        return html;
    }

    /*
    *
    * 提交进货清单的网络连接方法
    * */
    public static String getDate(String urlStr, String key) {

        String line;
        StringBuffer sb = new StringBuffer();
        String responseBody;
        try {
            URLEncoder.encode(urlStr, "UTF-8");
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();
            Log.i("TAG", "网络已连接上！!!！！！");
//            String data = "key=" + URLEncoder.encode(key, "UTF-8");
            //获取输出流
            OutputStream os = conn.getOutputStream();
//            os.write(data.getBytes());
//            os.write(msg.getBytes());
            os.flush();
            Log.i("TAG", "请求已发送！");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.i("TAG", "获得了in...");
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    Log.i("TAG", "数据在读入");
                }
                Log.v("TAG", "数据已读入");
                System.out.println(sb.toString());
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放连接
        }
        responseBody = sb.toString();
        return responseBody;
    }

    /**
     * 向指定URL发送POST方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String postHttpData(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "/n" + line;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    public static String readStream(InputStream inputStream, String enCodeType) throws Exception {

        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, enCodeType));
            buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            in.close();
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }


    /**
     * 获取网络图片
     *
     * @param urlStr
     */
    public static Bitmap getNetBitmap(String urlStr) {
        // System.out.println("image_url==> "+url);
        Bitmap bitmap = null;
        int time = 0;
        do {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inStream);
                inStream.close();
                break;
            } catch (Exception e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                // 发生网络异常
                e.printStackTrace();
            }
        } while (time < RETRY_TIME);
        return bitmap;
    }


    //从cache下json文件夹获取json数据
    public static String LoadDataFromLocal(String url) {
        String dir = FileUtils.getCacheDir(AppContext.getContext(), "json");
        String key = url.replace("/", "_");
        File cacheFile = new File(dir, key);
        if (cacheFile.exists()) { // 本地有缓存
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                String cacheJson = reader.readLine();
                return cacheJson;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(reader!=null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    //将json数据保存到cache下json文件夹
    public static void saveJson2FileCache(String url, String json) {
        // 将json字符串放进本地缓存
        String dir = FileUtils.getCacheDir(AppContext.getContext(), "json");
        String key = url.replace("/", "_");
        File cacheFile = new File(dir, key);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));
            // 清除一些空白字符
            if (json.contains("\r\n")) {
                json = json.replace("\r\n", "");
            }
            if (json.contains(" ")) {
                json.replace(" ", "");
            }
            // 写入
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
