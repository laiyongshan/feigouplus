package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.utils.SystemBarUtil;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class UploadFileHelpActivity extends Activity {

    private ImageView uploadfile_help_back_img;
    private TextView service_call_num_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_help);

        // 4.4及以上版本开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarUtil.setTranslucentStatus(true,UploadFileHelpActivity.this);
        }

        SystemBarUtil.useSystemBarTint(UploadFileHelpActivity.this);

        uploadfile_help_back_img= (ImageView) findViewById(R.id.uploadfile_help_back_img);
        uploadfile_help_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        service_call_num_tv= (TextView) findViewById(R.id.service_call_num_tv);
        service_call_num_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = service_call_num_tv.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PackageManager pm = UploadFileHelpActivity.this.getPackageManager();
                // 获取是否支持电话
                boolean telephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
                if(telephony) {
                    try {
                        UploadFileHelpActivity.this.startActivity(intent);
                    }catch (ActivityNotFoundException exception){
                        Toast.makeText(UploadFileHelpActivity.this,"no activity",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
