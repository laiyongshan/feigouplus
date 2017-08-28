package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.OrderInquiry;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by Administrator on 2016/9/24 0024.
 * 订单详情的适配器
 */
public class OrderdeListAdater extends BaseAdapter {

    private Context context;
    private OrderInquiry.DataBean bean;

    public OrderdeListAdater(Context context, OrderInquiry.DataBean bean) {
        this.context = context;
        this.bean = bean;

    }

    @Override
    public int getCount() {

        return bean.getDetails().size();
    }

    @Override
    public Object getItem(int i) {
        return bean.getDetails().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdeya_listview_lay, parent, false);
            viewHolder.carnum_tv= (TextView) convertView.findViewById(R.id.car_num_tv);//车牌号码
            viewHolder.neiron = (TextView) convertView.findViewById(R.id.tv2_1);//内容
            viewHolder.didian = (TextView) convertView.findViewById(R.id.tv2_2);//地点
            viewHolder.koufen = (TextView) convertView.findViewById(R.id.tv2_4);//扣分情况
            viewHolder.shijian = (TextView) convertView.findViewById(R.id.tv2_3);//时间
            viewHolder.fakuan = (TextView) convertView.findViewById(R.id.tv2_5);//罚款金额
            viewHolder.fuwufei = (TextView) convertView.findViewById(R.id.tv2_6);//服务费
            viewHolder.zhinan  = (TextView) convertView.findViewById(R.id.tv2_7);//滞纳金
            viewHolder.zhinajin  = (LinearLayout) convertView.findViewById(R.id.zhinanjin);//滞纳金
            viewHolder.count_tv= (TextView) convertView.findViewById(R.id.count_tv);//第几条违章
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (bean != null) {
            viewHolder.carnum_tv.setText(bean.getProprefix()+bean.getCarnumber());
            viewHolder.neiron.setText(bean.getDetails().get(i).getReason());
            viewHolder.didian.setText(bean.getDetails().get(i).getLocation());
            viewHolder.shijian.setText(bean.getDetails().get(i).getTime());
            NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);//数字金币格式化
            viewHolder.fakuan.setText(usFormat.format(Integer.parseInt(bean.getDetails().get(i).getCount())));
            viewHolder.fuwufei.setText(usFormat.format(Integer.parseInt(bean.getDetails().get(i).getPrice())));
            viewHolder.koufen.setText(bean.getDetails().get(i).getDegree());
            viewHolder.count_tv.setText((i+1)+"/"+bean.getDetails().size());


            if (bean.getDetails().get(i).getLatefee().equals("0")||bean.getDetails().get(i).getLatefee().equals("")||bean.getDetails().get(i).getLatefee()==null){

                Log.i("WU","滞纳金"+""+i+"滞纳金"+bean.getDetails().get(i).getLatefee());
                viewHolder.zhinan.setText(bean.getDetails().get(i).getLatefee());//滞纳金
                viewHolder.zhinajin.setVisibility(View.GONE);
            }else {
                viewHolder.zhinajin.setVisibility(View.VISIBLE);
                viewHolder.zhinan.setText(bean.getDetails().get(i).getLatefee());//滞纳金
            }

        }


        return convertView;
    }

    class ViewHolder {

        private TextView carnum_tv,neiron, didian, shijian, fakuan, fuwufei, koufen,zhinan,count_tv;
        private LinearLayout zhinajin;

    }


    /**
     * 删除哪一项
     *
     * @param pos
     */
    public void deleteDate(int pos) {
        bean.getDetails().remove(pos);
        notifyDataSetChanged();

    }

    /**
     * 清楚除全部数据
     */
    public void clearDate() {
        bean.getDetails().clear();
        notifyDataSetChanged();

    }

}
