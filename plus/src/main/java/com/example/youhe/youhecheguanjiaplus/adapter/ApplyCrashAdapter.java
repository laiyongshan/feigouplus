package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.ApplyCrashItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class ApplyCrashAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public ViewHolder holder;
    public static HashMap<Integer, View> map = new HashMap<Integer,View>();
    private Context mContext;

    private List<ApplyCrashItem> withdrawals_list;

    public ApplyCrashAdapter(Context mContext, List<ApplyCrashItem> withdrawals_list){
        this.mContext=mContext;
        mInflater = LayoutInflater.from(mContext);

        this.withdrawals_list=withdrawals_list;
    }

    @Override
    public int getCount() {
        return withdrawals_list.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if(map.get(i)==null){
            holder = new ViewHolder();
            convertView=mInflater.inflate(R.layout.item_apply_crash_lv,null);
            holder.order_serial_number_tv= (TextView) convertView.findViewById(R.id.order_serial_number_tv);
            holder.statut_tv= (TextView) convertView.findViewById(R.id.statut_tv);
            holder.apply_money_tv= (TextView) convertView.findViewById(R.id.apply_money_tv);
            holder.note_tv= (TextView) convertView.findViewById(R.id.note_tv);
            holder.apply_time_tv= (TextView) convertView.findViewById(R.id.apply_time_tv);
            holder.finish_time_tv= (TextView) convertView.findViewById(R.id.finish_time_tv);
            holder.withdrawal_fee_tv= (TextView) convertView.findViewById(R.id.withdrawal_fee_tv);
            holder.to_the_account_tv= (TextView) convertView.findViewById(R.id.to_the_account_tv);
        }else{
            convertView = map.get(i);
            holder = (ViewHolder) convertView.getTag();
        }

        holder.order_serial_number_tv.setText(withdrawals_list.get(i).getPay_flowing().toString());
        holder.statut_tv.setText(withdrawals_list.get(i).getStatus()+"");

        holder.apply_money_tv.setText("￥"+withdrawals_list.get(i).getMoney());
        holder.note_tv.setText("备注："+withdrawals_list.get(i).getRemark());
        holder.apply_time_tv.setText("申请时间："+withdrawals_list.get(i).getCreatetimestr());
        holder.finish_time_tv.setVisibility(View.VISIBLE);
        holder.to_the_account_tv.setVisibility(View.VISIBLE);
        holder.to_the_account_tv.setText("￥"+withdrawals_list.get(i).getAmount_money());
        holder.withdrawal_fee_tv.setVisibility(View.VISIBLE);
        holder.withdrawal_fee_tv.setText("￥"+withdrawals_list.get(i).getFee());
        holder.finish_time_tv.setText("完成时间：" + withdrawals_list.get(i).getFinishtimestr());
        holder.finish_time_tv.setVisibility(View.GONE);
        return convertView;
    }

    class ViewHolder{
        TextView order_serial_number_tv;
        TextView statut_tv;
        TextView apply_money_tv;
        TextView note_tv;
        TextView apply_time_tv;
        TextView finish_time_tv;
        TextView withdrawal_fee_tv;
        TextView to_the_account_tv;
    }
}
