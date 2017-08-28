package com.example.youhe.youhecheguanjiaplus.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.DontHandleIllegalAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommitDetailActivity;
import com.example.youhe.youhecheguanjiaplus.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20 0020.
 */
@SuppressLint("ValidFragment")
public class DontHandleFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    private MyListView dont_handle_lv;
    private List<Violation> violationList=new ArrayList<Violation>();
    private LinearLayout no_illegal_layout;

    private DontHandleIllegalAdapter dontHandleIllegalAdapter;

    public DontHandleFragment(){

    }

    public DontHandleFragment(List<Violation> violationList){
        this.violationList=violationList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_dont_handle,null);
        initView();//初始化控件

        return view;
    }


    //初始化控件
    private void initView(){

        dontHandleIllegalAdapter=new DontHandleIllegalAdapter(getActivity(),violationList);
        dont_handle_lv= (MyListView) view.findViewById(R.id.donthandle_illegal_lv);
        dont_handle_lv.setAdapter(dontHandleIllegalAdapter);

        dont_handle_lv.setOnItemClickListener(this);

        no_illegal_layout= (LinearLayout) view.findViewById(R.id.no_illegal_layout);
        if(violationList.isEmpty()){
            no_illegal_layout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent(getActivity(),CommitDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("violation",violationList.get(i));
        intent.putExtras(bundle);
//        intent.putExtra("carid",violationList.get(i).getCarId());
//        intent.putExtra("carnumber",IllegalQueryActivty.carnumber);
//        intent.putExtra("location",violationList.get(i).getLocation());
//        intent.putExtra("reason",violationList.get(i).getReason());
//        intent.putExtra("count",violationList.get(i).getCount());
//        intent.putExtra("time",violationList.get(i).getTime());
//        intent.putExtra("status",violationList.get(i).getStatus());
//        intent.putExtra("degree",violationList.get(i).getDegree());
//        intent.putExtra("poundage",violationList.get(i).getPoundage());
//        intent.putExtra("locationname",violationList.get(i).getLocationname());
//        intent.putExtra("canprocess",violationList.get(i).getCanprocess());
//        intent.putExtra("price",violationList.get(i).getPrice());
//        intent.putExtra("category",violationList.get(i).getCategory());
//        intent.putExtra("orderstatus",violationList.get(i).getOrderstatus());
//        intent.putExtra("quotedprice",violationList.get(i).getQuotedprice());
//        intent.putExtra("latefee",violationList.get(i).getLatefee());
//        intent.putExtra("remark",violationList.get(i).getRemark()+"");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);

    }
}
