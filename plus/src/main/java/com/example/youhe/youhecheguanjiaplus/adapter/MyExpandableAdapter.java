package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.AccountModel;
import com.example.youhe.youhecheguanjiaplus.bean.ChildModel;
import com.example.youhe.youhecheguanjiaplus.dialog.DeleteAcountDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.ui.base.AccountAddCarActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.AccountQueryActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.IllegalQueryActivty;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    AccountQueryActivity context;
    List<AccountModel> models;
    LinkedList<List<ChildModel>> childModelList;


    public MyExpandableAdapter(AccountQueryActivity context, List<AccountModel> models, LinkedList<List<ChildModel>> childModelList){
        super();
        this.context = (AccountQueryActivity)context;
        this.models = models;
        this.childModelList=childModelList;
    }


    @Override
    public int getGroupCount() {
        return models.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childModelList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return models.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childModelList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    //得到子item的ID
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // TODO Auto-generated method stub
        System.out.println(groupPosition+"CollapsedCollapsedCollapsed");
//        parentItems.get(groupPosition).parentItem.setBackgroundResource(R.drawable.category_expandlv_itemone_bg_normal);
//        parentItems.get(groupPosition).arrow.setBackgroundResource(R.drawable.category_iv_oneitem_arrow_down);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        // TODO Auto-generated method stub
//        parentItems.get(groupPosition).parentItem.setBackgroundResource(R.drawable.category_expandlv_itemone_bg_select);
//        parentItems.get(groupPosition).arrow.setBackgroundResource(R.drawable.category_iv_oneitem_arrow_up);
    }


    @Override
    public View getGroupView(final int i, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        ParentViewHolder parentViewHolder=null;
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.item_parent_expandable_listview,null);
            parentViewHolder=new ParentViewHolder(convertView);

            convertView.setTag(parentViewHolder);
        }else{
            parentViewHolder= (ParentViewHolder)convertView.getTag();
        }
        if (models!=null&&models.get(i)!=null) {
            parentViewHolder.account_tv.setText(models.get(i).getVehicleAccount());
            parentViewHolder.account_province_tv.setText(models.get(i).getProvinceName());
            parentViewHolder.account_add_car_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AccountAddCarActivity.class);
                    intent.putExtra("vehicleAccount", models.get(i).getVehicleAccount());
                    intent.putExtra("userType", models.get(i).getUserType());
                    context.startActivity(intent);
                }
            });

            //删除账号
            parentViewHolder.delete_count_tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DeleteAcountDialog dialog=new DeleteAcountDialog(context, R.style.Dialog,models.get(i).getVehicleAccount(),context);
                    dialog.show();
                }
            });

            //判断isExpanded就可以控制是按下还是关闭，同时更换图片
            if (isExpanded) {
                parentViewHolder.category_iv_itemone_arrow.setBackgroundResource(R.drawable.category_iv_oneitem_arrow_up);
            } else {
                parentViewHolder.category_iv_itemone_arrow.setBackgroundResource(R.drawable.category_iv_oneitem_arrow_down);
            }
        }
        return convertView;
    }


    @Override
    public View getChildView(final int i, final int i1, boolean b, View convertView, ViewGroup viewGroup) {

        ChildViewHolder childViewHolder=null;
        if(childViewHolder==null){

            convertView=LayoutInflater.from(context).inflate(R.layout.item_child_expandable_listview,null);
            childViewHolder=new ChildViewHolder(convertView);

            convertView.setTag(childViewHolder);
        }else{
            childViewHolder= (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.carnum_tv.setText(childModelList.get(i).get(i1).getProprefix()+childModelList.get(i).get(i1).getCarnum());
        childViewHolder.car_vin_tv.setText("车身架号："+childModelList.get(i).get(i1).getCarVin());
        childViewHolder.car_engine_no_tv.setText("发动机号：" +
                ""+childModelList.get(i).get(i1).getCarEngineNo());


        //点击去查询违章
        childViewHolder.child_expandable_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, IllegalQueryActivty.class);
                intent.putExtra("carid",childModelList.get(i).get(i1).getId());
                intent.putExtra("carnumber",childModelList.get(i).get(i1).getProprefix()+childModelList.get(i).get(i1).getCarnum());
                intent.putExtra("searchtype",1);
                intent.putExtra("queryUrl", URLs.QUERY_VIOLATION_122);
                intent.putExtra("vehicleAccount",models.get(i).getVehicleAccount()+"");

                context.startActivity(intent);
            }
        });

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }



    class ParentViewHolder{
        RelativeLayout parentItem;
        TextView account_tv,account_province_tv,account_add_car_tv;
        ImageView category_iv_itemone_arrow;

        TextView delete_count_tv;//删除账号按钮

        public ParentViewHolder(View convertView){
            parentItem= (RelativeLayout) convertView.findViewById(R.id.parent_item);
            account_tv= (TextView) convertView.findViewById(R.id.account_tv);
            account_province_tv= (TextView) convertView.findViewById(R.id.account_province_tv);
            account_add_car_tv= (TextView) convertView.findViewById(R.id.account_add_car_tv);
            category_iv_itemone_arrow= (ImageView) convertView.findViewById(R.id.category_iv_itemone_arrow);

            delete_count_tv= (TextView) convertView.findViewById(R.id.delete_count_tv);
        }
    }



    class ChildViewHolder{
        TextView carnum_tv,car_vin_tv,car_engine_no_tv;
        LinearLayout child_expandable_layout;

        public ChildViewHolder(View convertView){
            carnum_tv= (TextView) convertView.findViewById(R.id.carnum_tv);
            car_vin_tv= (TextView) convertView.findViewById(R.id.car_vin_tv);
            car_engine_no_tv= (TextView) convertView.findViewById(R.id.car_engine_no_tv);
            child_expandable_layout= (LinearLayout) convertView.findViewById(R.id.child_expandable_layout);
        }
    }

}
