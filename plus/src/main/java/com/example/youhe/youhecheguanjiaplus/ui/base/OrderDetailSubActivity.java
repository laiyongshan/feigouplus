package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.OrderDetailAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.OrderDeatilBean;
import com.example.youhe.youhecheguanjiaplus.databinding.ActivityOrderDetailSubBinding;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 罚款列表
 * Created by Administrator on 2017/5/10 0010.
 */

public class OrderDetailSubActivity extends AppCompatActivity {

    private OrderDetailAdapter detailAdapter;
    private ArrayList<OrderDeatilBean.PeccancyListBean> peccancyList;
    private ActivityOrderDetailSubBinding bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_order_detail_sub);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, OrderDetailSubActivity.this);
        }
        SystemBarUtil.useSystemBarTint(OrderDetailSubActivity.this);

        if (getIntent().hasExtra("list")) {
            String str = getIntent().getStringExtra("list");
            try {

                peccancyList = new Gson().fromJson(str, new TypeToken<List<OrderDeatilBean.PeccancyListBean>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            peccancyList=getIntent().getParcelableArrayListExtra("list");
        }
        if (peccancyList == null) {
            finish();
            return;
        } else {
            init();
        }
    }
    /**
     * 返回键
     *
     * @param view
     */
    public void fanhui(View view) {
        finish();
    }

    private void init() {
        detailAdapter = new OrderDetailAdapter(this);
        detailAdapter.setData(peccancyList);
        bind.listview.setAdapter(detailAdapter);
    }
}
