package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.ui.base.DenLuActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.RealNameActivity;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class UIHelper {
    public final static int LISTVIEW_ACTION_INIT = 1;
    public static ProgressDialog pd;

    public UIHelper() {
    }


    /**
     * 显示ProgressDialog
     */
    public static void showPd(Context context) {
        try {
            pd = new ProgressDialog(context);
            pd.setMessage("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * dismissProgressDialog
     */
    public static void dismissPd() {
        if (pd != null) {
            pd.dismiss();
        }
    }


    /*
     * 显示登录页面
     * */
    public static void showLoginActivity(Context context) {
        Intent intent = new Intent(context, DenLuActivity.class);
        context.startActivity(intent);
    }

    //联系客服
    public static void contactService(Context context, String phoneNum) {
        PackageManager pm = context.getPackageManager();
        // 获取是否支持电话
        boolean telephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(context, "no activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

	/*
     * 用户登录或退出登录
	 * */
//	public static void LoginOrLogout(Activity activity){
//        AppContext ac=(AppContext) activity.getApplication();
//        if(ac.isLogin()){
//            //ac.Logout();
//            ToastMessage(activity,"已经退出登录");
//        }else{
//            showLoginActivity(activity);
//        }
//    }

    public static void ToastMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void LogMessage(String TAG, String Exception, Exception excetion) {
        Log.i(TAG, Exception + ":" + excetion.getMessage());
    }

	/*
	 * 退出程序
	 * */

//	public static void exit(final Context context){
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setIcon(android.R.drawable.ic_dialog_info);
//        builder.setTitle(R.string.app_menu_surelogout);
//        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                //退出
//                AppManager.getAppManager().AppExit(context);
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//
//	}

    /*
     *动画效果打开activity,左进右出
     */
    public static void startActivityWithLR(Activity from, Intent intent, boolean isFinish) {
        from.startActivity(intent);
        if (isFinish) {
            from.finish();
        }
        from.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);
    }

    /*
    * 动画效果打开activity,下进上出
    * */
    public static void startActivityWithTB(Activity from, Class out, boolean isFinish) {
        Intent intent = new Intent(from, out);
        from.startActivity(intent);
        if (isFinish) {
            from.finish();
        }
        from.overridePendingTransition(R.anim.push_up_in,
                R.anim.push_up_out);
    }

    /*
     *动画效果打开activity
     */
    public static void startActivityforResultTB(Activity from, Class out, int requestCode) {
        Intent intent = new Intent(from, out);
        from.startActivityForResult(intent, requestCode);
        from.overridePendingTransition(R.anim.push_up_in,
                R.anim.push_up_out);

    }

    private static Dialog mDialog;

    /*加载动画*/
    public static void showRoundProcessDialog(Context mContext, int layout) {
        OnKeyListener keyListener = new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                }
                return false;
            }
        };

        mDialog = new AlertDialog.Builder(mContext).create();
        if (mDialog.getWindow() != null) {
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            // 模糊度
            mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            mDialog.getWindow().setAttributes(lp);
            lp.alpha = 0.1f;//透明度，黑暗度为lp.dimAmount=1.0f;
            mDialog.setOnKeyListener(keyListener);
            if (!mDialog.isShowing())
                mDialog.show();
            // 注意此处要放在show之后 否则会报异常
            mDialog.setContentView(layout);
            mDialog.setCanceledOnTouchOutside(false);
        }
    }

    public static void dismissRoundProcessDialog() {
        if (mDialog != null)
            mDialog.dismiss();
    }


    public static void showNetworkTips(Context context) {
        Toast.makeText(context, "网络连接失败，请检查网络设置", Toast.LENGTH_LONG).show();
    }


    //防止按钮连续点击
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;

        lastClickTime = time;

        if (0 < timeD && timeD > 600) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 添加，编辑车辆错误信息提示
     */
    public static void showErrTips(int code, Context activity) {
        if (code == 0) {

        } else if (code == 10001) {

            UIHelper.showLoginActivity(activity);
            AppContext.isLogin = false;

            StatusSQLUtils.undateApi("no");//保存退出状态

            HashMap params = new HashMap();
            Task ts = new Task(TaskType.TS_USER_EXIT, params);//退出
            MainService.newTask(ts);
            TokenSQLUtils.delete();

            EventBus.getDefault().post(new FirstEvent("no"));
            HashMap params1 = new HashMap();
            params1.put("auth", -1);
            Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//退出成功后更新个人中心数据
            MainService.newTask(ts1);

            XGPushManager.unregisterPush(AppContext.getContext());//jian 2017-5-2
//            Toast.makeText(activity, "校验Token失败，请重新登录！", Toast.LENGTH_SHORT).show();
        } else if (code == 30005) {
            Toast.makeText(activity, "查询失败，请重试", Toast.LENGTH_LONG).show();
        } else if (code == 30006) {
            Toast.makeText(activity, "车辆认证失败", Toast.LENGTH_LONG).show();
        } else if (code == 40002 || code == 40003 || code == 40005 || code == 40007) {
            Toast.makeText(activity, "提交订单失败", Toast.LENGTH_LONG).show();
        } else if (code == 50001) {
            Toast.makeText(activity, "车牌格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 50002) {
            Toast.makeText(activity, "车身架号格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 50003) {
            Toast.makeText(activity, "发动机号格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 50004) {
            Toast.makeText(activity, "无需重复保存", Toast.LENGTH_LONG).show();
        } else if (code == 50005) {
            Toast.makeText(activity, "修改后的车辆信息已经添加过了", Toast.LENGTH_LONG).show();
        } else if (code == 30010) {
            Toast.makeText(activity, "本系统暂不提供该城市违章查询请求", Toast.LENGTH_LONG).show();
        } else if (code == 30014) {
            Toast.makeText(activity, "暂时不支持此城市查询", Toast.LENGTH_LONG).show();
        } else if (code == -1) {
            Toast.makeText(activity, "缺少必要的参数或找不到车牌前缀所匹配的城市", Toast.LENGTH_LONG).show();
        } else if (code == -3) {
            Toast.makeText(activity, "本系统暂不提供该城市违章查询请求", Toast.LENGTH_LONG).show();
        } else if (code == -5) {
            Toast.makeText(activity, "服务器错误（超时，数据获取异常）", Toast.LENGTH_LONG).show();
        } else if (code == -6) {
            Toast.makeText(activity, "您输入的车辆信息有误，请检查", Toast.LENGTH_LONG).show();
        } else if (code == -10) {
            Toast.makeText(activity, "未被授权访问该服务或用户名密码不正确", Toast.LENGTH_LONG).show();
        } else if (code == -20) {
            Toast.makeText(activity, "未和错误", Toast.LENGTH_LONG).show();
        } else if (code == -40) {
            Toast.makeText(activity, "未被授权查询此车牌信息", Toast.LENGTH_LONG).show();
        } else if (code == -41) {
            Toast.makeText(activity, "输入参数不符合数据源要求", Toast.LENGTH_LONG).show();
        } else if (code == -43) {
            Toast.makeText(activity, "当日查询数已达到授权数标准，无法继续查询", Toast.LENGTH_LONG).show();
        } else if (code == -44) {
            Toast.makeText(activity, "已达到查询上限", Toast.LENGTH_LONG).show();
        } else if (code == -61) {
            Toast.makeText(activity, "输入车牌号有误", Toast.LENGTH_LONG).show();
        } else if (code == -62) {
            Toast.makeText(activity, "输入车架号有误", Toast.LENGTH_LONG).show();
        } else if (code == -63) {
            Toast.makeText(activity, "输入发动机号有误", Toast.LENGTH_LONG).show();
        } else if (code == -66) {
            Toast.makeText(activity, "不支持的车辆类型", Toast.LENGTH_LONG).show();
        } else if (code == -67) {
            Toast.makeText(activity, "该省份（城市）不支持异地车牌", Toast.LENGTH_LONG).show();
        } else if (code == 10100) {
            Toast.makeText(activity, "未被授权访接口", Toast.LENGTH_LONG).show();
        } else if (code == 10101) {
            Toast.makeText(activity, "车牌号格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 10102) {
            Toast.makeText(activity, "反序列化记录失败", Toast.LENGTH_LONG).show();
        } else if (code == 10103) {
            Toast.makeText(activity, "无此记录", Toast.LENGTH_LONG).show();
        } else if (code == 10104) {
            Toast.makeText(activity, "下单车牌与下单记录车牌不一致", Toast.LENGTH_LONG).show();
        } else if (code == 10105) {
            Toast.makeText(activity, "存在已办理记录", Toast.LENGTH_LONG).show();
        } else if (code == 10106) {
            Toast.makeText(activity, "车架号和发动机号都不符合下单规则", Toast.LENGTH_LONG).show();
        } else if (code == 10107) {
            Toast.makeText(activity, "车架号不符合下单规则", Toast.LENGTH_LONG).show();
        } else if (code == 10108) {
            Toast.makeText(activity, "发动机号不符合下单规则", Toast.LENGTH_LONG).show();
        } else if (code == 10109) {
            Toast.makeText(activity, "存在不可办理数据，请重新查询", Toast.LENGTH_LONG).show();
        } else if (code == 10120) {
            Toast.makeText(activity, "生成订单失败", Toast.LENGTH_LONG).show();
        } else if (code == 10121) {
            Toast.makeText(activity, "驾驶证号与车牌不匹配或车牌驾驶证信息错误", Toast.LENGTH_LONG).show();
        } else if (code == 500) {
            Toast.makeText(activity, "生成订单异常", Toast.LENGTH_LONG).show();
        } else if (code == 80006) {
            Toast.makeText(activity, "账户安全保障，请用户先进行实名认证", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(activity, RealNameActivity.class);
            activity.startActivity(intent);
        } else if (code == 80007) {
            Toast.makeText(activity, "此银行卡已经添加过了", Toast.LENGTH_LONG).show();
        } else if (code == 80015) {
            Toast.makeText(activity, "不能重复认证", Toast.LENGTH_LONG).show();
        } else if (code == 20018) {
            Toast.makeText(activity, "身份证格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 10004) {
            Toast.makeText(activity, "缺少参数", Toast.LENGTH_LONG).show();
        } else if (code == 20003) {
            Toast.makeText(activity, "注册验证码错误", Toast.LENGTH_LONG).show();
        } else if (code == 80014) {
            Toast.makeText(activity, "客户钱包扣款失败", Toast.LENGTH_LONG).show();
        } else if (code == 80003) {
            Toast.makeText(activity, "身份证和姓名不匹配", Toast.LENGTH_LONG).show();
        } else if (code == 80008) {
            Toast.makeText(activity, "银行卡认证失败", Toast.LENGTH_LONG).show();
        } else if (code == 80009) {
            Toast.makeText(activity, "添加银行卡失败,请重试", Toast.LENGTH_LONG).show();
        } else if (code == 20009) {
            Toast.makeText(activity, "密码错误,请重试", Toast.LENGTH_LONG).show();
        } else if (code == 20004) {
            Toast.makeText(activity, "手机号码已经注册", Toast.LENGTH_LONG).show();
        } else if (code == 20001) {
            Toast.makeText(activity, "账号密码错误", Toast.LENGTH_LONG).show();
        } else if (code == 20002) {
            Toast.makeText(activity, "重复密码错误", Toast.LENGTH_LONG).show();
        } else if (code == 20003) {
            Toast.makeText(activity, "注册验证码错误", Toast.LENGTH_LONG).show();
        } else if (code == 20005) {
            Toast.makeText(activity, "违章机已激活过", Toast.LENGTH_LONG).show();
        } else if (code == 20006) {
            Toast.makeText(activity, "违章机不存在", Toast.LENGTH_LONG).show();
        } else if (code == 20007) {
            Toast.makeText(activity, "注册验证码过期", Toast.LENGTH_LONG).show();
        } else if (code == 20008) {
            Toast.makeText(activity, "修改密码失败", Toast.LENGTH_LONG).show();
        } else if (code == 20010) {
            Toast.makeText(activity, "手机号码不存在", Toast.LENGTH_LONG).show();
        } else if (code == 20018) {
            Toast.makeText(activity, "身份证格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 20019) {
            Toast.makeText(activity, "密码格式错误", Toast.LENGTH_LONG).show();
        } else if (code == 100041) {
            Toast.makeText(activity, "", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, "未知错误,请稍后重试！", Toast.LENGTH_LONG).show();
        }
    }

}
