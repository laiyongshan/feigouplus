package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.TradingModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */

public class TradingRecAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public ViewHolder holder;
    private Context  context;
    private List<TradingModel> tradingModelList;

    final int VIEW_TYPE = 2;

    public static HashMap<Integer, View> map = new HashMap<Integer,View>();

    public TradingRecAdapter(Context context,List<TradingModel> tradingModelList){
        this.context=context;
        this.tradingModelList=tradingModelList;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return tradingModelList.size()+50;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        int type = 0;
        if (position == 0) {
            type = 1;
        }
        else if(position==1){
            type=0;
        }
        return type;
    }

    @Override
    public int getViewTypeCount(){// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
        return VIEW_TYPE;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(map.get(position)==null){
            holder = new ViewHolder();
            convertView=mInflater.inflate(R.layout.item_trading_reco,null);
        }else{
            convertView = map.get(position);
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder{
        TextView trading_type_tv,trading_from_tv,trading_money_tv,trading_date_tv;
    }

    private class TopViewHolder {
        TextView alpha; // 首字母标题
        TextView name;
    }
}
