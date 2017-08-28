package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * Created by Administrator on 2016/9/1 0001.
 * 提交订单详情
 */
public class CommitDetailActivity extends Activity implements OnGetGeoCoderResultListener,View.OnClickListener{
    private MapView mMapView;//百度地图控件
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    BaiduMap mBaiduMap = null;

//    private Button back_btn;
    private TextView commitdetail_back_img;

    private TextView carnumber_tv, illegalAdd_tv,illegalContent_tv,illegalTime_tv,nowState_tv,illegalmoney_tv,canprocess_tv,
            koufen_tv,poundage_tv,handle_remark_tv,latefee_tv;

    private LinearLayout latefee_layout;//滞纳金

    //车辆id,车牌号码,地点，内容，罚款，时间，状态，扣分，手续费,价格,是否为现场单,是否待报价，滞纳金,备注
    private String carid, carnumber, location, reason , time, orderstatus,locationname,category,remark;
    private int count,degree,price,quotedprice,latefee,iscommit;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitdetail);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CommitDetailActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CommitDetailActivity.this);

        appContext= (AppContext) getApplicationContext();
        if(!appContext.isNetworkConnected()){
            Toast.makeText(CommitDetailActivity.this,"网络连接失效，请检查网络设置",Toast.LENGTH_LONG).show();
        }

        Intent extraIntent=getIntent();
        Violation violation= (Violation) extraIntent.getSerializableExtra("violation");

        carid=violation.getCarId();
        carnumber=IllegalQueryActivty.carnumber;
        location=violation.getLocation();
        reason=violation.getReason();
        count=violation.getCount();
        time=violation.getTime();
        degree=violation.getDegree();
        locationname=violation.getLocationname();
        price=violation.getPrice();
        category=violation.getCategory();
        orderstatus=violation.getOrderstatus();
        quotedprice=violation.getQuotedprice();//1 实时报价   2 待报价
        latefee=violation.getLatefee();//滞纳金
        remark=violation.getRemark()+"";//备注信息
        iscommit=violation.getIscommit();//是否可提交


        initViews();//初始化控件
    }

    private void  initViews(){
//        back_btn= (Button) findViewById(R.id.back_btn);
//        back_btn.setOnClickListener(this);

        commitdetail_back_img= (TextView) findViewById(R.id.commitdetail_back_img);
        commitdetail_back_img.setOnClickListener(this);

        latefee_layout= (LinearLayout) findViewById(R.id.latefee_layout);
        if((latefee!=0)&&category.equals("现场单")){
            latefee_layout.setVisibility(View.VISIBLE);
            latefee_tv= (TextView) findViewById(R.id.latefee_tv);
            latefee_tv.setText("￥"+latefee+"");
        }

        carnumber_tv= (TextView) findViewById(R.id.carnumber_tv);
        carnumber_tv.setText(carnumber);

        canprocess_tv= (TextView) findViewById(R.id.canprocess_tv);
        if(quotedprice==1||quotedprice==2){
            canprocess_tv.setText("可代办");
        }else if(quotedprice==-1){
            canprocess_tv.setText("不可代办");
        }

        illegalAdd_tv= (TextView) findViewById(R.id.illegalAdd_tv);//违章地点
        illegalAdd_tv.setText(location);

        illegalContent_tv= (TextView) findViewById(R.id.illegalContent_tv);//违章内容
        illegalContent_tv.setText(reason);

        illegalTime_tv= (TextView) findViewById(R.id.illegalTime_tv);//违章时间
        illegalTime_tv.setText(time);

        nowState_tv= (TextView) findViewById(R.id.nowState_tv);//处理状态
        nowState_tv.setText(orderstatus+"");

        illegalmoney_tv= (TextView) findViewById(R.id.illegalmoney_tv);//罚款金额
        illegalmoney_tv.setText("￥"+count);
//        illegalmoney_tv.setTextColor(Color.RED);

        koufen_tv= (TextView) findViewById(R.id.koufen_tv);//扣分情况
        koufen_tv.setText(degree+"分");

        poundage_tv= (TextView) findViewById(R.id.degreepoundage_tv);//手续费
        if(price==0){
            poundage_tv.setText("待报价");
        }else{
            poundage_tv.setText("￥"+price);
        }

        handle_remark_tv= (TextView) findViewById(R.id.handle_remark_tv);
//


        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        //地图上比例尺
        mMapView.showScaleControl(true);
        // 隐藏缩放控件
        mMapView.showZoomControls(true);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // Geo搜索
//        mSearch.geocode(new GeoCodeOption().city(
//                editCity.getText().toString()).address(editGeoCodeKey.getText().toString()));
        mSearch.geocode(new GeoCodeOption().city(locationname.toString().trim()).address((locationname+location).toString().trim()));
    }

    @Override
    protected void onDestroy() {
//        mMapView.onDestroy();
        mBaiduMap = null;
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(CommitDetailActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
//                    .show();
            mSearch.geocode(new GeoCodeOption().city(locationname.toString().trim()).address((locationname+location).toString().trim()));
            return;
        }
        if(mBaiduMap!=null) {
            mBaiduMap.clear();
            mBaiduMap.addOverlay(new MarkerOptions().position(geoCodeResult.getLocation())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marka)));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(geoCodeResult
                    .getLocation()));
//        String strInfo = String.format("纬度：%f 经度：%f",
//                geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);
//        Toast.makeText(CommitDetailActivity.this, strInfo, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
//            case R.id.back_btn:
//                finish();
//                overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
//                break;
            case R.id.commitdetail_back_img:
                finish();
                overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
                break;
        }
    }
}
