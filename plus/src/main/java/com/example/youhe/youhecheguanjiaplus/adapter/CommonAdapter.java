package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * 通用适配器
 *
 * Created by Administrator on 2017/2/21 0021.
 */

public abstract class CommonAdapter<E> extends BaseAdapter{
    protected Context mContext;
    protected List<? super E> mData;
    protected int mPosition;
    protected View mConVertView;
    private int mLayoutId;
    private SparseArray<ViewHolder> viewHolders;

    public CommonAdapter(Context context, List<? super E> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
        viewHolders = new SparseArray<ViewHolder>();
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

    public View getSubView(int position, int id) {
        ViewHolder holder = viewHolders.get(position);
        if (holder == null)
            return null;
        return holder.getView(id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, mLayoutId, position, viewHolders);
        mPosition = position;
        mConVertView = holder.getConVertView();
        convert(holder, (E) mData.get(position));
        return holder.getConVertView();
    }

    /**
     * @param holder
     * @param t      数据列表
     * @说明 将数据列表list中的数据放到holder中的控件中。
     */
    public abstract void convert(ViewHolder holder, E t);

}
