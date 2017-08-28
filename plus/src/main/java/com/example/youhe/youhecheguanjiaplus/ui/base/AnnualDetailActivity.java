package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderDetailStatusAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.AnnualDetailBean;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Json;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/15.
 */

public class AnnualDetailActivity extends Activity implements View.OnClickListener{

    private ImageView back_iv,annual_delete_iv;//返回，删除按钮
    private TextView ordercode_tv,carnumber_tv,paymoney_tv,price_tv,pay_now_tv;
    private TextView carowner_name_tv,carcode_tv,carEngine_tv,annual_check_tv,getgoods_address_tv;

    private TextView out_express_name_tv,out_express_code_tv;
    private TextView in_express_code_tv,in_express_name_tv;

    private TextView express_to_addrss_tv;
    private String address;

    private RelativeLayout emty_express_code_layout;//未添加寄出快递单号显示
    private LinearLayout out_express_layout,in_express_layout;//快递寄出，快递寄回,
    private LinearLayout yet_pay_layout;//已支付

    private AnnualDetailBean annualDetailBean;

    //状态信息
    private OrderDetailStatusAdapter statusAdapter;
    private GridView gridView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AnnualDetailActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AnnualDetailActivity.this);
        setContentView(R.layout.activity_annual_order_detail);

        initViews();//初始化控件

        annualDetailBean=new AnnualDetailBean();
        statusAdapter = new OrderDetailStatusAdapter(this);

        getOrderDetatil(getIntent().getStringExtra("ordercode")==null?"":getIntent().getStringExtra("ordercode"));
    }


    /*
    * 初始化控件
    * */
    private void initViews(){
        EventBus.getDefault().register(this);

        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this);

        annual_delete_iv= (ImageView) findViewById(R.id.annual_delete_iv);
        annual_delete_iv.setOnClickListener(this);

        ordercode_tv= (TextView) findViewById(R.id.ordercode_tv);
        carnumber_tv= (TextView) findViewById(R.id.carnumber_tv);
        paymoney_tv= (TextView) findViewById(R.id.paymoney_tv);
        price_tv= (TextView) findViewById(R.id.price_tv);
        pay_now_tv= (TextView) findViewById(R.id.pay_now_tv);
        pay_now_tv.setOnClickListener(this);

        carowner_name_tv= (TextView) findViewById(R.id.carowner_name_tv);
        carcode_tv= (TextView) findViewById(R.id.carcode_tv);
        carEngine_tv= (TextView) findViewById(R.id.carEngine_tv);
        annual_check_tv= (TextView) findViewById(R.id.annual_check_tv);
        getgoods_address_tv= (TextView) findViewById(R.id.getgoods_address_tv);

        gridView= (GridView) findViewById(R.id.grid);

        out_express_layout= (LinearLayout) findViewById(R.id.out_express_layout);
        out_express_layout.setOnClickListener(this);
        in_express_layout= (LinearLayout) findViewById(R.id.in_express_layout);
        in_express_layout.setOnClickListener(this);

        out_express_name_tv=(TextView)findViewById(R.id.out_express_name_tv);
        out_express_code_tv= (TextView) findViewById(R.id.out_express_code_tv);

        in_express_code_tv= (TextView) findViewById(R.id.in_express_code_tv);
        in_express_name_tv= (TextView) findViewById(R.id.in_express_name_tv);

        yet_pay_layout= (LinearLayout) findViewById(R.id.yet_pay_layout);

        emty_express_code_layout= (RelativeLayout) findViewById(R.id.emty_express_code_layout);
        emty_express_code_layout.setOnClickListener(this);

        express_to_addrss_tv= (TextView) findViewById(R.id.express_to_addrss_tv);
    }


    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView() {
        if (annualDetailBean.getOrderStatusList() == null || annualDetailBean.getOrderStatusList().size() == 0)
            return;
        int size = annualDetailBean.getOrderStatusList().size();
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

        statusAdapter.setData(annualDetailBean.getOrderStatusList());
        gridView.setAdapter(statusAdapter);
//        startAnima();
    }


    Intent intent;
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.back_iv://返回键
                finish();
                break;

            case R.id.annual_delete_iv://删除订单
                deleteOrde();
                break;

            case R.id.out_express_layout:
                intent=new Intent(AnnualDetailActivity.this,ExpressCheckActivity.class);
                intent.putExtra("express",annualDetailBean.getClientexpress());
                startActivity(intent);
                break;

            case R.id.in_express_layout:
                intent=new Intent(AnnualDetailActivity.this,ExpressCheckActivity.class);
                intent.putExtra("express",annualDetailBean.getServerexpress());
                startActivity(intent);
                break;

            case R.id.emty_express_code_layout://跳转添加快递单号界面
                intent=new Intent(AnnualDetailActivity.this,AddExpressCodeActivity.class);
                intent.putExtra("ordercoder",annualDetailBean.getOrdercode());
                AnnualDetailActivity.this.startActivityForResult(intent,10);
                break;

            case R.id.pay_now_tv://立即支付
                intent=new Intent(AnnualDetailActivity.this,PayActivity.class);
                intent.putExtra("ordernumber",annualDetailBean.getOrdercode());
                intent.putExtra("ordertype",3);
                intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_INSPECTION);
                intent.putExtra("zonfakuan",annualDetailBean.getTotalprice());
                startActivity(intent);
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==123){
            if(data!=null) {
                getOrderDetatil(data.getStringExtra("ordercode")+"");
            }
        }
    }

    private void getOrderDetatil(String ordercode){

        UIHelper.showPd(AnnualDetailActivity.this);

        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("ordercode",ordercode);

        VolleyUtil.getVolleyUtil(AnnualDetailActivity.this).StringRequestPostVolley(URLs.ANNUAL_ORDER_DETAIL, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    Log.d("TAG", EncryptUtil.decryptJson(jsonObject.toString(), AnnualDetailActivity.this));
                    String data = EncryptUtil.decryptJson(jsonObject.toString(), AnnualDetailActivity.this);
                    JSONObject object = new JSONObject(data);
                    if (object.has("status") && object.getString("status").equals("ok")) {
//                        JSONObject dataJson=object.getJSONObject("data");
                        if (object.has("data")) {
                            annualDetailBean = Json.get().toObject(object.getString("data"), AnnualDetailBean.class);
//
                            if (annualDetailBean.getOrderStatusList() != null) {
                                setGridView();
                            }
                            setData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.d("TAG", volleyError.toString());
                ToastUtil.getShortToastByString(AnnualDetailActivity.this, "网络连接失败,无法发送请求");
            }
        });
    }



    private void setData(){
        ordercode_tv.setText(annualDetailBean.getOrdercode());
        carnumber_tv.setText(annualDetailBean.getProprefix()+annualDetailBean.getCarnumber());
        carcode_tv.setText(annualDetailBean.getCarcode());
        carEngine_tv.setText(annualDetailBean.getCardrivenumber());
        carowner_name_tv.setText(annualDetailBean.getName());
        paymoney_tv.setText("￥"+annualDetailBean.getTotalprice());
        price_tv.setText("￥"+annualDetailBean.getTotalprice());
        annual_check_tv.setText(annualDetailBean.getCheckyear_day());
        getgoods_address_tv.setText(annualDetailBean.getServer_address());

        express_to_addrss_tv.setText(annualDetailBean.getAddress()+"");

        if(annualDetailBean.getStatus().equals("1")&&annualDetailBean.getTimeout_status()!=1){//待支付且未过期
            pay_now_tv.setVisibility(View.VISIBLE);
            pay_now_tv.setClickable(true);
        }else {
            pay_now_tv.setVisibility(View.GONE);
            pay_now_tv.setClickable(false);
        }

        if(!annualDetailBean.getStatus().equals("1")){
            yet_pay_layout.setVisibility(View.VISIBLE);
        }else{
            yet_pay_layout.setVisibility(View.GONE);
        }


        if(annualDetailBean.getClientexpress()!=null){//已经添加了寄出快递单号
            out_express_layout.setVisibility(View.VISIBLE);
            emty_express_code_layout.setVisibility(View.GONE);
            out_express_code_tv.setText("单号："+annualDetailBean.getClientexpress());
            out_express_name_tv.setText("寄出快递："+annualDetailBean.getClientexpress_name());
        }else if(annualDetailBean.getTimeout_status()!=1&&!annualDetailBean.getStatus().equals("1")){//未过期
            out_express_layout.setVisibility(View.GONE);
            emty_express_code_layout.setVisibility(View.VISIBLE);
        }

        if(annualDetailBean.getServerexpress()!=null){//已经添加了寄回快递单号
            in_express_layout.setVisibility(View.VISIBLE);
            in_express_code_tv.setText("单号："+annualDetailBean.getServerexpress());
            in_express_name_tv.setText("寄回快递："+annualDetailBean.getServerexpress_name());
        }
    }


    private HashMap deleteOrdehHashMap=new HashMap();
    /**
     * 删除订单
     */
    public void deleteOrde() {
//        String orderid  = bean.getDetails().get(0).getOrderid();
        String orderid = annualDetailBean.getOrdercode();
        String status = annualDetailBean.getStatus();
        doLoginDialog(status, orderid);

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
                if (status.equals("1")) {
                    delect(orderid);
                } else {
                    ToastUtil.getShortToastByString(AnnualDetailActivity.this, status + "状态下不能删除");
                }
            }
        });
        builder.show();
    }


    public void delect(String orderid) {
        String token = TokenSQLUtils.check();
        deleteOrdehHashMap.put("token", token);
        deleteOrdehHashMap.put("ordercode", orderid);

        VolleyUtil.getVolleyUtil(AnnualDetailActivity.this).StringRequestPostVolley(URLs.ANNUAL_DEL, EncryptUtil.encrypt(deleteOrdehHashMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject jsonObject1 = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), AnnualDetailActivity.this));
                    String status = jsonObject1.getString("status");
                    if (status.equals("ok")) {
                        EventBus.getDefault().post(new FirstEvent("ok"));
                        ToastUtil.getLongToastByString(AnnualDetailActivity.this, "删除成功");
                    } else {
                       if(jsonObject1.has("show_msg")){
                           ToastUtil.getLongToastByString(AnnualDetailActivity.this, jsonObject1.optString("show_msg"));
                       }
                    }
                    EventBus.getDefault().unregister(this);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.getLongToastByString(AnnualDetailActivity.this, "删除失败");
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(AnnualDetailActivity.this, "网络连接失败,无法发送请求");
            }
        });
    }

    /**
     * 通知刷新
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }

}
