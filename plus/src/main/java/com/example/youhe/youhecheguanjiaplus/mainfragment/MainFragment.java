package com.example.youhe.youhecheguanjiaplus.mainfragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.CarInfoAdapter;
import com.example.youhe.youhecheguanjiaplus.adapter.TypeAdapter;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.Banner;
import com.example.youhe.youhecheguanjiaplus.bean.Bulletin;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.city.CityActivty;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.entity.base.CarOpenCity;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.MarqueeTextViewClickListener;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.manager.CarOpenCitysMenager;
import com.example.youhe.youhecheguanjiaplus.ui.base.AccountQueryActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.AddCarActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.AnnualInspectionActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommentWebActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.EditCarActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.HeatMapActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalCodeActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalQueryActivty;
import com.example.youhe.youhecheguanjiaplus.ui.base.LoanActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.OilPriceAPIActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.OrderStyleActivity;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.MarqueeTextView;
import com.example.youhe.youhecheguanjiaplus.widget.MyGridView;
import com.zxing.android.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by Administrator on 2016/9/5 0005.
 * 主界面
 */
public class MainFragment extends YeoheFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private View view;
    private LinearLayout dingwei_ll;//topbar定位
    private ImageView QRCode_img;//一二维码扫描
    private TextView local_city;//定位或选择的城市
    private String city = "";//城市

    private LayoutInflater inflater;

    public static List<View> carViews = null;//用户所添加车辆的显示
    public static List<Car> carList = null;//用户所添加车辆的list类
    public static String strCarList = "";//车辆列表信息

    private MyGridView gv_type;//新增功能按钮
    private ArrayList<Integer> types;
    private TypeAdapter typeAdapter;

    private MyGridView gv_funcation;
    private ArrayList<Integer> funcation_types;
    private TypeAdapter funcation_type_adapter;

    private LinearLayout car_ll;
    private LinearLayout violation_ll;
    private TextView illegal_degree_tv, illegal_count_tv;

