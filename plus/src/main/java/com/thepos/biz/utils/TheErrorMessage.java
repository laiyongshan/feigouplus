package com.thepos.biz.utils;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class TheErrorMessage {

    public static final int DCSWIPER_ERROR_OK = 0;
    public static final int DCSWIPER_ERROR_FAILED = 10001;//一般错误
    public static final int DCSWIPER_ERROR_READ_CARDNUMBER = 0x1002;//读卡号错误
    public static final int DCSWIPER_ERROR_READ_TRACK1 = 1003;//读卡号错误
    public static final int DCSWIPER_ERROR_READ_TRACK2 = 1004;//读卡号错误
    public static final int DCSWIPER_ERROR_READ_TRACK3 = 1005;//读卡号错误
    public static final int DCSWIPER_ERROR_SWIPER_IC = 1006;//请刷IC卡
    public static final int DCSWIPER_ERROR_TRANSMITING = 1007;//设备正在通信中
    public static final int DCSWIPER_ERROR_OPERATE_CARDS = 1008;//卡操作错误
    public static final int DCSWIPER_ERROR_OPERATE_CRD = 1009;//参数错误

    public static final int DCSWIPER_ERROR_OPERATE_CARD = 1010;//需要刷卡


    public static final int DCSWIPER_ERROR_TRANS_REFUSE = 2000;//交易拒绝
    public static final int DCSWIPER_ERROR_TRANS_SERVICE_NOTALLOW = 2001;//服务不允许
    public static final int DCSWIPER_ERROR_TRANS_EXCEPTION = 2002;//交易异常
    public static final int DCSWIPER_ERROR_BLT_SERVICE_NOT_USE = 2003;//蓝牙没有打开
}
