package com.example.youhe.youhecheguanjiaplus.mainfragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;
import com.example.youhe.youhecheguanjiaplus.adapter.PlusAdapter;
import com.example.youhe.youhecheguanjiaplus.app.AppContext;
import com.example.youhe.youhecheguanjiaplus.bean.PlusBean;
import com.example.youhe.youhecheguanjiaplus.databinding.ItemPlusBinding;
import com.example.youhe.youhecheguanjiaplus.databinding.PlusFragmentBinding;
import com.example.youhe.youhecheguanjiaplus.db.biz.TokenSQLUtils;
import com.example.youhe.youhecheguanjiaplus.dialog.UIDialog;
import com.example.youhe.youhecheguanjiaplus.https.URLs;
import com.example.youhe.youhecheguanjiaplus.logic.YeoheFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.AddPlusActivity;
import com.example.youhe.youhecheguanjiaplus.ui.base.PlusListActivity;
import com.example.youhe.youhecheguanjiaplus.utils.OnVolleyInterface;
import com.example.youhe.youhecheguanjiaplus.utils.PopWindowPlusFilter;
import com.example.youhe.youhecheguanjiaplus.utils.PopWindowShare;
import com.example.youhe.youhecheguanjiaplus.utils.QrCodeUtil;
import com.example.youhe.youhecheguanjiaplus.utils.StringUtils;
import com.example.youhe.youhecheguanjiaplus.utils.UIHelper;
import com.example.youhe.youhecheguanjiaplus.utils.VolleyUtil;
import com.example.youhe.youhecheguanjiaplus.widget.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 首页 车主卡
 * Created by Administrator on 2017/7/5 0005.
 */

public class PlusFragment extends YeoheFragment implements View.OnClickListener {


    private static final int ADD_CAR = 0;
    public static final String EXTRA_MORE = "extra_more";

    public static final String NET_TOKEN = "token";
    public static final String NET_PAGE = "page";
    public static final String NET_PAGE_SIZE = "page_size";
    public static final String NET_STATUS = "status";
    public static final String NET_CARD_NUMBER = "card_number";
    public static final String NET_PRICE = "price";

    public static final String SAVE_KET = "plus_list";

    private PlusFragmentBinding b = null;
    private PlusAdapter plusAdapter = null;
    private List<View> views = null;
    private ArrayList<PlusBean> plusList = null;
    private LayoutInflater inflater = null;
    private VolleyUtil volleyUtil = null;
    private UIDialog uiDialog = null;

