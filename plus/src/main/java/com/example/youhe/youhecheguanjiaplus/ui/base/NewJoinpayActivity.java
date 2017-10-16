package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamicode.p27.companyyh.bluetooth4.DcBleDevice;
import com.dynamicode.p27.companyyh.inter.CDCSwiperController;
import com.dynamicode.p27.companyyh.inter.DCSwiper;
import com.dynamicode.p27.companyyh.inter.DCSwiperControllerListener;
import com.dynamicode.p27.companyyh.inter.IDCSwiper;
import com.dynamicode.p27.companyyh.util.DCCharUtils;
import com.dynamicode.p27.companyyh.util.DcConstant;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.DeviceListAdapter;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.SerMap;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class NewJoinpayActivity extends YeoheActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "NewJoinpayActivity";

    private String price="",theSerialNumber="",totalDegree="",ordercode="";
    private int orderstyle;//订单类型

    private String order_type=""; //订单类型 1违章2年检3车主卡
    private String info;//收款方
    private TextView shop_info_tv,zongjine_tv,info_tv;

    private LinearLayout item_pay_choose_device,item_pay_swiping_card,item_pay_enter_psw;

    private ProgressBar scan_progress;

    private ImageButton new_pay_back_ib,research_ib;

    private RelativeLayout main_pro_linear;
    private static String m_deviceAddress = "";

    private ListView devicemanager_listv;
    private DeviceListAdapter adapter;
    private List<DcBleDevice> list = new ArrayList<DcBleDevice>();
    private BluetoothAdapter ba = null;
    private boolean isScanning;
    private boolean isBlueInfoShow;

    public static final String EXTRA_ORDER_TYPE="order_type";

    private DcBleDevice m_devive;
    private IDCSwiper m_dcswiperController = null;
    public static final String EXTRA_CUSTOMER_BUNDLE = "customer_bundle";
    private Bundle customerBundle=null;

    private DCSwiperControllerListener listener = new DCSwiperControllerListener() {
        @Override
        public void onDeviceScanning() {
            Log.d(TAG, "onDeviceScanning....");
        }

        @Override
        public void onDeviceScanStopped() {
            Log.d(TAG, "onDeviceScanStopped....");
            isScanning = false;

            if (scan_progress.isShown()) {
                scan_progress.setVisibility(View.GONE);
            }

        }

        @Override
        public void onDeviceListRefresh(List<DcBleDevice> devices) {

            Log.d(TAG, "onDeviceListRefresh....");
            list.clear();

            for (DcBleDevice t_device : devices) {
                DCCharUtils.showLogE(TAG, "address::" + t_device.getAddress());
                list.add(t_device);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDeviceConnected() {

            Log.d(TAG, "onDeviceConnected....");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(DcConstant.STATE_CONNECT_SUCCESS);
            }
        }

        @Override
        public void onDeviceConnectedFailed() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onDeviceConnectedFailed....");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(DcConstant.STATE_CONNECT_FAIL);
            }
        }

        @Override
        public void onDeviceDisconnected() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onDeviceDisconnected....");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(DcConstant.STATE_DISCONNECTED);
            }
        }

        @Override
        public void onWaitingForDevice() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onWaitingForDevice....");
        }

        @Override
        public void onReturnDeviceInfo(Map<String, String> deviceInfos) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onReturnDeviceInfo....") ;
        }

        @Override
        public void onWaitingForCardSwipe() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onWaitingForCardSwipe....");
        }

        @Override
        public void onReturnCardInfo(Map<String, String> cardInfos) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onReturnCardInfo....");
            // TODO Auto-generated method stub
            Log.d(TAG, "onReturnCardInfo....");