//    private LinearLayout network_request_error_layout;//网络加载失败，重新加载界面
//    private TextView reload_tv;//重新加载按钮

    private ImageButton addcar_btn;//添加车辆按钮
    public LinearLayout addcar_ll;//添加车辆的layout
    private ViewPager car_viewpager;//首页车辆信息viewpageer
    private RelativeLayout car_relativelayout;//车辆信息layout
    private LinearLayout car_dot_ll;//小圆点
    private ImageView[] dots;
    private int currentIndex;
    private ImageButton add_car_img_btn;

    private CarInfoAdapter carInfoAdapter;

    //    private RelativeLayout apply_credit_card_layout;//在线申请信用卡
    private RelativeLayout youth_loan_layout;//青春贷
    private RelativeLayout platinum_creditCar_layout;//浦发银行白金信用卡

    private ADD_DELETE_EditCarBroadcastReceiver editCarBroadcastReceiver;//添加，删除，编辑车辆的广播接受者
    private IntentFilter editIntentFilter;

    private NetReceiver mNetReceiver;

    private AppContext appContext;

    private final int ADDCAR_RESULTCODE = 01;
    private final int CITY_RESULTCODE = 02;
    public static final int EDITCAR_RESULTCODE = 03;
    private final int VIOCATION_RESULTCODE = 04;

    private final int REQUESTCODE = 10086;//权限申请返回码

    public static boolean isReflush = true;//是否刷新数据
    public static List<Integer> positionList = new ArrayList<Integer>();//是否已查询过违章的carID标记数组

    List<CarOpenCity> carOpenCityList;//本人本车开放城市列表

    private SliderLayout mSlider;//首页banner轮播图

    private MarqueeTextView marqueeTv;//公告
    private List<Bulletin> bulletinList = new ArrayList<Bulletin>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mainfragment, null);

        appContext = (AppContext) getActivity().getApplicationContext();
        carViews = new ArrayList<View>();
        carList = new ArrayList<Car>();
        carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
        dots = new ImageView[carViews.size()];

        carOpenCityList = CarOpenCitysMenager.carOpenCityList;

        initViews();//初始化控件
        initCategory();//新增功能按钮初始化
        initFuncation();//4个功能按钮

        mNetReceiver = new NetReceiver();//网络连接情况广播接收者
        IntentFilter mNetFilter = new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetReceiver, mNetFilter);//注册网络连接情况的监听广播接收者

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editCarBroadcastReceiver = new ADD_DELETE_EditCarBroadcastReceiver();
        editIntentFilter = new IntentFilter(EditCarActivity.ADD_EDIT_DELETE_CAR_ACTION);
        getActivity().registerReceiver(editCarBroadcastReceiver, editIntentFilter);

        Log.i("TAG","MainFragment is onCreat.....");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("strCarList", strCarList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            strCarList = savedInstanceState.getString("strCarList");
            carList.clear();
            carViews.clear();
            car_dot_ll.removeAllViewsInLayout();
            carList = jsonTolist(strCarList);
            if (carList.size() > 0) {
                carViews.add(inflater.inflate(R.layout.item_main_addcar, null));
                ImageView dot_img = new ImageView(getActivity());
                dot_img.setImageResource(R.drawable.dot);
                dot_img.setPadding(10, 0, 10, 0);
                dot_img.setEnabled(false);
                car_dot_ll.addView(dot_img);
                initCarViews(carList);//初始化车辆信息显示的Viewpager
                carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
                carInfoAdapter.notifyDataSetChanged();
                car_viewpager.setAdapter(carInfoAdapter);
                car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
                qurery(queryGetParams(carList.get(carList.size() - 1).getCarId()), carList.get(carList.size() - 1).getCarnumber());//查询viewpager显示的第一页的车辆的违章信息
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((!isReflush) && (AppContext.isLogin && ((carList.size() == 0) || (!(car_relativelayout.getVisibility() == View.VISIBLE)))) && (appContext.isNetworkConnected())) {
            getCarList(getParams());//获取用户已添加的车辆列表
        }
        car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());

        if (!AppContext.isLogin && appContext.isNetworkConnected()) {
            car_relativelayout.setVisibility(View.GONE);
            addcar_ll.setVisibility(View.VISIBLE);
            carList.clear();
            carViews.clear();
        } else if (!appContext.isNetworkConnected() && (carList.size() == 0 || !(car_relativelayout.getVisibility() == View.VISIBLE))) {
            car_relativelayout.setVisibility(View.VISIBLE);
            addcar_ll.setVisibility(View.GONE);
        }

        if (carList.size() > 0) {
            car_relativelayout.setVisibility(View.VISIBLE);
            addcar_ll.setVisibility(View.GONE);
        }

        Log.i("Mainfragment", "MainFragment is resume!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(editCarBroadcastReceiver);
        getActivity().unregisterReceiver(mNetReceiver);
    }

    //初始化控件
    private void initViews() {
        inflater = LayoutInflater.from(getActivity());

        tokenSQLUtils = new TokenSQLUtils(getActivity());
        QRCode_img = (ImageView) view.findViewById(R.id.erweimashaomiao);//扫一扫
        QRCode_img.setOnClickListener(this);

        city = ((AppContext) (getActivity().getApplication())).getLocalCity().toString();
        local_city = (TextView) view.findViewById(R.id.local_city);
        if (city != null && (!city.equals(""))) {
            local_city.setText(city.toString());
        } else {
            local_city.setText("广州");
        }
        dingwei_ll = (LinearLayout) view.findViewById(R.id.dingwei_ll);//城市选择
        dingwei_ll.setOnClickListener(this);

        addcar_btn = (ImageButton) view.findViewById(R.id.addcar_btn);//添加车辆按钮
        addcar_btn.setOnClickListener(this);

        addcar_ll = (LinearLayout) view.findViewById(R.id.addcar_ll);

        add_car_img_btn = (ImageButton) view.findViewById(R.id.add_car_img_btn);
        add_car_img_btn.setOnClickListener(this);

//        network_request_error_layout = (LinearLayout) view.findViewById(R.id.network_request_error_layout);
//        reload_tv = (TextView) view.findViewById(R.id.reload_tv);
//        reload_tv.setOnClickListener(this);

        car_relativelayout = (RelativeLayout) view.findViewById(R.id.car_relativeLayout);//车辆信息viewpager
        car_viewpager = (ViewPager) view.findViewById(R.id.car_viewpager);
        carViews.add(inflater.inflate(R.layout.item_main_addcar, null));
        carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
        car_viewpager.setAdapter(carInfoAdapter);
        car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
        car_dot_ll = (LinearLayout) view.findViewById(R.id.car_dot_ll);
        ImageView dot_img = new ImageView(getActivity());
        dot_img.setImageResource(R.drawable.dot);
        dot_img.setPadding(10, 0, 0, 0);
        dot_img.setEnabled(false);
        car_dot_ll.addView(dot_img);

//        apply_credit_card_layout= (RelativeLayout) view.findViewById(R.id.apply_credit_card_layout);
//        apply_credit_card_layout.setOnClickListener(this);

//        youth_loan_layout= (RelativeLayout) view.findViewById(R.id.youth_loan_layout);
//        youth_loan_layout.setOnClickListener(this);
//
//        platinum_creditCar_layout= (RelativeLayout) view.findViewById(R.id.platinum_creditCar_layout);
//        platinum_creditCar_layout.setOnClickListener(this);

        mSlider = (SliderLayout) view.findViewById(R.id.slider);

        marqueeTv = (MarqueeTextView) view.findViewById(R.id.marqueeTv);
    }


    //初始化首页banner轮播控件
    private void initSlider(List<Banner> banners) {
        Log.i("TAG", "Banner 加载默认图片....");
        HashMap<String, String> url_maps = new HashMap<String, String>();

        if (banners != null && banners.size() != 0) {

            Banner banner;
            for (int i = 0; i < banners.size(); i++) {
                banner = banners.get(i);
                if (banner.getLink().equals("")) {
                    url_maps.put(banner.getLink() + i, banner.getImgurl() + "");
                } else {
                    url_maps.put(banner.getLink() + "", banner.getImgurl() + "");
                }
            }
        } else {
            Log.i("TAG", "Banner 加载默认图片....");
            url_maps.put("starry starry night E", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488891830037&di=559b135476cee57747c16bbe56b050d0&imgtype=0&src=http%3A%2F%2Fimg1.bitautoimg.com%2Fbitauto%2F2015%2F08%2F22%2F008ca06e-9cb3-4c0a-a51b-98fcdbf848c2_630.jpg");
            url_maps.put("starry starry night V", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488892153845&di=420437f1ea63204c7fd2cae8c412dd26&imgtype=0&src=http%3A%2F%2Fwww.bz55.com%2Fuploads%2Fallimg%2F130604%2F1-130604141155.jpg");
        }

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mSlider.addSlider(textSliderView);
        }


        mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);
        mSlider.addOnPageChangeListener(this);

        mSlider.startAutoCycle();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        String linkUrl = slider.getBundle().get("extra") + "";
        if (linkUrl != null) {
            if (linkUrl.startsWith(URLs.HTTP) || linkUrl.startsWith(URLs.HTTPS)) {
                Intent intent = new Intent(getActivity(), CommentWebActivity.class);
                intent.putExtra("url", linkUrl);
                startActivity(intent);
            }
        }
        mSlider.startAutoCycle();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
//        Toast.makeText(MainActivity.this,"Slider Demo>>>>>>>>>>>Page Changed:"+position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 初始化车辆信息控件
     */
    public void initCarViews(final List<Car> carList) {
        for (int i = 0; i < carList.size(); i++) {
            car_ll = (LinearLayout) inflater.inflate(R.layout.item_carinfo2, null);
            final String carid = carList.get(i).getCarId();
            final String carnumber = carList.get(i).getCarnumber();
            String carcode = carList.get(i).getCarcode();
            String cardrivenumber = carList.get(i).getEnginenumber();
            String remark = carList.get(i).getRemark();
            String ischeck = carList.get(i).getIsCarCorrect();
            final String proprefix = carList.get(i).getProprefix();

            final Intent intent = new Intent();
            intent.putExtra("carid", carid);
            intent.putExtra("carnumber", carnumber);
            intent.putExtra("carcode", carcode);
            intent.putExtra("cardrivenumber", cardrivenumber);
            intent.putExtra("remark", remark);
            intent.putExtra("ischeck", ischeck);
            intent.putExtra("proprefix", proprefix);
            intent.putExtra("page", carList.size() - i - 1);
            intent.putExtra("cartype", carList.get(i).getCartype());
            intent.putExtra("carname", carList.get(i).getCarname());
            intent.putExtra("carbrand", carList.get(i).getCarbrand());
            intent.putExtra("cartypename", carList.get(i).getCartypename());

            violation_ll = (LinearLayout) car_ll.findViewById(R.id.violation_ll);
//            illegal_num_tv = (TextView) car_ll.findViewById(R.id.illegal_num_tv);
            illegal_degree_tv = (TextView) car_ll.findViewById(R.id.illegal_degree_tv);
            illegal_count_tv = (TextView) car_ll.findViewById(R.id.illegal_count_tv);
            TextView carnum_tv = (TextView) car_ll.findViewById(R.id.carnum_tv);
            TextView remark_tv = (TextView) car_ll.findViewById(R.id.remark_tv);
            TextView kaisuchaxun_tv = (TextView) car_ll.findViewById(R.id.kaisuchaxun_tv);
            final int finalI = i;
            kaisuchaxun_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(isProvice(proprefix)){
//                        new AlertDialog.Builder(getActivity()).setTitle("提示")
//                                .setMessage("由于系统数据升级维护中，车牌前缀为\"" + proprefix + "\"的车辆请到主界面的其他订单页面添加并下单")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                    }
//                                }).setNegativeButton("取消", null).show().setCanceledOnTouchOutside(false);
//                    }else
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        if (!carList.get(finalI).getIsCarCorrect().equals("-1")) {
                            intent.putExtra("searchtype", 1);
                            intent.putExtra("queryUrl", URLs.QUERY);
                            intent.putExtra(IllegalQueryActivty.EXTRA_QUERY_TYPE,IllegalQueryActivty.QUERY_TYPE_FAST);
                            intent.setClass(getActivity(), IllegalQueryActivty.class);
                            startActivity(intent);
                        } else {
                            intent.setClass(getActivity(), EditCarActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
//            ImageView ischeck_img = (ImageView) car_ll.findViewById(R.id.ischeck_img);

            //本人本车查询入口
            final ImageView car_icon = (ImageView) car_ll.findViewById(R.id.car_icon);
            car_icon.setEnabled(false);
            if (!carList.get(i).getIsCarCorrect().equals("-1")) {
                for (int j = 0; j < carOpenCityList.size(); j++) {
                    if (carnumber.substring(0, 2).equals(carOpenCityList.get(j).getPrefix())) {
                        car_icon.setImageResource(R.drawable.owner_icon);
                        car_icon.setEnabled(true);
                        break;
                    }
                }
            } else {
                car_icon.setImageResource(R.drawable.xinxiwu);
                car_icon.setEnabled(false);
            }

            car_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        if (car_icon.isEnabled()) {
                            intent.putExtra("searchtype", 2);
                            intent.setClass(getActivity(), IllegalQueryActivty.class);
                            intent.putExtra(IllegalQueryActivty.EXTRA_QUERY_TYPE,IllegalQueryActivty.QUERY_TYPE_FAST);
                            intent.putExtra("queryUrl", URLs.QUERY);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "该车牌暂不支持本人本车查询", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            carnum_tv.setText(carList.get(i).getCarnumber().toUpperCase());
            carnum_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        intent.setClass(getActivity(), EditCarActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_from_left);
                    }
                }
            });
            if (remark == null || remark.equals("")) {
                remark_tv.setText("未添加车辆备注信息");
            } else {
                remark_tv.setText(carList.get(i).getRemark());
            }

            addCar(car_ll);//添加一辆车
        }
        if (carList.size() > 0) {
            addcar_ll.setVisibility(View.GONE);
            car_relativelayout.setVisibility(View.VISIBLE);
//            network_request_error_layout.setVisibility(View.GONE);
        } else {
            addcar_ll.setVisibility(View.VISIBLE);
            car_relativelayout.setVisibility(View.GONE);
//            network_request_error_layout.setVisibility(View.GONE);
        }

        if (carList.size() >= 10) {
            carViews.remove(carViews.size() - 1);
            car_dot_ll.removeViewAt(carList.size());
            setCurrentDot(0);
        }

//        UIHelper.dismissRoundProcessDialog();
    }

    /**
     * 将车辆列表json数据转化list
     */
    Car car;

    public List<Car> jsonTolist(String json) {
        carList.clear();
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject dataObj = obj.getJSONObject("data");
            JSONArray array = dataObj.getJSONArray("carlist");
//            Toast.makeText(getActivity(), "array 的长度是：" + array.length(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < array.length(); i++) {
                car = new Car();
                JSONObject childObj = array.getJSONObject(i);
                car.setCarId(childObj.getString("id"));
                car.setCarnumber(childObj.getString("proprefix") + childObj.getString("carnumber").toUpperCase());
                car.setEnginenumber(childObj.getString("cardrivenumber"));
                car.setCarcode(childObj.getString("carcode"));
                car.setRemark(childObj.getString("title"));
                car.setCarCorrect(childObj.getString("ischeck"));
                car.setProprefix(childObj.getString("proprefix"));
                car.setCarbrand(childObj.getString("carbrand"));
                car.setCarname(childObj.getString("carname"));
                car.setCartype(childObj.getString("cartype"));

                car.setCartypename(childObj.optString("cartypename"));

                carList.add(car);
            }
            return carList;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", e.toString());
        }
        return carList;
    }

    /**
     * 功能按钮
     */
    Intent intent = null;

    private void initFuncation() {
        gv_funcation = (MyGridView) view.findViewById(R.id.funcation_gv);
        String[] funcation_strArr = {"订单列表", "精准查询", "车辆年检", "违章代码"};
        funcation_types = new ArrayList<Integer>();
        funcation_types.add(R.mipmap.dingdanliebiao);//订单列表
        funcation_types.add(R.mipmap.qita);//精准查询
        funcation_types.add(R.mipmap.cheliangnianjian);//车辆年检
        funcation_types.add(R.mipmap.weizhangdaima2);//违章代吗
        funcation_type_adapter = new TypeAdapter(funcation_types, getActivity().getApplicationContext(), funcation_strArr, 1);
        gv_funcation.setAdapter(funcation_type_adapter);
        gv_funcation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0://订单列表
                        if (AppContext.isLogin) {
                            if (!UserManager.checkUserStatus()) {
                                UserManager.userActivation(getContext());
                            } else {
                                intent = new Intent(getActivity(), OrderStyleActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;

                    case 1://其他订单
//                        Joinpay.startPay(getContext(), "12","13266663863","YH000000001","11","违章充值");

                        if (AppContext.isLogin) {
                            if (!UserManager.checkUserStatus()) {
                                UserManager.userActivation(getContext());
                            } else {
                                intent = new Intent(getActivity(), AccountQueryActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }

                        break;

                    case 2://车辆年检
//                        Toast.makeText(getActivity(), "新功能开发中，即将上线，敬请期待", Toast.LENGTH_SHORT).show();
                        if (AppContext.isLogin) {
                            if (!UserManager.checkUserStatus()) {
                                UserManager.userActivation(getContext());
                            } else {
                                intent = new Intent(getActivity(), AnnualInspectionActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;

                    case 3://违章代码
//                        Toast.makeText(getActivity(),"新功能开发中，即将上线，敬请期待",Toast.LENGTH_SHORT).show();
                        if (AppContext.isLogin) {
                            intent = new Intent(getActivity(), IllegalCodeActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.in_from_right,
                                    R.anim.out_from_left);
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;
                }

                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

            }
        });
    }


    public void initCategory() {
        gv_type = (MyGridView) view.findViewById(R.id.gv_type);
        String[] type_strArr = {"信用卡申请", "高速路况", "实时路况", "今日油价", "口袋借呗", "急用贷", "中信信用卡"};
        types = new ArrayList<Integer>();
        types.add(R.drawable.xinyongkashenqing);//信用卡申请
        types.add(R.drawable.guangdonggaosu);//高速路况
        types.add(R.drawable.shishilukuang);//实时路况
        types.add(R.drawable.jinreyoujia);//今日油价
        types.add(R.drawable.koudaijiebei);//口袋借呗
        types.add(R.drawable.jiyongdai);//急用贷
        types.add(R.drawable.zhongxin);//中信
        typeAdapter = new TypeAdapter(types, getActivity().getApplicationContext(), type_strArr, 2);
        gv_type.setAdapter(typeAdapter);
        gv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0://信用卡申请
                        intent = new Intent(getActivity(), CommentWebActivity.class);
                        intent.putExtra("url", URLs.APPLY_CREDIT_CARD_URL);
                        intent.putExtra("title", "信用卡申请");
                        startActivity(intent);
                        break;
                    case 2://实时路况
                        if ((checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            //请求权限
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUESTCODE);
                            //判断是否需要 向用户解释，为什么要申请该权限
                            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION);
                        } else {
                            intent = new Intent(getActivity(), HeatMapActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 1://高速路况
//                        if (AppContext.isLogin) {
//                            intent = new Intent(getActivity(), CommentWebActivity.class);
//                            intent.putExtra("url", URLs.HIGHTWAY_STATUS);
//                            intent.putExtra("title", "高速路况");
//                            startActivity(intent);
//                        } else {
//                            UIHelper.showLoginActivity(getActivity());
//                            UIHelper.ToastMessage(getActivity(), "请先登录");
//                        }
                        intent = new Intent(getActivity(), CommentWebActivity.class);
                        intent.putExtra("url", URLs.HIGHTWAY_STATUS);
                        intent.putExtra("title", "高速路况");
                        startActivity(intent);
                        break;
                    case 3://今日油价
                        intent = new Intent(getActivity(), OilPriceAPIActivity.class);
                        startActivity(intent);
                        break;

                    case 4://口袋借呗
                        if (AppContext.isLogin) {
                            if (!UserManager.checkUserStatus()) {
                                UserManager.userActivation(getContext());
                            } else {
                                intent = new Intent(getActivity(), LoanActivity.class);
                                intent.putExtra("loanType", 1);
                                startActivity(intent);
                            }
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;

                    case 5://急用贷
                        if (AppContext.isLogin) {
                            if (!UserManager.checkUserStatus()) {
                                UserManager.userActivation(getContext());
                            } else {
                                intent = new Intent(getActivity(), LoanActivity.class);
                                intent.putExtra("loanType", 2);
                                startActivity(intent);
                            }
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;

                    case 6://中信信用卡
                        if (AppContext.isLogin) {
                            intent = new Intent(getActivity(), CommentWebActivity.class);
                            intent.putExtra("url", URLs.ZHONGXIN_CREDIT);
                            startActivity(intent);
                        } else {
                            UIHelper.showLoginActivity(getActivity());
                            UIHelper.ToastMessage(getActivity(), "请先登录");
                        }
                        break;
                }

                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
            }
        });
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("TAG", (String) msg.obj);

//            Result result = new Result((String) msg.obj);
//            Toast.makeText(DemoActivity.this, result.getResult(),
//                    Toast.LENGTH_LONG).show();
        }
    };

    //控件的点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.erweimashaomiao://扫描二维码
                if (Build.VERSION.SDK_INT >= 23) {
//                    AppOpsManager appOpsManager = (AppOpsManager)getActivity().getSystemService(Context.APP_OPS_SERVICE);
//                    int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_CAMERA, Process.myUid(),getActivity().getPackageName());
//                    if (checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        //请求拍照权限
//                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUESTCODE);
//                        //判断是否需要 向用户解释，为什么要申请该权限
//                       shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
//                    }else if(checkOp == AppOpsManager.MODE_IGNORED){
//                        // 权限被拒绝了.
//                        AskForPermission();
//                    }
//                    else{
//                        intent = new Intent(getActivity(), CaptureActivity.class);
//                        startActivity(intent);
//                    }
                    String[] perms = {//照相机权限
                            Manifest.permission.CAMERA,
                    };
                    if (EasyPermissions.hasPermissions(getActivity(), perms)) {//已有权限
                        intent = new Intent(getActivity(), CaptureActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    } else {//没有权限
                        EasyPermissions.requestPermissions(this, "为了您能够正常使用友车功能,请您赋予APP权限",
                                REQUESTCODE, perms);
                    }
                } else {
                    intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                }
                break;
            case R.id.dingwei_ll://城市选择
                intent = new Intent(getActivity(), CityActivty.class);
                startActivityForResult(intent, CITY_RESULTCODE);
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            case R.id.addcar_btn://添加车辆
                if (AppContext.isLogin) {
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        intent = new Intent(getActivity(), AddCarActivity.class);
                        startActivityForResult(intent, ADDCAR_RESULTCODE);
                        getActivity().overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_from_left);
                    }
                } else {
                    UIHelper.showLoginActivity(getActivity());
                    UIHelper.ToastMessage(getActivity(), "请先登录");
                }
                break;
            case R.id.car_ll:
                if (!UserManager.checkUserStatus()) {
                    UserManager.userActivation(getContext());
                } else {
                    intent = new Intent(getActivity(), EditCarActivity.class);
                    intent.putExtra("page", car_viewpager.getCurrentItem() + "");
                    startActivityForResult(intent, EDITCAR_RESULTCODE);
                }
                break;
            case R.id.violation_ll:
                if (!UserManager.checkUserStatus()) {
                    UserManager.userActivation(getContext());
                } else {
                    intent = new Intent(getActivity(), EditCarActivity.class);
                    intent.putExtra("page", car_viewpager.getCurrentItem() + "");
                    startActivityForResult(intent, VIOCATION_RESULTCODE);
                }

                break;
            case R.id.add_car_img_btn://跳到添加车辆页面
                if (carList.size() < 10) {
                    car_viewpager.setCurrentItem(carViews.size() - 1, true);
                } else {
                    Toast.makeText(getActivity(), "至多添加10辆车", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reload_tv:
                if (AppContext.isLogin && (carList.size() == 0 || !(car_relativelayout.getVisibility() == View.VISIBLE)) && (appContext.isNetworkConnected())) {
                    getCarList(getParams());//获取用户已添加的车辆列表
                }
                break;

//            case R.id.apply_credit_card_layout://在线申请信用卡
//                intent=new Intent(getActivity(), CommentWebActivity.class);
//                intent.putExtra("url", URLs.APPLY_CREDIT_CARD_URL);
//                intent.putExtra("title","信用卡申请");
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.in_from_right,
//                        R.anim.out_from_left);
//                break;
//
//            case R.id.youth_loan_layout://青春贷
//                intent=new Intent(getActivity(), CommentWebActivity.class);
//                intent.putExtra("url", URLs.YOUTH_LOAD_URL);
//                intent.putExtra("title","青春贷");
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.in_from_right,
//                        R.anim.out_from_left);
//                break;
//
//            case R.id.platinum_creditCar_layout://浦发银行白金信用卡
//                intent=new Intent(getActivity(), CommentWebActivity.class);
//                intent.putExtra("url", URLs.PUFA_CREDIT_CARD_URL);
//                intent.putExtra("title","浦发银行");
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.in_from_right,
//                        R.anim.out_from_left);
//                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ADDCAR_RESULTCODE:
                addcar_ll.setVisibility(View.GONE);
                car_relativelayout.setVisibility(View.VISIBLE);
//                network_request_error_layout.setVisibility(View.GONE);
                break;
            case CITY_RESULTCODE:
                city = data.getStringExtra("city") + "";
                Log.i("TAG", city + "");
                if ((city != null) && (!city.equals(""))) {
                    local_city.setText(city.toString());
                } else {
                    local_city.setText("广州");
                }
                break;
            case EDITCAR_RESULTCODE:
                break;
            case VIOCATION_RESULTCODE:
                break;
        }
    }


    /**
     * 添加车辆之后carViewpager增加一个子页
     */
    public void addCar(LinearLayout car_ll) {
        carViews.add(0, car_ll);
        ImageView dot_img = new ImageView(getActivity());
        dot_img.setImageResource(R.drawable.dot);
        dot_img.setPadding(10, 0, 10, 0);

        car_dot_ll.addView(dot_img);
        dots = new ImageView[carViews.size()];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(15));
        for (int i = 0; i < carViews.size(); i++) {
            dots[i] = (ImageView) car_dot_ll.getChildAt(i);
            dots[i].setEnabled(true);
            dots[i].setLayoutParams(params);
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
        dots[currentIndex].setLayoutParams(params);
    }

    /**
     * 点
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position == currentIndex) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);
        currentIndex = position;
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (carList.size() < 10) {
                carViews.get(carViews.size() - 1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!UserManager.checkUserStatus()) {
                            UserManager.userActivation(getContext());
                        } else {
                            Intent intent = new Intent(getActivity(), AddCarActivity.class);
                            startActivityForResult(intent, 1);
                            getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
                        }
                    }
                });
            }
            setCurrentDot(position);

            if (isQuery(position)) {
                if (position != carList.size()) {
                    qurery(queryGetParams(carList.get((carList.size() - position) - 1).getCarId()), carList.get((carList.size() - position) - 1).getCarnumber());
                    positionList.add(position);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {
        }
    }

    /**
     * 是否已经查询过违章，查询过不再查询
     */
    private boolean isQuery(int position) {
        for (int positioned : positionList) {
            if (position == positioned)
                return false;
        }
        return true;
    }


    /*
    * 获取用户已添加的车辆信息列表
    * */
    public String key = "carlist_";

    public void getCarList(HashMap<String, Object> map) {
        if (map != null && map.containsKey("token") && StringUtils.isEmpty(map.get("token").toString())) {
            Toast.makeText(getActivity(), "请重新登录！", Toast.LENGTH_LONG).show();
            return;
        }
//        UIHelper.showRoundProcessDialog(getActivity(), R.layout.loading_process_dialog_anim);
        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(URLs.GET_CAR_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "车辆列表请求到的数据为：" + EncryptUtil.decryptJson(jsonObject.toString(), getActivity()));
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), getActivity()));
                    String status = obj.getString("status");
                    if (status.equals("ok")) {
                        carList.clear();
                        carViews.clear();
                        car_dot_ll.removeAllViewsInLayout();
                        carList = jsonTolist(EncryptUtil.decryptJson(jsonObject.toString(), getActivity()));
                        strCarList = jsonObject.toString();
                        if (carList.size() > 0) {
                            carViews.add(inflater.inflate(R.layout.item_main_addcar, null));
                            ImageView dot_img = new ImageView(getContext());
                            dot_img.setImageResource(R.drawable.dot);
                            dot_img.setEnabled(false);
                            dot_img.setPadding(10, 0, 10, 0);

                            car_dot_ll.addView(dot_img);
                            initCarViews(carList);//初始化车辆信息显示的Viewpager
                            carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
                            carInfoAdapter.notifyDataSetChanged();
                            car_viewpager.setAdapter(carInfoAdapter);
                            car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
                            qurery(queryGetParams(carList.get(carList.size() - 1).getCarId()), carList.get(carList.size() - 1).getCarnumber());//查询viewpager显示的第一页的车辆的违章信息
                        }
                        HttpUtil.saveJson2FileCache(key, jsonObject.toString());//缓存车辆列表数据
                    } else {
                        carList.clear();
                        carViews.clear();
                        car_dot_ll.removeAllViewsInLayout();
                        addcar_ll.setVisibility(View.VISIBLE);
                        car_relativelayout.setVisibility(View.GONE);
//                            UIHelper.dismissRoundProcessDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                        UIHelper.dismissRoundProcessDialog();
                    Toast.makeText(getActivity(), "网络连接超时，请检查网络连接设置！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                addcar_ll.setVisibility(View.GONE);
                car_relativelayout.setVisibility(View.VISIBLE);
//                    UIHelper.dismissRoundProcessDialog();
                Toast.makeText(getActivity(), "网络请求错误，请检查网络连接设置！", Toast.LENGTH_LONG).show();
                Log.i("TAG", "车辆列表请求错误信息为：" + volleyError.toString());
            }
        });
    }


    /*
    * 查询违章
    * */
    List<Violation> violationList = new ArrayList<Violation>();

    public static void qurery(final HashMap<String, Object> map, final String carnumber) {
        if (map != null && map.containsKey("token") && StringUtils.isEmpty(map.get("token").toString())) {
            return;
        }
        VolleyUtil.getVolleyUtil(AppContext.getContext()).StringRequestPostVolley(URLs.VIOLATION_QUERY, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG", "查询车辆违章请求到的数据为：" + EncryptUtil.decryptJson(jsonObject.toString(), AppContext.getContext()));
                try {
                    JSONObject obj = new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(), AppContext.getContext()));
                    String status = obj.getString("status");
                    if (status.equals("ok")) {//查询成功
                        HashMap params = new HashMap();
                        params.put("violationList", EncryptUtil.decryptJson(jsonObject.toString(), AppContext.getContext()));
                        params.put("carid", map.get("carid"));
                        params.put("carnumber", carnumber);//车牌号码
                        Task ts = new Task(TaskType.TS_REFLUSH_VIALATION, params);//编辑提交成功后更新首页车辆信息中的违章信息数据
                        MainService.newTask(ts);
                    } else if (status.equals("fail")) {//查询失败

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(AppContext.getContext(), "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "查询车辆违章请求错误信息为：" + volleyError.toString());
            }
        });
    }

    /*
   * 保存和查询请求参数
   * */
    private TokenSQLUtils tokenSQLUtils;

    public HashMap<String, Object> getParams() {
        String token = TokenSQLUtils.check();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("token", token);
//        Log.w("TAG","token"+ token);
        return map;
    }


    /**
     * 查询违章参数
     */
    public static HashMap<String, Object> queryGetParams(String carid) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token", token);
        map.put("carid", carid);
        return map;
    }


    /**
     * 添加，编辑，修改车辆的广播接收者
     */
    class ADD_DELETE_EditCarBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Car car = new Car();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            LinearLayout car_ll = (LinearLayout) inflater.inflate(R.layout.item_carinfo2, null);
            LinearLayout editcar_ll = (LinearLayout) car_ll.findViewById(R.id.car_ll);
            editcar_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            TextView carnum_tv = (TextView) car_ll.findViewById(R.id.carnum_tv);
            TextView remark_tv = (TextView) car_ll.findViewById(R.id.remark_tv);
