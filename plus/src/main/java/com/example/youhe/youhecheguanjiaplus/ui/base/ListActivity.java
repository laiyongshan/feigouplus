package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.ListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.Province122;
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
 * Created by Administrator on 2017/4/27 0027.
 */

public class ListActivity extends Activity implements AdapterView.OnItemClickListener{

    private TextView title_tv;
    private ImageView activity_list_back_img;
    private ListView listview;

    List<Province122> province122List=new ArrayList<Province122>();
    private BaseAdapter adapter;

    private String title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,ListActivity.this);
        }
        SystemBarUtil.useSystemBarTint(ListActivity.this);


        title=getIntent().getStringExtra("title");


        initView();//初始化控件

        UIHelper.showPd(this);
        getProvinceList();//获取省份
    }

    //初始化控件
    private void initView(){
        title_tv= (TextView) findViewById(R.id.title_tv);
        title_tv.setText(title);
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
        adapter=new ListAdapter(ListActivity.this,province122List);
        listview.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent();
        intent.putExtra("code",province122List.get(i).getCode());
        intent.putExtra("name",province122List.get(i).getName());
        setResult(RESULT_OK,intent);
        finish();

        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);

    }


    HashMap map;
    private void getProvinceList(){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());

        VolleyUtil.getVolleyUtil(ListActivity.this).StringRequestPostVolley(URLs.GET_PROVINCE_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                String json= EncryptUtil.decryptJson(jsonObject.toString(),ListActivity.this);

                parseJson(json);

                UIHelper.dismissPd();
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }


    private void parseJson(String json){
        Province122 province122;
        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");
            JSONArray arr=dataObj.getJSONArray("provinceList");
            for(int i=0;i<arr.length();i++){
                province122=new Province122();

                province122.setCode(arr.getJSONObject(i).getString("code"));
                province122.setName(arr.getJSONObject(i).getString("name"));

                province122List.add(province122);

                adapter=new ListAdapter(ListActivity.this,province122List);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
