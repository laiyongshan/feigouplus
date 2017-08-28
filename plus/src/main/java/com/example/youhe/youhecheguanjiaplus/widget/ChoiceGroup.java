package com.example.youhe.youhecheguanjiaplus.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.youhe.youhecheguanjiaplus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChoiceGroup extends LinearLayout {

    private int column = 0;//列数

    private int currentIndex = 0;//当前按钮下标

    private String currentValue = "";//当前按钮值

    private List<String> values = new ArrayList<>();//按钮文字列表

    private Map<Integer, Button> map = new HashMap<>();//按钮map

    private Context mContent;
    private Drawable mActiveDrawable,mInactiveDrawable;
    private int mActiveTextColor,mInactiveTextColor;

    private int mCurrentItem=-1;

    public ChoiceGroup(Context context){
        super(context);
        mContent=context;
        init(context);
    }

    public ChoiceGroup(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        mContent=context;
        init(context);
    }

    public ChoiceGroup(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
        mContent=context;
        init(context);
    }

    //初始化容器
    public void init(Context context){
        mActiveDrawable=mContent.getResources().getDrawable(R.drawable.myradio_active);
        mInactiveDrawable=mContent.getResources().getDrawable(R.drawable.myradio_inactive);
        mActiveTextColor=Color.WHITE;
        mInactiveTextColor=Color.WHITE;

        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
    }

    //设置当前被选下按钮
    public void setInitChecked(int index){
        ((MyRadio)map.get(index)).setTouch(2);
        setCurrentValue(((MyRadio)map.get(index)).getText().toString());
        mCurrentItem=index;
    }

    public int getmCurrentItem() {
        return mCurrentItem;
    }

    public void setmCurrentItem(int mCurrentItem) {
        this.mCurrentItem = mCurrentItem;
    }

    public void setColumn(int column){
        this.column = column;
    }

    public void setCurrentValue(String value){
        this.currentValue = value;
        setmCurrentItem(values.indexOf(value));
    }

    public String getCurrentValue(){
        return this.currentValue;
    }

    public int getCurrentIndex(){return this.currentIndex;}

    public void setValues(List<String> values){
        this.values = values;
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

    //初始化容器所有视图
    public void setView(final Context context){
        int size = values.size();
        int row = size/column;
        int leftSize = size%column;
        for(int i=0;i<row;i++){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            for(int j=0;j<column;j++){
                final MyRadio button = new MyRadio(context);
                button.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
                layoutParams.setMargins(10,10, 20, 5);
                button.setLayoutParams(layoutParams);
                button.setText(values.get(column * i + j));

                button.setActiveDrawable(mActiveDrawable);
                button.setInactiveDrawable(mInactiveDrawable);
                button.setInactiveTextColor(mInactiveTextColor);
                button.setActiveTextColor(mActiveTextColor);


                currentIndex = column * i + j;
                button.setOnValueChangedListner(new MyRadio.OnValueChangedListner() {
                    @Override
                    public void OnValueChanged(String value) {
                        setCurrentValue(value);
                        clearSelected(currentIndex);
                    }
                });
                map.put(column * i + j,button);
                linearLayout.addView(button);
            }
            addView(linearLayout);
        }
        if(leftSize != 0){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            for(int m=0;m<column;m++){
                if(m<leftSize) {
                    final MyRadio button = new MyRadio(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layoutParams.setMargins(10,10, 20, 5);
                    button.setGravity(Gravity.CENTER);
                    button.setLayoutParams(layoutParams);
                    button.setText(values.get(size - leftSize + m));

                    button.setActiveDrawable(mActiveDrawable);
                    button.setInactiveDrawable(mInactiveDrawable);
                    button.setInactiveTextColor(mInactiveTextColor);
                    button.setActiveTextColor(mActiveTextColor);

                    currentIndex = size - leftSize + m;
                    button.setOnValueChangedListner(new MyRadio.OnValueChangedListner() {
                        @Override
                        public void OnValueChanged(String value) {
                            setCurrentValue(value);
                            clearSelected(currentIndex);
                        }
                    });
                    map.put(size - leftSize + m,button);
                    linearLayout.addView(button);
                }else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layoutParams.setMargins(10,10, 20, 5);
                    Button view = new Button(context);
                    view.setLayoutParams(layoutParams);
                    view.setGravity(Gravity.CENTER);
                    view.setText("");
                    view.setVisibility(INVISIBLE);
                    view.setBackgroundColor(Color.RED);
                    linearLayout.addView(view);
                }
            }
            addView(linearLayout);
        }
    }

    //清除所有选择
    public void clearSelected(int Index){
        System.out.println("length = "+map.size());
        for(int index = 0;index < map.size(); index ++){
            ((MyRadio)map.get(index)).setTouch(1);
        }
    }

}