package com.thepos.biz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dynamicode.p27.companyyh.bluetooth4.DcBleDevice;
import com.example.youhe.youhecheguanjiaplus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class DeviceListAdapter extends BaseAdapter{
    private List<DcBleDevice> list;

    public DeviceListAdapter(){

        list = new ArrayList<>();


    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view==null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.devicelistadapter_lay,viewGroup,false);

            viewHolder.textView1 = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);

        }else {

            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView1.setText(list.get(i).getDeviceName());

        return view;
    }

    class ViewHolder{
        private TextView textView1;
    }

    public void addDate(List<DcBleDevice> newList){
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public void delete(){
        list.clear();
        notifyDataSetChanged();
    }

}
