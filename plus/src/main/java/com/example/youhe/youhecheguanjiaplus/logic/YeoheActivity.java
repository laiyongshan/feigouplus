package com.example.youhe.youhecheguanjiaplus.logic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public abstract class YeoheActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MainService.addActivity(this);

        switch(0)
        {
            case 1:
                JSONObject jsoObj;
                String date=null;
                String second=null;
                try{
                    jsoObj=new JSONObject();
                    date=jsoObj.getString("date");
                    second=jsoObj.getString("second");
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

               test test1=new test();
                test1.settime(date,second);
                break;
        }
    }

    class test
    {
        public  void settime(String a,String b){}
    }

    public abstract void init();

    public abstract void refresh(Object... param);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainService.removeActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
