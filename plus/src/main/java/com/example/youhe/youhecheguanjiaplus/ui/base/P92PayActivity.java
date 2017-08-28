package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aidl.utils.EMVTAGStr;
import com.aidl.utils.HexUtils;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.PayBen;
import com.example.youhe.youhecheguanjiaplus.bean.PayFirstEvent;
import com.example.youhe.youhecheguanjiaplus.biz.SignInDao;
import com.lkl.cloudpos.aidl.AidlDeviceService;
import com.lkl.cloudpos.aidl.emv.AidlCheckCardListener;
import com.lkl.cloudpos.aidl.emv.AidlPboc;
import com.lkl.cloudpos.aidl.emv.AidlPbocStartListener;
import com.lkl.cloudpos.aidl.emv.CardInfo;
import com.lkl.cloudpos.aidl.emv.EmvTransData;
import com.lkl.cloudpos.aidl.emv.PCardLoadLog;
import com.lkl.cloudpos.aidl.emv.PCardTransLog;
import com.lkl.cloudpos.aidl.magcard.TrackData;
import com.lkl.cloudpos.aidl.pinpad.AidlPinpad;
import com.lkl.cloudpos.aidl.pinpad.GetPinListener;
import com.lkl.cloudpos.data.PinpadConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * P92支付界面
 */
public class P92PayActivity extends AppCompatActivity {

    private final static String TAG = "P92Pay";

    private AidlPboc pboc = null;//PBOC 检测卡流程

    private EmvTransData transData = null;

    private PbocStartListener listener = null;//PBOC流程处理监听接口

    private AidlPinpad pinpad = null; // 密码键盘接口

    public String LKL_SERVICE_ACTION = "lkl_cloudpos_device_service";//服务地址

    private PayBen payBen;//存储卡片信息类


    private String passwordStitching;//用来拼接密码

    private TextView textViewPaymoney,textViewScore;

    public static final String EXTRA_ORDER_TYPE="order_type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p92_pay);

