package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.ClickUtils;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ClearEditText clearEditText1,passwordET,newpasswordET1,newpasswordET2;
    private SaveNameDao saveNameDao;
    private RelativeLayout logInbutton;
    private TokenSQLUtils tsl;
    private VolleyUtil volleyUtil;
    private static StatusSQLUtils statusSQLUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,ChangePasswordActivity.this);
        }
        SystemBarUtil.useSystemBarTint(ChangePasswordActivity.this);

        in();
    }

    private void in() {
        statusSQLUtils = new StatusSQLUtils(this);
        tsl = new TokenSQLUtils(this);//在数据库中拿到Token值
        clearEditText1 = (ClearEditText) findViewById(R.id.et_shouji);//手机号码EditText
        passwordET = (ClearEditText) findViewById(R.id.et_password);//原密码EditText
        newpasswordET1 = (ClearEditText) findViewById(R.id.et_mima);//新密码EditText
        newpasswordET2 = (ClearEditText) findViewById(R.id.et_mima2);//新密码EditText
        logInbutton = (RelativeLayout) findViewById(R.id.denglijian);//登录键
        logInbutton.setOnClickListener(this);
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        uidialog = new UIDialog(this,"正在修改密码.......");
        saveNameDao = new SaveNameDao(this);
        num(saveNameDao.readText("phonenumbe.txt"));
    }
    /**
     * 打开登录界面时
     * 读取本来手机号码
     */
    public void num(String numm){
        if(numm!=null){
            clearEditText1.setText(numm.trim());
        }

    }

    String  password ;
    String  newpassword;
    String  newpassword2;
    private void getParams(){
        String token = tsl.check();
        password=passwordET.getText().toString();
        newpassword= newpasswordET1.getText().toString();
        newpassword2= newpasswordET2.getText().toString();

        phonePams = new HashMap<>();
        phonePams.put("token",token);
//        phonePams.put("",)
        phonePams.put("password", ParamSign.getUserPassword(password));
        phonePams.put("newpassword",ParamSign.getUserPassword(newpassword));
        phonePams.put("newpassword2",ParamSign.getUserPassword(newpassword2));

//        phonePams.put("timestamp",System.currentTimeMillis()/1000+"");
//        String sign=ParamSign.getSign(phonePams);
//        phonePams.put("sign",sign);
//        return ParamSign.getSign(phonePams);


    }


    private HashMap<String,String> phonePams;
    private UIDialog uidialog;
    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastDoubleClick()) {//防止多点击
            return;
        }

        getParams();//获取参数

//        phonePams=new HashMap();
//        phonePams.clear();
//        phonePams.put("data",getParams());
//        phonePams=ParamSign.netWorkEncrypt(phonePams);

        if(newpassword.length()>=6&&newpassword2.length()>=6){
            uidialog.show();
            volleyUtil.StringRequestPostVolley(URLs.CHANGE_PASSWORD, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    getjson(EncryptUtil.decryptJson(jsonObject.toString(),ChangePasswordActivity.this));//解析解密之后的数据
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(ChangePasswordActivity.this,"网络连接失败,无法发送请求");
                }
            });
        }else {
            ToastUtil.getLongToastByString(ChangePasswordActivity.this,"密码长度不能少于6");
        }



    }

    private void getjson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String status=jsonObject.getString("status");
            if (status.equals("ok")){
                statusSQLUtils.addDate("yes");//保存已登录状态

                Toast.makeText(ChangePasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();

                uidialog.hide();
                finish();


            }else {

                Misidentification.misidentification1(this,status,jsonObject);//错误判断
                uidialog.hide();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回键点击事件
     *
     */
    public void fanhui(View view){
        this.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);
        finish();

    }
}
