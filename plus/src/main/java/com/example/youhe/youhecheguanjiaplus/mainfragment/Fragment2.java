package com.example.youhe.youhecheguanjiaplus.mainfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.MyOrderPagerAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq1;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq2;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq3;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq4;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq5;
import com.example.youhe.youhecheguanjiaplus.widget.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/8/23 0023.
 * 第二个主界面
 */
public class Fragment2 extends Fragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private ArrayList<Fragment> fragments;
    private Toolbar deleteToolbar;
//    private ImageButton imageButton;
    ViewPager viewPager;
    loginReciver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View frview = inflater.inflate(R.layout.activity_my_order, container, false);//导航栏加载第二个fragmen页面
        in(frview);

        receiver=new loginReciver();//广播接受者实例
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("REFLUSH_ORDER_LIST");
        getActivity().registerReceiver(receiver,intentFilter);

        return frview;
    }

    private void in(View frview) {
        EventBus.getDefault().register(this);
        fragments = new ArrayList<Fragment>();//打fragment初始化并放到List里面
        fragments.add(new MyOrderFragmentq3());//添加未支付fragment
        fragments.add(new MyOrderFragmentq1());//添加处理中fragment
        fragments.add(new MyOrderFragmentq2());//添加已成功fragment
        fragments.add(new MyOrderFragmentq4());//添加撤销fragment
        fragments.add(new MyOrderFragmentq5());//添加全部fragment

//        deleteToolbar = (Toolbar) frview.findViewById(R.id.fragment_toolbar);
//        deleteToolbar.inflateMenu(R.menu.fragment2_toolbar_menu);//设置右上角的填充菜单
//        deleteToolbar.setOnMenuItemClickListener(this);
//        imageButton = (ImageButton) frview.findViewById(R.id.sss);
//        imageButton.setOnClickListener(this);
        viewPager= (ViewPager) frview.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyOrderPagerAdapter(getActivity().getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(4);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) frview.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true); //是否填充满屏幕的宽度
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setCustomTabColorizer(
                new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return 0xFFFFFFFF;//设置导航栏下划线颜色
                    }
                });
    }

    private boolean onMen=true;//用来判断全选按钮是否被点击过
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId){
            case R.id.quanxuan:
                if (onMen){
                    EventBus.getDefault().post(new FirstEvent("quanxuan"));
                    onMen =false;
                }

                break;

        }
        return false;
    }

//    /**
//     * 点击头像弹出菜单
//     */
//    private DeletectPopupuWindow menuWindow;//自定义的弹出框类
//    private void inSelectMeu() {//初始化弹出菜单
//
//        //实例化SelectPicPopupWindow
//        menuWindow = new DeletectPopupuWindow(getActivity(), itemsOnClick);
//        menuWindow.setAnimationStyle(R.style.popwin_anim_style);
//        //设置layout在PopupWindow中显示的位置
//        menuWindow.showAtLocation(getActivity().findViewById(R.id.main),
//                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//
//    }

    /**
     *  点击头像弹出窗口实现监听类
     */
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
//            menuWindow.dismiss();
//            onMen = true;
            switch (v.getId()) {

                case R.id.btn_photo://删除被选中的

                    EventBus.getDefault().post(new FirstEvent("delete"));
//                    menuWindow.dismiss();
                    break;
                case R.id.btn_cancel://取消
                    EventBus.getDefault().post(new FirstEvent("hie"));
                    break;
            }
        }
    };



    /**
     * 订单查询主页面删除事件
     * @param event
     */
    @Subscribe
    public void onEvent(FirstEvent event) {

    }


    /**
     * 通知整体刷新
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }


    /**
     * 整体刷新
     */
    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new FirstEvent("ok"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    class loginReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            viewPager.setCurrentItem(0);
        }
    }
}
