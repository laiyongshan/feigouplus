package com.example.youhe.youhecheguanjiaplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;


/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class TipsDialog extends Dialog {
    private Activity activity;
    int REQUESTCODE = 10086;

    public TipsDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tips);
        ImageView dismiss_img = (ImageView) findViewById(R.id.dismiss_img);
        dismiss_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

//        final TextView service_call_num_tv = (TextView) findViewById(R.id.service_call_num_tv);
//        service_call_num_tv.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String text = service_call_num_tv.getText().toString().trim();
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                PackageManager pm = activity.getPackageManager();
//                // 获取是否支持电话
//                boolean telephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
//                if(telephony) {
//                    try {
//                        activity.startActivity(intent);
//                    }catch (ActivityNotFoundException exception){
//                        Toast.makeText(activity,"no activity",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
    }

}
