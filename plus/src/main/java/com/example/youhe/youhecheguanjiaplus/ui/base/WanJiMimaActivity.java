package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
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
 * 忘记密码界面
 */
public class WanJiMimaActivity extends AppCompatActivity implements View.OnClickListener {
    private TimeButton timeButton;
    private ClearEditText clearEditText1,clearEditText2,clearEditText3,clearEditText4;
    private TextView contact_service_tv;
    private VolleyUtil volleyUtil;
    private UIDialog uidialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wan_ji_mima);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,WanJiMimaActivity.this);
        }
        SystemBarUtil.useSystemBarTint(WanJiMimaActivity.this);

        timeButton(savedInstanceState);
        in();
    }

    private void in() {
        EventBus.getDefault().register(this);
        uidialog = new UIDialog(this,"正在登录.......");

        clearEditText1 = (ClearEditText) findViewById(R.id.et_shouji);
        clearEditText2 = (ClearEditText) findViewById(R.id.et_yanzhenmima);
        clearEditText3 = (ClearEditText) findViewById(R.id.et_mima);
        clearEditText4 = (ClearEditText) findViewById(R.id.et_mima2);
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求
        contact_service_tv= (TextView) findViewById(R.id.contact_service_tv);
        contact_service_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UIHelper.contactService(WanJiMimaActivity.this, CommentSetting.SERVICE_NUM);
            }
        });
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

    private HashMap phonePams;
    /**
     * 手机验证吗
     * @param view
     */
    @Override
    public void onClick(View view) {

        String phoneNumber = clearEditText1.getText().toString();
        if(phoneNumber.trim().length()<11){
            ToastUtil.getLongToastByString(this,"请输入完整手机号码");
        }else {
            phonePams = new HashMap();
            phonePams.put("mobile",phoneNumber);
            volleyUtil.StringRequestPostVolley(URLs.RESET, EncryptUtil.encrypt(phonePams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    jsonde(EncryptUtil.decryptJson(jsonObject.toString(),WanJiMimaActivity.this));

                }
                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(WanJiMimaActivity.this,"网络连接失败,无法发送请求");
                }
            });
        }

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
    private void getParams(){
        pams = new HashMap<>();
        pams.put("mobile", clearEditText1.getText().toString());
        pams.put("newpassword", ParamSign.getUserPassword(clearEditText3.getText().toString()));
        pams.put("newpassword2",ParamSign.getUserPassword(clearEditText4.getText().toString()));
        pams.put("verifycode",clearEditText2.getText().toString());
    }


    /**
     *确定键
     * @param view
     */
    public void confirm(View view){
        if (ClickUtils.isFastDoubleClick()){
            return;
        }
        if (MainActivity.getHtts()==false){
            ToastUtil.getLongToastByString(this,"网络连接失败，请检测设置");
            return;
        }

        if(!clearEditText3.getText().toString().trim().equals(clearEditText4.getText().toString().trim())){
            ToastUtil.getShortToastByString(WanJiMimaActivity.this,"两次密码输入不一致！");
        }else if(clearEditText3.getText().toString().length()>=6&&clearEditText4.getText().toString().length()>=6){
            uidialog.show();
            getParams();
            volleyUtil.StringRequestPostVolley(URLs.VERIFICATION_REGISTER, EncryptUtil.encrypt(pams), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    uidialog.hide();
                    Log.d("TAG", jsonObject.toString());
                    getjson(EncryptUtil.decryptJson(jsonObject.toString(),WanJiMimaActivity.this));//解析解密之后的数据
                }

                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(WanJiMimaActivity.this,"网络连接失败,无法发送请求");
                    uidialog.hide();
                }
            });

        }else {
            ToastUtil.getLongToastByString(this,"密码长度不能少于6");
        }

    }
    public void getjson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String  status = jsonObject.getString("status");

            if (status.equals("ok")){
                ToastUtil.getShortToastByString(this,"密码重置成功");
                EventBus.getDefault().post(new FirstEvent("ok"));//登录成功后通知刷新

                finish();
            }else {

            }

            Misidentification.misidentification1(this,status,jsonObject);//错误提示

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按下确定后通知订单查询
     * 个人1中心刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }


}
