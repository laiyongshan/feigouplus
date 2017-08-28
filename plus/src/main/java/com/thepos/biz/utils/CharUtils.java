package com.thepos.biz.utils;

public class CharUtils {

    private static final String TAG = "CharUtils";

    public static String errCode2Str(String resCode) {
        if (Constant.TRADE_RESPONSECODE_01.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_01_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_03.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_03_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_04.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_04_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_05.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_05_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_06.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_06_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_07.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_07_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_13.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_13_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_14.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_14_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_15.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_15_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_38.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_38_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_40.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_40_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_51.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_51_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_54.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_54_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_55.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_55_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_57.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_57_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_61.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_61_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_62.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_62_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_64.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_64_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_65.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_65_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_68.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_68_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_75.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_75_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_91.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_91_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_94.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_94_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_96.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_96_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_98.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_98_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_99.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_99_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_A0.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_A0_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_Y3.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_Y3_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XU.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XU_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X0.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X0_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X1.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X1_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X2.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X2_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X3.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X3_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X4.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X4_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X5.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X5_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X6.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X6_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X7.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X7_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X8.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X8_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_X9.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_X9_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XA.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XA_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XB.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XB_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XC.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XC_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XD.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XD_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XE.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XE_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XG.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XG_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XH.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XH_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XI.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XI_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XJ.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XJ_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XK.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XK_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XL.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XL_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XM.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XM_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XN.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XN_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XP.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XP_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XQ.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XQ_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XR.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XR_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XS.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XS_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XT.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XT_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XU.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XU_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_XV.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_XV_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_02.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_02_ESTR;
        } else if (Constant.TRADE_RESPONSECODE_NR.equals(resCode)) {
            return Constant.TRADE_RESPONSECODE_NR_ESTR;
        }
        return "";
    }


}
