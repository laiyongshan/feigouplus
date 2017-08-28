package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.AddMajorDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class MajorViolationActivity extends Activity implements View.OnClickListener{
    private TextView add_major_violation;
    private TextView major_violation_back_img;

    private Button add_major_btn;


    private LinearLayout major_violation_layout,empty_nonadd_major_layout;
    private TextView jiashiren_tv,jiashizhenghao_tv,danganbianhao_tv;//驾驶人姓名，驾驶证号，档案编号
    private String jiashiren,jiashizhenghao,danganbianhao;


    private String carid="";

    private final int RESULT_CODE=1010;

    private Car car;

    NetReceiver mNetReceiver;
    IntentFilter mNetFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_violation);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,MajorViolationActivity.this);
        }
        SystemBarUtil.useSystemBarTint(MajorViolationActivity.this);


        car=new Car();

        carid=getIntent().getStringExtra("carid");

        initView();//初始化控件

        mNetReceiver= new NetReceiver();//网络连接情况广播接收者
        mNetFilter= new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCarMajor(getCarMajorParams());//获取车辆车主的驾驶证信息
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetReceiver);
    }


    private void initView(){
        add_major_violation= (TextView) findViewById(R.id.add_major_violation);
        add_major_violation.setOnClickListener(this);

        major_violation_back_img= (TextView) findViewById(R.id.major_violation_back_img);
        major_violation_back_img.setOnClickListener(this);

        add_major_btn= (Button) findViewById(R.id.add_major_btn);
        add_major_btn.setOnClickListener(this);

        major_violation_layout= (LinearLayout) findViewById(R.id.major_violation_layout);
        major_violation_layout.setOnClickListener(this);

        empty_nonadd_major_layout= (LinearLayout) findViewById(R.id.empty_nonadd_major_layout);

        jiashiren_tv= (TextView) findViewById(R.id.jiashiren_tv);
        jiashizhenghao_tv= (TextView) findViewById(R.id.jiashizhenghao_tv);
        danganbianhao_tv= (TextView) findViewById(R.id.danganbianhao_tv);
    }


    //获取车辆驾照信息接口参数
    HashMap<String, Object> map;
    private HashMap<String,Object> getCarMajorParams(){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("carid",carid);
        return map;
    }

    //获取车辆驾照信息
    private void getCarMajor(HashMap<String,Object> map){
        UIHelper.showPd(MajorViolationActivity.this);
        VolleyUtil.getVolleyUtil(MajorViolationActivity.this).StringRequestPostVolley(URLs.GET_ONE_CAR_INFO, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG",jsonObject.toString());
                car=jsonToCar(EncryptUtil.decryptJson(jsonObject.toString(),MajorViolationActivity.this));
                if(jiashiren!=null&&jiashizhenghao!=null&&danganbianhao!=null&&!jiashiren.equals("")&&!jiashizhenghao.equals("")&&!danganbianhao.equals("")){
                    major_violation_layout.setVisibility(View.VISIBLE);
                    empty_nonadd_major_layout.setVisibility(View.GONE);

                    jiashiren_tv.setText(jiashiren);
                    jiashizhenghao_tv.setText(StringUtils.showNum(jiashizhenghao));
                    danganbianhao_tv.setText(StringUtils.showNum(danganbianhao));
                }else{
                    major_violation_layout.setVisibility(View.GONE);
                    empty_nonadd_major_layout.setVisibility(View.VISIBLE);
                    AddMajorDialog addMajorDialog=new AddMajorDialog(MajorViolationActivity.this,R.style.Dialog,carid);
                    if(!MajorViolationActivity.this.isDestroyed()) {
                        addMajorDialog.show();
                    }
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG",volleyError.toString());
                Toast.makeText(MajorViolationActivity.this,"网络请求错误，请检查网络设置",Toast.LENGTH_SHORT).show();
                UIHelper.dismissPd();
            }
        });

    }


    public Car jsonToCar(String json){

        Car carInfo=new Car();

        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");
            jiashiren=dataObj.optString("carownerlen");
            carInfo.setCarownerlen(jiashiren);
            carInfo.setCarownerphonelen(dataObj.optString("carownerphonelen"));
            carInfo.setFilephonelen(dataObj.optString("filephonelen"));
            carInfo.setCheliangzhengshulen(dataObj.optString("cheliangzhengshulen"));
            jiashizhenghao=dataObj.optString("jashizhenghaolen");
            carInfo.setJashizhenghaoLen(jiashizhenghao);
            danganbianhao=dataObj.optString("danganbianhaolen");
            carInfo.setDanganbianhaolen(danganbianhao);
            carInfo.setTiaoxingmalen(dataObj.optString("tiaoxingmalen"));
            carInfo.setOwnercardlen(dataObj.optString("ownercardlen"));
            carInfo.setXingshizhenghaoLen(dataObj.optString("xingshizhenghaolen"));
            carInfo.setMajorviolation(dataObj.optString("majorviolation"));
            carInfo.setCarcode(dataObj.optString("carcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            UIHelper.dismissPd();
        }
        return carInfo;
    }


    Intent intent=null;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_major_violation://点击编辑
                intent=new Intent(MajorViolationActivity.this,AddMajorviolationActivity.class);
                startActivityForResult(intent,RESULT_CODE);
                break;

            case R.id.add_major_btn://添加驾照
                intent=new Intent(MajorViolationActivity.this,AddMajorviolationActivity.class);
                startActivityForResult(intent,RESULT_CODE);
                break;

            case R.id.major_violation_layout:
                intent=new Intent(MajorViolationActivity.this,AddMajorviolationActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("car",car);
                intent.putExtras(bundle);
                intent.putExtra("carid",carid);
                startActivityForResult(intent,RESULT_CODE);
                break;

            case R.id.major_violation_back_img://返回
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_CODE){

        }

    }

    /**
     * 返回键返回
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            finish();
            return false;
        }
        return false;
    }


    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                if (isConnected) {
                    }
                } else {
                    Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_SHORT).show();
                }
            }
    }

}