    private String token = "";
    private String page = DEFAULT_PAGE + "";
    private String page_size = DEFAULT_PAGE_SIZE + "";
    private String status = "";
    private String card_number = "";
    private String price = "";

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.plus_fragment, null, false);

        initView();
        return b.getRoot();
    }

    private void initView() {
        views = new ArrayList<>();
        plusList = new ArrayList<>();
        plusAdapter = new PlusAdapter(views, getActivity(), plusList);
        b.viewpager.setAdapter(plusAdapter);
        b.viewpager.setOffscreenPageLimit(3);
        b.viewpager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin));
        b.viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

        inflater = getActivity().getLayoutInflater();

        uiDialog = new UIDialog(getActivity(), "加载中...");
        volleyUtil = VolleyUtil.getVolleyUtil(getActivity());
        event();
        isLoad = true;
        loadDate();
    }


    private boolean isLoad = false;

    @Override
    public void onResume() {
        super.onResume();
//        if (!isLoad)
//            loadDate();
    }

    /**
     * 获取数据
     */
    private void loadDate() {
        HashMap<String, String> hashMap = new HashMap<>();
        String token = TokenSQLUtils.check();
        hashMap.put(NET_TOKEN, token);
        hashMap.put(NET_PAGE, DEFAULT_PAGE + "");
        hashMap.put(NET_PAGE_SIZE, DEFAULT_PAGE_SIZE + "");
        hashMap.put(NET_STATUS, "");
        hashMap.put(NET_CARD_NUMBER, "");
        hashMap.put(NET_PRICE, "");
//        page page_size  status  card_number price
//        Log.d("TAGG",hashMap.toString());
        loadData(hashMap);
    }

    private String resultData = "";

    private void loadData(HashMap<String, String> hashMap) {
        try {

            token = hashMap.get(NET_TOKEN);
            page = hashMap.get(NET_PAGE);
            page_size = hashMap.get(NET_PAGE_SIZE);
            status = hashMap.get(NET_STATUS);
            card_number = hashMap.get(NET_CARD_NUMBER);
            price = hashMap.get(NET_PRICE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uiDialog.show();
        volleyUtil.postRequest(getActivity(), URLs.PLUS_LIST, hashMap, "获取车主卡信息失败", new OnVolleyInterface() {
            @Override
            public void success(JSONObject dataObject, String resultStr) {
                try {
                    if (plusList != null)
                        plusList.clear();
                    resultData = dataObject.getString("card_number_list");

                    plusList = new Gson().fromJson(resultData, new TypeToken<List<PlusBean>>() {
                    }.getType());

                    initPlus(plusList);
                    isLoad = false;
                    b.viewpager.setAdapter(plusAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    checkViewByData();
                    isLoad = false;
                }
                uiDialog.hide();

            }

            @Override
            public void failed(JSONObject resultObject, String code, String msg) {
                checkViewByData();
                if (!StringUtils.isEmpty(msg) && !msg.equals("false"))
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                isLoad = false;
                uiDialog.hide();
            }
        });
    }

    @Override
    public void init() {

    }

    /**
     * 跳到开始与最后一页 分享
     */
    private void event() {
        b.imageFirst.setOnClickListener(this);
        b.imageEnd.setOnClickListener(this);
        b.imageFilter.setOnClickListener(this);
        b.textShare.setOnClickListener(this);
        b.add.setOnClickListener(this);
        b.textCopy.setOnClickListener(this);
        b.textShowMore.setOnClickListener(this);
        b.textQR.setOnClickListener(this);

        b.imageAutoRefresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_first:
                if (plusList.size() > 0)
                    b.viewpager.setCurrentItem(0);
                break;
            case R.id.image_end:
                if (plusList.size() > 0)
                    b.viewpager.setCurrentItem(plusList.size() - 1);
                break;
            case R.id.text_share:
                share();
                break;
            case R.id.text_QR:
                QR();
                break;
            case R.id.image_filter:
                filter();
                break;
            case R.id.add:
//                if (StringUtils.i)
                if (!AppContext.isLogin){
                    UIHelper.showLoginActivity(getActivity());
                    UIHelper.ToastMessage(getActivity(), "请先登录");
                }else {
                    add();
                }
                break;
            case R.id.text_copy:
                PlusBean plusBean = plusList.get(b.viewpager.getCurrentItem());
                if (plusBean != null && !StringUtils.isEmpty(plusBean.getContent())) {
                    copy(plusBean.getCard_number());
                } else {
                    ToastUtil.getShortToastByString(getActivity(), "复制车主卡号失败");
                }

                break;
            case R.id.image_auto_refresh:
                loadDate();
                break;
            case R.id.text_show_more:
                Intent intent = new Intent(getActivity(), PlusListActivity.class);
                intent.putExtra(EXTRA_MORE, resultData);
                intent.putExtra(NET_TOKEN, token);
                intent.putExtra(NET_PAGE, page);
                intent.putExtra(NET_PAGE_SIZE, page_size);
                intent.putExtra(NET_CARD_NUMBER, card_number);
                intent.putExtra(NET_STATUS, status);
                intent.putExtra(NET_PRICE, price);
                startActivity(intent);
//                Intent intent=new Intent(getActivity(), CommentWebActivity.class);
//                intent.putExtra("url","file:///android_asset/test.html");
//                intent.putExtra("title","test");
//                startActivity(intent);
                break;
        }
    }

    /**
     * 查看二维码
     */
    private void QR() {
        PlusBean plusBean = plusList.get(b.viewpager.getCurrentItem());
        if (plusBean != null && !StringUtils.isEmpty(plusBean.getUrl())) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(QrCodeUtil.generateBitmap(plusBean.getUrl(), 450, 450));
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(imageView);
            dialog.show();
        } else {
            ToastUtil.getShortToastByString(getContext(), "生成二维码失败");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (volleyUtil != null)
            volleyUtil.cancelAllQueue(getActivity());
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
        Toast.makeText(getActivity(), "车主卡号已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

    /**
     * 筛选
     */
    private PopWindowPlusFilter popWindowPlusFilter = null;

    private void filter() {
        if (popWindowPlusFilter == null)
            popWindowPlusFilter = new PopWindowPlusFilter(getActivity());
        if (popWindowPlusFilter.isShowing())
            popWindowPlusFilter.dismiss();
        else {
//            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//            lp.alpha = 0.7f;
            popWindowPlusFilter.showAsDropDown(b.layoutTitle);
//            popWindowPlusFilter.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.soft_white)));
//            popWindowPlusFilter.backgroundAlpha(0.7f);
//            getActivity().getWindow().setAttributes(lp);
        }
        popWindowPlusFilter.setOnClickListenter(new PopWindowPlusFilter.OnItemClickListener() {
            @Override
            public void itemClick(String carNumber, String price, int selectRadio) {
                popWindowPlusFilter.dismiss();
                HashMap<String, String> hashMap = new HashMap<>();
                String token = TokenSQLUtils.check();
                hashMap.put(NET_TOKEN, token);
                hashMap.put(NET_PAGE, "1");
                hashMap.put(NET_PAGE_SIZE, "20");
                if (selectRadio >= 0)
                    hashMap.put(NET_STATUS, (selectRadio + 1) + "");

                hashMap.put(NET_CARD_NUMBER, StringUtils.isEmpty(carNumber) ? "" : carNumber);
                hashMap.put(NET_PRICE, StringUtils.isEmpty(price) ? "" : price);


                loadData(hashMap);
            }
        });

    }

    /**
     * 添加车主卡
     */
    private void add() {
        Intent intent = new Intent(getActivity(), AddPlusActivity.class);
        startActivityForResult(intent, ADD_CAR);

    }


    /**
     * 分享 弹出框
     */
    PopWindowShare popWindowShare = null;

    private void share() {
        if (popWindowShare == null)
            popWindowShare = new PopWindowShare(getActivity());
//        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.alpha = 0.7f;
//        getActivity().getWindow().setAttributes(lp);
        try {
            PlusBean plusBean = plusList.get(b.viewpager.getCurrentItem());
            if (plusBean != null && !StringUtils.isEmpty(plusBean.getContent())) {
                UMWeb umWeb = new UMWeb(plusBean.getUrl());
                umWeb.setDescription(plusBean.getContent());
                umWeb.setTitle(plusBean.getTitle());
                umWeb.setThumb(new UMImage(getContext(), R.mipmap.icon_app));
                popWindowShare.setDate(umWeb);
//                popWindowShare.setDate(plusBean.getContent() + "\r\n" + plusBean.getUrl());
                popWindowShare.showAtLocation(b.getRoot(),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            } else {
                ToastUtil.getShortToastByString(getActivity(), "获取分享内容失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.getShortToastByString(getActivity(), "获取分享内容失败");
        }

    }

    private boolean checkViewByData() {
        if (plusList == null || plusList.size() <= 0) {
            b.viewpager.setVisibility(View.GONE);
            b.layoutNo.setVisibility(View.VISIBLE);
            b.layoutAction.setVisibility(View.INVISIBLE);
            b.layoutShare.setVisibility(View.INVISIBLE);
            return false;
        } else {
            b.viewpager.setVisibility(View.VISIBLE);
            b.layoutNo.setVisibility(View.GONE);
            b.layoutAction.setVisibility(View.VISIBLE);
            b.layoutShare.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private void initPlus(final List<PlusBean> list) {
        if (plusList == null || plusList.size() <= 0) {
            b.viewpager.setVisibility(View.GONE);
            b.layoutNo.setVisibility(View.VISIBLE);
            b.layoutAction.setVisibility(View.INVISIBLE);
            b.layoutShare.setVisibility(View.INVISIBLE);
            return;
        } else {
            b.viewpager.setVisibility(View.VISIBLE);
            b.layoutNo.setVisibility(View.GONE);
            b.layoutAction.setVisibility(View.VISIBLE);
            b.layoutShare.setVisibility(View.VISIBLE);
        }

        ItemPlusBinding item = null;
        View view = null;

        if (list.size() == 1)
            b.layoutAction.setVisibility(View.INVISIBLE);
        else
            b.layoutAction.setVisibility(View.VISIBLE);
        views.clear();
//        Log.d("TAG",list.toString());
        for (int i = 0; i < list.size(); i++) {
            item = DataBindingUtil.inflate(inflater, R.layout.item_plus, null, false);

            view = loadView(list.get(i), item);

            if (view != null)
                views.add(view);
        }
        try {
            int state = plusList.get(0).getStatus();
            switch (state) {
                case PlusBean.STATE_UNAUTHORIZED://未授权
                    isShareAndCopyEnable(true);
                    break;
                case PlusBean.STATE_AUTHORIZED://已授权
                    isShareAndCopyEnable(true);
                    break;
                case PlusBean.STATE_ACTIVATE://已激活
                    isShareAndCopyEnable(false);
                    break;
                case PlusBean.STATE_EXPIRED://已过期
                    isShareAndCopyEnable(false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int a = list.size();
        b.textPage.setText((b.viewpager.getCurrentItem() + 1) + "/" + a);
    }

    /**
     * 加载车主卡页面
     */
    String a = "";
    String bb = "";

    private View loadView(PlusBean bean, ItemPlusBinding item) {
        if (bean == null || item == null)
            return null;
//        item.state.setText(bean.getStatus_name() + "  " + (bean.getClient_type() == 3 ? "普通" : "分销"));
        item.state.setText(bean.getStatus_name());
        item.price.setText("¥ " + bean.getPrice() + "");
        if (StringUtils.isEmpty(bean.getExpire_time())) {
            item.date.setText("");
        } else
            item.date.setText("有效期至：" + bean.getExpire_time() + "");
        if (!StringUtils.isEmpty(bean.getCard_number()) && bean.getCard_number().length() == 16) {
            a = bean.getCard_number();
            bb = a.substring(0, 4) + "  " + a.substring(4, 8) + "  " + a.substring(8, 12) + "  " + a.substring(12, 16);
            item.carNumber.setText(bb);
        } else {
            item.carNumber.setText("");
        }

        //2分销车主卡3普通车主卡
        if (bean.getClient_type() == 3)
            item.imageType.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.putgong));
        else
            item.imageType.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.sanji));

        switch (bean.getStatus()) {
            case PlusBean.STATE_UNAUTHORIZED://未授权
                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_new_color_primary));
//                item.state.setText("未授权");
                item.state.setTextColor(getContext().getResources().getColor(R.color.plus_orange));

                item.carNumber.setTextColor(getContext().getResources().getColor(R.color.white));

                item.date.setTextColor(getContext().getResources().getColor(R.color.plus_shallow_orange));

                item.imageState.setImageDrawable(getContext().getResources().getDrawable(R.drawable.vip_weijihuo));

                item.price.setTextColor(getContext().getResources().getColor(R.color.plus_orange));

                break;
            case PlusBean.STATE_AUTHORIZED://已授权
                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_deep_blue));
//                item.state.setText("已授权");
                item.state.setTextColor(getContext().getResources().getColor(R.color.plus_deep_green));

                item.carNumber.setTextColor(getContext().getResources().getColor(R.color.white));

                item.date.setTextColor(getContext().getResources().getColor(R.color.white));

                item.imageState.setImageDrawable(getContext().getResources().getDrawable(R.drawable.vip_yijihuo));

                item.price.setTextColor(getContext().getResources().getColor(R.color.plus_deep_green));
                break;
            case PlusBean.STATE_ACTIVATE://已激活
//                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_gray));
//                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_deep_blue));
                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_blue));
//                item.state.setText("已激活");
                item.state.setTextColor(getContext().getResources().getColor(R.color.plus_shallow_green));

                item.carNumber.setTextColor(getContext().getResources().getColor(R.color.white));

                item.date.setTextColor(getContext().getResources().getColor(R.color.plus_deep_gray));

                item.imageState.setImageDrawable(getContext().getResources().getDrawable(R.drawable.vip_yishiyong));

                item.price.setTextColor(getContext().getResources().getColor(R.color.plus_shallow_green));
                break;
            case PlusBean.STATE_EXPIRED://已过期
                item.layout.setBackground(getContext().getResources().getDrawable(R.drawable.round_corner_gray));
//                item.state.setText("已过期");
                item.state.setTextColor(getContext().getResources().getColor(R.color.red));

                item.carNumber.setTextColor(getContext().getResources().getColor(R.color.black));

                item.date.setTextColor(getContext().getResources().getColor(R.color.hint_gray));

                item.imageState.setImageDrawable(getContext().getResources().getDrawable(R.drawable.vip_yiguoqi));

                item.price.setTextColor(getContext().getResources().getColor(R.color.hint_gray));
                item.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                break;
        }

        return item.getRoot();
    }

    @Override
    public void refresh(Object... param) {
        loadDate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            loadDate();
    }

    private void isShareAndCopyEnable(boolean isEnable) {
        b.textShare.setEnabled(isEnable);
        b.textCopy.setEnabled(isEnable);
        b.textQR.setEnabled(isEnable);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            try {
                if (plusList != null && plusList.size() > 0) {
                    b.textPage.setText((b.viewpager.getCurrentItem() + 1) + "/" + plusList.size());
                    plusList.get(position);
                    int state = plusList.get(position).getStatus();
                    switch (state) {
                        case PlusBean.STATE_UNAUTHORIZED://未授权
                            isShareAndCopyEnable(true);
                            break;
                        case PlusBean.STATE_AUTHORIZED://已授权
                            isShareAndCopyEnable(true);
                            break;
                        case PlusBean.STATE_ACTIVATE://已激活
                            isShareAndCopyEnable(false);
                            break;
                        case PlusBean.STATE_EXPIRED://已过期
                            isShareAndCopyEnable(false);
                            break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }

}
