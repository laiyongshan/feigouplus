package com.example.youhe.youhecheguanjiaplus.logic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public abstract class YeoheFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG", "MainFragment is onCreate()...");
        MainService.addFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("fragment", "MainFragment is onDestroy()...");
        MainService.removeFragment(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("fragment", "MainFragment is onPause()...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragment", "MainFragment is onResume()...");
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        Log.i("fragment", "MainFragment is onStart()...");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("fragment", "MainFragment is onStop()...");
    }


    public abstract void init();

    public abstract void refresh(Object... param);

}
