package com.smartpost.postregistered.postergistered;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.lkl.cloudpos.aidl.AidlDeviceService;
import com.lkl.cloudpos.aidl.iccard.AidlICCard;
import com.lkl.cloudpos.aidl.magcard.AidlMagCard;
import com.lkl.cloudpos.aidl.rfcard.AidlRFCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/10/22 0022.
 * 智能POST机AIDL服务绑定
 */

public class PostRegisteredUtils {

    private Context mContext;
    public String LKL_SERVICE_ACTION = "lkl_cloudpos_device_service";//服务地址
    private String TAG = "WU";
    private AidlMagCard magCardDev = null; // 磁条卡设备
    private AidlICCard iccard = null;//接触式IC卡
    private AidlRFCard rfcard = null;//非接触IC卡
    private String ACREDITCARD = "acreditcard";//双向刷卡方式
    private String INSERTACARD = "insertacard";//插卡方式
    private String DROPOFCARD = "dropofcard";//滴卡方式
    private String way;


    public PostRegisteredUtils(Context context, String way) {
        this.mContext = context;
        this.way = way;
        EventBus.getDefault().register(this);

    }

    /**
     * 识别别服务连接桥
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            Log.i(TAG, "aidlService服务连接成功");
            if (serviceBinder != null) {    //绑定成功
                AidlDeviceService serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder);
                if (way.equals(ACREDITCARD)) {//双向刷卡服务连接

                    try {
                        magCardDev = AidlMagCard.Stub.asInterface(serviceManager
                                .getMagCardReader());
                        EventBus.getDefault().post(new FirstEvent("TheConnectionIsSuccessful"));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (way.equals(INSERTACARD)) {//插卡方式服务连接
                    try {
                        iccard = AidlICCard.Stub.asInterface(serviceManager.getInsertCardReader());
                        EventBus.getDefault().post(new FirstEvent("contact"));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                } else if (way.equals(DROPOFCARD)) {//滴卡方式服务连接
                    try {
                        rfcard = AidlRFCard.Stub
                                .asInterface(serviceManager.getRFIDReader());
                        EventBus.getDefault().post(new FirstEvent("noncontact"));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
    private void indService() {
        Intent intent = new Intent();
        intent.setAction(LKL_SERVICE_ACTION);
        boolean flag = mContext.bindService(intent, conn, mContext.BIND_AUTO_CREATE);//绑定服务
        if (flag) {
            Log.i(TAG, "服务绑定成功");
        } else {
            Log.i(TAG, "服务绑定失败");
        }
    }

    /**
     * 启动界面重新绑定服务
     */
    public void resume() {
        String judgeMachineType = android.os.Build.MODEL;
        if (judgeMachineType.equals("P92")) {
            indService();//绑定服务
        }
    }

    /**
     * 得到已注册好的AidlMagCard对象
     *
     * @return
     */
    public AidlMagCard getAidlMagCardObject() {

        return magCardDev;
    }

    /**
     * 得到IC卡操作对象
     *
     * @return
     */
    public AidlICCard getAidlICCardObject() {

        return iccard;
    }

    /**
     * 得到IC非接触操作对象
     *
     * @return
     */
    public AidlRFCard getAidlRFCard() {

        return rfcard;
    }


    /**
     * 当界面暂停止时
     * 解绑服务
     */
    public void pause() {
        String judgeMachineType = android.os.Build.MODEL;
        if (judgeMachineType.equals("P92")) {
            if (conn != null) {
                mContext.unbindService(conn);
                Log.i("WU","解绑服务");
            }
        }

    }

    /**
     * 通知服务绑定成功
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(FirstEvent event) {


    }
}