//            ImageView ischeck_img = (ImageView) car_ll.findViewById(R.id.ischeck_img);
            final ImageView car_icon = (ImageView) car_ll.findViewById(R.id.car_icon);
//            TextView illegal_num_tv = (TextView) car_ll.findViewById(R.id.illegal_num_tv);
            TextView illegal_degree_tv = (TextView) car_ll.findViewById(R.id.illegal_degree_tv);
            TextView illegal_count_tv = (TextView) car_ll.findViewById(R.id.illegal_count_tv);
            TextView kaisuchaxun_tv = (TextView) car_ll.findViewById(R.id.kaisuchaxun_tv);


            String carid = intent.getStringExtra("carid");
            String carnumber = intent.getStringExtra("carnumber");
            String carcode = intent.getStringExtra("carcode");
            final String proprefix = intent.getStringExtra("proprefix");
            String remark = intent.getStringExtra("title");
            String carengine = intent.getStringExtra("carengine");
            final String ischeck = intent.getStringExtra("ischeck");
            String type = intent.getStringExtra("type");
            String violationSize = intent.getIntExtra("violationSize", 0) + "";
            int page = intent.getIntExtra("page", 0);
            String cartype = intent.getStringExtra("cartype");
            String carname = intent.getStringExtra("carname");
            String carbrand = intent.getStringExtra("carbrand");

            String totalDgree = intent.getStringExtra("totalDgree");
            if (totalDgree == null) totalDgree = "0";
            String totalCount = intent.getStringExtra("totalCount");
            if (totalCount == null) totalCount = "0";

            String cartypename = intent.getStringExtra("cartypename");

            final Intent edit_intent = new Intent();
            edit_intent.putExtra("carid", carid);
            edit_intent.putExtra("carnumber", proprefix + carnumber);
            edit_intent.putExtra("carcode", carcode);
            edit_intent.putExtra("cardrivenumber", carengine);
            edit_intent.putExtra("remark", remark);
            edit_intent.putExtra("ischeck", ischeck);
            edit_intent.putExtra("proprefix", proprefix);
            edit_intent.putExtra("page", car_viewpager.getCurrentItem());
            edit_intent.putExtra("cartype", cartype);
            edit_intent.putExtra("carname", carname);
            edit_intent.putExtra("carbrand", carbrand);
            edit_intent.putExtra("cartypename", cartypename);


            car_icon.setEnabled(false);
            if (ischeck != null && !ischeck.equals("-1")) {
                for (int j = 0; j < carOpenCityList.size(); j++) {
                    if ((proprefix + carnumber).substring(0, 2).equals(carOpenCityList.get(j).getPrefix())) {
                        car_icon.setImageResource(R.drawable.owner_icon);
                        car_icon.setEnabled(true);
                        break;
                    }
                }
            } else {
                car_icon.setImageResource(R.drawable.xinxiwu);
                car_icon.setEnabled(false);
            }

            car_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        if (car_icon.isEnabled()) {
                            edit_intent.putExtra("searchtype", 2);
                            edit_intent.putExtra("queryUrl", URLs.QUERY);
                            edit_intent.setClass(getActivity(), IllegalQueryActivty.class);
                            edit_intent.putExtra(IllegalQueryActivty.EXTRA_QUERY_TYPE,IllegalQueryActivty.QUERY_TYPE_FAST);
                            startActivity(edit_intent);
                        } else {
                            Toast.makeText(getActivity(), "该车牌暂不支持本人本车查询", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            //快速查詢
            kaisuchaxun_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(isProvice(proprefix)){
//                        new AlertDialog.Builder(getActivity()).setTitle("提示")
//                                .setMessage("由于系统数据升级维护中，车牌前缀为\"" + proprefix + "\"的车辆请到主界面的其他订单页面添加并下单")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                    }
//                                }).setNegativeButton("取消", null).show().setCanceledOnTouchOutside(false);
//                    }else
                    if (!UserManager.checkUserStatus()) {
                        UserManager.userActivation(getContext());
                    } else {
                        if (ischeck != null && !ischeck.equals("-1")) {
                            edit_intent.putExtra("searchtype", 1);
                            edit_intent.putExtra("queryUrl", URLs.QUERY);
                            edit_intent.setClass(getActivity(), IllegalQueryActivty.class);
                            edit_intent.putExtra(IllegalQueryActivty.EXTRA_QUERY_TYPE,IllegalQueryActivty.QUERY_TYPE_FAST);
                            if (!(getActivity() instanceof Activity)) {
                                edit_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(edit_intent);
                        } else {
                            edit_intent.setClass(getActivity(), EditCarActivity.class);
                            startActivity(edit_intent);
                        }
                    }
                }
            });

            //编辑车辆成功后
            if (type.equals("1")) {
                for (int i = 0; i < carList.size(); i++) {
                    if (carid.equals(carList.get(i).getCarId())) {
                        carList.get(i).setCarcode(carcode);
                        carList.get(i).setEnginenumber(carengine);
                        carList.get(i).setCarnumber((proprefix + carnumber).toUpperCase());
                        carList.get(i).setProprefix(proprefix);
                        carList.get(i).setRemark(remark + "");
                        carList.get(i).setCarCorrect(ischeck);
                        carList.get(i).setCarbrand(carbrand);
                        carList.get(i).setCartype(cartype);
                        carList.get(i).setCarname(carname);
                        carList.get(i).setCartypename(cartypename);

                        carnum_tv.setText(carList.get(i).getCarnumber());
                        carnum_tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!UserManager.checkUserStatus()) {
                                    UserManager.userActivation(getContext());
                                } else {
                                    edit_intent.setClass(getActivity(), EditCarActivity.class);
                                    startActivity(edit_intent);
                                }
                            }
                        });
                        remark_tv.setText(carList.get(i).getRemark());
