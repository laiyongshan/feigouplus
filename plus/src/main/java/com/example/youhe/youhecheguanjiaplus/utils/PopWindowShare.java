package com.example.youhe.youhecheguanjiaplus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.TextImageBean;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享弹出框
 * Created by Administrator on 2017/7/10 0010.
 */

public class PopWindowShare extends PopWindowGrid {

    private ArrayList<TextImageBean> beens;

    public PopWindowShare(Activity context) {
        super(context);
    }

    @Override
    protected ArrayList<TextImageBean> initGrid() {
        beens = new ArrayList<>();
        beens.add(new TextImageBean("微信", context.getResources().getDrawable(R.drawable.weixin)));
        beens.add(new TextImageBean("QQ", context.getResources().getDrawable(R.drawable.qq)));
        beens.add(new TextImageBean("短信", context.getResources().getDrawable(R.drawable.duanxin)));
        beens.add(new TextImageBean("微信朋友圈", context.getResources().getDrawable(R.drawable.pengyouquan)));

        return beens;
    }

    private String content = "";

    public void setDate(String content) {
        this.content = content;
    }

    private UMWeb mUmWeb;

    public void setDate(UMWeb umWeb) {
        mUmWeb = umWeb;
    }

    @Override
    protected void itemClick(int position) {
        switch (position) {
            case 0://微信
                if (isAvilible(context, "com.tencent.mm")) {

                    if (mUmWeb != null) {
                        new ShareAction(context).setPlatform(SHARE_MEDIA.WEIXIN)
                                .withMedia(mUmWeb)
//                                .setShareContent(shareContent)
                                .setCallback(umShareListener)
                                .share();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");//微信朋友
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, content);
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "您还未安装微信", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1://QQ

                if (isAvilible(context, "com.tencent.mobileqq")) {
                    if (mUmWeb != null) {
                        new ShareAction(context).setPlatform(SHARE_MEDIA.QQ)
                                .withMedia(mUmWeb)
//                                .setShareContent(shareContent)
                                .setCallback(umShareListener)
                                .share();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                        intent.setType("text/plain");
//                    List<ResolveInfo> infos=context.getPackageManager().queryIntentActivities(imageIntent,PackageManager.MATCH_DEFAULT_ONLY);
//                imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                imageIntent.putExtra(Intent.EXTRA_TITLE,"天好圈");
                        intent.putExtra(Intent.EXTRA_TEXT, content);
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "您还未安装QQ", Toast.LENGTH_SHORT).show();
                }

                break;
            case 2://短信
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + ""));
                if (mUmWeb != null) {
                    intent.putExtra("sms_body", mUmWeb.getTitle() + "\r\n" + mUmWeb.getDescription() + "\r\n" + mUmWeb.toUrl());
                } else
                    intent.putExtra("sms_body", content);
                context.startActivity(intent);
                break;
            case 3://微信朋友圈
                if (isAvilible(context, "com.tencent.mm")) {
                    if (mUmWeb != null) {
                        new ShareAction(context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withMedia(mUmWeb)
                                .setCallback(umShareListener)
                                .share();
                    } else {
                        new ShareAction(context).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withText(content)
                                .setCallback(umShareListener)
                                .share();
                    }
//                    Intent intentSend= new Intent(Intent.ACTION_SEND);
//                    intentSend.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                } else {
                    Toast.makeText(context, "您还未安装微信", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

//            Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
}
