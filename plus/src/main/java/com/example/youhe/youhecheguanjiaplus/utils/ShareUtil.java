package com.example.youhe.youhecheguanjiaplus.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * 分享
 * Created by Administrator on 2017/7/10 0010.
 */

public class ShareUtil {

    public static void shareMsg(Context context, String activityTitle, String msgTitle, String content, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || StringUtils.isEmpty(imgPath)) {
            intent.setType("text/plain");
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
//        boolean isWinXin = false;
//        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
//        List<Intent> targetedShareIntents = new ArrayList<>();
//        for (ResolveInfo info : resInfo) {
//            Intent targeted = new Intent(Intent.ACTION_SEND);
//            targeted.setType("text/plain");
//            ActivityInfo activityInfo = info.activityInfo;
//            String packageName = activityInfo.packageName.toLowerCase();
//            if (packageName.contains("bluetooth"))
//                continue;
//            String msgText = content;
//            targeted.putExtra(Intent.EXTRA_TEXT, msgText);
//            targeted.setPackage(activityInfo.packageName);
//            targetedShareIntents.add(targeted);
//        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        Intent chooserIntent = Intent.createChooser(intent, "分享到:");
//        if (chooserIntent == null) {
//            return;
//        }
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        try {
            context.startActivity(chooserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
        }







    }

}
