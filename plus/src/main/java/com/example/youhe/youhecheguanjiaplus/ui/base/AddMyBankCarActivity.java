package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.util.BDFileUtils;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class AddMyBankCarActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_CAMERA = 0x001;
    private ClearEditText bankcar_owner_phonenum_et, bankcar_address_et, bankcard_code_et, bank_name_et, et_auth_code;
    private ImageButton add_my_bankcard_back_ib;
    private Button add_bankcard_btn;
    private TimeButton timeButton;//获取验证码

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_bankcard);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, AddMyBankCarActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddMyBankCarActivity.this);

        initView();

        timeButton(savedInstanceState);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
    }

    /**
     * 获取验证码
     *
     * @param savedInstanceState
     */
    private void timeButton(Bundle savedInstanceState) {

        //获取验证码键
        timeButton = (TimeButton) findViewById(R.id.auth_code_btn);
        timeButton.onCreate(savedInstanceState);
        timeButton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(30 * 1000);
        timeButton.setOnClickListener(this);
    }

    public void addBankQR(View view) {
        Intent intent = new Intent(AddMyBankCarActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, BDFileUtils.getSaveFile("QR", "temp.jpg").getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_BANK_CARD);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取调用参数
        String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
        final String filePath = BDFileUtils.getSaveFile("QR", "temp.jpg").getAbsolutePath();
        if (pd != null)
            pd.show();
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(AddMyBankCarActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                BankCardParams params = new BankCardParams();
                params.setImageFile(new File(filePath));
                OCR.getInstance().recognizeBankCard(params, new OnResultListener<BankCardResult>() {
                    @Override
                    public void onResult(BankCardResult bankCardResult) {
                        Log.d("chujun", "ID" + bankCardResult.getJsonRes());
                        parseOR(bankCardResult);
                        if (pd != null)
                            pd.dismiss();
                    }

                    @Override
                    public void onError(OCRError error) {
                        if (pd != null)
                            pd.dismiss();
                        ToastUtil.getShortToastByString(AddMyBankCarActivity.this, "识别银行卡失败");
                    }
                });
            }
        }
        BDFileUtils.deleteFile(AddMyBankCarActivity.this, filePath, true);
    }

    private void parseOR(BankCardResult bankCardResult) {
        try {
            JSONObject jsonObject = new JSONObject(bankCardResult.getJsonRes());
            JSONObject resJson = jsonObject.getJSONObject("result");
            if (resJson.has("bank_card_number")) {
                String bank_card_number = resJson.getString("bank_card_number");
                if (!TextUtils.isEmpty(bank_card_number))
                    bankcard_code_et.setText(bank_card_number);
            }
            if (resJson.has("bank_name")) {
                String bank_name = resJson.getString("bank_name");
                if (!TextUtils.isEmpty(bank_name))
                    bank_name_et.setText(bank_name);
            }
            if (resJson.has("bank_card_type")) {
                String bank_card_type = resJson.getString("bank_card_type");
            }

        } catch (Exception e) {
            ToastUtil.getShortToastByString(AddMyBankCarActivity.this, "识别银行卡失败");
            e.printStackTrace();
        }
    }

    private void initView() {
        bankcar_owner_phonenum_et = (ClearEditText) findViewById(R.id.bankcar_owner_phonenum_et);
//        bankcard_idcard_num_et= (ClearEditText) findViewById(R.id.bankcard_idcard_num_et);
        bankcar_address_et = (ClearEditText) findViewById(R.id.bankcar_address_et);
        bankcard_code_et = (ClearEditText) findViewById(R.id.bankcard_code_et);
        bank_name_et = (ClearEditText) findViewById(R.id.bank_name_et);
        et_auth_code = (ClearEditText) findViewById(R.id.et_auth_code);

        add_my_bankcard_back_ib = (ImageButton) findViewById(R.id.add_my_bankcard_back_ib);
        add_my_bankcard_back_ib.setOnClickListener(this);

        add_bankcard_btn = (Button) findViewById(R.id.add_bankcard_btn);
        add_bankcard_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_my_bankcard_back_ib:
                finish();
                break;

            case R.id.add_bankcard_btn:
                if (bankcar_owner_phonenum_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMyBankCarActivity.this, "银行卡预留手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else if (bankcar_address_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMyBankCarActivity.this, "开户支行不能为空！", Toast.LENGTH_SHORT).show();
                } else if (bankcard_code_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMyBankCarActivity.this, "银行卡号不能为空！", Toast.LENGTH_SHORT).show();
                } else if (bank_name_et.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMyBankCarActivity.this, "银行名称不能为空！", Toast.LENGTH_SHORT).show();
                } else if (et_auth_code.getText().toString().trim().equals("")) {
                    Toast.makeText(AddMyBankCarActivity.this, "请输入手机验证码！", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    addBankCard();//添加银行卡
                }
                break;

            case R.id.auth_code_btn:
                getetVerifycode();//获取验证码
                break;
        }
    }


    HashMap phonePams;

    public HashMap getVerifycodeParams() {
        String phoneNumber = bankcar_owner_phonenum_et.getText().toString();
        phonePams = new HashMap<>();
        String token = TokenSQLUtils.check();
        if (token != null) {
            phonePams.put("token", token);
        }
        phonePams.put("bank_mobile", phoneNumber);

        return phonePams;
    }

    /**
     * 获取验证码
     */

    public void getetVerifycode() {
        if (MainActivity.getHtts() == false) {
            Toast.makeText(this, "网络连接失败，请检测设置", Toast.LENGTH_LONG).show();
            return;
        }

        VolleyUtil.getVolleyUtil(AddMyBankCarActivity.this).StringRequestPostVolley(URLs.SEND_BANK_VERIFY_CODE, EncryptUtil.encrypt(getVerifycodeParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                EncryptUtil.decryptJson(jsonObject.toString(), AddMyBankCarActivity.this);
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(AddMyBankCarActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    //添加银行卡
    public void addBankCard() {

        VolleyUtil.getVolleyUtil(AddMyBankCarActivity.this).StringRequestPostVolley(URLs.ADD_BANK_CARD, EncryptUtil.encrypt(getAddBankParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                parseJson(EncryptUtil.decryptJson(jsonObject.toString(), AddMyBankCarActivity.this));//解析解密之后的数据
                pd.dismiss();
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });
    }


    HashMap<String, Object> map;

    //获取添加银行参数
    public HashMap getAddBankParams() {
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if (token != null) {
            map.put("token", token);
        }

        map.put("bank_name", bank_name_et.getText().toString().trim());
        map.put("bank_code", bankcard_code_et.getText().toString().trim());
        map.put("bank_address", bankcar_address_et.getText().toString().trim());
        map.put("bank_mobile", bankcar_owner_phonenum_et.getText().toString().trim());
        map.put("verifycode", et_auth_code.getText().toString().trim());

        return map;
    }


    //解析添加银行卡信息
    private void parseJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String status = obj.getString("status");
            if (obj.has("code")) {
                int code = obj.getInt("code");
                UIHelper.showErrTips(code, AddMyBankCarActivity.this);
            }
            if (status.equals("ok")) {
                Toast.makeText(AddMyBankCarActivity.this, "添加银行卡成功", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(AddMyBankCarActivity.this, "添加银行卡失败，请重试", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
