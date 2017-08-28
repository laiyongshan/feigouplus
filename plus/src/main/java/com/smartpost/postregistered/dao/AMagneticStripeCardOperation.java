package com.smartpost.postregistered.dao;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.lkl.cloudpos.aidl.magcard.AidlMagCard;
import com.lkl.cloudpos.aidl.magcard.MagCardListener;
import com.lkl.cloudpos.aidl.magcard.TrackData;

/**
 * Created by Administrator on 2016/10/22 0022.
 * 磁条卡操作逻辑类
 */

public class AMagneticStripeCardOperation {
    private static int iTimeOut = 60000;//操作超时时间
    private AidlMagCard aidlMagCard;
    private Activity context;

    public AMagneticStripeCardOperation(AidlMagCard aidlMagCard, Activity content) {
        this.aidlMagCard = aidlMagCard;
        this.context = content;

    }

    /**
     * 刷卡后返回信息
     */
    public void operation() {

        if (aidlMagCard != null) {
            ToastUtil.getLongToastByString(context, "请刷卡");
            Log.i("WU", "请刷卡");
            try {
                aidlMagCard.searchCard(iTimeOut, new MagCardListener.Stub() {
                    @Override
                    public void onTimeout() {//操作超时
                        Log.i("WU", "操作超时");
                        ToastUtil.getLongToastByString(context, "操作超时");
                        context.finish();
                    }

                    @Override
                    public void onError(int i) {//设备模块错误
                        Log.i("WU", "设备模块错误");
                        ToastUtil.getLongToastByString(context, "设备模块错误");
                    }

                    @Override
                    public void onCanceled() {//被取消
                        Log.i("WU", "被取消");
                        ToastUtil.getLongToastByString(context, "取消刷卡完成");
                    }

                    @Override
                    public void onSuccess(TrackData trackData) {//刷卡成功，实现序列化接口
                        Log.i("WU", "刷卡成功");
//                        ToastUtil.getLongToastByString(context, "刷卡成功");
                        Log.i("WU", "一磁道数据===>" + trackData.getFirstTrackData());
                        Log.i("WU", "二磁道数据===>" + trackData.getSecondTrackData());
                        Log.i("WU", "三磁道数据===>" + trackData.getThirdTrackData());
                        Log.i("WU", "卡号===>" + trackData.getCardno());
                        Log.i("WU", "二三磁格式化数据===>" + trackData.getFormatTrackData());
                        Log.i("WU", "卡片有效期===>" + trackData.getExpiryDate());
                        Log.i("WU", "服务码===>" + trackData.getServiceCode());

                    }

                    @Override
                    public void onGetTrackFail() {//刷卡失败，读取磁道失败
                        Log.i("WU", "刷卡失败");
                        ToastUtil.getLongToastByString(context, "刷卡失败");
                    }
                });

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /**
     * 取消操作
     */
    public void stopSearch() {

        if (null != aidlMagCard) {
            try {

                aidlMagCard.stopSearch();

            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ToastUtil.getLongToastByString(context, "取消刷卡操作异常");
            } // 中断刷卡
        }
    }
}
