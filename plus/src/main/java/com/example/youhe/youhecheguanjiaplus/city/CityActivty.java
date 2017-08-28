package com.example.youhe.youhecheguanjiaplus.city;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.CarCity;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ClearEditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/24 0024.
 * C:\Users\Administrator\Downloads\MyApplication1
 */
public class CityActivty extends Activity implements View.OnClickListener{
    private BaseAdapter adapter;
    private ListView personList;
    private TextView overlay; // 对话框首字母textview
    private IndexListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框
    private ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> city_lists;// 城市列表
    ListAdapter.TopViewHolder topViewHolder;
    CarCity carCities;

    private TextView cancel_citychoose_tv;

    private ClearEditText mClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private String lngCityName = "正在定位所在位置..";
    private int RESULT_CODE =02;//返回码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CityActivty.this);
        }
        SystemBarUtil.useSystemBarTint(CityActivty.this);

        pinyinComparator = new PinyinComparator();
        personList = (ListView) findViewById(R.id.city_lv);
        allCity_lists = new ArrayList<City>();
        letterListView = (IndexListView) findViewById(R.id.MyLetterListView01);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
//        CityActivty.setLocateIn(new GetCityName());
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        personList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(arg2>=1){
//                    Toast.makeText(CityActivty.this,((TextView)arg1.findViewById(R.id.name)).getText().toString(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("city", ((TextView) arg1.findViewById(R.id.name)).getText().toString());
                    setResult(RESULT_CODE, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                }else {
//                    Toast.makeText(CityActivty.this,((TextView)arg1.findViewById(R.id.lng_city)).getText().toString(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("city", ((TextView) arg1.findViewById(R.id.lng_city)).getText().toString());
                    setResult(RESULT_CODE, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                }
            }
        });
        personList.setAdapter(adapter);
        initOverlay();
        hotCityInit();
        setAdapter(allCity_lists);

        mClearEditText= (ClearEditText)findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索q
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        cancel_citychoose_tv= (TextView) findViewById(R.id.cancel_citychoose_tv);
        cancel_citychoose_tv.setOnClickListener(this);
    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city = new City("", "-");
        allCity_lists.add(city);
        city = new City("上海", "");
        allCity_lists.add(city);
        city = new City("北京", "");
        allCity_lists.add(city);
        city = new City("广州", "");
        allCity_lists.add(city);
        city = new City("深圳", "");
        allCity_lists.add(city);
        city = new City("武汉", "");
        allCity_lists.add(city);
        city = new City("南京", "");
        allCity_lists.add(city);
        city = new City("杭州", "");
        allCity_lists.add(city);
        city = new City("成都", "");
        allCity_lists.add(city);
        city = new City("重庆", "");
        allCity_lists.add(city);
        city_lists = getCityList();
        allCity_lists.addAll(city_lists);
//        readFromRaw();
    }

    /**
     * 从raw中读取txt
     */
//    private void readFromRaw() {
//        try {
//            InputStream is = getResources().openRawResource(R.raw.car_city);
//            String text = readTextFromSDcard(is);
//            Log.i("TAG",text+"");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("TAG","读取城市文件异常！"+e.getMessage());
//        }
//    }

    /**
     * 按行读取txt
     * @param is
     * @return
     * @throws Exception
     */
    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    private ArrayList<City> getCityList() {
        DBHelper dbHelper = new DBHelper(this);
        ArrayList<City> list = new ArrayList<City>();
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from city", null);
            City city;
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(1), cursor.getString(2));
                list.add(city);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(list, comparator);
        return list;
    }

    /**
     * a-z排序
     */
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
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

    private void setAdapter(List<City> list) {
        adapter = new ListAdapter(this, list);
        personList.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancel_citychoose_tv://取消城市选择
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;
        }

    }

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<City> list;
        final int VIEW_TYPE = 2;

        public ListAdapter(Context context, List<City> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
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
        public int getViewTypeCount() {// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int viewType = getItemViewType(position);
            if (viewType == 1) {
                if (convertView == null) {
                    topViewHolder = new TopViewHolder();
                    convertView = inflater.inflate(R.layout.frist_list_item,
                            null);
                    topViewHolder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    topViewHolder.name = (TextView) convertView
                            .findViewById(R.id.lng_city);
                    convertView.setTag(topViewHolder);
                } else {
                    topViewHolder = (TopViewHolder) convertView.getTag();
                }
                topViewHolder.name.setText(((AppContext)getApplication()).getLocalCity().toString()+"");
                topViewHolder.alpha.setVisibility(View.VISIBLE);
                topViewHolder.alpha.setText("当前城市");
            }
//            else if (viewType == 2) {
//                final ShViewHolder shViewHolder;
//                if (convertView == null) {
//                    shViewHolder = new ShViewHolder();
//                    convertView = inflater.inflate(R.layout.search_item, null);
//                    shViewHolder.editText= (ClearEditText) convertView.findViewById(R.id.filter_edit);
//                    convertView.setTag(shViewHolder);
//                } else {
//                    shViewHolder = (ShViewHolder) convertView.getTag();
//
//                    //根据输入框输入值的改变来过滤搜索
//                    shViewHolder.editText.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                            //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
//                            filterData(s.toString());
//                        }
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count,
//                                                      int after) {
//                        }
//                        @Override
//                        public void afterTextChanged(Editable s) {
//                        }
//                    });
//                }
//            }
            else{
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position >= 1) {
                    holder.name.setText(list.get(position).getName());
                    String currentStr = getAlpha(list.get(position).getPinyi());
                    String previewStr = (position - 1) >= 0 ? getAlpha(list
                            .get(position - 1).getPinyi()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        if (currentStr.equals("#")) {
                            currentStr = "热门城市";
                        }
                        holder.alpha.setText(currentStr);
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }

        private class TopViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
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
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
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
                personList.setSelection(position);
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

        if (str.equals("-")) {
            return "&";
        }
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

//    private class GetCityName implements MainActivity.LocateIn {
//        @Override
//        public void getCityName(String name) {
//            System.out.println(name);
//            if (topViewHolder.name != null) {
//                lngCityName = name;
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        ArrayList<City> filterDateList = new ArrayList<City>();

        if(TextUtils.isEmpty(filterStr)){
//            filterDateList = allCity_lists;
            setAdapter(allCity_lists);
        }else{
            filterDateList.clear();
            for(City sortModel : allCity_lists){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
            // 根据a-z进行排序
            Collections.sort(filterDateList, pinyinComparator);
            updateListView(filterDateList);
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(ArrayList<City> list){
        this.city_lists = list;
        setAdapter(list);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }
}
