package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27 0027.
 * 12分特殊处理列表的适配器
 */
public class Illegal12ListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public ViewHolder holder;
    private List<Violation> violations;
    private List<A> list;
    private Intent intent = new Intent();

    private Context mContext;
    public final static String SELECTED_ACTION = "com.youhecheguanjia.illegallistselect";

    public static List<Boolean> mChecked;
    public static HashMap<Integer, View> map = new HashMap<Integer, View>();

    private int searchtype=0;//1代办方式，2本人本车或否

    public Illegal12ListAdapter(Context mContext, List<Violation> violations,int searchtype) {
        this.mContext = mContext;
        this.searchtype=searchtype;
        mInflater = LayoutInflater.from(mContext);
        list = new ArrayList<A>();
        this.violations = violations;
        mChecked = new ArrayList<Boolean>();
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
            convertView = mInflater.inflate(R.layout.list_illegal_item, null);
            if (searchtype==2) {
                convertView.setBackgroundColor(Color.argb(90, 48, 48, 48));
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
            holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
            holder.canprocess_tv = (TextView) convertView.findViewById(R.id.canprocess_tv);
            holder.reson_tv = (TextView) convertView.findViewById(R.id.reson_tv);
            holder.degree_tv = (TextView) convertView.findViewById(R.id.degree_tv);
            holder.location_tv = (TextView) convertView.findViewById(R.id.location_tv);
            holder.status_tv = (TextView) convertView.findViewById(R.id.status_tv);
            holder.poundge_tv = (TextView) convertView.findViewById(R.id.poundge_tv);
            holder.count_tv = (TextView) convertView.findViewById(R.id.count_tv);
            holder.isselect_cb = (CheckBox) convertView.findViewById(R.id.isselect_cb);
            holder.owner_handler_mark_tv= (TextView) convertView.findViewById(R.id.owner_handler_mark_tv);
            holder.overdue_fine_tv= (TextView) convertView.findViewById(R.id.overdue_fine_tv);
            holder.overdue_fine_layout= (LinearLayout) convertView.findViewById(R.id.overdue_fine_layout);

            if ((violations.get(i).getPickone() ==1)&&(violations.get(i).getOrderstatus().equals("0"))) {
//                holder.isselect_cb.setClickable(false);
                holder.isselect_cb.setEnabled(false);
            }

            final int p = i;
            holder.isselect_cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
//                if(violations.get(i).getCategory().equals("现场单")){
//                    cb.setChecked(false);
//                   cb.setClickable(false);
//                }else{
//                    cb.setChecked(true);
//                    cb.setClickable(true);
//                }
                    mChecked.set(p, cb.isChecked());
//                    intent.putExtra("isSelect12",cb.isChecked());
                    intent.putExtra("price", violations.get(i).getPrice());
                    intent.putExtra("count", violations.get(i).getCount());
                    intent.putExtra("degree", violations.get(i).getDegree());
                    intent.putExtra("lateFre",violations.get(i).getLatefee());

                    intent.setAction(SELECTED_ACTION);
                    mContext.sendBroadcast(intent);
                }
            });
            convertView.setTag(holder);
        } else {
            convertView = map.get(i);
            holder = (ViewHolder) convertView.getTag();
        }

        holder.time_tv.setText(violations.get(i).getTime());
        holder.reson_tv.setText(violations.get(i).getReason());

//        if(searchtype==2){
//            holder.owner_handler_mark_tv.setText("本人本车办理");
//        }else if(searchtype==1){
//            holder.owner_handler_mark_tv.setText("可在线办理");
//        }

        holder.degree_tv.setText("扣分：" + violations.get(i).getDegree());
        holder.location_tv.setText(violations.get(i).getLocation());

        if(violations.get(i).getIscommit()==1){
            holder.isselect_cb.setChecked(true);
            holder.isselect_cb.setClickable(true);
            holder.isselect_cb.setVisibility(View.VISIBLE);
        }else{
            holder.status_tv.setTextColor(Color.GRAY);
            holder.isselect_cb.setChecked(false);
            holder.isselect_cb.setClickable(false);
            holder.isselect_cb.setVisibility(View.GONE);
        }

//        if (violations.get(i).getOrderstatus().equals("0")) {
//            Log.i("TAG","违章状态：未处理"+violations.get(i).getOrderstatus());
//            holder.status_tv.setText("未处理");
//            holder.isselect_cb.setChecked(true);
//            holder.isselect_cb.setClickable(true);
//            holder.isselect_cb.setVisibility(View.VISIBLE);
//        } else if (violations.get(i).getOrderstatus().equals("2")) {
//            Log.i("TAG","违章状态：处理中");
//            holder.status_tv.setText("处理中");
//            holder.status_tv.setTextColor(Color.GRAY);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//        } else if (violations.get(i).getOrderstatus().equals("1")) {
//            Log.i("TAG","违章状态：未支付");
//            holder.status_tv.setText("未支付");
//            holder.status_tv.setTextColor(Color.BLACK);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//        } else if(violations.get(i).getOrderstatus().equals("3")){
//            Log.i("TAG","违章状态：处理完成");
//            holder.status_tv.setText("处理完成");
//            holder.status_tv.setTextColor(Color.BLACK);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//        }else if(violations.get(i).getOrderstatus().equals("22")){
//            holder.status_tv.setText("处理中");
//            holder.status_tv.setTextColor(Color.GRAY);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//        }

