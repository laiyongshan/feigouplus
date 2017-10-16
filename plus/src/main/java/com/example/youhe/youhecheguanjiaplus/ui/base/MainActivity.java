package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.Banner;
import com.example.youhe.youhecheguanjiaplus.bean.Bulletin;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.bean.TabFragmentBean;
import com.example.youhe.youhecheguanjiaplus.biz.StartingTest;
import com.example.youhe.youhecheguanjiaplus.biz.TokenDaoOpinion;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.ExitDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.NoticeDialog;
import com.example.youhe.youhecheguanjiaplus.fragment.CommonWebFragment;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.mainfragment.Fragment4;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.mainfragment.PlusFragment;
import com.example.youhe.youhecheguanjiaplus.mainfragment.PlusInviteFragment;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.SavFragmentTabHost;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("WrongConstant")
public class MainActivity extends YeoheActivity implements View.OnClickListener {
    private Button btn;
    public static String scanResult = "";//扫描条形码或二维码的结果
    private CaptureReceiver captureReceiver;//手机摄像头扫描二维码结果的广播接收者
    private IntentFilter captureFilter;
    private SavFragmentTabHost fragmentTabHost;
    //    private String texts[] = {"查违章","车主卡", "个人中心"};
    private NetReceiver mNetReceiver;
    private IntentFilter mNetFilter;

    private static final int BAIDU_READ_PHONE_STATE = 100;
    private AppContext appContext;
    //导航栏按钮图标
    private int imageButton[] = {R.drawable.mainactivitychaweizhang1,
            R.drawable.mainactivitychezhuka, R.drawable.mainactivitychaweizhang4};

    private Class fragmentArray[] = {MainFragment.class,PlusFragment.class, Fragment4.class};

    boolean isHasBrowser = true;
    private ArrayList<TabFragmentBean> tabs = new ArrayList<>();
    public static String EXTRA_IS_NEW = "is_new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", getApplication().getPackageName() + "//////////");

        setContentView(R.layout.activity_main);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, MainActivity.this);
        }
        SystemBarUtil.useSystemBarTint(MainActivity.this);

        captureFilter = new IntentFilter("capture.action");
        captureReceiver = new CaptureReceiver();
        registerReceiver(captureReceiver, captureFilter);//注册广播接收者
//        EventBus.getDefault().register(this);
        appContext = (AppContext) getApplicationContext();
//        getToken();//判断刷新Token;
        mNetReceiver = new NetReceiver();//网络连接情况广播接收者
        mNetFilter = new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);
        //实例化FragmentTabHost
        fragmentTabHost = (SavFragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.maincontent);
        initTab();
        for (int i = 0; i < tabs.size(); i++) {
            TabHost.TabSpec spec = fragmentTabHost.newTabSpec(tabs.get(i).getId()+"").setIndicator(getView(i));
            if (tabs.get(i).getBundle() == null)
                fragmentTabHost.addTab(spec, tabs.get(i).getActivityClass(), null);
            else
                fragmentTabHost.addTab(spec, tabs.get(i).getActivityClass(), tabs.get(i).getBundle());

            //设置背景(必须在addTab之后，由于需要子节点（底部菜单按钮）否则会出现空指针异常)
//            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bt_selector);
            if (!appContext.isNetworkConnected()) {
                UIHelper.ToastMessage(MainActivity.this, "网络连接错误，请检查网络连接设置");
            }

        }
        update();//更新

        isHasBrowser = hasBrowser(MainActivity.this);

        getBulletin();

        getHomeMsg();

        initBanner();


    }

    /**
     * 初始化底部栏
     */
