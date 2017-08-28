package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class PswDialog extends Dialog implements View.OnClickListener{

    private ImageView dismiss_img;
    private EditText user_psw_et;
    private Button commit_check_btn;

    private int from;

    private Activity activity;


    public interface OnSureListener
    {
        void onSure(String psw);
    }
    public OnSureListener mSureListener = null;


    public PswDialog(Activity activity, int themeResId,int from) {
        super(activity, themeResId);

        this.activity=activity;

        this.from=from;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_real_name);

        initView();//初始化控件
    }

    private void  initView(){
        dismiss_img= (ImageView) findViewById(R.id.dismiss_img);
        dismiss_img.setOnClickListener(this);
        user_psw_et= (EditText) findViewById(R.id.user_psw_et);
        commit_check_btn= (Button) findViewById(R.id.commit_check_btn);
        commit_check_btn.setOnClickListener(this);
        if(from==1){
            commit_check_btn.setText("确   定");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.dismiss_img:
                dismiss();
                break;

            case R.id.commit_check_btn:

                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }


                if(user_psw_et.getText().toString().trim().equals("")){
                    Toast.makeText(activity,"请输入用户密码！",Toast.LENGTH_SHORT).show();
                }else{
                    if (PswDialog.this.mSureListener != null){
                        try
                        {
                            String psw=user_psw_et.getText().toString().trim();

                            PswDialog.this.mSureListener.onSure(psw);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
}
