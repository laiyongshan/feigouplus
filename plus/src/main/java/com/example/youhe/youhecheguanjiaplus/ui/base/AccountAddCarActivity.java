package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.VerificationCodeDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.AllCapTransformationMethod;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class AccountAddCarActivity extends Activity implements View.OnClickListener{

    private TextView account_change_psw_back_img,account_car_type_tv,erro_msg;
    private EditText car_number_et;
    private Button account_add_car_btn;

    private String plateType,plateTypename;
    private String vehicleAccount="";
    private String userType="";
    private String captchaCode="";

    AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod();//字母大写

    private final static int CHOOSE_CAR_TYPE_REQQUESTCODE=12202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_addcar);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AccountAddCarActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AccountAddCarActivity.this);

        vehicleAccount=getIntent().getStringExtra("vehicleAccount");
        userType=getIntent().getStringExtra("userType");

        initView();//初始化控件
    }

    /**
     * 初始化控件
     * */
    private void initView(){
        account_change_psw_back_img= (TextView) findViewById(R.id.account_change_psw_back_img);
        account_change_psw_back_img.setOnClickListener(this);

        account_car_type_tv= (TextView) findViewById(R.id.account_car_type_tv);
        account_car_type_tv.setOnClickListener(this);

        erro_msg= (TextView) findViewById(R.id.erro_msg);

        car_number_et= (EditText) findViewById(R.id.car_number_et);
        car_number_et.setTransformationMethod(allCapTransformationMethod);//字母大写

        account_add_car_btn= (Button) findViewById(R.id.account_add_car_btn);
        account_add_car_btn.setOnClickListener(this);
    }

    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.account_change_psw_back_img:
                finish();
                break;
            case R.id.account_car_type_tv:
                intent=new Intent(AccountAddCarActivity.this, CarTypeActivity.class);
                startActivityForResult(intent,CHOOSE_CAR_TYPE_REQQUESTCODE);
                AccountAddCarActivity.this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.account_add_car_btn:
                if(plateType==null&&car_number_et.getText().toString().trim().equals("")){
                    Toast.makeText(AccountAddCarActivity.this,"请填写完整信息",Toast.LENGTH_SHORT).show();
                }else{
                    UIHelper.showPd(AccountAddCarActivity.this);
                    bindVeh();//提交信息
                }
                break;
        }
    }


    /**
     * 绑定本人车辆
     * */
    HashMap map;
    VerificationCodeDialog verificationCodeDialog;
    private void bindVeh(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);
        map.put("plateType",plateType);
        map.put("plateNumber",car_number_et.getText().toString().trim().toUpperCase());
        VolleyUtil.getVolleyUtil(AccountAddCarActivity.this).StringRequestPostVolley(URLs.BIN_VEH, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountAddCarActivity.this));
                    String status=obj.getString("status");

                    if(status.equals("fail")){
                        int code=obj.optInt("code");
                        if(code==90010){
                            verificationCodeDialog=new VerificationCodeDialog(AccountAddCarActivity.this,R.style.Dialog,userType+"");
                            verificationCodeDialog.listener=new VerificationCodeDialog.VerificationCodeDialogListener() {
                                @Override
                                public void onSure(String input) {
                                    captchaCode=input;
                                    authAccountLogin(vehicleAccount+"");
                                }
                            };
                            verificationCodeDialog.showDialog();
                        }else {
                            String show_msg = obj.getString("show_msg");
                            Toast.makeText(AccountAddCarActivity.this, "绑定失败:" + show_msg, Toast.LENGTH_LONG).show();
                            erro_msg.setVisibility(View.VISIBLE);
                            erro_msg.setText(show_msg+"");
                        }
                    }else if(status.equals("ok")){
                        erro_msg.setVisibility(View.GONE);
                        Toast.makeText(AccountAddCarActivity.this,"绑定本人车辆成功",Toast.LENGTH_LONG).show();
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


    /**
     * 自动登录账号
     * */
    private void authAccountLogin(String vehicleAccount){
        map=new HashMap();
        map.put("token",TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);
        map.put("captchaCode",captchaCode);
        VolleyUtil.getVolleyUtil(AccountAddCarActivity.this).StringRequestPostVolley(URLs.AUTH_ACCOUNT_LOGIN, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountAddCarActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(AccountAddCarActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("ok")) {
                        bindVeh();//绑定本人车辆
                    }

                    if (verificationCodeDialog != null) {
                        verificationCodeDialog.dismiss();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_CAR_TYPE_REQQUESTCODE) {
            if (data != null) {
                plateTypename = data.getStringExtra("cartype");
                plateType = data.getStringExtra("typecode");
                if (plateTypename != null) {
                    account_car_type_tv.setText(plateTypename);
                    account_car_type_tv.setTextColor(Color.BLACK);
                }
            }
        }
    }
}
