package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.NamePathSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.ui.base.MainActivity;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapScale;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.Misidentification;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.utils.XGUtils;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by Administrator on 2016/9/7 0007.
 * 登录逻辑类
 */
public class LoginScreenDao {

    private Activity mActivity;
    private VolleyUtil volleyUtil;
    private HashMap param;
    private String json = "";
    private TokenSQLUtils tsu;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SaveNameDao saveNameDao;
    private StatusSQLUtils statusSQLUtils;
    private NamePathSQLUtils namePathSqlUtils;

    private UIDialog uidialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor ed;

    public LoginScreenDao(Activity activity) {
        mActivity = activity;
        volleyUtil = VolleyUtil.getVolleyUtil(mActivity);
        param = new HashMap<>();
        tsu = new TokenSQLUtils(mActivity);
        in();

        sharedPreferences = mActivity.getSharedPreferences("is_real_name", Context.MODE_PRIVATE);
        ed = sharedPreferences.edit();
    }

    private void in() {
        EventBus.getDefault().register(this);
        uidialog = new UIDialog(mActivity, "正在登录.......");

        sp = mActivity.getSharedPreferences("judges", Context.MODE_PRIVATE);
        editor = sp.edit();
        saveNameDao = new SaveNameDao(mActivity);
        namePathSqlUtils = new NamePathSQLUtils(mActivity);

    }

    public void judge(String mobile, String password) {//初始化工具
        requestString(mobile, password);//登录请求
    }


    public HashMap map;

    /**
     * 登录请求
     */
    public void requestString(final String mobile, final String password) {
        uidialog.show();

        map = new HashMap();
        map.clear();
        map.put("mobile", mobile);
        map.put("password", ParamSign.getUserPassword(password));

        volleyUtil.StringRequestPostVolley(URLs.REGISTERLG, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                saveNameDao.writeTxtToFile(mobile, "phonenumbe.txt");//点击登录时保存手机号码
                Log.i("WU", jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(jsonObject.toString());

                    Log.i("TAG", "登录返回的数据：" + EncryptUtil.decryptJson(jsonObject.toString(), mActivity));

                    if (obj.has("data")) {
//                    json = jsonObject.toString();
                        jsonDate(EncryptUtil.decryptJson(jsonObject.toString(), mActivity), mobile);
                    } else {
                        Toast.makeText(mActivity, "登录失败,请检查账号密码是否正确后重试", Toast.LENGTH_LONG).show();
                    }
//                    parseJson(json);//解析解密之后的数据

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "登录失败，请检查后重试", Toast.LENGTH_LONG).show();
                } finally {
                    uidialog.hide();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(mActivity, "网络连接失败");
                uidialog.hide();
            }
        });
    }

    /**
     * 注册信鸽
     */
    private void xgReg(String mobile) {
        XGUtils.registered(mobile);
    }

    public void jsonDate(String json, final String mobile) {
        String status = "";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            status = jsonObject.getString("status");

//            if(jsonObject.has("code")){
//                int code=jsonObject.getInt("code");
////                UIHelper.showErrTips(code,mActivity);
//            }

            if (status.equals("ok")) {//登录成功后应该执行的逻辑
                Log.i("WU", "进去了OK");

                AppContext.isLogin = true;
                xgReg(mobile);

                HashMap params = new HashMap();
                Task ts = new Task(TaskType.TS_REFLUSH_HOME_PAGE, params);//登录成功后更新首页数据
                MainService.newTask(ts);

//                Intent intent=new Intent();
//                intent.setAction("REFLUSH_ORDER_LIST");//登录成功后刷新订单列表
//                mActivity.sendBroadcast(intent);

                statusSQLUtils = new StatusSQLUtils(mActivity);
                if (statusSQLUtils.check().equals("")) {

                    statusSQLUtils.addDate("yes");//保存已登录状态
                    Log.i("WU", "第一次开机保存" + statusSQLUtils.check());
                } else {
                    statusSQLUtils.undateApi("yes");
                    Log.i("WU", "第开机保存" + statusSQLUtils.check());
                }
                JSONObject jsonObject2 = jsonObject.getJSONObject("data");

                Misidentification.misidentification1(mActivity, status, jsonObject);

                String token = jsonObject2.getString("token");//需要保存的Token值
                final String headimgurl = jsonObject2.getString("headimgurl");//得到头像地址
                String nickname = jsonObject2.getString("nickname");//得到昵称地址
                int userType = jsonObject2.getInt("user_type");

                int userStatus = UserManager.USER_STATUS_NO;
                if (jsonObject2.has("status"))
                    userStatus = jsonObject2.getInt("status");

                UserManager.setUserStatus(userStatus);
                UserManager.setUserType(userType);
                UserManager.setValue(UserManager.SP_USER_HEAR_IMG_URL, headimgurl);

                int auth = jsonObject2.getInt("auth");

                namePathSqlUtils.addDate(nickname);//保存昵称地址

//                HashMap params1 = new HashMap();
//                params1.put("auth",auth);
//                Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//登录成功后更新首页数据
//                MainService.newTask(ts1);

                if (auth == 1) {
                    ed.putBoolean("isrealname", true);
                } else if (auth == -1) {
                    ed.putBoolean("isrealname", false);
                }
                ed.commit();

                saveNameDao.writeTxtToFile(mobile, "phonenumbe.txt");//点击登录时保存手机号码
                Log.i("WU", "保存token" + token);
                tsu.addDate(token);//保存token到数据库
                saveNameDao.writeTxtToFile("no", "qqq.txt");

                Log.i("ATG", "token->>" + TokenSQLUtils.check());

                EventBus.getDefault().post(new FirstEvent("ok"));
                ImageLoader.getInstance().loadImage(headimgurl,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                        if (bitmap != null)
                            try {
                                BitmapScale.saveFile(bitmap, mobile.trim());//保存图片
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        super.onLoadingComplete(imageUri, view, bitmap);
                    }
                });


                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.EXTRA_IS_NEW, true);
                mActivity.startActivity(intent);

                mActivity.finish();
            } else {
                AppContext.isLogin = false;
                String show_msg = "";
                if (jsonObject.has("show_msg")) {
                    show_msg = jsonObject.optString("show_msg");
                }
                ToastUtil.getShortToastByString(mActivity, show_msg + ",登录失败，请重试！");
            }

        } catch (Exception e) {
            AppContext.isLogin = false;
            ToastUtil.getShortToastByString(mActivity, "登录失败，请检查网络设置或稍后重试！");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventMainThread(FirstEvent event) {


    }


}
