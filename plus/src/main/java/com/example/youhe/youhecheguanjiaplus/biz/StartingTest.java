package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.VersionSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.widget.DownloadTheAPP;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/8 0008.
 * 开机启动APP检测更新
 */

public class StartingTest {


    private TokenSQLUtils tsl;
    private VolleyUtil volleyUtil;
    private HashMap<String, String> phonePams;
    private VersionSQLUtils versionSQLUtils;
    private Activity context;

    public StartingTest(Activity context) {
        this.context = context;

    }

    public void ins() {

        volleyUtil = VolleyUtil.getVolleyUtil(context);//上网请求
//        tsl = new TokenSQLUtils(context);//在数据库中拿到Token值
        phonePams = new HashMap<>();


        contrast();//网上请求得到最新版本号
    }

    /**
     * 网上请求得到最新版本号
     */
    public void contrast() {
//        String token = tsl.check();
//        phonePams.put("", token);
        phonePams.put("type", CommentSetting.TYPE);
        volleyUtil.StringRequestPostVolley(URLs.DETECTION_OF_UPDATE, phonePams, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                jsonData(jsonObject.toString());
                Log.i("WU", "app检测更新" + jsonObject.toString());
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(context, "网络连接失败,无法发送请求");
            }
        });
    }

    private String isforceS;
    private String urlS;
    private String idS;

    /**
     * 得到isforceS
     *
     * @return
     */
    public String getIsforce() {

        return isforceS;
    }

    /**
     * urlS
     *
     * @return
     */
    public String getUrlS() {

        return urlS;
    }

    /**
     * idS
     *
     * @return
     */
    public String getIdS() {

        return idS;
    }

    /**
     * 解析JSON
     *
     * @param json
     */
    public void jsonData(String json) {

        try {
            JSONObject jsonobject = new JSONObject(json);
            String status = jsonobject.getString("status");

            if (status.equals("ok")) {
                JSONObject data = jsonobject.getJSONObject("data");
                String id = data.getString("id");
                String version = data.getString("version");//最新版本号
                String url = data.getString("url");
                String isforce = data.getString("isforce");//强制更新标识
                isforceS = isforce;
                urlS = url;
                idS = id;
            }
            Misidentification.misidentification1((Activity) context, status, jsonobject);//错误判断

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 低版本与最高版对比后
     * 下载
     *
     * @param id
     * @param version
     * @param url
     */

    private MAsyncTask tas;

    public void comparedToDownload(String isforce, String url) {
        versionSQLUtils = new VersionSQLUtils(context);
        downloadDialog = new ProgressDialog(context);
        downloadDialog.setCancelable(false);
        downloadDialog.setTitle("下载中请等待");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setButton2("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                boolean flag = tas.cancel(true);
                if (flag) {//如果不更新就强行关闭程序
                    ToastUtil.getLongToastByString(context, "取消成功");
                    android.os.Process.killProcess(android.os.Process.myPid());//关闭程序
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());//关闭程序
                    ToastUtil.getLongToastByString(context, "取消失败");
                }
            }
        });
        //1是强制更新
        if (isforce.equals("-1")) {

        } else if (isforce.equals("1")) {
            DetectionOfUpdate2 detectionOfUpdate = new DetectionOfUpdate2(context);//检测更新
            detectionOfUpdate.in();
        }
    }

    private ProgressDialog downloadDialog;

    public class MAsyncTask extends AsyncTask<String, Void, File> {
        private String url;

        public MAsyncTask(String url) {
            this.url = url;

        }

        @Override
        protected File doInBackground(String... strings) {
            File file = DownloadTheAPP.getFileFromServer(url, downloadDialog);
            return file;
        }


        @Override
        protected void onPostExecute(File file) {

            if (file != null) {
                downloadDialog.hide();
                //把下载的APP安装
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());//关闭程序
                ToastUtil.getLongToastByString(context, "下载完成");
            }
            super.onPostExecute(file);


        }


    }

    /**
     * 得到本地版本号
     *
     * @param context
     * @return
     */
    public int getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi.versionCode;
    }



    /**
     * 网上请求判断新版本
     */
    public void deDao(final RelativeLayout imageView, final TextView textView) {
//        String token = tsl.check();
//        Log.i("WU", token);
        volleyUtil = VolleyUtil.getVolleyUtil(context);//上网请求
//        tsl = new TokenSQLUtils(context);//在数据库中拿到Token值
        phonePams = new HashMap<>();
//        phonePams.put("token", token);
        volleyUtil.StringRequestPostVolley(URLs.DETECTION_OF_UPDATE, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                jsonss(EncryptUtil.decryptJson(jsonObject.toString(),context), imageView, textView);
                Log.i("TAG","判断新版本:"+EncryptUtil.decryptJson(jsonObject.toString(),context));
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(context, "网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 解析JSON
     *
     * @param json
     */
    public void jsonss(String json, RelativeLayout imageView, TextView textView) {

        try {
            JSONObject jsonobject = new JSONObject(json);
            String status = jsonobject.getString("status");

            if (status.equals("ok")) {
                JSONObject data = jsonobject.getJSONObject("data");
                String versionid = data.getString("versionid");
                String version = data.getString("version");

                int versionName = getPackageInfo(context);//得到app版本号
                int ids = Integer.parseInt(versionid);//网上ID
                //得到版本名
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                        PackageManager.GET_CONFIGURATIONS);
                textView.setText(version);

                if (versionName < ids) {

                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);

                } else if (versionName >= ids) {

                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
            Misidentification.misidentification1((Activity) context, status, jsonobject);//错误判断

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
