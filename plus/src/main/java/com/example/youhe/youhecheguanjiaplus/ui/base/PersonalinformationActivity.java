package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.bean.FirstEvent;
import com.example.youhe.youhecheguanjiaplus.biz.SaveNameDao;
import com.example.youhe.youhecheguanjiaplus.city.CityActivty;
import com.example.youhe.youhecheguanjiaplus.db.biz.NamePathSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.StatusSQLUtils;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.MainService;
import com.example.youhe.youhecheguanjiaplus.logic.Task;
import com.example.youhe.youhecheguanjiaplus.logic.TaskType;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapScale;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.ImageUtils;
import com.example.youhe.youhecheguanjiaplus.utils.ImgDownload;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;

/**
 * 个人信息界面
 */
public class PersonalinformationActivity extends Activity {
    private ImageView circleImageView;//头像
    private TextView name,tv_num;//昵称和手机号
    private TextView clity;//选择的城市位置
    private String city;
    private String nameText;
    private SaveNameDao saveNameDao;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TokenSQLUtils tokenSQLUtils;
    private StatusSQLUtils statusSQLUtils;
    private ImageUtils imageUtils;
    private RelativeLayout exitBtn;//退出按钮
    private UIDialog uiDialog=null;

    private final int CITY_REQUESTCODE = 2;

//    private String imgPath;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//       if(resultCode==RESULT_OK) {//头像选择结果
//           Log.i("imgPath","imgPath："+imgPath);
//           if (data != null) {
//               imgPath = data.getStringExtra("photo_path");
//
//           }
//           if (imgPath != null) {
//               Bitmap head_bitmap = BitmapScale.getBitmap(imgPath);
//
//               if (head_bitmap != null) {
//                   circleImageView.setImageBitmap(BitmapScale.toRoundBitmap(head_bitmap));
//               }
//           }
//       }
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinformation);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,PersonalinformationActivity.this);
        }
        SystemBarUtil.useSystemBarTint(PersonalinformationActivity.this);

        uiDialog=new UIDialog(this,"Loading...");
        x.view().inject(this);
        in();//初始化

    }

    /**
     * 初始化
     */
    private void in() {


        EventBus.getDefault().register(this);
        statusSQLUtils = new StatusSQLUtils(this);
        imageUtils = new ImageUtils();
        circleImageView = (ImageView) findViewById(R.id.iv_headportrait);//头像
        tokenSQLUtils = new TokenSQLUtils(this);
        clity = (TextView) findViewById(R.id.clity);//城市位置

        saveNameDao = new SaveNameDao(this);
        name = (TextView) findViewById(R.id.tv_name);//昵称
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_num.setText(StringUtils.showNum(saveNameDao.readText("phonenumbe.txt")));//把登录的电话号码显示出来
        sp = getSharedPreferences("judges", Context.MODE_PRIVATE);
        editor = sp.edit();

        exitBtn= (RelativeLayout) findViewById(R.id.rlay6);
//        if(!AppContext.isLogin) {
//            exitBtn.setClickable(false);
//            exitBtn.setBackgroundColor(Color.GRAY);
//        }
        setCity();//设置城市
    }

    /**
     * 布局中的菜单点击事件
     * @param view
     */
    public void dianji(View view){

        switch (view.getId()){

            case R.id.rlay1://头像
                inSelectMeu();//弹出菜单选择操作
                break;

            case R.id.rlay2://昵称
                startActivity(new Intent(this,TheNameOfTheSetActivity.class));
                break;

            case R.id.rlay4://地区
                startActivityForResult(new Intent(this,CityActivty.class),CITY_REQUESTCODE);
                break;

            case R.id.rlay5://更换手机号码
                startActivity(new Intent(this,ReplaceMobilePhoneNumberActivity.class));
                break;

            case R.id.rlay7://修改密码
                Intent intent = new Intent(this,ChangePasswordActivity.class);
                startActivity(intent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_from_left);
                break;

            case R.id.rlay6://退出登录
                saveTheLoginStatus();
                editor.putBoolean("judge", false);
                editor.commit();
                break;
        }

    }

    /**
     * 点击头像弹出菜单
     */
