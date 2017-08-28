package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class Province_GV_Adapter extends BaseAdapter {
    private ViewHolder holder;
    private LayoutInflater mInflater;
    public static List<Province> provinceList=new ArrayList<Province>();
    private int type;

    //    public static  String[] provinces={"京","粤","沪","湘","鄂","豫","冀","苏","浙","川","贵","皖","鲁","辽","吉","闽","渝","宁","青","云","琼","陕","黑","新","桂","甘","藏"};
    public Province_GV_Adapter(Context context, List<Province> provinceList,int type) {
        mInflater = LayoutInflater.from(context);
        this.provinceList = provinceList;
        this.type=type;
    }

    @Override
    public int getCount() {
        return provinceList.size();
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.province_gv_item, null);
            holder = new ViewHolder();
            holder.province_tv = (TextView) convertView.findViewById(R.id.province_tv);
            if(type==1) {
                holder.province_tv.setText(provinceList.get(i).getProvincePrefix().toString());
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder {
        TextView province_tv;
    }
}