//            statusEditText.setText(statusEditText.getText() + "\n" + "onReturnCardInfo....");
            String isIC = cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_ICCARDFLAG);

            //isIC.equals("1") ||
            if (isIC != null && ( isIC.equals("2"))) {
//                statusEditText.setText(statusEditText.getText() + "\n" + "当前为IC卡");

                map.put("cardtype","1");//IC卡

                //1-ICCard  2-磁条卡

//                statusEditText.setText(statusEditText.getText() + "\n" + "CardNumber: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CARDNUMBER));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track2length: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2LENGTH));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track2: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track3length: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3LENGTH));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track3: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3));
//                statusEditText.setText(statusEditText.getText() + "\n" + "needpin: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_NEEDPIN));
//                statusEditText.setText(statusEditText.getText() + "\n" + "icdata: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_ICDATA));
//                statusEditText.setText(statusEditText.getText() + "\n" + "CRDSQN: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CRDSQN));
//                statusEditText.setText(statusEditText.getText() + "\n" + "expired: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_EXPIRED));
//
            } else if(isIC != null && ( isIC.equals("1"))){
                //1-ICCard  2-磁条卡

                map.put("cardtype","2");//磁条卡

//                statusEditText.setText(statusEditText.getText() + "\n" + "当前为磁条卡");
//                statusEditText.setText(statusEditText.getText() + "\n" + "CardNumber: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CARDNUMBER));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track2length: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2LENGTH));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track2: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track3length: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3LENGTH));
//                statusEditText.setText(statusEditText.getText() + "\n" + "track3: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3));
//                statusEditText.setText(statusEditText.getText() + "\n" + "needpin: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_NEEDPIN));
//                statusEditText.setText(statusEditText.getText() + "\n" + "expired: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_EXPIRED));
//                statusEditText.setText(statusEditText.getText() + "\n" + "random: "
//                        + cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_RANDOM));
            }else{
                map.put("cardtype","3");//非接
            }

            {
                map.put("mainaccount",cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CARDNUMBER)+"");//主账号
                if(cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CRDSQN)!=null) {
                    String crdsqn=cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CRDSQN);
                    map.put("cardserialnumber",crdsqn);//卡序列号
                }
                if(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_ICDATA!=null) {
                    map.put("iccardinfo", cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_ICDATA)+"");//IC卡数据域
                }
                map.put("trackInfo2", cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2)+"");//二磁道数据
                if(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3!=null){
                    map.put("trackInfo3", cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK3)+"");//三磁道数据
                }

                map.put("cardvalidity",cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_EXPIRED)+"");//卡的有效期
            }

            StringBuilder sb=new StringBuilder();
//            sb.append("cardtype:"+"2\n"
//                    +"mainaccount" +cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_CARDNUMBER)+"\n"
//                    +"cardserialnumber"+"01\n"
//                    +"iccardinfo"+cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_ICDATA)+"\n"
//                    +"trackInfo2"+cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_TRACK2)+"\n"
//                    +"paymoney"+money_et.getText().toString().trim()+"\n"
//                    +"cardvalidity"+cardInfos.get(CDCSwiperController.DCSWIPER_RETURN_MAP_KEY_EXPIRED)+"\n"
//            );

            Log.i(TAG,sb.toString());

