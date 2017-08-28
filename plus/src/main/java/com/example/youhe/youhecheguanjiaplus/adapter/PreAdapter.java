package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public class PreAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private int selectedPosition = -1;
    private List<Province> provinceList=new ArrayList<Province>();

    public PreAdapter(Context context, List<Province> provinceList){
        this.context = context;
        this.provinceList=provinceList;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return provinceList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder  holder = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.multilist_item, null);
            holder = new ViewHolder();
            holder.pre_textview =(TextView)convertView.findViewById(R.id.pre_textview);
            holder.layout=(RelativeLayout)convertView.findViewById(R.id.colorlayout);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(selectedPosition == position)
        {
            holder.pre_textview.setTextColor(Color.rgb(0,166,121));
            holder.layout.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.pre_textview.setTextColor(Color.GRAY);
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
        }


        holder.pre_textview.setText(provinceList.get(position).getProvincePrefix());

        return convertView;
    }

    public static class ViewHolder{
        public TextView pre_textview;
        public RelativeLayout layout;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

}
