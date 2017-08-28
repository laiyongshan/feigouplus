package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 * 普通处理违章列表的适配器
 */
public class IllegalListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public ViewHolder holder;
    private List<Violation> violations;
    private Intent intent = new Intent();

    private int searchtype=0;//1代办方式，2本人本车或否
    private int type=0;//1 实时报价   2  待报价

    private Context mContext;
    public final static String SELECTED_ACTION1 = "com.youhecheguanjia.illegallistselect";
    public final static String SELECTED_ACTION2="com.youhecheguanjia.quotedillegal_select";

    public  List<Boolean> mChecked= new ArrayList<Boolean>();

    public static HashMap<Integer, View> map = new HashMap<Integer,View>();

    public IllegalListAdapter(Context mContext, List<Violation> violations,int searchtype,int type) {
        this.mContext = mContext;
        this.searchtype=searchtype;
        this.type=type;
        mInflater = LayoutInflater.from(mContext);
        this.violations = violations;
        for (int i = 0; i < violations.size(); i++) {
            mChecked.add(true);
        }
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }


    @Override
    public int getCount() {
        return violations.size();
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        if (map.get(i) == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_illegal_lv, null);

            holder.isselect_cb= (CheckBox) convertView.findViewById(R.id.isselect_cb);
            holder.illegalAdd_tv= (TextView) convertView.findViewById(R.id.illegalAdd_tv);
            holder.illegalContent_tv= (TextView) convertView.findViewById(R.id.illegalContent_tv);
            holder.illegalStatus_tv= (TextView) convertView.findViewById(R.id.illegalStatus_tv);
            holder.illegalTime_tv= (TextView) convertView.findViewById(R.id.illegalTime_tv);
            holder.illegalTips_tv= (TextView) convertView.findViewById(R.id.illegalTips_tv);
            holder.illegal_statuts_iv= (ImageView) convertView.findViewById(R.id.illegal_statuts_iv);

            holder.degree_tv= (TextView) convertView.findViewById(R.id.degree_tv);
            holder.price_tv= (TextView) convertView.findViewById(R.id.price_tv);
            holder.count_tv= (TextView) convertView.findViewById(R.id.count_tv);
            holder.latefee_tv= (TextView) convertView.findViewById(R.id.latefee_tv);

            if ((violations.get(i).getPickone() ==1)||violations.get(i).getIscommit()==-1) {
//                holder.isselect_cb.setClickable(false);
                holder.isselect_cb.setEnabled(false);
            }

            final int p = i;
            holder.isselect_cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    mChecked.set(p, cb.isChecked());
                    if (cb.isChecked()) {
                        intent.putExtra("isSelect", 1);
                    } else {
                        intent.putExtra("isSelect", 0);
                    }

                    intent.putExtra("count", violations.get(i).getCount());
                    intent.putExtra("degree", violations.get(i).getDegree());
                    intent.putExtra("price", violations.get(i).getPrice());
                    intent.putExtra("quotedprice",violations.get(i).getQuotedprice());
                    intent.putExtra("lateFre",violations.get(i).getLatefee());

                    if(type==1) {
                        intent.setAction(SELECTED_ACTION1);
                    }else if(type==2){
                        intent.setAction(SELECTED_ACTION2);
                    }

                    mContext.sendBroadcast(intent);
                }
            });
            convertView.setTag(holder);
        } else {
            convertView = map.get(i);
            holder = (ViewHolder) convertView.getTag();
        }

        if(violations.get(i).getIscommit()==1){
            holder.isselect_cb.setChecked(true);
            holder.isselect_cb.setClickable(true);
            holder.isselect_cb.setVisibility(View.VISIBLE);
        }else{
            holder.isselect_cb.setChecked(false);
            holder.isselect_cb.setClickable(false);
            holder.isselect_cb.setVisibility(View.GONE);
        }

        holder.illegalContent_tv.setText(violations.get(i).getReason()+"");
        holder.illegalAdd_tv.setText(violations.get(i).getLocation()+"");
        holder.illegalTime_tv.setText(violations.get(i).getTime()+"");
        holder.illegalTips_tv.setText(violations.get(i).getRemark()+"");
        holder.illegalStatus_tv.setText(violations.get(i).getOrderstatus()+"");

        holder.degree_tv.setText(""+violations.get(i).getDegree());
        holder.count_tv.setText("￥"+violations.get(i).getCount());

        if(violations.get(i).getPrice()!=0) {
            holder.price_tv.setText("￥" + violations.get(i).getPrice());
        }else{
            holder.price_tv.setText("待报价");
        }

        holder.latefee_tv.setText("￥"+violations.get(i).getLatefee());

        if(violations.get(i).getIscommit()==-1){
            holder.illegal_statuts_iv.setImageResource(R.drawable.illegal_committed_icon);
        }else if(violations.get(i).getPickone()==1){
            holder.illegal_statuts_iv.setImageResource(R.drawable.illegal_dontpick_icon);
        }else{
            holder.illegal_statuts_iv.setImageBitmap(null);
        }


        return convertView;
    }

    class ViewHolder {
        TextView illegalStatus_tv,illegalAdd_tv,illegalTime_tv,illegalContent_tv,illegalTips_tv;
        CheckBox isselect_cb;//选择多选框
        ImageView illegal_statuts_iv;

        TextView degree_tv,price_tv,count_tv,latefee_tv;//扣分，服务费，罚款，滞纳金
    }

}
