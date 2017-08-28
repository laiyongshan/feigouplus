package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.AlipayAuthentDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.SmsCodeDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 注册精准查询账号
 * Created by Administrator on 2017/4/13 0013.
 */

public class RegisterAccountActivit extends Activity implements View.OnClickListener{

    private ImageView register_account_back_img;
    private Button register_next1_btn;
    private TextView register_account_erro_msg;

    private TextView register_sevice_tv;

    private TextView license_tv;
    private EditText id_num_et,id_name_et,id_psw_et,id_agine_psw_et;
    private CheckBox isread_cb;

    private String userType,provice;
    private String mobile,plateType,plateNumber;
    private String smsnotice,cityname,plateCity;
    private String vehicleName,vehicleType;


    private final int PROVINCE_REQUEST_CODE=1221;
    private final int CITY_REQUEST_CODE=1222;
    private final int LICENSE_REQUEST_CODE=1223;

    private final int AUTH_FAIL_RESULTCODE=122201;
    private final int AUTH_SUCCESS_RESULTCODE=122202;

    private IntentFilter intentFilter;
    private AlipayAuthentBrocast alipayAuthentBrocast;//支付宝认证结果广播接受者

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,RegisterAccountActivit.this);
        }
        SystemBarUtil.useSystemBarTint(RegisterAccountActivit.this);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.ALIPAY_AUTHENT");
        alipayAuthentBrocast=new AlipayAuthentBrocast();
        registerReceiver(alipayAuthentBrocast,intentFilter);//注册支付宝认证结果广播接收者

        Intent intent=getIntent();
        userType=intent.getStringExtra("userType");
        plateCity=intent.getStringExtra("plateCity");
        mobile=intent.getStringExtra("mobile");
        plateType=intent.getStringExtra("plateType");
        plateNumber=intent.getStringExtra("plateNumber");//车牌号码

        initView();//初始化控件
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alipayAuthentBrocast);
    }

    private void initView(){
        register_account_back_img= (ImageView) findViewById(R.id.register_account_back_img);
        register_account_back_img.setOnClickListener(this);

        register_next1_btn= (Button) findViewById(R.id.register_next1_btn);
        register_next1_btn.setOnClickListener(this);

        register_account_erro_msg= (TextView) findViewById(R.id.register_account_erro_msg);

//        province_tv= (TextView) findViewById(R.id.province_tv);
//        province_tv.setOnClickListener(this);
//        city_tv= (TextView) findViewById(R.id.city_tv);
//        city_tv.setOnClickListener(this);
        license_tv= (TextView) findViewById(R.id.license_tv);
        license_tv.setOnClickListener(this);

        id_num_et= (EditText) findViewById(R.id.id_num_et);//证件号码
        id_name_et= (EditText) findViewById(R.id.id_name_et);//姓    名
//        user_phone_et= (EditText) findViewById(R.id.user_phone_et);//手机号码
        id_psw_et= (EditText) findViewById(R.id.id_psw_et);//密    码
        id_agine_psw_et= (EditText) findViewById(R.id.id_agine_psw_et);//确认密码


        isread_cb= (CheckBox) findViewById(R.id.isread_cb);
        isread_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("WU","onCheckedChanged");
                if (b){
                    register_next1_btn.setClickable(true);
                    register_next1_btn.setBackgroundResource(R.drawable.affirmbutton3);
                }else if (b==false){
                    register_next1_btn.setClickable(false);
                    register_next1_btn.setBackgroundResource(R.drawable.tijiaoa2);
                }
            }
        });

        register_sevice_tv= (TextView) findViewById(R.id.register_sevice_tv);
        register_sevice_tv.setOnClickListener(this);
    }


    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_account_back_img:
                finish();
                break;

            case R.id.register_next1_btn:
                if(id_num_et.getText().toString().trim().equals("")||id_name_et.getText().toString().trim().equals("") ||
                        id_psw_et.getText().toString().trim().equals("")||id_agine_psw_et.getText().toString().trim().equals("")){
                    Toast.makeText(RegisterAccountActivit.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                }else if(!id_psw_et.getText().toString().trim().equals(id_agine_psw_et.getText().toString().trim())){
                    Toast.makeText(RegisterAccountActivit.this,"两次密码输入不一致！",Toast.LENGTH_LONG).show();
                }else{
                    UIHelper.showPd(RegisterAccountActivit.this);
                    submitRegister();//提交注册信息
                }
                break;

//            case R.id.province_tv://选择身份
//                intent=new Intent(RegisterAccountActivit.this,ListActivity.class);
//                intent.putExtra("title","选择注册省份");
//                startActivityForResult(intent,PROVINCE_REQUEST_CODE);
//                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
//                break;
//
//            case R.id.city_tv://选择注册城市
//                intent=new Intent(RegisterAccountActivit.this,RegisterCityListActivity.class);
//                intent.putExtra("userType",userType);
//                startActivityForResult(intent,CITY_REQUEST_CODE);
//                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
//                break;

            case R.id.license_tv://选择证件类型
                intent=new Intent(RegisterAccountActivit.this,RegistgerLicenseActivity.class);
                startActivityForResult(intent,LICENSE_REQUEST_CODE);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.register_sevice_tv:
                intent=new Intent(RegisterAccountActivit.this,CommentWebActivity.class);
                intent.putExtra("url", URLs.REGISTER_122_SERVICE_DISCLAIMER);
                intent.putExtra("title","授权服务协议");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==PROVINCE_REQUEST_CODE){
//            if(data!=null){
//                userType=data.getStringExtra("code");
//                provice=data.getStringExtra("name");
//                if(provice!=null) {
//                    province_tv.setText(provice);
//                    province_tv.setTextColor(Color.BLACK);
//                }
//            }
//        }else if(requestCode==CITY_REQUEST_CODE){
//            if(data!=null){
//                plateCity=data.getStringExtra("proprefix");
//                cityname=data.getStringExtra("cityname");
//                smsnotice=data.getStringExtra("smsnotice");
//                if(provice!=null) {
//                    city_tv.setText(cityname);
//                    city_tv.setTextColor(Color.BLACK);
//                }
//            }
//        }else
        if(requestCode==LICENSE_REQUEST_CODE){
            if(data!=null){
                vehicleName=data.getStringExtra("vehicleName");
                vehicleType=data.getStringExtra("vehicleType");
                if(vehicleName!=null){
                    license_tv.setText(vehicleName);
                    license_tv.setTextColor(Color.BLACK);

                }
            }
        }

//        if(resultCode==AUTH_FAIL_RESULTCODE){
//            Toast.makeText(RegisterAccountActivit.this,"用户在第三方实名认证结果不符合办理该业务的条件",Toast.LENGTH_LONG).show();
//            register_account_erro_msg.setText("用户在第三方实名认证结果不符合办理该业务的条件,请检查后重试");
//            Log.i("TAG","用户在第三方实名认证结果不符合办理该业务的条件,请检查后重试");
//            register_account_erro_msg.setVisibility(View.VISIBLE);
//        }else if(resultCode==AUTH_SUCCESS_RESULTCODE){
//            Toast.makeText(RegisterAccountActivit.this,"安全认证成功",Toast.LENGTH_LONG).show();
//            Log.i("TAG","安全认证成功");
//            smsCodeDialog=new SmsCodeDialog(RegisterAccountActivit.this,R.style.Dialog,userType);
//            smsCodeDialog.showDialog();
//            register_account_erro_msg.setVisibility(View.INVISIBLE);
//        }

    }


    HashMap map;
    SmsCodeDialog smsCodeDialog;
    AlipayAuthentDialog alipayAuthentDialog;
    private void submitRegister(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);
        map.put("vehicleType",vehicleType);
        map.put("vehicleAccount",id_num_et.getText().toString().trim());
        map.put("username",id_name_et.getText().toString().trim());
        map.put("mobile",mobile);
        map.put("plateCity",plateCity);
        map.put("plateType",plateType);
        map.put("plateNumber",plateNumber);
        map.put("password", ParamSign.getUserPassword(id_psw_et.getText().toString().trim()));

        VolleyUtil.getVolleyUtil(RegisterAccountActivit.this).StringRequestPostVolley(URLs.SUBMIT_REGISTER_122, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                String json=EncryptUtil.decryptJson(jsonObject.toString(),RegisterAccountActivit.this);
                try {
                    JSONObject obj=new JSONObject(json);
                    String status=obj.optString("status");
                    if(status.equals("fail")) {
//                        smsCodeDialog=new SmsCodeDialog(RegisterAccountActivit.this,R.style.Dialog,userType);
//                        smsCodeDialog.showDialog();
                        int code=obj.optInt("code");
                        String show_msg=obj.optString("show_msg");
                        if(code==90014){
                            JSONObject error_data=obj.getJSONObject("error_data");
                            String userIdentityUrl=error_data.optString("userIdentityUrl");
                            String host=error_data.optString("host");
                            JSONObject cookies=error_data.optJSONObject("cookies");
                            String tmri_csfr_token=cookies.optString("tmri_csfr_token");
                            String JSESSIONID_L=cookies.optString("JSESSIONID-L");
                            String userpub=cookies.optString("userpub");
                            String insert_cookie=cookies.optString("insert_cookie");

                            String cookieArray[]={tmri_csfr_token,JSESSIONID_L,userpub,insert_cookie};

                            alipayAuthentDialog=new AlipayAuthentDialog(RegisterAccountActivit.this,R.style.Dialog,userIdentityUrl,host,cookieArray,show_msg);
                            alipayAuthentDialog.showDialog();

                            register_account_erro_msg.setVisibility(View.GONE);

                        }else {
                            Toast.makeText(RegisterAccountActivit.this, show_msg, Toast.LENGTH_SHORT).show();
                            register_account_erro_msg.setText(show_msg+"");
                            register_account_erro_msg.setVisibility(View.VISIBLE);
                        }
                    }else if(status.equals("ok")){
//                        Toast.makeText(RegisterAccountActivit.this, "精准查询账号注册成功", Toast.LENGTH_SHORT).show();
                        smsCodeDialog=new SmsCodeDialog(RegisterAccountActivit.this,R.style.Dialog,userType);
                        smsCodeDialog.showDialog();
                        register_account_erro_msg.setVisibility(View.GONE);
//                        finish();
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


    //支付宝认证结果的广播接收者
    class AlipayAuthentBrocast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent!=null) {
                int resultCode=intent.getIntExtra("resultCode",0);
                if (resultCode == AUTH_FAIL_RESULTCODE) {
                    Toast.makeText(RegisterAccountActivit.this, "用户在第三方实名认证结果不符合办理该业务的条件", Toast.LENGTH_LONG).show();
                    register_account_erro_msg.setText("用户在第三方实名认证结果不符合办理该业务的条件,请检查后重试");
                    register_account_erro_msg.setVisibility(View.VISIBLE);
                } else if (resultCode == AUTH_SUCCESS_RESULTCODE) {
                    Toast.makeText(RegisterAccountActivit.this, "安全认证成功", Toast.LENGTH_LONG).show();
                    smsCodeDialog = new SmsCodeDialog(RegisterAccountActivit.this, R.style.Dialog, userType);
                    smsCodeDialog.showDialog();
                    register_account_erro_msg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
