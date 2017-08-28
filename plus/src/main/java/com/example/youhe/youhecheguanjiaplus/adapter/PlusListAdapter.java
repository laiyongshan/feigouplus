package com.example.youhe.youhecheguanjiaplus.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.bean.PlusBean;
import com.example.youhe.youhecheguanjiaplus.databinding.AdapterPlusListBinding;
import com.example.youhe.youhecheguanjiaplus.utils.PopWindowShare;
import com.example.youhe.youhecheguanjiaplus.utils.QrCodeUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;

/**
 * 车主卡详情适配器
 * Created by Administrator on 2017/7/11 0011.
 */

public class PlusListAdapter extends CommonBindAdapter<PlusBean> {

    private Activity context;

    public PlusListAdapter(Activity context) {
        super(context, new ArrayList<PlusBean>(), R.layout.adapter_plus_list);
        this.context = context;
    }

    @Override
    public void convert(ViewDataBinding bind, final PlusBean bean, int position) {
        final AdapterPlusListBinding item = (AdapterPlusListBinding) bind;

        if (bean == null || item == null)
            return;
        item.state.setText(bean.getStatus_name());
        item.price.setText("¥ " + bean.getPrice() + "");
        item.date.setText("有效期至：" + bean.getExpire_time() + "");
        if (!StringUtils.isEmpty(bean.getCard_number()) && bean.getCard_number().length() == 16) {
            String a = bean.getCard_number();
            String bb = a.substring(0, 4) + "  " + a.substring(4, 8) + "  " + a.substring(8, 12) + "  " + a.substring(12, 16);
            item.carNumber.setText(bb);
        } else {
            item.carNumber.setText("");
        }
//2分销车主卡3普通车主卡
        if (bean.getClient_type()==3)
            item.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.putgong));
        else
            item.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.sanji));

        //分享
        item.textShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(bean.getContent())){
                    UMWeb umWeb=new UMWeb(bean.getUrl());
                    umWeb.setDescription(bean.getContent());
                    umWeb.setTitle(bean.getTitle());
                    umWeb.setThumb(new UMImage(context,R.mipmap.icon_app));
                    share(umWeb,item.getRoot());
                }else {
                    ToastUtil.getShortToastByString(context,"获取分享内容失败");
                }
            }
        });
        //复制
        item.textCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(bean.getContent())){
                    copy(bean.getCard_number());
                }else {
                    ToastUtil.getShortToastByString(context,"复制车主卡号失败");
                }
            }
        });

        //二维码
        item.textQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(bean.getUrl())){
                    ToastUtil.getShortToastByString(context,"生成二维码失败");
                    return;
                }
                ImageView imageView=new ImageView(context);
                imageView.setImageBitmap(QrCodeUtil.generateBitmap(bean.getUrl(),450,450));
                Dialog dialog=new Dialog(context);
                dialog.setContentView(imageView);
                dialog.show();
            }
        });



        switch (bean.getStatus()) {
            case PlusBean.STATE_UNAUTHORIZED://未授权
                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_new_color_primary));
//                item.state.setText("未授权");
                item.state.setTextColor(context.getResources().getColor(R.color.plus_orange));

                item.carNumber.setTextColor(context.getResources().getColor(R.color.white));

                item.date.setTextColor(context.getResources().getColor(R.color.plus_shallow_orange));

                item.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.vip_weijihuo));

                item.price.setTextColor(context.getResources().getColor(R.color.plus_orange));
                item.textShare.setEnabled(true);
                item.textCopy.setEnabled(true);
                break;
            case PlusBean.STATE_AUTHORIZED://已授权
                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_deep_blue));
//                item.state.setText("已授权");
                item.state.setTextColor(context.getResources().getColor(R.color.plus_deep_green));

                item.carNumber.setTextColor(context.getResources().getColor(R.color.white));

                item.date.setTextColor(context.getResources().getColor(R.color.white));

                item.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.vip_yijihuo));

                item.price.setTextColor(context.getResources().getColor(R.color.plus_deep_green));
                item.textShare.setEnabled(true);
                item.textCopy.setEnabled(true);
                break;
            case PlusBean.STATE_ACTIVATE://已激活
//                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_gray));
//                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_deep_blue));
                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_blue));
//                item.state.setText("已激活");
                item.state.setTextColor(context.getResources().getColor(R.color.plus_shallow_green));

                item.carNumber.setTextColor(context.getResources().getColor(R.color.white));

                item.date.setTextColor(context.getResources().getColor(R.color.plus_deep_gray));

                item.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.vip_yishiyong));

                item.price.setTextColor(context.getResources().getColor(R.color.plus_shallow_green));
                item.textShare.setEnabled(false);
                item.textCopy.setEnabled(false);
                break;
            case PlusBean.STATE_EXPIRED://已过期
                item.layout.setBackground(context.getResources().getDrawable(R.drawable.round_corner_gray));
//                item.state.setText("已过期");
                item.state.setTextColor(context.getResources().getColor(R.color.red));

                item.carNumber.setTextColor(context.getResources().getColor(R.color.black));

                item.date.setTextColor(context.getResources().getColor(R.color.hint_gray));

                item.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.vip_yiguoqi));

                item.price.setTextColor(context.getResources().getColor(R.color.hint_gray));
                item.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                item.textShare.setEnabled(false);
                item.textCopy.setEnabled(false);
                break;
        }

    }
    /**
     * 复制内容到粘贴板
     *
     * @param content
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
        Toast.makeText(context, "车主卡号已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

    /**
     * 分享 弹出框
     */
    PopWindowShare popWindowShare = null;

    private void share(UMWeb U,View view) {
        if (popWindowShare == null)
            popWindowShare = new PopWindowShare(context);

        popWindowShare.setDate(U);
        popWindowShare.showAtLocation(view,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
}
