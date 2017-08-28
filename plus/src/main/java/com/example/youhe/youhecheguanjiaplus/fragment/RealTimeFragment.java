package com.example.youhe.youhecheguanjiaplus.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.youhe.youhecheguanjiaplus.dialog.AddMajorDialog2;
import com.example.youhe.youhecheguanjiaplus.dialog.Commit12OrderTipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.CommitsTipsDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.KonwDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.OrderDialog;
import com.example.youhe.youhecheguanjiaplus.dialog.TipsDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.VolleyInterface;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommitDetailActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalQueryActivty;
import com.example.youhe.youhecheguanjiaplus.utils.EncryptUtil;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20 0020.
 */
@SuppressLint("ValidFragment")
public class RealTimeFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private View view;
    private MyListView real_time_lv;
    private List<Violation> violationList=new ArrayList<Violation>();
    private String msg="";
    private int searchtype=0;

    private LinearLayout no_illegal_layout;
    private RelativeLayout commit_layout;

    private IllegalListAdapter illegalListAdapter;
    List<Integer> listItemID = new ArrayList<Integer>();//实时报价

    private TextView totalprice_tv,count_illegal_choose_tv;//总金额，选择条数
    private Button commitOrder_btn;//提交实时报价按钮
    private TextView payment_instructions_tv;//服务说明

    public int totalprice = 0;//总金额
    public int totaldegree=0;//总扣分
    public  int totalservicefee=0;//总服务费
    public int totalNormalDegree=0;//普通违章的总扣分

    private SelectBrocastReceiver selectBrocastReceiver;
    private IntentFilter selectIntentFilter;
    private final static String SELECTED_ACTION1="com.youhecheguanjia.illegallistselect";
    private final int TYPE=1;

    private String carnumber;//车牌号码
    private String carid="";

    public RealTimeFragment(){

    }

    public RealTimeFragment(List<Violation> violationList,String msg,int searchtype,String carnumber,String carid){
        this.violationList=violationList;
        this.searchtype=searchtype;
        this.carnumber=carnumber;
        this.carid=carid;
        this.msg=msg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_real_time,null);

        initView();//初始化控件

        selectIntentFilter=new IntentFilter(SELECTED_ACTION1);
        selectBrocastReceiver=new SelectBrocastReceiver();
        getActivity().registerReceiver(selectBrocastReceiver,selectIntentFilter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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

        payment_instructions_tv= (TextView) view.findViewById(R.id.payment_instructions_tv);
        payment_instructions_tv.setOnClickListener(this);

        real_time_lv= (MyListView) view.findViewById(R.id.illegal_lv);
        real_time_lv.setOnItemClickListener(this);

        totalprice_tv= (TextView) view.findViewById(R.id.totalprice_tv1);
        count_illegal_choose_tv= (TextView) view.findViewById(R.id.count_illegal_choose_tv);
        commitOrder_btn= (Button) view.findViewById(R.id.commitOrder_btn);
        commitOrder_btn.setOnClickListener(this);

        if (violationList!= null) {
            illegalListAdapter = new IllegalListAdapter(getActivity(), violationList, searchtype, TYPE);
            illegalListAdapter.notifyDataSetChanged();
            real_time_lv.setAdapter(illegalListAdapter);

            listItemID.clear();
            for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
                if (illegalListAdapter.mChecked.get(i)) {
                    if ((violationList.get(i).getIscommit() == 1) && (!(violationList.get(i).getDegree() == 0
                            && violationList.get(i).getPrice() == 0 && violationList.get(i).getQuotedprice() == 1)))
                        if (violationList.get(i).getQuotedprice() == 1) {
                            listItemID.add(i);
                        }
                }
            }
        }
        totalprice=0;
        totaldegree=0;
        totalservicefee=0;
        totalNormalDegree=0;
        for (int i = 0; i < listItemID.size(); i++) {
            if ((violationList.get(listItemID.get(i)).getIscommit()!=1)||
                    (!(violationList.get(i).getDegree()==0&& violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==1))) {
                totalprice += violationList.get(listItemID.get(i)).getPrice()
                        +violationList.get(listItemID.get(i)).getCount()
                        +violationList.get(listItemID.get(i)).getLatefee();

                totaldegree += violationList.get(listItemID.get(i)).getDegree();

                totalservicefee +=violationList.get(listItemID.get(i)).getPrice();

                totalNormalDegree +=violationList.get(listItemID.get(i)).getDegree();
            }
        }

        totalprice_tv.setText("￥"+totalprice);

        if ((listItemID.size() == 0) && totalprice == 0) {
            commitOrder_btn.setClickable(false);
            commitOrder_btn.setBackgroundResource(R.drawable.tijiaoa2);
        } else {
            commitOrder_btn.setClickable(true);
            commitOrder_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tijiao_bg));
        }

        count_illegal_choose_tv.setText(listItemID.size()+"");//已选多少条违章

        commit_layout= (RelativeLayout) view.findViewById(R.id.commit_layout);
        no_illegal_layout= (LinearLayout) view.findViewById(R.id.no_illegal_layout);
        if(violationList.isEmpty()){
            no_illegal_layout.setVisibility(View.VISIBLE);
            commit_layout.setVisibility(View.INVISIBLE);
        }
    }

    class SelectBrocastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int quotedpriceType=intent.getIntExtra("quotedprice",0);
            int price=intent.getIntExtra("price",0);
            int count=intent.getIntExtra("count",0);
            int degree=intent.getIntExtra("degree",0);
            int lateFre=intent.getIntExtra("lateFre",0);
            int isSelect=intent.getIntExtra("isSelect",-1);

            if(isSelect==1&&quotedpriceType==1){
                totalprice+=(price+count+lateFre);
                totalservicefee+=price;
                totaldegree+=degree;
                totalNormalDegree+=degree;
            }else if(isSelect==0&&quotedpriceType==1){
                totalprice-=(price+count+lateFre);
                totaldegree-=degree;
                totalservicefee-=price;
                totalNormalDegree-=degree;
            }

            listItemID.clear();
            for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
                if (illegalListAdapter.mChecked.get(i)) {
                    if (violationList.get(i).iscommit==1&&
                            (!(violationList.get(i).getDegree()==0 && violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==1)))
                        if(violationList.get(i).getQuotedprice()==1){
                            listItemID.add(i);
                        }
                }
            }


            if(totalprice<0){
                totalprice=0;
                totalservicefee=0;
            }
            if(totaldegree<0){totaldegree=0;}
            if(listItemID.isEmpty()){
                totalprice_tv.setText("￥"+0);
            }else{
                totalprice_tv.setText("￥"+totalprice);
            }

            if((listItemID.size()==0)&&totalprice==0){
//                commitOrder_btn.setClickable(false);
                commitOrder_btn.setBackgroundResource(R.drawable.tijiaoa2);
                totalprice_tv.setText("￥"+0);
            }else{
                commitOrder_btn.setClickable(true);
                commitOrder_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tijiao_bg));
            }

            count_illegal_choose_tv.setText(listItemID.size()+"");//已选多少条违章
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.payment_instructions_tv://温馨提示
                TipsDialog tipsDialog1=new TipsDialog(getActivity(), R.style.Dialog);
                tipsDialog1.show();
                break;

            case R.id.commitOrder_btn://提交按钮
                if(UIHelper.isFastDoubleClick()){
                    Commit12OrderTipsDialog commit12OrderTipsDialog;
                    KonwDialog konwDialog;
//                    if(carnumber.substring(0,1).equals("闽")||carnumber.substring(0,2).equals("苏E")){
//                        commit12OrderTipsDialog=new Commit12OrderTipsDialog(getActivity(),RealTimeFragment.this,R.style.Dialog,getString(R.string.dialog_commit_fujian_tips));
//                        commit12OrderTipsDialog.show();
//                    }else{

//                        commitOrder();
//                    }
                    konwDialog=new KonwDialog(getActivity(),RealTimeFragment.this,R.style.Dialog);//用户须知显示
                    konwDialog.show();
                }
                break;
        }
    }


    List<Violation> order_violations=new ArrayList<Violation>();
    String peccancyids = "";//违章id拼成
    AddDravingLisenceTipsDialog addDravingLisenceTipsDialog;//补充资料弹出框
    public void getPeccancyids(){
        UIHelper.showPd(getActivity());
        listItemID.clear();
        for (int i = 0; i < illegalListAdapter.mChecked.size(); i++) {
            if (illegalListAdapter.mChecked.get(i)) {
                if (violationList.get(i).getIscommit()==1&&
                        (!(violationList.get(i).getDegree()==0 &&
                                violationList.get(i).getPrice()==0&&violationList.get(i).getQuotedprice()==1)))
                    if(violationList.get(i).getQuotedprice()==1){
                        listItemID.add(i);
                    }
            }
        }

        order_violations.clear();
        peccancyids = "";
        for (int i = 0; i < listItemID.size(); i++) {
            order_violations.add(violationList.get(listItemID.get(i)));
            if (i == listItemID.size() - 1) {
                peccancyids += violationList.get(listItemID.get(i)).getId();
            } else {
                peccancyids += violationList.get(listItemID.get(i)).getId() + ",";
            }
        }
    }

    //提交订单
    public void commitOrder(){
        if ((listItemID.size() == 0) && totalprice == 0) {
//                    Toast.makeText(IllegalQueryActivty.this,"未选择任何违章或违章已全部提交，用户可以到主界面的\"订单查询\"页面中查看",Toast.LENGTH_LONG).show();
            CommitsTipsDialog commitTipsDialog = new CommitsTipsDialog(getActivity());
            commitTipsDialog.show();
        } else {
            if (addDravingLisenceTipsDialog != null) {
                addDravingLisenceTipsDialog.dismiss();
            }
            addDravingLisenceTipsDialog = new AddDravingLisenceTipsDialog(getActivity(), R.style.Dialog,carid);

            if (searchtype == 1 &&((IllegalQueryActivty)getActivity()).getIsNeedLisence() == 1) {

                addDravingLisenceTipsDialog.show();
            } else {
                if (searchtype == 1) {//代扣分订单提交
                    if (!listItemID.isEmpty()) {
                        commitType1Order();
                    }
                } else if (searchtype == 2) {//本人本车订单提交
                    if (totalNormalDegree >= 12) {
                        Toast.makeText(getActivity(), "本人本车订单所选违章总扣分不应大于或等于12分", Toast.LENGTH_SHORT).show();
                    } else {
                        commitType2Order();
                    }
                }
            }
        }
    }


    /**
     * 非本人本车订单提交
     * */
    public void commitType1Order(){
        getPeccancyids();//获取选中的违章Id
        if(!listItemID.isEmpty()) {
            commitOrder(EncryptUtil.encrypt(commitOrderParams(peccancyids)), URLs.COMMIT_ORDER);
        }
    }

    /**
     * 本人本车订单提交
     * */
    public void commitType2Order(){
        getPeccancyids();//获取选中的违章Id
        ownerOderSupplyInfo(EncryptUtil.encrypt(ownerOderSupplyInfoParams(peccancyids)));
    }

    /*
    * 本人本车 订单提交 补充资料
    */
    public HashMap<String,Object> ownerOderSupplyInfoParams(String peccancyids){
        map=new HashMap<String,Object>();
        String token = TokenSQLUtils.check();
        map.put("token",token);
        map.put("carid",carid);
        map.put("peccancyids",peccancyids);//违章ID拼成
        return map;
    }


    HashMap<String, Object> map;
    /*
    * 非本人本车提交订单请求参数
    * */
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


    /**
     * 本人本车订单提交需要补充资料信息
     * */
    public void ownerOderSupplyInfo(final HashMap<String,Object> map){
        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(URLs.OEDER_SUPPLY_INFO, map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","本人本车订单提交车辆检测是否需要补充资料:"+jsonObject.toString());
                boolean isNeedCarCode = false,isNeedIdCar=false,isNeedTiaoxingma=false,isNeedZhengshu=false,isNeedXingshi=false,isNeedMajorViolation=false;
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),getActivity()));
                    JSONObject dataObj=obj.getJSONObject("data") ;

                    String status=dataObj.getString("status");
                    if(status.equals("ok")){
                        commitOrder(EncryptUtil.encrypt(commitOrderParams(peccancyids)), URLs.COMMIT_OWNER_ORDER);//不需要补充资料直接提交
                    }else{
                        JSONArray fieldNameArr=dataObj.getJSONArray("fieldList");
                        for(int i=0;i<fieldNameArr.length();i++) {
                            String fieldName=fieldNameArr.getJSONObject(i).getString("fieldName");
                            if(fieldName.equals("carcode")) {
                                isNeedCarCode =true;//是否需要补充填写发动机号
                            }else if(fieldName.equals("ownercardlen")) {
                                isNeedIdCar =true;//是否需要添加车主身份证号
                            }else if(fieldName.equals("tiaoxingmalen")) {
                                isNeedTiaoxingma = true;//是否需要添加驾照条形码号
                            }else if(fieldName.equals("cheliangzhengshulen")) {
                                isNeedZhengshu = true;//是否需要添加车辆登记证书号
                            }else if(fieldName.equals("xingshizhenghaoLen")) {
                                isNeedXingshi = true;//是否需要添加行驶证档案编号
                            }else if(fieldName.equals("majorviolation")) {
                                isNeedMajorViolation =true;//是否需要上传驾驶证照片
                            }
                        }
                        boolean[] isNeed={isNeedCarCode,isNeedIdCar,isNeedTiaoxingma,isNeedZhengshu,isNeedXingshi,isNeedMajorViolation};

                        AddMajorDialog2 addMajorDialog2=new AddMajorDialog2(getActivity(), R.style.Dialog,isNeed,carid);
                        addMajorDialog2.show();

                    }

                } catch (JSONException e) {
                    boolean[] isNeed={isNeedCarCode,isNeedIdCar,isNeedTiaoxingma,isNeedZhengshu,isNeedXingshi,isNeedMajorViolation};
                    AddMajorDialog2 addMajorDialog2=new AddMajorDialog2(getActivity(), R.style.Dialog,isNeed,carid);
                    addMajorDialog2.show();
                }finally {
                    UIHelper.dismissPd();
                }
            }

            @Override
            public void ResponError(VolleyError volleyError) {
                Toast.makeText(getActivity(),"本人本车订单提交车辆检测是否需要补充资料异常:"+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 提交订单
     * */
    public String ordercode="";//普通订单编号
    public void commitOrder(HashMap<String, Object> map, final String url) {
        VolleyUtil.getVolleyUtil(getActivity()).StringRequestPostVolley(url,map, new VolleyInterface() {
            @Override
            public void ResponseResult(Object jsonObject) {
                Log.i("TAG","提交订单获取到的信息："+jsonObject.toString());
                try {
                    JSONObject obj=new JSONObject(EncryptUtil.decryptJson(jsonObject.toString(),getActivity()));
                    JSONObject dataObj=obj.getJSONObject("data");
                    String status=obj.getString("status");
                    int code=obj.getInt("code");
                    if(status.equals("fail")){
                        if(dataObj.has("code")) {
                            code = dataObj.getInt("code");
                        }
                    }

                    if(url.equals(URLs.COMMIT_ORDER)) {//普通订单

                        UIHelper.showErrTips(code,getActivity());//提示错误信息

                        if(code==0){
                            Toast.makeText(getActivity(),"提交订单成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"请求发生异常，提交订单失败",Toast.LENGTH_SHORT).show();
                        }
                        ordercode = dataObj.getString("ordercode");//得到普通订单号
                        Log.i("TAG","普通订单编号："+ordercode);
                    }else if(url.equals(URLs.COMMIT_OWNER_ORDER)){//本人本车订单
                        if(code==0){
                            Toast.makeText(getActivity(),"提交订单成功",Toast.LENGTH_SHORT).show();
                        }else{
//                            Toast.makeText(IllegalQueryActivty.this,"请求发生异常，提交订单失败",Toast.LENGTH_SHORT).show();
                            UIHelper.showErrTips(code,getActivity());//提示错误信息
                        }
                        if(dataObj.has("ordercode")) {
                            ordercode = dataObj.getString("ordercode");
                        }
                        Log.i("TAG","本人本车订单编号："+ordercode);
                    }

                    if(status.equals("ok")){

                        MainFragment.positionList.clear();

                       if(url.equals(URLs.COMMIT_ORDER)){//普通订单
                            if(orderDialog!=null){
                                orderDialog.dismiss();
                            }
                            showOrderDialog();
                        }else if(url.equals(URLs.COMMIT_OWNER_ORDER)){//本人本车
                            if(orderDialog!=null){
                                orderDialog.dismiss();
                            }
                            showOrderDialog();
                        }
                    }else if(status.equals("fail")){
                        String errormsg=dataObj.getString("errormsg");
                        Toast.makeText(getActivity(),"订单提交失败，"+errormsg,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"请求发生异常，提交订单失败",Toast.LENGTH_SHORT).show();
                }finally {
                    UIHelper.dismissPd();
                    IllegalQueryActivty illegalQueryActivty= (IllegalQueryActivty) getActivity();
                    illegalQueryActivty.getData(illegalQueryActivty.queryUrl);
                    MainFragment.qurery(MainFragment.queryGetParams(((IllegalQueryActivty)getActivity()).carid),carnumber);//提交违章后刷新首页车辆的违章显示
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
        orderDialog= new OrderDialog(getActivity(), R.style.Dialog, order_violations, totalprice, totalNormalDegree,totalservicefee,totalNormalDegree,ordercode,carid,searchtype);
        orderDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                //默认返回 false
                return i == KeyEvent.KEYCODE_SEARCH;
            }
        });
        orderDialog.showDialog();
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
