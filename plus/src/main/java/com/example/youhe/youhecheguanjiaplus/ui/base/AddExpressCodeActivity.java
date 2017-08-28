package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/22.
 */

public class AddExpressCodeActivity extends Activity {
    private EditText express_code_et;
    private Button makesure_add_btn;
    private Context context;

    private ImageView back_iv;

    private String ordercoder;

//    public AddExpressNumberDialog(@NonNull Context context, @StyleRes int themeResId, String oredercode) {
//        super(context, themeResId);
//        this.context=context;
//        this.ordercoder=oredercode;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AddExpressCodeActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddExpressCodeActivity.this);
        setContentView(R.layout.dialog_add_express_number);

        context=AddExpressCodeActivity.this;

        ordercoder=getIntent().getStringExtra("ordercoder");

        initViews();
    }


    /*
    * 初始化控件
    * */
    private void initViews(){
        express_code_et= (EditText) findViewById(R.id.express_code_et);
        makesure_add_btn= (Button) findViewById(R.id.makesure_add_btn);
        makesure_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(express_code_et.getText().toString().trim().equals("")){
                    Toast.makeText(context,"请填写快递单号",Toast.LENGTH_SHORT).show();
                }else{//确定添加快递单号
                    addExpressNum();
                }
            }
        });

        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /*
    * 客户添加年检快递单号
    * */
    private void addExpressNum(){
        UIHelper.showPd(context);
        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("ordercode",ordercoder==null?"":ordercoder);//订单号
        map.put("clientexpress",express_code_et.getText().toString().trim());//快递单号

        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.ADD_EXPRESSE_NUMBER, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),context));
                    JSONObject dataObj=obj.optJSONObject("data");
                    int response_code=dataObj.optInt("response_code");
                    String response_msg=dataObj.optString("response_msg");
                    String ordercode=dataObj.optString("ordercode");
                    if(response_code==1){//添加成功
                        Intent intent=new Intent();
                        intent.putExtra("ordercode",ordercode);
                        AddExpressCodeActivity.this.setResult(123,intent);
                        AddExpressCodeActivity.this.finish();
                    }else{//添加失败
                        Toast.makeText(context,""+response_msg,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }
}
