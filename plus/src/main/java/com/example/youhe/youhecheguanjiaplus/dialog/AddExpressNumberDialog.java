package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/21.
 */

public class AddExpressNumberDialog extends Dialog {

    private EditText express_code_et;
    private Button makesure_add_btn;
    private Context context;

    private String ordercoder;

    public AddExpressNumberDialog(@NonNull Context context, @StyleRes int themeResId,String oredercode) {
        super(context, themeResId);
        this.context=context;
        this.ordercoder=oredercode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_express_number);

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

    }


    /*
    * 客户添加年检快递单号
    * */
    private void addExpressNum(){
        UIHelper.showPd(context);
        HashMap map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("ordercode",ordercoder);//订单号
        map.put("clientexpress",express_code_et.getText().toString().trim());//快递单号

        VolleyUtil.getVolleyUtil(context).StringRequestPostVolley(URLs.ADD_EXPRESSE_NUMBER, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","添加快递单号："+EncryptUtil.decryptJson(jsonObject.toString(),context));
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),context));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UIHelper.dismissPd();
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }


}
