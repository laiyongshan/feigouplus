package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapScale;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/15 0015.
 */

public class UploadProActivity extends Activity implements View.OnClickListener{
    private ImageView uploadfile_back_img;
    private TextView heple_tv;
    private ImageView drivinglicense_img,drivingsecondlicense_img,majorviolation_img,majorsecondviolation_img;
    private TextView uploadfile_tv;

    private ProgressDialog pd;

    private BitmapManager bmpManager;//图片加载管理工具类

    private Bitmap drivinglicense_bitmap = null;//行驶证正页
    private Bitmap drivingsecondlicense_bitmap=null;//行驶证负页
    private Bitmap majorviolation_bitmap=null;//驾驶证正页
    private Bitmap majorsecondviolation_bitmap=null;//驾驶证负页
    private String strDrivinglicense="",strDrivingsecondlicense="",strMajorviolation="",strMajorsecondviolation="";
    private int drivinglicenseid, drivingsecondlicenseid,majorviolationid,majorsecondviolationid;

    private ImageButton  change_drivinglicense_img_btn,change_drivingsecondlicense_img_btn,change_majorviolation_img_btn,change_majorsecondviolation_img_btn;

    // 选择文件
    public static final int TO_SELECT_PHOTO1= 1;
    public static final int TO_SELECT_PHOTO2= 2;
    public static final int TO_SELECT_PHOTO3= 3;
    public static final int TO_SELECT_PHOTO4= 4;

    private String imgPath1,imgPath2,imgPath3,imgPath4;
    private String drivinglicenseUrl,drivingSecondLicenseUrl,majorviolationUrl,majorsecondviolationUrl;

    private String carId;

    private AppContext appContext;


