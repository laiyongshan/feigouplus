package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ChoiceGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public class CheckRecoActivity extends Activity implements View.OnClickListener{

    private Button  reset_btn,sure_btn;
    private EditText trading_cardcode_et;
    private TextView start_date_tv,over_date_tv;

    private ImageView reco_back_img;

    private ChoiceGroup choice_reco_group,choice_type_group;


    private int mYear,mMonth,mDay;
    final int DATE_DIALOG1 = 1;
    final int DATE_DIALOG2 = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_reco);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CheckRecoActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CheckRecoActivity.this);

        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        initViews();
        initData();
    }


    //初始化控件
    private void initViews(){
        reco_back_img= (ImageView) findViewById(R.id.reco_back_img);
        reco_back_img.setOnClickListener(this);

        reset_btn= (Button) findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(this);

        sure_btn= (Button) findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);

        trading_cardcode_et= (EditText) findViewById(R.id.trading_cardcode_et);

        start_date_tv= (TextView) findViewById(R.id.start_date_tv);
        start_date_tv.setOnClickListener(this);
        over_date_tv= (TextView) findViewById(R.id.over_date_tv);
        over_date_tv.setOnClickListener(this);

        choice_reco_group= (ChoiceGroup) findViewById(R.id.choice_reco_group);//交易形式
        choice_type_group= (ChoiceGroup) findViewById(R.id.choice_type_group);//类型

    }

    //初始化控件
    private void initData(){
        List<String> list = new ArrayList<String>();
        list.add("支 出");
        list.add("收 入");

        List<String> list2 = new ArrayList<String>();
        list2.add("支 出");
        list2.add("收 入");
        list2.add("支 出");
        list2.add("收 入");
        list2.add("支 出");


        choice_reco_group.setColumn(2);//设置列数
        choice_reco_group.setValues(list);//设置记录列表
        choice_reco_group.setView(this);//设置视图
        choice_reco_group.setInitChecked(0);//设置最初默认被选按钮

        choice_type_group.setColumn(3);//设置列数
        choice_type_group.setValues(list2);//设置记录列表
        choice_type_group.setView(this);//设置视图
        choice_type_group.setInitChecked(0);//设置最初默认被选按钮

    }

    Intent intent;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reco_back_img:
                finish();
                CheckRecoActivity.this.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;

            case R.id.start_date_tv:
                showDialog(DATE_DIALOG1);
                break;

            case R.id.over_date_tv:
                showDialog(DATE_DIALOG2);
                break;

            case R.id.reset_btn:
                start_date_tv.setText("");
                over_date_tv.setText("");
                break;

            case R.id.sure_btn://确定按钮
                intent=new Intent(CheckRecoActivity.this,TradingRecoActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG1:
                return new DatePickerDialog(this, mdateListener1, mYear, mMonth, mDay);

            case DATE_DIALOG2:
                return new DatePickerDialog(this, mdateListener2, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display(TextView tv) {
        tv.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mdateListener1 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display(start_date_tv);
        }
    };


    private DatePickerDialog.OnDateSetListener mdateListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display(over_date_tv);
        }
    };



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            CheckRecoActivity.this.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        }
        return false;
    }

}
