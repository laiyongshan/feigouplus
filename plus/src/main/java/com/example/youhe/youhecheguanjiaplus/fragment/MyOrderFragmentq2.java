package com.example.youhe.youhecheguanjiaplus.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.biz.OrMyListViewDao;
import com.example.youhe.youhecheguanjiaplus.https.URLs;


/**
 * Created by Administrator on 2016/9/3 0003.
 * 我的订单已成功类型
 */
@SuppressLint("ValidFragment")
public class MyOrderFragmentq2 extends Fragment {

    private OrMyListViewDao dao;//逻辑类
    private String type = "finish";//请求页面是 处理成功 类型;
    private int orderListType;//订单列表类型

    public MyOrderFragmentq2(){

    }

    public MyOrderFragmentq2(int orderListType){
        this.orderListType=orderListType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myorderfragmentlay, container, false);

        if(orderListType==1) {
            dao = new OrMyListViewDao(getActivity(), view, URLs.ORDER_QUERY, type,orderListType);
        }else if(orderListType==2){//年检订单
            dao = new OrMyListViewDao(getActivity(), view, URLs.ANNUAL_ORDER_LIST, type,orderListType);
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(dao!=null) {
            dao.deletecData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
