package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.DoubtDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.ProvinceDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.City;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.manager.City_ProvinceManager;
import com.example.youhe.youhecheguanjiaplus.manager.ProprefixManager;
import com.example.youhe.youhecheguanjiaplus.utils.AllCapTransformationMethod;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.FileUtils;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31 0031.
 *
 */
public class AddCarActivity extends Activity implements View.OnClickListener{

    private TextView carEngine_tv,carCode_tv,driving_licence_tv;
    private TextView province_code_tv;//省份简称
    private ImageView addcar_back_img;//返回按钮
    private Button save_check_btn;//保存并查询按钮
    private EditText CarNumber_et, CarEngine_et, CarCode_et,add_remark_et;//车牌号码，发动机号，车身架号，备注输入框
    private ImageView carCode_doubt_img,carEngine_doubt_img;

    private RelativeLayout car_type_layout,car_model_layout;
    private TextView car_type_tv;
    private String car_type;
    private String car_code;//车型代码

    public ProvinceDialog dialog;//省份简称弹出框
    private ProvinceBrocast provinceBrocast;//接收选择的身份简称

    private String carnumber, carengine, carcode, phonenum;
    public String province="";//省份简称
    public String carid="";
    private String status="";

    private int carcodeLen=0,carengineLen=0;//输入车身架号、发动机号长度限制
    private LinearLayout carCode_ll,carEngine_ll;
    private List<Province> provinces=new ArrayList<Province>();
    private List<City> cities=new ArrayList<City>();

    AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod();//字母大写

    private ProgressDialog pd;//网络请求是弹出的Loading框

    private AppContext appContext;

