package com.example.youhe.youhecheguanjiaplus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.youhe.youhecheguanjiaplus.R;


public class MyRadio extends Button implements View.OnTouchListener{

    private boolean isTouched = false;//是否被按下

    private int touch = 1;//按钮被按下的次数
    private Context mContent;
    private Drawable mActiveDrawable,mInactiveDrawable;
    private int mActiveTextColor,mInactiveTextColor;

    public MyRadio(Context context){
        super(context);
        mContent=context;
        init();
    }

    public MyRadio(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mContent=context;
        init();
    }

    public MyRadio(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        mContent=context;
        init();
    }

    protected void init(){
        mActiveDrawable=mContent.getResources().getDrawable(R.drawable.myradio_active);
        mInactiveDrawable=mContent.getResources().getDrawable(R.drawable.myradio_inactive);
        mActiveTextColor=Color.WHITE;
        mInactiveTextColor=Color.WHITE;
        setOnTouchListener(this);
    }

    public void setActiveDrawable(Drawable drawable){
        mActiveDrawable=drawable;
    }
    public void setInactiveDrawable(Drawable drawable){
        mInactiveDrawable=drawable;
    }

    public void setActiveTextColor(int color){
        mActiveTextColor=color;
    }
    public void setInactiveTextColor(int color){
        mInactiveTextColor=color;
    }

    public void setTouch(int touch){
        this.touch = touch;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(0 == touch%2){
            this.setBackground(mActiveDrawable);
            this.setTextColor(mActiveTextColor);
//            this.setPadding(5,5,5,5);]
            this.setTextSize(14);
        }else {
//            this.setBackgroundResource(R.drawable.myradio_inactive);
            this.setBackground(mInactiveDrawable);
            this.setTextColor(mInactiveTextColor);
//            this.setPadding(5,5,5,5);
            this.setTextSize(14);
        }
        invalidate();
    }

    public void setTouched(boolean isTouched){
        this.isTouched = isTouched;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                onValueChangedListner.OnValueChanged(this.getText().toString());
                isTouched = true;
                touch ++;
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                break;
        }
        return true;
    }

    public interface OnValueChangedListner{
        void OnValueChanged(String value);
    }

    //实现接口，方便将当前按钮的值回调
    OnValueChangedListner onValueChangedListner;

    public void setOnValueChangedListner(OnValueChangedListner onValueChangedListner){
        this.onValueChangedListner = onValueChangedListner;
    }
}
