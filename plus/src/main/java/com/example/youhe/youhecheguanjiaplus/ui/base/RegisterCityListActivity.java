package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.ListAdapter;
import com.example.youhe.youhecheguanjiaplus.adapter.RegisterCityListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.City122;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class RegisterCityListActivity extends Activity implements AdapterView.OnItemClickListener{

    private TextView title_tv;
    private ImageView activity_list_back_img;
    private ListView listview;

    List<City122> city122List=new ArrayList<City122>();
    BaseAdapter adapter;


    private String userType;
    private String smsnotice="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,RegisterCityListActivity.this);
        }
        SystemBarUtil.useSystemBarTint(RegisterCityListActivity.this);

        userType=getIntent().getStringExtra("userType");

        initView();//初始化控件

        if(userType!=null){
            UIHelper.showPd(this);
            getRegisterCity(userType);
        }else{
            Toast.makeText(this,"请重新选择省份！",Toast.LENGTH_SHORT).show();
        }
    }


    private void initView(){
        title_tv= (TextView) findViewById(R.id.title_tv);
        title_tv.setText("选择城市");
        activity_list_back_img= (ImageView) findViewById(R.id.activity_list_back_img);
        activity_list_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        });
        listview= (ListView) findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        adapter=new ListAdapter(this,city122List);
        listview.setAdapter(adapter);
    }



    HashMap map;
    public void getRegisterCity(String userType){

        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("userType",userType);

        VolleyUtil.getVolleyUtil(RegisterCityListActivity.this).StringRequestPostVolley(URLs.GET_REGISTER_CITY, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                String json=EncryptUtil.decryptJson(jsonObject.toString(),RegisterCityListActivity.this);
                parseJson(json);
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });

    }


    private void parseJson(String json){
        City122 city122;
        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");

            smsnotice=dataObj.optString("smsnotice");

            JSONArray arr=dataObj.getJSONArray("cityList");
            for(int i=0;i<arr.length();i++){
                city122=new City122();

                city122.setCityProprefix(arr.getJSONObject(i).getString("cityProprefix"));
                city122.setCityName(arr.getJSONObject(i).getString("cityName"));

                city122List.add(city122);

                adapter=new RegisterCityListAdapter(RegisterCityListActivity.this,city122List);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            UIHelper.dismissPd();
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent();
        intent.putExtra("cityname",city122List.get(i).getCityName());
        intent.putExtra("proprefix",city122List.get(i).getCityProprefix());
        intent.putExtra("smsnotice",smsnotice);
        Log.i("TAG","省份信息提示："+smsnotice);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }
}
