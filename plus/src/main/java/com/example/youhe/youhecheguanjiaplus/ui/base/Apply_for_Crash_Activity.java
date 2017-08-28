package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.ApplyCrashAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.ApplyCrashItem;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

public class Apply_for_Crash_Activity extends Activity implements View.OnClickListener{

    private ImageButton apply_crash_back_ib;
    private ListView applyfor_crash_lv;
    private Button apply_crash_btn;
    private LinearLayout list_emty_layout;

    private ApplyCrashAdapter applyCrashAdapter;

    private List<ApplyCrashItem> withdrawals_list;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_crash);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, Apply_for_Crash_Activity.this);
        }
        SystemBarUtil.useSystemBarTint(Apply_for_Crash_Activity.this);


        withdrawals_list=new ArrayList<ApplyCrashItem>();
        applyCrashAdapter=new ApplyCrashAdapter(this,withdrawals_list);


        initView();//初始化控件

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        pd.show();
        withdrawals_list.clear();
        applyCrashAdapter.notifyDataSetChanged();
        getWithdrawalsList();//获取提现列表
    }

    private void initView() {
        apply_crash_back_ib = (ImageButton) findViewById(R.id.apply_crash_back_ib);
        apply_crash_back_ib.setOnClickListener(this);
        applyfor_crash_lv = (ListView) findViewById(R.id.applyfor_crash_lv);
        apply_crash_btn = (Button) findViewById(R.id.apply_crash_btn);
        apply_crash_btn.setOnClickListener(this);

        applyfor_crash_lv.setAdapter(applyCrashAdapter);

        list_emty_layout= (LinearLayout) findViewById(R.id.list_emty_layout);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.apply_crash_btn:
                Intent intent=new Intent(Apply_for_Crash_Activity.this,ApplyCrashActivity.class);
                startActivity(intent);
                break;

            case R.id.apply_crash_back_ib:
                finish();
                break;
        }
    }

    HashMap<String, Object> map;
    private HashMap getParams(){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            map.put("token", token);
        }

        return map;
    }




    //获取提现列表
    private void getWithdrawalsList(){

        VolleyUtil.getVolleyUtil(Apply_for_Crash_Activity.this).StringRequestPostVolley(URLs.GET_WITHDRAWALSLIST, EncryptUtil.encrypt(getParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                parseJson(EncryptUtil.decryptJson(jsonObject.toString(),Apply_for_Crash_Activity.this));//解析解密之后的数据
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });
    }

    ApplyCrashItem applyCrashItem;
    private void parseJson(String json){
        try {
            JSONObject object=new JSONObject(json);
            String status=object.getString("status");
            if(status.equals("ok")){
                JSONObject dataObj=object.getJSONObject("data");
                JSONArray withdrawalsArr=dataObj.getJSONArray("withdrawals_list");

                if(withdrawalsArr==null||withdrawalsArr.length()==0){
                    list_emty_layout.setVisibility(View.VISIBLE);
                    applyfor_crash_lv.setVisibility(View.GONE);
                }else {
                    for (int i = 0; i < withdrawalsArr.length(); i++) {
                        applyCrashItem=new ApplyCrashItem();
                        applyCrashItem.setCreatetimestr(withdrawalsArr.getJSONObject(i).getString("createtimestr"));
                        applyCrashItem.setFinishtimestr(withdrawalsArr.getJSONObject(i).getString("finishtimestr"));
                        applyCrashItem.setMoney(withdrawalsArr.getJSONObject(i).getInt("money"));
                        applyCrashItem.setPay_flowing(withdrawalsArr.getJSONObject(i).getString("pay_flowing"));
                        applyCrashItem.setStatus(withdrawalsArr.getJSONObject(i).getString("status"));
                        applyCrashItem.setRemark(withdrawalsArr.getJSONObject(i).getString("remark"));
                        applyCrashItem.setFee(withdrawalsArr.getJSONObject(i).getString("fee"));
                        applyCrashItem.setAmount_money(withdrawalsArr.getJSONObject(i).optDouble("the_amount_of_money"));

                        withdrawals_list.add(applyCrashItem);

                    }

                    applyCrashAdapter=new ApplyCrashAdapter(Apply_for_Crash_Activity.this,withdrawals_list);
                    applyCrashAdapter.notifyDataSetChanged();
                    applyfor_crash_lv.setAdapter(applyCrashAdapter);

                    list_emty_layout.setVisibility(View.GONE);
                    applyfor_crash_lv.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            list_emty_layout.setVisibility(View.VISIBLE);
            applyfor_crash_lv.setVisibility(View.GONE);
        }finally {
            pd.dismiss();
        }
    }


}
