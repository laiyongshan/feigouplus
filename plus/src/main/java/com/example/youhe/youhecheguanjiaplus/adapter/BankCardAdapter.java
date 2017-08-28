package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.BankCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class BankCardAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public ViewHolder holder;
    public static HashMap<Integer, View> map = new HashMap<Integer,View>();
    private Context mContext;
    private List<BankCard> bankCardList=new ArrayList<BankCard>();

    public BankCardAdapter(Context mContext,List<BankCard> bankCardList){

        this.mContext=mContext;
        mInflater = LayoutInflater.from(mContext);
        this.bankCardList=bankCardList;

    }

    @Override
    public int getCount() {
        return bankCardList.size();
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
            convertView=mInflater.inflate(R.layout.item_bankcardlist,null);
            holder.bank_name_tv= (TextView) convertView.findViewById(R.id.bank_name_tv);
            holder.bankcard_code_tv= (TextView) convertView.findViewById(R.id.bankcard_code_tv);
        }else{
            convertView = map.get(i);
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bank_name_tv.setText(bankCardList.get(i).getBank_name()+"");

        if(bankCardList.get(i).getBank_code().length()>=4) {
            holder.bankcard_code_tv.setText("尾号 " + bankCardList.get(i).getBank_code().substring(bankCardList.get(i).getBank_code().length() - 4) + " 储蓄卡");
        }

        return convertView;
    }


    class ViewHolder{
        TextView bank_name_tv;
        TextView bankcard_code_tv;
    }

}
