package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.IllegalFragmentPagerAdapter;
import com.example.youhe.youhecheguanjiaplus.fragment.CarOwnnerUserFragment;
import com.example.youhe.youhecheguanjiaplus.fragment.DriverUserFragment;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class CarOwnerTypeActivity extends YeoheActivity implements View.OnClickListener{

    private ImageView carownner_type_back_img;

    private TextView carownner_user_tv,driver_user_tv;
    private ViewPager carownner_type_viewpager;
    private List<Fragment> fragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carowner_type);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CarOwnerTypeActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CarOwnerTypeActivity.this);

        initView();//初始化控件
        initViewPager();//初始化ViewPager
    }

    //初始化控件
    private void initView(){
        carownner_user_tv= (TextView) findViewById(R.id.carownner_user_tv);
        carownner_user_tv.setOnClickListener(this);
        tvList.add(carownner_user_tv);
        driver_user_tv= (TextView) findViewById(R.id.driver_user_tv);
        driver_user_tv.setOnClickListener(this);
        tvList.add(driver_user_tv);

        carownner_type_back_img= (ImageView)findViewById(R.id.carownner_type_back_img);
        carownner_type_back_img.setOnClickListener(this);
    }



    private void initViewPager(){
        carownner_type_viewpager= (ViewPager) findViewById(R.id.carownner_type_viewpager);
        carownner_type_viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        fragmentList=new ArrayList<Fragment>();
        Fragment carOwnnerUserFragment=new CarOwnnerUserFragment();
        Fragment driverFragment=new DriverUserFragment();
        fragmentList.add(carOwnnerUserFragment);
        fragmentList.add(driverFragment);
        FragmentManager fm=getSupportFragmentManager();
        fragmentPagerAdapter=new IllegalFragmentPagerAdapter(fm,fragmentList);

        carownner_type_viewpager.setAdapter(fragmentPagerAdapter);

        carownner_type_viewpager.setOffscreenPageLimit(2);

    }

    List<TextView> tvList=new ArrayList<TextView>();
    private void tvChooose(TextView tv){
//        tv.setTextColor(Color.rgb(255,138,0));
        tv.setBackgroundResource(R.drawable.illegal_item_bg);
        tv.setTextColor(Color.WHITE);
        for(int i=0;i<tvList.size();i++){
            if((tv.getId())!=(tvList.get(i).getId())){
//                tvList.get(i).setTextColor(Color.WHITE);
                tvList.get(i).setBackgroundColor(Color.argb(0,255,255,255));
                tvList.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.carownner_user_tv://车主用户
                carownner_type_viewpager.setCurrentItem(0);
                tvChooose(carownner_user_tv);
                break;

            case R.id.driver_user_tv://驾驶人用户
                carownner_type_viewpager.setCurrentItem(1);
                tvChooose(driver_user_tv);
                break;

            case R.id.carownner_type_back_img:
                finish();
                break;
        }
    }




    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {

    }

}
