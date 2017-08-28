package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Annual;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.CommitSucessDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.DoubtDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.PrefixDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.AllCapTransformationMethod;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AnnualInspectionActivity extends Activity implements View.OnClickListener{

    private ImageView back_iv;
    private ImageView pack_up_iv1,pack_up_iv2,pack_up_iv3;
    private LinearLayout process_layout1,process_layout2,process_layout3;
    private CheckBox isread_cb;
    private Button commit_annual_btn;
    private EditText getgoods_address_et;
    private TextView price_tv;//价格
    private TextView annual_disclaimer_tv;

    private TextView express_to_addrss_tv;//指定邮寄地址

    private EditText car_number_et,carEngine_et,carowner_name_et,phone_num_et,CarCode_et,carowner_idcardnum_et;

    private ImageView carEngine_doubt_img,carCode_doubt_img;

    private TextView prefix_tv;//车辆前缀
    private PrefixDialog prefixDialog;
    private ProvinceBrocast provinceBrocast;//接收选择的身份简称
    public String prefix="";//省份前缀
    public String price="";//价格
    private List<Province> provinces=new ArrayList<Province>();
    private List<Annual> annualList = new ArrayList<Annual>();

    private boolean flag1=false,flag2=false,flag3=false;

    AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod();//字母大写

    private TextView datapicker_tv;
    private int mYear,mMonth,mDay;
    final int DATE_DIALOG = 1;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AnnualInspectionActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AnnualInspectionActivity.this);

        setContentView(R.layout.activity_annual_inspection);

        IntentFilter filter=new IntentFilter(PrefixDialog.PREFIX_ACTION_NAME);
        provinceBrocast=new ProvinceBrocast();
        registerReceiver(provinceBrocast,filter);

