package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.TradingRecordAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.TradingModel;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.PopWindowTadingRecordFilter;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.RefreshExpandableView;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 交易记录列表
 * Created by Administrator on 2017/7/10.
 */

public class TradingReco2Activity extends Activity {

    //    private RefreshExpandListView trading_reco_lv;
    private RefreshExpandableView trading_reco_lv;
    private TradingRecordAdapter tradingRecAdapter;
//    private List<TradingModel> tradingModelList;

    private ImageView back_img, imageFilter;
    private RelativeLayout topBar;
    private UIDialog uiDialog = null;
    private VolleyUtil volleyUtil = null;
    private ArrayList<PayWay> payWayList = null;//交易类型
    private ArrayList<String> payWayNameList = new ArrayList<>();
    private ArrayList<TradingModel> recordList = null;

    ArrayList<TradingModel.TradingSubModel> subModels = null;

    private int page = 1;
    private int pageSize = 20;

    private String start="";
    private String end="";
    private String plus_minus="";
    private String pay_action="";
    private String card_number="";

    private boolean isShowLoad=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_reco2);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true, TradingReco2Activity.this);
        }
        SystemBarUtil.useSystemBarTint(TradingReco2Activity.this);

        initViews();//初始化控件

        loadRecord();

        loadType();
    }


    /**
     * 获取交易记录
     */
    private void loadRecord() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", TokenSQLUtils.check());
        hashMap.put("page", page + "");
        hashMap.put("page_size", pageSize + "");
        hashMap.put("createtime1", start + "");
        hashMap.put("createtime2", end + "");
        hashMap.put("plus_minus", plus_minus + "");//交易形式  1：收入；2：支出
        hashMap.put("pay_action", pay_action + "");//交易类型
        hashMap.put("card_number", card_number + "");

        if (isShowLoad) {
            isShowLoad=false;
            uiDialog.show();
        }
        final String a=page<=1?"没有该账户交易记录":"没有更多交易记录";
        volleyUtil.postRequest(this, URLs.PAY_LIST, hashMap, a, new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {
                    if (subModels != null)
                        subModels.clear();
                    if (recordList != null && page <= 1)
                        recordList.clear();
                    subModels = new Gson().fromJson(dataObject.getString("data"),
                            new TypeToken<ArrayList<TradingModel.TradingSubModel>>() {
                            }.getType());
//                    Log.d("TAG", subModels.toString());
                    if (subModels != null && subModels.size() > 0) {
                        dealRecordDate();

                    } else {
                        ToastUtil.getLongToastByString(TradingReco2Activity.this, a);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.getLongToastByString(TradingReco2Activity.this, a);
                }
                setLayoutAdapter();
                trading_reco_lv.setRefreshing(false);
                uiDialog.hide();
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                uiDialog.hide();
                trading_reco_lv.setRefreshing(false);
                ToastUtil.getLongToastByString(TradingReco2Activity.this, a);
            }
        });
    }

    private int temp = 1;

    private void dealRecordDate() {
        if (subModels == null || subModels.size() <= 0)
            return;
        if (recordList == null)
            recordList = new ArrayList<>();
        ArrayList<TradingModel.TradingSubModel> b;
        ArrayList<TradingModel.TradingSubModel> a;

        for (int i = 0; i < subModels.size(); i++) {
            if (recordList.size() == 0) {
                a = new ArrayList<>();
                a.add(subModels.get(i));
                recordList.add(new TradingModel(subModels.get(i).getYearMonth(), a));
            } else {
                for (int j = 0; j < recordList.size(); j++) {
                    if (recordList.get(j).getYearMonth().equals(subModels.get(i).getYearMonth())) {
                        recordList.get(j).getDetailList().add(subModels.get(i));
                        temp = 0;
                    }
                }
                if (temp == 1) {
                    b = new ArrayList<>();
                    b.add(subModels.get(i));
                    recordList.add(new TradingModel(subModels.get(i).getYearMonth(), b));
                } else
                    temp = 1;
            }
        }

    }


