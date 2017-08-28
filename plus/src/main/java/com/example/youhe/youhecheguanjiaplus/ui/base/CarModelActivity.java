package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.CarModel;
import com.example.youhe.youhecheguanjiaplus.city.CharacterParser;
import com.example.youhe.youhecheguanjiaplus.city.IndexListView;
import com.example.youhe.youhecheguanjiaplus.city.PinyinComparator2;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.BitmapManager;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CarModelActivity extends ActionBarActivity implements View.OnClickListener {

    private BaseAdapter adapter;
    private ListView car_model_lv;
    private TextView overlay;// 对话框首字母textview
    private IndexListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框
    public ArrayList<CarModel> all_car_lists; // 所有车辆品牌列表
    private ArrayList<CarModel> car_linea_lists;//汽车品牌子品牌
    ListAdapter.TopViewHolder topViewHolder;

//    private ClearEditText filter_car_model_et;

    private DrawerLayout car_linea_drawerlayout;
    private ListView car_line_menu_lv;

    private ImageView car_model_back_img;

    private BitmapManager bmpManager;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator2 pinyinComparator2;


    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_model);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CarModelActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CarModelActivity.this);


        pd=new ProgressDialog(CarModelActivity.this);
        pd.setMessage("请稍候...");
        pd.setCanceledOnTouchOutside(false);

        bmpManager= new BitmapManager(BitmapFactory.decodeResource(this.getResources(),R.drawable.icon_car_bran_logo),this);

        /*初始化Toolbar与DrawableLayout*/
        initToolBarAndDrawableLayout();

        pinyinComparator2 = new PinyinComparator2();
        car_model_lv= (ListView) findViewById(R.id.car_model_lv);


        all_car_lists=new ArrayList<CarModel>();
        car_linea_lists=new ArrayList<CarModel>();

        letterListView = (IndexListView) findViewById(R.id.car_model_letterListView);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();

        car_model_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if(arg2==0){
                    Intent intent=new Intent();
                    intent.putExtra("carbrand","其他");
                    intent.putExtra("carname","其他");
                    setResult(RESULT_OK,intent);
                    CarModelActivity.this.finish();
                }else {
                    pd.show();
                    getCarLineaList(all_car_lists.get(arg2).getId());
                }
            }
        });

        car_model_lv.setAdapter(adapter);
        initOverlay();

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pd.show();
        getCarTypeList();//获取车型列表

    }


    /*初始化Toolbar与DrawableLayout*/
    private void initToolBarAndDrawableLayout(){

        car_model_back_img= (ImageView) findViewById(R.id.car_model_back_img);
        car_model_back_img.setOnClickListener(this);

        car_linea_drawerlayout= (DrawerLayout) findViewById(R.id.car_linea_drawerlayout);
        car_linea_drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        car_line_menu_lv= (ListView) findViewById(R.id.car_line_menu_lv);
        car_line_menu_lv.setOnItemClickListener(new MyOnItemClickListener());

    }


    /**
     * a-z排序
     */
    Comparator comparator = new Comparator<CarModel>() {
        @Override
        public int compare(CarModel lhs, CarModel rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    //子品牌排序
    Comparator comparator2=new Comparator<CarModel>() {
        @Override
        public int compare(CarModel o, CarModel t1) {
            String a=o.getFullname().substring(0,1);
            String b=t1.getFullname().substring(0,1);
            int flag=a.compareTo(b);
            if(flag==0){
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };


    private void setAdapter(List<CarModel> list) {
        adapter = new ListAdapter(this, list,1);
        adapter.notifyDataSetChanged();
        car_model_lv.setAdapter(adapter);

    }


    //设置汽车品牌子品牌Adapter
    private void setDrawerlayoutAdapter(List<CarModel> list){
        BaseAdapter adapter2=new ListAdapter(this,list,2);
        adapter2.notifyDataSetChanged();
        car_line_menu_lv.setAdapter(adapter2);
        car_linea_drawerlayout.openDrawer(Gravity.RIGHT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.car_model_back_img:
                finish();
                break;

        }
    }


    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<CarModel> list;
        final int VIEW_TYPE = 2;

        private int type=0;

        public ListAdapter(Context context, List<CarModel> list,int type) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.type=type;
            if(type==1) {
                alphaIndexer = new HashMap<String, Integer>();
                sections = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    // 当前汉语拼音首字母
                    String currentStr = getAlpha(list.get(i).getPinyi());
                    // 上一个汉语拼音首字母，如果不存在为“ ”
                    String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
                            .getPinyi()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        String name = getAlpha(list.get(i).getPinyi());
                        alphaIndexer.put(name, i);
                        sections[i] = name;
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            int type = 0;
            if (position == 0) {
                type = 1;
            }
            else if(position==1){
                type=0;
            }
            return type;
        }

        @Override
        public int getViewTypeCount(){// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int viewType = getItemViewType(position);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_car_type_list, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.car_type_alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.car_type_name);

                    holder.car_type_logo_iv= (ImageView) convertView.findViewById(R.id.car_type_logo_iv);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                bmpManager.loadLogoBitmap(list.get(position).getLogo()+"", holder.car_type_logo_iv,
                        BitmapFactory.decodeResource(CarModelActivity.this.getResources(),R.drawable.icon_car_bran_logo),260,260);

                holder.name.setText(list.get(position).getName());

            if(type==1) {
                //主要汽车品牌
                String currentStr = getAlpha(list.get(position).getPinyi());
                String previewStr = (position - 1) >= 0 ? getAlpha(list
                        .get(position - 1).getPinyi()) : " ";
                if (!previewStr.equals(currentStr)) {
                    holder.alpha.setVisibility(View.VISIBLE);
                    if (currentStr.equals("#")) {
                        currentStr = "#";
                    }
                    holder.alpha.setText(currentStr);
                } else {
                    holder.alpha.setVisibility(View.GONE);
                }
            }else if(type==2){
                //汽车子品牌
                String currentStr = list.get(position).getFullname();
                String previewStr = (position - 1) >= 0 ? list.get(position-1).getFullname() : " ";
                if (!previewStr.equals(currentStr)) {
                    holder.alpha.setVisibility(View.VISIBLE);
                    if (currentStr.equals("#")) {
                        currentStr = "#";
                    }
                    holder.alpha.setText(currentStr);
                } else {
                    holder.alpha.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 车型名字
            ImageView car_type_logo_iv;//车型logo
        }

        private class TopViewHolder {
            TextView alpha; // 首字母标题
            TextView name;
        }

        private class ShViewHolder {
            ClearEditText editText;

        }
    }




    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }



    private class LetterListViewListener implements
            IndexListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                car_model_lv.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }



    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }

    // 获得汉语拼音首字母
    private String getAlpha(String str) {

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        ArrayList<CarModel> filterDateList = new ArrayList<CarModel>();

        if(TextUtils.isEmpty(filterStr)){
//            filterDateList = allCity_lists;
            setAdapter(all_car_lists);
        }else{
            filterDateList.clear();
            for(CarModel sortModel : all_car_lists){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
            // 根据a-z进行排序
            Collections.sort(filterDateList, pinyinComparator2);
            updateListView(filterDateList);
        }

    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(ArrayList<CarModel> list){
        this.all_car_lists = list;
        setAdapter(list);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if(car_linea_drawerlayout.isDrawerOpen(Gravity.RIGHT)){
                car_linea_drawerlayout.closeDrawer(Gravity.RIGHT);
            }else {
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                return false;
            }
        }
        return false;
    }


    //获取车型数据列表
    HashMap map;
    public void getCarTypeList(){
        map=new HashMap();
        String token= TokenSQLUtils.check();
        map.put("token",token);

        VolleyUtil.getVolleyUtil(CarModelActivity.this).StringRequestPostVolley(URLs.GET_CAR_BRAND_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                parseJson(EncryptUtil.decryptJson(jsonObject.toString(),CarModelActivity.this));

                setAdapter(all_car_lists);

                pd.dismiss();

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });
    }

    private void parseJson(String jsonStr){
        CarModel carModel;

        carModel=new CarModel();
        carModel.setName("其他");
        carModel.setPinyi("#");
        all_car_lists.add(carModel);

        try {
            JSONObject obj=new JSONObject(jsonStr);
            JSONObject dataObj=obj.getJSONObject("data");
            if(dataObj.has("brandList")){
                JSONArray brandArr=dataObj.getJSONArray("brandList");
                for(int i=0;i<brandArr.length();i++){
                    carModel=new CarModel();
                    String name=brandArr.getJSONObject(i).getString("name");
                    carModel.setId(brandArr.getJSONObject(i).getString("id"));
                    carModel.setName(name);
                    carModel.setLogo(brandArr.getJSONObject(i).getString("logo"));
                    carModel.setPinyi(characterParser.getSelling(name));

                    all_car_lists.add(carModel);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(all_car_lists, comparator);

    }


    //获取汽车品牌子品牌列表
    private void getCarLineaList(String brandid){
        map=new HashMap();
        map.put("brandid",brandid);
        String token= TokenSQLUtils.check();
        map.put("token",token);
        VolleyUtil.getVolleyUtil(CarModelActivity.this).StringRequestPostVolley(URLs.GET_CAR_LIST_BY_BRAND, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {

                paseCarLineaJson(EncryptUtil.decryptJson(jsonObject.toString(),CarModelActivity.this));
                setDrawerlayoutAdapter(car_linea_lists);

                pd.dismiss();

            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });

    }

    private void paseCarLineaJson(String json){
        CarModel carModel1;
        car_linea_lists.clear();
        try {
            JSONObject obj=new JSONObject(json);
            String status=obj.getString("status");
            String id="";
            String name="";
            String logo="";
            String fullname="";
            if(status.equals("ok")){
                JSONObject dataObj=obj.getJSONObject("data");
                JSONArray brandArr=dataObj.getJSONArray("brandCarList");
                for(int i=0;i<brandArr.length();i++){
                    JSONArray carListArr=brandArr.getJSONObject(i).getJSONArray("carlist");
                    fullname=brandArr.getJSONObject(i).getString("name");
                    for(int j=0;j<carListArr.length();j++){
                        carModel1=new CarModel();
                        JSONObject clObj=carListArr.getJSONObject(j);

                        id=clObj.getString("id");
                        name=clObj.getString("fullname");
                        logo=clObj.getString("logo");


                        carModel1.setFullname(fullname);
                        carModel1.setId(id);
                        carModel1.setName(name);
                        carModel1.setLogo(logo);
                        car_linea_lists.add(carModel1);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(car_linea_lists, comparator2);
    }


    //选择汽车子品牌
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(CarModelActivity.this,"选择的车系是："+car_linea_lists.get(i).getFullname()+" . "+car_linea_lists.get(i).getName(),Toast.LENGTH_SHORT).show();
            Intent intent=new Intent();
            intent.putExtra("carbrand",car_linea_lists.get(i).getFullname()+"");
            intent.putExtra("carname",car_linea_lists.get(i).getName()+"");
            setResult(RESULT_OK,intent);
            finish();
        }
    }


}