//            statusEditText.setText(statusEditText.getText() + "\n" + "onReturnCardInfo..end.");
            m_dcswiperController.getPinBlock(30);

            item_pay_swiping_card.setVisibility(View.GONE);
            item_pay_enter_psw.setVisibility(View.VISIBLE);

        }

        @Override
        public void onTimeout() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onTimeout....");
        }

        @Override
        public void onError(int errorCode) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onError....");
        }

        @Override
        public void onNeedInsertICCard() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onNeedInsertICCard....");
        }

        @Override
        public void onDetectedCard() {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            Log.d(TAG, "onDetectedCard....");
        }

        @Override
        public void onReturnPinBlock(String pinBlock) {
            // TODO Auto-generated method stub
            map.put("passwordpin", ParamSign.getUserPassword(pinBlock));

            Intent intent=new Intent(NewJoinpayActivity.this,SignInActivity.class);
            //让hashmap实现可序列化,定义一个实现可序列化的类。
            SerMap serMap=new SerMap();
            serMap.setMap(getParamter());
            //创建Bundle对象，存放实现可序列化的SerMap
            Bundle bundle=new Bundle();
            bundle.putSerializable("serMap",serMap);
            //意图放置bundle变量
            intent.putExtras(bundle);
            Log.d("TAG","aaaaaaaaaaaa"+order_type);
            intent.putExtra(SignInActivity.EXTRA_ORDER_TYPE,order_type);
            if (customerBundle!=null)
                intent.putExtra(SignInActivity.EXTRA_CUSTOMER_BUNDLE,customerBundle);

            startActivity(intent);
            NewJoinpayActivity.this.finish();
        }


        @Override
        public void onPressCancelKey() {
            // TODO Auto-generated method stub
        }

    };

    @SuppressLint({"HandlerLeak", "HandlerLeak"})
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DcConstant.STATE_CONNECT_SUCCESS:
                    showProgressDialog(false);
                    Toast.makeText(NewJoinpayActivity.this, R.string.toast_connect_success, Toast.LENGTH_LONG).show();
                    item_pay_choose_device.setVisibility(View.GONE);
                    item_pay_swiping_card.setVisibility(View.VISIBLE);
                    m_dcswiperController.setSwiperParameters(1, new Integer(2));
                    m_dcswiperController.startSwiper(price, 60);//开始刷卡
                    break;
                case DcConstant.STATE_CONNECT_FAIL:
                    showProgressDialog(false);
                    Toast.makeText(NewJoinpayActivity.this, R.string.toast_connect_fail, Toast.LENGTH_LONG).show();
                    break;
                case DcConstant.STATE_NO_CONNECTED:
                    Toast.makeText(NewJoinpayActivity.this, R.string.toast_no_connected, Toast.LENGTH_LONG).show();
                    break;
                case DcConstant.STATE_DISCONNECTED:
                    isBlueInfoShow = false;
                    Toast.makeText(NewJoinpayActivity.this, R.string.toast_device_disconnect, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    /**
     * 隐藏配对等待对话框
     */
    private void showProgressDialog(boolean isShow) {
        if (isShow) {
            if (!main_pro_linear.isShown()) {
                main_pro_linear.setVisibility(View.VISIBLE);
            }
        } else {
            if (main_pro_linear.isShown()) {
                main_pro_linear.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_joinpay);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,NewJoinpayActivity.this);
        }
        SystemBarUtil.useSystemBarTint(NewJoinpayActivity.this);

        Intent extraIntent=getIntent();

        if (extraIntent.hasExtra(EXTRA_ORDER_TYPE))
            customerBundle=extraIntent.getBundleExtra(EXTRA_ORDER_TYPE);

        price=extraIntent.getStringExtra("price");//支付金额
        theSerialNumber=extraIntent.getStringExtra("theSerialNumber");//机器序列号
        totalDegree=extraIntent.getStringExtra("totalDegree")+"";//总扣分
        ordercode=extraIntent.getStringExtra("ordercode")+"";
        orderstyle=extraIntent.getIntExtra("orderstyle",orderstyle);
        order_type=extraIntent.getStringExtra(EXTRA_ORDER_TYPE);
        Log.d("TAG",order_type+"");
        info=extraIntent.getStringExtra("info");

        ba = BluetoothAdapter.getDefaultAdapter();

        findViews();
        initView();

    }


    /**
     * 通过ID查找所有控件
     */
    private void findViews(){
        main_pro_linear = (RelativeLayout) findViewById(R.id.main_pro_linear);
        devicemanager_listv= (ListView) findViewById(R.id.device_lv);

        new_pay_back_ib= (ImageButton) findViewById(R.id.new_pay_back_ib);
        new_pay_back_ib.setOnClickListener(this);

        item_pay_choose_device= (LinearLayout) findViewById(R.id.item_pay_choose_device);
        item_pay_swiping_card= (LinearLayout) findViewById(R.id.item_pay_swiping_card);
        item_pay_enter_psw= (LinearLayout) findViewById(R.id.item_pay_enter_psw);

        research_ib= (ImageButton) findViewById(R.id.research_ib);
        research_ib.setOnClickListener(this);

        scan_progress = (ProgressBar) findViewById(R.id.scan_progress);

        shop_info_tv= (TextView) findViewById(R.id.shop_info_tv);
        zongjine_tv= (TextView) findViewById(R.id.zongjine_tv);

        info_tv=(TextView)findViewById(R.id.info_tv);

    }

    /**
     * 初始化所有控件
     * */
    private void initView(){

        adapter = new DeviceListAdapter(list, NewJoinpayActivity.this);
        devicemanager_listv.setAdapter(adapter);
        devicemanager_listv.setOnItemClickListener(this);

        shop_info_tv.setText(totalDegree);
        zongjine_tv.setText("￥"+price);
        zongjine_tv.setTextColor(Color.RED);

        info_tv.setText(info+"");

        scan_progress.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DCCharUtils.showLog(TAG, "onResume...");

        item_pay_choose_device.setVisibility(View.VISIBLE);
        item_pay_enter_psw.setVisibility(View.GONE);
        item_pay_swiping_card.setVisibility(View.GONE);

        m_dcswiperController = DCSwiper.getInstance(getApplication());
        m_dcswiperController.setM_swiperControllerListener(listener);
        if (ba == null) {
            Toast.makeText(getApplicationContext(), "没有找到蓝牙设备", Toast.LENGTH_LONG).show();
            return;
        }
        if (ba.isEnabled()) {
            showProgressDialog(false);

            if (!isScanning) {// 如果没有扫描，则开始扫描
                isScanning = true;
                if (!scan_progress.isShown()) {
                    scan_progress.setVisibility(View.VISIBLE);
                }
                m_dcswiperController.startScan(null, 12);
            }

        } else {
            if (!isBlueInfoShow) {
                isBlueInfoShow = true;
                DCCharUtils.showLogD(TAG, "请先打开蓝牙...");
                Toast.makeText(getApplicationContext(), "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(intent);
            }
        }
        // changeList();
        // adapter.notifyDataSetChanged();

        DCCharUtils.showLogD(TAG, m_dcswiperController.isConnected() + "");
        m_dcswiperController.setM_swiperControllerListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCCharUtils.showLog(TAG, "onPause...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DCCharUtils.showLog(TAG, "onStop...");
        isBlueInfoShow = false;
        if (isScanning) {
            isScanning = false;
            if (scan_progress.isShown()) {
                scan_progress.setVisibility(View.GONE);
            }
            m_dcswiperController.stopScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DCCharUtils.showLog(TAG, "onDestroy...");
        m_dcswiperController.disconnectDevice();//断开连接
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.new_pay_back_ib:
                finish();
                break;

            case R.id.research_ib:
                if (!isScanning) {// 如果没有扫描，则开始扫描
                    list.clear();
                    adapter.notifyDataSetChanged();
                    isScanning = true;

                    if (!scan_progress.isShown()) {
                        scan_progress.setVisibility(View.VISIBLE);
                    }

                    m_dcswiperController.startScan(null, 12);
                } else {// 停止扫描
                    isScanning = false;
                    if (scan_progress.isShown()) {
                        scan_progress.setVisibility(View.GONE);
                    }
                    m_dcswiperController.stopScan();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (main_pro_linear.isShown()) {
            return;
        }
        showProgressDialog(true);
        m_devive = list.get(i);
        m_dcswiperController.connectDevice(m_devive.getAddress(), 20);
        m_deviceAddress=m_devive.getAddress();
//        Toast.makeText(NewJoinpayActivity.this,"m_deviceAddress:"+m_devive.getAddress(),Toast.LENGTH_LONG).show();
    }


    HashMap map=new HashMap();
    private HashMap getParamter(){
        map.put("token", TokenSQLUtils.check());//Token值
        map.put("poscode",theSerialNumber);//违章机序列号
        map.put("ordercode",ordercode);//订单编号
        map.put("paymoney",price);//支付金额（）
        map.put(EXTRA_ORDER_TYPE,order_type);
        if(orderstyle==3) {//年检订单传
            map.put("is_annual_inspection", 1);
        }
        return map;
    }



    @Override
    public void init() {
    }

    @Override
    public void refresh(Object... param) {
    }

}
