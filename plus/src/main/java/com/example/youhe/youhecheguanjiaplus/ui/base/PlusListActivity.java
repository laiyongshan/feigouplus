package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.PlusListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.PlusBean;
import com.example.youhe.youhecheguanjiaplus.databinding.ActivityPlusListBinding;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheActivity;
import com.example.youhe.youhecheguanjiaplus.mainfragment.PlusFragment;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.RefreshListView;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 车主卡列表
 * Created by Administrator on 2017/7/10 0010.
 */

public class PlusListActivity extends YeoheActivity {

    private ActivityPlusListBinding b;
    private PlusListAdapter adapter = null;
    private ArrayList<PlusBean> plusList = null;
    private String page = "1";
    private String page_size = "20";
    private String status = "";
    private String card_number = "";
    private String price = "";

    private VolleyUtil volleyUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = DataBindingUtil.setContentView(this, R.layout.activity_plus_list);

// 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, PlusListActivity.this);
        }
        SystemBarUtil.useSystemBarTint(PlusListActivity.this);

        volleyUtil = VolleyUtil.getVolleyUtil(this);
        page = PlusFragment.DEFAULT_PAGE + "";
        page_size = PlusFragment.DEFAULT_PAGE_SIZE + "";
        initData();

        event();
    }

    private void initData() {
        if (getIntent().hasExtra(PlusFragment.EXTRA_MORE)) {
            try {
                plusList = new Gson().fromJson(getIntent().getStringExtra(PlusFragment.EXTRA_MORE), new TypeToken<List<PlusBean>>() {
                }.getType());
                if (getIntent().hasExtra(PlusFragment.NET_PAGE))
                    page = getIntent().getStringExtra(PlusFragment.NET_PAGE);
                if (getIntent().hasExtra(PlusFragment.NET_PAGE_SIZE))
                    page_size = getIntent().getStringExtra(PlusFragment.NET_PAGE_SIZE);
                if (getIntent().hasExtra(PlusFragment.NET_STATUS))
                    status = getIntent().getStringExtra(PlusFragment.NET_STATUS);
                if (getIntent().hasExtra(PlusFragment.NET_CARD_NUMBER))
                    card_number = getIntent().getStringExtra(PlusFragment.NET_CARD_NUMBER);
                if (getIntent().hasExtra(PlusFragment.NET_PRICE))
                    price = getIntent().getStringExtra(PlusFragment.NET_PRICE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (plusList == null||plusList.size()<=0) {
            ToastUtil.getShortToastByString(this, "没有更多内容");
            finish();
        }

        adapter = new PlusListAdapter(this);
        adapter.setData(plusList);
        b.list.setAdapter(adapter);
//        b.textCount.setText("共"+plusList.size()+"条");

        b.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PlusListActivity.this, CommentWebActivity.class);
                intent.putExtra(CommentWebActivity.EXTRA_TITLE, "");
                intent.putExtra(CommentWebActivity.EXTRA_URL, URLs.PAY_RECORD+ TokenSQLUtils.check());
                startActivity(intent);
            }
        });
    }

    private boolean isUp = true;

    private void event() {

        b.myassetsBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        b.list.setRefreshListener(new RefreshListView.RefreshListener() {
            @Override
            public void onRefreshing(RefreshListView.Mode mode) {
                if (mode == RefreshListView.Mode.UP) {//向上刷新
                    page = "1";
                    isUp = true;
                    loadData();
                } else if (mode == RefreshListView.Mode.BOTTOM) {//向下
                    page = (Integer.parseInt(page) + 1) + "";
                    isUp = false;
                    loadData();
                }
            }
        });
    }

    private void loadData() {
        HashMap<String, String> hashMap = new HashMap<>();
        String token = TokenSQLUtils.check();
        hashMap.put(PlusFragment.NET_TOKEN, token);
        hashMap.put(PlusFragment.NET_PAGE, page);
        hashMap.put(PlusFragment.NET_PAGE_SIZE, page_size);
        hashMap.put(PlusFragment.NET_STATUS, status);
        hashMap.put(PlusFragment.NET_CARD_NUMBER, card_number);
        hashMap.put(PlusFragment.NET_PRICE, price);

        volleyUtil.postRequest(this, URLs.PLUS_LIST, hashMap, "获取车主卡信息失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {

                    String resultData = dataObject.getString("card_number_list");

                    ArrayList<PlusBean> list = new Gson().fromJson(resultData, new TypeToken<List<PlusBean>>() {
                    }.getType());

                    if (list != null && list.size() > 0) {
                        if (isUp)
                            plusList.clear();
                        plusList.addAll(list);
                        adapter.setData(plusList);
                        b.list.setAdapter(adapter);
//                        b.textCount.setText("共"+plusList.size()+"条");
                    } else {
                        if (!isUp) {
                            Toast.makeText(PlusListActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isUp)
                        Toast.makeText(PlusListActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(PlusListActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
                b.list.setRefreshing(false);
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                if (!StringUtils.isEmpty(msg))
                    Toast.makeText(PlusListActivity.this, msg, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PlusListActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                b.list.setRefreshing(false);
            }
        });
    }

    @Override
    public void init() {
    }

    @Override
    public void refresh(Object... param) {

    }
}
