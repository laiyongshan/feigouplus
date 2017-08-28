package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.IllegalFragmentPagerAdapter;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.fragment.DontHandleFragment;
import com.example.youhe.youhecheguanjiaplus.fragment.QuotedFragment;
import com.example.youhe.youhecheguanjiaplus.fragment.RealTimeFragment;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.NiftyUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class IllegalQueryActivty extends FragmentActivity implements View.OnClickListener{

    private ViewPager illegal_viewpager;
    private List<Fragment> fragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;

    private List<Violation> violations,violations_normal,violations_quoted,violations_donthandle;//总违章列表，12分违章列表，普通违章列表,待报价违章,不可代办

    private LinearLayout illegal_list_layout,network_request_error_layout;
    private TextView reload_tv;//重新加载

    private TextView query_back_img;
    private TextView major_violation_icon;
    private TextView carnum_tv;//车牌号码

    public String carid="";//车辆id
    public static String carnumber="";//车牌号码

    private int searchtype=-1;//是否本人本车  1代扣分   2本人本車

    private AppContext appContext;

    public int isNeedLisence=1;//是否需要补充资料，1需要

    NetReceiver mNetReceiver;
    IntentFilter mNetFilter;

    private TextView real_time_quotes_tv,to_quotes_tv,not_handel_tv;//实时报价，待报价，不可代办  选项卡
    private List<TextView> tvList;

    public String queryUrl="";
    private String vehicleAccount="";//车管所账号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,IllegalQueryActivty.this);
        }
        SystemBarUtil.useSystemBarTint(IllegalQueryActivty.this);

        EventBus.getDefault().register(this);
        appContext= (AppContext)this.getApplicationContext();

        carid=getIntent().getStringExtra("carid");
        carnumber=getIntent().getStringExtra("carnumber");
        searchtype=getIntent().getIntExtra("searchtype",1);
        queryUrl=getIntent().getStringExtra("queryUrl");
        vehicleAccount=getIntent().getStringExtra("vehicleAccount");

        violations=new ArrayList<Violation>();
        violations_normal=new ArrayList<Violation>();
        violations_quoted=new ArrayList<Violation>();
        violations_donthandle=new ArrayList<Violation>();

//        selectIntentFilter=new IntentFilter(IllegalListAdapter.SELECTED_ACTION);
//        selectBrocastReceiver=new SelectBrocastReceiver();
//        registerReceiver(selectBrocastReceiver,selectIntentFilter);

        initViews();

        mNetReceiver= new NetReceiver();//网络连接情况广播接收者
        mNetFilter= new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);

