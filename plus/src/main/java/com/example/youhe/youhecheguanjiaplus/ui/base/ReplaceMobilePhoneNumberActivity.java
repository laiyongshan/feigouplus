package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
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
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 跟换手机号界面
 */
public class ReplaceMobilePhoneNumberActivity extends AppCompatActivity implements View.OnClickListener{
    private TimeButton timeButton;
    private ClearEditText et_shouji,et_yanzhenmima,et_mima,et_oldphone;
    private VolleyUtil volleyUtil;
    private TokenSQLUtils tsq;
    private SaveNameDao saveNameDao;
    private static StatusSQLUtils statusSQLUtils;
    private UIDialog uidialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_mobile_phone_number);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,ReplaceMobilePhoneNumberActivity.this);
        }
        SystemBarUtil.useSystemBarTint(ReplaceMobilePhoneNumberActivity.this);

        timeButton(savedInstanceState);
        in();
    }
    private void in() {
        EventBus.getDefault().register(this);

        statusSQLUtils = new StatusSQLUtils(this);
        saveNameDao = new SaveNameDao(this);
        et_shouji = (ClearEditText) findViewById(R.id.et_shouji);//新手机号码
        et_yanzhenmima = (ClearEditText) findViewById(R.id.et_yanzhenmima);//旧手机验证码
        et_mima = (ClearEditText) findViewById(R.id.et_mima);//原密码
        et_oldphone = (ClearEditText) findViewById(R.id.et_oldphone);//原手机
        uidialog = new UIDialog(this,"正在修改.......");
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        tsq = new TokenSQLUtils(this);
        num(saveNameDao.readText("phonenumbe.txt"));
    }

    /**
     * 打开登录界面时
     * 读取本来手机号码
     */
    public void num(String numm){
        if(numm!=null){
            et_oldphone.setText(numm.trim());
        }

    }
    private void timeButton(Bundle savedInstanceState) {

        //获取验证码键
        timeButton = (TimeButton) findViewById(R.id.button1);
        timeButton.onCreate(savedInstanceState);
        timeButton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(60 * 1000);
        timeButton.setOnClickListener(this);
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

    private HashMap<String,String> phonePams;

    /**
     * 手机验证吗
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastDoubleClick()) {//防止多点击
            return;
        }

         String token = tsq.check();
        String nesPhoneNumber = et_shouji.getText().toString();//新手机号
        phonePams = new HashMap<>();
        phonePams.put("mobile",nesPhoneNumber);
        phonePams.put("token",token);//在数据库中拿到Token值
        volleyUtil.StringRequestPostVolley(URLs.TO_CHANGE_CELL_PHONE, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                jsonde(EncryptUtil.decryptJson(jsonObject.toString(),ReplaceMobilePhoneNumberActivity.this));
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(ReplaceMobilePhoneNumberActivity.this,"网络连接失败,无法发送请求");
            }
        });

    }
    public void jsonde(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            if(status.equals("ok")){
                ToastUtil.getShortToastByString(this,"请等候验证码,60秒内到达");
            }
            Misidentification.misidentification1(this,status,jsonObject);//判断错误信息

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private HashMap<String,String> pams;
    private String newmobile;
    /**
     *确定键
     * @param view
     */
    public void confirm(View view){
        newmobile = et_shouji.getText().toString();//得到新手机号码
        String  xinVerifycode = et_yanzhenmima.getText().toString();//得到新手机验证码
        String password = et_mima.getText().toString();//得到原密码
        String mobile = et_oldphone.getText().toString();//得到原手机号码
        String token = tsq.check();//在数据库中拿到token

        pams = new HashMap<>();
        pams.put("mobile",mobile);
        pams.put("token",token);
        pams.put("password", ParamSign.getUserPassword(password));
        pams.put("newmobile",newmobile);
        pams.put("verifycode",xinVerifycode);

        if(password.length()>=6){
            uidialog.show();
            volleyUtil.StringRequestPostVolley(URLs.TO_CHANGE, EncryptUtil.encrypt(pams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    getjson(EncryptUtil.decryptJson(jsonObject.toString(),ReplaceMobilePhoneNumberActivity.this));
                }
                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(ReplaceMobilePhoneNumberActivity.this,"网络连接失败,无法发送请求");
                }
            });
        }else {
            ToastUtil.getLongToastByString(ReplaceMobilePhoneNumberActivity.this,"密码长度不能少于6");
        }

    }
    public void getjson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String  status = jsonObject.getString("status");
            if (status.equals("ok")){

                saveNameDao.writeTxtToFile(et_shouji.getText().toString(), "phonenumbe.txt");//点击确定时保存手机号码
                EventBus.getDefault().post(new FirstEvent("huan"));
                ToastUtil.getShortToastByString(this,"手机更换成功");
                uidialog.hide();
                finish();

            }else {
                String show_msg=jsonObject.optString("show_msg");
                Toast.makeText(ReplaceMobilePhoneNumberActivity.this,show_msg+"",Toast.LENGTH_LONG).show();
                uidialog.hide();
//                Misidentification.misidentification1(this,status,jsonObject);//错误提示
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void onEventMainThread(FirstEvent event) {


    }
}
