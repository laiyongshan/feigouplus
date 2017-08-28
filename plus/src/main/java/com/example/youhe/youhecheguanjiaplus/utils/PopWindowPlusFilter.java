package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.databinding.PopupWindowsPlusFilterBinding;

/**
 * 车主卡分享信息
 * Created by Administrator on 2017/2/23 0023.
 */

public class PopWindowPlusFilter extends PopupWindow implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    PopupWindowsPlusFilterBinding b;
    private Activity context;
    private int price;//输入金额
    private int carNumber;//搜索卡号

    public PopWindowPlusFilter(Activity context) {
        super(context);
        this.context = context;

        init();
    }

    private void init() {
        b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_windows_plus_filter, null, false);
        this.setContentView(b.getRoot());
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setWidth(w);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//        this.setAnimationStyle(R.style.popup_top);
//        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new BitmapDrawable());
//        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.soft_white)));

        //添加pop窗口关闭事件
//        this.setOnDismissListener(new poponDismissListener());
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        this.update();
        event();
    }

    /**
     * 事件
     */
    private void event() {
        b.chUnauthorized.setOnCheckedChangeListener(this);
        b.chAuthorized.setOnCheckedChangeListener(this);
        b.chActivate.setOnCheckedChangeListener(this);
        b.chExpired.setOnCheckedChangeListener(this);


        b.btnRestart.setOnClickListener(this);
        b.btnOk.setOnClickListener(this);
        b.layoutDismiss.setOnClickListener(this);

        b.editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isRestart();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isRestart();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnClickListenter(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    private int selectRadio = -1;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.ch_unauthorized:
                if (b)
                    selectRadio = 0;
                break;
            case R.id.ch_authorized:
                if (b)
                    selectRadio = 1;
                break;
            case R.id.ch_activate:
                if (b)
                    selectRadio = 2;
                break;
            case R.id.ch_expired:
                if (b)
                    selectRadio = 3;
                break;
        }
        isRestart();
    }

    private void isRestart() {
        boolean is = false;
        if (b.chUnauthorized.isChecked()
                || b.chAuthorized.isChecked() || b.chActivate.isChecked() || b.chExpired.isChecked()
                || StringUtils.isEmpty(b.editSearch.getText().toString().trim())
                || StringUtils.isEmpty(b.editMoney.getText().toString().trim()))
            is = true;

        if (is)
            b.btnRestart.setEnabled(true);
        else
            b.btnRestart.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_restart:
                restart();
                break;
            case R.id.btn_ok:
                ok();
                break;
            case R.id.layout_dismiss:
                PopWindowPlusFilter.this.dismiss();
                break;
        }
    }

    /**
     * 确定
     */
    private void ok() {

//        onItemClickListener.itemClick(b.editSearch.getText().toString().trim(),b.editMoney.getText().toString().trim(),
//                b.chUnauthorized.isChecked(),b.chAuthorized.isChecked(),b.chActivate.isChecked(),b.chExpired.isChecked());
        onItemClickListener.itemClick(b.editSearch.getText().toString().trim(), b.editMoney.getText().toString().trim(), selectRadio);
    }

    /**
     * 重置
     */
    private void restart() {
        selectRadio=-1;
        b.editSearch.setText("");
        b.editSearch.setHint("请输入您想要的卡号");
        b.editMoney.setText("");
        b.editMoney.setHint("请输入您的金额");

        b.chUnauthorized.setChecked(false);
        b.chAuthorized.setChecked(false);
        b.chActivate.setChecked(false);
        b.chExpired.setChecked(false);

        b.btnRestart.setEnabled(false);
    }


    public interface OnItemClickListener {
        //        void itemClick(String carNumber,String price,boolean isUnauthorized,boolean isAuthorized,boolean isActivate,boolean isExpired);
        void itemClick(String carNumber, String price, int selectRadio);
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
