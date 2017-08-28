package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupWindow;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.databinding.PopupWindowsTadingRecordFilterBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 车主卡分享信息
 * Created by Administrator on 2017/2/23 0023.
 */

public class PopWindowTadingRecordFilter extends PopupWindow implements  View.OnClickListener {

    PopupWindowsTadingRecordFilterBinding b;
    private Activity context;
    private List<String> list1=null;
    private List<String> list2=null;

    private Calendar calendar=null;

    public PopWindowTadingRecordFilter(Activity context) {
        super(context);
        this.context = context;

        init();
    }

    private void init() {
        b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_windows_tading_record_filter, null, false);
        this.setContentView(b.getRoot());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setWidth(w);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.anim.slide_down_out);
//        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        this.setBackgroundDrawable(new BitmapDrawable());
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.soft_white)));

        //添加pop窗口关闭事件
        this.setOnDismissListener(new poponDismissListener());
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (UserManager.getUserType()==UserManager.USER_PLUS||UserManager.getUserType()==UserManager.ROLE_HONOUR_CARD){
            b.tradingLayout.setVisibility(View.VISIBLE);
            b.tradingLayout.setVisibility(View.VISIBLE);
        }else {
            b.tradingLayout.setVisibility(View.GONE);
            b.tradingView.setVisibility(View.GONE);
        }

        initData();

        this.update();
        event();
    }

    private void initData(){
        list1=new ArrayList<>();
        list1.add("收 入");
        list1.add("支 出");

        b.choiceRecoGroup.setActiveTextColor(context.getResources().getColor(R.color.white));
        b.choiceRecoGroup.setInactiveTextColor(context.getResources().getColor(R.color.hint_gray));
        b.choiceRecoGroup.setInactiveDrawable(context.getResources().getDrawable(R.drawable.shape_corner_white_gray));
        b.choiceRecoGroup.setActiveDrawable(context.getResources().getDrawable(R.color.new_color_primary));

        b.choiceRecoGroup.setColumn(2);//设置列数
        b.choiceRecoGroup.setValues(list1);//设置记录列表
        b.choiceRecoGroup.setView(context);//设置视图
//        b.choiceRecoGroup.setInitChecked(0);//设置最初默认被选按钮

        b.choiceTypeGroup.setActiveTextColor(context.getResources().getColor(R.color.white));
        b.choiceTypeGroup.setInactiveTextColor(context.getResources().getColor(R.color.black));
        b.choiceTypeGroup.setInactiveDrawable(context.getResources().getDrawable(R.drawable.shape_corner_white_gray));
        b.choiceTypeGroup.setActiveDrawable(context.getResources().getDrawable(R.color.new_color_primary));

        list2=new ArrayList<>();

        calendar=Calendar.getInstance();

    }
    public void setData(ArrayList<String> list){

        list2.clear();
        list2.addAll(list);

        b.choiceTypeGroup.setColumn(3);//设置列数
        b.choiceTypeGroup.setValues(list2);//设置记录列表
        b.choiceTypeGroup.setView(context);//设置视图
//        b.choiceTypeGroup.setInitChecked(0);//设置最初默认被选按钮
    }



    /**
     * 事件
     */
    private void event() {
        b.startDateTv.setOnClickListener(this);
        b.overDateTv.setOnClickListener(this);
        b.sureBtn.setOnClickListener(this);
        b.resetBtn.setOnClickListener(this);

        datePickerDialog1=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                b.startDateTv.setText(i+"-"+(i1+1)+"-"+i2);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog2=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                b.overDateTv.setText(i+"-"+(i1+1)+"-"+i2);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnClickListenter(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    private DatePickerDialog datePickerDialog1=null;
    private DatePickerDialog datePickerDialog2=null;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_date_tv://开始时间
                if (datePickerDialog1!=null)
                datePickerDialog1.show();
                break;
            case R.id.over_date_tv://结束时间
                if (datePickerDialog2!=null)
                    datePickerDialog2.show();
                break;
            case R.id.sure_btn:
                onItemClickListener.itemClick(b.startDateTv.getText().toString(),b.overDateTv.getText().toString().trim(),
                        b.choiceRecoGroup.getmCurrentItem(),b.choiceTypeGroup.getmCurrentItem(),b.tradingCardcodeEt.getText().toString().trim());
                this.dismiss();
                break;
            case R.id.reset_btn:
                b.startDateTv.setText("");
                b.startDateTv.setHint("请选择开始日期");
                b.overDateTv.setText("");
                b.overDateTv.setHint("请选择结束日期");
                b.tradingCardcodeEt.setText("");
                b.tradingCardcodeEt.setHint("请填写交易卡号");

                b.choiceRecoGroup.clearSelected(b.choiceRecoGroup.getCurrentIndex());
                b.choiceRecoGroup.setmCurrentItem(-1);

                b.choiceTypeGroup.clearSelected(b.choiceTypeGroup.getCurrentIndex());
                b.choiceTypeGroup.setmCurrentItem(-1);
                break;
        }
    }


    public interface OnItemClickListener {
        void itemClick(String startDateStr,String endDateStr,int formIndex,int typeIndex,String cardNumber);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }


    class poponDismissListener implements OnDismissListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }
    }


}
