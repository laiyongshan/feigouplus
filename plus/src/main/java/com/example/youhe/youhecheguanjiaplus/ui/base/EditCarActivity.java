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
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.DeleteCarDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.DoubtDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.ProvinceDialog;
import com.example.youhe.youhecheguanjiaplus.entity.base.City;
import com.example.youhe.youhecheguanjiaplus.entity.base.Province;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.manager.City_ProvinceManager;
import com.example.youhe.youhecheguanjiaplus.manager.ProprefixManager;
import com.example.youhe.youhecheguanjiaplus.manager.ViolationManager;
import com.example.youhe.youhecheguanjiaplus.utils.AllCapTransformationMethod;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.FileUtils;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/9/6 0006.
 * 编辑车辆
 */
public class EditCarActivity extends Activity implements View.OnClickListener {
    private ImageView editcar_back_img;
    private TextView carEngine_tv, carCode_tv;
    private TextView provincecode_tv;//省份简称
    private ImageView addcar_back_img;//返回按钮
    private Button save_btn;//保存按钮
    private TextView delete_car_tv;//删除
    private TextView verify_tv;//车辆是否验证，车型
    private ImageView carCode_doubt_img, carEngine_doubt_img;
    private EditText CarNumber_et, CarEngine_et, CarCode_et, remark_et;
    private String carnumber, carengine, carcode, remark;//车牌号，发动机号，车身架号，手机号码,备注,车型，品牌名称，系列
    public String province = "";//省份简称
    public ProvinceDialog dialog;//省份简称弹出框
    private ProvinceBrocast provinceBrocast;//接收选择的身份简称

    private ProgressDialog pd;//网络请求是弹出的Loading框

    private String carid = "";//车辆id
    private int page = 0;
    private String ischeck = "";
    private String proprefix = "";

    private int carcodeLen = 0, carengineLen = 0;
    private LinearLayout carCode_ll, carEngine_ll;
    private List<Province> provinces = new ArrayList<Province>();
    private List<City> cities = new ArrayList<City>();

    private LinearLayout updata_car_owner_info_ll;//车主资料
    private RelativeLayout car_type_layout, car_model_layout;
    private TextView car_type_tv;
    private String car_type;
    private String car_code;//车型代码

    private AppContext appContext;

    public static final String ADD_EDIT_DELETE_CAR_ACTION = "com.youhecheguanjia.editcar";

    public final int UPDATA_INFO = 110;
    public int isUpdata = 0;//是否已经修改车主资料

    AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod();//字母大写

    private final static int CAR_MODEL_REQUEST_CODE = 10001;
    private final static int CAR_TYPE_REQUEST_CODE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcar);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, EditCarActivity.this);
        }
        SystemBarUtil.useSystemBarTint(EditCarActivity.this);



        IntentFilter filter = new IntentFilter(ProvinceDialog.ACTION_NAME);
        provinceBrocast = new ProvinceBrocast();
        registerReceiver(provinceBrocast, filter);

        appContext = (AppContext) getApplicationContext();

        initCitydata();

        carid = getIntent().getStringExtra("carid");//需要编辑的车辆的carid
        carnumber = getIntent().getStringExtra("carnumber");
        carcode = getIntent().getStringExtra("carcode");
        carengine = getIntent().getStringExtra("cardrivenumber");
        remark = getIntent().getStringExtra("remark");
        proprefix = getIntent().getStringExtra("proprefix");
        ischeck = getIntent().getStringExtra("ischeck");
        page = getIntent().getIntExtra("page", 0);
        car_type = getIntent().getStringExtra("cartype")+"";

