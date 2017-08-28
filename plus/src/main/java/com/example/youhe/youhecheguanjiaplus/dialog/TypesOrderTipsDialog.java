package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class TypesOrderTipsDialog extends Dialog {

    public TypesOrderTipsDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_types_order_tips);

        TextView types_order_tips_sure_tv= (TextView) findViewById(R.id.types_order_tips_sure_tv);
        types_order_tips_sure_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
