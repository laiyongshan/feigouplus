package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.AppManager;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.bean.Banner;
import com.example.youhe.youhecheguanjiaplus.biz.StartingTest;
import com.example.youhe.youhecheguanjiaplus.biz.TokenDaoOpinion;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class AppStart extends YeoheActivity implements View.OnClickListener {
    boolean isFirstIn = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private LocationClient mClient;
    private LocationClientOption mOption;
    private boolean mLBSIsReceiver;
    private String mLBSAddress;
    private View view;
    private ImageView start_img;//广告图片
    private StartingTest startingTest;

    private final int REQUESTCODE = 100;
    private boolean isRedirectTo = false;//判断是否进入系统

    private final int DURATION_TIME = 4500;

    private AppContext application;

//    private BitmapManager bmpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_appstart, null);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        setContentView(view);

//        bmpManager = new BitmapManager(BitmapFactory.decodeResource(AppStart.this.getResources(), R.mipmap.start_emty), AppStart.this);

        start_img = (ImageView) this.findViewById(R.id.start_img);
        start_img.setOnClickListener(this);

        startingTest = new StartingTest(this);//检测更新
        startingTest.ins();

        getToken();//判断刷新Token;
        application = (AppContext) this.getApplicationContext();
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }

        /*
         * 启动后台服务
         * */
        if (!MainService.isRun) {
            Intent i = new Intent(this, MainService.class);
            this.startService(i);
        }

        HashMap params = new HashMap();

        Task ts0 = new Task(TaskType.TS_GET_START_IMG, params);//获取启动页广告图片
        MainService.newTask(ts0);

        Task ts1 = new Task(TaskType.TS_CAR_OPEN_CITYS, params);//获取本人本车开放城市數據
        MainService.newTask(ts1);

//        Task ts2 = new Task(TaskType.TS_OTHER_ORDER_PROVICE, params);//获取其他订单省份
//        MainService.newTask(ts2);

//        HashMap params=new HashMap();
        Task ts = new Task(TaskType.TS_QUERY_ORDER_RULES, params);//查询并下单的最低限条件
        MainService.newTask(ts);

//        Task ts3=new Task(TaskType.TS_GET_OPEN_PROVINCE,params);//获取开放省份
//        MainService.newTask(ts3);


        //是否第一次进入
        preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        isFirstIn = preferences.getBoolean("isFirstIn", true);
    }

    @Override
    public void init() {

    }


    /**
     * 判断TOKEN
     */
    private TokenDaoOpinion tokenDaoOpinion;

    public void getToken() {
        tokenDaoOpinion = new TokenDaoOpinion(this);
        tokenDaoOpinion.getToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //SDK版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(AppStart.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ) {
                //请求权限网络连接
                ActivityCompat.requestPermissions(AppStart.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
//            //判断是否需要 向用户解释，为什么要申请该权限
//            ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION);
            } else {
                initLBS();//初始化定位服务
                AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(), getPackageName());
                if (checkOp == AppOpsManager.MODE_IGNORED) {
                    // 权限被拒绝了 .
                    AskForPermission();
                } else {
                    initLBS();//初始化定位服务
                    animation();//动画跳转
                }
            }
        } else {
            initLBS();//初始化定位服务
            animation();//动画跳转
        }


        if (isRedirectTo) {
            initLBS();//初始化定位服务
            animation();//动画跳转
        }
    }

    //初始化地位服务
    private void initLBS() {

        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setOpenGps(true);
        mOption.setCoorType("bd09ll");
        mOption.setAddrType("all");
        mOption.setScanSpan(1000);
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

        mClient = new LocationClient(getApplicationContext(), mOption);
        mClient.setLocOption(mOption);
        mClient.start();
        mLBSIsReceiver = true;
        mClient.requestLocation();
        mClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation arg0) {
                mLBSAddress = arg0.getAddrStr();
                application.mLocation = arg0.getAddrStr();
                if (arg0.getCity() != null) {
                    application.setLocalCity(arg0.getCity().replace("市", ""));
                    application.setProvince(arg0.getProvince().replace("省", ""));
                } else {
                    AskForPermission();//询问开启权限
                }
            }
        });


