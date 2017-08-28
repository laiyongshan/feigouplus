package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.ShareItemAdapter;
import com.example.youhe.youhecheguanjiaplus.bean.TextImageBean;
import com.example.youhe.youhecheguanjiaplus.databinding.PopupWindowsPlusBinding;

import java.util.ArrayList;

/**
 * 车主卡分享信息
 * Created by Administrator on 2017/2/23 0023.
 */

public abstract class PopWindowGrid extends PopupWindow{

    PopupWindowsPlusBinding b;
    protected Activity context;
    private int price;//输入金额
    private int carNumber;//搜索卡号

    public PopWindowGrid(Activity context) {
        super(context);
        this.context = context;

        init();
    }

    private void init() {
        b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_windows_plus, null, false);
        this.setContentView(b.getRoot());
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setWidth(w);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.popup);
//        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new BitmapDrawable());
//        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.soft_white)));

        //添加pop窗口关闭事件
//        this.setOnDismissListener(new poponDismissListener());
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        adapter=new ShareItemAdapter(context);
        beens.clear();
        beens=initGrid();
        adapter.setData(beens);
        b.gridView.setAdapter(adapter);

        this.update();
        event();
    }

    private ArrayList<TextImageBean> beens=new ArrayList<>();
    private ShareItemAdapter adapter=null;
    protected abstract ArrayList<TextImageBean> initGrid() ;
    protected abstract void itemClick(int position);

    /**
     * 事件
     */
    private void event() {

        b.layoutDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopWindowGrid.this.dismiss();
            }
        });
        b.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopWindowGrid.this.dismiss();
            }
        });
        b.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (onItemClickListener!=null)
//                    onItemClickListener.itemClick(i);
                itemClick(i);
                PopWindowGrid.this.dismiss();
            }
        });

    }

//    private OnItemClickListener onItemClickListener = null;
//    public void setOnClickListenter(OnItemClickListener onClickListener) {
//        this.onItemClickListener = onClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void itemClick(int position);
//    }

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
