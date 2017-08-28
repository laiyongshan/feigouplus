package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.BankCardAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.BankCard;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class MyBankCardActivity extends Activity implements View.OnClickListener{

    private ImageButton my_bankcard_back_ib,add_my_bankcard_ib;

    private ListView bankcard_lv;

    private LinearLayout emty_layout;

    private List<BankCard> bank_list=new ArrayList<BankCard>();//银行卡列表

    private BankCardAdapter bankCardAdapter ;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bankcard);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, MyBankCardActivity.this);
        }
        SystemBarUtil.useSystemBarTint(MyBankCardActivity.this);

        initView();//初始化控件

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        bankCardAdapter=new BankCardAdapter(MyBankCardActivity.this,bank_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        pd.show();
        bank_list.clear();
        bankCardAdapter.notifyDataSetChanged();
        getBankList();//获取银行卡列表
    }


    @Override
    protected void onStop() {
        super.onStop();
        pd.dismiss();
    }

    private void initView(){
        my_bankcard_back_ib= (ImageButton) findViewById(R.id.my_bankcard_back_ib);
        my_bankcard_back_ib.setOnClickListener(this);
        add_my_bankcard_ib= (ImageButton) findViewById(R.id.add_my_bankcard_ib);
        add_my_bankcard_ib.setOnClickListener(this);

        bankcard_lv= (ListView) findViewById(R.id.bankcard_lv);

//        bankCardAdapter=new BankCardAdapter(MyBankCardActivity.this,bank_list);
//
//        bankcard_lv.setAdapter(bankCardAdapter);

        emty_layout= (LinearLayout) findViewById(R.id.emty_layout);
//        emty_layout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.my_bankcard_back_ib:
                finish();
                break;

            case R.id.add_my_bankcard_ib:
                Intent intent=new Intent(MyBankCardActivity.this,AddMyBankCarActivity.class);
                startActivity(intent);
                break;
        }
    }


    HashMap<String, Object> map;
    private HashMap getParams(){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            map.put("token", token);
        }

        return map ;
    }


    //获取银行卡列表
    private void getBankList(){

        VolleyUtil.getVolleyUtil(MyBankCardActivity.this).StringRequestPostVolley(URLs.GET_BANK_LIST, EncryptUtil.encrypt(getParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                    parseJson(EncryptUtil.decryptJson(jsonObject.toString(),MyBankCardActivity.this));//解析解密之后的数据
                    pd.dismiss();
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }


    BankCard bankCard;
    private void parseJson(String json){
        try {
            JSONObject obj=new JSONObject(json);
            String status=obj.getString("status");
            if(status.equals("ok")){
                JSONObject dataObj=obj.getJSONObject("data");
                JSONArray bankArr=dataObj.getJSONArray("bank_list");

                if(bankArr==null||(bankArr.length()==0)){
                    bankcard_lv.setVisibility(View.GONE);
                    emty_layout.setVisibility(View.VISIBLE);

                }else {

                    for (int i = 0; i < bankArr.length(); i++) {
                        bankCard = new BankCard();
                        bankCard.setBank_name(bankArr.getJSONObject(i).getString("bank_name"));
                        bankCard.setBank_address(bankArr.getJSONObject(i).getString("bank_address"));
                        bankCard.setBank_code(bankArr.getJSONObject(i).getString("bank_code"));
//                        bankCard.setCreatetimestr(bankArr.getJSONObject(i).getString("createtimestr"));
                        bankCard.setBankid(bankArr.getJSONObject(i).getString("bankid"));

                        bank_list.add(bankCard);
                    }

                    bankcard_lv.setVisibility(View.VISIBLE);
                    emty_layout.setVisibility(View.GONE);

                    bankCardAdapter = new BankCardAdapter(MyBankCardActivity.this, bank_list);

                    bankcard_lv.setAdapter(bankCardAdapter);

                    bankCardAdapter.notifyDataSetChanged();

                    bankcard_lv.setOnItemClickListener(new MyOnItemClickListener(bank_list));

                }
            }else{

            }

        } catch (JSONException e) {
            bankcard_lv.setVisibility(View.GONE);
            emty_layout.setVisibility(View.VISIBLE);

            e.printStackTrace();
        }finally {
            pd.dismiss();
        }
    }


    Intent intent;
    class MyOnItemClickListener  implements AdapterView.OnItemClickListener{

        List<BankCard> bankCards;
        public MyOnItemClickListener(List<BankCard> bankCards){
            this.bankCards=bankCards;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            intent=new Intent();

            intent.putExtra("bank_name",bankCards.get(i).getBank_name());
            intent.putExtra("bank_code",bankCards.get(i).getBank_code());
            intent.putExtra("bank_id",bankCards.get(i).getBankid());

            setResult(8001,intent);
            finish();
        }
    }

}
