package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2 0002.
 * 订单查询中ListView Adapter
 */
public class OrderListViewAdapter extends BaseAdapter {
    private Context context;
    private List<OrderInquiry.DataBean> datalist;
    private Map<Integer,Boolean> checkBoxMap;
//    private OrderInquiry orderInquiry;
    private OrderInquiry.DataBean bean;
    private String type;//类型
    private boolean isLongClick;// 是否全选状态
    private String zonjiage;//总金额

    public OrderListViewAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
        datalist = new ArrayList<>();
        checkBoxMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorderlistlayout, parent, false);
            viewHolder.titleTv = (TextView) convertView.findViewById(R.id.tv1);//标题
            viewHolder.contentTv = (TextView) convertView.findViewById(R.id.tv2);//内容
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_data);//时间
            viewHolder.stateTv = (TextView) convertView.findViewById(R.id.order_stuta);//状态
            viewHolder.textChePai = (TextView) convertView.findViewById(R.id.carnum_tv);//车牌号
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.isselect);//删除选择框
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.identifying);//用图片显示违章类线
            viewHolder.zonjiage = (TextView) convertView.findViewById(R.id.zonjiage);//总价格

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrderInquiry.DataBean bataBean = datalist.get(i);
        bean = bataBean;


        if (bataBean.getCreatetimestr()!=null){
            viewHolder.timeTv.setText(bataBean.getCreatetimestr());
        }
        if (bataBean.getProprefix() + bataBean.getCarnumber()!=null){
            String chepaihao = bataBean.getProprefix() + bataBean.getCarnumber();//车牌号
            viewHolder.textChePai.setText(chepaihao);//设置车牌号
        }

        if (bataBean.getType().equals("5")){//如果是其他类型就需要判断是否报价
            if (bataBean.getTotalprice().equals("0")){//如果总金额是0就不显示金额
                viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                viewHolder.contentTv.setText("......");
                viewHolder.titleTv.setText("");
            }else {
                viewHolder.zonjiage.setText(bataBean.getTotalprice());//如果总金额不是零就要显示金额出来
            }

        }else {

            viewHolder.zonjiage.setText(bataBean.getTotalprice());//如果总金额不是零就要显示金额出来
            if (bataBean.getDetails().get(0).getLocation()!=null){
                viewHolder.titleTv.setText(bataBean.getDetails().get(0).getLocation());
            }
            if (bataBean.getDetails().get(0).getReason()!=null){
                viewHolder.contentTv.setText(bataBean.getDetails().get(0).getReason());
            }
            zonjiage = zon(bataBean);//得到总价格
            viewHolder.zonjiage.setText("￥"+bataBean.getTotalprice());//设置总价格

        }



        // 判断模式。
        if (isLongClick) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxMap.put(i,isChecked);
            }
        });


        String type = bataBean.getType();
        switch (type){
            case "1":
                viewHolder.imageView.setImageResource(R.drawable.pu);//普通订单
                break;
            case "2":

                viewHolder.imageView.setImageResource(R.drawable.daibaojia);//12分订单
                for (int j = 0; j <bataBean.getDetails().size() ; j++) {
                    if (bataBean.getDetails().get(j).getPrice().equals("0")){
                        viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                        break;
                    }else {
                        viewHolder.zonjiage.setText(bataBean.getTotalprice());
                    }
                }


                break;
            case "3":
                viewHolder.imageView.setImageResource(R.drawable.icon_benren_order
                );//本人本车订单
                break;
            case "4":
                viewHolder.imageView.setImageResource(R.drawable.daibaojia);//待报价
                for (int j = 0; j < bataBean.getDetails().size(); j++) {//如果有一个违章服务费为0就显示未报价
                    if (bataBean.getDetails().get(j).getPrice().equals("0")){
                        viewHolder.zonjiage.setText("待报价");//如果总金额不是零就要显示金额出来
                        break;
                    }
                }
                break;
            case "5":
                viewHolder.imageView.setImageResource(R.drawable.qita);//其他
                break;
        }

        String status = bataBean.getStatus();
        if (status!=null){
            if (status.equals("1")) {
                viewHolder.stateTv.setText("未支付");
            }
            if (status.equals("2")) {
                viewHolder.stateTv.setText("处理中");
            }
            if (status.equals("3")) {
                viewHolder.stateTv.setText("处理完成");
            }
            if (status.equals("-1")) {
                viewHolder.stateTv.setText("处理失败");
            }
        }

        return convertView;
    }

    class ViewHolder {

        private TextView titleTv, contentTv, timeTv, stateTv,textChePai,zonjiage;

        private CheckBox checkBox;

        private ImageView imageView;
    }



    /**
     * 得到总价格
     * @param beans
     * @return
     */
    public String zon(OrderInquiry.DataBean beans){//计算总价格
        int zonfakuan = 0;
        int zonfuwu = 0;
        int zonjiner = 0;

        zonjiage="";
        String zo ="";
        for (int i = 0; i <beans.getDetails().size() ; i++) {//计算总罚金额和总服务费
            int f = Integer.parseInt(beans.getDetails().get(i).getCount());//得到罚款金额
            int fuwufei = Integer.parseInt(beans.getDetails().get(i).getPrice());//总服务费

            zonfuwu += fuwufei;//总服务费
            zonfakuan += f;//总罚款


        }
        zonjiner = zonfuwu+ zonfakuan;//计算总价格
        NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);//数字金币格式化
        zo= usFormat.format(zonjiner);



        return "￥"+zo;
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
            checkBoxMap.put(i,false);
        }

        notifyDataSetChanged();
    }

    /**
     * checkBoxMap重新赋值
     */
    public void checkMap(){
        checkBoxMap.clear();
        for (int i = 0; i < datalist.size(); i++) {
            checkBoxMap.put(i,false);
        }
        notifyDataSetChanged();
    }

    /**
     * 得到全部数据
     * @return
     */
    public List<OrderInquiry.DataBean> getDatalist(){

        return datalist;
    }

    /**
     * 删除哪一项
     * @param pos
     */
    public void deleteDate(int pos) {
        datalist.remove(pos);
        notifyDataSetChanged();

    }

    /**
     * 清楚除全部数据
     */
    public void clearDate() {
        datalist.clear();
//        notifyDataSetChanged();
    }

//    /**
//     * 传人类型
//     */
//    public void addForm(OrderInquiry orderInquiry) {
//        this.orderInquiry = orderInquiry;
//    }

    /**
     * 显示隐藏CheckBox
     * @param flag
     */
    public void setIsLongClick(boolean flag) {
        this.isLongClick = flag;
    }

    /**
     * 得到MAP
     * @return
     */
    public Map<Integer,Boolean> getCheckBox(){

        return checkBoxMap;
    }



}
