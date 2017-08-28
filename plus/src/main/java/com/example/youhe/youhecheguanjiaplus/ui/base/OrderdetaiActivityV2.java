package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderDetailStatusAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.bean.OrderDeatilBean;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.AnimManager;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Json;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 处理中、已成功、撤消订单详情界面
 */
public class OrderdetaiActivityV2 extends AppCompatActivity implements View.OnClickListener {
    private TextView tvTotalPrice, tvService, tvFine, tvDelay, tvCount, tvPriceStr;
    private TextView textDetail;
    private TextView textFillingMoney;
    private ImageView imageViewDele;
    private TextView complete_tv;
    private TextView textorder;
    private TextView textCarNumber;
    private View view_has_money;
    private LinearLayout linear_has_money;
    private TextView text_has_money, text_has_money_str;
    private OrderInquiry.DataBean bean;
    private TokenSQLUtils tls;
    private VolleyUtil volleyUtil;//请求网络
    private OrderDeatilBean orderDeatilBean;
    private RelativeLayout layoutDetail, layoutFilling_money;
    //状态信息
    private OrderDetailStatusAdapter statusAdapter;
    //    private ArrayList<OrderDeatilBean.OrderStatusListBean> statusList;
    private GridView gridView;

    private int ordertype;//订单类型 1：普通订单  2：补款订单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetai_v2);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, OrderdetaiActivityV2.this);
        }
        SystemBarUtil.useSystemBarTint(OrderdetaiActivityV2.this);

        in();
    }

    private void in() {
        EventBus.getDefault().register(this);

        complete_tv = (TextView) this.findViewById(R.id.complete_tv);
        complete_tv.setOnClickListener(this);
        imageViewDele = (ImageView) findViewById(R.id.img_delete);
        imageViewDele.setOnClickListener(this);
        textDetail = (TextView) findViewById(R.id.text_detail);
        textFillingMoney = (TextView) findViewById(R.id.text_filling_money);
//        textDetail.setOnClickListener(this);
        layoutDetail = (RelativeLayout) findViewById(R.id.layout_detail);
        layoutDetail.setOnClickListener(this);
        layoutFilling_money = (RelativeLayout) findViewById(R.id.layout_filling_money);
        layoutFilling_money.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.grid);
        //补款
        linear_has_money = (LinearLayout) findViewById(R.id.linear_has_money);
        text_has_money = (TextView) findViewById(R.id.text_has_money);
        text_has_money_str = (TextView) findViewById(R.id.text_has_money_str);
        view_has_money = findViewById(R.id.view_has_money);

        tvTotalPrice = (TextView) findViewById(R.id.text_price);//总金额
        tvPriceStr = (TextView) findViewById(R.id.text_price_str);//总金额文字
        tvService = (TextView) findViewById(R.id.text_service);//总服务费
        textorder = (TextView) findViewById(R.id.textorder);//订单号
        tvFine = (TextView) findViewById(R.id.text_fine);//总罚金额
        tvDelay = (TextView) findViewById(R.id.text_delay);//总滞纳金
        tvCount = (TextView) findViewById(R.id.text_count);//总分
        textCarNumber = (TextView) findViewById(R.id.carnumber);//车牌号
        deleteOrdehHashMap = new HashMap<>();
        tls = new TokenSQLUtils(this);
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求

        Intent inetnt = getIntent();
        ordertype=inetnt.getIntExtra("ordertype",1);//默认普通订单
        if (inetnt != null) {
            Bundle bundle = inetnt.getBundleExtra("bundle");
            bean = (OrderInquiry.DataBean) bundle.getSerializable("datalist");
//            orderdeListAdater = new OrderdeListAdater(this,bean);
//            listView.setAdapter(orderdeListAdater);
//            zon(bean);
        }
        if (bean == null) {
            finish();
            return;
        }

        statusAdapter = new OrderDetailStatusAdapter(this);

        initData();

    }

    private void initData() {
        loadDetail();
        tvTotalPrice.setText(bean.getTotalprice() != null ? (bean.getTotalprice().equals("0") ? "待报价" : "¥" + bean.getTotalprice()) : "");//总价格
        textorder.setText(bean.getOrdercode());//显示订单号
        //车牌号
        textCarNumber.setText(bean.getCarnumber() == null ? "" : (bean.getProprefix() == null ? "" : bean.getProprefix()) + bean.getCarnumber());
//        StateJudgment(bean.getStatus());//订单状态显示
        if ((bean.getStatus() != null && bean.getStatus().equals("1"))
                && (bean.getIscommit() != null && bean.getIscommit().equals("1"))) {
            complete_tv.setText("立即支付");
            complete_tv.setVisibility(View.VISIBLE);
        } else {
            complete_tv.setVisibility(View.GONE);
        }
    }

    private void setData() {
        if (orderDeatilBean != null) {
            tvFine.setText(orderDeatilBean.getAllCount() == null ? "" : "¥" + orderDeatilBean.getAllCount());//总罚款
            tvService.setText((orderDeatilBean.getAllPrice() == null || orderDeatilBean.getAllPrice().equals("0")) ? "待报价" : "¥" + orderDeatilBean.getAllPrice());//总服务费
            tvDelay.setText(orderDeatilBean.getAllLatefine() == null ? "" : "¥" + orderDeatilBean.getAllLatefine());//总滞纳金
            tvCount.setText(orderDeatilBean.getAllDegree() == null ? "" : orderDeatilBean.getAllDegree() + "分");//总扣分
        }
        //违章
        if (orderDeatilBean != null && orderDeatilBean.getPeccancyList() != null && orderDeatilBean.getPeccancyList().size() > 0) {
            textDetail.setText("共" + orderDeatilBean.getPeccancyList().size() + "条 >");
            layoutDetail.setClickable(true);
        } else {
            textDetail.setText("共0条");
            layoutDetail.setClickable(false);
        }

        //补款
//        if (orderDeatilBean != null && orderDeatilBean.getMakeUpMoneyList() != null && orderDeatilBean.getMakeUpMoneyList().size() > 0) {
//            layoutFilling_money.setVisibility(View.VISIBLE);
//            textFillingMoney.setText("共" + orderDeatilBean.getMakeUpMoneyList().size() + "条 >");
//            layoutFilling_money.setClickable(true);
//        } else {
//            layoutFilling_money.setVisibility(View.GONE);
//        }
        if (orderDeatilBean.getOrderStatus().equals("23") || orderDeatilBean.getOrderStatus().equals("24")) {//待补款
            linear_has_money.setVisibility(View.VISIBLE);
            view_has_money.setVisibility(View.VISIBLE);
            if (orderDeatilBean != null && orderDeatilBean.getMakeUpMoneyList() != null && orderDeatilBean.getMakeUpMoneyList().size() > 0) {
                layoutFilling_money.setVisibility(View.VISIBLE);
                textFillingMoney.setText("共" + orderDeatilBean.getMakeUpMoneyList().size() + "条 >");
                layoutFilling_money.setClickable(true);
            }

            if (orderDeatilBean.getOrderStatus().equals("23"))
                text_has_money.setText(StringUtils.isEmpty(orderDeatilBean.getPaid_mony()) ? "" : ("¥" + orderDeatilBean.getPaid_mony()));
            else if (orderDeatilBean.getOrderStatus().equals("24")){
                text_has_money_str.setText("已补款金额");
                text_has_money.setText(StringUtils.isEmpty(orderDeatilBean.getHas_made_money()) ? "" : ("¥" + orderDeatilBean.getHas_made_money()));
            }

            if (orderDeatilBean.getOrderStatus().equals("23")) {
                tvPriceStr.setText("待补款：");
                tvTotalPrice.setText("¥" + orderDeatilBean.getTo_make_money() + "");
                complete_tv.setText("立即支付");
                complete_tv.setVisibility(View.VISIBLE);
            }
        } else {
            linear_has_money.setVisibility(View.GONE);
            view_has_money.setVisibility(View.GONE);
            layoutFilling_money.setVisibility(View.GONE);
        }
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView() {
        if (orderDeatilBean.getOrderStatusList() == null || orderDeatilBean.getOrderStatusList().size() == 0)
            return;
        int size = orderDeatilBean.getOrderStatusList().size();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int itemWidth = dm.widthPixels / 3;
//        int gridviewWidth = (int) (size * (length + 4) * density);
        int gridviewWidth = itemWidth * size;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(0); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        statusAdapter.setData(orderDeatilBean.getOrderStatusList());
        gridView.setAdapter(statusAdapter);


//        startAnima();
    }

    private void startAnima() {
        try {
            List<Animation> anims = new ArrayList<Animation>();
            if (statusAdapter.getViews() != null && statusAdapter.getViews().size() > 0) {
                for (int i = 0; i < statusAdapter.getViews().size(); i++) {
                    anims.add(AnimationUtils.loadAnimation(this, R.anim.left_to_right));
                }
                AnimManager manager = new AnimManager(statusAdapter.getViews(), anims);
                manager.setOnEndListener(new AnimManager.OnEndListener() {
                    @Override
                    public void onStart(Animation animation, View view) {

                    }

                    @Override
                    public void onEnd(Animation animation, View view) {

                    }
                });
                manager.startAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void loadDetail() {
        UIHelper.showPd(OrderdetaiActivityV2.this);
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("token", TokenSQLUtils.check());
        loadMap.put("ordercode", bean.getOrdercode());
        volleyUtil.StringRequestPostVolley(URLs.GET_ORDER_DETAILS, EncryptUtil.encrypt(loadMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    Log.d("TAG", EncryptUtil.decryptJson(jsonObject.toString(), OrderdetaiActivityV2.this));
                    String data = EncryptUtil.decryptJson(jsonObject.toString(), OrderdetaiActivityV2.this);
                    JSONObject object = new JSONObject(data);
                    if (object.has("status") && object.getString("status").equals("ok")) {
//                        JSONObject dataJson=object.getJSONObject("data");
                        if (object.has("data")) {
                            orderDeatilBean = Json.get().toObject(object.getString("data"), OrderDeatilBean.class);

                            if (orderDeatilBean.getOrderStatusList() != null) {
                                setGridView();
                            }
                            setData();
                        }
                    }
//                    JSONObject jsonObject1 =new JSONObject(jsonObject.toString());
//                    String status = jsonObject1.getString("status");
//                    if (status.equals("ok")){
//                        EventBus.getDefault().post(new FirstEvent("ok"));
//                        ToastUtil.getLongToastByString(OrderdetaiActivity3.this,"删除成功");
//                    }else {
//                        Misidentification.misidentification1(OrderdetaiActivity3.this,status,jsonObject1);
//                    }
//                    EventBus.getDefault().unregister(this);
//                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "获取失败");
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.d("TAG", volleyError.toString());
                ToastUtil.getShortToastByString(OrderdetaiActivityV2.this, "网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 返回键
     *
     * @param view
     */
    public void fanhui(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.complete_tv://去支付  待付款
                if ((bean.getStatus() != null && bean.getStatus().equals("1")
                        && bean.getIscommit() != null && bean.getIscommit().equals("1"))
                        || orderDeatilBean.getOrderStatus().equals("23")) {
                    Intent intent = new Intent(this, PayActivity.class);
                    String price = orderDeatilBean.getOrderStatus().equals("23") ? orderDeatilBean.getTo_make_money() : bean.getTotalprice();
                    if (orderDeatilBean.getOrderStatus().equals("23"))//是否是补款  1
                        intent.putExtra("is_make_up_money", true);

                    intent.putExtra("zonfakuan", price);//总价格
                    int a = 0;
                    try {
                        a = Integer.parseInt(orderDeatilBean.getAllDegree());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("zonfuwu", a);//总扣分  int
                    intent.putExtra("ordernumber", bean.getOrdercode());//订单号
                    intent.putExtra("totalprice", String.valueOf(price));//总服务费
//                    intent.putExtra("totalCount",""+(Integer.valueOf(bean.getTotalprice())-zonfuwu));//总罚款
                    intent.putExtra("integerzonjine", price);//整数的总金额
                    intent.putExtra("type", "1");//订单类型
                    intent.putExtra("ordertype",ordertype);//订单类型
                    intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_ILLEGEL);
                    startActivity(intent);
                    OrderdetaiActivityV2.this.finish();
                }
                break;

            case R.id.img_delete://删除订单
                deleteOrde();
                break;
            case R.id.layout_detail://违章
                if (orderDeatilBean != null && orderDeatilBean.getPeccancyList() != null && orderDeatilBean.getPeccancyList().size() > 0) {
                    Intent intent = new Intent(OrderdetaiActivityV2.this, OrderDetailSubActivity.class);
                    intent.putExtra("list", Json.get().toJson(orderDeatilBean.getPeccancyList()));
                    startActivity(intent);
                }
                break;
            case R.id.layout_filling_money://补款
                if (orderDeatilBean != null && orderDeatilBean.getMakeUpMoneyList() != null && orderDeatilBean.getMakeUpMoneyList().size() > 0) {
                    Intent intent = new Intent(OrderdetaiActivityV2.this, OrderFillingListActivity.class);
                    intent.putExtra("list", Json.get().toJson(orderDeatilBean.getMakeUpMoneyList()));
                    startActivity(intent);
                }
                break;

        }
    }


    private HashMap<String, String> deleteOrdehHashMap;

    /**
     * 删除订单
     */
    public void deleteOrde() {
//        String orderid  = bean.getDetails().get(0).getOrderid();
        String orderid = bean.getOrdercode();
        String status = bean.getStatusName();
        doLoginDialog(status, orderid);

    }

    public void delect(String orderid) {
        String token = TokenSQLUtils.check();
        deleteOrdehHashMap.put("token", token);
        deleteOrdehHashMap.put("ordercode", orderid);

        volleyUtil.StringRequestPostVolley(URLs.DELETEORDE, EncryptUtil.encrypt(deleteOrdehHashMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
//                    Log.d("TAG",jsonObject.toString());
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), OrderdetaiActivityV2.this));
                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        EventBus.getDefault().post(new FirstEvent("ok"));
                        ToastUtil.getLongToastByString(OrderdetaiActivityV2.this, "删除成功");
                    } else {
                        Misidentification.misidentification1(OrderdetaiActivityV2.this, status, jsonObject1);
                    }
                    EventBus.getDefault().unregister(this);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.getLongToastByString(OrderdetaiActivityV2.this, "删除失败");
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(OrderdetaiActivityV2.this, "网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 删除订单对话框
     *
     * @param
     */
    public void doLoginDialog(final String status, final String orderid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("提示");
        builder.setMessage("是否删除信息");

        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status.equals("未支付")) {
                    delect(orderid);
                } else {
                    ToastUtil.getShortToastByString(OrderdetaiActivityV2.this, status + "状态下不能删除");
                }
//                }if(status.equals("2")){
////                    tvState.setText("处理中");
//                    ToastUtil.getShortToastByString(OrderdetaiActivityV2.this,"不能删除");
//                }if(status.equals("3")){
////                    tvState.setText("处理完成");
//                    ToastUtil.getShortToastByString(OrderdetaiActivityV2.this,"不能删除");
//                }if(status.equals("-1")){
////                    tvState.setText("处理失败");
//                    delect(orderid);
//                }
            }
        });
        builder.show();
    }

    /**
     * 通知刷新
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        loadDetail();
    }

}
