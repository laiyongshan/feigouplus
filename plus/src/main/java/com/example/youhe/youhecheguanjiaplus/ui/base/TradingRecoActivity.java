package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.TradingRecAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.TradingModel;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class TradingRecoActivity extends Activity {

    private ListView trading_reco_lv;
    private TradingRecAdapter tradingRecAdapter;
    private List<TradingModel> tradingModelList;

    private ImageView back_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_reco);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,TradingRecoActivity.this);
        }
        SystemBarUtil.useSystemBarTint(TradingRecoActivity.this);


        initViews();//初始化控件

    }


    /*
    * 初始化控件
    * */
    private void initViews(){
        tradingModelList=new ArrayList<TradingModel>();

        trading_reco_lv= (ListView) findViewById(R.id.trading_reco_lv);
        trading_reco_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        setLayoutAdapter(tradingModelList);


        back_img= (ImageView) findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * 设置listVIew的Adapter
     * */
    private void setLayoutAdapter(List<TradingModel> list){
        tradingRecAdapter=new TradingRecAdapter(this,tradingModelList);
        tradingRecAdapter.notifyDataSetChanged();
        trading_reco_lv.setAdapter(tradingRecAdapter);
    }

}
