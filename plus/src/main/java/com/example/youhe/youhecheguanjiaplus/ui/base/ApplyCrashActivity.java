package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.PswDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.ScriptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class ApplyCrashActivity extends Activity implements View.OnClickListener{

    private ImageButton apply_crash_back_ib;
    private RelativeLayout bankcard_layout;
    private TextView bank_name_tv,last_num_tv;
    private ImageView union_icon_iv;
    private EditText money_et;
    private TextView amount_tv;
    private TextView all_amount_tv;
    private TextView poundage_tv;//需要手续费百分比
    private TextView gather_tv;//收取服务费金额
    private Button sure_apply_crash_btn;

    private String bankid="";//银行卡id
    private float remaining_sum;//客户余额
    private String fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_crash);


        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, ApplyCrashActivity.this);
        }
        SystemBarUtil.useSystemBarTint(ApplyCrashActivity.this);

        initView();//初始化

        getFee();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getRemainingSum();//获取用户余额信息
    }

    private void initView(){

        apply_crash_back_ib= (ImageButton) findViewById(R.id.apply_crash_back_ib);
        apply_crash_back_ib.setOnClickListener(this);

        bankcard_layout= (RelativeLayout) findViewById(R.id.bankcard_layout);
        bankcard_layout.setOnClickListener(this);

        poundage_tv= (TextView) findViewById(R.id.poundage_tv);
        gather_tv= (TextView) findViewById(R.id.gather_tv);

        bank_name_tv= (TextView) findViewById(R.id.bank_name_tv);
        last_num_tv= (TextView) findViewById(R.id.last_num_tv);
        union_icon_iv= (ImageView) findViewById(R.id.union_icon_iv);

        money_et= (EditText) findViewById(R.id.money_et);

        amount_tv= (TextView) findViewById(R.id.amount_tv);
        amount_tv.setText(""+remaining_sum);

        all_amount_tv= (TextView) findViewById(R.id.all_amount_tv);
        all_amount_tv.setOnClickListener(this);

        sure_apply_crash_btn= (Button) findViewById(R.id.sure_apply_crash_btn);
        sure_apply_crash_btn.setOnClickListener(this);

        sure_apply_crash_btn.setClickable(false);
        sure_apply_crash_btn.setBackgroundResource(R.drawable.tijiaoa2);

        money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null||s.toString().trim().equals("")){
                    sure_apply_crash_btn.setClickable(false);
                    sure_apply_crash_btn.setBackgroundResource(R.drawable.tijiaoa2);
                    gather_tv.setVisibility(View.GONE);
                }else{
                    if(!bankid.equals("")) {
                        sure_apply_crash_btn.setClickable(true);
                        sure_apply_crash_btn.setBackgroundResource(R.drawable.affirmbutton3);
                    }

                    if(remaining_sum!=0) {
                        gather_tv.setVisibility(View.VISIBLE);
                        Object fun = ScriptUtil.runScript(ApplyCrashActivity.this, function, function_name, new String[]{money_et.getText().toString().trim()});
                        NumberFormat nf = new DecimalFormat("##.####");
                        try {
                            fee = nf.format(Double.valueOf(fun.toString()));
                            gather_tv.setText("需要手续费￥" + fee);
                        } catch (Exception e) {

                        }
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.apply_crash_back_ib:
                finish();
                break;

            case R.id.sure_apply_crash_btn:
                if(remaining_sum==0){
                    Toast.makeText(ApplyCrashActivity.this,"余额为零，不可提现",Toast.LENGTH_SHORT).show();
                }else if(Float.valueOf(money_et.getText().toString().trim())==0){
                    Toast.makeText(ApplyCrashActivity.this,"0元不提现！",Toast.LENGTH_SHORT).show();
                }else if(Float.valueOf(money_et.getText().toString().trim())>remaining_sum){
                    Toast.makeText(ApplyCrashActivity.this,"余额不足\n余额不足",Toast.LENGTH_SHORT).show();
                }else {
                    final PswDialog pswDialog = new PswDialog(ApplyCrashActivity.this, R.style.Dialog,2);
                    pswDialog.show();

                    //回调接口
                    pswDialog.mSureListener = new PswDialog.OnSureListener() {
                        @Override
                        public void onSure(String psw) {
                            applycrash(psw);//提现申请
                            pswDialog.dismiss();
                        }
                    };
                }
                break;

            case R.id.bankcard_layout:
                Intent intent=new Intent(ApplyCrashActivity.this,MyBankCardActivity.class);
                startActivityForResult(intent,801);
                break;

            case R.id.all_amount_tv:
                if(remaining_sum!=0) {
                    money_et.setText(amount_tv.getText().toString().trim());
                }else{
                    Toast.makeText(ApplyCrashActivity.this, "余额为0,不可提现", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==8001){
            union_icon_iv.setImageResource(R.drawable.yinliantu);
            bank_name_tv.setText(data.getStringExtra("bank_name")+"");
//            "尾号 " + bankCardList.get(i).getBank_code().substring(bankCardList.get(i).getBank_code().length() - 4)
            last_num_tv.setText( "尾号 " +data.getStringExtra("bank_code").substring(data.getStringExtra("bank_code").length()-4)+ " 储蓄卡");
            last_num_tv.setVisibility(View.VISIBLE);
            bankid=data.getStringExtra("bank_id");

            if(!money_et.getText().toString().trim().equals("")){

                sure_apply_crash_btn.setClickable(true);
                sure_apply_crash_btn.setBackgroundResource(R.drawable.affirmbutton3);

            }
        }

    }

    //提现申请
    public void applycrash(String psw){

        UIHelper.showPd(ApplyCrashActivity.this);

        VolleyUtil.getVolleyUtil(ApplyCrashActivity.this).StringRequestPostVolley(URLs.ADD_WITHDRAWALS, EncryptUtil.encrypt(getApplycrashParams(psw)), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    pareJosn(EncryptUtil.decryptJson(jsonObject.toString(),ApplyCrashActivity.this));
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                UIHelper.dismissPd();
            }
        });
    }



    private void  pareJosn(String json){
        try {
            JSONObject object=new JSONObject(json);
            String status=object.getString("status");
            if(status.equals("ok")){
                Toast.makeText(ApplyCrashActivity.this,"提交成功，请等待审核！",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }else{
                if(object.has("show_msg")){
                    String show_msg=object.optString("show_msg");
                    Toast.makeText(ApplyCrashActivity.this,"提现失败，"+show_msg,Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    HashMap map;
    public HashMap getApplycrashParams(String psw){
        map = new HashMap();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            map.put("token", token);
        }

        map.put("bankid",bankid);
        map.put("money",money_et.getText().toString().trim());
        map.put("password",ParamSign.getUserPassword(psw));
        map.put("fee",fee);//提现手续费

        return map;
    }


    HashMap params;
    private HashMap getRemainingSumParams(){
        params = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            params.put("token", token);
        }
        return  params;
    }

    //余额显示
    private void getRemainingSum(){

        UIHelper.showPd(this);

        VolleyUtil.getVolleyUtil(ApplyCrashActivity.this).StringRequestPostVolley(URLs.GET_CLIENT_REMAINING_SUM, EncryptUtil.encrypt(getRemainingSumParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),ApplyCrashActivity.this));
                    JSONObject dataObj=obj.getJSONObject("data");
                    remaining_sum=Float.valueOf(dataObj.getString("remaining_sum"));
                    amount_tv.setText(remaining_sum+"");
                    if(dataObj.getString("remaining_sum").equals("0.00")){
                        all_amount_tv.setClickable(false);
                        all_amount_tv.setEnabled(false);
                        all_amount_tv.setTextColor(Color.GRAY);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    all_amount_tv.setClickable(false);
                    all_amount_tv.setEnabled(false);
                    all_amount_tv.setTextColor(Color.GRAY);
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                UIHelper.dismissPd();
            }
        });
    }


    //获取提现手续费计算方式
    private String function_name;//js函数名称
    private String function;//js函数体
    private void getFee(){
        HashMap feemap=new HashMap();
        feemap.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(ApplyCrashActivity.this).StringRequestPostVolley(URLs.GET_FEE, EncryptUtil.encrypt(feemap), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),ApplyCrashActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("ok")){
                        JSONObject dataObj=obj.optJSONObject("data");
                        StringBuilder rule_description=new StringBuilder();
                        JSONArray ruleArr=dataObj.optJSONArray("rule_description");
                        for(int i=0;i<ruleArr.length();i++){
                            rule_description.append(ruleArr.getString(i));
                            rule_description.append("\n");
                        }

                        function_name=dataObj.optString("function_name");
                        function=dataObj.optString("function");

                        poundage_tv.setText(rule_description+"");
                        poundage_tv.setTextColor(Color.rgb(0,166,121));

                    }else{
                        if(obj.has("show_msg")){
                            poundage_tv.setText(obj.getString("show_msg"));
                            poundage_tv.setTextColor(Color.RED);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }

}
