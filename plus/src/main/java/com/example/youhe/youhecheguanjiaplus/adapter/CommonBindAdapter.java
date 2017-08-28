package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * dataBinding 通用适配器
 *
 * Created by Administrator on 2017/2/21 0021.
 */

public abstract class CommonBindAdapter<E> extends BaseAdapter {

    protected Context mContext;
    protected List<? super E> mData;
    protected int mPosition;
    protected View mConVertView;
    private int mLayoutId;
    private ViewDataBinding binding;

    public CommonBindAdapter(Context context, List<? super E> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;

    }

    public void setData(List<? super E> data) {
        if (data != null)
            this.mData = data;
    }

    public List<? super E> getData() {
        return mData;
    }

    public void clear() {
        this.mData.clear();
    }

    public void addAllData(List<? extends E> data) {
        this.mData.addAll(data);
    }

    public void addData(E t) {
        this.mData.add(t);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public E getItem(int position) {
        return (E) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), mLayoutId, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ViewDataBinding) convertView.getTag();
        }

        convert(binding, (E) mData.get(position),position);
        return convertView;
    }
    /**
     * @param
     * @param t 数据列表
     * @说明 将数据列表list中的数据放到holder中的控件中。
     */
    public abstract void convert(ViewDataBinding bind, E t,int position);

}
