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
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.VersionSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.widget.DownloadTheAPP;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/8 0008.
 * /检测更新逻辑类
 */

public class DetectionOfUpdate {
    private TokenSQLUtils tsl;
    private VolleyUtil volleyUtil;
    private HashMap<String, String> phonePams;
    private VersionSQLUtils versionSQLUtils;
    private Activity activity;

    public DetectionOfUpdate(Activity activity) {
        this.activity = activity;

    }

    public void in() {

        volleyUtil = VolleyUtil.getVolleyUtil(activity);//上网请求
//        tsl = new TokenSQLUtils(activity);//在数据库中拿到Token值
        phonePams = new HashMap<>();

        versionSQLUtils = new VersionSQLUtils(activity);
        contrast();//网上请求得到最新版本号
    }

    /**
     * 网上请求得到最新版本号
     */
    public void contrast() {
//        String token = tsl.check();
//        phonePams.put("token", token);
        phonePams.put("type", CommentSetting.TYPE);
        volleyUtil.StringRequestPostVolley(URLs.DETECTION_OF_UPDATE, phonePams, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                jsonData(jsonObject.toString());
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
            }
        });
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
                String version = data.getString("versionid");//最新版本号
                String url = data.getString("url");
                comparedToDownload(version, url);//下载

            }
            Misidentification.misidentification1(activity, status, jsonobject);//错误判断

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 低版本与最高版对比后
     * 下载APP
     *
     * @param id
     * @param version
     * @param url
     */
    private MAsyncTask tas;

    public void comparedToDownload(String version, String url) {
        downloadDialog = new ProgressDialog(activity);
        downloadDialog.setCancelable(false);
        downloadDialog.setTitle("下载中请等待");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setButton2("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                boolean flag = tas.cancel(true);
                if (flag) {
                    ToastUtil.getLongToastByString(activity, "取消成功");
                } else {
                    ToastUtil.getLongToastByString(activity, "取消失败");
                }
            }
        });

        int ids = Integer.parseInt(version);//网络appid
        int versionCode = getPackageInfo(activity);
        Log.i("WU" ,"versionCode"+versionCode);
        Log.i("WU", "ids"+ids);
        if (versionCode < ids) {//判断两个ID是否
            doLoginDialog(url);
        } else if (versionCode == ids||versionCode>ids) {
            ToastUtil.getLongToastByString(activity, "已是最新版本了！");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri= FileProvider.getUriForFile(activity,"com.example.youhe.youhecheguanjiaplus.fileprovider",file);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                }else{
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                activity.startActivity(intent);

                ToastUtil.getLongToastByString(activity, "下载完成");
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
    private int getPackageInfo(Context context) {
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
     * 弹出对化框是否更新
     *
     * @param
     */
    public void doLoginDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("发现新版本");
        builder.setMessage("请更新软件后再打开");

        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadDialog.show();
                tas = new MAsyncTask(url);
                tas.execute();

            }
        });
        builder.show();
    }

}
