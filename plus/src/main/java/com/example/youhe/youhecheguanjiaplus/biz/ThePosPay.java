package com.example.youhe.youhecheguanjiaplus.biz;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by Administrator on 2016/9/26 0026.
 * 计算总价
 */
public class ThePosPay {

    private static String TOTALPRICE ;//总金额
    private static String ZONGFUWU ;//总服务
    private static String ORDERNUMBER;//订单号
    private static String  FUWUFEI;//服务费
    private static String  YEHK;//未格式化的 总金额
    private Activity mActivity;
    private  static String types;
    private static boolean IS_MAKE_UP_MONEY=false;//是否补款
    public static double payPrice;//支付金额
    public static int is_balance_deductible;//是否使用余额抵扣

    public static int ordertype;//1：违章订单 2：补款订单  3：年检订单

    public ThePosPay(Activity activity){
        this.mActivity = activity;
    }


    /**
     * 设置服务费,总价
     * @param totalPrice
     * @param serviceFee
     */
    public void totalSetText(TextView totalPrice,TextView serviceFee,TextView bukuan_pice_tv,TextView order_number_tv){

        Intent  intent = mActivity.getIntent();
        ordertype=intent.getIntExtra("ordertype",0);
        String zonfuwu;
        String ordernumber;
        if(intent!=null){
            if(ordertype==1) {//违章订单
                String zonfakuan = intent.getStringExtra("zonfakuan");//总价
                zonfuwu= intent.getIntExtra("zonfuwu", 0)+"";//总扣分
                String shouxufei = intent.getStringExtra("totalprice");//服务费
                ordernumber= intent.getStringExtra("ordernumber");//得到订单号
                String integerzonjine = intent.getStringExtra("integerzonjine");//整数的总金额

                boolean is_make_up_money = intent.getBooleanExtra("is_make_up_money", false);//是否补款

                Log.i("WU","服务费===>"+zonfuwu);
                Log.i("WU", "整数的总金额===>" + integerzonjine);
                if (intent.getStringExtra("type").equals("5")) {
                    types = "" + 5;
                    serviceFee.setText("违章办理");//总扣分
                } else {
                    serviceFee.setText(zonfuwu + "分");//总扣分
                }
                YEHK = integerzonjine;//整数的总金额
                TOTALPRICE = zonfakuan;//总价
                ZONGFUWU = zonfuwu;//总扣分
                FUWUFEI = shouxufei;//总服务费
                types = intent.getStringExtra("type");
                IS_MAKE_UP_MONEY = is_make_up_money;
                ORDERNUMBER = ordernumber;//订单号
                totalPrice.setText("￥" + TOTALPRICE);

            }else if(ordertype==2||ordertype==3){//补款订单||年检订单

                zonfuwu= intent.getStringExtra("zonfuwu")+"";
                ordernumber=""+intent.getStringExtra("ordernumber");

                ZONGFUWU = zonfuwu;
                ORDERNUMBER = ordernumber;//订单号



                bukuan_pice_tv.setText("￥"+intent.getStringExtra("zonfakuan"));
                order_number_tv.setText(""+intent.getStringExtra("ordernumber"));
            }
        }
    }

    public static final boolean is_make_up_money(){
        return IS_MAKE_UP_MONEY;
    }

    /**
     * 拿到格式化过总共的金额
     * @return
     */
    public static final String getTotalPrice(){
        return TOTALPRICE;
    }
    /**
     * 未格式化的总金额
     * @return
     */
    public static final String getPrice(){
        return YEHK;
    }

    /**
     * 拿到中总分
     * @return
     */
    public static final String getZonfuwu(){

        return ""+ZONGFUWU;
    }

    /**
     * 得到订单号类型
     * @return
     */
    public static final String getType(){

        return types;
    }

    /**
     * 得到订单类型
     * @return
     */
    public static final String getOrenumber(){

        return ORDERNUMBER;
    }

    public static final String getFwufei(){
        return FUWUFEI;
    }

    /**
     * 获取支付金额
     * */
    public static double getPayPrice(){
        return payPrice;
    }

}
