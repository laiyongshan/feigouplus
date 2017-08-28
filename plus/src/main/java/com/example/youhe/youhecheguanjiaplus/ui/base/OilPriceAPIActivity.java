/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.OilPrice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mob.tools.utils.R.forceCast;

public class OilPriceAPIActivity extends Activity implements APICallback,View.OnClickListener {
    private OilPriceAdapter oilPriceAdapter;
    private ArrayList<HashMap<String, Object>> oilPriceList;
    private TextView oil_back_tv;
    private final String KEY="16e671c2065f0";
    private ProgressDialog pd;
    private LinearLayout oil_layout;
    private LinearLayout network_request_error_layout,illegalcode_layout;//网络连接失败显示的布局
    private TextView reload_tv;//重新加载

    private AppContext appContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 要在任何操作之前至少要调用一次initSDK来完成SDK的初始化
        MobAPI.initSDK(this,KEY);
        setContentView(R.layout.activity_oilprice);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,OilPriceAPIActivity.this);
        }
        SystemBarUtil.useSystemBarTint(OilPriceAPIActivity.this);

        appContext= (AppContext)this.getApplicationContext();

        oil_layout= (LinearLayout) findViewById(R.id.oil_layout);
        network_request_error_layout= (LinearLayout) findViewById(R.id.network_request_error_layout);
        reload_tv= (TextView) findViewById(R.id.reload_tv);
        reload_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        UIHelper.ToastMessage(this,"数据仅供参考，请以当地加油站的实际价格为准！");
        ListView lvResult = forceCast(findViewById(R.id.lvResult));
        oil_back_tv= (TextView) findViewById(R.id.oil_back_tv);
        oil_back_tv.setOnClickListener(this);//返回
        //init data
        oilPriceList = new ArrayList<HashMap<String, Object>>();
        oilPriceAdapter = new OilPriceAdapter(this, oilPriceList);
        lvResult.setAdapter(oilPriceAdapter);

        getData();//查询今日油价

    }

    private void getData(){
        if(appContext.isNetworkConnected()) {
            oil_layout.setVisibility(View.VISIBLE);
            network_request_error_layout.setVisibility(View.GONE);
            pd.show();
            //查询今日油价
            ((OilPrice) forceCast(MobAPI.getAPI(OilPrice.NAME))).queryOilPrice(OilPriceAPIActivity.this);

        }else{
            oil_layout.setVisibility(View.GONE);
            network_request_error_layout.setVisibility(View.VISIBLE);
            pd.dismiss();
        }

    }


    public void onSuccess(API api, int action, Map<String, Object> result) {
        HashMap<String, Object> res = forceCast(result.get("result"));
        if (res != null && res.size() > 0) {
            pd.dismiss();
            ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
            for (Map.Entry<String, Object> entry : res.entrySet()) {
                tempList.add(com.mob.tools.utils.R.<HashMap<String, Object>>forceCast(entry.getValue()));
            }
            if (tempList.size() > 0) {
                oilPriceList.clear();
                oilPriceList.addAll(tempList);
                oilPriceAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onError(API api, int action, Throwable details) {
        pd.dismiss();
        details.printStackTrace();
        Toast.makeText(this, R.string.error_raise, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.oil_back_tv:
                finish();
                overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
                break;
        }
    }

    private static class ViewHolder {
        TextView tvProvince;
        TextView tvDieselOil0;
        TextView tvGasoline90;
        TextView tvGasoline93;
        TextView tvGasoline97;
    }

    private class OilPriceAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<HashMap<String, Object>> list;

        OilPriceAdapter(Context context, ArrayList<HashMap<String, Object>> res) {
            inflater = LayoutInflater.from(context);
            list = res;
        }

        public int getCount() {
            if (null != list) {
                return list.size();
            }
            return 0;
        }

        public HashMap<String, Object> getItem(int position) {
            if (position < list.size()) {
                return list.get(position);
            }
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_oilprice_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvProvince = forceCast(convertView.findViewById(R.id.tvProvince));
                viewHolder.tvDieselOil0 = forceCast(convertView.findViewById(R.id.tvDieselOil0));
                viewHolder.tvGasoline90 = forceCast(convertView.findViewById(R.id.tvGasoline90));
                viewHolder.tvGasoline93 = forceCast(convertView.findViewById(R.id.tvGasoline93));
                viewHolder.tvGasoline97 = forceCast(convertView.findViewById(R.id.tvGasoline97));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = forceCast(convertView.getTag());
            }
            HashMap<String, Object> res = getItem(position);
            if (res != null) {
                viewHolder.tvProvince.setText(com.mob.tools.utils.R.toString(res.get("province")));
                viewHolder.tvDieselOil0.setText(com.mob.tools.utils.R.toString(res.get("dieselOil0")));
                viewHolder.tvGasoline90.setText(com.mob.tools.utils.R.toString(res.get("gasoline90")));
                viewHolder.tvGasoline93.setText(com.mob.tools.utils.R.toString(res.get("gasoline93")));
                viewHolder.tvGasoline97.setText(com.mob.tools.utils.R.toString(res.get("gasoline97")));
            }
            return convertView;
        }
    }
}
