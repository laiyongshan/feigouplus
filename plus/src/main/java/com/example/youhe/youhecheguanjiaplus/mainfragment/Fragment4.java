package com.example.youhe.youhecheguanjiaplus.mainfragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.biz.DetectionOfUpdate;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.biz.StartingTest;
import com.example.youhe.youhecheguanjiaplus.db.biz.NamePathSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.DenLuActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.DisclaimerActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.FeedbackActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.MyAssetsActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.PersonalinformationActivity;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapScale;
import com.example.youhe.youhecheguanjiaplus.utils.ClickUtils;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ImgDownload;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.TripleDES;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.xutils.x;

import java.util.HashMap;


/**
 * Created by Administrator on 2016/8/23 0023.
 * 第四个主界面
 */
public class Fragment4 extends YeoheFragment implements View.OnClickListener {
    private View view;
    private LinearLayout layout2;
    private RelativeLayout layout1;
    private ImageView dengluim;
    private TextView textView1, name;

    private TextView version_code_tv;

    private ImageView circleImageView;
    private SaveNameDao saveNameDao;
    private StatusSQLUtils statusSQLUtils;//保存或得到登录状态

    private LoadDataBrocast loadDataBrocast;//加载数据广播接受者
    private IntentFilter filter;

//    private TextView yu_e_tv;//余额

    //    private RelativeLayout real_name_layout;//实名认证
//    private RelativeLayout yu_e_layout;//可用余额
    private RelativeLayout request_refund_layout;//申请提现
    private SharedPreferences sharedPreferences;
    private Boolean isRealName = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("is_real_name", Activity.MODE_PRIVATE);
        isRealName = sharedPreferences.getBoolean("isrealname", false);

        view = inflater.inflate(R.layout.mainfragmentlayout4, container, false);//导航栏加载第四个fragmen页面
        x.view().inject(getActivity());

        loadDataBrocast = new LoadDataBrocast();
        filter = new IntentFilter();
        filter.addAction("android.net.conn.LOADDATA");
        getActivity().registerReceiver(loadDataBrocast, filter);

