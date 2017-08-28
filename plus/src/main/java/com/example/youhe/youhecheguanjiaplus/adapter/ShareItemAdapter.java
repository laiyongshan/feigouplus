package com.example.youhe.youhecheguanjiaplus.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.TextImageBean;
import com.example.youhe.youhecheguanjiaplus.databinding.ItemShareBinding;

import java.util.ArrayList;

/**
 * 分享  popWindowShare
 * Created by Administrator on 2017/7/10 0010.
 */

public class ShareItemAdapter extends CommonBindAdapter<TextImageBean> {

    public ShareItemAdapter(Context context) {
        super(context, new ArrayList<TextImageBean>(), R.layout.item_share);
    }

    @Override
    public void convert(ViewDataBinding bind, TextImageBean t, int position) {
        ItemShareBinding b= (ItemShareBinding) bind;
        b.text.setText(t.getName());
        b.image.setImageDrawable(t.getDrawable());
    }
}
