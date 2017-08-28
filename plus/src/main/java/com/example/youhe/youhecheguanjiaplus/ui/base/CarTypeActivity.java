package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.CarType;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class CarTypeActivity extends Activity {

    private ListView car_type_lv;
    private List<CarType> car_type_list;

    private BaseAdapter adapter;
    private ImageView car_type_back_img;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_type);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,CarTypeActivity.this);
        }
        SystemBarUtil.useSystemBarTint(CarTypeActivity.this);

        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        initView();//初始化控件

        car_type_list=new ArrayList<CarType>();
        adapter=new ListAdapter(car_type_list);
        car_type_lv.setAdapter(adapter);

        getCarType();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView(){
        car_type_back_img= (ImageView) findViewById(R.id.car_type_back_img);
        car_type_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        car_type_lv= (ListView) findViewById(R.id.car_type_lv);
        car_type_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(CarTypeActivity.this,car_type_list.get(i).getTypename(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("cartype",car_type_list.get(i).getTypename());
                intent.putExtra("typecode",car_type_list.get(i).getTypecode());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    private void getCarType(){
        HashMap map=new HashMap();
        String token= TokenSQLUtils.check();
        map.put("token",token);

        VolleyUtil.getVolleyUtil(CarTypeActivity.this).StringRequestPostVolley(URLs.GET_CAR_TYPE_LIST, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG",EncryptUtil.decryptJson(jsonObject.toString(),CarTypeActivity.this));
                paseJson(EncryptUtil.decryptJson(jsonObject.toString(),CarTypeActivity.this));
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                pd.dismiss();
            }
        });
    }

    private void paseJson(String json){
        CarType carType;
        try {
            JSONObject obj=new JSONObject(json);
            JSONObject dataObj=obj.getJSONObject("data");
            JSONArray arr=dataObj.getJSONArray("carTypeList");
            for(int i=0;i<arr.length();i++){
                carType=new CarType();
                carType.setTypecode(arr.getJSONObject(i).getString("typecode"));
                carType.setTypename(arr.getJSONObject(i).getString("typename"));

                car_type_list.add(carType);

                adapter=new ListAdapter(car_type_list);
                car_type_lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            pd.dismiss();
        }
    }



     class ListAdapter extends BaseAdapter{
         private LayoutInflater mInflater;
         private ViewHolder holder;
         private List<CarType> list;

        public ListAdapter(List<CarType> list){
            this.list=list;
            mInflater = LayoutInflater.from(CarTypeActivity.this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                holder = new ViewHolder();
                view=mInflater.inflate(R.layout.item_textview,null);
                holder.car_type_tv= (TextView) view.findViewById(R.id.car_type_tv);

                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }

            holder.car_type_tv.setText(car_type_list.get(i).getTypename());

            return view;
        }

         class ViewHolder{
             TextView  car_type_tv;
         }

    }


}
