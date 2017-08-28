package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.manager.SystemBarTintManager;
import com.example.youhe.youhecheguanjiaplus.utils.ClickUtils;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.MyListView;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * 未支付详情界面
 */
public class OrderdetaiActivity2 extends AppCompatActivity implements View.OnClickListener {
    private MyListView listView;
    private OrderdeListAdater orderdeListAdater;
    private TextView tv1,tv2,textorder;
    private ImageView imageViewDele;
    private TextView goTopay_tv;

    private TextView zhuangtaiView;
    private OrderInquiry.DataBean bean;
    private TokenSQLUtils tls;
    private VolleyUtil volleyUtil;//请求网络

    private SystemBarTintManager mTintManager;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetai2);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,OrderdetaiActivity2.this);
        }
        SystemBarUtil.useSystemBarTint(OrderdetaiActivity2.this);


        in();
    }

    private void in() {
        EventBus.getDefault().register(this);
        goTopay_tv = (TextView) findViewById(R.id.gotopay_tv);
        goTopay_tv.setOnClickListener(this);
        imageViewDele = (ImageView) findViewById(R.id.img_delete);
        imageViewDele.setOnClickListener(this);

        listView= (MyListView) findViewById(R.id.listview);
        zhuangtaiView = (TextView) findViewById(R.id.tv_zhuangtai);//状态显示
        tv1 = (TextView) findViewById(R.id.zonfa);//总罚金额
        tv2 = (TextView) findViewById(R.id.zonfuwu);//总服务费
        textorder = (TextView) findViewById(R.id.textorder);//订单号
        deleteOrdehHashMap = new HashMap<>();
        tls = new TokenSQLUtils(this);
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        Intent inetnt = getIntent();

        if(inetnt!=null){

            position=inetnt.getIntExtra("position",0);

            Bundle bundle  = inetnt.getBundleExtra("bundle");
//            bean = (OrderInquiry.DataBean) bundle.getSerializable("datalist");
            bean = (OrderInquiry.DataBean) bundle.getSerializable("datalist");
            orderdeListAdater = new OrderdeListAdater(this,bean);
            textorder.setText(bean.getOrdercode());//显示订单号
            listView.setAdapter(orderdeListAdater);
//            zon(bean);
            stateJudgment(bean.getStatus());//订单状态显示
        }
    }

    private int zonfakuan = 0;
    private int zonfuwu = 0;
    private int zenfen= 0;
    private int zonjiner = 0;
    private int latefee = 0;
    private String zo;
    public void zon(OrderInquiry.DataBean beans){//计算总价格

        for (int i = 0; i <beans.getDetails().size() ; i++) {//计算总罚金额和总服务费
            int f = Integer.parseInt(beans.getDetails().get(i).getCount());//得到罚款金额
            int fuwufei = Integer.parseInt(beans.getDetails().get(i).getPrice());//总服务费
            int zhonfw = Integer.parseInt(beans.getDetails().get(i).getDegree());//总扣分

            zonfuwu += fuwufei;//总服务费
            zonfakuan += f;//总罚款
            zenfen +=zhonfw;//总分
        }
        zonjiner = zonfuwu+ zonfakuan;//计算总价格
        NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);//数字金币格式化
//        zo= usFormat.format(bean.getTotalprice());

        tv1.setText(""+bean.getTotalprice());//总价格
        tv2.setText(zenfen+"");//总扣分

        for (int i = 0; i <bean.getDetails().size() ; i++) {
            if (bean.getDetails().get(i).getPrice().equals("0")){
                ToastUtil.getLongToastByString(this,"该订单未报价");
                goTopay_tv.setPressed(false);
                goTopay_tv.setClickable(false);
                goTopay_tv.setBackgroundResource(R.drawable.gotopay2);
                setPayForFailure();//提示信息
                return;
            }
        }
    }

    public void stateJudgment(String status){//判断状态
        if(status.equals("1")){

            zhuangtaiView.setText("未支付");

        }if(status.equals("2")){

            zhuangtaiView.setText("处理中");

        }if(status.equals("3")){

            zhuangtaiView.setText("处理完成");

        }if(status.equals("-1")){

            zhuangtaiView.setText("处理失败");

        }
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
        if (ClickUtils.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()){

            case R.id.gotopay_tv://去支付

                Intent intent = new Intent(this,PayActivity.class);
                intent.putExtra("zonfakuan",bean.getTotalprice());//总价格
                intent.putExtra("zonfuwu",zenfen);//总扣分
                intent.putExtra("ordernumber",bean.getOrdercode());//订单号
                intent.putExtra("totalprice",String.valueOf(zonfuwu));//总服务费
                intent.putExtra("totalCount",""+(Integer.valueOf(bean.getTotalprice())-zonfuwu));//总罚款
                intent.putExtra("integerzonjine",bean.getTotalprice());//整数的总金额
                intent.putExtra("type","1");//订单类型
                intent.putExtra("ordertype",1);//违章订单
                intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_ILLEGEL);
                startActivity(intent);

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
        String orderid  = bean.getDetails().get(0).getOrderid();
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
                        ToastUtil.getLongToastByString(OrderdetaiActivity2.this,"删除成功");


                        Intent intent=new Intent();
                        intent.putExtra("position",position);
                        intent.setAction("REFLUSH_ORDER_LIST");
                        sendBroadcast(intent);

                        MainFragment.qurery(MainFragment.queryGetParams(bean.getCarid()),bean.getCarnumber());//提交违章后刷新首页车辆的违章显示

                        finish();

                    }else {

                        Misidentification.misidentification1(OrderdetaiActivity2.this,status,jsonObject1);
                    }
                    EventBus.getDefault().unregister(this);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(OrderdetaiActivity2.this,"网络连接失败,无法发送请求");
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
                    zhuangtaiView.setText("未支付");
                    delect(orderid);

                }if(status.equals("2")){
                    zhuangtaiView.setText("处理中");
                    ToastUtil.getShortToastByString(OrderdetaiActivity2.this,"处理中订单不能删除");
                }if(status.equals("3")){
                    zhuangtaiView.setText("处理完成");
                    delect(orderid);
                }if(status.equals("-1")){
                    zhuangtaiView.setText("处理失败");
                    delect(orderid);
                }
            }
        });
        builder.show();
    }

    /**
     * 带报价提醒
     * @param successful
     *
     */
    private AlertDialog dialog;
    public void setPayForFailure(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //创建自定义的布局 View
        View loginView = getLayoutInflater().inflate(R.layout.prompt_layout, null);
        loginView.findViewById(R.id.queding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        builder.setView(loginView);

        dialog  = builder.show();
    }

    /**
     * 通知刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        if(event.getMsg().equals("finish")){
            finish();
        }
    }
}

