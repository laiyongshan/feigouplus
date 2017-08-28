package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class AddAcountActivity extends Activity implements View.OnClickListener{

    private TextView  forget_password_tv,go_to_regist_tv,province_tv;
    private EditText vehicleAccount_et,psw_et,yanzhenmima_et;
    private Button add_account_btn;
    private CheckBox isread_cb;

    private TextView disclaimer122_tv;

    private TextView add_account_erromsg;

    private ImageView addaccount_back_img,yzm_img;

    private BitmapManager bmpManager;

    private ProgressDialog pd;

    private String userType,provice;
    private final int PROVINCE_REQUEST_CODE=122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acount);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AddAcountActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddAcountActivity.this);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        bmpManager= new BitmapManager(null,this);

        initView();//初始化控件

//        getVerificationCode();//获取验证码
    }

    private void initView(){
        go_to_regist_tv= (TextView) findViewById(R.id.go_to_regist_tv);
        go_to_regist_tv.setOnClickListener(this);

        disclaimer122_tv= (TextView) findViewById(R.id.disclaimer122_tv);//免责声明
        disclaimer122_tv.setOnClickListener(this);

        add_account_btn= (Button) findViewById(R.id.add_account_btn);
        add_account_btn.setOnClickListener(this);

        vehicleAccount_et= (EditText) findViewById(R.id.vehicleAccount_et);
        psw_et= (EditText) findViewById(R.id.psw_et);
        yanzhenmima_et= (EditText) findViewById(R.id.yanzhenmima_et);

        addaccount_back_img= (ImageView) findViewById(R.id.addaccount_back_img);
        addaccount_back_img.setOnClickListener(this);

        yzm_img= (ImageView) findViewById(R.id.yzm_img);
        yzm_img.setOnClickListener(this);

        province_tv= (TextView) findViewById(R.id.province_tv);
        province_tv.setOnClickListener(this);

        forget_password_tv= (TextView) findViewById(R.id.forget_password_tv);
        forget_password_tv.setOnClickListener(this);

        add_account_erromsg= (TextView) findViewById(R.id.add_account_erromsg);

        isread_cb= (CheckBox) findViewById(R.id.isread_cb);
        isread_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("WU","onCheckedChanged");
                if (b){
                    add_account_btn.setClickable(true);
                    add_account_btn.setBackgroundResource(R.drawable.affirmbutton3);
                }else if (b==false){
                    add_account_btn.setClickable(false);
                    add_account_btn.setBackgroundResource(R.drawable.tijiaoa2);
                }
            }
        });
    }




    Intent intent=null;
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.go_to_regist_tv://去注册
                intent=new Intent(AddAcountActivity.this,CarOwnerTypeActivity.class);
                startActivity(intent);
                break;

            case R.id.add_account_btn://完成
                if(vehicleAccount_et.getText().toString().trim().equals("")
                        ||psw_et.getText().toString().trim().equals("")
                        ||yanzhenmima_et.getText().toString().trim().equals("")){
                    Toast.makeText(AddAcountActivity.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                }else if(provice==null){
                    Toast.makeText(AddAcountActivity.this,"请选择省份！",Toast.LENGTH_LONG).show();
                }else{
                    pd.show();
                    addAccount();//添加账号
                }
                break;

            case R.id.addaccount_back_img://返回按钮
                finish();
                break;

            case R.id.province_tv://选择省份
                intent=new Intent(AddAcountActivity.this,ListActivity.class);
                intent.putExtra("title","选择省份");
                startActivityForResult(intent,PROVINCE_REQUEST_CODE);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.yzm_img://刷新验证码

                if(userType!=null){
                    getVerificationCode(userType);
                }else{
                    Toast.makeText(AddAcountActivity.this,"请选择省份！",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.forget_password_tv:
                intent=new Intent(AddAcountActivity.this,AccountChangePswActivity.class);
                startActivity(intent);
                break;

            case R.id.disclaimer122_tv:
                intent=new Intent(AddAcountActivity.this,CommentWebActivity.class);
                intent.putExtra("url", URLs.DISCLAIMER_122);
                intent.putExtra("title","授权服务协议");
                startActivity(intent);
                break;
        }

    }



    private void addAccount(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        if(userType!=null) {
            map.put("userType", userType);
        }
        map.put("vehicleAccount",vehicleAccount_et.getText().toString().trim());
        map.put("accountPassword", ParamSign.getUserPassword(psw_et.getText().toString().trim()));
        map.put("captchaCode",yanzhenmima_et.getText().toString().trim());


        VolleyUtil.getVolleyUtil(AddAcountActivity.this).StringRequestPostVolley(URLs.ADD_ACCOUNTS, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {

                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AddAcountActivity.this));
                    String status=obj.getString("status");
                    if(status.equals("fail")){
                        String msg=obj.getString("msg");
                        Toast.makeText(AddAcountActivity.this,"msg:"+msg,Toast.LENGTH_LONG).show();
                        add_account_erromsg.setText(msg+"");
                        add_account_erromsg.setVisibility(View.VISIBLE);
                        if(userType!=null){
                            getVerificationCode(userType);
                        }else{
                            Toast.makeText(AddAcountActivity.this,"请选择省份！",Toast.LENGTH_SHORT).show();
                        }

                    }else if(status.equals("ok")){
                        add_account_erromsg.setVisibility(View.GONE);
                        Toast.makeText(AddAcountActivity.this,"添加账号成功",Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();


            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();

            }
        });
    }



    HashMap map;
    private void getVerificationCode(String userType){

        UIHelper.showPd(AddAcountActivity.this);

        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);//传getProvinceList 这个接口返回的 code
        VolleyUtil.getVolleyUtil(AddAcountActivity.this).StringRequestPostVolley(URLs.GET_CAPTCHA_IMG, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AddAcountActivity.this));
                    String status=obj.getString("status");
                    if(obj.has("show_msg")) {
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(AddAcountActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();
                    }

                    if(status.equals("ok")){
                        JSONObject dataObj=obj.getJSONObject("data");
                        String captchaImgUrl=dataObj.getString("captchaImgUrl");
                        bmpManager.loadBitmap(captchaImgUrl,yzm_img,null,490,240);
                        yzm_img.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                UIHelper.dismissPd();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PROVINCE_REQUEST_CODE){
            if(data!=null){
                userType=data.getStringExtra("code");
                provice=data.getStringExtra("name");
                if(provice!=null) {
                    province_tv.setText(provice);
                    province_tv.setTextColor(Color.BLACK);
                }

                if(userType!=null) {
                    getVerificationCode(userType);
                }
            }
        }

    }
}
