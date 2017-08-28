package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.SpinnerAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;

public class JoinUsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ClearEditText editText1,editText2,editText3;
    private ImageView imageView;
    private TokenSQLUtils tsu;
    private  VolleyUtil volleyUtil;
    private  HashMap<String,String> param;
    private UIDialog uidialog;
    private Spinner spinner;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,JoinUsActivity.this);
        }
        SystemBarUtil.useSystemBarTint(JoinUsActivity.this);

        in();
    }

    private void in() {
        editText1 = (ClearEditText) findViewById(R.id.name_edit);//姓名
        editText2 = (ClearEditText) findViewById(R.id.phone_edit);//电话
        editText3 = (ClearEditText) findViewById(R.id.content_et);//描述内容
        arrayList = SpinnerAdapter.getSpinnerAdapter(this);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        spinner = (Spinner) findViewById(R.id.clity);//省区选择
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        tsu = new TokenSQLUtils(this);
        param = new HashMap<>();
        volleyUtil = VolleyUtil.getVolleyUtil(this);
        imageView = (ImageView) findViewById(R.id.imggg);
        imageView.setOnClickListener(this);
        uidialog = new UIDialog(this,"正在提交.......");
    }


    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    /**
     *
     * 提交信息
     * @param v
     */
    @Override
    public void onClick(View v) {


        String  linkname = editText1.getText().toString();
        String  mobile = editText2.getText().toString();
        String  remark = editText3.getText().toString();
        String token = tsu.check();

        param.put("mobile",mobile);
        param.put("linkname",linkname);
        param.put("remark",remark);
        param.put("token", token);
        uidialog.show();
        volleyUtil.StringRequestPostVolley(URLs.JOIN_US, EncryptUtil.encrypt(param), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("WU",jsonObject.toString());
                jsonData(EncryptUtil.decryptJson(jsonObject.toString(),JoinUsActivity.this));
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }

    public void jsonData(String json){

        try {
            JSONObject jsonObject =new JSONObject(json);
             String status = jsonObject.getString("status");
            if(status.equals("ok")){
                ToastUtil.getLongToastByString(this,"提交成功");
                uidialog.hide();
                finish();
            }else {
                uidialog.hide();
            }
            Misidentification.misidentification1(this,status,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String province = arrayList.get(position);
        param.put("province",province);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


