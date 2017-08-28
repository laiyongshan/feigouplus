package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class RegistgerLicenseActivity extends Activity implements AdapterView.OnItemClickListener{

    private TextView title_tv;
    private ImageView activity_list_back_img;
    private ListView listview;

    private BaseAdapter adapter;
//    private List<VehicleType> vehicleTypeList;
    private String[] vehicleTypeList={"A","C","D","E","F","G"};
    private String[] vehicleNameList={"居民身份证","军官证","士兵证","军官离退休证","境外人员身份证明","外交人员身份证明"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initView();//初始化控件

    }


    //初始化控件
    private void initView(){
        title_tv= (TextView) findViewById(R.id.title_tv);
        title_tv.setText("证件类型");
        activity_list_back_img= (ImageView) findViewById(R.id.activity_list_back_img);
        activity_list_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        });
        listview= (ListView) findViewById(R.id.listview);
        listview.setOnItemClickListener(this);

        adapter=new LicenseAdapter(this);
        listview.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent();
        intent.putExtra("vehicleName",vehicleNameList[i]);
        intent.putExtra("vehicleType",vehicleTypeList[i]);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
    }



    class LicenseAdapter extends BaseAdapter{

        private LayoutInflater mInflater;
        private ViewHolder holder;
        private Context context;

        public LicenseAdapter(Context context){
            this.context=context;
            mInflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return vehicleTypeList.length;
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
                holder=new ViewHolder();
                view=mInflater.inflate(R.layout.item_textview,null);

                holder.license_type_tv= (TextView) view.findViewById(R.id.car_type_tv);

                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }

            holder.license_type_tv.setText(vehicleNameList[i]);

            return view;
        }

        class ViewHolder{
            TextView license_type_tv;
        }
    }

}
