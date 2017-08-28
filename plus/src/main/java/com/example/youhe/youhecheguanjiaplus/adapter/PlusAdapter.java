package com.example.youhe.youhecheguanjiaplus.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.example.youhe.youhecheguanjiaplus.bean.PlusBean;

import java.util.List;

/**
 * 车主卡 适配器
 * Created by Administrator on 2017/7/5 0005.
 */

public class PlusAdapter extends PagerAdapter {

    // 界面列表
    private final List<View> views;
    private final Activity activity;
    private List<PlusBean> plusList;

    public PlusAdapter(List<View> views, Activity activity, List<PlusBean> plusList) {
        this.views = views;
        this.activity = activity;
        this.plusList = plusList;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(views.get(position), 0);
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
