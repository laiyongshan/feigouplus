package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 将ListView的Adapter里的方法进行封装，减少代码量
 * Created by Administrator on 2017/2/21 0021.
 */

public class ViewHolder  {

    private SparseArray<View> mViews;
    private View mConVertView;

    public ViewHolder(Context context, ViewGroup parent,int layoutId,int position){
        this.mViews=new SparseArray<>();
        mConVertView= LayoutInflater.from(context).inflate(layoutId,parent);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position, SparseArray<ViewHolder> hs) {

        ViewHolder holder = hs.get(position);
        if (holder == null) {
            holder = new ViewHolder(context, parent, layoutId, position);
            hs.put(position, holder);
        }
        return holder;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public View getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConVertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    public View getConVertView() {
        return mConVertView;
    }



}
