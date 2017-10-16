package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.MyExpandableAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.AccountModel;
import com.example.youhe.youhecheguanjiaplus.bean.ChildModel;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.VerificationCodeDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class AccountQueryActivity extends Activity implements View.OnClickListener{

    private ImageView account_query_back_img;
    private ImageButton add_account_query_ib;
    private TextView noaccount_to_regist_tv;
    private ExpandableListView account_query_elv;
    private MyExpandableAdapter myExpandableAdapter;
    private List<AccountModel> accountList;
    private LinkedList<List<ChildModel>> childModelList;
    int tempPosition=0;

    public String captchaCode="";//验证码

    private LinearLayout emty_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_query);
        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,AccountQueryActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AccountQueryActivity.this);

        childModelList=new LinkedList<List<ChildModel>>();

        initViews();//初始化控件
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAccountList();//获取账号列表

        Log.i("TAG","AccountQueryActivity is onResume!!!");
    }

    private void initViews(){

        emty_layout= (LinearLayout) findViewById(R.id.emty_layout);

        account_query_back_img= (ImageView) findViewById(R.id.account_query_back_img);
        account_query_back_img.setOnClickListener(this);

        add_account_query_ib= (ImageButton) findViewById(R.id.add_account_query_ib);
        add_account_query_ib.setOnClickListener(this);

        noaccount_to_regist_tv= (TextView) findViewById(R.id.noaccount_to_regist_tv);
        noaccount_to_regist_tv.setOnClickListener(this);

        account_query_elv= (ExpandableListView) findViewById(R.id.account_query_elv);
        account_query_elv.setGroupIndicator(null);
        accountList=new ArrayList<AccountModel>();
        myExpandableAdapter=new MyExpandableAdapter(AccountQueryActivity.this,accountList,childModelList);
        account_query_elv.setAdapter(myExpandableAdapter);

        account_query_elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {

                boolean expanded = expandableListView.isGroupExpanded(groupPosition);
                if(!expanded){
                    if(childModelList.get(groupPosition).size()>0) {//集合里有数据不去请求网络
                        account_query_elv.expandGroup(groupPosition);
                        return true;
                    }else{//请求网络数据
                        //加载数据
                        getAccountCarList(accountList.get(groupPosition).getVehicleAccount(), accountList.get(groupPosition).getUserType(),groupPosition);//获取账号车辆列表
                        return true;
                    }
                }
//                Toast.makeText(AccountQueryActivity.this, "dd"+groupPosition,Toast.LENGTH_SHORT).show();
                view.setBackgroundColor(Color.WHITE);
                view.invalidate();
                view.postInvalidate();

                return false;
            }
        });
    }


    Intent intent=null;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.account_query_back_img:
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;

            case R.id.add_account_query_ib:
                intent=new Intent(AccountQueryActivity.this,AddAcountActivity.class);
                startActivity(intent);
                break;

            case R.id.noaccount_to_regist_tv:
                intent=new Intent(AccountQueryActivity.this,CarOwnerTypeActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }


    HashMap map;
    /**
     * 获取客户122帐号车辆列表
     * */
    public void getAccountList(){

        UIHelper.showPd(this);

        accountList.clear();
        childModelList.clear();
        myExpandableAdapter=new MyExpandableAdapter(AccountQueryActivity.this,accountList,childModelList);
        myExpandableAdapter.notifyDataSetChanged();
        account_query_elv.setAdapter(myExpandableAdapter);

        map=new HashMap();
        map.put("token", TokenSQLUtils.check());

        VolleyUtil.getVolleyUtil(AccountQueryActivity.this).StringRequestPostVolley(URLs.GET_ACCOUNT_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                String json= EncryptUtil.decryptJson(jsonObject.toString(),AccountQueryActivity.this);
                parseAccountJson(json);

                UIHelper.dismissPd();
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }

    private void parseAccountJson(String json){
        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");
            JSONArray accountListArr=dataObj.getJSONArray("accountList");
            AccountModel accountModel;
            for(int i=0;i<accountListArr.length();i++){
                accountModel=new AccountModel();
                accountModel.setVehicleAccount(accountListArr.getJSONObject(i).optString("vehicleAccount"));
                accountModel.setUserType(accountListArr.getJSONObject(i).optString("userType"));
                accountModel.setProvinceName(accountListArr.getJSONObject(i).optString("ProvinceName"));
                accountList.add(accountModel);
            }

            //初始化数据
            for (int i = 0; i < accountList.size(); i++) {
                childModelList.add(new LinkedList<ChildModel>());
            }

            myExpandableAdapter=new MyExpandableAdapter(AccountQueryActivity.this,accountList,childModelList);
            myExpandableAdapter.notifyDataSetChanged();
            account_query_elv.setAdapter(myExpandableAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            if(!accountList.isEmpty()){
                emty_layout.setVisibility(View.GONE);
            }else{
                emty_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 122车辆车辆列表
     */
    private void getAccountCarList(final String vehicleAccount, final String userType, final int groupPosition){
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);

        VolleyUtil.getVolleyUtil(AccountQueryActivity.this).StringRequestPostVolley(URLs.GET_ACCOUNT_CAR_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                String json= EncryptUtil.decryptJson(jsonObject.toString(),AccountQueryActivity.this);
                paresCarListJson(json,userType,vehicleAccount,groupPosition);
            }

            @Override
            public void ResponError(VolleyError volleyError) {

            }
        });
    }

    VerificationCodeDialog verificationCodeDialog;
    private void paresCarListJson(String json, final String userType, final String vehicleAccount, final int groupPosition){
        try {
            JSONObject obj=new JSONObject(json);
            int code=obj.getInt("code");
            String status=obj.optString("status");
            if (code==90010){
                verificationCodeDialog=new VerificationCodeDialog(AccountQueryActivity.this, R.style.Dialog,userType);
                verificationCodeDialog.listener=new VerificationCodeDialog.VerificationCodeDialogListener() {
                    @Override
                    public void onSure(String input) {
                        captchaCode=input;
                        authAccountLogin(vehicleAccount,userType,groupPosition);
                    }
                };
                verificationCodeDialog.showDialog();
            }else if(status.equals("ok")){
//                authAccountLogin(vehicleAccount,userType);
                JSONObject dataObj=obj.optJSONObject("data");
                JSONArray carlistArr=dataObj.optJSONArray("carlist");
                ChildModel childModel;
                LinkedList<ChildModel> childModels=new LinkedList<ChildModel>();
                for(int i=0;i<carlistArr.length();i++){
                    childModel=new ChildModel();
                    childModel.setCarEngineNo(carlistArr.getJSONObject(i).optString("cardrivenumber"));//发动机号
                    childModel.setCarnum(carlistArr.getJSONObject(i).optString("carnumber"));//车牌号
                    childModel.setCarVin(carlistArr.getJSONObject(i).optString("carcode"));//车身架号
                    childModel.setIscommit(carlistArr.getJSONObject(i).optInt("iscommit"));//是否可提交
                    childModel.setId(carlistArr.getJSONObject(i).optString("id"));//车辆id
                    childModel.setProprefix(carlistArr.getJSONObject(i).optString("proprefix"));//车辆前缀

                    childModels.add(childModel);
                }

                childModelList.set(groupPosition,childModels);
                if(myExpandableAdapter==null){
                    myExpandableAdapter=new MyExpandableAdapter(AccountQueryActivity.this,accountList,childModelList);
                    account_query_elv.setAdapter(myExpandableAdapter);
                }else{
                    myExpandableAdapter.notifyDataSetChanged();
                }
                account_query_elv.expandGroup(groupPosition);
            }else if(status.equals("fail")){
                String show_msg=obj.optString("show_msg");
                Toast.makeText(AccountQueryActivity.this,show_msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
             if(accountList.isEmpty()){
                 emty_layout.setVisibility(View.VISIBLE);
                 account_query_elv.setVisibility(View.GONE);
             }else{
                 emty_layout.setVisibility(View.GONE);
                 account_query_elv.setVisibility(View.VISIBLE);
             }
        }
    }


    /**
     * 自动登录账号
     * */
    private void authAccountLogin(final String vehicleAccount, final String userType, final int groupPosition){
        UIHelper.showPd(AccountQueryActivity.this);
        map=new HashMap();
        map.put("token", TokenSQLUtils.check());
        map.put("vehicleAccount",vehicleAccount);
        map.put("captchaCode",captchaCode);
        VolleyUtil.getVolleyUtil(AccountQueryActivity.this).StringRequestPostVolley(URLs.AUTH_ACCOUNT_LOGIN, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),AccountQueryActivity.this));
                    String status=obj.optString("status");
                    if(status.equals("fail")){
                        String show_msg=obj.optString("show_msg");
                        Toast.makeText(AccountQueryActivity.this,show_msg+"",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("ok")) {
                        getAccountCarList(vehicleAccount,userType,groupPosition);
                    }

                    if (verificationCodeDialog != null) {
                        verificationCodeDialog.dismiss();
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
