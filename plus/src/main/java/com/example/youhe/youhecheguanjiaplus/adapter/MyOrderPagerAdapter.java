package com.example.youhe.youhecheguanjiaplus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class MyOrderPagerAdapter extends FragmentPagerAdapter {
//    private String mTabTitle[] = new String[]{"处理中", "已成功", "未支付", "已撤销", "全部"};
    private String mTabTitle[] = new String[]{"未支付","处理中", "已成功", "已撤销", "全部"};
    private ArrayList<Fragment> fragments;

    public MyOrderPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;

    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle[position];
    }
}
