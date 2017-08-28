package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.OrderDeatilBean;
import com.example.youhe.youhecheguanjiaplus.databinding.AdapterOrderDetailStatusBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情状态
 * Created by Administrator on 2017/5/8 0008.
 */

public class OrderDetailStatusAdapter extends CommonBindAdapter<OrderDeatilBean.OrderStatusListBean> {


    private Context mContext;

    public OrderDetailStatusAdapter(Context context) {
        super(context, new ArrayList<OrderDeatilBean.OrderStatusListBean>(), R.layout.adapter_order_detail_status);
        mContext = context;
    }

    @Override
    public void convert(ViewDataBinding bind, OrderDeatilBean.OrderStatusListBean t, int position) {
        AdapterOrderDetailStatusBinding binding = (AdapterOrderDetailStatusBinding) bind;

        if (position == 0) {
            binding.viewLeft.setVisibility(View.INVISIBLE);
            binding.viewRight.setVisibility(View.VISIBLE);
//            views.add(binding.viewRight);
        } else if (mData != null && position == mData.size() - 1) {
            binding.viewLeft.setVisibility(View.VISIBLE);
            binding.viewRight.setVisibility(View.INVISIBLE);
//            views.add(binding.viewLeft);
        } else {
            binding.viewLeft.setVisibility(View.VISIBLE);
            binding.viewRight.setVisibility(View.VISIBLE);
//            views.add(binding.viewLeft);
//            views.add(binding.viewRight);
        }

        if (t.getFinishStatus() == -1) {  //灰色 表示未进行
            binding.imageStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.duigou2));
        } else if (t.getFinishStatus() == 0) {  //红色  表示失败
            binding.imageStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chexiao));
        } else if (t.getFinishStatus() == 1)//成功
            binding.imageStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.duigou));

        binding.statusName.setText(t.getStatusName() == null ? "" : t.getStatusName());
        binding.time.setText(t.getStatusTime() == null ? "" : t.getStatusTime());

    }

    List<View> views = new ArrayList<View>();

    public List<View> getViews() {
//        views.add(tv1);
//        views.add(tv2);
//        views.add(btn1);
//        views.add(btn2);
//        views.add(iv1);
        for (View v : views) {
            hideView(v);
        }
        return views;
    }
    private void hideView(View v) {
        v.setVisibility(View.INVISIBLE);
    }

}
