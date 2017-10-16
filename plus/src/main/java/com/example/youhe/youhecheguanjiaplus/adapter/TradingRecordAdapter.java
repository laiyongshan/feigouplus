package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.TradingModel;
import com.example.youhe.youhecheguanjiaplus.databinding.ItemRecordChildBinding;
import com.example.youhe.youhecheguanjiaplus.databinding.ItemRecordGroupBinding;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 交易记录
 * Created by Administrator on 2017/7/26 0026.
 */

public class TradingRecordAdapter extends BaseExpandableListAdapter {

    private List<TradingModel> datas;
    private Context mContext;

    public TradingRecordAdapter(Context context, List<TradingModel> datas) {
        this.datas = datas;
        this.mContext = context;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {

        ItemRecordGroupBinding m;
        if (convertView == null) {
            m = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_record_group, null, false);
            convertView = m.getRoot();
            convertView.setTag(m);
        } else {
            m = (ItemRecordGroupBinding) convertView.getTag();
        }

        try {
            String dateStr = datas.get(groupPosition).getYearMonth();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
            long lt = new Long(dateStr);
            Date date = new Date(lt * 1000);
            String res = simpleDateFormat.format(date);
            m.tvDate.setText(res);
        } catch (Exception e) {
            m.tvDate.setText("");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        final ItemRecordChildBinding m;
        if (convertView == null) {
            m = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_record_child, null, false);
            convertView = m.getRoot();
            convertView.setTag(m);
        } else {
            m = (ItemRecordChildBinding) convertView.getTag();
        }

        if (childPosition == (datas.get(groupPosition).getDetailList().size() - 1)) {
            m.line.setVisibility(View.GONE);
        } else
            m.line.setVisibility(View.VISIBLE);
        TradingModel.TradingSubModel model = datas.get(groupPosition).getDetailList().get(childPosition);
        if (model != null) {
            m.title.setText(StringUtils.isEmpty(model.getTitle()) ? "" : model.getTitle());

            m.source.setText(StringUtils.isEmpty(model.getPay_flowing()) ? "" : ("" + model.getPay_flowing()));

            m.time.setText(StringUtils.isEmpty(model.getCreatetimestr()) ? "" : model.getCreatetimestr());

            m.money.setText(StringUtils.isEmpty(model.getMoney()) ? "" :
                    ((model.getPlus_minus().equals("1") ? "+" : "-") + model.getMoney()));
        }

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return datas.get(i).getDetailList().size();
    }

    @Override
    public TradingModel getGroup(int i) {
        return datas.get(i);
    }

    @Override
    public TradingModel.TradingSubModel getChild(int i, int i1) {
        return datas.get(i).getDetailList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
