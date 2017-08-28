package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderListViewAdapter2;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.CheckInTimeSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.AnnualDetailActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.DenLuActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.OrderdetaiActivityV2;
import com.example.youhe.youhecheguanjiaplus.ui.base.OtherTheOrderActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.TheNetworkStatuUtils;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.example.youhe.youhecheguanjiaplus.widget.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/9/1 0001.
 * ListView刀类
 * <p>
 * 具有上拉下拉刷新、删除
 */
public class OrMyListViewDao implements XListView.IXListViewListener {


    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private XListView listView;//传进来的View
    //    public OrderListViewAdapter adapter;//传进来的适配器
    public OrderListViewAdapter2 adapter;//传进来的适配器
    private Activity activity;//Context
    private Boolean fullData = true;//用来判断LisView页面是否加载满屏；
    private LinearLayout emView, error, maintenance, notLoggedIn;
    private VolleyUtil volleyUtil;//请求网络
    private TokenSQLUtils tsl;//拿到已保存的Token值
    private StatusSQLUtils statusSQLUtils;//得到登录状态
    private String url = "";//请求地址
    private String type = "";//请求类型
    private int orderListType;//订单列表类型 1：违章订单  2：年检订单
    private UIDialog uidialog;
    private SharedPreferences sp;

    private String del_url;//删除订单接口地址

    private Handler mHandlers = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1:
                    page = 1;//把加载的页面变为第一页
                    if (beanList != null) {
                        beanList.clear();
                        adapter.clearDate();
                    }
                    httsData(url);
                    break;
                case 2:

                    adapter.notifyDataSetChanged();

                    break;

                case 3://隐藏

                    error.setVisibility(View.GONE);
                    maintenance.setVisibility(View.GONE);
                    notLoggedIn.setVisibility(View.GONE);
                    listView.setEmptyView(emView);//当listView 没有数据时显示该界面

                    break;
                case 4:
                    error.setVisibility(View.GONE);
                    maintenance.setVisibility(View.GONE);
                    emView.setVisibility(View.GONE);
                    listView.setEmptyView(notLoggedIn);
                    break;

                case 5:
                    error.setVisibility(View.GONE);
                    maintenance.setVisibility(View.GONE);
                    emView.setVisibility(View.GONE);
                    listView.setEmptyView(notLoggedIn);
                    break;

                case 6:
                    error.setVisibility(View.GONE);
                    emView.setVisibility(View.GONE);
                    notLoggedIn.setVisibility(View.GONE);
                    listView.setEmptyView(maintenance);
                    uidialog.hide();
                    listView.removeFooterView(loadMoreView);
                    break;