        in();
    }

    private void in() {

        EventBus.getDefault().register(this);

        chushhua();
    }

    public void chushhua(){

        listener = new PbocStartListener();//初始化PBOC流程处理监听接口
        payBen = new PayBen();
        payBen.setOrdercode(getIntent().getStringExtra("ordercode"));//放入订单号
        payBen.setPaymoney(getIntent().getStringExtra("paymoney"));//放入总金额
        payBen.setOrder_type(getIntent().getStringExtra(EXTRA_ORDER_TYPE));//订单类型
        textViewPaymoney = (TextView) findViewById(R.id.shoukuanfang);
        textViewPaymoney.setText(getIntent().getStringExtra("showmoney"));//显示格式化的总金额
        textViewScore = (TextView) findViewById(R.id.zonjinew);
        textViewScore.setText(getIntent().getStringExtra("feng")+"分");//显示扣分

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

                try {
                    //初始化密码键盘
                    pinpad = AidlPinpad.Stub.asInterface(serviceManager.getPinPad(PinpadConstant.PinpadId.BUILTIN));
                    String worldKey = SignInDao.getWorldKey();//得到工作密钥
                    if (worldKey!=null){//如果去签到就导入工作密钥
                        Log.i(TAG,"已签到，开始导入工作密钥");
                        boolean flag = pinpad.loadWorkKey(
                                PinpadConstant.WKeyType.WKEY_TYPE_PIK,
                                0,
                                0,
                                HexUtils.hexStringToByte(worldKey.substring(0,16)),
                                HexUtils.hexStringToByte(worldKey.substring(16,worldKey.length())));
                        Log.i(TAG,"校验码===>"+worldKey.substring(16,worldKey.length()));
                        Log.i(TAG,"worldKey===>"+worldKey.substring(0,16));
                        if (flag){
                            Log.i(TAG,"PIK导入成功");
                        }else {
                            Log.i(TAG,"PIK导入失败");
                        }
                    }else {
                        Log.i(TAG,"不用签到 工作密钥 ===>"+worldKey);
                    }

                    //初始化卡片操作
                    pboc = AidlPboc.Stub.asInterface(serviceManager.getEMVL2());
                    try {
                        CheckCardListener checkCard = new CheckCardListener();
                        pboc.checkCard(true, true, true, 60000, checkCard);//开启检卡操作
                        Log.i("P92Pay","开启检卡操作==>");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "AidlService服务断开了");
        }
    };


    @Override
    protected void onResume() {
        Intent intent = new Intent();
        intent.setAction(LKL_SERVICE_ACTION);
        boolean flag = this.bindService(intent, conn,this.BIND_AUTO_CREATE);//绑定服务
        if (flag) {
            Log.i(TAG, "服务绑定成功");
        } else {
            Log.i(TAG, "服务绑定失败");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        try {
            pboc.cancelCheckCard();//取消检卡
            pboc.abortPBOC();
            this.unbindService(conn);

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i(TAG,"取消检卡操作异常");
        }

        super.onDestroy();
    }

    /**
     * 检测卡片
     */
    private class CheckCardListener extends AidlCheckCardListener.Stub{

        @Override
        public void onFindMagCard(TrackData trackData) throws RemoteException {//检测到磁条卡
            payBen.setCardtype(""+2);//卡片类型
            kahao = trackData.getCardno();//得到卡号
//            EventBus.getDefault().post(new PayFirstEvent("h",trackData.getCardno()));//通知隐藏动画
            Message message = Message.obtain();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("getCardno",trackData.getCardno());
            message.setData(bundle);
            handler.sendMessage(message);

            payBen.setTrack2(trackData.getSecondTrackData());//二磁到数据
            payBen.setTrack3(trackData.getThirdTrackData());//三磁道数据
            payBen.setCardnumber(trackData.getCardno());//卡号
            payBen.setExpired(trackData.getExpiryDate());//卡片有效期
            Log.i("P92Pay","检测到磁条卡");
            Log.i("P92Pay","一磁道数据==>"+trackData.getFirstTrackData());
            Log.i("P92Pay"," 二磁道数据==>"+trackData.getSecondTrackData());
            Log.i("P92Pay"," 三磁道数据==>"+trackData.getThirdTrackData());
            Log.i("P92Pay"," 卡号 ==>"+trackData.getCardno());
            Log.i("P92Pay"," 二三磁格式化数据 ==>"+trackData.getFormatTrackData ());
            Log.i("P92Pay"," 卡片有效期 ==>"+trackData.getExpiryDate());
            Log.i("P92Pay"," 服务码 ==>"+trackData.getServiceCode());

        }

        @Override
        public void onSwipeCardFail() throws RemoteException {//刷卡失败
            Log.i("P92Pay","刷卡失败");
        }

        @Override
        public void onFindICCard() throws RemoteException {//检测到接触式IC卡
            Log.i(TAG,"检测到接触式IC卡=============");
            payBen.setCardtype(""+1);//存储卡片类型

            transData = new EmvTransData((byte)0x00,
                    (byte)0x01, false, true, false,
                    (byte)0x01, (byte)0x00, new byte[]{0x00,0x00,0x00});
            transData.setEmvFlow((byte)0x01);
            transData.setTranstype((byte)0x00);
            transData.setEcashEnable(false);
            transData.setRequestAmtPosition((byte)0x01);
            try {
                pboc.processPBOC(transData, listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFindRFCard() throws RemoteException {//检测到非接卡
            Log.i("P92Pay","检测到非接卡");

            payBen.setCardtype(""+3);//存储非接卡片类型
            transData = new EmvTransData((byte)0x00,
                    (byte)0x01, false, true, false,
                    (byte)0x01, (byte)0x01, new byte[]{0x00,0x00,0x00});
            transData.setEmvFlow((byte)0x01);
            transData.setTranstype((byte)0x00);
            transData.setEcashEnable(false);
            transData.setRequestAmtPosition((byte)0x01);
            try {
                pboc.processPBOC(transData, listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }



        }

        @Override
        public void onTimeout() throws RemoteException {//检查卡超时
            Log.i("P92Pay","检查卡超时");
            finish();
        }

        @Override
        public void onCanceled() throws RemoteException {//检卡被取消
            Log.i("P92Pay","检卡被取消");
            finish();
        }

        @Override
        public void onError(int i) throws RemoteException {//检卡错误
            Log.i("P92Pay","检卡错误");
            finish();
        }
    }


    /**
     * IC卡支付流程处理
     */
    private class PbocStartListener extends AidlPbocStartListener.Stub{
        @Override
        public void requestUserAuth(int certType, String certno) throws RemoteException {
            Log.i(TAG,"请求账户类型确认");
            Log.i(TAG,"账户类型" + certType);
            Log.i(TAG,"账户号码" + certno);
            pboc.importUserAuthRes(true);
        }

        @Override
        public void requestTipsConfirm(String arg0) throws RemoteException {
            Log.i(TAG,"账户号码" + "请求提示信息");
            Log.i(TAG,"arg0" + arg0);
            pboc.importMsgConfirmRes(true);
        }





        @Override
        public void requestImportPin(int type, boolean lastFlag, String info) throws RemoteException {
            Log.i(TAG,"requestImportPin==>"+info);
//            EventBus.getDefault().post(new FirstEvent("h"));
            inputPin();//调起键盘


        }

        @Override
        public void requestImportAmount(int arg0) throws RemoteException {

            Log.i(TAG,"金额导入类型" + arg0);
            boolean result = pboc.importAmount(getIntent().getStringExtra("paymoney"));
            Log.i(TAG,"金额："+getIntent().getStringExtra("paymoney"));
            if (result) {
                Log.i(TAG,"导入交易金额成功");
                payBen.setTotalPrice(""+getIntent().getStringExtra("paymoney"));//存储总价格

            }else {
                Log.i(TAG,"导入交易金额失败，请中断PBOC流程");
            }
        }

        @Override
        public void requestEcashTipsConfirm() throws RemoteException {
            Log.i(TAG,"请求电子现金信息确认");
            pboc.importECashTipConfirmRes(transData.isEcashEnable());
        }

        @Override
        public void requestAidSelect(int times, String[] arg1) throws RemoteException {
            Log.i(TAG,"请选择应用列表");
            String str = "";
            for(int i = 0;i < arg1.length;i++){
                str += arg1[i];
            }
            Log.i(TAG,"应用列表内容" + str);
            pboc.importAidSelectRes(0x01);
        }

        @Override
        public void onTransResult(int arg0) throws RemoteException {
            Log.i(TAG,"PBOC流程处理结果");
            switch (arg0) {
                case 0x01:
                    Log.i(TAG,"交易批准");
                    break;
                case 0x02:
                    Log.i(TAG,"交易拒绝");
                    finish();
                    break;
                case 0x03:
                    Log.i(TAG,"交易终止");
                    finish();
                    break;
                case 0x04:
                    Log.i(TAG,"降级");
                    break;
                case 0x05:
                    Log.i(TAG,"采用其他界面");
                    break;
                case 0x06:

                    Log.i(TAG,"未知错误");
                    finish();
                    break;
                case 0x07:
                    Log.i(TAG,"交易取消");
                    finish();
                    break;
                default:
                    Log.i(TAG,"其他");
                    finish();
                    break;
            }
        }

        @Override
        public void onRequestOnline() throws RemoteException {//请求联机
            getACardData();//得到卡片数据
            get55Region();//得到55域数据
            handler.sendEmptyMessage(1);//通知跳转到签名墙
            Log.i(TAG,"=========================================");
        }

        /**
         * 得到卡片数据
         */
        public void getACardData(){

            byte[] data = new byte[2048];
            try {

                int retCode = pboc.readKernelData(EMVTAGStr.getACardData(), data);
                if(retCode > 0){
                    byte[] dataKernel = new byte[retCode];
                    System.arraycopy(data, 0, dataKernel, 0, retCode);
                    Log.i(TAG,"全部数据 - " + HexUtils.bcd2str(dataKernel));
                    String resString1 = pboc.parseTLV("5A", HexUtils.bcd2str(dataKernel));
                    Log.i(TAG,"TLV解析：卡号 - " + resString1);
                    String resString3 = pboc.parseTLV("57", HexUtils.bcd2str(dataKernel));
                    Log.i(TAG,"TLV解析：二磁道数据- " + resString3);
                    String resString4 = pboc.parseTLV("5F24", HexUtils.bcd2str(dataKernel));
                    Log.i(TAG,"TLV解析：有效期- " + resString4.substring(0,4));
                    String resString5 = pboc.parseTLV("5F34", HexUtils.bcd2str(dataKernel));
                    Log.i(TAG,"TLV解析：卡号序列号 - " + resString1);

                    payBen.setCardnumber(resString1);//保存卡号
                    payBen.setTrack2(resString3);//保存二磁道数据
                    payBen.setCrdsqn(resString5);//得到卡片序列号
                    payBen.setExpired(resString4.substring(0,4));//失效期

                }else{
                    Log.i(TAG,"内核数据读取失败");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 得到55域数据
         */
        public void get55Region(){
            byte[] data = new byte[2048];
            try {

                int retCode = pboc.readKernelData(EMVTAGStr.getLakalaF55UseModeOneForOnlineSale(), data);
                if(retCode > 0){
                    byte[] dataKernel = new byte[retCode];
                    System.arraycopy(data, 0, dataKernel, 0, retCode);
                    payBen.setIcdata(HexUtils.bcd2str(dataKernel));//保存55域数据
                    Log.i(TAG,"55域数据 - " + HexUtils.bcd2str(dataKernel));
                    Log.i(TAG,"55域数据长度 - " + HexUtils.bcd2str(dataKernel).length());
                }else {
                    Log.i(TAG,"内核数据读取失败");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        @Override
        public void onReadCardOffLineBalance(String arg0, String arg1, String arg2, String arg3) throws RemoteException {
            Log.i(TAG,"读取电子现金余额接口回调了");
            Log.i(TAG,"第一电子现金货币代码" + arg0);
            Log.i(TAG,"第一电子现金余额" + arg1);
            Log.i(TAG,"第二电子现金余额" + arg3);
        }

        @Override
        public void onReadCardLoadLog(String arg0, String arg1, PCardLoadLog[] arg2) throws RemoteException {

            Log.i(TAG,"交易计数器" + arg0);
            Log.i(TAG,"日志校验码" + arg1);
            for(int i = 0;i < arg2.length;i++){
                Log.i(TAG,(i+1) +"、Put Data P1 值:" + arg2[i].getPutdata_p1());
                Log.i(TAG,"Put Data P2 值:" + arg2[i].getPutdata_p2());
                Log.i(TAG,"Put Data 修改前:" + arg2[i].getBefore_putdata());
                Log.i(TAG,"Put Data 修改后:" + arg2[i].getAfter_putdata());
                Log.i(TAG,"交易日期:" + arg2[i].getTransDate());
                Log.i(TAG,"交易时间:" + arg2[i].getTransTime());
                Log.i(TAG,"应用交易计数器:" + HexUtils.bytes2short(arg2[i].getAppTransCount()));
            }
        }

        @Override
        public void onError(int arg0) throws RemoteException {
            Log.i(TAG,"PBOC处理出错:" + arg0);
        }

        @Override
        public void onConfirmCardInfo(CardInfo arg0) throws RemoteException {
            Log.i(TAG,"卡号信息：" + arg0.getCardno());
            kahao = arg0.getCardno();
//            EventBus.getDefault().post(new PayFirstEvent("h",arg0.getCardno()));//通知隐藏动画

            Message message = Message.obtain();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("getCardno",arg0.getCardno());
            message.setData(bundle);
            handler.sendMessage(message);
            Log.i(TAG,"describeContents：" +arg0.describeContents());
            pboc.importConfirmCardInfoRes(true);
        }

        @Override
        public void onReadCardTransLog(PCardTransLog[] logs) throws RemoteException {
            Log.i(TAG,"读取卡片交易日志:");
            for (int i = 0; i < logs.length; i++) {
                Log.i(TAG,(i+1) +"、交易日期:" + logs[i].getTransDate());
                Log.i(TAG,"交易时间:" + logs[i].getTransTime());
                Log.i(TAG,"授权金额:" + logs[i].getAmt());
                Log.i(TAG,"其他金额:" + logs[i].getOtheramt());
                Log.i(TAG,"国家代码:" + logs[i].getCountryCode());
                Log.i(TAG,"货币代码:" + logs[i].getMoneyCode());
                Log.i(TAG,"商户名称:" + logs[i].getMerchantName());
                Log.i(TAG,"交易类型:" + logs[i].getTranstype());
                Log.i(TAG,"应用交易计数器:" + HexUtils.bytes2short(logs[i].getAppTransCount()));

            }
        }
    }

    /**
     * PIN输入监听器
     * @author Tianxiaobo
     *
     */
    private class MyGetPinListener extends GetPinListener.Stub {
        @Override
        public void onStopGetPin() throws RemoteException {
            Log.i(TAG,"停止了PIN输入");
            finish();
        }

        @Override
        public void onInputKey(int arg0, String arg1) throws RemoteException {
//            Log.i(TAG,getStar(arg0) + arg1 == null ? "" : arg1);
            if (arg1!=null){//处理密码长度
                if (passwordStitching!=null){
                    passwordStitching += arg1;
                }else {
                    passwordStitching = arg1;
                }
            }else {
                passwordStitching = passwordStitching.substring(0,passwordStitching.length()-1);
            }

            EventBus.getDefault().post(new PayFirstEvent("shu",passwordStitching));//通知刷新显示密码
            Log.i(TAG,""+arg0);
            Log.i(TAG,""+arg1);
            Log.i(TAG,""+passwordStitching);

        }

        @Override
        public void onError(int arg0) throws RemoteException {
            Log.i(TAG,"读取PIN输入错误，错误码"+arg0);
            finish();
        }

        @Override
        public void onConfirmInput(byte[] arg0) throws RemoteException {//得到输入的密码
            Log.i(TAG,"PIN输入成功" + (null == arg0 ? ",您输入了空密码" : ",PIN为" + HexUtils.bcd2str(arg0)));

            if (arg0 != null && arg0.length > 0) {
                boolean arg = pboc.importPin(HexUtils.bcd2str(arg0));//导入密码调起联机
                if (arg){
                    payBen.setPinBlock((HexUtils.bcd2str(arg0)));//保存密码

                }

            }

        }

        @Override
        public void onCancelKeyPress() throws RemoteException {
            Log.i(TAG,"您按下了取消键");
            finish();
        }
    }

    /**
     * 输入联机PIN
     * @param
     * @createtor：Administrator
     * @date:2015-8-4 下午8:39:35
     */
    private String kahao;
    public void inputPin() {
        final Bundle bundle = new Bundle();
        bundle.putInt("wkeyid", 0x00);//工作密钥 ID
        bundle.putInt("keytype", 0x00);
        bundle.putByteArray("random", null);
        bundle.putInt("inputtimes", 1);
        bundle.putString("pan","0000"+kahao.substring(kahao.length()-13 ,kahao.length()-1));
        bundle.putInt("minlength", 6);
        bundle.putInt("maxlength", 6);
        bundle.putString("tips", "RMB:2000.00");
        Log.i(TAG,"截取的卡号===>"+kahao.substring(kahao.length()-13 ,kahao.length()-1));
        new Thread(){
            public void run() {
                try {
                    pinpad.getPin(bundle, new MyGetPinListener());

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }




    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }


    @Subscribe
    public void onEventMainThread(PayFirstEvent event) {

        if (event.getMsg().equals("h")){
            Log.i(TAG,"隐藏动画");
        }

    }

    /**
     * 处理显示密码输入框
     * @param event
     */
    @Subscribe
    public void onEvent(PayFirstEvent event) {


    }


    /**
     * 用来处理线程间逻辑
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1://跳转签名强页面

                    Intent intent = new Intent(P92PayActivity.this,SignInActivity.class);//跳到签名墙界面
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("payBen",payBen);
                    intent.putExtra("benbundle",bundle);
                    Log.d("TAG",payBen.getOrder_type()+"ddddddddddsssdddd");
                    intent.putExtra(SignInActivity.EXTRA_ORDER_TYPE,payBen.getOrder_type());
                    startActivity(intent);
                    finish();

                    break;
                case 2://隐藏动画
                    Bundle kahaobun = msg.getData();
                    Log.i(TAG,"隐藏动画");

                    break;
            }


            super.handleMessage(msg);
        }
    };


}
