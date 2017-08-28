package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/22 0022.
 * 添加驾照
 */

public class AddMajorviolationActivity extends Activity implements View.OnClickListener {

    private ImageView add_lisence_back_img;
    private EditText majorviolation_name_et, file_number_et, driving_license_no_et,
            driving_license_phone_number, car_owner_phone_et, chezhu_idcar_info_et,
            tiaoxingma_info_et, dengji_zhengshu_info_et, xingshi_danganbianhao_et, car_carcode_et;

    private LinearLayout chezhu_idcar_ll, tiaoxiangma_ll, dengji_zhengshu_ll, xingshi_danganbianhao_ll, car_carcode_ll;//车主身份证号，驾照条形码，车辆登记证书号,行驶证档案编号,发动机号
    private LinearLayout upload_car_owner_info_ll;//上传驾驶证，行驶证照片

    private Button save_majorviolation_btn;
    private String carId = "";
    private Car car;

    private ProgressDialog pd;

    private boolean[] isNeed;
    private boolean isNeedCarCode, isNeedIdCar, isNeedTiaoxingma, isNeedZhengshu, isNeedXingshi, isNeedMajorViolation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_majorviolation);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, AddMajorviolationActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddMajorviolationActivity.this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        Intent extraIntent = getIntent();

        isNeed = extraIntent.getBooleanArrayExtra("isNeed");

        if (isNeed != null && isNeed.length >= 4) {
            isNeedCarCode = isNeed[0];//是否要补充车辆发动机号
            isNeedIdCar = isNeed[1];//是否要添加车主身份证号码
            isNeedTiaoxingma = isNeed[2];//是否要添加驾照条形码
            isNeedZhengshu = isNeed[3];//是否要添加车辆登记证书号
            isNeedXingshi = isNeed[4];//是否需要行驶证档案编号
            isNeedMajorViolation = isNeed[5];//是否需要上传驾驶证照片
        }

        car = new Car();
        car = (Car) getIntent().getSerializableExtra("car");
        if (car != null) {
//            if(!car.getCarcode().toString().equals("")){
//                isNeedCarCode=true;
//            }
            if (!StringUtils.isEmpty(car.getOwnercardlen()))
                isNeedIdCar = true;
            if (!StringUtils.isEmpty(car.getCheliangzhengshulen()))
                isNeedZhengshu = true;
            if (!StringUtils.isEmpty(car.getTiaoxingmalen()))
                isNeedTiaoxingma = true;
            if (!StringUtils.isEmpty(car.getXingshizhenghaoLen()))
                isNeedXingshi = true;
            if (!StringUtils.isEmpty(car.getMajorviolation()))
                isNeedMajorViolation = true;
        }
        carId = getIntent().getStringExtra("carid");

        initView();//初始化控件
    }

    private void initView() {
        add_lisence_back_img = (ImageView) findViewById(R.id.add_lisence_back_img);//返回按钮
        add_lisence_back_img.setOnClickListener(this);
        majorviolation_name_et = (EditText) findViewById(R.id.majorviolation_name_et);//驾驶人姓名
        file_number_et = (EditText) findViewById(R.id.file_number_et);//档案编号
        driving_license_no_et = (EditText) findViewById(R.id.driving_license_no_et);//驾驶证号
        driving_license_phone_number = (EditText) findViewById(R.id.driving_license_phone_number);//手机号码
        car_owner_phone_et = (EditText) findViewById(R.id.car_owner_phone_et);//车主联系电话

        chezhu_idcar_info_et = (EditText) findViewById(R.id.chezhu_idcar_info_et);//车主身份证号
        tiaoxingma_info_et = (EditText) findViewById(R.id.tiaoxingma_info_et);//驾照条形码
        dengji_zhengshu_info_et = (EditText) findViewById(R.id.dengji_zhengshu_info_et);//车辆登记证书号
        xingshi_danganbianhao_et = (EditText) findViewById(R.id.xingshi_danganbianhao_et);//行驶证档案编号
        car_carcode_et = (EditText) findViewById(R.id.car_carcode_et);

        car_carcode_ll = (LinearLayout) findViewById(R.id.car_carcode_ll);
        if (isNeedCarCode) {
            car_carcode_ll.setVisibility(View.VISIBLE);
        }

        upload_car_owner_info_ll = (LinearLayout) findViewById(R.id.upload_car_owner_info_ll);//上传驾照，行驶证图片
        upload_car_owner_info_ll.setOnClickListener(this);
        if (isNeedMajorViolation) {
            upload_car_owner_info_ll.setVisibility(View.VISIBLE);
        }

        chezhu_idcar_ll = (LinearLayout) findViewById(R.id.chezhu_idcar_ll);
        if (isNeedIdCar) {
            chezhu_idcar_ll.setVisibility(View.VISIBLE);
        }
        tiaoxiangma_ll = (LinearLayout) findViewById(R.id.tiaoxiangma_ll);
        if (isNeedTiaoxingma) {
            tiaoxiangma_ll.setVisibility(View.VISIBLE);
        }
        dengji_zhengshu_ll = (LinearLayout) findViewById(R.id.dengji_zhengshu_ll);
        if (isNeedZhengshu) {
            dengji_zhengshu_ll.setVisibility(View.VISIBLE);
        }

        xingshi_danganbianhao_ll = (LinearLayout) findViewById(R.id.xingshi_danganbianhao_ll);
        if (isNeedXingshi) {
            xingshi_danganbianhao_ll.setVisibility(View.VISIBLE);
        }

        save_majorviolation_btn = (Button) findViewById(R.id.save_majorviolation_btn);//保存按钮
        save_majorviolation_btn.setOnClickListener(this);

        if (car != null) {
            majorviolation_name_et.setText(car.getCarownerlen());
            driving_license_no_et.setText(car.getJashizhenghaoLen());
            file_number_et.setText(car.getDanganbianhaolen());
            driving_license_phone_number.setText(car.getFilephonelen());
            car_owner_phone_et.setText(car.getCarownerphonelen());
            chezhu_idcar_info_et.setText(car.getOwnercardlen());
            tiaoxingma_info_et.setText(car.getTiaoxingmalen());
            dengji_zhengshu_info_et.setText(car.getTiaoxingmalen());
            xingshi_danganbianhao_et.setText(car.getXingshizhenghaoLen());
            car_carcode_et.setText(car.getCarcode());
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_lisence_back_img:
                finish();
                break;
            case R.id.save_majorviolation_btn:
                if (majorviolation_name_et.getText().toString().trim().equals("") || file_number_et.getText().toString().trim().equals("") ||
                        driving_license_no_et.getText().toString().equals("") || driving_license_phone_number.getText().toString().trim().equals("")
                        || car_owner_phone_et.getText().toString().equals("")) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整信息", Toast.LENGTH_LONG).show();
                } else if (chezhu_idcar_ll.getVisibility() == View.VISIBLE && chezhu_idcar_info_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整信息", Toast.LENGTH_LONG).show();
                } else if (tiaoxiangma_ll.getVisibility() == View.VISIBLE && tiaoxingma_info_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整信息", Toast.LENGTH_LONG).show();
                } else if (dengji_zhengshu_ll.getVisibility() == View.VISIBLE && dengji_zhengshu_info_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整信息", Toast.LENGTH_LONG).show();
                } else if (xingshi_danganbianhao_ll.getVisibility() == View.VISIBLE && xingshi_danganbianhao_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整信息", Toast.LENGTH_LONG).show();
                } else if (car_carcode_ll.getVisibility() == View.VISIBLE && car_carcode_et.getText().toString().trim().length() < 6) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整车辆发动机号", Toast.LENGTH_LONG).show();
                } else if (file_number_et.getText().toString().trim().length() < 12) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整档案编号", Toast.LENGTH_LONG).show();
                } else if (driving_license_no_et.getText().toString().trim().length() < 18) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入完整驾驶证号", Toast.LENGTH_LONG).show();
                } else if (driving_license_phone_number.getText().toString().trim().length() != 11 || car_owner_phone_et.getText().toString().trim().length() != 11) {
                    Toast.makeText(AddMajorviolationActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                } else {
                    commitMajorViolation(getMajorViolationParams(), URLs.UPLOAD_FILE);
                }
                break;

            case R.id.upload_car_owner_info_ll:
                Intent intent = new Intent(AddMajorviolationActivity.this, UploadProActivity.class);
                intent.putExtra("carid", carId);
                startActivity(intent);
                break;
        }
    }

    public HashMap<String, Object> getMajorViolationParams() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("carid", carId);
        map.put("carownerlen", majorviolation_name_et.getText().toString().trim());
        map.put("danganbianhaolen", file_number_et.getText().toString().trim());
        map.put("jashizhenghaolen", driving_license_no_et.getText().toString().trim());
        map.put("filephonelen", driving_license_phone_number.getText().toString().trim());
        map.put("carownerphonelen", car_owner_phone_et.getText().toString().trim());
        map.put("ownercardlen", chezhu_idcar_info_et.getText().toString().trim());
        map.put("tiaoxingmalen", tiaoxingma_info_et.getText().toString().trim());
        map.put("cheliangzhengshulen", dengji_zhengshu_info_et.getText().toString().trim());
        map.put("xingshizhenghaolen", xingshi_danganbianhao_et.getText().toString().trim());
        map.put("carcode", car_carcode_et.getText().toString().trim());
        return map;
    }

    public void commitMajorViolation(HashMap<String, Object> map, String url) {
        pd.show();
        VolleyUtil.getVolleyUtil(AddMajorviolationActivity.this).StringRequestPostVolley(url, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject object = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), AddMajorviolationActivity.this));
                    String status = object.getString("status");
                    if (status.equals("ok")) {
                        Toast.makeText(AddMajorviolationActivity.this, "驾照信息保存成功", Toast.LENGTH_LONG).show();
                        AddMajorviolationActivity.this.finish();
                    } else if (status.equals("fail")) {
                        Toast.makeText(AddMajorviolationActivity.this, "保存失败,请检查是否有对内容修改", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    pd.dismiss();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(AddMajorviolationActivity.this, "保存失败\n" + volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 返回键返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return false;
        }
        return false;
    }

}
