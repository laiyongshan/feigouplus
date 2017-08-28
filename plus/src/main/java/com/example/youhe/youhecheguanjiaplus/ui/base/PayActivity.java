package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.PayStyleAdapter;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.bean.Paytype;
import com.example.youhe.youhecheguanjiaplus.biz.IPPWDao;
import com.example.youhe.youhecheguanjiaplus.biz.ThePosPay;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ListViewHeightUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/9/2 0002.
 * 支付界面
 */
public class PayActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView pay_back_img, serviceFee, totalPrice, total_count_tv;
    public ListView pay_style_lv;
    private RelativeLayout relativeLayout;
    private PayStyleAdapter payStyleAdapter;
    private Button pay_btn;
    private CheckBox isread_cb;//是否同意服务协议
    private int selectId = 0;
    private ThePosPay thePosPayDao;//Pos机支付逻辑

    private RelativeLayout yu_e_layout;//可用余额
    private TextView yu_e_tv;
    private CheckBox yu_e_chBox;

    private String ordernumber;//订单号

    private List<Paytype> paytypeList=new ArrayList<Paytype>();

    PayTypeReceiver payTypeReceiver;
    IntentFilter mFilter;
    private final static String PAY_TYPE_ACTION="com_yeohe_payActivity_paytype_action";

    private int is_use_balance=1;//是否使用余额 0：不使用 1：使用
    private double balance_value;//用户可用余额多少
    private int paymoney;//应付费用

    private LinearLayout pay_info1_layout,pay_info2_layout;
    private int ordertype;//1：违章订单  2：补款订单  3：年检订单

    private TextView title_tv, bukuan_pice_tv,order_number_tv,shoppay_name_tv;//补款数额，订单编号

    public static final  String EXTRA_ORDER_TYPE="order_type";  //1违章2年检3车主卡
    public static final int ORDER_TYPE_ILLEGEL=1;
    public static final  int ORDER_TYPE_INSPECTION=2;
    public static final  int ORDER_TYPE_PLUS=3;

    private int order_type=ORDER_TYPE_ILLEGEL;//订单类型


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ordernumber = getIntent().getStringExtra("ordernumber");//得到订单号
        ordertype=getIntent().getIntExtra("ordertype",0);

        order_type=getIntent().getIntExtra(EXTRA_ORDER_TYPE,ORDER_TYPE_ILLEGEL);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, PayActivity.this);
        }
        SystemBarUtil.useSystemBarTint(PayActivity.this);

        initViews();//初始化控件

        payTypeReceiver=new PayTypeReceiver();
        mFilter=new IntentFilter(PAY_TYPE_ACTION);
        registerReceiver(payTypeReceiver,mFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ippwDao = new IPPWDao(this,order_type);//弹出支付
        if(ordertype==1) {
            ippwDao.checkPayment(this, ordernumber, getIntent().getStringExtra("zonfakuan"));//支付前校验
        }

        getPaytype(PayActivity.this, ordernumber,order_type+"");//获取支付类型;
    }

    private void initViews() {
        permissionToApplyFor();//读写权限申请
//        SignInDao signInDao = new SignInDao(PayActivity.this);
//        signInDao.judgmentSignIn();//签到下载工作密钥

        title_tv= (TextView) findViewById(R.id.title_tv);

        pay_info1_layout= (LinearLayout) findViewById(R.id.pay_info1_layout);
        pay_info2_layout= (LinearLayout) findViewById(R.id.pay_info2_layout);

        bukuan_pice_tv= (TextView) findViewById(R.id.bukuan_pice_tv);//补款数额
        order_number_tv= (TextView) findViewById(R.id.order_number_tv);//补款订单编号
        shoppay_name_tv=(TextView) findViewById(R.id.shoppay_name_tv);

        if(ordertype==1){
            title_tv.setText("支付订单");
            pay_info1_layout.setVisibility(View.VISIBLE);
            pay_info2_layout.setVisibility(View.GONE);
        }else if(ordertype==2){
            title_tv.setText("补款");
            pay_info1_layout.setVisibility(View.GONE);
            pay_info2_layout.setVisibility(View.VISIBLE);
            shoppay_name_tv.setText("需补款数：");
        }else if(ordertype==3){
            title_tv.setText("年检订单支付");
            pay_info1_layout.setVisibility(View.GONE);
            pay_info2_layout.setVisibility(View.VISIBLE);
            shoppay_name_tv.setText("应付金额：");
        }

        yu_e_layout= (RelativeLayout) findViewById(R.id.yu_e_layout);
        yu_e_tv= (TextView) findViewById(R.id.yu_e_tv);//可用余额
        yu_e_chBox= (CheckBox) findViewById(R.id.yu_e_chBox);//是否选择使用余额支付
        yu_e_chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    is_use_balance=1;
                }else{
                    is_use_balance=0;
                }
            }
        });

        relativeLayout = (RelativeLayout) findViewById(R.id.agreement);//协议界面
        serviceFee = (TextView) findViewById(R.id.service_fee);//总扣分
        totalPrice = (TextView) findViewById(R.id.tv_total_price);//总金额
        thePosPayDao = new ThePosPay(this);
        thePosPayDao.totalSetText(totalPrice, serviceFee,bukuan_pice_tv,order_number_tv);

        pay_back_img = (TextView) findViewById(R.id.pay_back_img);
        pay_back_img.setOnClickListener(this);