//                        illegal_num_tv.setText(violationSize);
                        illegal_degree_tv.setText("扣分" + totalDgree + " 分");
                        illegal_count_tv.setText("罚款" + totalCount + " 元");

                        carViews.set((carList.size() - i - 1), car_ll);
//                        setCurrentDot(0);
                    }
                }
            }

            //添加新车辆
            if (type.equals("0")) {
                car.setCarId(carid);
                car.setProprefix(proprefix);
                car.setCarCorrect(ischeck);
                car.setCarnumber((proprefix + carnumber).toUpperCase());
                car.setEnginenumber(carengine);
                car.setCarcode(carcode);
                car.setCarname(carname);
                car.setCartype(cartype);
                car.setCarbrand(carbrand);
                car.setCartypename(cartypename);
                carList.add(car);
                if (carList.size() == 1) {
                    carViews.add(inflater.inflate(R.layout.item_main_addcar, null));
                    ImageView dot_img = new ImageView(getActivity());
                    dot_img.setImageResource(R.drawable.dot);
                    dot_img.setPadding(10, 0, 10, 0);
                    car_dot_ll.addView(dot_img);
                }
                carnum_tv.setText(proprefix + carnumber);
                carnum_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!UserManager.checkUserStatus()) {
                            UserManager.userActivation(getContext());
                        } else {
                            edit_intent.setClass(getActivity(), EditCarActivity.class);
                            startActivity(edit_intent);
                        }
                    }
                });
                if (remark == null || remark.equals("")) {
                    remark_tv.setText("未添加车辆备注");
                } else {
                    remark_tv.setText(remark + "");
                }
