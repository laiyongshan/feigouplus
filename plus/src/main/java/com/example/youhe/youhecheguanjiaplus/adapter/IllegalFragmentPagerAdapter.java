package com.example.youhe.youhecheguanjiaplus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class IllegalFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList;

    public IllegalFragmentPagerAdapter(FragmentManager fragmentManager,List<Fragment> fragmentList) {
        super(fragmentManager);
        this.fragmentManager=fragmentManager;
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