//        isUseYue_switch= (Switch) findViewById(R.id.isUseYue_switch);


        pay_btn = (Button) findViewById(R.id.pay_btn);
        pay_btn.setOnClickListener(this);

        isread_cb = (CheckBox) findViewById(R.id.isread_cb);
        isread_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("WU", "onCheckedChanged");
                if (b) {
                    pay_btn.setClickable(true);
                    pay_btn.setBackgroundResource(R.drawable.affirmbutton3);
                } else if (b == false) {
                    pay_btn.setClickable(false);
                    pay_btn.setBackgroundResource(R.drawable.tijiaoa2);
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent disIntent = new Intent(PayActivity.this, DisclaimerActivity.class);
                disIntent.putExtra("mianzhe", URLs.PAYMENT_AGREEMENT);
                disIntent.putExtra("title", "支付协议");
                startActivity(disIntent);
            }
        });

    }


    public int getPayStyle() {

        calculate_payment_amount(selectID,is_use_balance);

        return paytypeList.get(selectID).getMethod();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        payStyleAdapter.setSelectID(i);
        payStyleAdapter.notifyDataSetChanged();
    }

    /**
     * 立即支付按钮
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pay_back_img://回退键
                finish();
                break;
            case R.id.pay_btn://立即支付

//                String shouxufei = ThePosPay.getFwufei();//判断是否是12分为零服务费
//                Log.i("WU", "服务费" + shouxufei);
//                if (shouxufei.equals("0")) {
////                    ImageView imageView = (ImageView) findViewById(R.id.pay_btn);//如果是服务费为0就不可以去支付
//                    pay_btn.setClickable(false);
//                    pay_btn.setBackgroundResource(R.drawable.gotopay2);
////                        EventBus.getDefault().post(new FirstEvent("ok"));//通知订单查询刷新
//                } else {
//                    Log.i("WU", "服务费" + shouxufei);
                    termsOfPayment();//选择付款方式
//                }
                break;
        }
    }

    /**
     * 付款方式
     */
    private IPPWDao ippwDao;

    public void termsOfPayment() {
        IPPWDao.payType = getPayStyle();//得到支付类型

        if(is_use_balance==1) {//使用余额
            ThePosPay.is_balance_deductible=1;
          if(ordertype==3){//年检订单检测
                ippwDao.annualOrderCheck(PayActivity.this,IPPWDao.payType);
            }else{//违章订单检测
                ippwDao.orderCheck(PayActivity.this, IPPWDao.payType);
            }
        }else {//不使用余额
            ThePosPay.is_balance_deductible=0;
            ippwDao.judgeMachineType();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String judgeMachineType = Build.MODEL;
        if (judgeMachineType.equals("P92")) {

        } else {//如果是p84就要注销广播
            ippwDao.destroy();
        }

        unregisterReceiver(payTypeReceiver);
    }

    /**
     * 权限申请
     */
    private static final int TAKE_PHOTO_REQUEST_CODE = 2;

    public void permissionToApplyFor() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)//申请写入权限
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)//申请读权限
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    TAKE_PHOTO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                    startActivity(intent);
                }
                return;
            }
        }
    }


    //获取支付通道
    public void getPaytype(final Context context, String ordercode,String order_type) {
        UIHelper.showPd(PayActivity.this);

        paytypeList.clear();

        HashMap map = new HashMap();
        map.put("token", TokenSQLUtils.check() + "");
        map.put("ordercode", ordercode);
        map.put("order_type",order_type);
        if(ordertype==3){
            map.put("is_annual_inspection",1);//年检订单必传
        }
        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.GET_PAY_TYPE, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "获取用户支付通道返回的数据：" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), context));
                    JSONObject dataObj = obj.optJSONObject("data");
                    if (dataObj.has("mjOpenType")) {//扫码支付支持类型
                        CommentSetting.mjOpenType = dataObj.optInt("mjOpenType");
                    }

                    String client_wallet=dataObj.optString("client_wallet");
                    yu_e_layout.setVisibility(View.VISIBLE);
                    yu_e_tv.setText("总余额：￥"+client_wallet);
                    balance_value=Double.valueOf(client_wallet);

                    paymoney=dataObj.optInt("paymoney");//应付金额

                    Paytype paytype=null;
                    JSONArray paytypeArr=dataObj.getJSONArray("paytypes");
                    for(int i=0;i<paytypeArr.length();i++){
                        paytype=new Paytype();
                        paytype.setMethod(paytypeArr.getJSONObject(i).optInt("method"));
                        paytype.setIs_balance_deductible(paytypeArr.getJSONObject(i).optInt("is_balance_deductible"));
                        paytypeList.add(paytype);
                    }

                    show_paytype(paytypeList);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    UIHelper.dismissPd();
                    if(paytypeList.size()==0){
                        pay_btn.setClickable(false);
                        pay_btn.setBackgroundResource(R.drawable.gotopay2);
                    }
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(PayActivity.this, "支付通道紧急维护中，请稍候重试", Toast.LENGTH_LONG).show();
                pay_btn.setClickable(false);
                pay_btn.setBackgroundResource(R.drawable.tijiaoa2);
                UIHelper.dismissPd();
            }
        });
    }


    /**
     * 支付方式显示
     * */
    private void  show_paytype(List<Paytype> paytypeList){
        List<Integer> iconIds=new ArrayList<Integer>();
        List<String> texts=new ArrayList<String>();
        selectID=0;
        for(Paytype paytype:paytypeList){
            if(paytype.getMethod()==1){
                iconIds.add(R.drawable.yinlian);
                texts.add("银联支付");
            }else if(paytype.getMethod()==2){
                iconIds.add(R.drawable.yinlian);
                texts.add("银联支付");
            }else if(paytype.getMethod()==3){
                iconIds.add(R.drawable.erweimazhifu);
                texts.add("扫码支付");
            }else if(paytype.getMethod()==4){
                iconIds.add(R.drawable.qianbao);
                texts.add("余额支付");
            }
        }

        payStyleAdapter = new PayStyleAdapter(PayActivity.this, PayActivity.this, texts, iconIds,paytypeList);
        payStyleAdapter.notifyDataSetChanged();
        pay_style_lv = (ListView)findViewById(R.id.pay_style_lv);
        pay_style_lv.setAdapter(payStyleAdapter);
        ListViewHeightUtil.setListViewHeightBasedOnChildren(pay_style_lv);
        pay_style_lv.setOnItemClickListener(PayActivity.this);

        payStyleAdapter.setOncheckChanged(new PayStyleAdapter.OnMyCheckChangedListener() {
            @Override
            public void setSelectID(int selectID) {
                payStyleAdapter.setSelectID(selectID);//选中位置
                payStyleAdapter.notifyDataSetChanged();//刷新适配器
            }
        });

        selectPay(0);
        calculate_payment_amount(0,is_use_balance);
    }


    int selectID;
    class PayTypeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                selectID =intent.getIntExtra("selectID",-1);

                selectPay(selectID);
            }
        }
    }

    /**
     * 支付方式选择
     * */
    private void selectPay(int selectID){
        if(paytypeList.get(selectID).getIs_balance_deductible()==1){//支持余额抵扣
            yu_e_chBox.setChecked(true);
            yu_e_chBox.setEnabled(true);
            yu_e_layout.setVisibility(View.VISIBLE);
        }else if(paytypeList.get(selectID).getIs_balance_deductible()==2){//不支持余额抵扣
            if(paytypeList.get(selectID).getMethod()==4){//余额支付方式
                yu_e_chBox.setChecked(true);
                yu_e_chBox.setEnabled(false);
                yu_e_layout.setVisibility(View.VISIBLE);
            }else{//非余额支付方式
                yu_e_chBox.setChecked(false);
                yu_e_chBox.setEnabled(false);
                yu_e_layout.setVisibility(View.GONE);
                is_use_balance=0;
            }
        }
    }

    /**
     *计算支付金额
    */
    private void calculate_payment_amount(int selectID,int is_use_balance){

        if(paytypeList.get(selectID).getIs_balance_deductible()==1){//支持余额抵扣
            if(is_use_balance==1) {
                if(paytypeList.get(selectID).getMethod()==4){//余额支付方式
                    ThePosPay.payPrice=paymoney;
                }else{
                    ThePosPay.payPrice = paymoney - balance_value;
                }
            }else{
                ThePosPay.payPrice = paymoney;
            }
        }else if(paytypeList.get(selectID).getIs_balance_deductible()==2){//不支持余额抵扣
            if(paytypeList.get(selectID).getMethod()==4){//余额支付方式
                ThePosPay.payPrice=paymoney;
            }else{//非余额支付方式
                ThePosPay.payPrice=paymoney;
            }
        }

        Log.i("TAG",ThePosPay.payPrice+"<>"+paymoney);
    }
}