        in(view);

//        if(AppContext.isLogin) {
//            getRemainingSum();//余额查询
//            checkClientAuth();//实名检验
//        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(loadDataBrocast);
    }

    public void loadDate() {
        if (AppContext.isLogin) {
            getRemainingSum();//获取余额
            checkClientAuth();//实名检验

//            yu_e_layout.setVisibility(View.VISIBLE);
            request_refund_layout.setVisibility(View.VISIBLE);
        } else {
//            real_name_layout.setVisibility(View.GONE);
//            yu_e_layout.setVisibility(View.GONE);
            request_refund_layout.setVisibility(View.GONE);
            layout1 = (RelativeLayout) view.findViewById(R.id.line1);//未登录的布局
            layout1.setVisibility(View.VISIBLE);
            layout2 = (LinearLayout) view.findViewById(R.id.line2);//登录后的布局
            layout2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//                loadDate();
        }
    }

    private void in(View view) {
//        permissionToApplyFor();//权限申请
//        imageUtils = new ImageUtils();
        statusSQLUtils = new StatusSQLUtils(getActivity());
        sptk = getActivity().getSharedPreferences("sta", getActivity().MODE_PRIVATE);//用来得到到已存的Token状态
        dengluim = (ImageView) view.findViewById(R.id.denglu_im);//得到登录按钮
        dengluim.setOnClickListener(this);
        layout1 = (RelativeLayout) view.findViewById(R.id.line1);//未登录的布局
//        textView1 = (TextView) view.findViewById(R.id.denglu_tv);
        layout2 = (LinearLayout) view.findViewById(R.id.line2);//登录后的布局
        newsImageView = (RelativeLayout) view.findViewById(R.id.news);
        newsTextView = (TextView) view.findViewById(R.id.version_code_tv);
        startingTest = new StartingTest(getActivity());//上网判断是否有新内容

//        yu_e_layout= (RelativeLayout) view.findViewById(R.id.yu_e_layout);
        request_refund_layout = (RelativeLayout) view.findViewById(R.id.request_refund_layout);

//        real_name_layout= (RelativeLayout) view.findViewById(R.id.real_name_layout);
        if (isRealName) {
//            real_name_layout.setVisibility(View.GONE);
        }


//        yu_e_tv= (TextView) view.findViewById(R.id.yu_e_tv);

        version_code_tv = (TextView) view.findViewById(R.id.version_code_tv);
        version_code_tv.setText(getVersionName());

        circleImageView = (ImageView) view.findViewById(R.id.denglu_iv2);//登录后的头像
        name = (TextView) view.findViewById(R.id.denglu_tv2);//用户名
        saveNameDao = new SaveNameDao(getActivity());
        layout2.setOnClickListener(this);
        layout1.setOnClickListener(this);
        EventBus.getDefault().register(this);
        layDianJiShij(view);//登录界面中的各个菜单的点击事件
        state();
        judgeUpdate();//判断是否有新版本

    }

    private Bitmap getHearImage() {
        Bitmap bitmap = null;
        try {
            String a = UserManager.getValue(UserManager.SP_USER_HEAR_IMG_URL);
//            Log.d("TAG","aaaaaaa"+a);
            if (!StringUtils.isEmpty(a)) {
                bitmap= ImageLoader.getInstance().loadImageSync(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取坂本明
     *
     * @return
     */
    public String getVersionName() {
        PackageManager manager = getActivity().getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param view 登录界面中的各个菜单的点击事件
     */
    public void layDianJiShij(View view) {

//        //可用余额
//        view.findViewById(R.id.yu_e_layout).setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                getRemainingSum();//余额查询
//            }
//        });


        //跳到违章常见问题界面
        view.findViewById(R.id.lay3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                if (status.equals("yes")) {

                } else {
                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
                    startActivity(intent);
                }

            }
        });


//        //关于我们
//        view.findViewById(R.id.guanyuwomen).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String status = statusSQLUtils.check();
//                if (status.equals("yes")) {
//                    Intent intent = new Intent(getActivity(), YeoActivity.class);
//
//                    startActivity(intent);
//
//                } else {
//                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
//                    startActivity(intent);
//
//                }
//
//            }
//        });

        //免责声明
        view.findViewById(R.id.mianzhi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                if (status.equals("yes")) {
                    Intent disIntent = new Intent(getActivity(), DisclaimerActivity.class);
                    disIntent.putExtra("mianzhe", URLs.THE_STATEMENT);
                    disIntent.putExtra("title", "免责声明");
                    startActivity(disIntent);

                } else {

                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
                    startActivity(intent);
                }
            }
        });
        //常见问题
        view.findViewById(R.id.lay3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                if (status.equals("yes")) {
                    Intent disIntent = new Intent(getActivity(), DisclaimerActivity.class);
                    disIntent.putExtra("mianzhe", URLs.COMMONPROBLEMS);
                    disIntent.putExtra("title", "常见问题");
                    startActivity(disIntent);

                } else {

                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
                    startActivity(intent);
                }

            }
        });


        //检测更新
        view.findViewById(R.id.jinace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                DetectionOfUpdate detectionOfUpdate = new DetectionOfUpdate(getActivity());//检测更新
                detectionOfUpdate.in();
//                if (status.equals("yes")) {
//
//                } else {
//                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
//                    startActivity(intent);
//                }

            }
        });

        //意见反馈
        view.findViewById(R.id.yijianfankui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                if (status.equals("yes")) {
                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
                    startActivity(intent);
                }
            }
        });

        //设置
        view.findViewById(R.id.setting_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSQLUtils.check();
                if (status.equals("yes")) {
                    Intent intent = new Intent(getActivity(), PersonalinformationActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
                    startActivity(intent);
                }
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
            }
        });


        //我的资产
        view.findViewById(R.id.request_refund_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String status = statusSQLUtils.check();
                if (AppContext.isLogin) {
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    }else {
                        Intent intent = new Intent(getActivity(), MyAssetsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    UIHelper.showLoginActivity(getActivity());
                    UIHelper.ToastMessage(getActivity(), "请先登录");
                }
//
            }
        });