    private int mScreenWidth;
    private int mScreenHeigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,UploadProActivity.this);
        }
        SystemBarUtil.useSystemBarTint(UploadProActivity.this);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        WindowManager wm = (WindowManager) (UploadProActivity.this.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth= dm.widthPixels;
        mScreenHeigh= dm.heightPixels;

        appContext= (AppContext) getApplicationContext();

        bmpManager= new BitmapManager(BitmapFactory.decodeResource(UploadProActivity.this.getResources(),R.drawable.zhengmian),UploadProActivity.this);

        initViews();//初始化控件

        Intent extraIntent=getIntent();
        carId=extraIntent.getStringExtra("carid");//传入的carId
        getCarInfo(EncryptUtil.encrypt(getCarInfoParams()));
    }

    /**
    * 初始化控件
    * */
    private void initViews(){
        uploadfile_back_img= (ImageView) findViewById(R.id.uploadfile_back_img);
        uploadfile_back_img.setOnClickListener(this);
        heple_tv= (TextView) findViewById(R.id.heple_tv);
        heple_tv.setOnClickListener(this);
        drivinglicense_img= (ImageView) findViewById(R.id.drivinglicense_img);
        drivinglicense_img.setOnClickListener(this);
        drivingsecondlicense_img= (ImageView) findViewById(R.id.drivingsecondlicense_img);
        drivingsecondlicense_img.setOnClickListener(this);
        majorviolation_img= (ImageView) findViewById(R.id.majorviolation_img);
        majorviolation_img.setOnClickListener(this);
        majorsecondviolation_img= (ImageView) findViewById(R.id.majorsecondviolation_img);
        majorsecondviolation_img.setOnClickListener(this);
        uploadfile_tv= (TextView) findViewById(R.id.uploadfile_tv);
        uploadfile_tv.setOnClickListener(this);

        change_drivinglicense_img_btn= (ImageButton) findViewById(R.id.change_drivinglicense_img_btn);
        change_drivinglicense_img_btn.setOnClickListener(this);

        change_drivingsecondlicense_img_btn= (ImageButton) findViewById(R.id.change_drivingsecondlicense_img_btn);
        change_drivingsecondlicense_img_btn.setOnClickListener(this);

        change_majorviolation_img_btn= (ImageButton) findViewById(R.id.change_majorviolation_img_btn);
        change_majorviolation_img_btn.setOnClickListener(this);

        change_majorsecondviolation_img_btn= (ImageButton) findViewById(R.id.change_majorsecondviolation_img_btn);
        change_majorsecondviolation_img_btn.setOnClickListener(this);

    }


    private void isShowChangeBtn(){
        if(!strDrivinglicense.equals("")){
            change_drivinglicense_img_btn.setVisibility(View.VISIBLE);
        }

        if(!strDrivingsecondlicense.equals("")){
            change_drivingsecondlicense_img_btn.setVisibility(View.VISIBLE);
        }

        if(!strMajorviolation.equals("")){
            change_majorviolation_img_btn.setVisibility(View.VISIBLE);
        }

        if(!strMajorsecondviolation.equals("")){
            change_majorsecondviolation_img_btn.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uploadfile_back_img:
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadProActivity.this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("您还未修改或上传选择的图片，确定要返回吗？");
                alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                break;

            case R.id.uploadfile_tv://立即上传
                if(((strDrivinglicense!=null&&strDrivingsecondlicense!=null)||(strMajorviolation!=null&&strMajorsecondviolation!=null))&&
                        ((strDrivinglicense.length()>0&&strDrivingsecondlicense.length()>0)||(strMajorviolation.length()>0&&strMajorsecondviolation.length()>0))){
                    if(appContext.isNetworkConnected()) {
                        pd.show();
                        uploadFile(getUploadFileParams());
                    }else {
                        UIHelper.showNetworkTips(UploadProActivity.this);
                    }
                }else{
                    Toast.makeText(UploadProActivity.this,"您还未完全选择图片",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.drivinglicense_img:
                    Intent intent1 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                    startActivityForResult(intent1, TO_SELECT_PHOTO1);
                break;

            case R.id.drivingsecondlicense_img:
                    Intent intent2 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                    startActivityForResult(intent2, TO_SELECT_PHOTO2);
                break;

            case R.id.majorviolation_img:
                    Intent intent3 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                    startActivityForResult(intent3, TO_SELECT_PHOTO3);
                break;

            case R.id.majorsecondviolation_img:
                    Intent intent4 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                    startActivityForResult(intent4, TO_SELECT_PHOTO4);
                break;

            case R.id.heple_tv:
                Intent helpIntent=new  Intent(UploadProActivity.this,UploadFileHelpActivity.class);
                startActivity(helpIntent);
                break;

            case R.id.change_drivinglicense_img_btn:
                Intent changeIntent1 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                startActivityForResult(changeIntent1, TO_SELECT_PHOTO1);
                break;

            case R.id.change_drivingsecondlicense_img_btn:
                Intent changeIntent2 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                startActivityForResult(changeIntent2, TO_SELECT_PHOTO2);
                break;

            case R.id.change_majorviolation_img_btn:
                Intent changeIntent3 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                startActivityForResult(changeIntent3, TO_SELECT_PHOTO3);
                break;

            case R.id.change_majorsecondviolation_img_btn:
                Intent changeIntent4 = new Intent(UploadProActivity.this, SelectPicActivity.class);
                startActivityForResult(changeIntent4, TO_SELECT_PHOTO4);
                break;
        }

    }


    //获取单辆车的车辆信息
    public HashMap<String,Object> getCarInfoParams(){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token",token);
        if(carId!=null) {
            map.put("carid", carId);//车辆id
        }else{
            map.put("carid","");
        }

        return map;
    }

    //获取上传文件的参数
    public HashMap<String, Object> getUploadFileParams(){
        map = new HashMap<String, Object>();
        String token = TokenSQLUtils.check();
        map.put("token",token);
        if(carId!=null) {
            map.put("carid", carId);//车辆id
        }else{
            map.put("carid","");
        }
        map.put("drivinglicenseid",drivinglicenseid);
        map.put("drivingsecondlicenseid",drivingsecondlicenseid);
        map.put("majorviolationid",majorviolationid);
        map.put("majorsecondviolationid",majorsecondviolationid);
//        map.put("drivinglicense",strDrivinglicense);//行驶证正页
//        map.put("drivingsecondlicense",strDrivingsecondlicense);//行驶证负页
//        map.put("majorviolation",strMajorviolation);//驾驶证正页
//        map.put("majorsecondviolation",strMajorsecondviolation);//驾驶证负页
        return EncryptUtil.encrypt(map)
                ;
    }


    //获取该辆车的行驶证和驾驶证的图片并显示
    public void getCarInfo(HashMap<String,Object> map){
        VolleyUtil.getVolleyUtil(UploadProActivity.this).StringRequestPostVolley(URLs.GET_ONE_CAR_INFO, map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject){
                Log.i("TAG","获取该辆车信息返回的信息："+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),UploadProActivity.this));
                    JSONObject dataObj=obj.getJSONObject("data");
                    drivinglicenseUrl=dataObj.optString("drivinglicenseurl");
                    drivingSecondLicenseUrl=dataObj.optString("drivingsecondlicenseurl");
                    majorviolationUrl=dataObj.optString("majorviolationurl");
                    majorsecondviolationUrl=dataObj.optString("majorsecondviolationurl");

                    drivinglicenseid=Integer.valueOf(dataObj.optString("drivinglicenseid"));
                    drivingsecondlicenseid=Integer.valueOf(dataObj.optString("drivingsecondlicenseid"));
                    majorviolationid=Integer.valueOf(dataObj.optString("majorviolationid"));
                    majorsecondviolationid=Integer.valueOf(dataObj.optString("majorsecondviolationid"));


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception",e.toString());
                }finally{
                    loadBitmap(drivinglicenseUrl,drivinglicense_img,1);//行驶证正页
                    loadBitmap(drivingSecondLicenseUrl,drivingsecondlicense_img,2);//行驶证负页
                    loadBitmap(majorviolationUrl,majorviolation_img,3);//驾驶证正页
                    loadBitmap(majorsecondviolationUrl,majorsecondviolation_img,4);//驾驶证负页

                    isShowChangeBtn();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(UploadProActivity.this,"网络异常\n"+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadBitmap(String url,ImageView img,int type){
        if (url!=null&&url.endsWith("portrait.gif") || StringUtils.isEmpty(url)) {
            switch (type) {
                case 1:
                    img.setImageResource(R.drawable.zhengmian);
                    break;
                case 2:
                    img.setImageResource(R.drawable.fanmian);
                    break;
                case 3:
                    img.setImageResource(R.drawable.zhengmian1);
                    break;
                case 4:
                    img.setImageResource(R.drawable.beimian1);
                    break;
            }
        } else {
            if (!url.contains("http")) {
                url = url;
            }
            bmpManager.loadBitmap(url,img);
        }
    }


    //上传文件
    Intent resultIntent=new Intent();
    public void uploadFile(HashMap<String, Object> map){
        VolleyUtil.getVolleyUtil(UploadProActivity.this).StringRequestPostVolley(URLs.UPLOAD_FILE, map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),UploadProActivity.this));
                    String status=obj.getString("status");
                    if(status.equals("ok")){
                        Toast.makeText(UploadProActivity.this,"上传成功,请继续操作",Toast.LENGTH_LONG).show();
                        resultIntent.putExtra("isUpdata",1);
                        setResult(1,resultIntent);
                        finish();
                    }else{
                        resultIntent.putExtra("isUpdata",0);
                        Toast.makeText(UploadProActivity.this,"上传失败，请重试",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                if(pd.isShowing()){
                    pd.dismiss();
                }
                //上传异常
                Toast.makeText(UploadProActivity.this,"上传异常，请重试\n"+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadProActivity.this);
            alertDialog.setTitle("提示");
            alertDialog.setMessage("您还未上传选择的图片，确定要返回吗？");
            alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case TO_SELECT_PHOTO1:
                if(data!=null) {
                    imgPath1 = data.getStringExtra("photo_path");
                }
                if(imgPath1!=null) {
                    Bitmap d1_bitmap=BitmapScale.getBitmap(imgPath1);
                    if(d1_bitmap!=null) {
                        if(d1_bitmap.getHeight()>d1_bitmap.getWidth()){
                            d1_bitmap=BitmapScale.rotaingImageView(90,d1_bitmap);
                        }
                        drivinglicense_bitmap=BitmapScale.fitBitmap(d1_bitmap,mScreenWidth*5/9);
                        Bitmap d1_bitmap_2=BitmapScale.fitBitmap(d1_bitmap,1000);

                        strDrivinglicense=BitmapScale.getImage(d1_bitmap_2);

                        uploadOnePhoto(strDrivinglicense,1);//上传行驶证正页
                    }
                }
                break;
            case TO_SELECT_PHOTO2:
                if(data!=null) {
                    imgPath2 = data.getStringExtra("photo_path");
                }
                if(imgPath2!=null) {
//                    Bitmap d2_bitmap=BitmapFactory.decodeFile(imgPath2);
                    Bitmap d2_bitmap=BitmapScale.getBitmap(imgPath2);
//                    drivingsecondlicense_bitmap = BitmapFactory.decodeFile(imgPath2);
                    if(d2_bitmap!=null) {
                        if(d2_bitmap.getHeight()>d2_bitmap.getWidth()){
                            d2_bitmap=BitmapScale.rotaingImageView(90,d2_bitmap);
                        }
                        // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                        drivingsecondlicense_bitmap=BitmapScale.fitBitmap(d2_bitmap,mScreenWidth*5/9);
                        Bitmap d2_bitmap_2=BitmapScale.fitBitmap(d2_bitmap,1000);

                        strDrivingsecondlicense=BitmapScale.getImage(d2_bitmap_2);
                        Log.i("TAG","strDrivingsecondlicense"+strDrivingsecondlicense);

                        uploadOnePhoto(strDrivingsecondlicense,2);//上传行驶证负页
                    }
                }
                break;
            case TO_SELECT_PHOTO3:
                if(data!=null) {
                    imgPath3 = data.getStringExtra("photo_path");
                }
                if(imgPath3!=null) {
                    Bitmap m1_bitmap=BitmapScale.getBitmap(imgPath3);
                    if(m1_bitmap!=null) {

                        if(m1_bitmap.getHeight()>m1_bitmap.getWidth()){
                            m1_bitmap=BitmapScale.rotaingImageView(90,m1_bitmap);
                        }

                        majorviolation_bitmap=BitmapScale.fitBitmap(m1_bitmap,mScreenWidth*5/9);
                        Bitmap m1_bitmap_2=BitmapScale.fitBitmap(m1_bitmap,1000);

                        strMajorviolation=BitmapScale.getImage(m1_bitmap_2);

                        uploadOnePhoto(strMajorviolation,3);//上传驾驶证正页
                    }
                }
                break;
            case TO_SELECT_PHOTO4:
                if(data!=null) {
                    imgPath4 = data.getStringExtra("photo_path");
                }
                if(imgPath4!=null) {
//                    majorsecondviolation_bitmap = BitmapFactory.decodeFile(imgPath4);
//                    Bitmap m2_bitmap=BitmapFactory.decodeFile(imgPath4);
                    Bitmap m2_bitmap=BitmapScale.getBitmap(imgPath4);
                    if(m2_bitmap!=null) {

                        if(m2_bitmap.getHeight()>m2_bitmap.getWidth()){
                            m2_bitmap=BitmapScale.rotaingImageView(90,m2_bitmap);
                        }

                        majorsecondviolation_bitmap=BitmapScale.fitBitmap(m2_bitmap,mScreenWidth*5/9);
                        Bitmap m2_bitmap_2=BitmapScale.fitBitmap(m2_bitmap,1000);

                        strMajorsecondviolation=BitmapScale.getImage(m2_bitmap_2);

                        uploadOnePhoto(strMajorsecondviolation,4);//上传驾驶证副页
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    HashMap map;
    private final String IMAGE_TYPE="car";
    private HashMap getOnePhotoParam(String imgtype){
        map = new HashMap();
        map.put("imgtype",imgtype);
        map.put("token",TokenSQLUtils.check()+"");
        return EncryptUtil.encrypt(map);
    }

    private void uploadOnePhoto(String strImg, final int imgth){

        pd.show();

        map=getOnePhotoParam(IMAGE_TYPE);
        map.put("img",strImg);
        VolleyUtil.getVolleyUtil(UploadProActivity.this).StringRequestPostVolley(URLs.ADD_IMG_URL,map,new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    String  json=EncryptUtil.decryptJson(jsonObject.toString(),UploadProActivity.this);

                    parseJson(json,imgth);//解析解密之后的数据

                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(UploadProActivity.this,"上传图片返回的错误信息："+volleyError.toString(),Toast.LENGTH_SHORT).show();
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
                        drivinglicenseid=imgid;

                        drivinglicense_img.setImageBitmap(BitmapScale.toRoundCorner(drivinglicense_bitmap,10));

                        break;

                    case 2:
                        drivingsecondlicenseid=imgid;
                        drivingsecondlicense_img.setImageBitmap(BitmapScale.toRoundCorner(drivingsecondlicense_bitmap,10));
                        break;

                    case 3:
                        majorviolationid=imgid;
                        majorviolation_img.setImageBitmap(BitmapScale.toRoundCorner(majorviolation_bitmap,10));
                        break;

                    case 4:
                        majorsecondviolationid=imgid;
                        majorsecondviolation_img.setImageBitmap(BitmapScale.toRoundCorner(majorsecondviolation_bitmap,10));
                        break;

                }
            }else {
                Toast.makeText(UploadProActivity.this,"上传图片失败，请重试",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
