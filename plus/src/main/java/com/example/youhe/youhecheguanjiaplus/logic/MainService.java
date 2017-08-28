package com.example.youhe.youhecheguanjiaplus.logic;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.AppManager;
import com.example.youhe.youhecheguanjiaplus.bean.Banner;
import com.example.youhe.youhecheguanjiaplus.bean.Bulletin;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.manager.CarOpenCitysMenager;
import com.example.youhe.youhecheguanjiaplus.manager.City_ProvinceManager;
import com.example.youhe.youhecheguanjiaplus.manager.ProprefixManager;
import com.example.youhe.youhecheguanjiaplus.ui.base.MainActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.FileUtils;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class MainService extends Service implements Runnable {
    public static boolean isRun = false;
    public static ArrayList<Task> allTask = new ArrayList<Task>();


    // 添加窗口到集合中
    public static void addActivity(YeoheActivity ia) {
        AppManager.getAppManager().addActivity(ia);
    }

    public static void removeActivity(YeoheActivity ia) {
        AppManager.getAppManager().finishActivity(ia);
    }

    public static void addFragment(YeoheFragment xf) {
        Log.i("TAG"," AppManager.getAppManager().addFragment(xf);");
        AppManager.getAppManager().addFragment(xf);
    }

    public static void removeFragment(YeoheFragment xf) {
        AppManager.getAppManager().finishFragment(xf);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 添加任务
    public static void newTask(Task ts) {
        allTask.add(ts);
    }

    @Override
    public void run() {
        while (isRun) {
            if (allTask.size() > 0) {
                doTask(allTask.get(0));
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Exception", e.getMessage());
                }
            }
//            Log.i("TAG", "MainService is Running....");
        }
    }

    /*
     * 执行各种网络请求任务
	 */
    public String key = "car_open_citys_";
    public String province_key = "car_province_";
    public String proprefixJson = "";
    public String carOpnCityStr = "";

    private void doTask(Task ts) {
        final Message message = hand.obtainMessage();
        message.what = ts.getTaskId();
        switch (ts.getTaskId()) {
            case TaskType.TS_YHCGJ_HOMG_PAGE_INIT: // 首页初始化
                break;

            case TaskType.TS_REFLUSH_HOME_PAGE://用户登录
                break;

            case TaskType.TS_USER_EXIT://退出登出
                if (MainFragment.carList != null && MainFragment.carViews != null) {
                    MainFragment.carList.clear();
                    MainFragment.carViews.clear();
                    AppContext.isLogin = false;

                    Intent intent = new Intent(AppContext.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(MainActivity.EXTRA_IS_NEW, true);
                    startActivity(intent);
                }
                break;

            case TaskType.TS_REFLUSH_VIALATION://刷新车辆信息中的违章信息
                final String json = (String) ts.getTaskParam().get("violationList");
                String carid = (String) ts.getTaskParam().get("carid");
                String carnumber = (String) ts.getTaskParam().get("carnumber");
                HashMap map = new HashMap();
                try {
                    JSONObject obj = new JSONObject(json);
                    int totalDgree = obj.getJSONObject("data").getInt("degree");
                    int totalCount = obj.getJSONObject("data").getInt("count");
                    int num = obj.getJSONObject("data").getInt("num");

                    map.put("carid", carid);
                    map.put("carnumber", carnumber);
                    map.put("totalDgree", totalDgree);
                    map.put("totalCount", totalCount);

                    message.arg1 = num;
                    if (map != null) {
                        message.obj = map;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case TaskType.TS_QUERY_ORDER_RULES://查询及下单的最低规则
                VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestGetVolley(URLs.QUERY_ORDER_RULES, new VolleyInterface() {
                    @Override
                    public void ResponseResult(Object json) {
                        Log.i("TAG", json.toString());
                        if (json == null || json.equals("")) {
                            json = FileUtils.readFromRaw(getApplicationContext());
                        }
                        try {
                            City_ProvinceManager.jsonToProvinceList(json.toString());
                            City_ProvinceManager.jsonToCityList(json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ResponError(VolleyError volleyError) {
                        Log.i("TAG", volleyError.toString());
                        String json = FileUtils.readFromRaw(getApplicationContext());
                        try {
                            City_ProvinceManager.jsonToProvinceList(json.toString());
                            City_ProvinceManager.jsonToCityList(json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;

            case TaskType.TS_CAR_OPEN_CITYS://获取本人本车开放城市

                HashMap<String, Object> owner_citys_map = new HashMap<String, Object>();
                VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestGetVolley(URLs.GET_CAR_OPEN_CITY, new VolleyInterface() {
                    @Override
                    public void ResponseResult(Object jsonObject) {
                        Log.i("TAG", "获取本人本车开放城市" + jsonObject.toString());
                        carOpnCityStr = EncryptUtil.decryptJson(jsonObject.toString(), getApplicationContext());
                        try {
                            JSONObject obj = new JSONObject(carOpnCityStr);
                            String status = obj.getString("status");
                            if (status.equals("ok")) {
                                carOpnCityStr = EncryptUtil.decryptJson(jsonObject.toString(), getApplicationContext());
                                HttpUtil.saveJson2FileCache(key, carOpnCityStr);//缓存数据
                            } else {
                                carOpnCityStr = HttpUtil.LoadDataFromLocal(key) + "";//使用缓存数据
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            CarOpenCitysMenager.citysJson2list(carOpnCityStr);
                        }
                    }

                    @Override
                    public void ResponError(VolleyError volleyError) {
                        Log.i("TAG", volleyError.toString());
                        carOpnCityStr = HttpUtil.LoadDataFromLocal(key);//使用缓存数据
                    }
                });

                if (CarOpenCitysMenager.carOpenCityList.isEmpty()) {
                    carOpnCityStr = HttpUtil.LoadDataFromLocal(key) + "";//使用缓存数据
                    CarOpenCitysMenager.citysJson2list(carOpnCityStr);
                }
                break;

            case TaskType.TS_TO_XINGE_TOKEN://提交信鸽Token
                VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestPostVolley(URLs.MOBILEREGISTERXINGE,
                        ts.getTaskParam(), new VolleyInterface() {
                            @Override
                            public void ResponseResult(Object jsonObject) {
                                Log.w("xGNotifaction", "注册完成后返回的数据" + EncryptUtil.decryptJson(jsonObject.toString(), getApplicationContext()));

                            }

                            @Override
                            public void ResponError(VolleyError volleyError) {
                                Log.w("xGNotifaction", "注册失败后返回的数据" + EncryptUtil.decryptJson(volleyError.toString(), getApplicationContext()));
                            }
                        });
                break;

            case TaskType.TS_TO_MAINFRAGMENT:

                break;

            case TaskType.TS_TO_ORDERFRAGMENT:

                break;

            case TaskType.TS_GET_START_IMG://获取启动页广告图片

                break;

            case TaskType.TS_GET_BANNER://获取首页banner图片
                List<Banner> banners = (List<Banner>) ts.getTaskParam().get("banners");
                message.obj = banners;
                Log.i("TAG","banners size is:"+banners.size());
                break;

            case TaskType.TS_GET_NOTICE_INFO://获取首页公告栏
                List<Bulletin> bulletins = (List<Bulletin>) ts.getTaskParam().get("bulletins");
                message.obj = bulletins;
                break;

            case TaskType.TS_REAL_NAME:
                message.arg1 = (int)ts.getTaskParam().get("auth");
                break;

            case TaskType.TS_GET_YU_E:
                //获取用户余额
                Log.i("TAG", "AppManager.getAppManager().getFragmentByName(\"Fragment4\").refresh(msg.what);");
                break;

            case TaskType.TS_GET_OPEN_PROVINCE://获取开放省份
                VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestGetVolley(URLs.GET_OPEN_PROVINCE, new VolleyInterface() {
                    @Override
                    public void ResponseResult(Object jsonObject) {
                        Log.i("TAG", "获取开放的省份：" + jsonObject.toString());
                        try {
                            JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), getApplicationContext()));

                            String status = obj.getString("status");
                            if (status.equals("ok")) {
                                proprefixJson = obj.toString();
                                HttpUtil.saveJson2FileCache(province_key, EncryptUtil.decryptJson(jsonObject.toString(), getApplicationContext()));//缓存数据
                            } else {
                                proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                        } finally {
                            ProprefixManager.jsonToProprefixList(proprefixJson);
                        }
                    }

                    @Override
                    public void ResponError(VolleyError volleyError) {
                        Log.i("TAG", volleyError.toString());
                        proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                        ProprefixManager.jsonToProprefixList(proprefixJson);
                    }
                });
                break;
            case TaskType.TS_GET_PLUS_DISTRIBUTION_INVICODE:////获取分销用户的邀请码

                break;

        }

        allTask.remove(ts);
        hand.sendMessage(message);
    }

    public Handler hand = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TaskType.TS_YHCGJ_HOMG_PAGE_INIT:// 初始化程序界面
                    if (AppManager.getAppManager().getActivityByName("AppStart") != null)
                        AppManager.getAppManager().getActivityByName("AppStart").refresh(msg.what);
                    Log.i("TAG", "初始化首页界面。。。。");
                    break;
                case TaskType.TS_REFLUSH_HOME_PAGE://刷新首页数据
                    if (AppManager.getAppManager().getFragmentByName("MainFragment") != null)
                        AppManager.getAppManager().getFragmentByName("MainFragment").refresh(msg.what);
                    Log.i("TAG", "刷新首页数据。。。。。。");
                    break;

                case TaskType.TS_REFLUSH_VIALATION://刷新首页车辆信息中的违章信息
                    if (AppManager.getAppManager().getFragmentByName("MainFragment") != null)
                        AppManager.getAppManager().getFragmentByName("MainFragment").refresh(msg.what, msg.obj, msg.arg1);
                    break;

                case TaskType.TS_USER_LOGIN://用户登录

                    break;

                case TaskType.TS_USER_EXIT://退出登录

                    if (AppManager.getAppManager().getFragmentByName("MainFragment") != null) {
                        //noinspection ConstantConditions
                        AppManager.getAppManager().getFragmentByName("MainFragment").refresh(msg.what);
                    }

                    if (AppManager.getAppManager().getFragmentByName("Fragment4") != null) {
                        AppManager.getAppManager().getFragmentByName("Fragment4").refresh(msg.what);
                    }
                    break;

                case TaskType.TS_QUERYINDEX://违章查询
                    break;

                case TaskType.TS_QUERY_ORDER_RULES://查询并下单的最低限条件
                    break;

                case TaskType.TS_CAR_OPEN_CITYS://获取本人本车开放城市

                    break;

                case TaskType.TS_OTHER_ORDER_PROVICE:
                    break;

                case TaskType.TS_TO_XINGE_TOKEN://提交信鸽token

                    break;

                case TaskType.TS_TO_MAINFRAGMENT://跳转到mainFragment
                    if (AppManager.getAppManager().getActivityByName("MainActivity") != null)
                        AppManager.getAppManager().getActivityByName("MainActivity").refresh(msg.what);
                    Log.i("TAG", "初始化首页界面。。。。");
                    break;

                case TaskType.TS_TO_ORDERFRAGMENT://跳转到orderFragment
                    if (AppManager.getAppManager().getActivityByName("MainActivity") != null)
                        AppManager.getAppManager().getActivityByName("MainActivity").refresh(msg.what);
                    Log.i("TAG", "跳转到订单查询中心");
                    break;


                case TaskType.TS_GET_START_IMG://获取启动页图片
                    if (AppManager.getAppManager().getActivityByName("AppStart") != null)
                        AppManager.getAppManager().getActivityByName("AppStart").refresh(msg.what, msg.obj);
                    break;

                case TaskType.TS_GET_BANNER://获取首页banner图
                    Log.i("TAG", "Banner 加载默认图片请...."+AppManager.getAppManager().getFragmentByName("MainFragment"));

                    if (AppManager.getAppManager().getFragmentByName("MainFragment") != null) {
                        //noinspection ConstantConditions
                        List<Banner> bannerList;
                        bannerList = (List<Banner>) msg.obj;
                        if (bannerList == null) {
                            bannerList = new ArrayList<Banner>();
                        }
                        AppManager.getAppManager().getFragmentByName("MainFragment").refresh(msg.what, bannerList);
                        Log.i("TAG", "Banner 加载默认图片请1wwww....");
                    }
                    break;

                case TaskType.TS_GET_NOTICE_INFO://获取首页公告栏
                    if (AppManager.getAppManager().getFragmentByName("MainFragment") != null) {
                        //noinspection ConstantConditions
                        List<Bulletin> bulletinList;
                        bulletinList = (List<Bulletin>) msg.obj;
                        if (bulletinList == null) {
                            bulletinList = new ArrayList<Bulletin>();
                        }
                        AppManager.getAppManager().getFragmentByName("MainFragment").refresh(msg.what, bulletinList);
                    }
                    break;

                case TaskType.TS_REAL_NAME://是否已经实名认证
                    if (AppManager.getAppManager().getFragmentByName("Fragment4") != null)
                        AppManager.getAppManager().getFragmentByName("Fragment4").refresh(msg.what, msg.arg1);
//                    Intent  intent=new Intent(AppContext.getContext(), MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                    break;

                case TaskType.TS_GET_YU_E://余额查询
                    if (AppManager.getAppManager().getFragmentByName("Fragment4") != null) {
                        AppManager.getAppManager().getFragmentByName("Fragment4").refresh(msg.what);
                    }
                    break;

                case TaskType.TS_GET_OPEN_PROVINCE:

                    break;
            }
        }
    };




    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        isRun = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

}