//        initPrefixdata();//初始化省份数据
        getSupportedProvinces();//获取年检支持的省份城市及价格

        initViews();//初始化控件

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        context=AnnualInspectionActivity.this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(provinceBrocast);
    }

    //初始化控件
    private void initViews(){
        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this);

        pack_up_iv1= (ImageView) findViewById(R.id.pack_up_iv1);
        pack_up_iv1.setOnClickListener(this);
        pack_up_iv2= (ImageView) findViewById(R.id.pack_up_iv2);
        pack_up_iv2.setOnClickListener(this);
        pack_up_iv3= (ImageView) findViewById(R.id.pack_up_iv3);
        pack_up_iv3.setOnClickListener(this);

        process_layout1= (LinearLayout) findViewById(R.id.process_layout1);
        process_layout2= (LinearLayout) findViewById(R.id.process_layout2);
        process_layout3= (LinearLayout) findViewById(R.id.process_layout3);

        car_number_et= (EditText) findViewById(R.id.car_number_et);
        car_number_et.setTransformationMethod(allCapTransformationMethod);

        car_number_et.setTransformationMethod(allCapTransformationMethod);

        prefix_tv= (TextView)findViewById(R.id.prefix_tv);
        prefix_tv.setOnClickListener(this);

        commit_annual_btn= (Button) findViewById(R.id.commit_annual_btn);
        commit_annual_btn.setOnClickListener(this);

        isread_cb= (CheckBox) findViewById(R.id.isread_cb);
        isread_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    commit_annual_btn.setClickable(true);
                    commit_annual_btn.setBackgroundColor(getColor(R.color.new_color_primary));
                } else if (isChecked == false) {
                    commit_annual_btn.setClickable(false);
                    commit_annual_btn.setBackgroundColor(getColor(R.color.gray));
                }
            }
        });

        getgoods_address_et= (EditText) findViewById(R.id.getgoods_address_et);
        price_tv= (TextView) findViewById(R.id.price_tv);

        CarCode_et= (EditText) findViewById(R.id.CarCode_et);
        CarCode_et.setTransformationMethod(allCapTransformationMethod);
        carEngine_et= (EditText) findViewById(R.id.carEngine_et);
        carEngine_et.setTransformationMethod(allCapTransformationMethod);
        carowner_name_et= (EditText) findViewById(R.id.carowner_name_et);
        phone_num_et= (EditText) findViewById(R.id.phone_num_et);
        carowner_idcardnum_et= (EditText) findViewById(R.id.carowner_idcardnum_et);

        annual_disclaimer_tv= (TextView) findViewById(R.id.annual_disclaimer_tv);
        annual_disclaimer_tv.setOnClickListener(this);

        datapicker_tv= (TextView) findViewById(R.id.datapicker_tv);//年审日期选择
        datapicker_tv.setOnClickListener(this);

        carEngine_doubt_img= (ImageView) findViewById(R.id.carEngine_doubt_img);
        carEngine_doubt_img.setOnClickListener(this);
        carCode_doubt_img= (ImageView) findViewById(R.id.carCode_doubt_img);
        carCode_doubt_img.setOnClickListener(this);

        express_to_addrss_tv= (TextView) findViewById(R.id.express_to_addrss_tv);

    }

    DoubtDialog doubtDialog=null;
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_iv:
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;

            case R.id.pack_up_iv1:
                flag1=!flag1;
                if(flag1){
                    process_layout1.setVisibility(View.GONE);
                    flag1=true;
                    pack_up_iv1.setImageResource(R.drawable.category_iv_oneitem_arrow_down);
                }else{
                    process_layout1.setVisibility(View.VISIBLE);
                    flag1=false;
                    pack_up_iv1.setImageResource(R.drawable.category_iv_oneitem_arrow_up);
                }
                break;

            case R.id.pack_up_iv2:
                flag2=!flag2;
                if(flag2){
                    process_layout2.setVisibility(View.GONE);
                    pack_up_iv2.setImageResource(R.drawable.category_iv_oneitem_arrow_down);
                    flag2=true;
                }else{
                    process_layout2.setVisibility(View.VISIBLE);
                    flag2=false;
                    pack_up_iv2.setImageResource(R.drawable.category_iv_oneitem_arrow_up);
                }
                break;

            case R.id.pack_up_iv3:
                flag3=!flag3;
                if(flag3){
                    process_layout3.setVisibility(View.GONE);
                    flag3=true;
                    pack_up_iv3.setImageResource(R.drawable.category_iv_oneitem_arrow_down);
                }else{
                    process_layout3.setVisibility(View.VISIBLE);
                    flag3=false;
                    pack_up_iv3.setImageResource(R.drawable.category_iv_oneitem_arrow_up);
                }
                break;

            case R.id.prefix_tv://车辆前缀
                prefixDialog = new PrefixDialog(AnnualInspectionActivity.this, R.style.Dialog,provinces);
                prefixDialog.showDialog();
                prefixDialog.setCancelable(true);
                break;

            case R.id.annual_disclaimer_tv://用户协议
                Intent intent=new Intent(AnnualInspectionActivity.this,CommentWebActivity.class);
                intent.putExtra("url", URLs.ANNUALINSPECTIONT_DISCLAIMER);
                intent.putExtra("title","用户服务协议");
                startActivity(intent);
                break;

            case R.id.datapicker_tv://年审时间选择
                showDialog(DATE_DIALOG);
                break;

            case R.id.commit_annual_btn://提交年检订单

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                if(prefix_tv.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint1),Toast.LENGTH_SHORT).show();
                }else if(car_number_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint2),Toast.LENGTH_SHORT).show();
                }else if(CarCode_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint3),Toast.LENGTH_SHORT).show();
                }else if(carEngine_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint4),Toast.LENGTH_SHORT).show();
                }else if(datapicker_tv.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint5),Toast.LENGTH_SHORT).show();
                }else if(carowner_name_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint6),Toast.LENGTH_SHORT).show();
                }else if(phone_num_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint7),Toast.LENGTH_SHORT).show();
                }else if(getgoods_address_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint8),Toast.LENGTH_SHORT).show();
                }else if(carowner_idcardnum_et.getText().toString().trim().length()<4){
                    Toast.makeText(context,getResources().getString(R.string.annual_hint9),Toast.LENGTH_SHORT).show();
                }else{
                    addAnnualOrder();//提交年检订单
                }
                break;

            case R.id.carEngine_doubt_img:
                doubtDialog = new DoubtDialog(AnnualInspectionActivity.this, R.style.Dialog, R.drawable.carengine_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;

            case R.id.carCode_doubt_img:
                doubtDialog = new DoubtDialog(AnnualInspectionActivity.this, R.style.Dialog, R.drawable.carcode_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;

        }
    }

    /*
  * 车牌前缀的广播接收者
  * */
    class ProvinceBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                prefix = intent.getStringExtra("prefix");
                prefix_tv.setText(prefix+"");
                price =intent.getStringExtra("price");
                price_tv.setText("￥"+price);
            }
            prefixDialog.dismiss();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        datapicker_tv.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };


    /**
     * 获取年检支持的省份城市及价格
     * */
    String address;//指定邮寄地址
    private  void getSupportedProvinces(){
        UIHelper.showPd(AnnualInspectionActivity.this);
        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(AnnualInspectionActivity.this).StringRequestPostVolley(URLs.GET_SUPPORTED_PROVINCES, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG",jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AnnualInspectionActivity.this));
                    if(obj.has("data")){
                        JSONObject dataObj = obj.optJSONObject("data");

                        address=dataObj.optString("address");
                        express_to_addrss_tv.setText(address+"");

                        JSONArray proArr=dataObj.optJSONArray("provinces");
                        JSONObject o;
                        Province province;

                        for(int i=0;i<proArr.length();i++){
                            o=proArr.getJSONObject(i);
                            String prifex = o.optString("province");//车牌前缀
                            province = new Province();
                            province.setProvincePrefix(prifex);

                            JSONArray detailsArr=o.optJSONArray("details");
                            Annual annual;
                            annualList=new ArrayList<Annual>();
                            for(int j=0;j<detailsArr.length();j++){
                                annual=new Annual();
                                annual.setLetter(detailsArr.optJSONObject(j).optString("zimu"));
                                annual.setPrice(detailsArr.optJSONObject(j).optString("price"));
                                annualList.add(annual);
                                if(j==0){
                                    prefix_tv.setText(prifex+detailsArr.optJSONObject(j).optString("zimu"));
                                    price=detailsArr.optJSONObject(j).optString("price");
                                    price_tv.setText("￥"+price);
                                }
                            }
                            province.setAnnualList(annualList);
                            provinces.add(province);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }

    /**
     * 添加年检订单
     * */
    private void addAnnualOrder(){
        UIHelper.showPd(AnnualInspectionActivity.this);
        HashMap addMap=new HashMap();
        addMap.put("token", TokenSQLUtils.check());
        addMap.put("proprefix",prefix_tv.getText().toString().trim());//粤A
        addMap.put("carnumber",car_number_et.getText().toString().trim().toUpperCase());//车牌号码
        addMap.put("cardrivenumber",carEngine_et.getText().toString().trim().toUpperCase());//发动机号
        addMap.put("carcode",CarCode_et.getText().toString().trim().toUpperCase());//车架号后六位
        addMap.put("name",carowner_name_et.getText().toString().trim());//客户姓名
        addMap.put("mobile",phone_num_et.getText().toString().trim());//客户电话
        addMap.put("checkyear_day",datapicker_tv.getText().toString().trim());//年审日
        addMap.put("server_address",getgoods_address_et.getText().toString().trim());//收件地址
        addMap.put("id_card",carowner_idcardnum_et.getText().toString().trim()+"");//车主身份证号码后4位

//        addMap.put("orderMoney",price);//订单金额
        addMap.put("orderMoney",price);//订单金额
        VolleyUtil.getVolleyUtil(AnnualInspectionActivity.this).StringRequestPostVolley(URLs.ADD_ANNUAL_ORDER, EncryptUtil.encrypt(addMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),context));
                    String status=obj.optString("status");
                    if(status.equals("ok")){
                        if(obj.has("data")){
                            JSONObject dataObj=obj.optJSONObject("data");
                            String ordercode=dataObj.optString("ordercode");//订单号
                            String server_address=dataObj.optString("server_address");//收件地址
                            CommitSucessDialog commitSucessDialog=new CommitSucessDialog(AnnualInspectionActivity.this, R.style.Dialog,ordercode,3,price);
                            commitSucessDialog.showDialog();
                        }
                        Toast.makeText(context,"提交年检订单成功",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(context,"提交年检订单失败，"+show_msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }



}