//                illegal_num_tv.setText(violationSize);
                illegal_degree_tv.setText("扣分" + totalDgree + "分");
                illegal_count_tv.setText("罚款" + totalCount + "元");
                if (ischeck != null && !ischeck.equals("-1")) {
                    car_icon.setVisibility(View.VISIBLE);
                } else {
                    car_icon.setImageResource(R.drawable.xinxiwu);
                }
                addCar(car_ll);

                if (carList.size() >= 10) {
                    carViews.remove(carViews.size() - 1);
                    car_dot_ll.removeViewAt(carList.size());
                    setCurrentDot(0);
                }
            } else if (type.equals("2")) {//删除车辆
                for (int i = 0; i < carList.size(); i++) {
                    if (carid.equals(carList.get(i).getCarId())) {
                        carList.remove(i);
                        carViews.remove(carList.size() - i);
                        car_dot_ll.removeViewAt(carList.size());
                        setCurrentDot(0);
                    }
                }

                if (carList.size() == 0) {
                    carViews.remove(carViews.size() - 1);
                    if (car_dot_ll.getChildCount() > 0)
                        car_dot_ll.removeViewAt(carList.size());
                    setCurrentDot(0);
                    car_relativelayout.setVisibility(View.GONE);
                    addcar_ll.setVisibility(View.VISIBLE);
                }

                if (carList.size() == 9) {//如果添加车辆少于10辆可添加车辆
                    carViews.add(inflater.inflate(R.layout.item_main_addcar, null));
                    ImageView dot_img = new ImageView(getActivity());
                    dot_img.setImageResource(R.drawable.dot);
                    dot_img.setPadding(10, 0, 10, 0);
                    car_dot_ll.addView(dot_img);
                }
            }

            carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
            carInfoAdapter.notifyDataSetChanged();
            car_viewpager.setAdapter(carInfoAdapter);
            if (page > 0) {
                car_viewpager.setCurrentItem(page);
                setCurrentDot(page);
            }
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];
        switch (type) {
            case TaskType.TS_REFLUSH_HOME_PAGE://登录成功后刷新首页数据
                getCarList(getParams());
                car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
                if (carList.size() == 0) {
                    car_relativelayout.setVisibility(View.GONE);
                    addcar_ll.setVisibility(View.VISIBLE);
                } else {
                    car_relativelayout.setVisibility(View.VISIBLE);
                    addcar_ll.setVisibility(View.GONE);
                }
//                UIHelper.dismissRoundProcessDialog();
                break;

            case TaskType.TS_USER_EXIT://退出登录
                Log.i("TAG", "退出登录fffffffff");
                car_relativelayout.setVisibility(View.GONE);
                if (appContext.isNetworkConnected()) {
                    addcar_ll.setVisibility(View.VISIBLE);
//                    network_request_error_layout.setVisibility(View.GONE);
                } else {
                    addcar_ll.setVisibility(View.GONE);
//                    network_request_error_layout.setVisibility(View.VISIBLE);
                }
                carList.clear();
                carViews.clear();
                MainFragment.positionList.clear();
                break;

            case TaskType.TS_REFLUSH_VIALATION://刷新车辆信息中的违章信息
                Log.i("TAG", "刷新车辆信息中的违章信息");
                HashMap map = new HashMap();
                if (param[1] != null) {
                    map = (HashMap) param[1];
                }
                String totalDgree = map.get("totalDgree") + "";
                String totalCount = map.get("totalCount") + "";
                String carId;
                String carnumber;
                if (map != null || map.size() != 0) {
                    for (int i = 0; i < carList.size(); i++) {
                        carId = (String) map.get("carid");
                        carnumber = (String) map.get("carnumber");
                        if (carId != null && (carId.equals(carList.get(i).getCarId()))) {
                            View view = carViews.get(carList.size() - 1 - i);
                            TextView carnum_tv = (TextView) view.findViewById(R.id.carnum_tv);
                            carnum_tv.setText(carList.get(i).getCarnumber());
                            TextView remark_tv = (TextView) view.findViewById(R.id.remark_tv);
                            if (carList.get(i).getRemark() == null || carList.get(i).getRemark().equals("")) {
                                remark_tv.setText("未添加车辆备注");
                            } else {
                                remark_tv.setText(carList.get(i).getRemark());
                            }

                            final ImageView car_icon = (ImageView) view.findViewById(R.id.car_icon);
                            car_icon.setEnabled(false);
                            if (!carList.get(i).getIsCarCorrect().equals("-1")) {
                                for (int j = 0; j < carOpenCityList.size(); j++) {
                                    if (carnumber.substring(0, 2).equals(carOpenCityList.get(j).getPrefix())) {
                                        car_icon.setImageResource(R.drawable.owner_icon);
                                        car_icon.setEnabled(true);
                                        break;
                                    }
                                }
                            } else {
                                car_icon.setImageResource(R.drawable.xinxiwu);
                                car_icon.setClickable(false);
                            }

                            TextView illegal_degree_tv = (TextView) view.findViewById(R.id.illegal_degree_tv);
                            illegal_degree_tv.setText("扣分" + totalDgree + "分");
                            TextView illegal_count_tv = (TextView) view.findViewById(R.id.illegal_count_tv);
                            illegal_count_tv.setText("罚款" + totalCount + "元");
                            carViews.set(carList.size() - i - 1, view);
                            carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
                            carInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;

            case TaskType.TS_CAR_OPEN_CITYS://本人本车开放城市列表
                break;

            case TaskType.TS_GET_BANNER://Banner轮播图
                if (param[1] != null) {
                    initSlider((List<Banner>) param[1]);
                }
                break;

            case TaskType.TS_GET_NOTICE_INFO://首页公告栏
                if (param[1] != null) {
                    marqueeTv.setTextArraysAndClickListener((List<Bulletin>) param[1], new MarqueeTextViewClickListener() {
                        @Override
                        public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this,AnotherActivity.class));
                        }
                    });
                }
                break;
        }
    }


    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                if (isConnected) {
                    getCarList(getParams());//获取用户已添加的车辆列表
                    car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
                    MainFragment.positionList.clear();
                    MainFragment.isReflush = false;
                } else {
                    if (carList.isEmpty()) {
                        String carListData = HttpUtil.LoadDataFromLocal(key);
                        if (carListData == null) {
                            UIHelper.ToastMessage(getActivity(), "网络连接失败，请检查网络连接设置");
                            car_relativelayout.setVisibility(View.GONE);
                            addcar_ll.setVisibility(View.VISIBLE);
                        } else {
                            carList.clear();
//                            carViews.clear();
//                            car_dot_ll.removeAllViewsInLayout();
                            carList = jsonTolist(carListData);
                            initCarViews(carList);
                            carInfoAdapter = new CarInfoAdapter(carViews, getActivity(), carList);
                            car_viewpager.setAdapter(carInfoAdapter);
                            car_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
                        }
                    }
                    Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //拒绝
        AskForPermission();
    }


    //询问设置权限
    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("帮助");
        builder.setMessage("您还未授予应用所必需的定位权限。\n\n请点击\\\"设置\\\"-\\\"权限控制\\\"-允许获取照相机权限");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }

        });
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return false;
            }
        });
        builder.create().show();
    }
}
