package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderdeListAdater;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.MyListView;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 处理中、已成功、撤消订单详情界面
 */
public class OrderdetaiActivity3 extends AppCompatActivity implements View.OnClickListener {
    private MyListView listView;
    private OrderdeListAdater orderdeListAdater;
    private TextView tvTotalPrice, tvScore;
    private ImageView imageButton,imageViewDele;
    private TextView complete_tv;
    private TextView tvState,textorder;
    private OrderInquiry.DataBean bean;
    private TokenSQLUtils tls;
    private VolleyUtil volleyUtil;//请求网络

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetai3);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,OrderdetaiActivity3.this);
        }
        SystemBarUtil.useSystemBarTint(OrderdetaiActivity3.this);


        in();
    }

    private void in() {
        EventBus.getDefault().register(this);

//        imageButton = (ImageView) findViewById(R.id.imggg);
//        imageButton.setOnClickListener(this);

        complete_tv = (TextView)this.findViewById(R.id.complete_tv);
        complete_tv.setOnClickListener(this);
        imageViewDele = (ImageView) findViewById(R.id.img_delete);
        imageViewDele.setOnClickListener(this);
        listView= (MyListView) findViewById(R.id.listview);
        tvState = (TextView) findViewById(R.id.tv_zhuangtai);//状态显示
        tvTotalPrice = (TextView) findViewById(R.id.zonfa);//总罚金额
        tvScore = (TextView) findViewById(R.id.zonfuwu);//总服务费
        textorder = (TextView) findViewById(R.id.textorder);//订单号
        deleteOrdehHashMap = new HashMap<>();
        tls = new TokenSQLUtils(this);
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        Intent inetnt = getIntent();

        if(inetnt!=null){
            Bundle bundle  = inetnt.getBundleExtra("bundle");
            bean = (OrderInquiry.DataBean) bundle.getSerializable("datalist");
//            orderdeListAdater = new OrderdeListAdater(this,bean);
//            listView.setAdapter(orderdeListAdater);
//            zon(bean);
        }
        if (bean==null){
            finish();
            return;
        }
        initData();

    }

    private void initData() {
        loadDetail();
        tvTotalPrice.setText(bean.getTotalprice()!=null?bean.getTotalprice():"");//总价格
//        tvScore.setText(bean.get);
        textorder.setText(bean.getOrdercode());//显示订单号
        tvState.setText(bean.getStatus());
//        StateJudgment(bean.getStatus());//订单状态显示


    }

    private void loadDetail(){
        HashMap<String,String> loadMap=new HashMap<>();
        loadMap.put("token",TokenSQLUtils.check());
        loadMap.put("ordercode",bean.getOrdercode());
        volleyUtil.StringRequestPostVolley(URLs.GET_ORDER_DETAILS, EncryptUtil.encrypt(loadMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    Log.d("TAG",EncryptUtil.decryptJson(jsonObject.toString(),OrderdetaiActivity3.this));
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
                    Log.d("TAG","获取失败");
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.d("TAG",volleyError.toString());
                ToastUtil.getShortToastByString(OrderdetaiActivity3.this,"网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.complete_tv://完成
                finish();
                break;

            case R.id.img_delete://删除订单

                deleteOrde();

                break;

        }
    }


    private HashMap<String,String> deleteOrdehHashMap;
    /**
     * 删除订单
     */
    public void deleteOrde(){
//        String orderid  = bean.getDetails().get(0).getOrderid();
        String orderid  = bean.getId();
        String status = bean.getStatus();
        doLoginDialog(status,orderid);

    }
    public  void delect(String orderid){
        String token = TokenSQLUtils.check();
        deleteOrdehHashMap.put("token",token);
        deleteOrdehHashMap.put("orderid",orderid);

        volleyUtil.StringRequestPostVolley(URLs.DELETEORDE, deleteOrdehHashMap, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject jsonObject1 =new JSONObject(jsonObject.toString());
                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")){
                        EventBus.getDefault().post(new FirstEvent("ok"));
                        ToastUtil.getLongToastByString(OrderdetaiActivity3.this,"删除成功");
                    }else {
                        Misidentification.misidentification1(OrderdetaiActivity3.this,status,jsonObject1);
                    }
                    EventBus.getDefault().unregister(this);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(OrderdetaiActivity3.this,"网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 删除订单对话框
     * @param
     */
    public void doLoginDialog (final String status, final String orderid){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("提示");
        builder.setMessage("是否删除信息");

        builder.setNegativeButton("否",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status.equals("1")){
                    tvState.setText("未支付");
                    delect(orderid);

                }if(status.equals("2")){
                    tvState.setText("处理中");
                    ToastUtil.getShortToastByString(OrderdetaiActivity3.this,"不能删除");
                }if(status.equals("3")){
                    tvState.setText("处理完成");
                    ToastUtil.getShortToastByString(OrderdetaiActivity3.this,"不能删除");
                }if(status.equals("-1")){
                    tvState.setText("处理失败");
                    delect(orderid);
                }
            }
        });
        builder.show();
    }

    /**
     * 通知刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {


    }

}
