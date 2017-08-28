package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderDetailListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.ui.base.OrderStyleActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.PayActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class OrderDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private OrderDetailListAdapter orderDetailListAdapter;
    private List<Violation> violationList;
    private ListView order_detail_lv;
    private TextView orderdetail_back_img;
    private Button topay_btn;//去支付按钮

    private String carid = "";
    private int searchtype=0;

    private int totalprice = 0;
    private int totaldegree = 0;
    private TextView totalprice_tv;
    private TextView totaldegree_tv;
    private TextView total_count_tv;
    private String ordercode;//普通订单编号

    private int totalservicefee=0;//总服务费
    private int totalNormalDegree=0;//普通违章的总扣分

    private Window window = null;

    public OrderDialog(Activity activity, int themeResId, List<Violation> violationList,
                       int totalprice, int totaldegree,int totalservicefee,int totalNormalDegree,String ordercode,String carid,int searchtype) {
        super(activity, themeResId);
        this.activity = activity;
        this.violationList = violationList;
        this.totalprice = totalprice;
        this.totaldegree = totaldegree;
        this.carid = carid;
        this.searchtype=searchtype;
        this.ordercode=ordercode;
        this.totalservicefee=totalservicefee;
        this.totalNormalDegree=totalNormalDegree;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_order_detail);
        orderDetailListAdapter = new OrderDetailListAdapter(activity, violationList);
        setCancelable(false);
        Log.i("TAG","总服务费："+totalservicefee+"");

        initViews();//初始化控件
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initViews() {
        order_detail_lv = (ListView) findViewById(R.id.order_detail_lv);
        order_detail_lv.setAdapter(orderDetailListAdapter);
        orderdetail_back_img = (TextView) findViewById(R.id.orderdetail_back_img);
        orderdetail_back_img.setOnClickListener(this);
        topay_btn = (Button) findViewById(R.id.topay_btn);
        topay_btn.setOnClickListener(this);

//        total_count_tv= (TextView) findViewById(R.id.total_count_tv);
//        total_count_tv.setText("罚款:￥"+(totalprice-totalservicefee));

        totalprice_tv = (TextView) findViewById(R.id.totalprice_tv);
        totalprice_tv.setText("总金额:￥" + totalprice);
        totaldegree_tv = (TextView) findViewById(R.id.totaldegree_tv);
        totaldegree_tv.setText("总扣分:" + totaldegree + "分");
        if(totalprice==0){
            topay_btn.setText("待报价");
            totalprice_tv.setText("总金额:"+"待报价" );
            topay_btn.setClickable(false);
            topay_btn.setBackgroundResource(R.drawable.tijiaoa2);
        }
    }

    public void showDialog() {
        windowDeploy();
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(false);
        show();
    }


    //设置窗口显示动画
    public void windowDeploy() {
        window = getWindow(); //得到对话框
        if(window!=null) {
            window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
            window.setBackgroundDrawableResource(R.color.lucency2); //设置对话框背景为半透明
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
//            wl.x = x; //x小于0左移，大于0右移
//            wl.y = y; //y小于0上移，大于0下移
            // wl.alpha = 0.5f; //设置透明度
            wl.gravity = Gravity.CENTER; //设置重力
            window.setAttributes(wl);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.orderdetail_back_img:
                dismiss();
                Intent orederIntent=new Intent(activity, OrderStyleActivity.class);
                activity.startActivity(orederIntent);
                activity.finish();
                break;
            case R.id.topay_btn:
                if(totalprice==0){
                    Toast.makeText(activity,"用户等待系统报价后，到主界面\"订单查询\"中的未支付界面中选择订单支付",Toast.LENGTH_LONG).show();
                }else {
                    dismiss();
                    Intent intent = new Intent(activity, PayActivity.class);
//                    NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);//数字金币格式化
                    intent.putExtra("zonfakuan",totalprice+"");//总金额
                    intent.putExtra("zonfuwu", totalNormalDegree);//总扣分
                    intent.putExtra("totalprice", "" + getZonPrice());//总手续费
                    intent.putExtra("totalCount",""+(totalprice-totalservicefee));//总罚款
                    intent.putExtra("ordernumber",ordercode);//一般违章订单号
                    Log.i("WU", "ordercode==>" + ordercode);
                    intent.putExtra("integerzonjine",""+totalprice);//整数的总金额
                    Log.i("WU", "整数的总金额==>" + totalprice);
                    intent.putExtra("type", "1");//订单类型
                    intent.putExtra("ordertype",1);//违章订单
                    intent.putExtra(PayActivity.EXTRA_ORDER_TYPE,PayActivity.ORDER_TYPE_ILLEGEL);
                    activity.startActivity(intent);
                    activity.finish();
                }
                break;
        }
    }

    private int getZonPrice;
    public int getZonPrice(){//得到提交订单的总手续费

        for (int i = 0; i <violationList.size() ; i++) {
           int getPrice =  violationList.get(i).getPrice();
             getZonPrice +=  getPrice;
        }
        Log.i("WU",""+getZonPrice);

        return getZonPrice;
    }
}
