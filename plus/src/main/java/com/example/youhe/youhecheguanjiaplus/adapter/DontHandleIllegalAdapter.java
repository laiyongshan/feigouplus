package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class DontHandleIllegalAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    public ViewHolder holder;
    private List<Violation> violations;

    public static HashMap<Integer, View> map = new HashMap<Integer,View>();

    public DontHandleIllegalAdapter(Context mContext, List<Violation> violations){

        mInflater = LayoutInflater.from(mContext);
        this.mContext=mContext;
        this.violations=violations;

    }

    @Override
    public int getCount() {
        return violations.size();
    }

    @Override
    public Object getItem(int i) {
        return violations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (map.get(i) == null) {
            holder= new ViewHolder();

            view=mInflater.inflate(R.layout.item_donthandle_illegal_lv,null);

            holder.isselect_cb= (CheckBox) view.findViewById(R.id.isselect_cb);
            holder.illegalAdd_tv= (TextView) view.findViewById(R.id.illegalAdd_tv);
            holder.illegalContent_tv= (TextView) view.findViewById(R.id.illegalContent_tv);
            holder.illegalStatus_tv= (TextView) view.findViewById(R.id.illegalStatus_tv);
            holder.illegalTime_tv= (TextView) view.findViewById(R.id.illegalTime_tv);
            holder.illegalTips_tv= (TextView) view.findViewById(R.id.illegalTips_tv);

            holder.degree_tv= (TextView) view.findViewById(R.id.degree_tv);
            holder.price_tv= (TextView) view.findViewById(R.id.price_tv);
            holder.count_tv= (TextView) view.findViewById(R.id.count_tv);
            holder.latefee_tv= (TextView) view.findViewById(R.id.latefee_tv);

        }else {
            view = map.get(i);
            holder = (ViewHolder) view.getTag();
        }

        holder.isselect_cb.setClickable(false);
        holder.isselect_cb.setEnabled(false);

        holder.illegalContent_tv.setText(violations.get(i).getReason()+"");
        holder.illegalAdd_tv.setText(violations.get(i).getLocation()+"");
        holder.illegalTime_tv.setText(violations.get(i).getTime()+"");
        holder.illegalTips_tv.setText(violations.get(i).getRemark()+"");
        holder.illegalStatus_tv.setText(violations.get(i).getOrderstatus()+"");

        holder.degree_tv.setText(""+violations.get(i).getDegree());
        holder.count_tv.setText("￥"+violations.get(i).getCount());
        holder.price_tv.setText("￥"+violations.get(i).getPrice());
        holder.latefee_tv.setText("￥"+violations.get(i).getLatefee());

        return view;
    }

    class ViewHolder{
        CheckBox isselect_cb;
        TextView illegalStatus_tv,illegalAdd_tv,illegalTime_tv,illegalContent_tv,illegalTips_tv;


        TextView degree_tv,price_tv,count_tv,latefee_tv;//扣分，服务费，罚款，滞纳金
    }
}
