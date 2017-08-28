package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.AccountQueryActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class SmsCodeDialog extends Dialog implements View.OnClickListener{

    private Window window = null;

    private EditText sms_code_et;
    private TimeButton get_smscode_timebutton;
    private Button register_finish_btn;

    private String userType;
    private Context context;

    public SmsCodeDialogListener listener;

    public interface  SmsCodeDialogListener{
        void onSure(String input);
    }

    public SmsCodeDialog(Context context, int themeResId, String userType) {
        super(context, themeResId);
        this.userType=userType;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sms_code);

        initView();//初始化控件
        timeButton(savedInstanceState);
    }

    /**
     * 初始化控件
     * */
    private void initView(){
        sms_code_et= (EditText) findViewById(R.id.sms_code_et);
        sms_code_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null||s.toString().trim().equals("")){
                    register_finish_btn.setClickable(false);
                    register_finish_btn.setBackgroundResource(R.drawable.tijiaoa2);
                }else{
                    register_finish_btn.setClickable(true);
                    register_finish_btn.setBackgroundResource(R.drawable.affirmbutton3);
                }
            }
        });

        get_smscode_timebutton= (TimeButton) findViewById(R.id.get_smscode_timebutton);
        get_smscode_timebutton.setOnClickListener(this);
        register_finish_btn= (Button) findViewById(R.id.register_finish_btn);
        register_finish_btn.setOnClickListener(this);
    }

    /**
     * 获取验证码
     * @param savedInstanceState
     */
    private void timeButton(Bundle savedInstanceState) {
        //获取验证码键
        get_smscode_timebutton = (TimeButton) findViewById(R.id.get_smscode_timebutton);
        get_smscode_timebutton.onCreate(savedInstanceState);
        get_smscode_timebutton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(30 * 1000);
        get_smscode_timebutton.setOnClickListener(this);
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
            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.register_finish_btn:
                UIHelper.showPd(context);
                validRegisterSmscode();//校验注册验证码
                break;

            case R.id.get_smscode_timebutton:
                if(userType!=null){
                    sendRegisterSmsCode();//发送短信验证码
                }
                break;
        }
    }


    /*
    *发送注册短信验证码
    * */
    HashMap map=new HashMap();
    private void sendRegisterSmsCode(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);
        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.SEND_REGISTER_SMSCODE, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","发送短信验证码返回的信息："+jsonObject.toString());
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),context));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(context,show_msg+"",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("ok")) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        Toast.makeText(context,"短信验证码发送成功，请注意查收",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG","发送短信验证码返回的信息："+volleyError.toString());
            }
        });
    }


    /**
     * 校验注册短信验证码
     * */
    private void validRegisterSmscode(){
        map=new HashMap();
        map.put("token",TokenSQLUtils.check());
        map.put("userType",userType);
        map.put("smsCode",sms_code_et.getText().toString());
        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.VALID_REGISTER_SMS_CODE, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","校验短信验证码返回的信息："+jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),context));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(context,show_msg+"",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("ok")) {
//                        Toast.makeText(context,"短信验证码校验成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(context,AccountQueryActivity.class);
                        context.startActivity(intent);
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(context,"短信验证码校验失败，请稍候重试",Toast.LENGTH_SHORT).show();
                UIHelper.dismissPd();
                dismiss();
            }
        });
    }




}
