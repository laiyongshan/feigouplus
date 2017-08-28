package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;
import com.example.youhe.youhecheguanjiaplus.ui.base.OrderdetaiActivityV2;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2 0002.
 * 订单查询中ListView Adapter
 */
public class OrderListViewAdapter2 extends BaseAdapter {
    private Context context;
    private List<OrderInquiry.DataBean> datalist;
    private Map<Integer, Boolean> checkBoxMap;
    //    private OrderInquiry orderInquiry;
    private OrderInquiry.DataBean bean;
    private String type;//类型
    private boolean isLongClick;// 是否全选状态

    private int orderListType;//订单列表类型 1：违章订单  2：年检订单

    public OrderListViewAdapter2(Context context, String type,int orderListType) {
        this.context = context;
        this.type = type;
        datalist = new ArrayList<>();
        checkBoxMap = new HashMap<>();

        this.orderListType=orderListType;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < datalist.size())
            return datalist.get(i);
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorderlistlayout_v2, parent, false);

            viewHolder.linearTimeList = (LinearLayout) convertView.findViewById(R.id.linear_time_list);
            viewHolder.stateTv = (TextView) convertView.findViewById(R.id.order_stuta);//状态
            viewHolder.textChePai = (TextView) convertView.findViewById(R.id.carnum_tv);//车牌号
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.identifying);//用图片显示违章类线
            viewHolder.zonjiage = (TextView) convertView.findViewById(R.id.zonjiage);//总价格
            viewHolder.orderNumber = (TextView) convertView.findViewById(R.id.order_number); //订单号
            viewHolder.imageStatus = (ImageView) convertView.findViewById(R.id.image_status);
            viewHolder.fillingMoney= (TextView) convertView.findViewById(R.id.filling_money);//补款

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrderInquiry.DataBean bataBean = datalist.get(i);
        if (bataBean != null) {
            bean = bataBean;

            //订单编号
            viewHolder.orderNumber.setText("订单编号：" + (StringUtils.isEmpty(bataBean.getOrdercode()) ? "" : bataBean.getOrdercode()) + "");
            //设置过期标识
            if ((bean.getTimeout_status() != null && bean.getTimeout_status().equals("1"))) {//过期
                viewHolder.imageStatus.setVisibility(View.VISIBLE);
                viewHolder.imageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.yiguoqi));
            } else {
                viewHolder.imageStatus.setVisibility(View.GONE);
            }
            //时间
            viewHolder.linearTimeList.removeAllViews();
            if (bataBean.getTimeList() != null && bataBean.getTimeList().size() > 0) {
                for (int j = 0; j < bataBean.getTimeList().size(); j++) {
                    TextView textviewTime = new TextView(context);
                    textviewTime.setText(bataBean.getTimeList().get(j).getTimeName()
                            + ":" + bataBean.getTimeList().get(j).getTime());
                    textviewTime.setAlpha(0.8f);
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textviewTime.setLayoutParams(p);
                    textviewTime.setTextSize(11);
                    viewHolder.linearTimeList.addView(textviewTime);
                }
            }

            if (bataBean.getProprefix() != null||bataBean.getCarnumber() != null) {
                String chepaihao = bataBean.getProprefix()+ bataBean.getCarnumber();//车牌号
                viewHolder.textChePai.setText(chepaihao.replaceFirst("null",""));//设置车牌号
            } else
                viewHolder.textChePai.setText("未知车牌号");//设置车牌号

            if (bataBean.getType().equals("5")) {//如果是其他类型就需要判断是否报价
                if (bataBean.getTotalprice().equals("0")) {//如果总金额是0就不显示金额
                    viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                }else{
                    viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");//如果总金额不是零就要显示金额出来
                }
            } else {
                viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");//如果总金额不是零就要显示金额出来
                viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");//设置总价格
            }


            String type = bataBean.getType();
            switch (type) {
                case "1":
                    viewHolder.imageView.setImageResource(R.drawable.pu);//普通订单
                    break;
                case "2":
                    viewHolder.imageView.setImageResource(R.drawable.teshu);//12分订单

                    if (StringUtils.isEmpty(bataBean.getTotalprice()) || bataBean.getTotalprice().equals("0"))
                        viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                    else
                        viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");
                    break;
                case "3":
                    viewHolder.imageView.setImageResource(R.drawable.icon_benren_order
                    );//本人本车订单
                    break;
                case "4":
                    viewHolder.imageView.setImageResource(R.drawable.daibaojia);//待报价

                    if (StringUtils.isEmpty(bataBean.getTotalprice()) || bataBean.getTotalprice().equals("0"))
                        viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                    else
                        viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");
                    break;
                case "5":
                    viewHolder.imageView.setImageResource(R.drawable.qita);//其他

                    if (StringUtils.isEmpty(bataBean.getTotalprice()) || bataBean.getTotalprice().equals("0"))
                        viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                    else
                        viewHolder.zonjiage.setText("¥" + bataBean.getTotalprice() + "");
                    break;
            }

            if(orderListType==2){
                viewHolder.imageView.setImageResource(R.drawable.nianjian);//年检订单
            }


            //设置订单状态
            viewHolder.stateTv.setText(StringUtils.isEmpty(bataBean.getStatusName()) ? "" : bataBean.getStatusName());
            //去补款
            if (!StringUtils.isEmpty(bataBean.getStatus())&&bataBean.getStatus().equals("23")){//23待补款24补款处理中
                viewHolder.fillingMoney.setVisibility(View.VISIBLE);
            }else
                viewHolder.fillingMoney.setVisibility(View.GONE);

            //去补款  跳转到详情按钮
            viewHolder.fillingMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inter = new Intent(context, OrderdetaiActivityV2.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("datalist",  datalist.get(i));
                    inter.putExtra("bundle", bundle);
                    inter.putExtra("ordertype",2);//补款订单
                    context.startActivity(inter);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {

        private TextView stateTv, textChePai, zonjiage, orderNumber,fillingMoney;//timeCreate,timeCompete,

        private ImageView imageView;
        private ImageView imageStatus;
        private LinearLayout linearTimeList;
    }


    /**
     * 得到全部数据
     *
     * @return
     */
    public OrderInquiry.DataBean getlist() {

        return bean;
    }


    /**
     * 添加新数据
     *
     * @param newsData
     */
    public void addData(List<OrderInquiry.DataBean> newsData) {

        datalist.addAll(newsData);
        for (int i = 0; i < datalist.size(); i++) {
            checkBoxMap.put(i, false);
        }

        notifyDataSetChanged();
    }

    /**
     * checkBoxMap重新赋值
     */
    public void checkMap() {
        checkBoxMap.clear();
        for (int i = 0; i < datalist.size(); i++) {
            checkBoxMap.put(i, false);
        }
        notifyDataSetChanged();
    }

    /**
     * 得到全部数据
     *
     * @return
     */
    public List<OrderInquiry.DataBean> getDatalist() {

        return datalist;
    }

    /**
     * 删除哪一项
     *
     * @param pos
     */
    public void deleteDate(int pos) {
        try {
            if (pos < datalist.size())
                datalist.remove(pos);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 清楚除全部数据
     */
    public void clearDate() {
        if (datalist != null)
            datalist.clear();
        notifyDataSetChanged();
    }

    /**
     * 传人类型
     */
//    public void addForm(OrderInquiry orderInquiry) {
//        this.orderInquiry = orderInquiry;
//    }

    /**
     * 显示隐藏CheckBox
     *
     * @param flag
     */
    public void setIsLongClick(boolean flag) {
        this.isLongClick = flag;
    }

    /**
     * 得到MAP
     *
     * @return
     */
    public Map<Integer, Boolean> getCheckBox() {

        return checkBoxMap;
    }


}
