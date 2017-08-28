package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.CommonBindAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.OrderDeatilBean;
import com.example.youhe.youhecheguanjiaplus.databinding.AdapterOrderFillingListBinding;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单补款 列表
 * Created by Administrator on 2017/5/27 0027.
 */

public class OrderFillingListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<OrderDeatilBean.MakeUpMoneyBean> makeUpMoneyList;
    private OrderFillAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_filling_list);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, OrderFillingListActivity.this);
        }
        SystemBarUtil.useSystemBarTint(OrderFillingListActivity.this);

        if (getIntent().hasExtra("list")) {
            String str = getIntent().getStringExtra("list");
            try {

                makeUpMoneyList = new Gson().fromJson(str, new TypeToken<List<OrderDeatilBean.MakeUpMoneyBean>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (makeUpMoneyList == null) {
            finish();
            return;
        } else {
            init();
        }

    }

    /**
     * 初始化
     */
    private void init() {
        listView = (ListView) findViewById(R.id.list);
        adapter = new OrderFillAdapter(this);
        adapter.setData(makeUpMoneyList);
        listView.setAdapter(adapter);

    }


    /**
     * 返回键
     *
     * @param view
     */
    public void fanhui(View view) {
        finish();
    }

    class OrderFillAdapter extends CommonBindAdapter<OrderDeatilBean.MakeUpMoneyBean> {

        public OrderFillAdapter(Context context) {
            super(context, new ArrayList<OrderDeatilBean.MakeUpMoneyBean>(), R.layout.adapter_order_filling_list);
        }

        @Override
        public void convert(ViewDataBinding bind, OrderDeatilBean.MakeUpMoneyBean t, int position) {
            AdapterOrderFillingListBinding b = (AdapterOrderFillingListBinding) bind;
            //金额
            b.price.setText(StringUtils.isEmpty(t.getExtra_money()) ? "" : ("¥" + t.getExtra_money() + ""));
            //原因
            b.reason.setText(StringUtils.isEmpty(t.getRemark()) ? "" : t.getRemark());
            //创建时间
            b.createTimeStr.setText(StringUtils.isEmpty(t.getCreatetimestr()) ? "" : ("创建时间：" + t.getCreatetimestr()));
            //支付时间
            if (StringUtils.isEmpty(t.getPaytimestr()))
                b.payTimeStr.setVisibility(View.GONE);
            else {
                b.payTimeStr.setVisibility(View.VISIBLE);
                b.payTimeStr.setText("支付时间：" + t.getPaytimestr() + "");
            }
            if (t.getStatus() != null && t.getStatus().equals("2"))//1未支付2已支付
                b.imageStatus.setVisibility(View.VISIBLE);
            else
                b.imageStatus.setVisibility(View.GONE);

        }
    }


}
