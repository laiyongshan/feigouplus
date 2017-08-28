package com.smartpost.postregistered.dao;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.lkl.cloudpos.aidl.iccard.AidlICCard;

/**
 * Created by Administrator on 2016/11/22 0022.
 *
 */

public class CardWay {
    private AidlICCard mIccard;
    private Activity mActivity;

    public CardWay(AidlICCard iccard, Activity activity){

        this.mIccard = iccard;
        this.mActivity = activity;

    }

    /**
     * 打开插卡操作
     */
    public void opens(){
        try {
            boolean open  = mIccard.open();
            if (open){
                ToastUtil.getLongToastByString(mActivity,"请插卡");


            }else{

                ToastUtil.getLongToastByString(mActivity,"打开设备失败");
                mActivity.finish();

            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭插卡操作
     */
    public void guanbi(){
        try {
             boolean close = mIccard.close();
            if (close){

                Log.i("WU","关闭成功");

            }else{
                Log.i("WU","关闭失败");
                mActivity.finish();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
