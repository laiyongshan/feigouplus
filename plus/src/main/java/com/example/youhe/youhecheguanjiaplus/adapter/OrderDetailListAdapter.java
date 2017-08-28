package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class OrderDetailListAdapter extends BaseAdapter {
    private List<Violation> violationList;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public static HashMap<Integer, View> map = new HashMap<Integer,View>();


    public OrderDetailListAdapter(Context context, List<Violation> violationList) {
        this.violationList = violationList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return violationList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (map.get(i) == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_illegal_list, null);
            holder.illegalAdd_tv = (TextView) view.findViewById(R.id.illegalAdd_tv);
            holder.illegalContent_tv = (TextView) view.findViewById(R.id.illegalContent_tv);
            holder.illegalTime_tv = (TextView) view.findViewById(R.id.illegalTime_tv);
            holder.illegalmoney_tv = (TextView) view.findViewById(R.id.illegalmoney_tv);
            holder.koufen_tv = (TextView) view.findViewById(R.id.koufen_tv);
            holder.poundage_tv = (TextView) view.findViewById(R.id.shouxufei_tv);
            holder.page_count_tv= (TextView) view.findViewById(R.id.page_count_tv);
            holder.latefee_tv= (TextView) view.findViewById(R.id.latefee_tv);
            holder.latefee_layout= (LinearLayout) view.findViewById(R.id.latefee_layout);
            view.setTag(holder);
        } else {
            view = map.get(i);
            holder = (ViewHolder) view.getTag();
        }

        holder.illegalAdd_tv.setText(violationList.get(i).getLocation());
        holder.illegalContent_tv.setText(violationList.get(i).getReason());
        holder.illegalTime_tv.setText(violationList.get(i).getTime());
        holder.illegalmoney_tv.setText("￥" + violationList.get(i).getCount());
        holder.koufen_tv.setText(violationList.get(i).getDegree() + "分");
        if (violationList.get(i).getDegree()==12||violationList.get(i).getPrice()==0) {
            holder.poundage_tv.setText("待报价");
        } else {
            holder.poundage_tv.setText("￥" + violationList.get(i).getPrice());


        }

        holder.page_count_tv.setText((i+1)+"/"+violationList.size());

        if(violationList.get(i).getCategory().equals("现场单")) {
            holder.latefee_layout.setVisibility(View.VISIBLE);
            holder.latefee_tv.setText("￥" + violationList.get(i).getLatefee());//滞纳金
        }

        return view;
    }

    public class ViewHolder {
        TextView illegalContent_tv;
        TextView illegalAdd_tv;
        TextView illegalTime_tv;
        TextView illegalmoney_tv;
        TextView koufen_tv;
        TextView poundage_tv;
        TextView page_count_tv;
        TextView latefee_tv;
        LinearLayout latefee_layout;
    }


}
