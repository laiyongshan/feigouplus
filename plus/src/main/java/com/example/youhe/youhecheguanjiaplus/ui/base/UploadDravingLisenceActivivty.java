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
 * Created by Administrator on 2016/11/18 0018.
 */

public class UploadDravingLisenceActivivty extends Activity implements View.OnClickListener{

    private ImageView uploadlisence_back_img;
    private TextView add_dravinglisence_heple_tv;//帮助
    private ImageView drivinglicense_img,drivingsecondlicense_img;
    private TextView upload_dravinglisence_tv;

    private BitmapManager bmpManager;//图片加载管理工具类

    private ProgressDialog pd;

    private String drivinglicenseUrl="",drivingSecondLicenseUrl="";

    private Bitmap drivinglicense_bitmap = null;//行驶证正页
    private Bitmap drivingsecondlicense_bitmap=null;//行驶证负页
    private String strDrivinglicense="",strDrivingsecondlicense="";
    private int drivinglicenseid,drivingsecondlicenseid;

    private ImageButton  change_drivinglicense_img_btn,change_drivingsecondlicense_img_btn;

    // 选择文件
    public static final int TO_SELECT_PHOTO1= 1;
    public static final int TO_SELECT_PHOTO2= 2;

    private String imgPath1,imgPath2;

    private String carId;

    public static final int RESULT_CODE=110;

    private AppContext appContext;

