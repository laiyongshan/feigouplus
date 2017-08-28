package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {
    private ClearEditText edidText1,edidText2;
    private ImageView tijiao;
    private VolleyUtil volleyUtil;
    private HashMap<String,String> param;
    private TokenSQLUtils tokenSQLUtils;
    private UIDialog uidialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,FeedbackActivity.this);
        }
        SystemBarUtil.useSystemBarTint(FeedbackActivity.this);

        in();
    }

    private void in() {
        edidText1 = (ClearEditText) findViewById(R.id.yijianfankui_et);//反馈内容
        edidText2 = (ClearEditText) findViewById(R.id.shoujihao_et);//手机号码
        tijiao = (ImageView) findViewById(R.id.imggg);//提交
        tijiao.setOnClickListener(this);
        volleyUtil = VolleyUtil.getVolleyUtil(this);
        param = new HashMap<>();
        tokenSQLUtils = new TokenSQLUtils(this);
        uidialog = new UIDialog(this,"正在提交.......");
    }


    /**
     * 提交建议
     */
    public  void requestString() {
        uidialog.show();
        String fakui = edidText1.getText().toString();
        String shouji = edidText2.getText().toString();
        String token = tokenSQLUtils.check();
        if(fakui.equals("")){
            ToastUtil.getLongToastByString(this,"您还未输入任何内容");
            uidialog.hide();
        }else if(shouji.equals("")){
            ToastUtil.getLongToastByString(this,"联系方式不能为空");
            uidialog.hide();
        } else {
            param.put("email",shouji);
            param.put("content",fakui);
            param.put("token",token);
            volleyUtil.StringRequestPostVolley(URLs.FEEDBACK, EncryptUtil.encrypt(param), new VolleyInterface() {
                @Override
                public void ResponseResult(Object jsonObject) {
                    jsons(EncryptUtil.decryptJson(jsonObject.toString(),FeedbackActivity.this));
                }
                @Override
                public void ResponError(VolleyError volleyError) {
                    ToastUtil.getShortToastByString(FeedbackActivity.this,"网络连接失败");
                }
            });
        }
    }
    public void jsons(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
             String status = jsonObject.getString("status");
            if(status.equals("ok")){
                uidialog.hide();
                ToastUtil.getLongToastByString(this,"提交成功");
                finish();
            }else if (status.equals("fail")){
                String show_msg=jsonObject.optString("show_msg");
                ToastUtil.getLongToastByString(this,"提交失败,"+show_msg);
                Misidentification.misidentification1(this,status,jsonObject);
                uidialog.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imggg:
                requestString();
                break;
        }
    }
}
