package com.example.youhe.youhecheguanjiaplus.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.IllegalListAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.Violation;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.AddDravingLisenceTipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.CommitsTipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.OrderDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.TipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.TypesOrderTipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.WaitPayTipsDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommitDetailActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalQueryActivty;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20 0020.
 */
@SuppressLint("ValidFragment")
public class QuotedFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private View view;

    private List<Violation> violationList=new ArrayList<Violation>();

    private LinearLayout no_illegal_layout;
    private RelativeLayout commit_layout;
    private MyListView quoted_lv;//待报价违章列表
    private Button commitOrder_btn;//提交按钮
    private TextView payment_instructions_tv;//服务须知
    private TextView count_illegal_choose_tv;//已选条数

    public int totalprice = 0;//总金额
    public int totaldegree=0;//总扣分
    public  int totalservicefee=0;//总服务费
    public int totalNormalDegree=0;//普通违章的总扣分

    private IllegalListAdapter illegalListAdapter;
    List<Integer> listItemQuotedID = new ArrayList<Integer>();//待报价
    private int searchtype;
    private String carnumber;//车牌号码
    private String carid="";

    private SelectBrocastReceiver selectBrocastReceiver;
    private IntentFilter selectIntentFilter;
    public final static String SELECTED_ACTION2="com.youhecheguanjia.quotedillegal_select";
    private final int TYPE=2;

    public QuotedFragment(List<Violation> violationList,int searchtype,String carnumber,String carid){
        this.violationList=violationList;
        this.searchtype=searchtype;
        this.carnumber=carnumber;
        this.carid=carid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_quoted,null);
        initView();//初始化控件

        selectIntentFilter=new IntentFilter(SELECTED_ACTION2);
        selectBrocastReceiver= new SelectBrocastReceiver();
        getActivity().registerReceiver(selectBrocastReceiver,selectIntentFilter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(selectBrocastReceiver!=null){
            getActivity().unregisterReceiver(selectBrocastReceiver);
        }
    }

    //初始化控件
    private void initView(){

        quoted_lv= (MyListView) view.findViewById(R.id.illegal_lv);
        quoted_lv.setOnItemClickListener(this);

        commitOrder_btn= (Button) view.findViewById(R.id.commitOrder_btn);
        commitOrder_btn.setOnClickListener(this);

        payment_instructions_tv= (TextView) view.findViewById(R.id.payment_instructions_tv);
        payment_instructions_tv.setOnClickListener(this);

        count_illegal_choose_tv= (TextView) view.findViewById(R.id.count_illegal_choose_tv);

        if (violationList!= null) {
            illegalListAdapter = new IllegalListAdapter(getActivity(), violationList,searchtype,TYPE);
            illegalListAdapter.notifyDataSetChanged();
            quoted_lv.setAdapter(illegalListAdapter);

            listItemQuotedID.clear();
            for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
                if (illegalListAdapter.mChecked.get(i)) {
                    if ((violationList.get(i).getIscommit()==1)&&(!(violationList.get(i).getDegree()==0
                            && violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==2))) {
                        if (violationList.get(i).getQuotedprice() == 2) {
                            listItemQuotedID.add(i);
                        }
                    }
                }
            }
        }

        totaldegree=0;
        totalNormalDegree=0;
        for (int i = 0; i < listItemQuotedID.size(); i++) {
            if ((violationList.get(listItemQuotedID.get(i)).getIscommit()!=1)||
                    (!(violationList.get(i).getDegree()==0&& violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==2))) {
                totaldegree += violationList.get(listItemQuotedID.get(i)).getDegree();
                totalNormalDegree +=violationList.get(listItemQuotedID.get(i)).getDegree();
            }
        }

        if ((listItemQuotedID.size() == 0)) {
            commitOrder_btn.setClickable(false);
            commitOrder_btn.setBackgroundResource(R.drawable.tijiaoa2);
        } else {
            commitOrder_btn.setClickable(true);
            commitOrder_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tijiao_bg));
        }

        count_illegal_choose_tv.setText(listItemQuotedID.size()+"");//已选多少条违章

        no_illegal_layout= (LinearLayout) view.findViewById(R.id.no_illegal_layout);
        commit_layout= (RelativeLayout) view.findViewById(R.id.commit_layout);
        if(violationList.isEmpty()){
            no_illegal_layout.setVisibility(View.VISIBLE);
            commit_layout.setVisibility(View.INVISIBLE);
        }
    }


    String peccancyidsquoted="";//待报价违章id拼成
