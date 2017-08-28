package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.EditCarActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class DeleteCarDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Window window = null;
    private TextView delete_car_tv, cancel_delete_tv;
    private String carid = "";
    private AppContext appContext;

    public DeleteCarDialog(Activity activity, int themeResId, String carid) {
        super(activity, themeResId);
        this.activity = activity;
        this.carid = carid;
        appContext = (AppContext) activity.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_deletecar);
        initViews();
    }

    /*
    * 初始化控件
    * */
    private void initViews() {
        delete_car_tv = (TextView) findViewById(R.id.delete_car_tv);
        delete_car_tv.setOnClickListener(this);
        cancel_delete_tv = (TextView) findViewById(R.id.cancel_delete_tv);
        cancel_delete_tv.setOnClickListener(this);
    }

    public void showDialog() {
        show();
        windowDeploy();
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
    }


    //设置窗口显示动画
    public void windowDeploy() {
        window = getWindow(); //得到对话框
//        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
//        window.setBackgroundDrawableResource(Color.GRAY); //设置对话框背景为半透明
        if(window!=null) {
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
//        wl.x = x; //x小于0左移，大于0右移
//        wl.y = y; //y小于0上移，大于0下移
            // wl.alpha = 0.5f; //设置透明度
            WindowManager m =window.getWindowManager();
            Display d = m.getDefaultDisplay();
            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
            wl.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
            getWindow().setAttributes(wl);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_delete_tv:
                dismiss();
                break;
            case R.id.delete_car_tv:
                if (appContext.isNetworkConnected()) {
                    delete(getParams());//确定删除车辆
                } else {
                    Toast.makeText(activity, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                break;
        }
    }

    /**
     * 删除车辆
     */
    public void delete(HashMap<String, Object> map) {
        VolleyUtil.getVolleyUtil(activity).StringRequestPostVolley(URLs.DELETE_CAR, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "删除车辆成功，返回的数据是：" + jsonObject.toString());
//                Toast.makeText(activity,"返回的数据是："+jsonObject.toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("carid", carid);
                intent.putExtra("type", "2");
                intent.setAction(EditCarActivity.ADD_EDIT_DELETE_CAR_ACTION);
//                intent.setClass(mContext, MainActivity.class);
                try {
                    JSONObject json = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),activity));
                    String status = json.getString("status");
                    int code=json.getInt("code");
                    UIHelper.showErrTips(code,activity);
                    if (status.equals("ok")) {
                        activity.sendBroadcast(intent);
//                        ViolationManager.deleteViolationsByCarId(carid,activity);//删除车辆在数据库中的违章信息
                        activity.setResult(IllegalActivity.RESULT_CODE);
                        dismiss();
//                        mContext.startActivity(intent);
                        activity.finish();
                        Toast.makeText(activity, "删除车辆成功", Toast.LENGTH_LONG).show();
                    } else {
                        dismiss();
                        Toast.makeText(activity, "删除车辆失败,请重试", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                dismiss();
                Toast.makeText(activity, "删除车辆失败\n" + volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 获取删除车辆所需要的请求参数
     */
    private TokenSQLUtils tokenSQLUtils;

    public HashMap<String, Object> getParams() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("carid", carid);
        String token = TokenSQLUtils.check();
        map.put("token", token);
        return map;
    }
}