//        TipsDialog tipsDialog1=new TipsDialog(IllegalQueryActivty.this, R.style.Dialog);//温馨提示
//        tipsDialog1.show();
    }

    //获取数据
    public void getData(String url){
        if(appContext.isNetworkConnected()){
            illegal_list_layout.setVisibility(View.VISIBLE);
            network_request_error_layout.setVisibility(View.GONE);
            qurery(queryGetParams(carid, searchtype),url);
        }else {
            Toast.makeText(this, "网络连接失败，请检查网络连接设置", Toast.LENGTH_LONG).show();
            illegal_list_layout.setVisibility(View.GONE);
            network_request_error_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((TokenSQLUtils.check().equals("") || TokenSQLUtils.check() == null)){
            finish();
        };

        if(carid!=null&&searchtype==1) {
            carOrderCheck(queryGetParams(carid, searchtype));//代扣分 查询是否需要补充资料
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new FirstEvent("shua"));
        unregisterReceiver(mNetReceiver);
        super.onDestroy();
    }


    //初始化Viewpager
    private void initViewPager(){
        illegal_viewpager= (ViewPager) findViewById(R.id.illegal_viewpager);
        illegal_viewpager .setOnTouchListener( new View.OnTouchListener()//修改不可左右滑动
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;  //修改为true
            }
        });

        fragmentList=new ArrayList<Fragment>();
        Fragment realTimeFragment=new RealTimeFragment(violations_normal,"",searchtype,carnumber,carid);
        Fragment dontHandleFragment=new DontHandleFragment(violations_donthandle);
        Fragment quotedFragment=new QuotedFragment(violations_quoted,searchtype,carnumber,carid);
        fragmentList.add(realTimeFragment);
        fragmentList.add(quotedFragment);
        fragmentList.add(dontHandleFragment);
        FragmentManager fm=getSupportFragmentManager();

        fragmentPagerAdapter=new IllegalFragmentPagerAdapter(fm,fragmentList);
        illegal_viewpager.setAdapter(fragmentPagerAdapter);

        illegal_viewpager.setOffscreenPageLimit(3);

        if(violations_quoted.isEmpty()){
            to_quotes_tv.setClickable(false);
            to_quotes_tv.setTextColor(Color.argb(150,198,198,198));
        }

        if(violations_donthandle.isEmpty()){
            not_handel_tv.setClickable(false);
            not_handel_tv.setTextColor(Color.argb(150,198,198,198));
        }
    }

    private void initViews(){

        tvList=new ArrayList<TextView>();

        real_time_quotes_tv= (TextView) findViewById(R.id.real_time_quotes_tv);
        real_time_quotes_tv.setOnClickListener(this);

        tvList.add(real_time_quotes_tv);
        to_quotes_tv= (TextView) findViewById(R.id.to_quotes_tv);
        to_quotes_tv.setOnClickListener(this);
        tvList.add(to_quotes_tv);
        not_handel_tv= (TextView) findViewById(R.id.not_handel_tv);
        not_handel_tv.setOnClickListener(this);
        tvList.add(not_handel_tv);


        reload_tv= (TextView) findViewById(R.id.reload_tv);
        reload_tv.setOnClickListener(this);


        carnum_tv= (TextView) findViewById(R.id.carnum_tv);
        carnum_tv.setText(carnumber);


        query_back_img= (TextView) findViewById(R.id.query_back_img);
        query_back_img.setOnClickListener(this);

        major_violation_icon= (TextView) findViewById(R.id.major_violation_icon);
        major_violation_icon.setOnClickListener(this);

        illegal_list_layout= (LinearLayout) findViewById(R.id.illegal_list_layout);
        network_request_error_layout= (LinearLayout) findViewById(R.id.network_request_error_layout);

        if(!appContext.isNetworkConnected()){
            illegal_list_layout.setVisibility(View.GONE);
            network_request_error_layout.setVisibility(View.VISIBLE);
        }
    }

    List<Violation> order_violations=new ArrayList<Violation>();
    /**
     * 控件点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.real_time_quotes_tv://实时报价
                tvChooose(real_time_quotes_tv);
                setView(0);
                break;

            case R.id.to_quotes_tv://待报价
                tvChooose(to_quotes_tv);
                setView(1);
                break;

            case R.id.not_handel_tv://不可代办
                tvChooose(not_handel_tv);
                setView(2);
                break;

            case R.id.reload_tv:
                getData(queryUrl);//网络加载失败，重新加载数据
                break;

            case R.id.query_back_img:
                finish();
                if(NiftyUtil.niftyNotificationView!=null) {
                    NiftyUtil.niftyNotificationView.removeSticky();
                }
                overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
                break;

            case R.id.major_violation_icon://驾照信息
                if(queryUrl.equals(URLs.QUERY)) {
                    Intent intent = new Intent(IllegalQueryActivty.this, MajorViolationActivity.class);
                    intent.putExtra("carid", carid);
                    startActivity(intent);
                }else if(queryUrl.equals(URLs.QUERY_VIOLATION_122)){
                    Intent intent = new Intent(IllegalQueryActivty.this, UploadDravingLisenceActivivty.class);
                    intent.putExtra("carid", carid);
                    startActivity(intent);
                }
                break;

        }
    }


    private void tvChooose(TextView tv){
        tv.setBackgroundResource(R.drawable.illegal_item_bg);
        for(int i=0;i<tvList.size();i++){
            if((tv.getId())!=(tvList.get(i).getId())){
                tvList.get(i).setBackgroundColor(Color.argb(0,255,255,255));
            }
        }
    }


    private void setView(int type){

        switch(type){
            case 0://实时报价
                if(illegal_viewpager!=null) {
                    illegal_viewpager.setCurrentItem(0);
                }
                break;

            case 1://待报价
                if(illegal_viewpager!=null) {
                    illegal_viewpager.setCurrentItem(1);
                }
                break;

            case 2://不可代办
                if(illegal_viewpager!=null) {
                    illegal_viewpager.setCurrentItem(2);
                }
                break;
        }
    }

    /**
     * 订单提交车辆检测是否需要补充资料
     * */
    public void carOrderCheck(HashMap<String,Object> map){
        VolleyUtil.getVolleyUtil(IllegalQueryActivty.this).StringRequestPostVolley(URLs.CAR_ORDER_CHECK,EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","订单提交车辆检测是否需要补充资料:"+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),IllegalQueryActivty.this));
                    String status=obj.getString("status");
                    if(status.equals("ok")){
                        isNeedLisence=0;//不需要补充资料
                    }else{
                        isNeedLisence=1;//需要补充资料
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    setIsNeedLisence(isNeedLisence);
                    Log.i("isNeedLisence",">>>>>>>>>"+(getIsNeedLisence()));
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(IllegalQueryActivty.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
  * 查询请求参数
  * */
    HashMap<String, Object> map;
    private TokenSQLUtils tokenSQLUtils;
    public HashMap<String, Object> queryGetParams(String carid,int searchtype) {
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            map.put("token", token);
        }
        map.put("carid", carid);
        map.put("searchtype",searchtype+"");//1代扣分   2本人本車
        return map;
    }


    /*
    * 查询违章
    * */
    public void qurery(HashMap<String, Object> map,String url) {

        UIHelper.showPd(IllegalQueryActivty.this);

        if(url.equals(URLs.QUERY_VIOLATION_122)){
            map.put("vehicleAccount",vehicleAccount);
        }

        VolleyUtil.getVolleyUtil(IllegalQueryActivty.this).StringRequestPostVolley(url, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "查询车辆违章请求到的数据为：" + jsonObject);
//                Toast.makeText(IllegalQueryActivty.this, jsonObject.toString() + "", Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),IllegalQueryActivty.this));
                    String status=obj.getString("status");
//                    int code=obj.getInt("code");
//                    UIHelper.showErrTips(code,IllegalQueryActivty.this);
                    if(status.equals("ok")) {

                        violations.clear();
                        violations_normal.clear();
                        violations_donthandle.clear();

                        violations = jsonToList(EncryptUtil.decryptJson(jsonObject.toString(), IllegalQueryActivty.this));

                        for (Violation violation : violations) {
                            if (violation.getQuotedprice() == 1) {
                                violations_normal.add(violation);//实时报价
                            } else if (violation.getQuotedprice() == 2) {
                                violations_quoted.add(violation);//待报价
                            } else if (violation.getQuotedprice() == -1) {
                                violations_donthandle.add(violation);//不可代办
                            }
                        }

                        if (violations.size() == 0) {
                            illegal_list_layout.setVisibility(View.VISIBLE);
                            network_request_error_layout.setVisibility(View.INVISIBLE);
                        } else {
                            illegal_list_layout.setVisibility(View.VISIBLE);
                            network_request_error_layout.setVisibility(View.INVISIBLE);

//                            String msg="办理12分特殊处理违章需要邮寄车主行驶证原件，具体地址提交订单后会有客服人员联系您，请用户知悉。";
                            if (content != null || (!content.equals("")))
                                NiftyUtil.showNotify(IllegalQueryActivty.this, content);
                        }
                    }else if(status.equals("fail")){
                        if(obj.has("show_msg")) {
                            String show_msg = obj.optString("show_msg");
                            Toast.makeText(IllegalQueryActivty.this,"查询违章失败，"+show_msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(IllegalQueryActivty.this, "网络请求超时，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                }finally {
                    UIHelper.dismissPd();
                    initViewPager();//初始化
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                illegal_list_layout.setVisibility(View.GONE);
                network_request_error_layout.setVisibility(View.VISIBLE);
                Toast.makeText(IllegalQueryActivty.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "查询车辆违章请求错误信息为：" + volleyError.toString());
                UIHelper.dismissPd();
            }
        });
    }

    Violation violation;
    String content="";
    private List<Violation> jsonToList(String json){
        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");
            JSONObject msgObj=dataObj.getJSONObject("message");

            content=msgObj.getString("content");

            JSONArray array=dataObj.getJSONArray("peccancyList");
            JSONObject aObj;
            for(int i=0;i<array.length();i++){
                aObj=array.getJSONObject(i);
                violation=new Violation();
                violation.setId(aObj.getString("id"));
                violation.setTime(aObj.getString("time"));
                violation.setLocation(aObj.getString("location"));
                violation.setReason(aObj.getString("reason"));
                violation.setCount(aObj.getInt("count"));
                violation.setCarId(aObj.getString("carid"));
                violation.setCanprocess(aObj.getString("canprocess"));
                violation.setStatus(aObj.getString("status"));
                violation.setDegree(aObj.getInt("degree"));
                violation.setPoundage(aObj.getString("poundage"));
                violation.setLocationname(aObj.getString("locationname"));
                violation.setPrice(aObj.getInt("price"));
                violation.setCategory(aObj.getString("category"));
//                violation.setCategory("现场单");
                violation.setOrderstatus(aObj.getString("orderstatus"));
                violation.setQuotedprice(aObj.getInt("quotedprice"));
//                violation.setQuotedprice(2);
                if(aObj.has("latefee")){// 如果有滞纳金字段
                    violation.setLatefee(aObj.getInt("latefee"));
                }

                violation.setPickone(aObj.getInt("pickone"));//可否挑单
                violation.setRemark(aObj.getString("remark"));//违章说明
                violation.setIscommit(aObj.getInt("iscommit"));//是否可提交

                violations.add(violation);
            }
            return violations;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return violations;
    }

    /**
     * 返回键点击事件
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            if(NiftyUtil.niftyNotificationView!=null) {
                NiftyUtil.niftyNotificationView.removeSticky();
            }
            finish();
            overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
            return false;
        }
        return false;
    }

    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                if (isConnected) {
                    if(searchtype==1){
                        getData(queryUrl);
                    }else {
                        getData(queryUrl);//获取数据
                    }
                } else {
                    network_request_error_layout.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Subscribe
    public void onEve(FirstEvent event){

    }
    /**
     * 通知刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==110){
            if(data!=null){
                isNeedLisence=data.getIntExtra("isUpdataS",1);
                setIsNeedLisence(isNeedLisence);
                Log.i("isNeedLisence",">>>>>>>>>"+isNeedLisence);
            }
        }
    }

    public void setIsNeedLisence(int isNeedLisence){
        this.isNeedLisence=isNeedLisence;
    }

    public int getIsNeedLisence() {
        return isNeedLisence;
    }
}
