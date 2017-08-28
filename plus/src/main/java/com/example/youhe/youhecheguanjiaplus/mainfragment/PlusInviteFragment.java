package com.example.youhe.youhecheguanjiaplus.mainfragment;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.databinding.FragmentPlusInviteBinding;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.CommentWebActivity;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.PopWindowShare;
import com.example.youhe.youhecheguanjiaplus.utils.QrCodeUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 车主卡邀请 页面
 * Created by Administrator on 2017/7/21 0021.
 */

public class PlusInviteFragment extends YeoheFragment implements View.OnClickListener {

    private FragmentPlusInviteBinding b = null;
    private String inviCode = "";
    private String shareURL = "";
    private String content = "";
    private String title = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_plus_invite, null, false);

        initView();

        return b.getRoot();
    }

    private void initView() {
        b.tvQrCode.setOnClickListener(this);
        b.tvShare.setOnClickListener(this);
        b.textCopy.setOnClickListener(this);

        b.imageAutoRefresh.setOnClickListener(this);
        b.record.setOnClickListener(this);
    }

    @Override
    public void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void dealInviCode() {
        if (StringUtils.isEmpty(inviCode))
            return;
        if (!StringUtils.isEmpty(inviCode) && inviCode.length() == 16) {
            b.tv1.setText(inviCode.substring(0, 4));
            b.tv2.setText(inviCode.substring(4, 8));
            b.tv3.setText(inviCode.substring(8, 12));
            b.tv4.setText(inviCode.substring(12, 16));
        } else {
            b.tv1.setText("");
            b.tv2.setText("");
            b.tv3.setText("");
            b.tv4.setText("");
        }
    }

    private void load() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", TokenSQLUtils.check());
        VolleyUtil.getVolleyUtil(getActivity()).postRequest(getActivity(), URLs.PAY_INVICODE, hashMap, "获取邀请码失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
//                Log.d("TAG", resultStr);

                try {
                    if (dataObject.has("invitation_code"))
                        inviCode = dataObject.getString("invitation_code");
                    if (dataObject.has("url"))
                        shareURL = dataObject.getString("url");
                    if (dataObject.has("content"))
                        content = dataObject.getString("content");
                    if (dataObject.has("title"))
                        title = dataObject.getString("title");
                    dealInviCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {

            }
        });
    }

    @Override
    public void refresh(Object... param) {
        load();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share://分享
                if (shareURL == null || StringUtils.isEmpty(shareURL)) {
                    Toast.makeText(getContext(), "获取分享链接失败", Toast.LENGTH_SHORT).show();
                } else
                    share();
                break;
            case R.id.tv_QrCode://查看二维码
                if (StringUtils.isEmpty(shareURL)){
                    ToastUtil.getShortToastByString(getContext(),"生成二维码失败");
                    return;
                }
                ImageView imageView=new ImageView(getContext());
                imageView.setImageBitmap(QrCodeUtil.generateBitmap(shareURL,450,450));
                Dialog dialog=new Dialog(getContext());
                dialog.setContentView(imageView);
                dialog.show();
                break;
            case R.id.text_copy://复制

                if (!StringUtils.isEmpty(inviCode)) {
                    copy(inviCode);
                } else {
                    ToastUtil.getShortToastByString(getActivity(), "复制邀请码失败");
                }
                break;
            case R.id.image_auto_refresh:
                load();
                break;
            case R.id.record://分销记录
                Intent intent=new Intent(getActivity(), CommentWebActivity.class);
                intent.putExtra(CommentWebActivity.EXTRA_TITLE, "");
                intent.putExtra(CommentWebActivity.EXTRA_URL, URLs.PAY_RECORD+ TokenSQLUtils.check());
                getActivity().startActivity(intent);

                break;
        }
    }
    /**
     * 复制内容到粘贴板
     *
     * @param content
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
        Toast.makeText(getActivity(), "邀请码已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

    /**
     * 分享 弹出框
     */
    PopWindowShare popWindowShare = null;

    private void share() {

        if (popWindowShare == null)
            popWindowShare = new PopWindowShare(getActivity());
        UMWeb umWeb = new UMWeb(shareURL);
        umWeb.setDescription(content);
        umWeb.setTitle(title);
        umWeb.setThumb(new UMImage(getContext(), R.mipmap.icon_app));
        popWindowShare.setDate(umWeb);
        popWindowShare.showAtLocation(b.getRoot(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


}
