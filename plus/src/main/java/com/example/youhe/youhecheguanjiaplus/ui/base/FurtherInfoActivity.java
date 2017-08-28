package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.Get122Url;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class FurtherInfoActivity extends Activity implements View.OnClickListener{

    private ClearEditText et_further_info_idcar,et_further_phone_num,et_further_122_psd,et_further_122_usename;
    private TextView go_to_122_tv;
    private TextView further_info_carnum_tv;

    private Button commit_other_order_btn;//提交订单按钮
    private Button further_info_close_button;


    private String provincecode,carnumber,carcode,cardrivenumber,title,strDrivinglicense,strDrivingsecondlicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otheroder_further_info);

        Intent extraIntent=getIntent();
        provincecode=extraIntent.getStringExtra("provincecode");
        carnumber=extraIntent.getStringExtra("carnumber");
        carcode=extraIntent.getStringExtra("carcode");
        cardrivenumber=extraIntent.getStringExtra("cardrivenumber");
        title=extraIntent.getStringExtra("title");

        initViews();

        pd=new ProgressDialog(FurtherInfoActivity.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

    }


    /**
    * 初始化控件
    * */
    private void initViews(){
        et_further_info_idcar= (ClearEditText) findViewById(R.id.et_further_info_idcar);
        et_further_phone_num= (ClearEditText) findViewById(R.id.et_further_phone_num);
        et_further_122_psd= (ClearEditText) findViewById(R.id.et_further_122_psd);
        et_further_122_usename= (ClearEditText) findViewById(R.id.et_further_122_usename);

        go_to_122_tv= (TextView) findViewById(R.id.go_to_122_tv);
        go_to_122_tv.setOnClickListener(this);

        commit_other_order_btn= (Button) findViewById(R.id.commit_other_order_btn);
        commit_other_order_btn.setOnClickListener(this);

        further_info_close_button= (Button) findViewById(R.id.further_info_close_button);
        further_info_close_button.setOnClickListener(this);

        further_info_carnum_tv= (TextView) findViewById(R.id.further_info_carnum_tv);
        if(carnumber!=null&&provincecode!=null) {
            further_info_carnum_tv.setText(provincecode + carnumber + "");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_to_122_tv://跳转到注册页面
                Intent intent=new Intent(FurtherInfoActivity.this,CommentWebActivity.class);
                intent.putExtra("url", Get122Url.get122url(provincecode));
                intent.putExtra("title","注册122官网");
                intent.putExtra("type",1);
                startActivity(intent);
                break;

            case R.id.commit_other_order_btn://提交订单
                if(et_further_info_idcar.getText().toString().trim().equals("")
                        ||et_further_phone_num.getText().toString().trim().equals("")
                        ||et_further_122_psd.getText().toString().trim().equals("")
                        ||et_further_122_usename.getText().toString().trim().equals("")
                        ){
                    Toast.makeText(FurtherInfoActivity.this,"请输入完整信息后再提交",Toast.LENGTH_LONG).show();
                }else if(et_further_info_idcar.getText().toString().trim().length()<18){
                    Toast.makeText(FurtherInfoActivity.this,"请输入完整车主身份证号后再提交",Toast.LENGTH_LONG).show();
                }else if(et_further_phone_num.getText().toString().trim().length()<11){
                    Toast.makeText(FurtherInfoActivity.this,"请输入完整车主手机号码后再提交",Toast.LENGTH_LONG).show();
                }else{
                    //提交订单
                    pd.show();
                    commitOtherOder(getCarParams());
                }
                break;

            case R.id.further_info_close_button:
                setResult(113);
                finish();
                break;
        }
    }


    /**
     * 获取其他订单提交参数
     */
    private TokenSQLUtils tokenSQLUtils;
    HashMap<String, Object> map;
    public HashMap<String, Object> getCarParams() {

        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("proprefix",provincecode);
        map.put("carnumber",carnumber.toUpperCase());
        map.put("carcode", carcode.toUpperCase());
        map.put("cardrivenumber",cardrivenumber.toUpperCase());
        map.put("title",title);
        map.put("drivinglicense",strDrivinglicense);
        map.put("drivingsecondlicense",strDrivingsecondlicense);

        map.put("cardcode_122",et_further_info_idcar.getText().toString().trim());
        map.put("mobile_122",et_further_phone_num.getText().toString().trim());
        map.put("pwd_122",et_further_122_psd.getText().toString().trim());
        map.put("username_122",et_further_122_usename.getText().toString().trim());
        return map;
    }


    private ProgressDialog pd;
    public void commitOtherOder(HashMap<String, Object> map){

        VolleyUtil.getVolleyUtil(FurtherInfoActivity.this).StringRequestPostVolley(URLs.COMMIT_OTHER_ORDER, map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(jsonObject.toString());
                    String status=obj.getString("status");
                    if(status.equals("ok")){

                        Intent intent=new Intent();
                        intent.putExtra("reflush","reflush");
                        intent.setAction("REFLUSH_ORDER_LIST");
                        sendBroadcast(intent);

                        setResult(114);
                        finish();

                    }else{
                        setResult(112);
                        finish();
                        Toast.makeText(FurtherInfoActivity.this,"下单失败，请重试或联系客服",Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e) {
                    Toast.makeText(FurtherInfoActivity.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }finally{
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
                Toast.makeText(FurtherInfoActivity.this, "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "添加车辆请求错误信息为：" + volleyError.toString());
            }
        });
    }

}
