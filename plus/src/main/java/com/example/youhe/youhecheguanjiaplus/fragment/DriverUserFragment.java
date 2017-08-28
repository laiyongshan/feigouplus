package com.example.youhe.youhecheguanjiaplus.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.ListActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.RegisterAccountActivit;
import com.example.youhe.youhecheguanjiaplus.ui.base.RegisterCityListActivity;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class DriverUserFragment extends YeoheFragment implements View.OnClickListener{

    private View view;

    private TextView driver_province_tv,driver_city_tv,driver_province_tips_tv;
    private EditText obligate_phone_et;
    private Button driver_user_next_btn;

    private String userType,provinceName;
    private String plateCity,city_name,smsnotice;

    private final static int CHOOSE_PROVINCE_REQUESTCODE=12200;
    private final static int CHOOSE_CITY_REQUESTCODE=12201;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_driver_user,null);

        initView();//初始化控件

        return view;
    }


    private void initView(){
        driver_province_tv= (TextView) view.findViewById(R.id.driver_province_tv);
        driver_province_tv.setOnClickListener(this);
        driver_city_tv= (TextView) view.findViewById(R.id.driver_city_tv);
        driver_city_tv.setOnClickListener(this);

        driver_province_tips_tv= (TextView) view.findViewById(R.id.driver_province_tips_tv);

        obligate_phone_et= (EditText) view.findViewById(R.id.obligate_phone_et);

        driver_user_next_btn= (Button) view.findViewById(R.id.driver_user_next_btn);
        driver_user_next_btn.setOnClickListener(this);
    }

    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_province_tv:
                intent=new Intent(getActivity(), ListActivity.class);
                intent.putExtra("title","选择省份");
                startActivityForResult(intent,CHOOSE_PROVINCE_REQUESTCODE);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.driver_city_tv:
                intent=new Intent(getActivity(), RegisterCityListActivity.class);
                intent.putExtra("userType",userType);
                startActivityForResult(intent,CHOOSE_CITY_REQUESTCODE);
                break;

            case R.id.driver_user_next_btn:
                if(userType==null||plateCity==null||plateCity.equals("")){
                    Toast.makeText(getActivity(),"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if(obligate_phone_et.getText().toString().trim().length()<11){
                    Toast.makeText(getActivity(),"请输入完整手机号码",Toast.LENGTH_SHORT).show();
                }else {
                    intent = new Intent(getActivity(), RegisterAccountActivit.class);
                    intent.putExtra("userType",userType);
                    intent.putExtra("plateCity",plateCity);
                    intent.putExtra("mobile",obligate_phone_et.getText().toString().trim());
                    startActivity(intent);
                    getActivity().finish();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case CHOOSE_PROVINCE_REQUESTCODE:
                if(data!=null){
                    userType=data.getStringExtra("code");
                    provinceName=data.getStringExtra("name");
                    if(provinceName!=null) {
                        driver_province_tv.setText(provinceName);
                        driver_province_tv.setTextColor(Color.BLACK);

                        city_name="";
                        plateCity="";
                        driver_city_tv.setText(city_name);
                    }
                }
                break;

            case CHOOSE_CITY_REQUESTCODE:
                if(data!=null){
                    plateCity=data.getStringExtra("proprefix");
                    city_name=data.getStringExtra("cityname");
                    smsnotice=data.getStringExtra("smsnotice");
                    Log.i("TAG","省份提示信息："+smsnotice);
                    driver_province_tips_tv.setText(StringUtils.subSpanStr(smsnotice));
//                    driver_province_tips_tv.setText(smsnotice+"");
                    if(provinceName!=null) {
                        driver_city_tv.setText(city_name);
                        driver_city_tv.setTextColor(Color.BLACK);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {

    }


}