                case 7:
                    emView.setVisibility(View.GONE);
                    maintenance.setVisibility(View.GONE);
                    notLoggedIn.setVisibility(View.GONE);
                    listView.setEmptyView(error);//当网络错误时 显示该界面
                    uidialog.hide();
                    break;
            }
        }
    };
    private View loadMoreView;//上拉刷新


    public OrMyListViewDao(Activity activity, View faragmentView, String url, String type,int orderListType) {
        this.activity = activity;
        this.url = url;
        this.type = type;
        this.orderListType=orderListType;

        if(orderListType==2){//年检订单
            del_url= URLs.ANNUAL_DEL;
        }else{//普通订单
            del_url=URLs.DELETEORDE;
        }

        in(faragmentView);//初始化
    }

    private void in(View view) {
        Log.i("WU", "" + getNetSpeedBytes() / 1024);
        EventBus.getDefault().register(this);
        sp = activity.getSharedPreferences("first", activity.MODE_PRIVATE);
        uidialog = new UIDialog(activity, "正在加载.......");

        listView = (XListView) view.findViewById(R.id.listview);
        listView.setPullRefreshEnable(true);//启动下拉刷新功能
        listView.setXListViewListener(this);//下拉刷新事件
        loadMoreView = activity.getLayoutInflater().inflate(R.layout.footer, null);//上拉刷新

        emView = (LinearLayout) view.findViewById(R.id.layyh);//没有数据需要显示的界面
        error = (LinearLayout) view.findViewById(R.id.network_error);//网络错误界面
        maintenance = (LinearLayout) view.findViewById(R.id.maintenance_lay);//系统正在维护时显示的界面
        notLoggedIn = (LinearLayout) view.findViewById(R.id.logged);//未登录时显示这个界面

        adapter = new OrderListViewAdapter2(activity, type,orderListType);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swh_status);
        listView.setAdapter(adapter);

        volleyUtil = VolleyUtil.getVolleyUtil(activity);//上网请求
        tsl = new TokenSQLUtils(activity);//在数据库中拿到Token值
        beanList = new ArrayList<>();//放OrderInquiry数据对象的集合
        phonePams = new HashMap<>();
        deleteOrdehHashMap = new HashMap<>();
        statusSQLUtils = new StatusSQLUtils(activity);
        theInterfaceButton();
        theNetworkStatus();//当网络出错时显示的界面
        loadingDate();//RecyclerView的上拉加载
        longItemClick();//长按事件
        itemClick();//点击事件
        httsData(url);//上网请求数据
    }

    /***
     * 异常界面的按钮事件
     */
    public void theInterfaceButton() {
        //网络错误重新加载界面
        error.findViewById(R.id.chonxingjiaz1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FirstEvent("ok"));
            }
        });
        //维护中重新加载界面
        maintenance.findViewById(R.id.chonxingjiaz2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FirstEvent("ok"));
            }
        });
        //跳到登录界面
        notLoggedIn.findViewById(R.id.qudenglu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, DenLuActivity.class));
            }
        });
    }

    /**
     * 显示未登录界面
     */
    public void according() {
        String status = statusSQLUtils.check();
        if (status.equals("")) {
            mHandlers.sendEmptyMessage(4);
            mHandlers.sendEmptyMessage(2);
        }
        if (status.equals("no")) {

            mHandlers.sendEmptyMessage(5);

            mHandlers.sendEmptyMessage(2);
        }
    }


    private HashMap<String, String> phonePams;
    public int page = 1;

    /**
     * 上网请求数据
     */
    public void httsData(String url) {

        theNetworkStatus();//当网络出错时显示该界面
        according();//显示未登录界面

        String token = tsl.check();
        phonePams.put("token", token);
        phonePams.put("page", "" + page);//页
        Log.d("TAG","pgae"+page);
        phonePams.put("type", type);//类型
        volleyUtil.StringRequestPostVolley(url, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("WU", "jsonObject=====>" + jsonObject.toString());
                listView.removeFooterView(loadMoreView);
                jsonAnalysis(EncryptUtil.decryptJson(jsonObject.toString(), activity));//json解析
                Log.i("TAG", jsonObject.toString());

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
                listView.removeFooterView(loadMoreView);
            }
        });
    }

    public ArrayList<OrderInquiry.DataBean> beanList = new ArrayList<OrderInquiry.DataBean>();

    /**
     * json解析
     *
     * @param json
     */
    public void jsonAnalysis(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            JSONObject data = jsonObject.getJSONObject("data");
            String orderList = data.getString("orderList");
            if (status.equals("ok") && orderList != null) {
                mHandlers.sendEmptyMessage(3);

                //解析Json
                ArrayList<OrderInquiry.DataBean> bean1 = new Gson().fromJson(orderList, new TypeToken<List<OrderInquiry.DataBean>>() {
                }.getType());

                if (beanList != null && page <= 1)
                    beanList.clear();

                if (beanList != null)
                    beanList.addAll(bean1);

                if (beanList != null && !beanList.isEmpty()) {
                    adapter.addData(beanList);
                }


//                adapter.addForm(bean);

//                listView.removeFooterView(loadMoreView);

                mHandlers.sendEmptyMessage(2);
            }
            if (status.equals("fail")) {
                String code = jsonObject.getString("code");
                loadMoreView.setVisibility(View.GONE);
                if (code.equals("120")) {//系统真正维护
                    mHandlers.sendEmptyMessage(2);
                    mHandlers.sendEmptyMessage(6);
                }
                adapter.notifyDataSetChanged();
            }
//            Misidentification.misidentification1(activity, status, jsonObject);//判断错误信息
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }

    }


    /**
     * 上拉加载跟多数据
     * 并显示上拉加载框
     */
    private void loadingDate() {

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 0 : 屏幕已停止
                        if (fullData) {//分页

                            listView.addFooterView(loadMoreView);

                            page++;//加载下一页数据
                            httsData(url);//数据请求

                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://2: 手指做了抛的动作

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //1: 表示正在滚动
                        break;
                }

            }

            /**
             * 分页判断
             * @param absListView
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                fullData = ((i + i1) == i2);
            }
        });


    }


    /**
     * 长按删除事件
     */
    private void longItemClick() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int postion, long l) {

                doLoginDialog(postion);//显示对话框
                return true;
            }
        });
    }

    /**
     * LitView点击事件
     */
    private  void itemClick() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapter.getCount() >= position) {
                    OrderInquiry.DataBean bean = (OrderInquiry.DataBean) adapter.getItem(position - 1);
                    if(orderListType==1) {//违章订单跳转详情页
                        mHandlers.sendEmptyMessage(2);
                        stars(bean, position);
                    }else if(orderListType==2){//年检订单跳转详情页
                        intent=new Intent(activity, AnnualDetailActivity.class);
                        intent.putExtra("ordercode",bean.getOrdercode() );
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }

    Intent intent;
    public void stars(OrderInquiry.DataBean bean, int position) {

        if (bean.getType().equals("5")) {//如果类型是其他类型订单
            if (bean.getTotalprice().equals("0")) {//如果该订单类型总价格为0就不让他不可点击
                ToastUtil.getLongToastByString(activity, "该订单未报价");
                return;
            } else {
                intent= new Intent(activity, OtherTheOrderActivity.class);//其他类型 跳到显示网页详情
                Bundle bundle = new Bundle();
                bundle.putSerializable("datalist", bean);
                intent.putExtra("bundle", bundle);
                activity.startActivity(intent);
            }
        } else {
            jump(bean, position);
        }


    }

    /**
     * 跳转页面
     */
    public void jump(OrderInquiry.DataBean bean, int position) {
        String status = bean.getStatus();
//        if (bean.getStatus().equals("未支付") && !StringUtils.isEmpty(bean.getIscommit())
//                && bean.getIscommit().equals("1")) {//去支付
//            Intent intent = new Intent(activity, OrderdetaiActivity2.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("datalist", bean);
//            intent.putExtra("bundle", bundle);
//            intent.putExtra("position", position);
//            activity.startActivity(intent);
//        } else {
//            Intent inter = new Intent(activity, OrderdetaiActivity3.class);
        Intent inter = new Intent(activity, OrderdetaiActivityV2.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("datalist", bean);
        inter.putExtra("bundle", bundle);
        activity.startActivity(inter);
//        }
//        if (status.equals("2") || status.equals("3") || status.equals("-1")) {//跳转到处理中和完成、撤销详的情页
//            Intent inter = new Intent(activity, OrderdetaiActivity3.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("datalist", bean);
//            inter.putExtra("bundle", bundle);
//            activity.startActivity(inter);
//
//        } else if (status.equals("1")) {//未支付
//            Intent intent = new Intent(activity, OrderdetaiActivity2.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("datalist", bean);
//            intent.putExtra("bundle", bundle);
//            intent.putExtra("position",position);
//            activity.startActivity(intent);
//        }
    }


    /**
     * 删除订单对话框
     *
     * @param
     */
    public void doLoginDialog(final int postion) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示");
        builder.setCancelable(false);
        builder.setMessage("是否删除信息");

        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteOrde(postion);//删除订单

            }
        });
        builder.show();
    }

    private HashMap<String, String> deleteOrdehHashMap;


    /**
     * 删除订单
     */
    public void deleteOrde(final int postion) {
        //从适配中拿到datalist；因为添加了头部下拉刷新，所有要postion要减一；
        OrderInquiry.DataBean datalist = (OrderInquiry.DataBean) adapter.getItem(postion - 1);
        //拿到适配器的一个订单中第一个违章的orderid，orderid用来删除订单
        String orderid = "";
        orderid = datalist.getOrdercode();
//        if (datalist.getType().equals("5")) {
//            orderid = datalist.getId();
//        } else {
//            orderid = datalist.getDetails().get(0).getOrderid();
//        }

        String orrdStatus = datalist.getStatusName();
        if (!orrdStatus.equals("未支付")) {
            ToastUtil.getShortToastByString(activity, orrdStatus + "状态下不能删除");
        } else {
            String token = TokenSQLUtils.check();
            deleteOrdehHashMap.put("token", token);
            deleteOrdehHashMap.put("ordercode", orderid);

            volleyUtil.StringRequestPostVolley(del_url, EncryptUtil.encrypt(deleteOrdehHashMap), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    Log.i("WU", jsonObject.toString());
                    try {
                        JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), activity));
                        String status = jsonObject1.getString("status");
                        if (status.equals("ok")) {
                            adapter.deleteDate(postion - 1);
                            ToastUtil.getLongToastByString(activity, "删除成功");
                            EventBus.getDefault().post(new FirstEvent("ok"));
                            MainFragment.positionList.clear();
                        } else {
                            if(jsonObject1.has("show_msg")){
                                String show_msg=jsonObject1.optString("show_msg");
                                Toast.makeText(activity,""+show_msg,Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
                }
            });
        }


    }

    /**
     * 当网络错误时显示这个页面
     */
    public void theNetworkStatus() {
        boolean networkConnected = TheNetworkStatuUtils.isNetworkConnected(activity);

        if (networkConnected == false || getNetSpeedBytes() < 20) {

            mHandlers.sendEmptyMessage(7);
//            adapter.notifyDataSetChanged();
            mHandlers.sendEmptyMessage(2);

        }

    }

    @Override
    public void onRefresh() {//下拉刷新
        Log.i("WU", "onRefresh");
        listView.setPullRefreshEnable(false);//启动下拉刷新功能
        mHandlers.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter.getCount() > 0) {

                    mHandlers.sendEmptyMessage(1);
                    onLoad();
                }

            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {


    }

    private CheckInTimeSQLUtils dateSQLUtils;

    private void onLoad() {//点击更多或上拉更多
        dateSQLUtils = new CheckInTimeSQLUtils(activity);
        listView.stopRefresh();
        listView.stopLoadMore();
        String date = dateSQLUtils.check();
        listView.setRefreshTime(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        if (date.equals("")) {
            dateSQLUtils.addDate(str);
        } else {
            dateSQLUtils.undateApi(str);
        }


    }

    /**
     * 通知刷新
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        if (event.getMsg().equals("ok")) {
            if (adapter.getCount() > 0) {//如果有数据就刷新第一页
                page = 1;//把加载的页面变为第一页
                beanList.clear();
                adapter.clearDate();
                httsData(url);
                Log.i("WU", "点单查询页面刷新了");
            } else {
                page = 1;//把加载的页面变为第一页
                Log.i("WU", "点单查询页面刷新了====");
                httsData(url);//当点击退出登录时，重新登录 加载数据
            }
        }
        if (event.getMsg().equals("no")) {
            deletecData();//退出时清处数据
            according();//显示未登录界面

        }
    }

    /**
     * 第二个主页面的 全选删除通知事件
     *
     * @param event
     */
    private boolean panduanguo;//用来判断当前界面 是否有勾选的订单

    @Subscribe
    public void onEvent(FirstEvent event) {

        if (event.getMsg().equals("quanxuan")) {//显示
            adapter.setIsLongClick(true);
//            adapter.notifyDataSetChanged();
            mHandlers.sendEmptyMessage(2);
        } else if (event.getMsg().equals("hie")) {
            adapter.setIsLongClick(false);
//            adapter.notifyDataSetChanged();
            mHandlers.sendEmptyMessage(2);
        } else if (event.getMsg().equals("delete")) {

            String batchDelete = "";//批量删除的订单号

            Map<Integer, Boolean> checkBoxMap = adapter.getCheckBox();
            if (adapter.getCount() > 0) {

                for (int i = 0; i < adapter.getCount(); i++) {//获取勾选的的订单ID

                    if (checkBoxMap.get(i)) {
                        panduanguo = true;
                        if (i == 0) {
                            batchDelete += adapter.getDatalist().get(i).getId();
                        } else {
                            batchDelete += "," + adapter.getDatalist().get(i).getId();
                        }
                        Log.i("WU", "" + checkBoxMap.get(i));
                        Log.i("WU", "" + adapter.getDatalist().get(i).getId());
                        Log.i("WU", "" + i);

                    }

                }
                if (panduanguo) {
                    httpsBatchRemove(batchDelete);
                    Log.i("WU", "batchDelete====>" + batchDelete);
                    panduanguo = false;
                }

                adapter.setIsLongClick(false);
//                adapter.notifyDataSetChanged();
                mHandlers.sendEmptyMessage(2);
            }

        }
    }

    /**
     * 提交要删除的批量订单号给后台
     */
    public void httpsBatchRemove(String data) {

        HashMap<String, String> pasMap = new HashMap<>();
        volleyUtil = VolleyUtil.getVolleyUtil(activity);//上网请求
        String token = tsl.check();
        pasMap.put("token", token);
        pasMap.put("orderids", data);
        volleyUtil.StringRequestPostVolley(URLs.BATCHREMOVE, pasMap, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                Log.i("WU", jsonObject.toString());

                deleteOrde(jsonObject.toString());

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(activity, "网络连接失败,无法发送请求");
            }
        });
    }

    public void deleteOrde(String jsonObject) {

        try {
            JSONObject json = new JSONObject(jsonObject);
            String status = json.getString("status");
            if (status.equals("ok")) {

                EventBus.getDefault().post(new FirstEvent("shua"));
                adapter.checkMap();//给适配器的checkBoxMap重新赋值
                for (int i = 0; i < adapter.getCount(); i++) {//把所有的CheckBox设置为不选择状态
                    CheckBox c = (CheckBox) listView.getChildAt(i).findViewById(R.id.isselect);
                    c.setChecked(false);
                }
            } else {
                Misidentification.misidentification1(activity, status, json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void onEve(FirstEvent event) {
        if (event.getMsg().equals("shua")) {
            page = 1;//把加载的页面变为第一页
            beanList.clear();
            adapter.clearDate();
//            adapter.notifyDataSetChanged();
//            mHandlers.sendEmptyMessage(2);
            httsData(url);
            Log.i("WU", "了");

        }

    }


    /**
     * 当退出登录时
     * 删除订单所有数据
     */
    public void deletecData() {

        try {
            StatusSQLUtils statusSQLUtils = new StatusSQLUtils(AppContext.getContext());

            if (statusSQLUtils.check().equals("yes")) {//判断登录状态

            } else if (statusSQLUtils.check().equals("no")) {//不是登录状态是数据全清空

                beanList.clear();
                adapter.clearDate();
//            adapter.notifyDataSetChanged();
                mHandlers.sendEmptyMessage(2);
            }
//            statusSQLUtils.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前网络速度
     *
     * @return
     */
    public int getNetSpeedBytes() {
        String line;
        String[] segs;
        int received = 0;
        int i;
        int tmp = 0;
        boolean isNum;
        try {
            FileReader fr = new FileReader("/proc/net/dev");
            BufferedReader in = new BufferedReader(fr, 500);
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("rmnet") || line.startsWith("eth") || line.startsWith("wlan")) {
                    segs = line.split(":")[1].split(" ");
                    for (i = 0; i < segs.length; i++) {
                        isNum = true;
                        try {
                            tmp = Integer.parseInt(segs[i]);
                        } catch (Exception e) {
                            isNum = false;
                        }
                        if (isNum == true) {
                            received = received + tmp;
                            break;
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            return -1;
        }
        return received;
    }

}