//    private HashMap<String, String> getParamRecord(String start, String end,
//                                                   String plus_minus, String pay_action, String card_number) {
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("token", TokenSQLUtils.check());
//        hashMap.put("page", page + "");
//        hashMap.put("page_size", pageSize + "");
//        hashMap.put("createtime1", start + "");
//        hashMap.put("createtime2", end + "");
//        hashMap.put("plus_minus", plus_minus + "");//交易形式  1：收入；2：支出
//        hashMap.put("pay_action", pay_action + "");//交易类型
//        hashMap.put("card_number", card_number + "");
//
//        return hashMap;
//    }


    /**
     * 获取交易类型
     */
    private void loadType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", TokenSQLUtils.check());

        volleyUtil.postRequest(this, URLs.PAY_WAY_LIST, hashMap, "获取交易类型失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {

                    payWayList = new Gson().fromJson(dataObject.getString("list"), new TypeToken<ArrayList<PayWay>>() {
                    }.getType());

                    if (payWayList != null && payWayList.size() > 0) {
                        loadTypeDate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
            }
        });
    }

    /**
     * 装载交易类型数据
     */
    private void loadTypeDate() {
        if (payWayList.size() <= 0)
            return;
        payWayNameList.clear();
        for (int i = 0; i < payWayList.size(); i++) {
            payWayNameList.add(payWayList.get(i).actionname);
        }

    }


    /**
     * 初始化控件
     */
    private void initViews() {
        topBar = (RelativeLayout) findViewById(R.id.topBar);
        payWayList = new ArrayList<>();
        recordList = new ArrayList<>();

        uiDialog = new UIDialog(this, "Loading...");
        volleyUtil = VolleyUtil.getVolleyUtil(this);

        trading_reco_lv = (RefreshExpandableView) findViewById(R.id.trading_reco_lv);

        tradingRecAdapter = new TradingRecordAdapter(this, recordList);
        trading_reco_lv.setAdapter(tradingRecAdapter);
        trading_reco_lv.getListView().setGroupIndicator(null);

        trading_reco_lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        trading_reco_lv.setRefreshListener(new RefreshExpandableView.RefreshListener() {
            @Override
            public void onRefreshing(RefreshExpandableView.Mode mode) {
                if (mode == RefreshExpandableView.Mode.UP) {//向上刷新
                    page = 1;
                    loadRecord();
                } else if (mode == RefreshExpandableView.Mode.BOTTOM) {//向下
                    page++;
                    loadRecord();
                }
            }
        });


        trading_reco_lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                if (recordList != null && recordList.get(groupPosition) != null
                        && recordList.get(groupPosition).getDetailList() != null
                        && recordList.get(groupPosition).getDetailList().get(childPosition) != null) {

                    Intent intent = new Intent(TradingReco2Activity.this, CommentWebActivity.class);
                    intent.putExtra(CommentWebActivity.EXTRA_TITLE, "交易详情");
                    intent.putExtra(CommentWebActivity.EXTRA_URL, recordList.get(groupPosition).getDetailList().get(childPosition).getListurl());

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                } else {
                    ToastUtil.getLongToastByString(TradingReco2Activity.this, "获取详情地址失败");
                }
                return false;
            }
        });


        back_img = (ImageView) findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageFilter = (ImageView) findViewById(R.id.image_filter);
        imageFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
        });

    }

    /**
     * 筛选
     */
    private PopWindowTadingRecordFilter popWindowPlusFilter = null;

    private void filter() {
        if (popWindowPlusFilter == null) {
            popWindowPlusFilter = new PopWindowTadingRecordFilter(this);
            if (payWayNameList.size() == 0)
                payWayNameList.add("全部");
            popWindowPlusFilter.setData(payWayNameList);
        }
        if (popWindowPlusFilter.isShowing())
            popWindowPlusFilter.dismiss();
        else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.7f;
            getWindow().setAttributes(lp);
            popWindowPlusFilter.showAsDropDown(topBar);

            popWindowPlusFilter.setOnClickListenter(new PopWindowTadingRecordFilter.OnItemClickListener() {
                @Override
                public void itemClick(String startDateStr, String endDateStr, int formIndex, int typeIndex, String cardNumber) {
                    String action = "";
                    try {
                        if (payWayNameList.size() > 0 && !payWayNameList.get(0).equals("全部")) {
                            action = payWayList.get(typeIndex).getAction();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG",startDateStr+","+endDateStr+","+(formIndex+1)+","+action+","+cardNumber);
                    start=startDateStr;
                    end=endDateStr;
                    plus_minus=formIndex < 0 ? "" : (formIndex + 1) + "";
                    pay_action=action;
                    card_number=cardNumber;
                    loadRecord();
                }
            });
        }
    }

    /**
     * 设置listVIew的Adapter
     */
    private void setLayoutAdapter() {
//        Log.d("TAG", recordList.toString());

        if (recordList.size() > 0) {
            for (int i = 0; i < recordList.size(); i++) {
                trading_reco_lv.expandGroup(i);
            }
        }
        tradingRecAdapter.notifyDataSetChanged();
    }

    /**
     * 交易类型
     */
    class PayWay implements Serializable {
        private String action;
        private String actionname;

        public PayWay(String action, String actionname) {
            this.action = action;
            this.actionname = actionname;
        }

        public PayWay() {
        }

        @Override
        public String toString() {
            return "PayWay{" +
                    "action='" + action + '\'' +
                    ", actionname='" + actionname + '\'' +
                    '}';
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getActionname() {
            return actionname;
        }

        public void setActionname(String actionname) {
            this.actionname = actionname;
        }
    }


}