//    List<Integer> listItemIDQuoted=new ArrayList<Integer>();//待报价
    List<Violation> order_violations=new ArrayList<Violation>();//提交的订单列表
    AddDravingLisenceTipsDialog addDravingLisenceTipsDialog;//补充资料弹出框
    public void getPeccancyids() {
        listItemQuotedID.clear();
        for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
            if (illegalListAdapter.mChecked.get(i)) {
                if (violationList.get(i).getIscommit() == 1 &&
                        (!(violationList.get(i).getDegree() == 0 &&violationList.get(i).getPrice() == 0 && violationList.get(i).getQuotedprice() == 2))) {
                    if (violationList.get(i).getQuotedprice() == 2) {
                        listItemQuotedID.add(i);
                    }
                }
            }
        }

        order_violations.clear();
        peccancyidsquoted="";
        for(int i=0;i<listItemQuotedID.size();i++){
            order_violations.add(violationList.get(listItemQuotedID.get(i)));
            if (i == listItemQuotedID.size() - 1) {
                peccancyidsquoted += violationList.get(listItemQuotedID.get(i)).getId();
            } else {
                peccancyidsquoted += violationList.get(listItemQuotedID.get(i)).getId() + ",";
            }
        }
    }

    //提交订单
    public void commitOrder(){
        if (listItemQuotedID.size()==0) {
//                    Toast.makeText(IllegalQueryActivty.this,"未选择任何违章或违章已全部提交，用户可以到主界面的\"订单查询\"页面中查看",Toast.LENGTH_LONG).show();
            CommitsTipsDialog commitTipsDialog = new CommitsTipsDialog(getActivity());
            commitTipsDialog.show();
        } else {
            if (addDravingLisenceTipsDialog != null) {
                addDravingLisenceTipsDialog.dismiss();
            }
            addDravingLisenceTipsDialog = new AddDravingLisenceTipsDialog(getActivity(), R.style.Dialog, carid);
            if (searchtype == 1 && ((IllegalQueryActivty)getActivity()).getIsNeedLisence() == 1) {
                addDravingLisenceTipsDialog.show();
            } else {
                if (searchtype == 1) {//代扣分订单提交
                    if (listItemQuotedID.size() != 0) {
                        commitType3Order();
                    }
                }
            }
        }
    }

    /**
     * 待报价订单提交
     * */
    public void commitType3Order(){
        getPeccancyids();//获取选中的违章Id
        if(!listItemQuotedID.isEmpty()) {
            commitQuotedOrder(commitOrderParams(peccancyidsquoted), URLs.COMMIT_QUOTED_PRICE_ORDER);
        }
    }


    /**
     * 提交订单
     * */
    WaitPayTipsDialog waitPayTipsDialog;
    TypesOrderTipsDialog typesOrderTipsDialog;
    public String ordercodequoted="";//待报价订单编号
    public void commitQuotedOrder(HashMap<String, Object> map, final String url) {
        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(url, EncryptUtil.encrypt(map), new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","提交订单获取到的信息："+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),getActivity()));
                    JSONObject dataObj=obj.getJSONObject("data");
                    String status=obj.getString("status");
                    int code=obj.getInt("code");
                    if(status.equals("fail")){
                        if(dataObj.has("code")){
                            code = dataObj.getInt("code");
                        }
                    }

                   if(url.equals(URLs.COMMIT_QUOTED_PRICE_ORDER)){//待报价订单
                        if(code==0){
                            Toast.makeText(getActivity(),"提交订单成功",Toast.LENGTH_SHORT).show();
                        }else{
//                            Toast.makeText(IllegalQueryActivty.this,"请求发生异常，提交订单失败",Toast.LENGTH_SHORT).show();
                            UIHelper.showErrTips(code,getActivity());//提示错误信息
                        }
                        ordercodequoted=dataObj.getString("ordercode");
                        Log.i("TAG","待报价订单编号："+ordercodequoted);
                    }

                    if(status.equals("ok")){
                        MainFragment.positionList.clear();

                        if(url.equals(URLs.COMMIT_QUOTED_PRICE_ORDER)){//待报价订单
                            if(orderDialog!=null){
                                orderDialog.dismiss();
                            }
                            showOrderDialog();
                        }
                        if(totalprice==0){
                            if(waitPayTipsDialog!=null) {
                                waitPayTipsDialog.dismiss();
                            }
                            waitPayTipsDialog=new WaitPayTipsDialog(getActivity(), R.style.Dialog);
                            waitPayTipsDialog.setCanceledOnTouchOutside(false);
                            Window dialogWindow = waitPayTipsDialog.getWindow();
                            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                            dialogWindow.clearFlags(WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
                            dialogWindow.setGravity(Gravity.CENTER);
                            dialogWindow.setAttributes(lp);
                            waitPayTipsDialog.show();
                        }
                    }else if(status.equals("fail")){
                        String errormsg=dataObj.getString("errormsg");
                        Toast.makeText(getActivity(),"订单提交失败，"+errormsg,Toast.LENGTH_LONG).show();
                        UIHelper.dismissPd();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UIHelper.dismissPd();
                    Toast.makeText(getActivity(),"请求发生异常，提交订单失败",Toast.LENGTH_SHORT).show();
                }finally {
                    IllegalQueryActivty illegalQueryActivty= (IllegalQueryActivty) getActivity();
                    illegalQueryActivty.getData(illegalQueryActivty.queryUrl);
                    MainFragment.qurery(MainFragment.queryGetParams(carid),carnumber);//提交违章后刷新首页车辆的违章显示
                }
            }
            @Override
            public void ResponError(VolleyError volleyError) {
                UIHelper.dismissPd();
                Toast.makeText(getActivity(), "网络请求错误，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "提交订单请求错误信息为：" + volleyError.toString());
            }
        });
    }


    OrderDialog orderDialog;
    public void showOrderDialog(){
        orderDialog= new OrderDialog(getActivity(), R.style.Dialog, order_violations, totalprice,totaldegree,totalservicefee, totalNormalDegree,ordercodequoted,carid,searchtype);
        orderDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                //默认返回 false
                return i == KeyEvent.KEYCODE_SEARCH;
            }
        });
        orderDialog.showDialog();
    }

    /*
    * 非本人本车提交订单请求参数
    * */
    HashMap map=new HashMap();
    public HashMap<String,Object> commitOrderParams(String peccancyids){
        map=new HashMap<String,Object>();
        String token = TokenSQLUtils.check();
        map.put("token",token);
        map.put("carid",carid);
        map.put("peccancyids",peccancyids);//违章ID拼成
        map.put("totalprice",totalprice+"");//支付总金额
        Log.i("TAG",peccancyids);
        return map;
    }

    class SelectBrocastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int quotedpriceType=intent.getIntExtra("quotedprice",0);
            int isSelect=intent.getIntExtra("isSelect",-1);

            listItemQuotedID.clear();
            for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
                if (illegalListAdapter.mChecked.get(i)) {
                    if (violationList.get(i).iscommit==1&&
                            (!(violationList.get(i).getDegree()==0 && violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==2)))
                        if(violationList.get(i).getQuotedprice()==2){
                            listItemQuotedID.add(i);
                        }
                }
            }

            if((listItemQuotedID.size()==0)){
//                commitOrder_btn.setClickable(false);
                commitOrder_btn.setBackgroundResource(R.drawable.tijiaoa2);
            }else{
                commitOrder_btn.setClickable(true);
                commitOrder_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tijiao_bg));
            }

            count_illegal_choose_tv.setText(listItemQuotedID.size()+"");//已选多少条违章
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.payment_instructions_tv:
                TipsDialog tipsDialog1=new TipsDialog(getActivity(), R.style.Dialog);
                tipsDialog1.show();
                break;

            case R.id.commitOrder_btn://提交按钮
                commitOrder();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(getActivity(),CommitDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("violation",violationList.get(i));
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_from_left);
    }
}