//        //实名认证
//        view.findViewById(R.id.real_name_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String status = statusSQLUtils.check();
//                if (status.equals("yes")) {
//                    Intent intent = new Intent(getActivity(), RealNameActivity.class);
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(getActivity(), DenLuActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }

    /**
     * 界面跳转
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (ClickUtils.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.line1:
                //跳到登录界面
                Intent dengluintent = new Intent(getContext(), DenLuActivity.class);
                startActivity(dengluintent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                break;
            case R.id.denglu_im:
                //跳到登录界面
                Intent dengluintent2 = new Intent(getContext(), DenLuActivity.class);
                startActivity(dengluintent2);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                break;

            case R.id.line2:
                //跳到个人信息
                Intent pfaIntent = new Intent(getContext(), PersonalinformationActivity.class);
                startActivity(pfaIntent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                break;
        }
    }


    /**
     * 判断是否登录
     */
    public void
    whetherTheLogin() {

        String status = statusSQLUtils.check();
        Log.i("WU", status);

        if (status.equals("no")) {//未登录状态
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
//            Log.i("WU","未登录状态");

        } else if (status.equals("yes")) {//登录状态

            layout2.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);

            touxiang();//设置头像
            setName();//设置昵称

        }

    }

    /**
     * 设置名称
     */
    private NamePathSQLUtils sqlUtils;

    public void setName() {
        sqlUtils = new NamePathSQLUtils(getActivity());
        String httpNames = sqlUtils.check();//网上的昵称
        if (httpNames.equals("")) {
            name.setText("未设置用户名称");
        } else {
            name.setText(httpNames);
        }
    }


    /**
     * 设置个人中心头像
     */
    public void touxiang() {
        //显示SD卡中的图片缓存

        Bitmap head_bitmap = BitmapScale.getDiskBitmap(Environment.getExternalStorageDirectory().toString() + "/yeohe/head_bitmap/"
        +saveNameDao.readText("phonenumbe.txt").trim()+ "head.jpg");
//        Bitmap head_bitmap = getHearImage();
        if (head_bitmap != null) {
            circleImageView.setImageBitmap(ImgDownload.toRoundBitmap(head_bitmap));
        } else {
            circleImageView.setImageResource(R.drawable.gerz_01);
        }
    }


    /**
     * 每次这个界面出现都要判断
     */
    @Override
    public void onResume() {
        super.onResume();

        whetherTheLogin();//判断是否登录状态

    }

    private SharedPreferences sptk;
    private String isFirsttk;

    /**
     * 得到Token的状态是否过期
     */
    public void state() {
        isFirsttk = sptk.getString("status", "q");

        if (isFirsttk.equals("aaa")) {//未过期
            statusSQLUtils.undateApi("yes");//设置为登录状态
        } else if (isFirsttk.equals("ccc")) {///已过期或者

        } else if (isFirsttk.equals("ddd")) {
            statusSQLUtils.undateApi("no");//设置为未登录状态
        }
    }

    /**
     * 判断是否有新版本
     */
    private RelativeLayout newsImageView;//有新版本要显示的图标
    private TextView newsTextView;//当前为最新版本要显示的文字
    private StartingTest startingTest;

    public void judgeUpdate() {

        startingTest.deDao(newsImageView, newsTextView);

    }


    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        if (event.getMsg().equals("ok")) {

            whetherTheLogin();//判断是否登录状态
            Log.i("WU", "判断是否登录状态");
        }

    }


    @Override
    public void init() {

    }

    //刷新界面
    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];

        switch (type) {
            case TaskType.TS_REAL_NAME:
                int auth = (int) param[1];
                if (auth == -1) {
//                    real_name_layout.setVisibility(View.VISIBLE);
                } else if (auth == 1) {
//                    real_name_layout.setVisibility(View.GONE);
                }
                break;

            case TaskType.TS_GET_YU_E:
//                if(AppContext.isLogin) {
////                    getRemainingSum();//获取用户余额
//                }else{
//                    yu_e_tv.setText("");
//                }
                loadDate();//加载数据
                break;

            case TaskType.TS_USER_EXIT:
                whetherTheLogin();
                break;
        }

    }


    HashMap params;

    //余额显示
    private void getRemainingSum() {

        params = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if (token != null) {
            params.put("token", token);
        }

        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(URLs.GET_CLIENT_REMAINING_SUM, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "获取余额返回的数据：" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(jsonObject.toString());
                    String data = obj.getString("data");
                    byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                    String json = new String(encrypt, "UTF-8");
                    Log.i("TAG", "解密之后的数据：" + json);

                    obj = new JSONObject(json);
                    JSONObject dataObj = obj.getJSONObject("data");
                    float remaining_sum = dataObj.getInt("remaining_sum");
//                    yu_e_tv.setText(remaining_sum+"元");

                } catch (Exception e) {
                    e.printStackTrace();
//                    yu_e_tv.setText("");
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Log.i("TAG", "获取余额返回的错误数据：" + volleyError.toString());
            }
        });
    }


    /*
    * 检验是否已经实名验证
    * */
    private void checkClientAuth() {
        params = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if (token != null) {
            params.put("token", token);
        }

        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(URLs.CHECK_CLIENT_AUTH, EncryptUtil.encrypt(params), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "实名检验返回的数据：" + jsonObject.toString());

                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), getActivity()));
                    int status = obj.optJSONObject("data").optInt("status");
                    if (status == 1) {//已验证
//                        real_name_layout.setVisibility(View.GONE);
                    } else if (status == -1) {//未验证
//                        real_name_layout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });

    }

    //广播接受者
    class LoadDataBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            loadDate();//加载数据
        }
    }

}
