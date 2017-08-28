package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.City122;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class RegisterCityListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ViewHolder holder;
    private List<City122> list;
    private Context context;

    public RegisterCityListAdapter(Context context,List<City122> list){
        this.context=context;
        mInflater=LayoutInflater.from(context);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        if(view==null){
            holder=new ViewHolder();
            view=mInflater.inflate(R.layout.item_textview,null);

            holder.city122_tv= (TextView) view.findViewById(R.id.car_type_tv);

            view.setTag(holder
            );

        }else{
            holder= (ViewHolder) view.getTag();
        }

        holder.city122_tv.setText(list.get(i).getCityName());

        return view;
    }

    class ViewHolder{
        TextView city122_tv;
    }
}
