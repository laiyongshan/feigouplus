package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.bean.PlusRoleBean;
import com.example.youhe.youhecheguanjiaplus.biz.GetMasterKey;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.utils.XGUtils;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.TimeButton;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lkl.cloudpos.aidl.AidlDeviceService;
import com.lkl.cloudpos.aidl.pinpad.AidlPinpad;
import com.lkl.cloudpos.aidl.system.AidlSystem;
import com.lkl.cloudpos.data.PinpadConstant;
import com.thepos.biz.ui.base.GainSerialNumberActivity2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 手机注册登录面
 */
public class KuaiSuZhuCheActivity extends AppCompatActivity implements View.OnClickListener {
    private TimeButton timeButton;//获取验证码
    private ClearEditText etTheserialPassword1, etTheserialPassword2, etTheserialMobile, id_car_num_et, name_et2;
    private EditText etTheserialVerification;

    private Button bt_theserialnumber;//获取机器序列号
    private TextView register_tv;//注册按钮

    private String poscode, mobile, password, password2, verifycode, IdCard, name;
    private String sheng, shi;
    private SaveNameDao saveNameDao;
    private VolleyUtil volleyUtil;//数据请求
    private StatusSQLUtils statusSQLUtils;//登录状态
    private TokenSQLUtils tsu;//Token
    private UIDialog uidialog, uiDialoRequest;
    private TextView etTheserialNumber;
    public String LKL_SERVICE_ACTION = "lkl_cloudpos_device_service";//服务地址
    private AidlSystem systemInf = null;
    private GetMasterKey getMasterKey;
    private AidlPinpad pinpad = null; // 密码键盘接口,用来灌入主密钥

    public static boolean isGetverifycode = true;//是否获取验证码

    public PosCodeBrocast posCodeBrocast;//mpos设备序列号广播接受者
    private final String POSCODE_ACTION = "POSCODE_ACTION_";
    private IntentFilter intentFilter;

    private TextView to_login_tv;//去登录
    private AppCompatSpinner spinnerRole;//角色选择
    private LinearLayout layoutPlusNumber;//车主卡号码布局
    private ClearEditText etPlusNumber;//车主卡输入框
    private LinearLayout layoutName, layoutCarNumber, layoutTheserialNumber;

    //    public static final int ROLE_PLUS = 1;//plus 用户

