package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.CommentSetting;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.PswDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapScale;
import com.example.youhe.youhecheguanjiaplus.utils.ParamSign;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.TripleDES;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class RealNameActivity extends Activity implements View.OnClickListener{

    private ClearEditText real_name_et,idcard_num_et;
    private Button commit_real_name_btn;
    private ImageButton real_name_back_ib;

    private TextView is_verify_tv;

    private ImageView idcard_zhengmian_iv,idcard_fanmian_iv,head_idcard_zhengmian_iv,head_idcard_fanmian_iv;


    public static final int IDCARD_Z_PHOTO=1001;
    public static final int IDCARD_F_PHOTO=1002;
    public static final int IDCARD_H_Z_PHOTO=1003;
    public static final int IDCARD_H_F_PHOTO=1004;

    private String imgPath1,imgPath2,imgPath3,imgPath4;

    private int identitycard_front,identitycard_back,hand_identitycard_front,hand_identitycard_back;//上传图片返回的id

    private int mScreenWidth;
    private int mScreenHeigh;

    private ProgressDialog pd;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,RealNameActivity.this);
        }
        SystemBarUtil.useSystemBarTint(RealNameActivity.this);

        sharedPreferences=getSharedPreferences("is_real_name",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        WindowManager wm = (WindowManager) (RealNameActivity.this.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth= dm.widthPixels;
        mScreenHeigh= dm.heightPixels;

        bmpManager= new BitmapManager(BitmapFactory.decodeResource(RealNameActivity.this.getResources(),R.drawable.zhengmian),RealNameActivity.this);

        initView();//初始化控件

        pd.show();
        getUserAuthInfo();//获取用户认证信息
    }

    @Override
    protected void onStop() {
        super.onStop();
        pd.dismiss();
    }

    private void initView(){
        real_name_et= (ClearEditText) findViewById(R.id.real_name_et);
        idcard_num_et= (ClearEditText) findViewById(R.id.idcard_num_et);

        commit_real_name_btn= (Button) findViewById(R.id.commit_real_name_btn);
        commit_real_name_btn.setOnClickListener(this);

        real_name_back_ib= (ImageButton) findViewById(R.id.real_name_back_ib);
        real_name_back_ib.setOnClickListener(this);

        idcard_zhengmian_iv= (ImageView) findViewById(R.id.idcard_zhengmian_iv);
        idcard_zhengmian_iv.setOnClickListener(this);

        idcard_fanmian_iv= (ImageView) findViewById(R.id.idcard_fanmian_iv);
        idcard_fanmian_iv.setOnClickListener(this);

        head_idcard_zhengmian_iv= (ImageView) findViewById(R.id.head_idcard_zhengmian_iv);
        head_idcard_zhengmian_iv.setOnClickListener(this);

        head_idcard_fanmian_iv= (ImageView) findViewById(R.id.head_idcard_fanmian_iv);
        head_idcard_fanmian_iv.setOnClickListener(this);

        is_verify_tv= (TextView) findViewById(R.id.is_verify_tv);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.commit_real_name_btn://提交实名认证按钮
                if(real_name_et.getText().toString().trim().equals("")){
                    Toast.makeText(RealNameActivity.this,"真实姓名不能为空！",Toast.LENGTH_SHORT).show();
                }else if(idcard_num_et.getText().toString().trim().length()<15){
                    Toast.makeText(RealNameActivity.this,"请填写完整身份证号码！",Toast.LENGTH_SHORT).show();
                }else if(identitycard_front==0||identitycard_back==0||hand_identitycard_front==0||hand_identitycard_back==0){
                    Toast.makeText(RealNameActivity.this,"请上传身份证照片！",Toast.LENGTH_SHORT).show();
                }else{
                    final PswDialog pswDialog=new PswDialog(RealNameActivity.this,R.style.Dialog,2);
                    pswDialog.show();

                    //回调接口
                    pswDialog.mSureListener=new PswDialog.OnSureListener(){
                        @Override
                        public void onSure(String psw) {

//                            HashMap pswMap=new HashMap();
//                            pswMap.put("password",psw);
                            pd.dismiss();
                            clientAuth(ParamSign.getUserPassword(psw));
                            pswDialog.dismiss();
                        }
                    };
                }
                break;

            case R.id.real_name_back_ib:
                finish();
                break;

            case R.id.idcard_zhengmian_iv:
                Intent intent1 = new Intent(RealNameActivity.this, SelectPicActivity.class);
                startActivityForResult(intent1,IDCARD_Z_PHOTO);
                break;

            case R.id.idcard_fanmian_iv:
                Intent intent2 = new Intent(RealNameActivity.this, SelectPicActivity.class);
                startActivityForResult(intent2,IDCARD_F_PHOTO);
                break;

            case R.id.head_idcard_zhengmian_iv:
                Intent intent3 = new Intent(RealNameActivity.this, SelectPicActivity.class);
                startActivityForResult(intent3,IDCARD_H_Z_PHOTO);
                break;

            case R.id.head_idcard_fanmian_iv:
                Intent intent4 = new Intent(RealNameActivity.this, SelectPicActivity.class);
                startActivityForResult(intent4,IDCARD_H_F_PHOTO);
                break;

        }
    }


    private Bitmap idCard_zhengmian_bitmap = null;//行驶证正页
    private Bitmap idCard_fanmian_bitmap=null;//行驶证负页
    private Bitmap idCard_head_zhengmian_bitmap=null;//驾驶证正页
    private Bitmap idCard_head_fanmian_bitmap=null;//驾驶证负页

    private String strIdcard_zm="";
    private String strIdcard_fm="";
    private String strIdcard_h_z="";
    private String strIdcard_h_f="";

    private String auth="auth";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case IDCARD_Z_PHOTO:
                if(data!=null) {
                    imgPath1 = data.getStringExtra("photo_path");
                }

                if(imgPath1!=null){

                    pd.show();

                    Bitmap id_z_bitmap= BitmapScale.getBitmap(imgPath1);
                    if(id_z_bitmap!=null) {
                        if(id_z_bitmap.getHeight()>id_z_bitmap.getWidth()){
                            id_z_bitmap=BitmapScale.rotaingImageView(90,id_z_bitmap);
                        }
                        idCard_zhengmian_bitmap=BitmapScale.fitBitmap(id_z_bitmap,mScreenWidth*5/9);
                        Bitmap id_z_bitmap_2=BitmapScale.fitBitmap(id_z_bitmap,1000);

                        strIdcard_zm=BitmapScale.getImage(id_z_bitmap_2);

                        uploadphoto(strIdcard_zm,auth,1);
                    }
                }
                break;

            case IDCARD_F_PHOTO:
                if(data!=null) {
                    imgPath2 = data.getStringExtra("photo_path");
                }

                if(imgPath2!=null){

                    pd.show();

                    Bitmap id_f_bitmap=BitmapScale.getBitmap(imgPath2);
                    if(id_f_bitmap!=null) {
                        if(id_f_bitmap.getHeight()>id_f_bitmap.getWidth()){
                            id_f_bitmap=BitmapScale.rotaingImageView(90,id_f_bitmap);
                        }
                        idCard_fanmian_bitmap=BitmapScale.fitBitmap(id_f_bitmap,mScreenWidth*5/9);
                        Bitmap id_f_bitmap_2=BitmapScale.fitBitmap(id_f_bitmap,1000);

                        strIdcard_fm=BitmapScale.getImage(id_f_bitmap_2);

                        uploadphoto(strIdcard_fm,auth,2);
                    }

                }
                break;

            case IDCARD_H_Z_PHOTO:
                if(data!=null) {
                    imgPath3 = data.getStringExtra("photo_path");
                }

                if(imgPath3!=null){

                    pd.show();

                    Bitmap id_h_z_bitmap=BitmapScale.getBitmap(imgPath3);
                    if(id_h_z_bitmap!=null) {
                        if(id_h_z_bitmap.getHeight()>id_h_z_bitmap.getWidth()){
                            id_h_z_bitmap=BitmapScale.rotaingImageView(90,id_h_z_bitmap);
                        }
                        idCard_head_zhengmian_bitmap=BitmapScale.fitBitmap(id_h_z_bitmap,mScreenWidth*5/9);
                        Bitmap id_h_z_bitmap_2=BitmapScale.fitBitmap(id_h_z_bitmap,1000);

                        strIdcard_h_z=BitmapScale.getImage(id_h_z_bitmap_2);

                        uploadphoto(strIdcard_h_z,auth,3);
                    }
                }
                break;

            case IDCARD_H_F_PHOTO:
                if(data!=null) {
                    imgPath4 = data.getStringExtra("photo_path");
                }

                if(imgPath4!=null){

                    pd.show();

                    Bitmap id_h_f_bitmap=BitmapScale.getBitmap(imgPath4);
                    if(id_h_f_bitmap!=null) {
                        if(id_h_f_bitmap.getHeight()>id_h_f_bitmap.getWidth()){
                            id_h_f_bitmap=BitmapScale.rotaingImageView(90,id_h_f_bitmap);
                        }
                        idCard_head_fanmian_bitmap=BitmapScale.fitBitmap(id_h_f_bitmap,mScreenWidth*5/9);
                        Bitmap id_h_f_bitmap_2=BitmapScale.fitBitmap(id_h_f_bitmap,1000);

                        strIdcard_h_f=BitmapScale.getImage(id_h_f_bitmap_2);

                        uploadphoto(strIdcard_h_f,auth,4);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    HashMap<String, Object> map;
    private String getUploadPhotoParam(String imgtype){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            map.put("token", token);
        }
//        map.put("img",strImg);
        map.put("imgtype",imgtype);

        map.put("timestamp",System.currentTimeMillis()/1000+"");

        String sign=ParamSign.getSign(map);

        map.put("sign",sign);

        return  ParamSign.getSign(map);
    }

    private void uploadphoto(String strImg, String imgtype, final int imgth){
        map= new HashMap<String, Object>();
        map.put("data",getUploadPhotoParam(imgtype));
        map=ParamSign.netWorkEncrypt(map);
        map.put("img",strImg);
        VolleyUtil.getVolleyUtil(RealNameActivity.this).StringRequestPostVolley(URLs.ADD_IMG_URL,map , new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(jsonObject.toString());
                    String data=obj.getString("data");
                    byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                    String json=new String(encrypt,"UTF-8");

                    parseJson(json,imgth);//解析解密之后的数据

                } catch (Exception e){
                    e.printStackTrace();
                }

                pd.dismiss();
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(RealNameActivity.this,"上传图片返回的错误信息："+volleyError.toString(),Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void parseJson(String json,int imgth){

        try {
            JSONObject newObj = new JSONObject(json);
            String status=newObj.getString("status");
            if(status.equals("ok")) {
                JSONObject dataObj = newObj.getJSONObject("data");
                int imgid = dataObj.getInt("imgid");

                switch (imgth) {

                    case 1:
                        identitycard_front=imgid;

                        idcard_zhengmian_iv.setImageBitmap(BitmapScale.toRoundCorner(idCard_zhengmian_bitmap,10));

                        break;

                    case 2:
                        identitycard_back=imgid;
                        idcard_fanmian_iv.setImageBitmap(BitmapScale.toRoundCorner(idCard_fanmian_bitmap,10));
                        break;

                    case 3:
                        hand_identitycard_front=imgid;
                        head_idcard_zhengmian_iv.setImageBitmap(BitmapScale.toRoundCorner(idCard_head_zhengmian_bitmap,10));
                        break;

                    case 4:
                        hand_identitycard_back=imgid;
                        head_idcard_fanmian_iv.setImageBitmap(BitmapScale.toRoundCorner(idCard_head_fanmian_bitmap,10));
                        break;

                }
            }else {
                Toast.makeText(RealNameActivity.this,"上传图片失败，请重试",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    HashMap<String, Object> params;
    //获取实名认证的参数
    private String getClientAuthParams(String psw){
        params = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            params.put("token", token);
        }

        params.put("timestamp",System.currentTimeMillis()/1000+"");

        params.put("password",psw);

        params.put("identitycard",idcard_num_et.getText().toString().trim());
        params.put("clientname",real_name_et.getText().toString().trim());
        params.put("identitycard_front",identitycard_front);
        params.put("identitycard_back",identitycard_back);
        params.put("hand_identitycard_front",hand_identitycard_front);
        params.put("hand_identitycard_back",hand_identitycard_back);

        String sign=ParamSign.getSign(params);

        params.put("sign",sign);

        return ParamSign.getSign(params);
    }



    //上传用户认证信息
    public void clientAuth(String psw){

        params= new HashMap<String, Object>();
        params.put("data",getClientAuthParams(psw));
        params=ParamSign.netWorkEncrypt(params);

        VolleyUtil.getVolleyUtil(RealNameActivity.this).StringRequestPostVolley(URLs.CLIENT_AUTH, params, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                try {
                    JSONObject obj=new JSONObject(jsonObject.toString());
                    if(obj.has("data")) {
                        String data = obj.getString("data");
                        byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                        String json=new String(encrypt,"UTF-8");
                        parseJson(json);//解析解密之后的数据
                    }else if(obj.has("code")){
                        int code=obj.getInt("code");
                        UIHelper.showErrTips(code,RealNameActivity.this);
                    }

//                    Toast.makeText(RealNameActivity.this,"实名认证成功",Toast.LENGTH_LONG).show();
//                    Toast.makeText(RealNameActivity.this,"实名认证失败，请重试",Toast.LENGTH_LONG).show();

                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }

            }

            @Override
            public void ResponError(VolleyError volleyError) {
            }
        });
    }

    //解析上传用户认证信息返回的数据
    private void parseJson(String json){
        try {
            JSONObject obj=new JSONObject(json);
            String status=obj.getString("status");
            int code=obj.getInt("code");
            UIHelper.showErrTips(code,RealNameActivity.this);
            if(status.equals("ok")){
                Toast.makeText(RealNameActivity.this,"实名认证成功",Toast.LENGTH_LONG).show();
                editor.putBoolean("isrealname",true);
                editor.commit();

                HashMap params1 = new HashMap();
                params1.put("auth",1);
                Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//登录成功后更新首页数据
                MainService.newTask(ts1);

                setResult(RESULT_OK);
                finish();
            }else{
                Toast.makeText(RealNameActivity.this,"实名认证失败，请重试",Toast.LENGTH_LONG).show();

                HashMap params1 = new HashMap();
                params1.put("auth",-1);
                Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//登录成功后更新首页数据
                MainService.newTask(ts1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    //获取用户认证信息
    public void getUserAuthInfo(){

        user_map= new HashMap<String, Object>();
        user_map.put("data",getUserInfoParams());
        user_map=ParamSign.netWorkEncrypt(user_map);

        VolleyUtil.getVolleyUtil(RealNameActivity.this).StringRequestPostVolley(URLs.CHECK_CLIENT_AUTH, user_map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(jsonObject.toString());
                    String data=obj.getString("data");
                    byte[] encrypt = TripleDES.decrypt(TripleDES.hexStringToBytes(data), CommentSetting.appkey.getBytes());//解密
                    String json=new String(encrypt,"UTF-8");

                    parseUserInfo(json);//解析解密之后的数据

                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });
    }


    HashMap<String, Object> user_map;
    public String getUserInfoParams(){
        user_map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        if(token!=null) {
            user_map.put("token", token);
        }

        user_map.put("timestamp",System.currentTimeMillis()/1000+"");

        String sign=ParamSign.getSign(user_map);

        user_map.put("sign",sign);

        return  ParamSign.getSign(user_map);
    }

    private BitmapManager bmpManager;//图片加载管理工具类
    //解析用户信息json
    public void parseUserInfo(String json){
        try {
            JSONObject obj=new JSONObject(json);
            String status=obj.getString("status");
            if(status.equals("ok")){


                JSONObject dataObj=obj.getJSONObject("data");
                int statu=dataObj.getInt("status");
                if(statu==1){
                    is_verify_tv.setText("已验证");
                    is_verify_tv.setTextColor(Color.BLUE);
                }else if(statu==-1){
                    is_verify_tv.setText("未验证");
                }

                idcard_num_et.setText(dataObj.getString("identitycard")+"");
                real_name_et.setText(dataObj.getString("clientname")+"");

                String identitycard_front_url=dataObj.getString("identitycard_front_url")+"";
                String identitycard_back_url=dataObj.getString("identitycard_back_url")+"";
                String hand_identitycard_front_url=dataObj.getString("hand_identitycard_front_url")+"";
                String hand_identitycard_back_url=dataObj.getString("hand_identitycard_back_url")+"";

                loadBitmap(identitycard_front_url,idcard_zhengmian_iv,1);
                loadBitmap(identitycard_back_url,idcard_fanmian_iv,2);
                loadBitmap(hand_identitycard_front_url,head_idcard_zhengmian_iv,3);
                loadBitmap(hand_identitycard_back_url,head_idcard_fanmian_iv,4);
            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void loadBitmap(String url,ImageView img,int type){
        if (url!=null&&url.endsWith("portrait.gif") || StringUtils.isEmpty(url)) {
            switch (type) {
                case 1:
                    img.setImageResource(R.drawable.shenfenzheng1);
                    break;
                case 2:
                    img.setImageResource(R.drawable.shenfenzheng2);
                    break;
                case 3:
                    img.setImageResource(R.drawable.shenfenzheng3);
                    break;
                case 4:
                    img.setImageResource(R.drawable.shenfenzheng3);
                    break;
            }
        } else {
            if (!url.contains("http")) {

            }
            bmpManager.loadBitmap(url,img);
        }
    }

}
