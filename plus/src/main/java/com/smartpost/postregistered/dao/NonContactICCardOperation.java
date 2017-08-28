package com.smartpost.postregistered.dao;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.lkl.cloudpos.aidl.rfcard.AidlRFCard;

/**
 * Created by Administrator on 2016/10/24 0024.
 * 非接触卡逻辑类
 */

public class NonContactICCardOperation {
    private AidlRFCard rfcard;
    private Context context;
    private boolean is;//是否打开操作

    public NonContactICCardOperation(AidlRFCard rfcard, Activity content) {
        this.rfcard = rfcard;
        this.context = content;

    }

    /**
     * 打开非接触操作
     */
    public void openNoContacOperation() {
        try {
            is = rfcard.open();
            if (is) {
                Log.i("WU","打开设备操作成功");
            } else {
                Log.i("WU","打开设备操作失败");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭非接触操作
     */
    private boolean shut;

    public void shutSDdown() {
        try {
            shut = rfcard.close();
            if (shut) {

                Log.i("WU","关闭设备操作成功");

            } else {

                ToastUtil.getLongToastByString(context, "关闭设备操作失败");

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测卡片是否在位
     */
    public void detection() {
        try {
            boolean flag = rfcard.isExist();
            if (flag) {
                ToastUtil.getLongToastByString(context, "检测到卡片");
            } else {
                ToastUtil.getLongToastByString(context, "未检测到卡片");
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