//    private HeadPortraitPopupWindow menuWindow;//自定义的弹出框类
    private void inSelectMeu() {//初始化弹出菜单
//        //实例化SelectPicPopupWindow
//        menuWindow = new HeadPortraitPopupWindow(PersonalinformationActivity.this, itemsOnClick);
//        menuWindow.setAnimationStyle(R.style.popwin_anim_style);
//        //设置layout在PopupWindow中显示的位置
//        menuWindow.showAtLocation(PersonalinformationActivity.this.findViewById(R.id.main),
//                Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
        Intent intent1 = new Intent(PersonalinformationActivity.this, SelectPicActivity.class);
        startActivityForResult(intent1, 1);
    }

//    /**
//     *  点击头像弹出窗口实现监听类
//     */
//    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
//        public void onClick(View v) {
////            menuWindow.dismiss();
//            switch (v.getId()) {
//
//                case R.id.btn_photo://拍照
//                    CameraDao photoAlbumDao = new CameraDao(PersonalinformationActivity.this);
//                    photoAlbumDao.takePhoto();
//                    break;
//
//                case R.id.btn_pick_photo://从相册中选取
//                    photoAlbum.onlick();
//                    break;
//            }
//        }
//    };


    /**
     * 返回键点击事件
     *
     */
    public void fanhui(View view){
        finish();
    }

    private Handler handler = new Handler(){//退出清除Token值

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    uidialog.hide();

                    HashMap params=new HashMap();
                    Task ts=new Task(TaskType.TS_USER_EXIT,params);//退出
                    MainService.newTask(ts);
                    finish();
                    break;

            }
        }
    };
    private UIDialog uidialog;
    /**
     * 保存退出登录状态
     */
    public void saveTheLoginStatus(){
        uidialog = new UIDialog(this,"正在退出.......");
        uidialog.show();
        tokenSQLUtils.delete();//清除Token值
        statusSQLUtils.undateApi("no");//保存退出状态
//        UserManager.setIsPlus(false);
        UserManager.setUserType(UserManager.USER_NORMAL);

        EventBus.getDefault().post(new FirstEvent("no"));
        handler.sendEmptyMessageDelayed(1,1500);

        HashMap params1 = new HashMap();
        params1.put("auth",-1);
        Task ts1 = new Task(TaskType.TS_REAL_NAME, params1);//退出成功后更新个人中心数据
        MainService.newTask(ts1);


    }

    @Subscribe
    public void onEventMainThread(FirstEvent event) {
        Log.i("WU","设置昵称");
        if (event.getMsg().equals("rush")){
            setName();//设置昵称
        }
        if (event.getMsg().equals("huan")){
            tv_num.setText(saveNameDao.readText("phonenumbe.txt"));//把登录的电话号码显示出来
        }

    }

    /**
     * 每次这个界面出现都要判断
     */
    @Override
    public void onResume() {
        super.onResume();

        touxiang();//头像
        setName();//设置昵称

    }

    /**
     * 设置头像
     */
    public void touxiang(){

        Bitmap head_bitmap = BitmapScale.getDiskBitmap(Environment.getExternalStorageDirectory().toString()+"/yeohe/head_bitmap/"
                +saveNameDao.readText("phonenumbe.txt").trim()+ "head.jpg");
//        Bitmap head_bitmap =getHearImage();
        if(head_bitmap!=null){
            circleImageView.setImageBitmap(ImgDownload.toRoundBitmap(head_bitmap));
        }else {
            circleImageView.setImageResource(R.drawable.gerz_01);
        }

    }

    /**
     * 设置名称
     */
    private NamePathSQLUtils sqlUtils;
    public void setName(){
        sqlUtils = new NamePathSQLUtils(this);
        String httpNames = sqlUtils.check();//网上的昵称
        if (httpNames.equals("")){

        }else {
            name.setText(httpNames);
        }


    }

    public void setCity(){
//        String q = saveNameDao.readText("City.txt");
//        if (q.equals("")){
//
//        }else {
//            if(q!=null){
//                city =q.trim();//得到城市文本
//                if(city!=null){
//                    clity.setText(city);
//                }
//            }
//        }
        city = ((AppContext) (PersonalinformationActivity.this.getApplication())).getLocalCity().toString();
        if (city != null && (!city.equals(""))) {
            clity.setText(city.toString());
        } else {
            clity.setText("广州");
        }
        clity.setText(city);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private String imgPath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1) {//头像选择结果

           if (data != null) {
               imgPath = data.getStringExtra("photo_path");

           }

           if (imgPath != null) {
               Bitmap head_bitmap = BitmapScale.getBitmap(imgPath);

               if (head_bitmap != null) {
                   Log.i("imgPath","imgPath："+imgPath);
                   uploadOnePhoto(BitmapScale.getImage(head_bitmap),head_bitmap);
               }else{
                   circleImageView.setImageResource(R.drawable.gerz_01);
               }
           }
       }

       if(requestCode==CITY_REQUESTCODE) {
           if (data != null) {
               city = data.getStringExtra("city") + "";
               Log.i("TAG", city + "");
               if ((city != null) && (!city.equals(""))) {
                   clity.setText(city.toString());
               } else {
                   clity.setText("广州");
               }
           }
       }
    }


    /**
     * 以base64上传图片
     *
     * @param bitmap
     */
    HashMap pams=new HashMap();
    public void httpImgs(final Bitmap bitmap,int imgid) {

        pams.put("token", tokenSQLUtils.check());
        pams.put("headimg", imgid);
        VolleyUtil.getVolleyUtil(PersonalinformationActivity.this).StringRequestPostVolley(URLs.UPLOAD_THE_PICTURE, EncryptUtil.encrypt(pams), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject){
                Log.i("TAG", "上传图片Id:"+jsonObject.toString());
                jsonParsing(EncryptUtil.decryptJson(jsonObject.toString(),PersonalinformationActivity.this), bitmap);
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                ToastUtil.getShortToastByString(PersonalinformationActivity.this, "网络连接失败,无法发送请求");
            }
        });

    }

    public void jsonParsing(String json, Bitmap mbitmap) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            if (status.equals("ok")) {
                BitmapScale.saveFile(mbitmap,saveNameDao.readText("phonenumbe.txt").trim());//保存图片


                //把图片变成圆
                circleImageView.setImageBitmap(ImgDownload.toRoundBitmap(mbitmap));
                try {
                    ImageUtils.saveImage(circleImageView.getContext(),"touxiang.png",mbitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveNameDao.writeTxtToFile("yes", "qqq.txt");
//                UserManager.setValue(UserManager.SP_USER_HEAR_IMG_URL,jsonObject.getJSONObject("data").getString("imgurl"));
                ToastUtil.getLongToastByString(PersonalinformationActivity.this, "头像上传成功");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    HashMap map;
    private final String IMAGE_TYPE="head";
    private HashMap getOnePhotoParam(String imgtype){
        map = new HashMap();
        map.put("imgtype",IMAGE_TYPE);
        map.put("token", TokenSQLUtils.check()+"");
        return EncryptUtil.encrypt(map);
    }

    private void uploadOnePhoto(String strImg, final Bitmap bitmap){

        map=getOnePhotoParam(IMAGE_TYPE);
        map.put("img",strImg);
        if (uiDialog!=null)
            uiDialog.show();
        VolleyUtil.getVolleyUtil(PersonalinformationActivity.this).StringRequestPostVolley(URLs.ADD_IMG_URL,map,new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","头像返回的数据："+jsonObject.toString());

                try {
                    String  json= EncryptUtil.decryptJson(jsonObject.toString(),PersonalinformationActivity.this);

                    parseJson(json,bitmap);//解析解密之后的数据

                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (uiDialog!=null)
                        uiDialog.hide();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                if (uiDialog!=null)
                    uiDialog.hide();
                Toast.makeText(PersonalinformationActivity.this,"上传头像返回的错误信息："+volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseJson(String json,Bitmap bitmap){

        try {
            JSONObject newObj = new JSONObject(json);
            String status=newObj.getString("status");
            if(status.equals("ok")) {
                JSONObject dataObj = newObj.getJSONObject("data");
                int imgid = dataObj.getInt("imgid");
                httpImgs(bitmap,imgid);
            }else {
                Toast.makeText(PersonalinformationActivity.this,"上传头像失败，请重试",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
