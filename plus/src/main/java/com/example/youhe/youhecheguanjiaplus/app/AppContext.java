package com.example.youhe.youhecheguanjiaplus.app;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.XGUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class AppContext extends Application {
    public String mLocation;
    private static Context mContext;
    public String localCity = "";//当前城市名称
    public String addrStr = "";//当前位置详细地址
    public String province = "";

    public static boolean isLogin = false;//是否已经登录

    public static final int NETTYPE_WIFI = 0x01;//wifi网络
    public static final int NETTYPE_CMWAP = 0x02;//wap网络
    public static final int NETTYPE_CMNET = 0x03;//net网络
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

    // 建立请求队列
    public static RequestQueue queue;

    // 用于存放倒计时时间
    public static Map<String, Long> map;

    public static final String UTF_8 = "UTF-8";


    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(getApplicationContext());
        x.Ext.init(this);//霸气的xutils

        initXG();//信鸽推送
        initCrash();//腾讯崩溃统计

        UMShareAPI.get(this);
        PlatformConfig.setWeixin(CommentSetting.WEI_XIN_APP_ID,CommentSetting.WEI_XIN_APP_KEY);
        PlatformConfig.setQQZone(CommentSetting.QQ_APP_ID,CommentSetting.QQ_APP_KEY);
        initImageLoader(this);
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(ImageBuild().build());

        ImageLoader.getInstance().init(config.build());
    }


    /**
     * 返回ImageLoder的常用配置
     */
    public static DisplayImageOptions.Builder ImageBuild() { // 图片加载工具
        return new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
//                .showImageOnLoading(R.drawable.default_error)
                // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageForEmptyUri(R.drawable.default_error)
                // 设置图片加载/解码过程中错误时候显示的图片
//                .showImageOnFail(R.drawable.default_error)
                // 加载图片前重新恢复View
                .resetViewBeforeLoading(true)
                // 缓存到SD卡
                .cacheOnDisk(true)
                // 缓存到内存
                .cacheInMemory(true)
                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .considerExifParams(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
        MultiDex.install(this) ;
    }


    @TargetApi(9)
    protected void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }

    /**
     * 信鸽推送
     */
    private void initXG(){
        // 在主进程设置信鸽相关的内容
//        if (isMainProcess()) {
        // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
        // 收到通知时，会调用本回调函数。
        // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
        // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
        SaveNameDao saveNameDao=new SaveNameDao(this);
        String mobile=saveNameDao.readText("phonenumbe.txt");
        XGUtils.registered(mobile.trim(),false);
    }
    /**
     * 腾讯崩溃统计
     */
    private void initCrash() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getAppName(this, android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        Bugly.init(this, CommentSetting.CrashReport_APP_ID, true);
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = true;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;
        setStrictMode();
//        CrashReport.initCrashReport(context, "23cf2c530c", true, strategy);
        CrashReport.initCrashReport(context,strategy);
        CrashReport.putUserData(context, "product",context.getPackageName());
        CrashReport.setAppVersion(context, getAppVersion(context));//设置版本号
    }
    //获取应用名称
    public static String getAppName(Context context, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) i.next();
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {

            }
        }
        return processName;
    }
    /**
     * 获取当前应用程序的版本号
     */
    public static String getAppVersion(Context context) {
        String version = "0";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("the application not found");
        }
        return version;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public String getAddrStr() {
        return addrStr;
    }

    public void setLocalCity(String localCity) {
        this.localCity = localCity;
    }

    public String getLocalCity() {
        return localCity;
    }

    public static Context getContext() {
        return mContext;
    }


    /*
     * 检测网络是否可用
	 * */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /*
     * 获取当前网络类型
	 * 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /*
	 * 判断当前版本是否兼容目标版本的方法
	 */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /*
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
//        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        String uniqueID = "";
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
//            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }


    /**
     * 判断一个字符串是不是一个网址开头
     */
    public static boolean isURL(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
//            return path;
//        return "http://" + URLEncoder.encode(path);
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists()
                && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (Exception e) {
                Log.i("TAG", e.getMessage());
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
