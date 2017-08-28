package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2016/10/15 0015.
 * 提示对话框
 */

public class UIDialog {
    private Context context;
    private String testx;
    private ProgressDialog progressDialog;

    public UIDialog(Context context, String Message) {

        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("提示");
        progressDialog.setMessage(Message);
    }

    public void hide() {

        progressDialog.dismiss();
    }

    public void show() {
        progressDialog.show();
    }
}
