package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.entity.base.CarOpenCity;
import com.example.youhe.youhecheguanjiaplus.manager.CarOpenCitysMenager;
import com.example.youhe.youhecheguanjiaplus.ui.base.EditCarActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalQueryActivty;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21 0021.
 * 违章处理界面中车辆列表的适配器
 */
public class IllegalHandleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Car> carList;
    private ViewHolder holder;
    private List<CarOpenCity> carOpenCities;
    private Context mContext;

    String carid = "";//车辆id
    String carnumber = "";//车牌号码
    String carcode = "";//车身架号
    String cardrivenumber = "";//发动机号
    String remark = "";//备注
    String ischeck="";//车辆是否认证
    String proprefix="";//省份前缀

    public IllegalHandleAdapter(Context mContext, List<Car> carList) {
        this.mContext=mContext;
        inflater = LayoutInflater.from(mContext);
        this.carList = carList;
        this.carOpenCities= CarOpenCitysMenager.carOpenCityList;
    }

    @Override
    public int getCount() {
        return carList.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
//        if (view == null) {
            view = inflater.inflate(R.layout.illegal_car_list_item, null);
            holder = new ViewHolder();

            holder.carnumber_tv = (TextView) view.findViewById(R.id.carnumber_tv);
            holder.car_remark_tv= (TextView) view.findViewById(R.id.car_remark_tv);
            holder.mession_icon= (ImageView) view.findViewById(R.id.mession_icon);
            holder.owner_car_query_tv= (TextView) view.findViewById(R.id.owner_car_query_tv);
            holder.no_owner_car_query_tv= (TextView) view.findViewById(R.id.no_owner_car_query_tv);

            holder.owner_car_query_tv.setTag(i);
            holder.no_owner_car_query_tv.setTag(i);
            holder.mession_icon.setTag(i);

//            view.setTag(holder);
//        } else {
//            holder = (ViewHolder) view.getTag();
//            holder.owner_car_query_tv.setTag(i);
//            holder.no_owner_car_query_tv.setTag(i);
//            holder.mession_icon.setTag(i);
//        }

        holder.carnumber_tv.setText(carList.get(i).getCarnumber());
        holder.car_remark_tv.setText(carList.get(i).getRemark()+"");
        if(carList.get(i).getIsCarCorrect().equals("-1")){
            holder.mession_icon.setVisibility(View.VISIBLE);
            holder.owner_car_query_tv.setBackgroundResource(R.drawable.gray);
        }

        holder.owner_car_query_tv.setEnabled(false);
        if (!carList.get(i).getIsCarCorrect().equals("-1")) {
            for(int j=0;j<carOpenCities.size();j++){
                String carnum=carList.get(i).getCarnumber();
                String proprefix=carList.get(i).getProprefix();
                if(carnum.substring(0,2).equals(carOpenCities.get(j).getPrefix())){
                    holder.owner_car_query_tv.setEnabled(true);
                    holder.owner_car_query_tv.setBackgroundResource(R.drawable.orige);
                    break;
                }
            }
        } else {
            holder.owner_car_query_tv.setEnabled(false);
            holder.owner_car_query_tv.setBackgroundResource(R.drawable.gray);
        }

        carid = carList.get(i).getCarId();
        carnumber = carList.get(i).getCarnumber();
        carcode = carList.get(i).getCarcode();
        cardrivenumber = carList.get(i).getEnginenumber();
        remark = carList.get(i).getRemark();
        ischeck=carList.get(i).getIsCarCorrect();
        proprefix=carList.get(i).getProprefix();
        final Intent intent=new Intent();
        intent.putExtra("carid", carid);
        intent.putExtra("carnumber", carnumber);
        intent.putExtra("carcode", carcode);
        intent.putExtra("cardrivenumber", cardrivenumber);
        intent.putExtra("remark", remark);
        intent.putExtra("ischeck",ischeck);
        intent.putExtra("proprefix",proprefix);


        holder.owner_car_query_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("searchtype",2);
                intent.setClass(mContext, IllegalQueryActivty.class);
                mContext.startActivity(intent);
            }
        });

        holder.no_owner_car_query_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!carList.get(i).getIsCarCorrect().equals("-1")) {
                    intent.putExtra("searchtype", 1);
                    intent.setClass(mContext, IllegalQueryActivty.class);
                    mContext.startActivity(intent);
                }else{
                    intent.setClass(mContext,EditCarActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });

        return view;
    }


    public class ViewHolder {
        TextView carnumber_tv;//车牌号码
        TextView car_remark_tv;//车辆备注信息
        ImageView mession_icon;//车辆信息错误图标
        TextView owner_car_query_tv;//本人本车查询按钮
        TextView no_owner_car_query_tv;//非本人本车查询按钮
    }
}
