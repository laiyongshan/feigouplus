package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.TraceListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.Trace;
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
 * Created by Administrator on 2017/6/16.
 */

public class ExpressCheckActivity extends Activity{

    private ImageView back_iv;

    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<Trace>();
    private TraceListAdapter adapter;

    private TextView express_status_tv,express_name_tv,express_code_tv,timediff_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_express);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,ExpressCheckActivity.this);
        }
        SystemBarUtil.useSystemBarTint(ExpressCheckActivity.this);

        String express=getIntent().getStringExtra("express");

        initView();//初始化控件
        initData(express!=null?express:"");//获取数据
    }

    private void initView(){
        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        express_status_tv= (TextView) findViewById(R.id.express_status_tv);
        express_name_tv= (TextView) findViewById(R.id.express_name_tv);
        express_code_tv= (TextView) findViewById(R.id.express_code_tv);

        timediff_tv= (TextView) findViewById(R.id.timediff_tv);

        rvTrace= (RecyclerView) findViewById(R.id.rvTrace);
    }

    /**
     * 初始化数据
     * */
    private void initData(String express){
        UIHelper.showPd(ExpressCheckActivity.this);
        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("express",express);
        VolleyUtil.getVolleyUtil(ExpressCheckActivity.this).StringRequestPostVolley(URLs.GET_EXPRESS_INFO, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),ExpressCheckActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("ok")) {
                        JSONObject dataObj = obj.optJSONObject("data");
                        JSONArray arr = dataObj.optJSONArray("data");
                        String expTextName = dataObj.optString("expTextName");
                        String mailNo = dataObj.optString("mailNo");
                        String timediff = dataObj.optString("timediff");
                        express_status_tv.setText("" + dataObj.optString("status"));
                        express_name_tv.setText("" + expTextName);
                        express_code_tv.setText("" + mailNo);
                        timediff_tv.setText("耗时" + timediff);

                        Trace trace;
                        for (int i = 0; i < arr.length(); i++) {
                            trace = new Trace();
                            trace.setAcceptStation(arr.getJSONObject(i).getString("context"));
                            trace.setAcceptTime(arr.getJSONObject(i).getString("time"));
                            traceList.add(trace);
                        }

                        adapter = new TraceListAdapter(ExpressCheckActivity.this, traceList);
                        rvTrace.setLayoutManager(new LinearLayoutManager(ExpressCheckActivity.this));
                        rvTrace.setAdapter(adapter);
                    }else{
                        if(obj.has("show_msg")){
                            Toast.makeText(ExpressCheckActivity.this,""+obj.optString("show_msg"),Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    UIHelper.dismissPd();
                }


            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }


}
