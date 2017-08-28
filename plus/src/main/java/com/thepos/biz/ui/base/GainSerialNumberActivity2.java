package com.thepos.biz.ui.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.dynamicode.p27.companyyh.bluetooth4.DcBleDevice;
import com.dynamicode.p27.companyyh.inter.DCSwiper;
import com.dynamicode.p27.companyyh.inter.DCSwiperControllerListener;
import com.dynamicode.p27.companyyh.inter.IDCSwiper;
import com.dynamicode.p27.companyyh.util.DCCharUtils;
import com.dynamicode.p27.companyyh.util.DcConstant;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.thepos.biz.adapter.DeviceListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取POS机设备序列号
 */
public class GainSerialNumberActivity2 extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "WU";
    private ImageButton next;
    private ListView devicemanager_listv;
    private DeviceListAdapter adapter;
    private List<DcBleDevice> list = new ArrayList<DcBleDevice>();
    private BluetoothAdapter ba = null;
    private boolean isScanning;
    private boolean isBlueInfoShow;
    private String text;//TERMINALSN
    private DcBleDevice m_devive;
    private IDCSwiper m_dcswiperController = null;
    private Button shouxi;
    private String test;//序列号
    private SaveNameDao saveNameDao;

    private final String POSCODE_ACTION = "POSCODE_ACTION_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theserial_numberctivity);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, GainSerialNumberActivity2.this);
        }
        SystemBarUtil.useSystemBarTint(GainSerialNumberActivity2.this);

        in();
    }

    private void in() {
        mayRequestLocation();//android 6.0蓝牙权限申请
        unpDol();
        saveNameDao = new SaveNameDao(this);
        adapter = new DeviceListAdapter();
        devicemanager_listv = (ListView) findViewById(R.id.devicename_listview);
        devicemanager_listv.setAdapter(adapter);
        devicemanager_listv.setOnItemClickListener(this);
        next = (ImageButton) findViewById(R.id.search_bt);
        next.setOnClickListener(this);
        ba = BluetoothAdapter.getDefaultAdapter();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventMainThread(FirstEvent event) {

    }

    private DCSwiperControllerListener listener = new DCSwiperControllerListener() {

        @Override
        public void onDeviceScanning() {//扫描中
            Log.d(TAG, "onDeviceScanning....");
            progressDialog.show();
        }

        @Override
        public void onDeviceScanStopped() {//扫描结束
            Log.d(TAG, "onDeviceScanStopped....");
            progressDialog.hide();
        }

        @Override
        public void onDeviceListRefresh(List<DcBleDevice> devices) {//返回当前设备扫描列表。

            list.clear();
            Log.i("WU", "onDeviceListRefresh" + devices.size());
            for (DcBleDevice t_device : devices) {
                list.add(t_device);
            }
            adapter.delete();
            adapter.addDate(list);
            adapter.notifyDataSetInvalidated();
        }

        @Override
        public void onDeviceConnected() {//连接成功


            if (mHandler != null) {
                if (m_dcswiperController.isConnected() == false) {
                    mHandler.sendEmptyMessage(DcConstant.STATE_CONNECT_SUCCESS);
                }
            }

            for (int i = 0; i < devicemanager_listv.getCount(); i++) {
                devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.GONE);
                if (postion == i) {
                    devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.VISIBLE);
                }

            }
            if (m_dcswiperController.isConnected()) {
                m_dcswiperController.getDeviceInfo();
//                saveNameDao.writeTxtToFile(test,"poscode.txt");//保存POS机序列号
                m_dcswiperController.disconnectDevice();//断开蓝牙
                EventBus.getDefault().post(new FirstEvent(test));
                Intent posIntent = new Intent();
                posIntent.putExtra("poscode", test);
                setResult(110, posIntent);
//                Toast.makeText(GainSerialNumberActivity2.this, "获取到的序列号"+test, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                ToastUtil.getLongToastByString(GainSerialNumberActivity2.this, "目前还没有连接设备");
            }
            progressDialog2.hide();

        }

        @Override
        public void onDeviceConnectedFailed() {//连接失败
            // TODO Auto-generated method stub
            Log.d(TAG, "onDeviceConnectedFailed....");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(DcConstant.STATE_CONNECT_FAIL);
            }
            for (int i = 0; i < devicemanager_listv.getCount(); i++) {
                devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.GONE);
                if (postion == i) {
                    devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.GONE);
                }
            }
            Toast.makeText(GainSerialNumberActivity2.this, "连接失败", Toast.LENGTH_SHORT).show();
            progressDialog2.hide();
        }

        @Override
        public void onDeviceDisconnected() {//设备断开
            // TODO Auto-generated method stub
            Log.i("WU", "onDeviceDisconnected....");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(DcConstant.STATE_DISCONNECTED);
                ToastUtil.getLongToastByString(GainSerialNumberActivity2.this, "设备断开");
            }
        }

        @Override
        public void onWaitingForDevice() {//等待插入刷卡器 设备未插入时启动刷卡器会得到这个事件通知
            // TODO Auto-generated method stub
            Log.d(TAG, "onWaitingForDevice....");
        }

        @Override
        public void onReturnDeviceInfo(Map<String, String> deviceInfos) {//返回设备信息
            // TODO Auto-generated method stub
            Log.d("WU", "onReturnDeviceInfo....");
            text = deviceInfos.get("TERMINALSN");
            test = deviceInfos.get("KSN");
            Log.i("WU", "TERMINALSN===>" + deviceInfos.get("TERMINALSN"));
            Log.i("WU", "KSN===>" + deviceInfos.get("KSN"));
        }

        @Override
        public void onWaitingForCardSwipe() {//已经检测到刷卡器，进入等待刷卡或者其他指令状态
            // TODO Auto-generated method stub
        }

        @Override
        public void onReturnCardInfo(Map<String, String> cardInfos) {//返回卡片信息
            // TODO Auto-generated method stub
        }

        @Override
        public void onTimeout() {//刷卡超时
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(int errorCode) {//错误
            // TODO Auto-generated method stub
        }

        @Override
        public void onNeedInsertICCard() {//请插入IC卡，刷带磁条的IC卡时得到这个事件通知
            // TODO Auto-generated method stub
        }

        @Override
        public void onDetectedCard() {//已检测到刷卡操作
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
        }

        @Override
        public void onReturnPinBlock(String pinBlock) {//返回pos机输入的密码
            // TODO Auto-generated method stub

        }

        @Override
        public void onPressCancelKey() {

        }

    };

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DcConstant.STATE_CONNECT_SUCCESS:
                    progressDialog2.hide();
                    Toast.makeText(GainSerialNumberActivity2.this, R.string.toast_connect_success, Toast.LENGTH_LONG).show();
                    break;
                case DcConstant.STATE_CONNECT_FAIL:
                    progressDialog2.hide();
                    Toast.makeText(GainSerialNumberActivity2.this, R.string.toast_connect_fail, Toast.LENGTH_LONG).show();
                    break;
                case DcConstant.STATE_NO_CONNECTED:
                    Toast.makeText(GainSerialNumberActivity2.this, R.string.toast_no_connected, Toast.LENGTH_LONG).show();
                    break;
                case DcConstant.STATE_DISCONNECTED:
                    isBlueInfoShow = false;
                    progressDialog2.hide();
                    Toast.makeText(GainSerialNumberActivity2.this, R.string.toast_device_disconnect, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 操作蓝牙设备
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (m_dcswiperController == null) {
            m_dcswiperController = DCSwiper.getInstance(getApplication());
            m_dcswiperController.setM_swiperControllerListener(listener);
            if (m_dcswiperController.isConnected()) {

                AlertDialog.Builder progressDialog3 = new AlertDialog.Builder(this);
                progressDialog3.setTitle("提示");
                progressDialog3.setMessage("设备真正连接中,若不断开,该设备不出现列表中!是否断开?");

                progressDialog3.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shaoMiao();
                    }
                });

                progressDialog3.setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_dcswiperController.disconnectDevice();//断开蓝牙
                        shaoMiao();
                    }
                });
                progressDialog3.create();
                progressDialog3.show();
            } else {
                shaoMiao();
            }

        } else {
            m_dcswiperController = DCSwiper.getInstance(getApplication());
            m_dcswiperController.setM_swiperControllerListener(listener);
            shaoMiao();
        }
    }

    public void shaoMiao() {
        list.clear();
        adapter.delete();
        if (ba == null) {
            Toast.makeText(getApplicationContext(), "没有找到蓝牙设备", Toast.LENGTH_LONG).show();
            return;
        }
        if (ba.isEnabled()) {

            if (!isScanning) {// 如果没有扫描，则开始扫描

                isScanning = true;
                m_dcswiperController.startScan(null, 12);

            }
        } else {
            if (!isBlueInfoShow) {
                isBlueInfoShow = true;
                DCCharUtils.showLogD(TAG, "请先打开蓝牙...");
                Toast.makeText(getApplicationContext(), "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);//打开蓝牙
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBlueInfoShow = false;
        if (isScanning) {
            isScanning = false;

            m_dcswiperController.stopScan();
        }
    }

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private AlertDialog.Builder progressDialog3;

    /**
     * 对话框
     */
    public void unpDol() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在搜索设备中.......");
        progressDialog.setButton("停止扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isScanning = false;
                m_dcswiperController.stopScan();
            }
        });

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setTitle("提示");
        progressDialog2.setMessage("连接设备中.......");
        progressDialog2.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        progressDialog3 = new AlertDialog.Builder(this);
        progressDialog3.setTitle("提示");
        progressDialog3.setMessage("请断开设备......");

        progressDialog3.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        progressDialog3.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                disconn();
            }
        });


    }

    /**
     * 点击扫描
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.search_bt:
                if (m_dcswiperController.isConnected()) {
                    progressDialog3.show();

                } else {
                    startScan();
                }
                break;

        }

    }

    public void startScan() {

        if (!isScanning) {// 如果没有扫描，则开始扫描
            list.clear();
            adapter.delete();
            isScanning = true;
            m_dcswiperController.startScan(null, 12);

            Intent mIntent = new Intent();
            if (m_devive != null) {
                mIntent.putExtra("devmac", m_devive.getAddress());
            } else {
                mIntent.putExtra("devmac", new String(""));
            }
            // 设置结果，并进行传送
            this.setResult(RESULT_OK, mIntent);

        } else {// 停止扫描
            isScanning = false;
            m_dcswiperController.stopScan();

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent mIntent = new Intent();
            if (m_devive != null) {
                mIntent.putExtra("devmac", m_devive.getAddress());
            } else {
                mIntent.putExtra("devmac", new String(""));
            }
            // 设置结果，并进行传送
            this.setResult(RESULT_OK, mIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private String txt = "";
    private String deviceName = "";
    private int postion = 0;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        m_devive = (DcBleDevice) adapter.getItem(i);
//        m_dcswiperController.connectDevice(m_devive.getAddress(), 20);
        deviceName = m_devive.getDeviceName();//设备名称
//        progressDialog2.show();
        postion = i;

        EventBus.getDefault().post(new FirstEvent(deviceName));
        Intent posIntent = new Intent();
        posIntent.setAction(POSCODE_ACTION);
        posIntent.putExtra("poscode", deviceName);
        GainSerialNumberActivity2.this.sendBroadcast(posIntent);
//        setResult(110,posIntent);
//                Toast.makeText(GainSerialNumberActivity2.this, "获取到的序列号"+deviceName, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 返回键
     */
    public void fanhui(View view) {

        finish();

    }


    public void disconn() {
        if (m_dcswiperController.isConnected()) {
            m_dcswiperController.disconnectDevice();//断开蓝牙
            if (m_dcswiperController.isConnected() == false) {
                ToastUtil.getLongToastByString(GainSerialNumberActivity2.this, "设备断开");

                for (int i = 0; i < devicemanager_listv.getCount(); i++) {
                    devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.GONE);
                    if (postion == i) {
                        devicemanager_listv.getChildAt(i).findViewById(R.id.lay).setVisibility(View.GONE);
                    }
                }
            }
            startScan();
        } else {
            ToastUtil.getLongToastByString(GainSerialNumberActivity2.this, "目前还没有连接设备");
        }
    }

    /**
     * android6.0
     * 蓝牙权限扫描
     */
    private static final int REQUEST_FINE_LOCATION = 0;

    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))

                    Log.i("WU", "权限");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
                return;
            }
        }
    }

    /**
     * android 6.0蓝牙权限申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
