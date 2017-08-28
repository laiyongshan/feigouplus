package com.example.youhe.youhecheguanjiaplus.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.dialog.ProvinceDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.manager.City_ProvinceManager;
import com.example.youhe.youhecheguanjiaplus.ui.base.CarTypeActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.ListActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.RegisterAccountActivit;
import com.example.youhe.youhecheguanjiaplus.ui.base.RegisterCityListActivity;
import com.example.youhe.youhecheguanjiaplus.utils.AllCapTransformationMethod;
import com.example.youhe.youhecheguanjiaplus.utils.FileUtils;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2 0002.
 * 车主用户
 */

public class CarOwnnerUserFragment extends YeoheFragment implements View.OnClickListener{
    private View view;
    private TextView carownner_province_tips_tv,carownner_province_tv,carownner_city_tv,carownner_car_type_tv;
    private EditText obligate_phone_et,carownner_car_number_et;

    private Button carownner_user_next_btn;

    private String plateType,plateTypename;
    private String userType,provinceName;
    private String plateCity,city_name,smsnotice;

    AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod();//字母大写

    private TextView prefix_tv;
    public ProvinceDialog dialog;//省份简称弹出框
    private ProvinceBrocast provinceBrocast;//接收选择的身份简称
    public String prefix="";//省份简称
    private List<Province> provinces=new ArrayList<Province>();

    private final static int CHOOSE_PROVINCE_REQUESTCODE=12200;
    private final static int CHOOSE_CITY_REQUESTCODE=12201;
    private final static int CHOOSE_CAR_TYPE_REQQUESTCODE=12202;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_carownner_user,null);

        initView();
        initPrefixdata();

        IntentFilter filter=new IntentFilter(ProvinceDialog.ACTION_NAME);
        provinceBrocast=new ProvinceBrocast();
        getActivity().registerReceiver(provinceBrocast,filter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(provinceBrocast);
    }

    //初始化控件
    private void  initView(){
        carownner_province_tips_tv= (TextView) view.findViewById(R.id.carownner_province_tips_tv);
        carownner_province_tv= (TextView) view.findViewById(R.id.carownner_province_tv);
        carownner_province_tv.setOnClickListener(this);
        carownner_city_tv= (TextView) view.findViewById(R.id.carownner_city_tv);
        carownner_city_tv.setOnClickListener(this);
        carownner_car_type_tv= (TextView) view.findViewById(R.id.carownner_car_type_tv);
        carownner_car_type_tv.setOnClickListener(this);

        prefix_tv= (TextView) view.findViewById(R.id.prefix_tv);
        prefix_tv.setOnClickListener(this);

        obligate_phone_et= (EditText) view.findViewById(R.id.obligate_phone_et);
        carownner_car_number_et= (EditText) view.findViewById(R.id.carownner_car_number_et);
        carownner_car_number_et.setTransformationMethod(allCapTransformationMethod);//字母大写

        carownner_user_next_btn= (Button) view.findViewById(R.id.carownner_user_next_btn);
        carownner_user_next_btn.setOnClickListener(this);

    }

    /**
     * 初始化全部城市的查询并下单的最低条件
     * */
    private void initPrefixdata(){
        try {
            provinces.clear();
            String json= FileUtils.readFromRaw(getActivity());
            provinces= City_ProvinceManager.jsonToProvinceList(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    * 车牌前缀的广播接收者
    * */
    class ProvinceBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            prefix = intent.getStringExtra("province");
            prefix_tv.setText(prefix);
            dialog.dismiss();
        }
    }

    Intent intent;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.carownner_user_next_btn://下一步
                if(userType==null||plateCity==null||plateType==null||plateCity.equals("")){
                    Toast.makeText(getActivity(),"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if(obligate_phone_et.getText().toString().trim().length()<11){
                    Toast.makeText(getActivity(),"请输入完整手机号码",Toast.LENGTH_SHORT).show();
                }else if(carownner_car_number_et.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(),"请输入完整车牌号码",Toast.LENGTH_SHORT).show();
                }else {
                    intent = new Intent(getActivity(), RegisterAccountActivit.class);

                    intent.putExtra("userType",userType);
                    intent.putExtra("plateCity",plateCity);
                    intent.putExtra("mobile",obligate_phone_et.getText().toString().trim());
                    intent.putExtra("plateType",plateType);//车辆类型
                    intent.putExtra("plateNumber",prefix_tv.getText()+carownner_car_number_et.getText().toString().trim().toUpperCase());

                    startActivity(intent);
                    getActivity().finish();
                }
                break;

            case R.id.carownner_province_tv://省份选择
                intent=new Intent(getActivity(), ListActivity.class);
                intent.putExtra("title","选择省份");
                startActivityForResult(intent,CHOOSE_PROVINCE_REQUESTCODE);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.carownner_city_tv://城市选择
                if(userType!=null) {
                    intent = new Intent(getActivity(), RegisterCityListActivity.class);
                    intent.putExtra("userType", userType);
                    startActivityForResult(intent, CHOOSE_CITY_REQUESTCODE);
                    getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                }else{
                    Toast.makeText(getActivity(), "请先选择机动车登记省份！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.carownner_car_type_tv://车型选择
                intent=new Intent(getActivity(), CarTypeActivity.class);
                startActivityForResult(intent,CHOOSE_CAR_TYPE_REQQUESTCODE);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;

            case R.id.prefix_tv:
                dialog = new ProvinceDialog(getActivity(),R.style.Dialog,provinces,1);
                dialog.showDialog();
                dialog.setCancelable(true);
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
                        carownner_province_tv.setText(provinceName);
                        carownner_province_tv.setTextColor(Color.BLACK);

                        city_name="";
                        plateCity="";
                        carownner_city_tv.setText(city_name);
                    }
                }
                break;

            case CHOOSE_CITY_REQUESTCODE:
                if(data!=null){
                    plateCity=data.getStringExtra("proprefix");
                    city_name=data.getStringExtra("cityname");
                    smsnotice=data.getStringExtra("smsnotice");
                    carownner_province_tips_tv.setText(StringUtils.subSpanStr(smsnotice));
//                    carownner_province_tips_tv.setText(smsnotice+"");
                    if(city_name!=null) {
                        carownner_city_tv.setText(city_name);
                        carownner_city_tv.setTextColor(Color.BLACK);
                    }
                }
                break;

            case CHOOSE_CAR_TYPE_REQQUESTCODE:
                if(data!=null){
                    plateTypename=data.getStringExtra("cartype");
                    plateType=data.getStringExtra("typecode");
                    if(plateTypename!=null) {
                        carownner_car_type_tv.setText(plateTypename);
                        carownner_car_type_tv.setTextColor(Color.BLACK);
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
