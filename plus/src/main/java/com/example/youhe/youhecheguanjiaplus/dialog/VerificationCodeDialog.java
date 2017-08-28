package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class VerificationCodeDialog extends Dialog implements View.OnClickListener{

    private Window window = null;

    private Button register_finish_btn;
    private TimeButton get_verification_code_btn;
    private ImageView verification_code_img;
    private EditText verification_code_et;

    private Activity activity;

    private BitmapManager bmpManager;

    private String userType;

    public VerificationCodeDialogListener listener;

    public interface VerificationCodeDialogListener{
        void onSure(String input);
    }

    public VerificationCodeDialog(Activity activity, int themeResId,String userType) {
        super(activity, themeResId);
        this.activity=activity;
        this.userType=userType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verification_code);

        bmpManager= new BitmapManager(null,activity);

        initView();//初始化控件

        if(userType!=null) {
            getVerificationCode(userType);//获取验证码
        }
    }

    private void initView(){
        register_finish_btn= (Button) findViewById(R.id.register_finish_btn);
        register_finish_btn.setOnClickListener(this);
        register_finish_btn.setClickable(false);

//        account_query_back_img= (ImageView) findViewById(R.id.account_query_back_img);
//        account_query_back_img.setOnClickListener(this);

        verification_code_img= (ImageView) findViewById(R.id.verification_code_img);
        verification_code_img.setOnClickListener(this);

        verification_code_et= (EditText) findViewById(R.id.verification_code_et);
        verification_code_et.addTextChangedListener(new TextWatcher() {
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
            case R.id.register_finish_btn://完成按钮

//                AccountQueryActivity.captchaCode=verification_code_et.getText().toString().trim();
//                dismiss();
                if(listener!=null){
                    listener.onSure(verification_code_et.getText().toString());
                }
                break;

            case R.id.verification_code_img:
                if(userType!=null) {
                    getVerificationCode(userType);
                }
                break;

        }
    }




    HashMap map;
    private void getVerificationCode(String userType){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);

        UIHelper.showPd(activity);

        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.GET_CAPTCHA_IMG, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","获取验证码返回的数据是："+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),activity));
                    String status=obj.getString("status");

                    if(status.equals("ok")){
                        JSONObject dataObj=obj.getJSONObject("data");
                        String captchaImgUrl=dataObj.getString("captchaImgUrl");

                        new GetImgTask().execute(captchaImgUrl);//获取验证码

//                        bmpManager.loadBitmap(captchaImgUrl,verification_code_img,null,590,40);
//                        verification_code_img.setVisibility(View.VISIBLE);
                    }else{
                        if(obj.has("show_msg")){
                            String show_msg=obj.optString("show_msg");
                            Toast.makeText(activity,"请求验证码失败"+show_msg,Toast.LENGTH_SHORT).show();
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
                UIHelper.dismissPd();
            }
        });
    }


    /**
     * 第一个参数表示要执行的任务，通常是网络的路径；第二个参数表示进度的刻度，第三个参数表示任务执行的返回结果
     */
    public class GetImgTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 表示任务执行之前的操作
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        /**
         * 主要是完成耗时的操作
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // 使用网络连接类HttpClient类王城对网络数据的提取

            Bitmap bitmap =  HttpUtil.getNetBitmap(arg0[0]);
            return bitmap;
        }

        /**
         * 主要是更新UI的操作
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            verification_code_img.setImageBitmap(result);
            verification_code_img.setVisibility(View.VISIBLE);
        }
    }

}