//        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//        {
//            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
//        }
    }

    public void animation() {
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(DURATION_TIME);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();//跳转
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOption != null)
            mOption.setOpenGps(false);
        if (mClient != null)
            mClient.stop();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void redirectTo() {
        if (alertDialog!=null)
            alertDialog.dismiss();

        if (!isFirstIn) {
            Intent homeIntent = new Intent(AppStart.this, MainActivity.class);
            homeIntent.putExtra("isforceS", startingTest.getIsforce() == null ? "" : startingTest.getIsforce());
            homeIntent.putExtra("urlS", startingTest.getUrlS() == null ? "" : startingTest.getUrlS());
            homeIntent.putExtra("idS", startingTest.getIdS() == null ? "" : startingTest.getIdS());
            startActivity(homeIntent);
            finish();
        } else {
            Intent guideIntent = new Intent(AppStart.this, GuideActivity.class);
            startActivity(guideIntent);
            editor.putBoolean("isFirstIn", false);
            editor.commit();
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUESTCODE) {
            if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                //用户已授权
                initLBS();//初始化定位服务
                animation();//动画跳转
            } else {
                /**用户拒绝了权限*/
                AskForPermission();
//                ToastUtil.getLongToastByString(this, "用户未授予位置定位权限！");
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    List<Banner> banners = new ArrayList<Banner>();

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];
        switch (type) {

            case TaskType.TS_YHCGJ_HOMG_PAGE_INIT://完成首页加载跳转到首页
                break;

            case TaskType.TS_GET_START_IMG://获取启动页图片显示
                HashMap params = new HashMap();
                params.put("type", CommentSetting.TYPE + "");
//                params.put("keyname", "startpage");
                params.put("device_type", "Android");
                VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestPostVolley(URLs.GET_BANNER_URL,
                        EncryptUtil.encrypt(params), new VolleyInterface() {
                            @Override
                            public void ResponseResult(Object jsonObject) {
                                String imgurl = "";
                                try {
                                    JSONObject bannerObj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), AppStart.this));
                                    JSONObject dataObj = bannerObj.getJSONObject("data");
                                    JSONObject startpageObj = dataObj.getJSONObject("startpage");

                                    imgurl = startpageObj.optString("imgurl");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (imgurl != null) {
//                                        BitmapFactory.Options options = new BitmapFactory.Options();
//                                        Bitmap defaultBmp = BitmapFactory.decodeResource(getResources(), R.drawable.start_emty, options);
//                                      bmpManager.loadLogoBitmap(banners.get(0).getImgurl(),start_img,defaultBmp,720,1280);
//                                      start_img.setImageBitmap(HttpUtil.getNetBitmap(banners.get(0).getImgurl()));
                                        new GetImgTask().execute(imgurl);
                                    } else {
                                        start_img.setImageResource(R.mipmap.start_emty);
                                    }
                                }

                                animation();//动画跳转
                            }

                            @Override
                            public void ResponError(VolleyError volleyError) {
                                animation();//动画跳转
                            }
                        });
                break;
        }
    }


    //询问设置权限
    private int a = 0;
    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;

    private void AskForPermission() {
        if (a == 1 || AppStart.this.isDestroyed())
            return;
        else if (a == 0) {
            a = 1;
        }
        if (builder == null)
            builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("您还未授予应用所必需的定位权限。\n\n请点击\\\"设置\\\"-\\\"权限控制\\\"-允许获取位置信息和读写权限");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                    startActivity(intent);
                    isRedirectTo = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return false;
            }
        });

        try {
            if (alertDialog == null)
                alertDialog = builder.create();

            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_img:
                Intent intent = new Intent(AppStart.this, CommentWebActivity.class);
                if (!banners.isEmpty() && !banners.get(0).getLink().equals("")) {
                    intent.putExtra("url", banners.get(0).getLink());
                    startActivity(intent);
                }
                break;
        }
    }


    /**
     * 第一个参数表示要执行的任务，通常是网络的路径；第二个参数表示进度的刻度，第三个参数表示任务执行的返回结果
     */
    public class GetImgTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 表示任务执行之前的操作
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        /**
         * 主要是完成耗时的操作
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // 使用网络连接类HttpClient类王城对网络数据的提取

            Bitmap bitmap = HttpUtil.getNetBitmap(arg0[0]);
            return bitmap;
        }

        /**
         * 主要是更新UI的操作
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {

                start_img.setImageBitmap(result);
            } else {
                start_img.setImageResource(R.mipmap.start_emty);
            }
        }
    }
}
