package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class CommitsTipsDialog extends Dialog {
    private TextView commit_tips_sure_tv;

    public CommitsTipsDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_commit_tips);

        commit_tips_sure_tv= (TextView) findViewById(R.id.commit_tips_sure_tv);
        commit_tips_sure_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