//    private String texts[] = {"查违章","车主卡", "个人中心"};
    private void initTab() {

        TabFragmentBean illegalBean = new TabFragmentBean("查违章", getResources().getDrawable(R.drawable.mainactivitychaweizhang1), MainFragment.class, 1);
        TabFragmentBean plusBean = new TabFragmentBean("车主卡", getResources().getDrawable(R.drawable.mainactivitychezhuka), PlusFragment.class, 2);
        TabFragmentBean plusInviteBean = new TabFragmentBean("邀请码", getResources().getDrawable(R.drawable.mainactivityinvite), PlusInviteFragment.class, 4);
        TabFragmentBean personBean = new TabFragmentBean("个人中心", getResources().getDrawable(R.drawable.mainactivitychaweizhang4), Fragment4.class, 3);
        Log.d("TAG",UserManager.getUserType()+","+UserManager.getUserStatus());

        Bundle bundle = new Bundle();
        bundle.putString(CommonWebFragment.EXTRA_TITLE, "");
        bundle.putString(CommonWebFragment.EXTRA_URL, URLs.PLUS_CLIENTMERCHANT+ TokenSQLUtils.check());

//        ClientMerchant/register/token/tokenValue    商户注册h5
//        TabFragmentBean collectionBean = new TabFragmentBean("收款", getResources().getDrawable(R.drawable.mainactivityshoukuan), CommonWebFragment.class, 4, bundle);

        tabs.add(illegalBean);
        if (UserManager.isPlus()) {
            Log.d("TAG","sss");
            tabs.add(plusBean);
        }
        if ((UserManager.getUserType()==UserManager.USER_DISTRIBUTION)&&(UserManager.getUserStatus() == UserManager.USER_STATUS_YES)){
            Log.d("TAG","www");
            tabs.add(plusInviteBean);
        }

        tabs.add(personBean);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(EXTRA_IS_NEW)) {
            if (intent.getBooleanExtra(EXTRA_IS_NEW, false)) {
                tabs.clear();
                fragmentTabHost.clearAllTabs();
                initTab();
                for (int i = 0; i < tabs.size(); i++) {
                    TabHost.TabSpec spec = fragmentTabHost.newTabSpec(tabs.get(i).getId()+"").setIndicator(getView(i));
                    if (tabs.get(i).getBundle() == null)
                        fragmentTabHost.addTab(spec, tabs.get(i).getActivityClass(), null);
                    else
                        fragmentTabHost.addTab(spec, tabs.get(i).getActivityClass(), tabs.get(i).getBundle());

                    //设置背景(必须在addTab之后，由于需要子节点（底部菜单按钮）否则会出现空指针异常)
//            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bt_selector);
                    if (!appContext.isNetworkConnected()) {
                        UIHelper.ToastMessage(MainActivity.this, "网络连接错误，请检查网络连接设置");
                    }

                }

            }
        }
    }

    private void initBanner() {

        HashMap params = new HashMap();
        params.put("type", CommentSetting.TYPE);
//        params.put("keyname","hometop");
        params.put("device_type", "Android");
        VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestPostVolley(URLs.GET_BANNER_URL, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                List<Banner> banners = new ArrayList<Banner>();

                try {
                    JSONObject bannerObj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), MainActivity.this));
                    JSONObject dataObj = bannerObj.getJSONObject("data");
                    JSONObject startpageObj = dataObj.getJSONObject("hometop");
                    JSONArray imgListArr = startpageObj.getJSONArray("imgList");
                    for (int i = 0; i < imgListArr.length(); i++) {
                        Banner banner = new Banner();
                        banner.setLink(imgListArr.getJSONObject(i).getString("link"));
                        banner.setImgurl(imgListArr.getJSONObject(i).getString("imgurl"));
                        banner.setAction(imgListArr.getJSONObject(i).getString("action"));
                        banners.add(banner);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    HashMap map = new HashMap();
                    map.put("banners", banners);
                    Task ts = new Task(TaskType.TS_GET_BANNER, map);
                    MainService.newTask(ts);
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }

    /*
   * 获取公告通知
   * */
    NoticeDialog noticeDialog;

    public void getHomeMsg() {
        HashMap params = new HashMap();
        params.put("type", CommentSetting.TYPE);
        VolleyUtil.getVolleyUtil(MainActivity.this).StringRequestPostVolley(URLs.GETMSG, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject object = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), MainActivity.this));
                    JSONObject dataObj = object.getJSONObject("data");
                    String status = object.getString("status");
                    if (status.equals("ok")) {
                        String msg = "";
                        if (dataObj.has("content")) {
                            msg = dataObj.getString("content");
                        }
                        String imgUrl = "";
                        if (dataObj.has("imgpath")) {
                            imgUrl = dataObj.getString("imgpath");
                        }
                        noticeDialog = new NoticeDialog(MainActivity.this, R.style.Dialog, msg, imgUrl);
                        noticeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                //默认返回 false
                                return i == KeyEvent.KEYCODE_SEARCH;
                            }
                        });
                        if (!msg.trim().equals("")) {
                            noticeDialog.showDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }


    /**
     * 获取首页公告栏公告
     */
    public void getBulletin() {
        HashMap params = new HashMap();
//        params.put("token", TokenSQLUtils.check());
        params.put("type", CommentSetting.TYPE);
//        params.put("keyname","hometop");
//        params.put("device_type","Android");

        VolleyUtil.getVolleyUtil(MainActivity.this).StringRequestPostVolley(URLs.GET_HOME_NOTICE, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                List<Bulletin> bulletins = new ArrayList<Bulletin>();
                try {
                    JSONObject Obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), MainActivity.this));
                    JSONObject dataObj = Obj.getJSONObject("data");
                    JSONArray noticeInfoArr = dataObj.getJSONArray("noticeInfo");
                    for (int i = 0; i < noticeInfoArr.length(); i++) {
                        Bulletin bulletin = new Bulletin();
                        bulletin.setLink(noticeInfoArr.getJSONObject(i).getString("link"));
                        bulletin.setTitle(noticeInfoArr.getJSONObject(i).optString("title"));
                        bulletins.add(bulletin);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    HashMap map = new HashMap();
                    map.put("bulletins", bulletins);
                    Task ts = new Task(TaskType.TS_GET_NOTICE_INFO, map);
                    MainService.newTask(ts);
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });

    }


    /**
     * 更新
     */
    public void update() {

        Intent intent = getIntent();
        String isforce = intent.getStringExtra("isforceS");
        String url = intent.getStringExtra("urlS");
        if (isforce != null && url != null) {
            new StartingTest(this).comparedToDownload(isforce, url);//开始下载更新
        }
    }


    public static String CONSTANT_SELECT_FRAGMENT = "select_fragment";

    @Override
    protected void onResume() {
        super.onResume();

//        HashMap map=new HashMap();
//        Task ts=new Task(TaskType.TS_GET_YU_E,map);
//        MainService.newTask(ts);

        if (fragmentTabHost.getCurrentTab() == 2) {
            Intent ldIntent = new Intent("android.net.conn.LOADDATA");
            sendBroadcast(ldIntent);
        }
        //信鸽接收
        XGPushClickedResult receiver = XGPushManager.onActivityStarted(this);
        if (receiver != null && !StringUtils.isEmpty(receiver.getCustomContent())) {
            try {
                Log.d("TAG", receiver.getCustomContent());
                JSONObject object = new JSONObject(receiver.getCustomContent());
                if (object.has(CONSTANT_SELECT_FRAGMENT)) {
                    try {
                        int select_fragment = object.getInt(CONSTANT_SELECT_FRAGMENT);
                        if (select_fragment >= 0 && select_fragment < tabs.size()) {
                            fragmentTabHost.setCurrentTab(select_fragment);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(captureReceiver);
        this.unregisterReceiver(mNetReceiver);
    }


    private View getView(int i) {
        //取得布局实例
        View view = View.inflate(this, R.layout.tabcontent, null);

        //取得布局对象
        ImageView imageViews = (ImageView) view.findViewById(R.id.image);

        TextView textView = (TextView) view.findViewById(R.id.text1);
        //设置图标
//        imageViews.setImageResource(imageButton[i]);
        imageViews.setImageDrawable(tabs.get(i).getIcon());

        //设置标题
        textView.setText(tabs.get(i).getName());
        textView.setTextColor(getResources().getColorStateList(R.color.main_tabhost_textcolor));
        textView.setTextSize(12);

        return view;
    }

    Intent intent = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    //接收由手机摄像头扫描条码和得到的扫描结果
    class CaptureReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanResult = intent.getStringExtra("value");
            if (AppContext.isURL(scanResult) && isHasBrowser) {
                link(scanResult);
            } else {
                Toast.makeText(MainActivity.this, "扫描到的信息为：" + scanResult, Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "请检查设备是否已经安装了浏览器", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //调用手机浏览器打开网页
    private void link(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private boolean hasBrowser(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://"));

        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        final int size = (list == null) ? 0 : list.size();
        return size > 0;
    }


    @Override
    public void onBackPressed() {
//        inSelectMeu();//初始化弹出菜单
        new ExitDialog(MainActivity.this, R.style.Dialog).show();
    }

    private TokenDaoOpinion tokenDaoOpinion;

    /**
     * 判断TOKEN
     */
    public void getToken() {
        tokenDaoOpinion = new TokenDaoOpinion(this);
        tokenDaoOpinion.getToken();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private static boolean wanglstatus;

    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                wanglstatus = isConnected;
                if (isConnected) {
                    EventBus.getDefault().post(new FirstEvent("ok"));
                    EventBus.getDefault().post(new FirstEvent("sahuxin"));
                }
            }
        }
    }

    /**
     * 判断网络是否正在连接
     *
     * @return
     */
    public final static boolean getHtts() {

        return wanglstatus;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 114:
                break;
        }

    }

    /**
     * 通知显示页面
     */
    @Subscribe
    public void onEve(FirstEvent firstEvent) {

        int mainIndex = 0;
        int personIndex = 0;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getActivityClass() == MainFragment.class)
                mainIndex = i;
            if (tabs.get(i).getActivityClass() == Fragment4.class)
                personIndex = i;
        }
        if (firstEvent.getMsg().equals("Accordingtothehomepage")) {
            fragmentTabHost.setCurrentTab(mainIndex);
        } else if (firstEvent.getMsg().equals("PlaceAnOrderSuccessfully")) {
            fragmentTabHost.setCurrentTab(personIndex);
            Log.i("WU", "fragmentTabHost.setCurrentTab(1");
        }
    }


    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];
        int mainIndex = 0;
        int personIndex = 0;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getActivityClass() == MainFragment.class)
                mainIndex = i;
            if (tabs.get(i).getActivityClass() == Fragment4.class)
                personIndex = i;
        }
        switch (type) {
            case TaskType.TS_TO_MAINFRAGMENT:
                fragmentTabHost.setCurrentTab(mainIndex);
                break;

            case TaskType.TS_TO_ORDERFRAGMENT:
                fragmentTabHost.setCurrentTab(personIndex);
                Intent intent = new Intent();
                intent.setAction("REFLUSH_ORDER_LIST");
                sendBroadcast(intent);
                break;

        }
    }

}