    //    public static final int ROLE_HONOUR_CARD = 2;//尊享卡用户
//    public static final int ROLE_OWNER_CARD = 3;//车主卡用户
    private ArrayList<PlusRoleBean> plusRoleList = new ArrayList<>();
    private ArrayList<String> spinnerData = new ArrayList<>();
    private ArrayAdapter spinnerAdapter = null;
    private int selectRole;
    private int selectItem = 0;
    //    [{"name":"puls用户","type":1},{"name":"尊享卡用户","type":2},{"name":"车主卡用户","type":3}]
//    private int selectItem=0;
    private String plus_name_et_alert = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_jji_den_lu);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            SystemBarUtil.setTranslucentStatus(true, KuaiSuZhuCheActivity.this);
        }
        SystemBarUtil.useSystemBarTint(KuaiSuZhuCheActivity.this);

        EventBus.getDefault().register(this);
        posCodeBrocast = new PosCodeBrocast();
        intentFilter = new IntentFilter();
        intentFilter.addAction(POSCODE_ACTION);

        timeButton(savedInstanceState);

        in();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(posCodeBrocast, intentFilter);
    }


    private void in() {

        tsu = new TokenSQLUtils(this);
        statusSQLUtils = new StatusSQLUtils(this);
        saveNameDao = new SaveNameDao(this);

        etTheserialNumber = (TextView) findViewById(R.id.et_theserialnumber);//设备序列号
        etTheserialVerification = (EditText) findViewById(R.id.et_yanzhenmima);//验证码
        etTheserialPassword1 = (ClearEditText) findViewById(R.id.et_mima);//密码
        etTheserialPassword2 = (ClearEditText) findViewById(R.id.onceagain_et);//第二次密码
        etTheserialMobile = (ClearEditText) findViewById(R.id.et_shouji);//手机号码
        id_car_num_et = (ClearEditText) findViewById(R.id.id_car_num_et);//获取身分证
        name_et2 = (ClearEditText) findViewById(R.id.name_et2);//获取姓名
        volleyUtil = VolleyUtil.getVolleyUtil(this);//上网请求

        spinnerRole = (AppCompatSpinner) findViewById(R.id.spinner_role);
        layoutPlusNumber = (LinearLayout) findViewById(R.id.layout_plus);
        layoutName = (LinearLayout) findViewById(R.id.layout_name);
        layoutCarNumber = (LinearLayout) findViewById(R.id.layout_car_number);
        etPlusNumber = (ClearEditText) findViewById(R.id.plus_name_et);
        layoutTheserialNumber = (LinearLayout) findViewById(R.id.layout_serial);

        param = new HashMap<>();
        uidialog = new UIDialog(this, "正在登录.......");
        uiDialoRequest = new UIDialog(this, "获取信息中...");

        register_tv = (TextView) findViewById(R.id.register_tv);//注册
        register_tv.setOnClickListener(this);

        bt_theserialnumber = (Button) findViewById(R.id.bt_theserialnumber);
        bt_theserialnumber.setOnClickListener(this);

        to_login_tv = (TextView) findViewById(R.id.to_login_tv);
        to_login_tv.setOnClickListener(this);

        initLBS();//定位


        requestType();
        event();
    }

    private void event() {
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem = i;
                selectRole = i + 1;
                changeRole(selectRole);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     * 根据角色改变布局
     */
    private void changeRole(int selectRole) {
//        switch (selectRole) {
//            case UserManager.ROLE_PLUS://plus用户
//                layoutPlusNumber.setVisibility(View.GONE);//隐藏车主卡
//                layoutTheserialNumber.setVisibility(View.VISIBLE);//显示序列号
//                break;
//            case UserManager.ROLE_OWNER_CARD://车主卡
//            case UserManager.ROLE_HONOUR_CARD://至尊卡
//                layoutPlusNumber.setVisibility(View.VISIBLE);//显示车主卡
//                layoutTheserialNumber.setVisibility(View.GONE);//隐藏序列号
//                break;
//        }
        if (selectRole == UserManager.ROLE_PLUS) {
            layoutPlusNumber.setVisibility(View.GONE);//隐藏车主卡
            layoutTheserialNumber.setVisibility(View.VISIBLE);//显示序列号
        } else {
            layoutPlusNumber.setVisibility(View.VISIBLE);//显示车主卡
            layoutTheserialNumber.setVisibility(View.GONE);//隐藏序列号
        }
        switch (selectRole) {
            case UserManager.ROLE_HONOUR_CARD://尊享
                plus_name_et_alert = getResources().getString(R.string.edit_plus_name_honour);
                break;
            case UserManager.ROLE_OWNER_CARD://车主卡
                plus_name_et_alert = getResources().getString(R.string.edit_plus_name);
                break;
            case UserManager.ROLE_PROMOTION://推广码
                plus_name_et_alert = getResources().getString(R.string.edit_plus_name_invitation);
                break;
        }
        etPlusNumber.setHint(plus_name_et_alert);
    }

    /**
     * 获取mpost的序列号
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        TextView textView = (TextView) findViewById(R.id.et_theserialnumber);
        if (event.getMsg().startsWith("YH")) {
            textView.setText(event.getMsg());
            param.put("poscode", event.getMsg());//mpost设备序列号
        }
    }

    /**
     * 获取验证码
     *
     * @param savedInstanceState
     */
    private void timeButton(Bundle savedInstanceState) {

        //获取验证码键
        timeButton = (TimeButton) findViewById(R.id.time_btn);
        timeButton.onCreate(savedInstanceState);
        timeButton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(60 * 1000);
        timeButton.setOnClickListener(this);

    }

    /**
     * 返回键
     */
    public void fanhui(View view) {
        finish();
    }


    /**
     * @param view
     */
    private HashMap<String, String> phonePams;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.time_btn:
                getetVerifycode();
                break;

            case R.id.bt_theserialnumber:
                theserialNumber();
                break;

            case R.id.register_tv:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    //隐藏键盘
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                register();
                break;

            case R.id.to_login_tv:
                Intent i = new Intent(KuaiSuZhuCheActivity.this, DenLuActivity.class);
                startActivity(i);
                finish();
                break;
        }

    }

    /**
     * 获取验证码
     */
    public void getetVerifycode() {
        if (MainActivity.getHtts() == false) {
            Toast.makeText(this, "网络连接失败，请检测设置", Toast.LENGTH_LONG).show();
            return;
        }

        String phoneNumber = etTheserialMobile.getText().toString();

        isGetverifycode = false;
        if (selectRole == UserManager.ROLE_PLUS && (etTheserialNumber.getText().toString().equals("") || etTheserialNumber.getText().toString() == null)) {
            Toast.makeText(this, "请先获取机器序列号", Toast.LENGTH_LONG).show();
            isGetverifycode = false;
            return;
        }
        if (selectRole != UserManager.ROLE_PLUS && StringUtils.isEmpty(etPlusNumber.getText().toString().trim())) {
            Toast.makeText(this, plus_name_et_alert, Toast.LENGTH_LONG).show();
            isGetverifycode = false;
            return;
        }

        if (phoneNumber.length() < 11) {
            Toast.makeText(this, "手机号码不能低于11位", Toast.LENGTH_LONG).show();
            isGetverifycode = false;
        } else {

            isGetverifycode = true;

            phonePams = new HashMap<>();
            phonePams.put("mobile", phoneNumber);
            phonePams.put("type", selectRole + "");
//                phonePams.put("poscode", etTheserialNumber.getText().toString());
            phonePams.put("unique_code", selectRole == UserManager.ROLE_PLUS ? etTheserialNumber.getText().toString() : etPlusNumber.getText().toString().trim());

            volleyUtil.postRequest(KuaiSuZhuCheActivity.this, URLs.PLUS_REGISTER_SMS, phonePams, "获取验证码失败", new OnVolleyInterface() {
                @Override
                public void success(JSONObject dataObject, String resultStr) {

                }

                @Override
                public void failed(JSONObject resultObject, String code, String msg) {
                    Toast.makeText(KuaiSuZhuCheActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    /**
     * 注册请求
     */
    public void register() {
        if (MainActivity.getHtts() == false) {
            Toast.makeText(KuaiSuZhuCheActivity.this, "网络连接失败，请检测设置", Toast.LENGTH_LONG).show();
            return;
        }

        String judgeMachineType = Build.MODEL;

        //判断机器机型

        if (selectRole == UserManager.ROLE_PLUS && judgeMachineType.equals("P92")) {//智能POS支付
            poscode = etTheserialNumber.getText().toString();//智能post设备序列号
            param.put("poscode", poscode);
        }

        verifycode = etTheserialVerification.getText().toString();//验证码
        password = etTheserialPassword1.getText().toString();//第一次密码
        password2 = etTheserialPassword2.getText().toString();//第二次密码
        mobile = etTheserialMobile.getText().toString();//手机号码
        IdCard = id_car_num_et.getText().toString();//获取身份证
        name = name_et2.getText().toString();//得到名字

//        boolean isOk=selectRole!=ROLE_OWNER_CARD(StringUtils.isEmpty(etPlusNumber.getText().toString()));
        boolean isOk = selectRole != UserManager.ROLE_PLUS && StringUtils.isEmpty(etPlusNumber.getText().toString());

        if (password.length() >= 6 && password2.length() >= 6) {
//            if(verifycode.equals("")||password.equals("")||password2.equals("")||mobile.equals("")||IdCard.equals("")||name.equals("")) {
            if (verifycode.equals("") || password.equals("") || password2.equals("") || mobile.equals("") || isOk) {
                Toast.makeText(KuaiSuZhuCheActivity.this, "请填写完整信息", Toast.LENGTH_LONG).show();
            } else if (!password.equals(password2)) {
                Toast.makeText(KuaiSuZhuCheActivity.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
            } else {
                if (selectRole == UserManager.ROLE_PLUS && StringUtils.isEmpty(etTheserialNumber.getText().toString())) {
                    Toast.makeText(KuaiSuZhuCheActivity.this, "请先获取机器序列号", Toast.LENGTH_LONG).show();
                } else {
                    if (!password.equals(password2)) {
                        Toast.makeText(KuaiSuZhuCheActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    } else {
                        requestString();
                    }
                }
            }
        } else {
            Toast.makeText(KuaiSuZhuCheActivity.this, "密码长度不能少于6", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 跳转到获取设备序列号界面
     */
    public void theserialNumber() {
        Intent intent = new Intent(this, GainSerialNumberActivity2.class);
        startActivityForResult(intent, 1000);
        this.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);

    }


    private static final String TAG = "WU";
    /**
     * 设别服务连接桥
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            Log.i(TAG, "aidlService服务连接成功");
            if (serviceBinder != null) {    //绑定成功
                AidlDeviceService serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder);
                try {
                    systemInf = AidlSystem.Stub.asInterface(serviceManager
                            .getSystemService());
                    try {
                        String terminalSn = systemInf.getSerialNo();//获取终端序列号
                        etTheserialNumber.setText(terminalSn);
                        //初始化密码键盘
                        pinpad = AidlPinpad.Stub.asInterface(serviceManager.getPinPad(PinpadConstant.PinpadId.BUILTIN));
                        getMasterKey = new GetMasterKey(KuaiSuZhuCheActivity.this, pinpad, terminalSn);
                        getMasterKey.httpSign();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "AidlService服务断开了");
        }
    };


    /**
     * 绑定服务
     */
    public void indService() {
        Intent intent = new Intent();
        intent.setAction(LKL_SERVICE_ACTION);
        boolean flag = this.bindService(intent, conn, this.BIND_AUTO_CREATE);//绑定服务
        if (flag) {
            Log.i(TAG, "服务绑定成功");
        } else {
            Log.i(TAG, "服务绑定失败");
        }
    }

    /**
     * 重新进去界面时
     * 绑定服务
     */
    @Override
    protected void onResume() {
        super.onResume();
        String judgeMachineType = Build.MODEL;
        if (judgeMachineType.equals("P92")) {
            findViewById(R.id.bt_theserialnumber).setVisibility(View.GONE);
            indService();//绑定服务
        } else {

        }
    }

    /**
     * 暂停时
     * 解绑服务
     */
    @Override
    protected void onPause() {
        super.onPause();

        String judgeMachineType = Build.MODEL;
        if (judgeMachineType.equals("P92")) {
            if (conn != null) {
                this.unbindService(conn);
            }
        } else {
        }
    }

    //获取参数
    private HashMap getParams() {

        param = new HashMap();

//        param.put("poscode", etTheserialNumber.getText().toString());
        //唯一序列号 Pos机号/尊享卡/车主卡
        param.put("unique_code", selectRole == UserManager.ROLE_PLUS ? etTheserialNumber.getText().toString() : etPlusNumber.getText().toString().trim());
        param.put("mobile", mobile);//手机号码
        param.put("verifycode", verifycode);
        param.put("password", ParamSign.getUserPassword(password));
        param.put("password2", ParamSign.getUserPassword(password2));

        param.put("type", selectRole + "");//注册类型 1puls2尊享卡3车主卡用户

        param.put("province", sheng + "");//省
        param.put("city", shi + "");//城市

        return param;

    }


    private HashMap<String, String> param;

    //注册请求
    public void requestString() {
        uidialog.show();

        if (MainActivity.getHtts() == false) {
            Toast.makeText(this, "网络连接失败，请检测设置", Toast.LENGTH_LONG).show();
            return;
        }

        volleyUtil.StringRequestPostVolley(URLs.PLUS_REGISTER_URL, EncryptUtil.encrypt(getParams()), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "注册返回的数据：" + jsonObject.toString());
                uidialog.hide();

                String josn = EncryptUtil.decryptJson(jsonObject.toString(), KuaiSuZhuCheActivity.this);
                Log.i("TAG", "注册返回的数据：" + josn);
                String judgeMachineType = Build.MODEL;
                //如果是P92就需要判断主密钥导入是否成功
                if (judgeMachineType.equals("P92")) {
                    if (getMasterKey.getIntoTheState()) {
                        estimate(josn);
                    } else {
                        ToastUtil.getLongToastByString(KuaiSuZhuCheActivity.this, "机器资源准备失败，请按左侧下方重置按钮！");
                    }

                } else {
                    estimate(josn);
                    //解析解密之后的数据
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(KuaiSuZhuCheActivity.this, "连接错误");
                uidialog.hide();
            }
        });
    }

    private void requestType() {
        uiDialoRequest.show();

        if (MainActivity.getHtts() == false) {
            Toast.makeText(this, "网络连接失败，请检测设置", Toast.LENGTH_LONG).show();
            return;
        }

        volleyUtil.StringRequestPostVolley(URLs.PLUS_REGISTER_TYPE, null, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "获取注册类型返回的数据：" + jsonObject.toString());
                uiDialoRequest.hide();
                try {
                    String json = EncryptUtil.decryptJson(jsonObject.toString(), KuaiSuZhuCheActivity.this);
                    JSONObject data = new JSONObject(json);

                    if (data.getString("status").equals("ok")) {
                        String listStr = data.getJSONObject("data").getString("user_type");

//                        plusRoleList.clear();
                        Log.d(TAG, listStr);
                        plusRoleList = new Gson().fromJson(listStr, new TypeToken<List<PlusRoleBean>>() {
                        }.getType());

                        Log.d(TAG, plusRoleList.toString());
                        if (plusRoleList.size() > 0) {
                            spinnerData.clear();
                            for (int i = 0; i < plusRoleList.size(); i++) {
                                spinnerData.add(plusRoleList.get(i).getName());
                                if (plusRoleList.get(i).getType() == 3) {
                                    selectItem = i;
                                    selectRole = 3;
                                }
                            }
                            spinnerAdapter = new ArrayAdapter<String>(KuaiSuZhuCheActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerData);
                            spinnerRole.setAdapter(spinnerAdapter);
                            spinnerRole.setSelection(selectItem);
                        } else {
                            ToastUtil.getShortToastByString(KuaiSuZhuCheActivity.this, "获取用户类型失败");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.getShortToastByString(KuaiSuZhuCheActivity.this, "获取用户类型失败");
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(KuaiSuZhuCheActivity.this, "连接错误");
                uiDialoRequest.hide();
            }
        });
    }

    /**
     * 注册判断
     *
     * @param
     */
    public void estimate(String josn) {
        String status = "";
        JSONObject jsonObject = null;
        Log.d("TAG","注册："+josn);
        try {
            jsonObject = new JSONObject(josn);
            status = jsonObject.getString("status");
            if (status.equals("ok")) {//如果是请求正确
                JSONObject jsonObject2 = null;

                Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
                AppContext.isLogin = true;

                HashMap params = new HashMap();
                Task ts = new Task(TaskType.TS_REFLUSH_HOME_PAGE, params);//登录成功后更新首页数据
                MainService.newTask(ts);
                EventBus.getDefault().post(new FirstEvent("ok"));
                jsonObject2 = jsonObject.getJSONObject("data");
                String token = "";
                if (jsonObject2.has("token"))
                    token = jsonObject2.getString("token");
                if (jsonObject2.has("user_type")) {
                    int userType = jsonObject2.getInt("user_type");
                    UserManager.setUserType(userType);
                }

                int userStatus = UserManager.USER_STATUS_NO;
                if (jsonObject2.has("status"))
                    userStatus = jsonObject2.getInt("status");

                UserManager.setUserStatus(userStatus);

                volleyUtil.cancelAllQueue(this);
                saveNameDao.writeTxtToFile("", "poscode.txt");
                statusSQLUtils.addDate("yes");//注册成功后设置为登录状态
                tsu.addDate(token);//注册成功保存token
                saveNameDao.writeTxtToFile(mobile, "phonenumbe.txt");

                XGUtils.registered(mobile);
                Toast.makeText(KuaiSuZhuCheActivity.this, "注册成功", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(KuaiSuZhuCheActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.EXTRA_IS_NEW, true);
                startActivity(intent);

                finish();

            } else if (status.equals("fail")) {//如果请求错误
//                    Misidentification.misidentification1(this,status,jsonObject);
//                if (jsonObject.has("code")) {
//                    int code = jsonObject.getInt("code");
//                    UIHelper.showErrTips(code, KuaiSuZhuCheActivity.this);
//                }
                if (jsonObject.has("error")) {
                    Toast.makeText(KuaiSuZhuCheActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KuaiSuZhuCheActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到免责声明界面
     *
     * @param view
     */
    public void click(View view) {
        if (MainActivity.getHtts() == false) {
            ToastUtil.getLongToastByString(this, "网络连接失败，请检测设置");
            return;
        }
        Intent intent = new Intent(this, DisclaimerActivity.class);
        intent.putExtra("mianzhe", URLs.THE_MANUAL);
        intent.putExtra("title", "服务使用协议");
        startActivity(intent);
    }


    private LocationClient mClient;
    private LocationClientOption mOption;
    private String mLBSAddress;
    private boolean mLBSIsReceiver;
    private AppContext application;

    //初始化地位服务
    private void initLBS() {
        application = new AppContext();
        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setOpenGps(true);
        mOption.setCoorType("bd09ll");
        mOption.setAddrType("all");
        mOption.setScanSpan(1000);
        mOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mClient = new LocationClient(getApplicationContext(), mOption);
        mClient.setLocOption(mOption);
        mClient.start();
        mLBSIsReceiver = true;
        mClient.requestLocation();
        mClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation arg0) {
                mLBSAddress = arg0.getAddrStr();
                application.mLocation = arg0.getAddrStr();
                if (arg0.getCity() != null) {
                    sheng = arg0.getProvince().replace("省", "");
                    shi = arg0.getCity().replace("市", "");
                    Log.i("WU", sheng + shi);

                }
                Log.i("TAG", "当前位置为：" + application.getLocalCity() + arg0.getAddrStr());
                Log.i("TAG", "当前位置城市为：" + application.getLocalCity());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOption.setOpenGps(false);
        mClient.stop();
        unregisterReceiver(posCodeBrocast);
    }


    //mpost设备序列号广播接收者
    class PosCodeBrocast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String poscode = intent.getStringExtra("poscode");
                if (poscode != null) {
                    etTheserialNumber.setText(poscode);
                    param.put("poscode", poscode);//mpost设备序列号
                }
            }
        }
    }

}