//        car_model_name = getIntent().getStringExtra("carname")+"";
//        carbrand = getIntent().getStringExtra("carbrand");

        initViews();

        for (City city : cities) {
            boolean cBool;
            if (city.getCarNumberPrefix().equals("京") || city.getCarNumberPrefix().equals("津")
                    || city.getCarNumberPrefix().equals("沪") || city.getCarNumberPrefix().equals("渝")) {
                cBool = city.getCarNumberPrefix().equals(carnumber.subSequence(0, 1).toString().toUpperCase());
            } else {
                cBool = city.getCarNumberPrefix().equals(carnumber.subSequence(0, 2).toString().toUpperCase());
            }
            if (cBool) {
                String carCodeLen = city.getCarCodeLen();
                String carEngineLen = city.getCarEngineLen();
                viewChange(carCodeLen, carEngineLen);
                break;
            }
        }
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
        UIHelper.showPd(EditCarActivity.this);
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
                            String json= FileUtils.readFromRaw(EditCarActivity.this);//使用缓存数据
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

    /**
     * 初始化控件
     */
    private void initViews() {
        pd = new ProgressDialog(EditCarActivity.this);
        pd.setMessage("请稍候...");
        pd.setCanceledOnTouchOutside(false);

//        car_model_name_tv = (TextView) findViewById(R.id.car_model_name_tv);
//        if (!carbrand.equals("")) {
//            car_model_name_tv.setText(car_model_name + "");
//        } else {
//            car_model_name_tv.setText("点击选择");
//        }
//        car_model_name_tv.setTextColor(Color.BLACK);
        car_type_tv = (TextView) findViewById(R.id.car_type_tv);
        if (!car_type.equals("")) {
            car_type_tv.setText(car_type + "");
        } else {
            car_type_tv.setText("点击选择");
        }
        car_type_tv.setTextColor(Color.BLACK);
        if(car_type.equals("02")){
            car_type_tv.setText("小型汽车");
        }

        car_type_layout = (RelativeLayout) findViewById(R.id.car_type_layout);
        car_type_layout.setOnClickListener(this);

        car_model_layout = (RelativeLayout) findViewById(R.id.car_model_layout);
        car_model_layout.setOnClickListener(this);

        car_type_layout = (RelativeLayout) findViewById(R.id.car_type_layout);
        car_type_layout.setOnClickListener(this);


        editcar_back_img = (ImageView) findViewById(R.id.editcar_back_img);
        editcar_back_img.setOnClickListener(this);

        carCode_doubt_img = (ImageView) findViewById(R.id.carCode_doubt_img);
        carCode_doubt_img.setOnClickListener(this);
        carEngine_doubt_img = (ImageView) findViewById(R.id.carEngine_doubt_img);
        carEngine_doubt_img.setOnClickListener(this);

        delete_car_tv = (TextView) findViewById(R.id.delete_car_tv);//删除车辆
        delete_car_tv.setOnClickListener(this);

        save_btn = (Button) findViewById(R.id.save_btn);//保存按钮
        save_btn.setOnClickListener(this);

        carEngine_tv = (TextView) findViewById(R.id.carEngine_tv);//发动机号
        carEngine_tv.setOnClickListener(this);
        carCode_tv = (TextView) findViewById(R.id.carCode_tv);//车身架号
        carCode_tv.setOnClickListener(this);
        provincecode_tv = (TextView) findViewById(R.id.provincecode_tv);//省份前缀
        provincecode_tv.setText(proprefix);
        provincecode_tv.setOnClickListener(this);

        CarEngine_et = (EditText) findViewById(R.id.CarEngine_et);//发动机号输入框
        CarEngine_et.setText(carengine);
        CarEngine_et.setTransformationMethod(allCapTransformationMethod);
        CarCode_et = (EditText) findViewById(R.id.CarCode_et);//车身架号输入框
        CarCode_et.setText(carcode);
        CarCode_et.setTransformationMethod(allCapTransformationMethod);
        remark_et = (EditText) findViewById(R.id.remark_et);//备注输入框
        if (remark == null) {
            remark_et.setText("");
        } else {
            remark_et.setText(remark + "");
        }
        verify_tv = (TextView) findViewById(R.id.verify_tv);//车辆是否已经验证
        if (ischeck.equals("-1")) {
            verify_tv.setText("未通过");
            verify_tv.setTextColor(Color.RED);
        } else {
            verify_tv.setText("已验证");
        }

        carCode_ll = (LinearLayout) findViewById(R.id.carCode_ll);
        carEngine_ll = (LinearLayout) findViewById(R.id.carEngine_ll);

        updata_car_owner_info_ll = (LinearLayout) findViewById(R.id.updata_car_owner_info_ll);
        updata_car_owner_info_ll.setOnClickListener(this);

        CarNumber_et = (EditText) findViewById(R.id.CarNumber_et);//车牌号码输入框
        CarNumber_et.setText(carnumber.substring(1));
        CarNumber_et.setTransformationMethod(allCapTransformationMethod);
        CarNumber_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 1) {
//                    Toast.makeText(EditCarActivity.this,s.subSequence(0,1),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().trim().equals("")) {
                    CarEngine_et.setText("");
                    CarCode_et.setText("");
                } else {
                    String pfixe = provincecode_tv.getText().toString();
                    if (s.length() == 1) {
//                        Toast.makeText(EditCarActivity.this, "车牌前缀是：" + pfixe + s.subSequence(0, 1).toString().toUpperCase(), Toast.LENGTH_SHORT).show();
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

    private void viewChange(String carCode, String carEngine) {
        if (carCode.equals("0")) {
            carcodeLen = 6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        } else if (carCode.equals("99")) {
            carcodeLen = 17;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入完整车身架号");
        } else if (carCode.equals("6")) {
            carcodeLen = 6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        } else if (carCode.equals("4")) {
            carcodeLen = 6;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入车身架号后6位");
        } else {
            carcodeLen = 17;
            carCode_ll.setVisibility(View.VISIBLE);
            CarCode_et.setHint("请输入完整车身架号");
        }

        if (carEngine.equals("0")) {
            carengineLen = 6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        } else if (carEngine.equals("99")) {
            carengineLen = 6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入完整发动机号");
        } else if (carEngine.equals("6")) {
            carengineLen = 6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        } else if (carEngine.equals("4")) {
            carengineLen = 6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入发动机号后6位");
        } else {
            carengineLen = 6;
            carEngine_ll.setVisibility(View.VISIBLE);
            CarEngine_et.setHint("请输入完整发动机号");
        }
    }


    DoubtDialog doubtDialog = null;
    Intent intent = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.provincecode_tv://省份前缀
                dialog = new ProvinceDialog(EditCarActivity.this, R.style.Dialog, provinces, 1);
                dialog.showDialog();
                dialog.setCancelable(true);
                break;
            case R.id.editcar_back_img://返回按钮
                finish();
                overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
                break;
            case R.id.save_btn://保存按钮
                if (StringUtils.isEmpty(CarCode_et.getText().toString().trim()) ||
                        StringUtils.isEmpty(CarEngine_et.getText().toString().trim()) || (CarNumber_et.getText().toString().trim().length() < 6)) {
                    UIHelper.ToastMessage(EditCarActivity.this, "输入信息不完整");
                }
                else if (car_type == null || car_type.equals("") ) {
                    UIHelper.ToastMessage(EditCarActivity.this, "请选择车型或车系");
                }
                else if (carcode.equals(CarCode_et.getText().toString().trim()) && carengine.equals(CarEngine_et.getText().toString().trim())
                        && carnumber.substring(1).equals(CarNumber_et.getText().toString().trim())
                        && remark.equals(remark_et.getText().toString().trim())
                        && proprefix.equals(provincecode_tv.getText().toString().trim())
                        && car_type.equals(car_type_tv.getText().toString().trim())
                        && isUpdata != 1) {

                    UIHelper.ToastMessage(EditCarActivity.this, "内容未修改，无需提交");

                } else if ((CarCode_et.getText().toString().trim().length() < carcodeLen) ||
                        (CarEngine_et.getText().toString().length() < carengineLen) || (CarNumber_et.getText().toString().trim().length() < 6)) {
                    UIHelper.ToastMessage(EditCarActivity.this, "输入信息不完整");
                }
//                else if(isProvice(provincecode_tv.getText().toString().trim())){
//                    new AlertDialog.Builder(EditCarActivity.this).setTitle("提示")
//                            .setMessage("由于系统数据升级维护中，车牌前缀为\""+province+"\"的车辆请到主界面的其他订单页面添加并下单")
//                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                }
//                            }).show().setCanceledOnTouchOutside(false);
//                }
                else {
                    if (appContext.isNetworkConnected()) {
//                        if((!proprefix.equals(provincecode_tv.getText().toString().trim())
//                                ||(!carnumber.substring(1).equals(CarNumber_et.getText().toString().trim())))&&isUpdata==0){
//                            AlertDialog.Builder alertDialog=new AlertDialog.Builder(EditCarActivity.this);
//                            alertDialog.setTitle("提示");
//                            alertDialog.setMessage("若修改车牌号码，请点击“车主资料”，修改和上传行驶证和驾驶证照片再保存");
//                            alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent editPhotoIntent=new Intent(EditCarActivity.this,UploadProActivity.class);
//                                    editPhotoIntent.putExtra("carid",carid);
//                                    startActivityForResult(editPhotoIntent,110);
//                                }
//                            });
//                            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            });
//                            alertDialog.show();
//                        }else{
                        pd.show();
                        editcarCommit(getEditParams());//保存并提交新编辑的车辆信息
//                        }
                    } else {
                        UIHelper.ToastMessage(EditCarActivity.this, "网络连接失败，请检查网络连接设置");
                    }
                }
                break;
            case R.id.delete_car_tv://删除车辆
                DeleteCarDialog deleteCarDialog = new DeleteCarDialog(EditCarActivity.this, R.style.Dialog, carid);
                deleteCarDialog.showDialog();
                deleteCarDialog.setCancelable(true);
                break;

            case R.id.carCode_doubt_img:
                doubtDialog = new DoubtDialog(EditCarActivity.this, R.style.Dialog, R.drawable.carcode_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;
            case R.id.carEngine_doubt_img:
                doubtDialog = new DoubtDialog(EditCarActivity.this, R.style.Dialog, R.drawable.carengine_img);
                doubtDialog.showDialog();
                doubtDialog.setCancelable(true);
                break;

            case R.id.updata_car_owner_info_ll://修改车主资料
                intent = new Intent(EditCarActivity.this, UploadProActivity.class);
//                intent=new Intent(EditCarActivity.this,UploadDravingLisenceActivivty.class);
                intent.putExtra("carid", carid);
                startActivityForResult(intent, UPDATA_INFO);
                break;

            case R.id.car_model_layout://车系选择
                intent = new Intent(EditCarActivity.this, CarModelActivity.class);
                startActivityForResult(intent, CAR_MODEL_REQUEST_CODE);
                break;

            case R.id.car_type_layout://车型选择
                intent = new Intent(EditCarActivity.this, CarTypeActivity.class);
                startActivityForResult(intent, CAR_TYPE_REQUEST_CODE);
                break;
        }

    }

    public boolean isProvice(String province) {
        for (int i = 0; i < ProprefixManager.proprefixList.size(); i++) {
            if (province.equals(ProprefixManager.proprefixList.get(i).getProvincePrefix())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 省份前缀选择的广播接受者
     */
    class ProvinceBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            province = intent.getStringExtra("province");
//            if(isProvice(province)){
//                    new AlertDialog.Builder(EditCarActivity.this).setTitle("提示")
//                            .setMessage("由于系统数据升级维护中，车牌前缀为\""+province+"\"的车辆请到主界面的\"其他订单\"页面添加并下单")
//                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            }).setNegativeButton("取消",null).show().setCanceledOnTouchOutside(false);
//
//            }else{
            if (!province.equals(provincecode_tv.getText().toString())) {
                CarNumber_et.setText("");
            }
            provincecode_tv.setText(province);
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
    * 保存提交车辆信息请求参数
    * */
    HashMap<String, Object> map = new HashMap<String, Object>();

    public HashMap<String, Object> getEditParams() {
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("proprefix", provincecode_tv.getText().toString().trim());//车牌前缀
        map.put("carnumber", CarNumber_et.getText().toString().trim());//车牌号码
        map.put("carcode", CarCode_et.getText().toString().trim());//车身架号
        map.put("cardrivenumber", CarEngine_et.getText().toString().trim());//发动机号
        map.put("title", remark_et.getText().toString().trim() + "");//备注
        map.put("carid", carid);
//        map.put("carbrand", carbrand + "");
//        map.put("carname", car_model_name + "");
        map.put("cartype", car_type + "");
        return map;
    }

    /**
     * 查询车辆违章需要的车辆
     */
    HashMap<String, Object> queryMap = new HashMap<String, Object>();

    public HashMap<String, Object> queryGetParams() {
        String token = TokenSQLUtils.check();
        queryMap.put("token", token);
        queryMap.put("carid", carid);
        queryMap.put("searchtype", "1");
        return queryMap;
    }


    /*
    * 保存车辆并查询
    * */
    public void editcarCommit(HashMap<String, Object> map) {
        VolleyUtil.getVolleyUtil(EditCarActivity.this).StringRequestPostVolley(URLs.EDIT_CAR, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), EditCarActivity.this));
                    String status = obj.getString("status");
                    int code = obj.getInt("code");
                    UIHelper.showErrTips(code, EditCarActivity.this);
                    if (status.equals("ok")) {
                        qurery(queryGetParams());//修改车辆信息成功，查询该车辆的违章信息

                        remark = remark_et.getText().toString().trim();
                        carnumber = provincecode_tv.getText().toString().trim() + CarNumber_et.getText().toString().trim();
                        carengine = CarEngine_et.getText().toString().trim();
                        carcode = CarCode_et.getText().toString().trim();
                        proprefix = provincecode_tv.getText().toString().trim();
//                        carbrand = car_model_name_tv.getText().toString().trim();
                        car_type = car_type_tv.getText().toString().trim();


                    } else {
                        pd.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
                Toast.makeText(EditCarActivity.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    * 查询违章
    * */
    public void qurery(HashMap<String, Object> map) {
        VolleyUtil.getVolleyUtil(EditCarActivity.this).StringRequestPostVolley(URLs.VIOLATION_QUERY, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Intent intent = new Intent();
                intent.putExtra("carnumber", CarNumber_et.getText().toString().trim().toUpperCase());
                intent.putExtra("carcode", CarCode_et.getText().toString().trim().toUpperCase());
                intent.putExtra("carengine", CarEngine_et.getText().toString().trim().toUpperCase());
                intent.putExtra("proprefix", provincecode_tv.getText().toString());
                intent.putExtra("carid", carid);
                intent.putExtra("title", remark_et.getText().toString());
                intent.putExtra("type", "1");
                intent.putExtra("page", page);
                intent.putExtra("cartype", car_type);
//                intent.putExtra("carname", car_model_name);
//                intent.putExtra("carbrand", carbrand);

                intent.setAction(ADD_EDIT_DELETE_CAR_ACTION);
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), EditCarActivity.this));
                    String status = obj.getString("status");
                    int code = obj.getInt("code");
                    UIHelper.showErrTips(code, EditCarActivity.this);
                    if (status.equals("ok")) {

                        Toast.makeText(EditCarActivity.this, "车辆信息保存成功", Toast.LENGTH_LONG).show();

                        intent.putExtra("ischeck", "0");
                        List<Violation> violationList = ViolationManager.json2Violations(jsonObject.toString());//违章List

                        int violationSize = 0;
                        int totalDgree = 0;
                        int totalCount = 0;

                        JSONObject dataObj = obj.getJSONObject("data");
                        violationSize = dataObj.getInt("num");
                        totalDgree = dataObj.getInt("degree");
                        totalCount = dataObj.getInt("count");

//                        if(violationList!=null) {
//                            for (int k = 0; k < violationList.size(); k++) {
//                                if(violationList.get(k).getOrderstatus().equals("0")) {
//                                    totalDgree += Integer.valueOf(violationList.get(k).getDegree());
//                                    totalCount += Integer.valueOf(violationList.get(k).getCount());
//                                    ++violationSize;
//                                }
//                            }
//                        }

                        intent.putExtra("violationSize", violationSize);//未处理违章条数
                        intent.putExtra("totalDgree", totalDgree + "");//未处理违章分数
                        intent.putExtra("totalCount", totalCount + "");//未处理违章罚款

                        EditCarActivity.this.sendBroadcast(intent);//发送广播修改首页的车辆信息显示
                        EditCarActivity.this.finish();
                    } else if (status.equals("fail")) {//查询失败，车辆不认证
                        intent.putExtra("ischeck", "-1");
                        EditCarActivity.this.sendBroadcast(intent);//发送广播修改首页的车辆信息显示
                        EditCarActivity.this.finish();
                        Toast.makeText(EditCarActivity.this, "车辆信息有误，请重新编辑！", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EditCarActivity.this, "网络请求错误，请重试！", Toast.LENGTH_LONG).show();
                }

                pd.dismiss();
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
                Toast.makeText(EditCarActivity.this, "网络请求错误，请重试！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATA_INFO) {
            if (data != null) {
                isUpdata = data.getIntExtra("isUpdata", 1);
            }
        }
//        else if (requestCode == CAR_MODEL_REQUEST_CODE) {
//            if (data != null) {
//                if (data.hasExtra("carbrand"))
//                    carbrand = data.getStringExtra("carbrand");//车品牌
//                if (data.hasExtra("carname")) {
//                    car_model_name = data.getStringExtra("carname");//系列名称
//
////                map.put("carbrand",carbrand);
////                map.put("carname",car_model_name);
//                    car_model_name_tv.setText(car_model_name+"");
//                    car_model_name_tv.setTextColor(Color.BLACK);
//                }
//            }
//        }
        else if (requestCode == CAR_TYPE_REQUEST_CODE) {
            if (data != null) {
                if (data.hasExtra("cartype")) {
                    car_type = data.getStringExtra("cartype");
                    car_type_tv.setText(car_type);
                    car_type_tv.setTextColor(Color.BLACK);
                    car_type=data.getStringExtra("typecode");
                }
            }
        }
    }

    /**
     * 返回键返回事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.bottom_int, R.anim.bottom_out);
            return false;
        }
        return false;
    }

}
