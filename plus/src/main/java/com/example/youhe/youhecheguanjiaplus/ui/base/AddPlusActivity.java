package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.databinding.ActivityAddPlusBinding;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.FileUtils;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 添加车主卡
 * Created by Administrator on 2017/7/7 0007.
 */

public class AddPlusActivity extends YeoheActivity implements View.OnClickListener {

    ActivityAddPlusBinding b;
    private int selectType = 0;//选择模式 默认为普通  1为三级分销
    private UIDialog uiDialog = null;
    private VolleyUtil volleyUtil = null;
    //    private int isFree = 1;//开通车主卡是否免费 1免费2收费
    private int cardFree = 0;//生成每张费用
    private int rate1 = 0;
    private int rate2 = 0;
    private int rate3 = 0;
    private String maxExpireTime = "";
    //    private String uploadMaxExpireTime = "";
    private int maxNumber = 0;
    private int maxPrice = 0;
    private int mixPrice = 0;
    private int singlePrice = 0;
    private int isFree = 0;//是否允许免费   0不允许1允许
    private RoleBean ordinaryRole = null;
    private RoleBean distributionRole = null;
    private int count = 0;

    //    {"status":2,"ordercode":"20170720114921"}
//2:支付  1：添加成功
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_add_plus);

// 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, AddPlusActivity.this);
        }
        SystemBarUtil.useSystemBarTint(AddPlusActivity.this);

    }

    @Override
    public void init() {
//        b.seekScale1.setFocusableInTouchMode(false);
//        b.seekScale2.setFocusableInTouchMode(false);
//        b.seekScale3.setFocusableInTouchMode(false);
//        b.seekScale1.setPressed(false);
//        b.seekScale2.setFocusable(false);
        b.edScale1.setEnabled(false);
        b.edScale2.setEnabled(false);
        b.edScale3.setEnabled(false);

//        if (isWrite())
//            loadDate();
        b.myassetsBackImg.setOnClickListener(this);
        scaleValue1 = b.seekScale1.getProgress();
        scaleValue2 = b.seekScale2.getProgress();
        scaleValue3 = b.seekScale3.getProgress();

        event();
        uiDialog = new UIDialog(this, "加载中...");
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求

        loadRule();

        b.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }



    private void submit() {
        if (StringUtils.isEmpty(b.edCount.getText().toString()) || b.edCount.getText().toString().equals("0")) {
            Toast.makeText(AddPlusActivity.this, "请填写要添加张数", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(b.edCount.getText().toString()) > maxNumber) {
            Toast.makeText(AddPlusActivity.this, "最多可添加" + maxNumber + "张车主卡", Toast.LENGTH_SHORT).show();
            return;
        }

        if (b.radioNo.isChecked() && StringUtils.isEmpty(b.edSinglePrice.getText().toString()) || b.edSinglePrice.getText().toString().equals("0")) {
            Toast.makeText(AddPlusActivity.this, "请输入单张卡费用", Toast.LENGTH_SHORT).show();
            return;
        }
        if (b.radioNo.isChecked() && (Integer.parseInt(b.edSinglePrice.getText().toString()) > maxPrice || Integer.parseInt(b.edSinglePrice.getText().toString()) < mixPrice)) {
            Toast.makeText(AddPlusActivity.this, "单张卡费用最大为" + maxPrice + ",最小为" + mixPrice, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        String token = TokenSQLUtils.check();
        hashMap.put("token", token);
        hashMap.put("number", b.edCount.getText().toString().trim());//车主卡数量
//        hashMap.put("type", b.radioYes.isChecked() ? "0" : "1");//车主卡注册是否收费 0免费1收费
        hashMap.put("price", b.radioNo.isChecked() ? b.edSinglePrice.getText().toString().trim() : "0");//车主卡单价

        hashMap.put("client_type", b.spinner.getSelectedItemPosition() == 0 ? "3" : "2");//2分销用户3普通用户
        hashMap.put("totalprice", Integer.parseInt(b.edCount.getText().toString()) * cardFree + "");


        volleyUtil.postRequest(this, URLs.PLUS_ADD, hashMap, "添加失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                Toast.makeText(AddPlusActivity.this, "生成卡片成功", Toast.LENGTH_SHORT).show();
                try {
                    int status = dataObject.getInt("status");//1成功2待付款
                    if (status == 1) {
                        volleyUtil.cancelAllQueue(AddPlusActivity.this);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String ordercode = "";//订单号 status为2时才有
                        String paymoney = "";//支付金额
                        int order_type = 3;//订单类型   1违章2年检3车主卡
                        int mjOpenType = 3;//茂捷二维码支付支持的支付类型  目前：paytype=3会返 1:支持微信支付   2：支持支付宝支付 3：全部
                        if (dataObject.has("ordercode"))
                            ordercode = dataObject.getString("ordercode");
                        if (dataObject.has("paymoney"))
                            paymoney = dataObject.getString("paymoney");
                        if (dataObject.has("order_type"))
                            order_type = dataObject.getInt("order_type");
                        if (dataObject.has("mjOpenType"))
                            mjOpenType=dataObject.getInt("mjOpenType");

                        if (StringUtils.isEmpty(ordercode)){
                            Toast.makeText(AddPlusActivity.this, "获取订单号失败", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (StringUtils.isEmpty(paymoney)){
                            Toast.makeText(AddPlusActivity.this, "获取支付金额失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent=new Intent(AddPlusActivity.this,ScanQrPayActivity.class);
                        intent.putExtra(ScanQrPayActivity.EXTRA_STRING_ORDER_CODE,ordercode);
                        intent.putExtra(ScanQrPayActivity.EXTRA_STRING_PAY_MONEY,paymoney);
                        intent.putExtra(ScanQrPayActivity.EXTRA_INT_ORDER_TYPE,order_type);
                        intent.putExtra(ScanQrPayActivity.EXTRA_INT_MJ_OPEN_TYPE,mjOpenType);

                        intent.putExtra(ScanQrPayActivity.EXTRA_RETURN_CLASS,"com.example.youhe.youhecheguanjiaplus.ui.base.MainActivity");
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddPlusActivity.this, "生成卡片失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                if (StringUtils.isEmpty(msg))
                    Toast.makeText(AddPlusActivity.this, "生成卡片失败", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AddPlusActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 加载添加车主卡规则
     */
    private void loadRule() {
        uiDialog.show();
        HashMap<String, String> hashMap = new HashMap<>();
        String token = TokenSQLUtils.check();
        hashMap.put("token", token);

        volleyUtil.postRequest(this, URLs.PLUS_ADD_RULE, hashMap, "获取添加规则失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                Log.d("TAG", resultStr);
                uiDialog.hide();
                try {

                    if (dataObject.has("ordinary"))
                        ordinaryRole = new Gson().fromJson(dataObject.getString("ordinary"), RoleBean.class);
                    if (dataObject.has("distribution"))
                        distributionRole = new Gson().fromJson(dataObject.getString("distribution"), RoleBean.class);

                    if (dataObject.has("card_fee"))
                        cardFree = dataObject.getInt("card_fee");
                    if (dataObject.has("max_number"))
                        maxNumber = dataObject.getInt("max_number");
                    if (dataObject.has("max_expire_time"))
                        maxExpireTime = dataObject.getString("max_expire_time");
                    if (distributionRole != null) {
                        rate1 = (int) (distributionRole.getDistribution_rate_1() * 100);
                        rate2 = (int) (distributionRole.getDistribution_rate_2() * 100);
                        rate3 = (int) (distributionRole.getDistribution_rate_3() * 100);
                    }
                    if (ordinaryRole.getMin_price()==0)
                        ordinaryRole.setIs_free(1);
                    else
                        ordinaryRole.setIs_free(0);
                    if (distributionRole.getMin_price()==0)
                        distributionRole.setIs_free(1);
                    else
                        distributionRole.setIs_free(0);

                    changViewByRole();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddPlusActivity.this, "获取添加规则失败", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(0, 1500);
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                Toast.makeText(AddPlusActivity.this, msg, Toast.LENGTH_SHORT).show();
                uiDialog.hide();
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                finish();
            }
        }
    };

    /**
     * 根据后台下发的规则改变页面
     */
    private void changViewByRole() {
        if (cardFree <= 0) {
            b.layoutPayPrice.setVisibility(View.GONE);
        } else {
            b.layoutPayPrice.setVisibility(View.VISIBLE);
//            b.tvPrice.setText("¥"+cardFree+"");
            if (!StringUtils.isEmpty(b.edCount.getText().toString())) {
                b.tvPrice.setText("¥" + Integer.parseInt(b.edCount.getText().toString()) * cardFree + "");
            }
        }
        b.tvDate.setText(maxExpireTime);
        if (maxNumber != 0)
            b.tvAlertMaxCount.setText("" + maxNumber + "张");

        if (distributionRole != null) {
            b.seekScale1.setProgress(rate1);
            b.seekScale2.setProgress(rate2);
            b.seekScale3.setProgress(rate3);
            b.edScale1.setText(rate1 + "");
            b.edScale2.setText(rate2 + "");
            b.edScale3.setText(rate3 + "");
        }
        spinnerChange();

    }

    private void spinnerChange() {
        if (b.spinner.getSelectedItemPosition() == 0) {//普通车主卡
            if (ordinaryRole != null) {
                if (ordinaryRole.getMax_price() > 0)
                    b.tvAlertMaxPrice.setText("¥" + ordinaryRole.getMax_price() + "");
                if (ordinaryRole.getMin_price() >= 0)
                    b.tvAlertMixPrice.setText("¥" + ordinaryRole.getMin_price() + "");
                maxPrice = ordinaryRole.getMax_price();
                mixPrice = ordinaryRole.getMin_price();
                isFree = ordinaryRole.getIs_free();

            }
        } else {
            if (distributionRole != null) {
                if (distributionRole.getMax_price() > 0)
                    b.tvAlertMaxPrice.setText("¥" + distributionRole.getMax_price() + "");
                if (distributionRole.getMin_price() >= 0)
                    b.tvAlertMixPrice.setText("¥" + distributionRole.getMin_price() + "");

                maxPrice = distributionRole.getMax_price();
                mixPrice = distributionRole.getMin_price();
                isFree = distributionRole.getIs_free();
            }
        }
        if (isFree == 0) {//不允许免费
            b.radioYes.setVisibility(View.INVISIBLE);
            b.radioNo.setChecked(true);
        } else {
            b.radioYes.setVisibility(View.VISIBLE);
        }
    }

    private boolean isWrite() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadDate() {
        try {
            String str = FileUtils.read(this, "addPlus");
            if (StringUtils.isEmpty(str))
                return;

            JSONObject jsonObject = new JSONObject(str);

            if (!StringUtils.isEmpty(jsonObject.getString("addCount")))
                b.edCount.setText(jsonObject.getString("addCount"));
            if (jsonObject.getBoolean("isFree"))
                b.radioYes.setChecked(true);
            else
                b.radioNo.setChecked(true);
            if (!StringUtils.isEmpty(jsonObject.getString("singlePrice")))
                b.edSinglePrice.setText(jsonObject.getString("singlePrice"));
            if (!StringUtils.isEmpty(jsonObject.getString("date")))
                b.tvDate.setText(jsonObject.getString("date"));

            b.spinner.setSelection(jsonObject.getInt("type"));
            if (jsonObject.getInt("type") == 1) {
                scaleValue1 = jsonObject.getInt("scaleValue1");
                scaleValue2 = jsonObject.getInt("scaleValue2");
                scaleValue3 = jsonObject.getInt("scaleValue3");
                b.seekScale1.setProgress(scaleValue1);
                b.seekScale2.setProgress(scaleValue2);
                b.seekScale3.setProgress(scaleValue3);
                b.edScale1.setText(scaleValue1 + "");
                b.edScale2.setText(scaleValue2 + "");
                b.edScale3.setText(scaleValue3 + "");
            }
//            if (!StringUtils.isEmpty(jsonObject.getString("payMoney")))
//                b.tvPrice.setText(jsonObject.getString("payMoney"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addCount", b.edCount.getText().toString().trim()); //张数
            jsonObject.put("isFree", b.radioYes.isChecked());
            jsonObject.put("singlePrice", b.edSinglePrice.getText().toString().trim());
            jsonObject.put("date", b.tvDate.getText().toString().trim());
            jsonObject.put("type", selectType);
            jsonObject.put("scaleValue1", scaleValue1);
            jsonObject.put("scaleValue2", scaleValue2);
            jsonObject.put("scaleValue3", scaleValue3);
//            jsonObject.put("payMoney", );
            FileUtils.write(this, "addPlus", jsonObject.toString());
//            jsonObject.put("isFree",)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (isWrite())
//            saveDate();
    }

    private int scaleValue1 = 0;
    private int scaleValue2 = 0;
    private int scaleValue3 = 0;

    private void event() {

        b.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectType = i;
                if (selectType == 0) {
                    b.layoutScale.setVisibility(View.GONE);
                } else
                    b.layoutScale.setVisibility(View.VISIBLE);
                spinnerChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        b.seekScale1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean a) {
//                if ((b.seekScale2.getProgress() + b.seekScale3.getProgress() + seekBar.getProgress()) <= 100) {
//                    b.edScale1.setText("" + seekBar.getProgress() + "");
//                    b.seekScale1.setProgress(seekBar.getProgress());
//                    scaleValue1 = seekBar.getProgress();
//                } else {
//                    b.seekScale1.setProgress(scaleValue1);
//                }
                b.seekScale1.setProgress(rate1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        b.seekScale2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean a) {
//                if ((b.seekScale1.getProgress() + b.seekScale3.getProgress() + seekBar.getProgress()) <= 100) {
//                    b.edScale2.setText("" + seekBar.getProgress() + "");
//                    b.seekScale2.setProgress(seekBar.getProgress());
//                    scaleValue2 = seekBar.getProgress();
//                } else {
//                    b.seekScale2.setProgress(scaleValue2);
//                }
                b.seekScale2.setProgress(rate2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        b.seekScale3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean a) {
//                if ((b.seekScale2.getProgress() + b.seekScale1.getProgress() + seekBar.getProgress()) <= 100) {
//                    b.edScale3.setText("" + seekBar.getProgress() + "");
//                    b.seekScale3.setProgress(seekBar.getProgress());
//                    scaleValue3 = seekBar.getProgress();
//                } else {
//                    b.seekScale3.setProgress(scaleValue3);
//                }
                b.seekScale3.setProgress(rate3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        b.edScale1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (b.edScale1.hasFocus()) {
                    try {
//                    if (!isScaleMax100()) {
                        if ((Integer.parseInt(charSequence.toString()) + scaleValue2 + scaleValue3) > 100) {
                            b.edScale1.setText("" + scaleValue1 + "");
                        } else {
                            if (Integer.parseInt(charSequence.toString()) > 100) {
                                b.seekScale1.setProgress(100);
                                b.edScale1.setText("100");
                            } else if (Integer.parseInt(charSequence.toString()) < 0) {
                                b.seekScale1.setProgress(0);
                                b.edScale1.setText("0");
                            } else {
                                b.seekScale1.setProgress(Integer.parseInt(charSequence.toString()));
                            }
                            scaleValue1 = Integer.parseInt(charSequence.toString());
                        }
//                    }else {
//                        b.edScale1.setText(""+scaleValue1+"");
//                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.edScale2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (b.edScale2.hasFocus()) {
                    try {
                        if ((Integer.parseInt(charSequence.toString()) + scaleValue2 + scaleValue3) > 100) {
                            b.edScale2.setText("" + scaleValue2 + "");
                        } else {
                            if (Integer.parseInt(charSequence.toString()) > 100) {
                                b.seekScale2.setProgress(100);
                                b.edScale2.setText("100");
                            } else if (Integer.parseInt(charSequence.toString()) < 0) {
                                b.seekScale1.setProgress(0);
                                b.edScale2.setText("0");
                            } else {
                                b.seekScale2.setProgress(Integer.parseInt(charSequence.toString()));
                            }
                            scaleValue2 = Integer.parseInt(charSequence.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.edScale3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (b.edScale3.hasFocus()) {
                    try {
                        if ((Integer.parseInt(charSequence.toString()) + scaleValue2 + scaleValue1) > 100) {
                            b.edScale3.setText("" + scaleValue3 + "");
                        } else {
                            if (Integer.parseInt(charSequence.toString()) > 100) {
                                b.seekScale3.setProgress(100);
                                b.edScale3.setText("100");
                            } else if (Integer.parseInt(charSequence.toString()) < 0) {
                                b.seekScale3.setProgress(0);
                                b.edScale3.setText("0");
                            } else {
                                b.seekScale3.setProgress(Integer.parseInt(charSequence.toString()));
                            }
                            scaleValue3 = Integer.parseInt(charSequence.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        b.edCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (b.edCount.hasFocus()) {
                    if (!StringUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals("0")) {
                        if (Integer.parseInt(charSequence.toString()) > maxNumber) {
                            b.edCount.setText("" + count + "");
                            b.tvPrice.setText("¥" + count * cardFree + "");
                        } else {
                            count = Integer.parseInt(charSequence.toString());
                            b.tvPrice.setText("¥" + Integer.parseInt(charSequence.toString()) * cardFree + "");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.edSinglePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (b.edSinglePrice.hasFocus()) {
                    if (!StringUtils.isEmpty(charSequence.toString())) {
//                        if ((Integer.parseInt(charSequence.toString()) > maxPrice) || (Integer.parseInt(charSequence.toString()) < mixPrice)) {
                        if ((Integer.parseInt(charSequence.toString()) > maxPrice)) {
                            b.edSinglePrice.setText("" + singlePrice + "");

                        } else {
                            singlePrice = Integer.parseInt(charSequence.toString());
                        }
                    }else
                        singlePrice=0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private boolean isScaleMax100() {
        return (scaleValue1 + scaleValue2 + scaleValue3) > 100;
    }

    @Override
    public void refresh(Object... param) {

    }

    class RoleBean implements Serializable {
        private int max_price;//最大价格
        private int min_price;//最小价格
        private int is_free;//是否允许免费 0不允许1允许

        private double distribution_rate_1;//三级分销推广费率
        private double distribution_rate_2;
        private double distribution_rate_3;

        public int getMax_price() {
            return max_price;
        }

        public void setMax_price(int max_price) {
            this.max_price = max_price;
        }

        public int getMin_price() {
            return min_price;
        }

        public void setMin_price(int min_price) {
            this.min_price = min_price;
        }

        public int getIs_free() {
            return is_free;
        }

        public void setIs_free(int is_free) {
            this.is_free = is_free;
        }

        public double getDistribution_rate_1() {
            return distribution_rate_1;
        }

        public void setDistribution_rate_1(double distribution_rate_1) {
            this.distribution_rate_1 = distribution_rate_1;
        }

        public double getDistribution_rate_2() {
            return distribution_rate_2;
        }

        public void setDistribution_rate_2(double distribution_rate2) {
            this.distribution_rate_2 = distribution_rate2;
        }

        public double getDistribution_rate_3() {
            return distribution_rate_3;
        }

        public void setDistribution_rate_3(double distribution_rate_3) {
            this.distribution_rate_3 = distribution_rate_3;
        }

        @Override
        public String toString() {
            return "RoleBean{" +
                    "max_price=" + max_price +
                    ", min_price=" + min_price +
                    ", is_free=" + is_free +
                    ", distribution_rate_1=" + distribution_rate_1 +
                    ", distribution_rate_2=" + distribution_rate_2 +
                    ", distribution_rate_3=" + distribution_rate_3 +
                    '}';
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myassets_back_img://返回按钮
                finish();
                break;
        }
    }
}
