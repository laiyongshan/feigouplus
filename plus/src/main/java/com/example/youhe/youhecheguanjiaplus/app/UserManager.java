package com.example.youhe.youhecheguanjiaplus.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.ui.base.ScanQrPayActivity;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.SharedPreferencesUtils;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 用户信息管理类
 * Created by Administrator on 2017/7/17 0017.
 */

public class UserManager {

    /**
     * 注册 车主卡用户
     *
     * @return
     */
    public static final int ROLE_PLUS = 1;//plus 用户
    public static final int ROLE_HONOUR_CARD = 2;//尊享卡用户
    public static final int ROLE_OWNER_CARD = 3;//车主卡用户
    public static final int ROLE_PROMOTION = 4;//推广码

    public static final int ROLE_UNLOGIN = 0;//未登录

    /**
     * 用户类型  1puls2尊享卡3分销4普通
     */
    public static final int USER_PLUS = 1;//plus 用户
    public static final int USER_HONOUR_CARD = 2;//尊享卡用户
    public static final int USER_DISTRIBUTION = 3;//分销用户
    public static final int USER_NORMAL = 4;//普通用户

    public static final int USER_STATUS_YES = 2;//1未激活2已激活
    public static final int USER_STATUS_NO = 1;//1未激活2已激活

    public static final String STRING_USER_STATUS ="userStatus";


    public static final String SP_USER_HEAR_IMG_URL="headimgurl"; //用户头像保存key
//    public static final String SP_USER_NICK_NAME_URL="nickname"; //用户名称保存key


    public static void setUserType(int userType) {
        SharedPreferencesUtils
                .setSharedPreferences(AppContext.getContext(), "userType", userType);
        switch (userType) {
            case USER_HONOUR_CARD:
            case USER_PLUS:
                setIsPlus(true);
                break;
            case USER_DISTRIBUTION:
            case USER_NORMAL:
                setIsPlus(false);
                break;
        }
    }

    /**用户类型 1：plus  2:尊享  3：分销 4：普通
     * @return
     */
    public static int getUserType() {
        return SharedPreferencesUtils.getSharedPreferences(AppContext.getContext()).getInt("userType", USER_NORMAL);
    }

    public static void setUserStatus(int userStatus) {
        SharedPreferencesUtils
                .setSharedPreferences(AppContext.getContext(), STRING_USER_STATUS, userStatus);
    }

    /**用户状态  分销 普通  //1未激活2已激活
     * @return
     */
    public static int getUserStatus() {
        return SharedPreferencesUtils.getSharedPreferences(AppContext.getContext()).getInt(STRING_USER_STATUS, USER_STATUS_NO);
    }

    /**
     * 是否为车主卡
     *
     * @return
     */
    public static boolean isPlus() {
        return SharedPreferencesUtils.getSharedPreferences(AppContext.getContext()).getBoolean("isPlus", false);
    }

    public static void setIsPlus(boolean isPlus) {
        SharedPreferencesUtils
                .setSharedPreferences(AppContext.getContext(), "isPlus", isPlus);
    }


    /**
     * 设置用户信息
     * @param key
     * @param value
     */
    public static void setValue(String key,String value){
        SharedPreferencesUtils
                .setSharedPreferences(AppContext.getContext(), key, value);
    }

    public static String getValue(String key){
        return SharedPreferencesUtils.getSharedPreferences(AppContext.getContext()).getString(key,"");
    }


    /**
     * 检查是否为分销 用户或者普通用户是否已经激活
     *
     * @return
     */
    public static boolean checkUserStatus() {
        if (UserManager.getUserType() == UserManager.USER_DISTRIBUTION || UserManager.getUserType() == USER_NORMAL) {
            if (UserManager.getUserStatus() == UserManager.USER_STATUS_YES)
                return true;
            else
                return false;
        } else
            return true;
    }

    /**
     * 判断用户是否激活  并且跳转到支付页面
     * @param context
     */
    public static void userActivation(final Context context) {
        if (checkUserStatus())
            return;
        if (StringUtils.isEmpty(TokenSQLUtils.check())){
            UIHelper.showLoginActivity(context);
            UIHelper.ToastMessage(context, "请先登录");
        }

        ToastUtil.getLongToastByString(context,"请激活该用户");
        final UIDialog uiDialog = new UIDialog(context, "加载中");
        uiDialog.show();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(context).postRequest(context, URLs.PAY_USER_ACTIVATION, hashMap, "获取激活信息失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {

                    String ordercode = "";//订单号 status为2时才有
                    String paymoney = "";//支付金额
                    int order_type = 3;//订单类型   1违章2年检3车主卡
                    int mjOpenType = 3;//茂捷二维码支付支持的支付类型  目前：paytype=3会返 1:支持微信支付   2：支持支付宝支付 3：全部

                    if (dataObject.has("ordercode"))
                        ordercode = dataObject.getString("ordercode");
                    if (dataObject.has("paymoney"))
                        paymoney = dataObject.getString("paymoney");
                    if (dataObject.has("order_type"))
                        order_type = dataObject.getInt("order_type");
                    if (dataObject.has("mjOpenType"))
                        mjOpenType = dataObject.getInt("mjOpenType");
                    if (StringUtils.isEmpty(ordercode)) {
                        Toast.makeText(context, "获取激活信息失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (StringUtils.isEmpty(paymoney)) {
                        Toast.makeText(context, "获取激活信息失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(context, ScanQrPayActivity.class);
                    intent.putExtra(ScanQrPayActivity.EXTRA_STRING_ORDER_CODE, ordercode);
                    intent.putExtra(ScanQrPayActivity.EXTRA_STRING_PAY_MONEY, paymoney);
                    intent.putExtra(ScanQrPayActivity.EXTRA_INT_ORDER_TYPE, order_type);
                    intent.putExtra(ScanQrPayActivity.EXTRA_INT_MJ_OPEN_TYPE, mjOpenType);

                    Bundle bundle=new Bundle();
                    bundle.putString(UserManager.STRING_USER_STATUS,"");
                    intent.putExtra(ScanQrPayActivity.EXTRA_CUSTOMER_BUNDLE,bundle);

                    intent.putExtra(ScanQrPayActivity.EXTRA_RETURN_CLASS, "com.example.youhe.youhecheguanjiaplus.ui.base.MainActivity");
                    context.startActivity(intent);
                } catch (Exception e) {
                    ToastUtil.getShortToastByString(context, "获取激活信息失败");
                    e.printStackTrace();
                }

                uiDialog.hide();
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                if (StringUtils.isEmpty(msg) || msg.equals("false")) {
                    ToastUtil.getShortToastByString(context, "获取激活信息失败");
                } else
                    ToastUtil.getShortToastByString(context, msg);
                uiDialog.hide();
            }
        });
    }


}
