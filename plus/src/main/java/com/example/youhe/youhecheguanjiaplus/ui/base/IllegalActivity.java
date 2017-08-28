package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.IllegalHandleAdapter;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/25 0025.
 */
public class IllegalActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private Button addcar_btn;
    private ImageView illegal_back_iv;
    private TextView tips_tv;
    private LinearLayout empty_nonadd_car_layout;
    private ListView illegal_handle_lv;

    private int searchtype=-1;//是否本人本车    1代扣分  0本人本車

    private List<Car> carList;
    private IllegalHandleAdapter illegalHandleAdapter;

    public static final int RESULT_CODE=1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illegal_handle);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,IllegalActivity.this);
        }
        SystemBarUtil.useSystemBarTint(IllegalActivity.this);

        carList=new ArrayList<Car>();
        carList= MainFragment.carList;
        illegalHandleAdapter=new IllegalHandleAdapter(IllegalActivity.this,carList);
        initViews();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void initViews(){
        addcar_btn= (Button) findViewById(R.id.addcar_btn);
        addcar_btn.setOnClickListener(this);
        illegal_back_iv= (ImageView) findViewById(R.id.illegal_back_iv);
        illegal_back_iv.setOnClickListener(this);
        tips_tv= (TextView) findViewById(R.id.tips_tv);
        empty_nonadd_car_layout = (LinearLayout) findViewById(R.id.empty_nonadd_car_layout);
        if(carList!=null&&carList.size()==0){
            empty_nonadd_car_layout.setVisibility(View.VISIBLE);
            tips_tv.setVisibility(View.INVISIBLE);
        }else{
            empty_nonadd_car_layout.setVisibility(View.INVISIBLE);
            tips_tv.setVisibility(View.VISIBLE);
        }
        illegal_handle_lv = (ListView) findViewById(R.id.illegal_car_lv);
        illegal_handle_lv.setAdapter(illegalHandleAdapter);
        illegal_handle_lv.setOnItemClickListener(this);
    }

    Intent intent=null;
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.addcar_btn:
                intent=new Intent(IllegalActivity.this,AddCarActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                break;
            case R.id.illegal_back_iv:
                finish();
                overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            finish();
            overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    String carid = "";//车辆id
    String carnumber = "";//车牌号码
    String carcode = "";//车身架号
    String cardrivenumber = "";//发动机号
    String remark = "";//备注
    String ischeck="";//车辆是否认证
    String proprefix="";//省份前缀
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        carid = carList.get(i).getCarId();
        carnumber = carList.get(i).getCarnumber();
        carcode = carList.get(i).getCarcode();
        cardrivenumber = carList.get(i).getEnginenumber();
        remark = carList.get(i).getRemark();
        ischeck=carList.get(i).getIsCarCorrect();
        proprefix=carList.get(i).getProprefix();
        intent=new Intent();
        intent.putExtra("carid", carid);
        intent.putExtra("carnumber", carnumber);
        intent.putExtra("carcode", carcode);
        intent.putExtra("cardrivenumber", cardrivenumber);
        intent.putExtra("remark", remark);
        intent.putExtra("ischeck",ischeck);
        intent.putExtra("proprefix",proprefix);
        intent.putExtra("searchtype",1);
        if(ischeck.equals("-1")){
            intent.setClass(IllegalActivity.this, EditCarActivity.class);
            startActivityForResult(intent,RESULT_CODE);
            UIHelper.ToastMessage(IllegalActivity.this,"车辆信息有误，请重新编辑");
        }
//        else {
//            intent.setClass(IllegalActivity.this, IllegalQueryActivty.class);
//            intent.putExtra("searchtype",searchtype);
//            startActivity(intent);
//        }

        overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_CODE:
                illegalHandleAdapter=new IllegalHandleAdapter(IllegalActivity.this,carList);
                illegalHandleAdapter.notifyDataSetChanged();
                illegal_handle_lv.setAdapter(illegalHandleAdapter);
                break;
        }
    }

}
