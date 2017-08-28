package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.db.biz.NamePathSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 设置名称界面
 */
public class TheNameOfTheSetActivity extends AppCompatActivity {
    private ClearEditText et;
    private SaveNameDao saveNameDao;
    private VolleyUtil volleyUtil;
    private HashMap<String,String> hashMap;
    private TokenSQLUtils tsu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_name_of_the_set);
        et = (ClearEditText) findViewById(R.id.et);
        saveNameDao = new SaveNameDao(this);
        returnAndSave();
    }

    private void returnAndSave() {
        volleyUtil = VolleyUtil.getVolleyUtil(this);
        tsu = new TokenSQLUtils(this);
        hashMap = new HashMap<>();

        //返回键
        findViewById(R.id.wangjimina_fanhuijian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //保存名称键
        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et.getText().toString();

                httpsName(name);//上传昵称


            }
        });

    }

    /**
     * 上传昵称
     * @param name
     */
    public void httpsName(final String name){
        String token = tsu.check();
        hashMap.put("token", token);
        hashMap.put("nickname", name);

        UIHelper.showPd(TheNameOfTheSetActivity.this);

        volleyUtil.StringRequestPostVolley(URLs.UPLOAD_THE_NICKNAME, EncryptUtil.encrypt(hashMap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                jsonData(EncryptUtil.decryptJson(jsonObject.toString(),TheNameOfTheSetActivity.this),name);
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(TheNameOfTheSetActivity.this,"网络连接失败");
            }
        });
    }
    public void jsonData(String json,String name){
        try {
            JSONObject jsonObject  = new JSONObject(json);
            String status = jsonObject.getString("status");
            if(status.equals("ok")){
                NamePathSQLUtils sqlUtils = new NamePathSQLUtils(this);
                String httpNames = sqlUtils.check();//保存修改的昵称到本地
                EventBus.getDefault().register(this);
                if(httpNames.equals("")){
                    sqlUtils.addDate(name);

                    EventBus.getDefault().post("rush");
                }else {
                    sqlUtils.undateApi(name);
                    EventBus.getDefault().post("rush");
                }

                ToastUtil.getShortToastByString(TheNameOfTheSetActivity.this,"已保存");
                finish();
            }else {
                Misidentification.misidentification1(this,status,jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            UIHelper.dismissPd();
        }
    }

    @Subscribe
    public void onEventMainThread(FirstEvent event) {


    }
}
