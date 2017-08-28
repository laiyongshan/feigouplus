package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.IllegalCodeAdapter;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.IllegalCode;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.HttpUtil;
import com.example.youhe.youhecheguanjiaplus.utils.NetUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class IllegalCodeActivity extends Activity implements View.OnClickListener,AbsListView.OnScrollListener {
    private TextView tvTag0,tvTag1,tvTag2,tvTag3,tvTag6,tvTag12;
    private ListView illegalcode_lv;
    private IllegalCodeAdapter illegalCodeAdapter;
    private List<IllegalCode> codeList;

    private int visibleLastIndex=0; // 最后的可视项索引
    private int visibleItemCount; // 当前窗口可见项总数
    private View loadMoreView;
    private Handler handler = new Handler();

    public int page=1;
    public int scop=0;

    List<TextView> tvList;
    private int offset;
    private int cursorWidth;
    private int originalIndex = 0;
    private ImageView cursor = null;
    private Animation animation = null;

    private ImageView back_iv;

    private LinearLayout network_request_error_layout,illegalcode_layout;
    private TextView reload_tv;//重新加载

    NetReceiver mNetReceiver;
    IntentFilter mNetFilter;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illegalcode);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,IllegalCodeActivity.this);
        }
        SystemBarUtil.useSystemBarTint(IllegalCodeActivity.this);

        appContext= (AppContext)this.getApplicationContext();

        loadMoreView = getLayoutInflater().inflate(R.layout.footer, null);
        codeList=new ArrayList<IllegalCode>();
        illegalCodeAdapter=new IllegalCodeAdapter(IllegalCodeActivity.this,codeList);
        initView();
        initCursor();

        mNetReceiver= new NetReceiver();//网络连接情况广播接收者
        mNetFilter= new IntentFilter();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetReceiver);
    }

    private void getData(){
        if(appContext.isNetworkConnected()){
            illegalcode_layout.setVisibility(View.VISIBLE);
            network_request_error_layout.setVisibility(View.INVISIBLE);
            initData("0");
            tvTag0.setTextColor(Color.RED);
            tvChooose(tvTag0);
            cursorChange(0);
        }else{
            illegalcode_layout.setVisibility(View.INVISIBLE);
            network_request_error_layout.setVisibility(View.VISIBLE);
            Toast.makeText(this,"网络连接失败，请检查网络连接设置",Toast.LENGTH_LONG).show();
        }
    }

    private void initData(String scop){
        VolleyUtil.getVolleyUtil(IllegalCodeActivity.this).StringRequestPostVolley(URLs.GET_CODE_LIST, EncryptUtil.encrypt(getParams(page+"", scop)), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                key="0"+"1";

                try {
                    JSONObject object=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),IllegalCodeActivity.this));
                    String status=object.getString("status");
                    int code=object.getInt("code");

                    UIHelper.showErrTips(code,IllegalCodeActivity.this);

                    if(status.equals("ok")){
                        codeList.clear();
                        illegalCodeAdapter=new IllegalCodeAdapter(IllegalCodeActivity.this,codeList);
                        codeList=json2Codelist(object);
                        illegalCodeAdapter.notifyDataSetChanged();
                        illegalcode_lv.setAdapter(illegalCodeAdapter);
                        HttpUtil.saveJson2FileCache(key, jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                illegalcode_lv.removeFooterView(loadMoreView);
            }
        });
    }

    public List<IllegalCode> json2Codelist(JSONObject json) throws JSONException {
        JSONObject dataObj=json.getJSONObject("data");
        JSONArray codeListArr=dataObj.getJSONArray("codeList");
        for(int i=0;i<codeListArr.length();i++){
            IllegalCode illegalCode=new IllegalCode();
            illegalCode.setIllegalCode(codeListArr.getJSONObject(i).getString("code"));
            illegalCode.setFakuan(codeListArr.getJSONObject(i).getString("count"));
            illegalCode.setScop(codeListArr.getJSONObject(i).getString("score"));
            illegalCode.setIllegalDetail(codeListArr.getJSONObject(i).getString("peccancyinfo"));
            illegalCode.setPunishment(codeListArr.getJSONObject(i).getString("punishment"));
//            codeList.add(illegalCode);
            illegalCodeAdapter.addNewsItem(illegalCode);
        }
        return codeList;
    }


    HashMap map=new HashMap();
    public HashMap getParams(String page,String score){
        String token = TokenSQLUtils.check();
        map.put("token",token);
        map.put("page",page);
        map.put("score",score);
        return map;
    }

    private void initView(){
        tvList=new ArrayList<TextView>();
        tvTag0= (TextView) findViewById(R.id.tvTag0);//0分
        tvTag0.setTextColor(Color.RED);
        tvTag0.setOnClickListener(this);
        tvList.add(tvTag0);
        tvTag1= (TextView) findViewById(R.id.tvTag1);//1分
        tvTag1.setOnClickListener(this);
        tvList.add(tvTag1);
        tvTag2= (TextView) findViewById(R.id.tvTag2);//2分
        tvTag2.setOnClickListener(this);
        tvList.add(tvTag2);
        tvTag3= (TextView) findViewById(R.id.tvTag3);//3分
        tvTag3.setOnClickListener(this);
        tvList.add(tvTag3);
        tvTag6= (TextView) findViewById(R.id.tvTag6);//6分
        tvTag6.setOnClickListener(this);
        tvList.add(tvTag6);
        tvTag12= (TextView) findViewById(R.id.tvTag12);//12分
        tvTag12.setOnClickListener(this);
        tvList.add(tvTag12);

        illegalcode_lv= (ListView) findViewById(R.id.illegalcode_lv);
        illegalcode_lv.setAdapter(illegalCodeAdapter);
        illegalcode_lv.setOnScrollListener(this);
        illegalcode_lv.addFooterView(loadMoreView);

        back_iv= (ImageView) findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this);

        network_request_error_layout= (LinearLayout) findViewById(R.id.network_request_error_layout);
        reload_tv= (TextView) findViewById(R.id.reload_tv);
        reload_tv.setOnClickListener(this);
        illegalcode_layout= (LinearLayout) findViewById(R.id.illegalcode_layout);

        if(!appContext.isNetworkConnected()){
            network_request_error_layout.setVisibility(View.VISIBLE);
            illegalcode_layout.setVisibility(View.INVISIBLE);
        }
    }

    public void initCursor() {
        cursorWidth = BitmapFactory.decodeResource(getResources(),
                R.drawable.cursor).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        offset = ((dm.widthPixels / 6) - cursorWidth) / 2;
        cursor = (ImageView) findViewById(R.id.ivCursor);
        Matrix matrix = new Matrix();
        matrix.setTranslate(offset, 0);
        cursor.setImageMatrix(matrix);
    }

    public void cursorChange(int arg0){
        int one = 2 * offset + cursorWidth;
        int two = one * 2;
        int three=one*3;
        int four=one*4;
        int five=one*5;

        animation = new TranslateAnimation(originalIndex*one, 0, 0, 0);

        switch (originalIndex) {
            case 0:
                if (arg0 == 1) {
                    animation = new TranslateAnimation(0, one, 0, 0);
                }
                if (arg0 == 2) {
                    animation = new TranslateAnimation(0, two, 0, 0);
                }
                if (arg0 == 3) {
                    animation = new TranslateAnimation(0, three, 0, 0);
                }
                if(arg0==4){
                    animation = new TranslateAnimation(0, four, 0, 0);
                }
                if(arg0==5){
                    animation = new TranslateAnimation(0, five, 0, 0);
                }
                break;

            case 1:
                if (arg0 == 0) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                }
                if (arg0 == 2) {
                animation = new TranslateAnimation(one, two, 0, 0);
            }
                if (arg0 == 3) {
                    animation = new TranslateAnimation(one, three, 0, 0);
                }
                if (arg0 == 4) {
                    animation = new TranslateAnimation(one, four, 0, 0);
                }
                if (arg0 == 5) {
                    animation = new TranslateAnimation(one, five, 0, 0);
                }
                break;

            case 2:
                if (arg0 == 1) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                }
                if (arg0 == 0) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                }
                if (arg0 == 3) {
                    animation = new TranslateAnimation(two,three, 0, 0);
                }
                if (arg0 == 4) {
                    animation = new TranslateAnimation(two,four, 0, 0);
                }
                if (arg0 == 5) {
                    animation = new TranslateAnimation(two,five, 0, 0);
                }
                break;

            case 3:
                if (arg0 == 2) {
                    animation = new TranslateAnimation(three, two, 0, 0);
                }
                if (arg0 == 1) {
                    animation = new TranslateAnimation(three, one, 0, 0);
                }
                if (arg0 == 0) {
                    animation = new TranslateAnimation(three, 0, 0, 0);
                }
                if(arg0==4){
                    animation = new TranslateAnimation(three, four, 0, 0);
                }
                if(arg0==5){
                    animation = new TranslateAnimation(three, five, 0, 0);
                }
                break;

            case 4:
                if (arg0 == 2) {
                    animation = new TranslateAnimation(four, two, 0, 0);
                }
                if (arg0 == 1) {
                    animation = new TranslateAnimation(four, one, 0, 0);
                }
                if (arg0 == 0) {
                    animation = new TranslateAnimation(four, 0, 0, 0);
                }
                if(arg0==3){
                    animation = new TranslateAnimation(four, three, 0, 0);
                }
                if(arg0==5){
                    animation = new TranslateAnimation(four, five, 0, 0);
                }
                break;

            case 5:
                if (arg0 == 2) {
                    animation = new TranslateAnimation(five, two, 0, 0);
                }
                if (arg0 == 1) {
                    animation = new TranslateAnimation(five, one, 0, 0);
                }
                if (arg0 == 0) {
                    animation = new TranslateAnimation(five, 0, 0, 0);
                }
                if(arg0==4){
                    animation = new TranslateAnimation(five, four, 0, 0);
                }
                if(arg0==3){
                    animation = new TranslateAnimation(five, three, 0, 0);
                }
                break;
        }
        animation.setFillAfter(true);
        animation.setDuration(300);
        cursor.startAnimation(animation);
        originalIndex = arg0;
    }

    public void tvChooose(TextView tv){
        tv.setTextColor(Color.RED);
        for(int i=0;i<tvList.size();i++){
            if((tv.getId())!=(tvList.get(i).getId())){
                tvList.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvTag0:
                page=1;
                scop=0;
                tvChooose(tvTag0);
                cursorChange(0);
                initData("0");
                break;
            case R.id.tvTag1:
                page=1;
                scop=1;
                tvChooose(tvTag1);
                cursorChange(1);
                initData("1");
                break;
            case R.id.tvTag2:
                page=1;
                scop=2;
                tvChooose(tvTag2);
                cursorChange(2);
                initData("2");
                break;
            case R.id.tvTag3:
                scop=3;
                page=1;
                tvChooose(tvTag3);
                cursorChange(3);
                initData("3");
                break;
            case R.id.tvTag6:
                page=1;
                scop=6;
                tvChooose(tvTag6);
                cursorChange(4);
                initData("6");
                break;
            case R.id.tvTag12:
                page=1;
                scop=12;
                tvChooose(tvTag12);
                cursorChange(5);
                initData("12");
                break;
            case R.id.back_iv:
                finish();
                overridePendingTransition(R.anim.bottom_int,R.anim.bottom_out);
                break;
            case R.id.reload_tv:
                getData();
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        int itemsLastIndex = illegalCodeAdapter.getCount() - 1; // 数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == lastIndex) {
            // 如果是自动加载,可以在这里放置异步加载数据的代码
            loadMoreData((++page)+"",scop+"");
            illegalCodeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

        // 如果所有的记录选项等于数据集的条数，则移除列表底部视图
//        if (totalItemCount == datasize + 1) {
//            illegalcode_lv.removeFooterView(loadMoreView);
//            Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_LONG).show();
//        }
    }

    /**
     * 加载更多数据
     */
    public String key="";
    private void loadMoreData(final String page, final String scop) {
        VolleyUtil.getVolleyUtil(IllegalCodeActivity.this).StringRequestPostVolley(URLs.GET_CODE_LIST, EncryptUtil.encrypt(getParams(page, scop)), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
//                key=scop+page;
                try {
                    JSONObject object=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),IllegalCodeActivity.this));
                    String status=object.getString("status");
                    if(status.equals("ok")){
                        JSONObject dataObj=object.getJSONObject("data");
                        JSONArray dataArray=dataObj.getJSONArray("codeList");
                        for(int i=0;i<dataArray.length();i++){
                            IllegalCode illegalCode=new IllegalCode();
                            illegalCode.setIllegalCode(dataArray.getJSONObject(i).getString("code"));
                            illegalCode.setFakuan(dataArray.getJSONObject(i).getString("count"));
                            illegalCode.setScop(dataArray.getJSONObject(i).getString("score"));
                            illegalCode.setPunishment(dataArray.getJSONObject(i).getString("punishment"));
                            illegalCode.setIllegalDetail(dataArray.getJSONObject(i).getString("peccancyinfo"));
                            illegalCodeAdapter.addNewsItem(illegalCode);
                        }

                        if(dataArray.length()==0){
                            Toast.makeText(IllegalCodeActivity.this,"数据已全部加载",Toast.LENGTH_LONG).show();
                        }

                        illegalCodeAdapter.notifyDataSetChanged();
                        HttpUtil.saveJson2FileCache(key, jsonObject.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                illegalcode_lv.removeFooterView(loadMoreView);
            }
        });
    }

    /**
     * 检查网络连接监听情况
     */
    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isConnected = NetUtils.isNetworkConnected(context);
                if (isConnected) {
                    getData();//获取数据
                } else {
//                    network_request_error_layout.setVisibility(View.VISIBLE);
//                    illegalcode_layout.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "网络连接已断开，请检查设置！", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