//        if (violations.get(i).getCa
// tegory().equals("现场单")) {
//            holder.canprocess_tv.setText("不可代办");
//            holder.canprocess_tv.setTextColor(Color.YELLOW);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//            holder.status_tv.setText("现场单");
//        }else
        if (violations.get(i).getQuotedprice()==-1) {
            holder.canprocess_tv.setText("不可代办");
            holder.canprocess_tv.setTextColor(Color.YELLOW);
            holder.isselect_cb.setChecked(false);
            holder.isselect_cb.setClickable(false);
            holder.isselect_cb.setVisibility(View.GONE);
            holder.status_tv.setText("不可代办");
            convertView.setBackgroundColor(Color.argb(90, 48, 48, 48));
//            holder.status_tv.setText("现场单");
        }else if (searchtype==2) {
            holder.canprocess_tv.setText("不可代办");
            holder.canprocess_tv.setTextColor(Color.YELLOW);
            holder.isselect_cb.setChecked(false);
            holder.isselect_cb.setClickable(false);
            holder.isselect_cb.setVisibility(View.GONE);
        } else if (violations.get(i).getCount()==0||searchtype==2) {
//            holder.canprocess_tv.setText("不可代办");
//            holder.canprocess_tv.setTextColor(Color.YELLOW);
//            holder.isselect_cb.setChecked(false);
//            holder.isselect_cb.setClickable(false);
//            holder.isselect_cb.setVisibility(View.GONE);
//            holder.status_tv.setText("未处理");
        } else {
            holder.canprocess_tv.setText("可代办");
            holder.isselect_cb.setChecked(true);
        }


        if (violations.get(i).getPrice()!=0) {
            holder.poundge_tv.setText("手续费：￥" + violations.get(i).getPrice());
        } else {
            holder.poundge_tv.setText("待报价");
            holder.poundge_tv.setTextColor(Color.RED);
        }

        holder.isselect_cb.setChecked(mChecked.get(i));

        if(violations.get(i).getCategory().equals("现场单")){
            holder.overdue_fine_layout.setVisibility(View.VISIBLE);
            holder.overdue_fine_tv.setVisibility(View.VISIBLE);
            holder.overdue_fine_tv.setText("现场单滞纳金：￥"+violations.get(i).getLatefee());
        }

        holder.count_tv.setText("罚款：￥" + violations.get(i).getCount());


//       if(searchtype==1&& (!holder.canprocess_tv.getText().toString().equals("不可代办"))
//                &&!holder.status_tv.getText().toString().equals("未支付")){
//            holder.owner_handler_mark_tv.setText("特殊处理违章，该违章的罚款金额并不包含在共计费用中，提交后系统将在一个工作日内为您报价");
//        }else if(holder.status_tv.getText().toString().equals("未支付")){
//            holder.owner_handler_mark_tv.setText("该违章已提交，用户可等待系统报价后到主界面的订单查询中心的未支付页面选择订单支付，我们将火速为您办理");
//        }else if(searchtype==2){
//            holder.owner_handler_mark_tv.setText("本人本车办理，不可代办，敬请谅解");
//        }else{
//           holder.owner_handler_mark_tv.setText("不可代办，敬请谅解");
//       }
//
//        if(!holder.status_tv.getText().toString().equals("未支付")&&violations.get(i).getQuotedprice().equals("2")){
//            holder.owner_handler_mark_tv.setText("提交后，用户可等待系统报价后到主界面的订单查询中心的未支付页面选择订单支付");
//        }else if(holder.status_tv.getText().toString().equals("未支付")&&violations.get(i).getQuotedprice().equals("2")){
//            holder.owner_handler_mark_tv.setText("该违章已提交，用户可等待系统报价后到主界面的订单查询中心的未支付页面选择订单支付，我们将火速为您办理");
//        }
//
//        if(violations.get(i).getQuotedprice().equals("-1")){
//            holder.owner_handler_mark_tv.setText("不可代办，敬请谅解");
//        }else if(violations.get(i).getCategory().equals("现场单")){
//            holder.owner_handler_mark_tv.setText("现场单违章");
//        }

        holder.owner_handler_mark_tv.setText(violations.get(i).getRemark());


        return convertView;
    }

    class ViewHolder {
        TextView time_tv;//时间
        TextView canprocess_tv;//是否可代办
        TextView reson_tv;//违章内容
        TextView degree_tv;//扣分
        TextView location_tv;//违章地点
        TextView status_tv;//处理状态
        TextView poundge_tv;//手续费
        TextView count_tv;//罚款金额
        CheckBox isselect_cb;//选择多选框
        TextView owner_handler_mark_tv;//本人本车办理标示提示文字
        TextView overdue_fine_tv;//滞纳金
        LinearLayout overdue_fine_layout;
    }

    class A {
        public static final int TYPE_CHECKED = 1;
        public static final int TYPE_NOCHECKED = 0;
        int type;

        public A(int type) {
            this.type = type;
        }
    }
}
