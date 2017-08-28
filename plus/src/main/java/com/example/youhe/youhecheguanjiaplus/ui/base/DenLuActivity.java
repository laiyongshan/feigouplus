package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.biz.LoginScreenDao;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;


/**
 * 登录界面
 */
public class DenLuActivity extends AppCompatActivity {
    private ClearEditText clearEditText1,clearEditText2;
    private LoginScreenDao loginScreenDao;
    private SaveNameDao saveNameDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_den_lu);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,DenLuActivity.this);
        }
        SystemBarUtil.useSystemBarTint(DenLuActivity.this);

        in();//初始化控件
    }

    private void in() {
        takePhoto();
        clearEditText1 = (ClearEditText) findViewById(R.id.et_shouji);
        clearEditText2= (ClearEditText) findViewById(R.id.et_mima);
        loginScreenDao = new LoginScreenDao(this);
        saveNameDao = new SaveNameDao(this);//拿到之前填写的手机号码

        num(saveNameDao.readText("phonenumbe.txt"));

        Intent intent = getIntent();
        String password1 = intent.getStringExtra("password");
        String mobile1 = intent.getStringExtra("mobile");
        if (password1 != null && mobile1 != null){
            clearEditText1.setText(mobile1);
            clearEditText2.setText(password1);
            loginScreenDao.judge(mobile1,password1);//登录
        }
    }

    /**
     * 打开登录界面时
     * 读取本来手机号码
     */
    public void num(String numm){
        if(numm.equals("")){

        }else {
            clearEditText1.setText(numm.trim());
        }
    }

    /**
     * 返回键点击事件
     *
     */
    public void fanhui(View view){

        finish();
        Intent intent=new Intent(DenLuActivity.this,MainActivity.class);
        startActivity(intent);

    }
    //各个按钮的点击事件
    public void denglu(View view){

        switch (view.getId()){
            case R.id.tv_shoujilianzheng://手机验证码登录
                Intent shouJiIntent = new Intent(this,ShouJiDengLuActivity.class);
                startActivity(shouJiIntent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                finish();
                break;
            case R.id.tv_wangjimima://重置密码
                Intent wangJiMiMaIntent = new Intent(this,WanJiMimaActivity.class);
                startActivity(wangJiMiMaIntent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                finish();
                break;

            case R.id.tv_kuaisuzhuce://快速注册
                Intent kuaiSuIntent = new Intent(this,KuaiSuZhuCheActivity.class);
                startActivity(kuaiSuIntent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                finish();
                break;

            case R.id.dengluq://登录

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                String mobile = clearEditText1.getText().toString();
                String password = clearEditText2.getText().toString();
                 Boolean staust = NetUtils.isNetworkConnected(DenLuActivity.this);
                if (staust){
                    if(mobile.trim().length()<11){
                        Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    } else if (clearEditText2.length()>=6){
                        //ClearEditText逻辑
                        loginScreenDao.judge(mobile,password);//登录
                    }else {
                        ToastUtil.getLongToastByString(this,"密码长度不能小于6");
                    }
                }else {
                    ToastUtil.getLongToastByString(this,"网络连接失败，请检测设置");
                }

                break;
        }
    }

    /**
     * 调用
     */
    private static final int TAKE_PHOTO_REQUEST_CODE = 1;
    public void takePhoto() {

        if  (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)//申请写入权限
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)//申请读权限
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    TAKE_PHOTO_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(this, "友禾车管家需要权限正常运行", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName())); // 根据包名打开对应的设置界面
                    this.startActivity(intent);
                }
                break;
        }
    }



}
