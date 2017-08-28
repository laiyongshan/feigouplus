package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.MyOrderPagerAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq1;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq2;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq3;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq4;
import com.example.youhe.youhecheguanjiaplus.fragment.MyOrderFragmentq5;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.SlidingTabLayout;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class OrderActivity extends FragmentActivity implements View.OnClickListener {

    private TextView orderlist_title_tv;
    private ArrayList<Fragment> fragments;
    ViewPager viewPager;

    private ImageView order_back_img;

    private int orderListType;//1:违章订单   2：年检订单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, OrderActivity.this);
        }
        SystemBarUtil.useSystemBarTint(OrderActivity.this);

        orderListType=getIntent().getIntExtra("orderListType",0);

        in();

    }

    private void in() {

        orderlist_title_tv= (TextView) findViewById(R.id.orderlist_title_tv);
        orderlist_title_tv.setText(getIntent().getStringExtra("title")==null?"我的订单":getIntent().getStringExtra("title"));

        EventBus.getDefault().register(this);
        fragments = new ArrayList<Fragment>();//打fragment初始化并放到List里面
        fragments.add(new MyOrderFragmentq3(orderListType));//添加未支付fragment
        fragments.add(new MyOrderFragmentq1(orderListType));//添加处理中fragment
        fragments.add(new MyOrderFragmentq2(orderListType));//添加已成功fragment
        fragments.add(new MyOrderFragmentq4(orderListType));//添加撤销fragment
        fragments.add(new MyOrderFragmentq5(orderListType));//添加全部fragment

//        deleteToolbar = (Toolbar) frview.findViewById(R.id.fragment_toolbar);
//        deleteToolbar.inflateMenu(R.menu.fragment2_toolbar_menu);//设置右上角的填充菜单
//        deleteToolbar.setOnMenuItemClickListener(this);
//        imageButton = (ImageButton) frview.findViewById(R.id.sss);
//        imageButton.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyOrderPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(4);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)
                findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true); //是否填充满屏幕的宽度
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setCustomTabColorizer(
                new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return 0xFFFFFFFF;//设置导航栏下划线颜色
                    }
                });

        order_back_img= (ImageView) findViewById(R.id.order_back_img);
        order_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
     * 点击头像弹出窗口实现监听类
     */
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
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
    public void onEvent(FirstEvent event){
    }


    /**
     * 通知整体刷新
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event){
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
    }

    public static String CONSTANT_SELECT_FRAGMENT = "select_fragment";

    @Override
    protected void onResume() {
        super.onResume();
//        viewPager.setCurrentItem(2);
        XGPushClickedResult receiver = XGPushManager.onActivityStarted(this);
        if (receiver != null && !StringUtils.isEmpty(receiver.getCustomContent())) {
            try {
                JSONObject object = new JSONObject(receiver.getCustomContent());
                if (object.has(CONSTANT_SELECT_FRAGMENT)) {
                    try {
                        int select_fragment = object.getInt(CONSTANT_SELECT_FRAGMENT);
                        if (select_fragment >= 0 && viewPager.getAdapter().getCount() > 0 && select_fragment < viewPager.getAdapter().getCount()) {
                            viewPager.setCurrentItem(select_fragment);
                        }
//                        else {
//                            viewPager.setCurrentItem(0);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        XGPushManager.onActivityStoped(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }
}