    private int mScreenWidth;
    private int mScreenHeigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_license);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,UploadDravingLisenceActivivty.this);
        }

        SystemBarUtil.useSystemBarTint(UploadDravingLisenceActivivty.this);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        appContext= (AppContext) getApplicationContext();

        bmpManager= new BitmapManager(BitmapFactory.decodeResource(UploadDravingLisenceActivivty.this.getResources(),R.drawable.zhengmian),UploadDravingLisenceActivivty.this);

        initViews();//初始化控件

        Intent extraIntent=getIntent();
        carId=extraIntent.getStringExtra("carid");//传入的carId

        WindowManager wm = (WindowManager) (UploadDravingLisenceActivivty.this.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth= dm.widthPixels;
        mScreenHeigh= dm.heightPixels;

        getCarInfo(getCarInfoParams());
    }

    private void initViews(){
        uploadlisence_back_img= (ImageView) findViewById(R.id.uploadlisence_back_img);
        uploadlisence_back_img.setOnClickListener(this);
        add_dravinglisence_heple_tv= (TextView) findViewById(R.id.add_dravinglisence_heple_tv);
        add_dravinglisence_heple_tv.setOnClickListener(this);
        drivinglicense_img= (ImageView) findViewById(R.id.drivinglicense_img);
        drivinglicense_img.setOnClickListener(this);
        drivingsecondlicense_img= (ImageView) findViewById(R.id.drivingsecondlicense_img);
        drivingsecondlicense_img.setOnClickListener(this);
        upload_dravinglisence_tv= (TextView) findViewById(R.id.upload_dravinglisence_tv);
        upload_dravinglisence_tv.setOnClickListener(this);

        change_drivinglicense_img_btn= (ImageButton) findViewById(R.id.change_drivinglicense_img_btn);
        change_drivinglicense_img_btn.setOnClickListener(this);

        change_drivingsecondlicense_img_btn= (ImageButton) findViewById(R.id.change_drivingsecondlicense_img_btn);
        change_drivingsecondlicense_img_btn.setOnClickListener(this);

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

    //获取该辆车的行驶证和驾驶证的图片并显示
    public void getCarInfo(HashMap<String,Object> map){
        VolleyUtil.getVolleyUtil(UploadDravingLisenceActivivty.this).StringRequestPostVolley(URLs.GET_ONE_CAR_INFO, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject)  {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),UploadDravingLisenceActivivty.this));
                    JSONObject dataObj=obj.getJSONObject("data");
                    drivinglicenseUrl=dataObj.optString("drivinglicenseurl");
                    drivingSecondLicenseUrl=dataObj.optString("drivingsecondlicenseurl");

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    loadBitmap(drivinglicenseUrl,drivinglicense_img,1);//行驶证正页
                    loadBitmap(drivingSecondLicenseUrl,drivingsecondlicense_img,2);//行驶证负页

                    isShowChangeBtn();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(UploadDravingLisenceActivivty.this,"网络异常\n"+volleyError.toString(),Toast.LENGTH_LONG).show();
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
            }
        } else {
            if (!url.contains("http")) {
                url = url;
            }
            bmpManager.loadBitmap(url,img);
        }
    }

    private void isShowChangeBtn(){
        if(!strDrivinglicense.equals("")){
            change_drivinglicense_img_btn.setVisibility(View.VISIBLE);
        }

        if(!strDrivingsecondlicense.equals("")){
            change_drivingsecondlicense_img_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.uploadlisence_back_img:
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadDravingLisenceActivivty.this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("您还未上传选择的图片，确定要返回吗？");
                alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resultIntent.putExtra("isUpdataS",1);//未上传
                        setResult(RESULT_CODE,resultIntent);
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

            case R.id.add_dravinglisence_heple_tv:
                Intent helpIntent=new Intent(UploadDravingLisenceActivivty.this,UploadFileHelpActivity.class);
                startActivity(helpIntent);
                break;

            case R.id.drivinglicense_img:
                    Intent intent1 = new Intent(UploadDravingLisenceActivivty.this, SelectPicActivity.class);
                    startActivityForResult(intent1, TO_SELECT_PHOTO1);
                break;

            case R.id.drivingsecondlicense_img:
                    Intent intent2 = new Intent(UploadDravingLisenceActivivty.this, SelectPicActivity.class);
                    startActivityForResult(intent2, TO_SELECT_PHOTO2);
                break;

            case R.id.upload_dravinglisence_tv:
                if (appContext.isNetworkConnected()) {
                    if((strDrivinglicense!=null&&strDrivingsecondlicense!=null)&&(
                            strDrivinglicense.length()>0&&strDrivingsecondlicense.length()>0)) {
                        pd.show();
                        uploadDravingLisence(getUploadFileParams());
                    }else{
                        Toast.makeText(this, "您还未全部选择图片！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    UIHelper.showNetworkTips(UploadDravingLisenceActivivty.this);
                }
                break;

            case R.id.change_drivinglicense_img_btn:
                Intent changeIntent1= new Intent(UploadDravingLisenceActivivty.this, SelectPicActivity.class);
                startActivityForResult(changeIntent1, TO_SELECT_PHOTO1);
                break;

            case R.id.change_drivingsecondlicense_img_btn:
                Intent changeIntent2= new Intent(UploadDravingLisenceActivivty.this, SelectPicActivity.class);
                startActivityForResult(changeIntent2, TO_SELECT_PHOTO2);
                break;
        }

    }

    //获取上传文件的参数
//    HashMap map;
    public HashMap getUploadFileParams(){
        map = new HashMap();
        String token = TokenSQLUtils.check();
        map.put("token",token);
        if(carId!=null) {
            map.put("carid", carId);//车辆id
        }else{
            map.put("carid","");
        }
        map.put("drivinglicenseid",drivinglicenseid);
        map.put("drivingsecondlicenseid",drivingsecondlicenseid);
        return EncryptUtil.encrypt(map);
    }

    //上传文件
    Intent resultIntent=new Intent();
    public void uploadDravingLisence(HashMap<String, Object> map){
        VolleyUtil.getVolleyUtil(UploadDravingLisenceActivivty.this).StringRequestPostVolley(URLs.UPLOAD_FILE,map,new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),UploadDravingLisenceActivivty.this));
                    String status=obj.getString("status");
                    if(status.equals("ok")){
                        Toast.makeText(UploadDravingLisenceActivivty.this,"上传图片成功，请继续操作",Toast.LENGTH_LONG).show();
                        resultIntent.putExtra("isUpdataS",0);//上传成功
                        setResult(RESULT_CODE,resultIntent);
                        finish();
                    }else{
                        resultIntent.putExtra("isUpdataS",1);//上传失败
                        Toast.makeText(UploadDravingLisenceActivivty.this,"上传失败，请重试",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                //上传异常
                if(pd.isShowing()){
                    pd.dismiss();
                }
                Toast.makeText(UploadDravingLisenceActivivty.this,"上传异常，请重试\n"+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(UploadDravingLisenceActivivty.this);
            alertDialog.setTitle("提示");
            alertDialog.setMessage("您还未修改或上传选择的图片，确定要返回吗？");
            alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resultIntent.putExtra("isUpdataS",1);//上传失败
                    setResult(RESULT_CODE,resultIntent);
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
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case TO_SELECT_PHOTO1:
                if(data!=null) {
                    imgPath1 = data.getStringExtra("photo_path");
                }
                if(imgPath1!=null) {
                    Bitmap d1_bitmap=BitmapScale.getBitmap(imgPath1);

//                    Bitmap d1_bitmap=BitmapFactory.decodeFile(imgPath1);
//                    drivinglicense_bitmap = BitmapFactory.decodeFile(imgPath1);
                    if(d1_bitmap!=null) {
//                        drivinglicense_bitmap= BitmapScale.fitBitmap(d1_bitmap,500);
                        if(d1_bitmap.getHeight()>d1_bitmap.getWidth()){
                            d1_bitmap=BitmapScale.rotaingImageView(90,d1_bitmap);
                        }
                        drivinglicense_bitmap=BitmapScale.fitBitmap(d1_bitmap,mScreenWidth*5/9);
                        Bitmap d1_bitmap_2=BitmapScale.fitBitmap(d1_bitmap,1000);
                        strDrivinglicense= BitmapScale.getImage(d1_bitmap_2);

                        uploadOnePhoto(strDrivinglicense,1);//上传行驶证正页
                    }
                }
                break;

            case TO_SELECT_PHOTO2:
                if(data!=null) {
                    imgPath2 = data.getStringExtra("photo_path");
                }
                if(imgPath2!=null) {
                    Bitmap d2_bitmap=BitmapScale.getBitmap(imgPath2);
                    if(d2_bitmap!=null) {
                        if(d2_bitmap.getHeight()>d2_bitmap.getWidth()){
                            d2_bitmap=BitmapScale.rotaingImageView(90,d2_bitmap);
                        }
                        // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                        drivingsecondlicense_bitmap=BitmapScale.fitBitmap(d2_bitmap,mScreenWidth*5/9);
                        Bitmap d2_bitmap_2=BitmapScale.fitBitmap(d2_bitmap,1000);

                        strDrivingsecondlicense=BitmapScale.getImage(d2_bitmap_2);
                        uploadOnePhoto(strDrivingsecondlicense,2);//上传行驶证副页
                    }
                }
                break;

        }
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
        VolleyUtil.getVolleyUtil(UploadDravingLisenceActivivty.this).StringRequestPostVolley(URLs.ADD_IMG_URL,map,new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                try {
                    String  json=EncryptUtil.decryptJson(jsonObject.toString(),UploadDravingLisenceActivivty.this);

                    parseJson(json,imgth);//解析解密之后的数据

                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(UploadDravingLisenceActivivty.this,"上传图片返回的错误信息："+volleyError.toString(),Toast.LENGTH_SHORT).show();
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


                }
            }else {
                Toast.makeText(UploadDravingLisenceActivivty.this,"上传图片失败，请重试",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
