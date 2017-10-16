package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.biz.ThePosPay;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.entity.base.SerMap;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.utils.ClickUtils;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.PayUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static com.example.youhe.youhecheguanjiaplus.app.AppContext.getContext;

/**
 * 签名墙界面
 */
public class SignInActivity extends YeoheActivity {

//    public  String LKL_SERVICE_ACTION = "lkl_cloudpos_device_service";//服务地址
//    private AidlSystem systemInf = null;

    private String TAG = "Pay";
    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private Button bt;//确定键

    private HashMap payMap=new HashMap();//支付参数

    private ProgressBar pb;
    private String order_type="";
    public static final String EXTRA_ORDER_TYPE="order_type";
    public static final String  EXTRA_CUSTOMER_BUNDLE="customer_bundle";
    public Bundle customerBundle=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Intent intent=getIntent();
        if (intent.hasExtra(EXTRA_CUSTOMER_BUNDLE))
            customerBundle=intent.getBundleExtra(EXTRA_CUSTOMER_BUNDLE);

        Bundle bundle=intent.getExtras();
        SerMap serMap= (SerMap) bundle.get("serMap");

        payMap=serMap.getMap();
        order_type=getIntent().getStringExtra(EXTRA_ORDER_TYPE);

        bt  = (Button) findViewById(R.id.bt);
        bt.setClickable(false);
        bt.setPressed(true);
        iv = (ImageView) findViewById(R.id.iv);
        paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);
        //消除锯齿
        paint.setAntiAlias(true);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        // 创建一个可以被修改的bitmap
        baseBitmap = Bitmap.createBitmap(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBitmap);
        canvas.drawColor(Color.WHITE);

        // 知道用户手指在屏幕上移动的轨迹
        iv.setOnTouchListener(new View.OnTouchListener() {
            // 设置手指开始的坐标
            int startX;
            int startY;
            boolean fa = true;//设置按钮
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 手指第一次接触屏幕
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        if (fa==true){
                            bt.setClickable(true);
                            bt.setPressed(false);
                            fa= false;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上滑动
                        int newX = (int) event.getX();
                        int newY = (int) event.getY();
                        canvas.drawLine(startX, startY, newX, newY, paint);
                        // 重新更新画笔的开始位置
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        iv.setImageBitmap(baseBitmap);
                        break;
                    case MotionEvent.ACTION_UP: // 手指离开屏幕
                        break;

                }
                return true;
            }
        });

        pb= (ProgressBar) findViewById(R.id.progressBar);

    }



    /**
     * 得到签名图片并上传支付数据
     * 跳转到支付完成界面
     * @param view
     */
    public void save(View view) {
        if (ClickUtils.isFastDoubleClick()){
            return;
        }

        pb.setVisibility(View.VISIBLE);

//
        String bitmapBas64 = getBas64();//把签名强图片转为Bas64

        uploadSignPhoto(bitmapBas64);
    }


    /**
     * 把签名强图片转为
     * bas64
     * @return
     */
    public String getBas64(){
        String headimg = null;

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);

        byte[] bytes = bStream.toByteArray();

        headimg = Base64.encodeToString(bytes, Base64.DEFAULT);//把图片转成BASE64

        return headimg;
    }

    /**
     * 返回键
     * @param view
     */
    public void fanhui(View view){
        finish();
    }

    /**
     * 重签
     * @param view
     */
    public void clear(View view){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清屏
    }


//




    @Override
    protected void onResume() {
        super.onResume();
    }

    HashMap map;
    private final String IMAGE_TYPE="pay";
    private HashMap getOnePhotoParam(String imgtype){
        map = new HashMap();
        map.put("imgtype",imgtype);
        map.put("token", TokenSQLUtils.check()+"");
        return EncryptUtil.encrypt(map);
    }

    private void uploadSignPhoto(final String strImg ){
        UIHelper.showPd(SignInActivity.this);
        map=new HashMap();
        map=getOnePhotoParam(IMAGE_TYPE);
        map.put("img",strImg);
        VolleyUtil.getVolleyUtil(SignInActivity.this).StringRequestPostVolley(URLs.ADD_IMG_URL,map,new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    String  json=EncryptUtil.decryptJson(jsonObject.toString(),SignInActivity.this);
                    JSONObject newObj = new JSONObject(json);
                    String status=newObj.getString("status");
                    if(status.equals("ok")) {
                        JSONObject dataObj = newObj.getJSONObject("data");
                        int imgid = dataObj.getInt("imgid");
                        payMap.put("signimg",imgid);
//                        Log.d("TAG","ssssssssssssssssss"+order_type);
                        payMap.put("order_type",order_type);
                        payMap.put("is_balance_deductible", ThePosPay.is_balance_deductible);//是否使用余额
//                        payMap.put("order_type")
                        PayUtil.yinjiaPay(SignInActivity.this,payMap,strImg,customerBundle);//银嘉支付
                    }else {
                        Toast.makeText(SignInActivity.this,"上传签名图片失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(SignInActivity.this,"上传图片返回的错误信息："+volleyError.toString(),Toast.LENGTH_SHORT).show();
                UIHelper.dismissPd();
            }
        });
    }




    @Override
    public void init() {
    }

    @Override
    public void refresh(Object... param) {
    }

}