    private final static int CAR_MODEL_REQUEST_CODE=10001;
    private final static int CAR_TYPE_REQUEST_CODE=10002;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcar);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AddCarActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddCarActivity.this);


        initCitydata();//初始化全部城市的查询并下单的最低条件

        appContext= (AppContext)AddCarActivity.this.getApplicationContext();

        IntentFilter filter=new IntentFilter(ProvinceDialog.ACTION_NAME);
        provinceBrocast=new ProvinceBrocast();
        registerReceiver(provinceBrocast,filter);

        initViews();//初始化控件
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(provinceBrocast);
    }

    /**
     * 初始化全部城市的查询并下单的最低条件
     */
    String province_key="proprefix_json_";
    public String proprefixJson="";
    private void initCitydata() {
        UIHelper.showPd(AddCarActivity.this);
        VolleyUtil.getVolleyUtil(getApplicationContext()).StringRequestGetVolley(URLs.GET_OPEN_PROVINCE, new VolleyInterface(){
            @Override
            public void ResponseResult(Object jsonObject){
                Log.i("TAG","获取开放的省份："+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),getApplicationContext()));

                    String status=obj.getString("status");
                    if(status.equals("ok")){
                        proprefixJson=obj.toString();
                        HttpUtil.saveJson2FileCache(province_key, EncryptUtil.decryptJson(jsonObject.toString(),getApplicationContext()));//缓存数据
                    } else {
                        proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                    proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                }finally {
                    ProprefixManager.jsonToProprefixList(proprefixJson);
                    provinces.clear();
                    cities.clear();
                    if(ProprefixManager.proprefixList.size()>0) {
                        provinces = ProprefixManager.proprefixList;
                        cities = City_ProvinceManager.cityList;
                    }else{
                        try {
                            String json= FileUtils.readFromRaw(AddCarActivity.this);//使用缓存数据
                            provinces= ProprefixManager.jsonToProprefixList(json);
                            cities= City_ProvinceManager.jsonToCityList(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG",volleyError.toString());
                proprefixJson = HttpUtil.LoadDataFromLocal(province_key) + "";//使用缓存数据
                ProprefixManager.jsonToProprefixList(proprefixJson);
            }
        });
    }

    //初始化控件
    private void initViews(){
        pd = new ProgressDialog(AddCarActivity.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

//        car_model_name_tv= (TextView) findViewById(R.id.car_model_name_tv);
        car_type_tv= (TextView) findViewById(R.id.car_type_tv);

        car_type_layout= (RelativeLayout) findViewById(R.id.car_type_layout);
        car_type_layout.setOnClickListener(this);

        car_model_layout= (RelativeLayout) findViewById(R.id.car_model_layout);
        car_model_layout.setOnClickListener(this);

        car_type_layout= (RelativeLayout) findViewById(R.id.car_type_layout);
        car_type_layout.setOnClickListener(this);

        carEngine_tv= (TextView) findViewById(R.id.carEngine_tv);//发动机号
        carEngine_tv.setOnClickListener(this);
        carCode_tv= (TextView) findViewById(R.id.carCode_tv);//车身架号
        carCode_tv.setOnClickListener(this);
        province_code_tv= (TextView) findViewById(R.id.provincecode_tv);
        if(provinces!=null) {
            province_code_tv.setText("粤");
        }
        province_code_tv.setOnClickListener(this);
        addcar_back_img= (ImageView) findViewById(R.id.addcar_back_img);
        addcar_back_img.setOnClickListener(this);

        carCode_doubt_img= (ImageView) findViewById(R.id.carCode_doubt_img);
        carCode_doubt_img.setOnClickListener(this);
        carEngine_doubt_img= (ImageView) findViewById(R.id.carEngine_doubt_img);
        carEngine_doubt_img.setOnClickListener(this);

        save_check_btn= (Button) findViewById(R.id.save_check_btn);
        save_check_btn.setOnClickListener(this);

        CarEngine_et= (EditText) findViewById(R.id.CarEngine_et);//发动机号输入框
        carengine = CarEngine_et.getText().toString();
        CarEngine_et.setTransformationMethod(allCapTransformationMethod);
        CarCode_et = (EditText) findViewById(R.id.CarCode_et);
        carcode=CarCode_et.getText().toString().trim();
        CarCode_et.setTransformationMethod(allCapTransformationMethod);

        add_remark_et= (EditText) findViewById(R.id.remark_et);

        carCode_ll= (LinearLayout) findViewById(R.id.carCode_ll);
        carEngine_ll= (LinearLayout) findViewById(R.id.carEngine_ll);

        CarNumber_et= (EditText) findViewById(R.id.CarNumber_et);//车牌号码输入框
        carnumber=CarNumber_et.getText().toString().trim();//车牌号
        CarNumber_et.setTransformationMethod(allCapTransformationMethod);
        CarNumber_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s==null||s.toString().trim().equals("")) {
                    CarEngine_et.setText("");
                    CarCode_et.setText("");
                }else {
                    String pfixe = province_code_tv.getText().toString();
                    if (s.length() == 1) {
                        for (City city : cities) {
                            if (city.getCarNumberPrefix().equals(pfixe + s.subSequence(0, 1).toString().toUpperCase())) {
                                String carCodeLen = city.getCarCodeLen();
                                String carEngineLen = city.getCarEngineLen();
                                viewChange(carCodeLen, carEngineLen);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 添加车辆的输入条件的变化
     * */
    private void viewChange(String carCodeLen,String carEngineLen){
        if(carCodeLen.equals("0")){
            carcodeLen=6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        }else if(carCodeLen.equals("99")){
            carcodeLen=17;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入完整车身架号");
        }else if(carCodeLen.equals("6")){
            carcodeLen=6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        }else if(carCodeLen.equals("4")){
            carcodeLen=6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        }else{
            carcodeLen=17;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入完整车身架号");
        }

        if(carEngineLen.equals("0")){
            carengineLen=6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        }else if(carEngineLen.equals("99")){
            carengineLen=6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入完整发动机号");
        }else if(carEngineLen.equals("6")){
            carengineLen=6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        }else if(carEngineLen.equals("4")){
            carengineLen=6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        }else{
            carengineLen=6;//完整发动机号最低4位
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入完整发动机号");
        }
    }

    DoubtDialog doubtDialog=null;
    Intent intent=null;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.carCode_doubt_img://发动机号疑问号
                doubtDialog = new DoubtDialog(AddCarActivity.this, R.style.Dialog, R.drawable.carcode_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;
            case R.id.carEngine_doubt_img://车身架号疑问号
                doubtDialog = new DoubtDialog(AddCarActivity.this, R.style.Dialog, R.drawable.carengine_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;
            case R.id.provincecode_tv://省份前缀
                Log.d("test",provinces.toString());
                dialog = new ProvinceDialog(AddCarActivity.this, R.style.Dialog,provinces,1);
                dialog.showDialog();
                dialog.setCancelable(true);
                break;
            case R.id.addcar_back_img://返回按钮
                finish();
                overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
                break;
            case R.id.save_check_btn://保存并查询按钮
                if ((CarCode_et.getText().toString().trim().length()<carcodeLen) ||
                        (CarEngine_et.getText().toString().length()<carengineLen) ||(CarNumber_et.getText().toString().trim().length()<6)) {
                    UIHelper.ToastMessage(AddCarActivity.this,"输入信息不完整");
                }
//                else if(car_type==null||car_model_name==null){
//                    UIHelper.ToastMessage(AddCarActivity.this,"请选择车型或车系");
//                }
                else {
                    if(appContext.isNetworkConnected()) {
                        pd.show();
                        addcar(addGetParams());//添加车辆
                    }else{
                        Toast.makeText(AddCarActivity.this,"网络连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.car_model_layout://车系选择
                intent=new Intent(AddCarActivity.this,CarModelActivity.class);
                startActivityForResult(intent,CAR_MODEL_REQUEST_CODE);
                break;

            case R.id.car_type_layout://车型选择
                intent=new Intent(AddCarActivity.this,CarTypeActivity.class);
                startActivityForResult(intent,CAR_TYPE_REQUEST_CODE);
                break;
        }
    }


    /*
    * 车牌前缀的广播接收者
    * */
    class ProvinceBrocast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            province = intent.getStringExtra("province");
//            for(int i=0;i< ProprefixManager.proprefixList.size();i++){
//                if(province.equals(ProprefixManager.proprefixList.get(i).getProvincePrefix())) {
//                    new AlertDialog.Builder(AddCarActivity.this).setTitle("提示")
//                            .setMessage("由于系统数据升级维护中，车牌前缀为\"" + province + "\"的车辆请到主界面的\"其他订单\"页面添加并下单")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    AddCarActivity.this.finish();
//                                }
//                            }).show().setCanceledOnTouchOutside(false);
//                    }
//                }

                if (!province.equals(province_code_tv.getText().toString())) {
                    CarNumber_et.setText("");
                }
                province_code_tv.setText(province);
                for (City city : cities) {
                    if ((city.getCarNumberPrefix().substring(0, 1)).equals(province)) {
                        String carCode = city.getCarCodeLen();
                        String carEngine = city.getCarEngineLen();
                        viewChange(carCode, carEngine);
                        break;
                    }
                }
                dialog.dismiss();
        }
    }

    /*
    * 查询车辆违章请求参数
    * */
    HashMap<String,Object> queryMap;
    public HashMap<String, Object> queryGetParams(String carid) {
        queryMap = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        queryMap.put("token", token);
        queryMap.put("carid", carid);
        queryMap.put("searchtype","1");
        return queryMap;
    }

    /**
     * 保存车辆请求参数
     */
    HashMap<String, Object> map=new HashMap<String, Object>();
    public HashMap<String, Object> addGetParams() {
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("proprefix", province_code_tv.getText().toString().trim());
        map.put("carnumber", CarNumber_et.getText().toString().trim().toUpperCase());
        map.put("carcode", CarCode_et.getText().toString().trim().toUpperCase());
        map.put("cardrivenumber", CarEngine_et.getText().toString().trim().toUpperCase());
        map.put("title",add_remark_et.getText().toString().trim());
//        Log.d("TAG",map.toString());
//        Log.d("TAG","ddddd"+map.get("cartype").toString());
        return map;
    }

    /*
    * 查询违章
    * */
    public void qurery(HashMap<String, Object> map) {
        VolleyUtil.getVolleyUtil(AddCarActivity.this).StringRequestPostVolley(URLs.VIOLATION_QUERY, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                Intent intent=new Intent();
                intent.putExtra("carnumber",CarNumber_et.getText().toString().trim().toUpperCase());
                intent.putExtra("carcode",CarCode_et.getText().toString().trim().toUpperCase());
                intent.putExtra("carengine",CarEngine_et.getText().toString().trim().toUpperCase());
                intent.putExtra("proprefix",province_code_tv.getText().toString());
                intent.putExtra("carid",carid);
                intent.putExtra("title",add_remark_et.getText().toString());
                intent.putExtra("type","0");
//                intent.putExtra("cartype",car_type);
//                intent.putExtra("cartypename",car_code);
                intent.putExtra("cartype",car_code);
                intent.putExtra("cartypename",car_type);
//                intent.putExtra("carbrand",carbrand);
//                intent.putExtra("carname",car_model_name);
                intent.setAction(EditCarActivity.ADD_EDIT_DELETE_CAR_ACTION);
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AddCarActivity.this));
                    String status=obj.getString("status");
                    int code=obj.getInt("code");
                    UIHelper.showErrTips(code,AddCarActivity.this);
                    if(status.equals("ok")){//查询成功，车辆认证
                        intent.putExtra("ischeck","0");
                        int violationSize;
                        int totalDgree;
                        int totalCount;

                        JSONObject dataObj=obj.getJSONObject("data");
                        violationSize=dataObj.getInt("num");
                        totalDgree=dataObj.getInt("degree");
                        totalCount=dataObj.getInt("count");

//                        if(violationList!=null) {
//                            for (int k = 0; k < violationList.size(); k++) {
//                                if(violationList.get(k).getOrderstatus().equals("0")) {
//                                    totalDgree += Integer.valueOf(violationList.get(k).getDegree().trim());
//                                    Log.i("ViolationListManager", violationList.get(k).getDegree());
//                                    totalCount += Integer.valueOf(violationList.get(k).getCount().trim());
//                                    Log.i("ViolationListManager", violationList.get(k).getCount());
//                                    ++violationSize;
//                                }
//                            }
//                        }

                        intent.putExtra("violationSize",violationSize);
                        intent.putExtra("totalDgree",totalDgree+"");
                        intent.putExtra("totalCount",totalCount+"");

                        AddCarActivity.this.sendBroadcast(intent);
                        pd.dismiss();

//                        AddInfoTips addInfoTips=new AddInfoTips(AddCarActivity.this,R.style.Dialog,carid);
//                        addInfoTips.setCanceledOnTouchOutside(false);
//                        addInfoTips.show();

                        Toast.makeText(AddCarActivity.this,"添加车辆成功",Toast.LENGTH_SHORT).show();

                    }else{//查询失败，车辆认证失败
                        Toast.makeText(AddCarActivity.this,"车辆信息有误，请修改",Toast.LENGTH_SHORT).show();
                        intent.putExtra("ischeck","-1");
                        AddCarActivity.this.sendBroadcast(intent);
                        pd.dismiss();
                    }
                } catch (JSONException e) {
                    pd.dismiss();
                    intent.putExtra("ischeck","-1");
                    AddCarActivity.this.sendBroadcast(intent);
                    e.printStackTrace();
                }finally{
                    finish();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                finish();
                intent.putExtra("ischeck","-1");
                AddCarActivity.this.sendBroadcast(intent);
                pd.dismiss();
                Toast.makeText(AddCarActivity.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
    * 保存提交车辆信息
    * */
    public void addcar(HashMap<String, Object> map) {
        VolleyUtil.getVolleyUtil(AddCarActivity.this).StringRequestPostVolley(URLs.ADD_CAR, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject json = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AddCarActivity.this));
                    status = json.getString("status");
                    int code=json.getInt("code");
                    UIHelper.showErrTips(code,AddCarActivity.this);
                    if (status.equals("ok")) {//添加车辆成功
//                        Toast.makeText(AddCarActivity.this,"添加车辆成功",Toast.LENGTH_SHORT).show();
                        carid = json.getJSONObject("data").get("carid") + "";
                        qurery(queryGetParams(carid));//查询违章
                    }else{
                        pd.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
                Toast.makeText(AddCarActivity.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 返回键返回
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            finish();
            AddCarActivity.this.overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
            return false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
//            case CAR_MODEL_REQUEST_CODE://车系选择
//                if(data!=null){
//                    carbrand=data.getStringExtra("carbrand");//车品牌
//                    car_model_name=data.getStringExtra("carname");//系列名称
//
//                    map.put("carbrand",carbrand);
//                    map.put("carname",car_model_name);
//
//                    car_model_name_tv.setText(car_model_name);
//                    car_model_name_tv.setTextColor(Color.BLACK);
//                }
//                break;

            case CAR_TYPE_REQUEST_CODE://车型选择
                if(data!=null) {
                    car_type=data.getStringExtra("cartype");
                    car_code=data.getStringExtra("typecode");

                    car_type_tv.setText(car_type);
                    car_type_tv.setTextColor(Color.BLACK);

//                    map.put("cartype",car_type);
                    map.put("cartype",car_code);
                }

                break;
        }

    }
}
