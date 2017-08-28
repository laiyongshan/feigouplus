package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class AccountChangePswActivity extends Activity implements View.OnClickListener{

    private ImageView back_img;
    private TextView  province_tv;
    private EditText id_num_et,id_name_et;
    private Button  next_btn;

    private TextView erro_msg_1,erro_msg_2;

    private TimeButton timeButton;
    private EditText smscode_et,password_et,password_again_et;
    private TextView obligate_phonenum_tv;//预留手机号码
    private Button change_psw_btn;

    private LinearLayout account_change_psw1,account_change_psw2;

    private String userType,provice;
    private String vehicleAccount;
    private String mobile="";

    private final int PROVINCE_REQUEST_CODE=12200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_psw);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AccountChangePswActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AccountChangePswActivity.this);

        initView();//初始化控件
        timeButton(savedInstanceState);

    }

    private void initView(){
        back_img= (ImageView) findViewById(R.id.account_change_psw_back_img);
        back_img.setOnClickListener(this);
        province_tv= (TextView) findViewById(R.id.province_tv);
        province_tv.setOnClickListener(this);
        id_num_et= (EditText) findViewById(R.id.id_num_et);
        id_name_et= (EditText) findViewById(R.id.id_name_et);
        next_btn= (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(this);

        smscode_et= (EditText) findViewById(R.id.smscode_et);
        password_et= (EditText) findViewById(R.id.password_et);
        password_again_et= (EditText) findViewById(R.id.password_again_et);
        obligate_phonenum_tv= (TextView) findViewById(R.id.obligate_phonenum_tv);
        change_psw_btn= (Button) findViewById(R.id.change_psw_btn);
        change_psw_btn.setOnClickListener(this);

        account_change_psw1= (LinearLayout) findViewById(R.id.account_change_psw1);
        account_change_psw2= (LinearLayout) findViewById(R.id.account_change_psw2);

        erro_msg_1= (TextView) findViewById(R.id.erro_msg_1);
        erro_msg_2= (TextView) findViewById(R.id.erro_msg_2);

    }

    private void timeButton(Bundle savedInstanceState) {
        //获取验证码键
        timeButton = (TimeButton) findViewById(R.id.time_btn);
        timeButton.onCreate(savedInstanceState);
        timeButton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(30 * 1000);
        timeButton.setOnClickListener(this);
    }

    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.account_change_psw_back_img:
                finish();
                break;

            case R.id.province_tv://省份选择
                intent=new Intent(AccountChangePswActivity.this,ListActivity.class);
                intent.putExtra("title","选择省份");
                startActivityForResult(intent,PROVINCE_REQUEST_CODE);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.next_btn://下一步
                if(id_num_et.getText().toString().trim().equals("")||id_name_et.getText().toString().trim().equals("")){
                    Toast.makeText(AccountChangePswActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else {
                    UIHelper.showPd(AccountChangePswActivity.this);
                    //提交找回密码用户信息
                    submitAccount();
                }
                break;

            case R.id.change_psw_btn://确定
                if(smscode_et.getText().toString().trim().equals("")
                        ||password_et.getText().toString().trim().equals("")||password_again_et.getText().toString().trim().equals("")){
                    Toast.makeText(AccountChangePswActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if(!password_et.getText().toString().trim().equals(password_again_et.getText().toString().trim())){
                    Toast.makeText(AccountChangePswActivity.this,"两次输入密码不一致！",Toast.LENGTH_SHORT).show();
                }else{
                    UIHelper.showPd(AccountChangePswActivity.this);
                    updateFindPwd();//提交找回密码的新密码
                }
                break;

            case R.id.time_btn://获取短信验证码
                sendFindPwdSmsCode();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PROVINCE_REQUEST_CODE){
            if(data!=null){
                userType=data.getStringExtra("code");
                provice=data.getStringExtra("name");
                if(provice!=null) {
                    province_tv.setText(provice);
                    province_tv.setTextColor(Color.BLACK);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    HashMap map;
    private void submitAccount(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);
        vehicleAccount=id_num_et.getText().toString().trim();
        map.put("vehicleAccount",vehicleAccount);
        map.put("username",id_name_et.getText().toString().trim());
        VolleyUtil.getVolleyUtil(AccountChangePswActivity.this).StringRequestPostVolley(URLs.SUBMIT_ACCOUNT, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG",jsonObject.toString());
                try {
                    JSONObject object=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountChangePswActivity.this));
                    String status=object.optString("status");
                    if(status.equals("fail")) {
                        String show_msg = object.optString("show_msg");
                        Toast.makeText(AccountChangePswActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();
                        erro_msg_1.setVisibility(View.VISIBLE);
                        erro_msg_1.setText(show_msg+"");
                    }else if(status.equals("ok")){
                        JSONObject dataObj=object.getJSONObject("data");
                        mobile=dataObj.optString("mobile");
                        account_change_psw1.setVisibility(View.INVISIBLE);
                        account_change_psw2.setVisibility(View.VISIBLE);
                        erro_msg_1.setVisibility(View.GONE);
                        obligate_phonenum_tv.setText("预留号码："+ StringUtils.showNum(mobile));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                erro_msg_1.setVisibility(View.VISIBLE);
                erro_msg_1.setText("网络连接超时，请检查后重试");
            }
        });
    }


    /**
     * 发送找回密码短信验证码
     * */
    private void sendFindPwdSmsCode(){
        map=new HashMap();
        map.put("token",TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);
        VolleyUtil.getVolleyUtil(AccountChangePswActivity.this).StringRequestPostVolley(URLs.SEND_FIND_PSW_SMSCODE, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountChangePswActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(AccountChangePswActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();

                    }else if(status.equals("ok")){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }


    /**
     * 提交找回密码的新密码
     * */
    private void updateFindPwd(){
        map=new HashMap();
        map.put("token",TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);
        map.put("password", ParamSign.getUserPassword(password_et.getText().toString().trim()));
        map.put("smsCode",smscode_et.getText().toString().trim());
        VolleyUtil.getVolleyUtil(AccountChangePswActivity.this).StringRequestPostVolley(URLs.UPDATA_FIND_PWD, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountChangePswActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(AccountChangePswActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();
                        erro_msg_2.setVisibility(View.VISIBLE);
                        erro_msg_2.setText(show_msg+"");
                    }else if(status.equals("ok")){
                        erro_msg_2.setVisibility(View.GONE);
                        Toast.makeText(AccountChangePswActivity.this,"新密码设置成功",Toast.LENGTH_SHORT).show();
                        finish();
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

}
