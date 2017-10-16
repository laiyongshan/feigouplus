package com.example.youhe.youhecheguanjiaplus.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.app.UserManager;
import com.example.youhe.youhecheguanjiaplus.entity.base.Car;
import com.example.youhe.youhecheguanjiaplus.mainfragment.MainFragment;
import com.example.youhe.youhecheguanjiaplus.ui.base.EditCarActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class CarInfoAdapter extends PagerAdapter {

    // 界面列表
    private final List<View> views;
    private final Activity activity;
    private List<Car> carList;

    public CarInfoAdapter(List<View> views, Activity activity, List<Car> carList) {
        this.views = views;
        this.activity = activity;
        this.carList = carList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView(views.get(position));
        container.removeView((View) object);
    }

    @Override
    public void finishUpdate(View arg0) {

    }

    // 获得当前界面数
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    // 初始化arg1位置的界面
    String carid = "";//车辆id
    String carnumber = "";//车牌号码
    String carcode = "";//车身架号
    String cardrivenumber = "";//发动机号
    String remark = "";//备注
    String ischeck = "";//车辆是否已经认证
    String proprefix = "";//车牌前缀
    String cartype="";//车型
    String carname="";
    String carbrand="";

    @Override
    public Object instantiateItem(View arg0, final int arg1) {
        ((ViewPager) arg0).addView(views.get(arg1), 0);
        if (arg1 <= carList.size() - 1) {
            views.get(arg1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    carid = carList.get(carList.size() - arg1 - 1).getCarId();
                    carnumber = carList.get(carList.size() - arg1 - 1).getCarnumber();
                    carcode = carList.get(carList.size() - arg1 - 1).getCarcode();
                    cardrivenumber = carList.get(carList.size() - arg1 - 1).getEnginenumber();
                    remark = carList.get(carList.size() - arg1 - 1).getRemark();
                    ischeck = carList.get(carList.size() - arg1 - 1).getIsCarCorrect();
                    proprefix = carList.get(carList.size() - arg1 - 1).getProprefix();
                    carbrand=carList.get(carList.size() - arg1 - 1).getCarbrand();
                    cartype=carList.get(carList.size() - arg1 - 1).getCartype();
                    carname=carList.get(carList.size() - arg1 - 1).getCarbrand();

                    Intent intent =new Intent();
                    intent.putExtra("page", arg1+ "");
                    intent.putExtra("carid", carid);
                    intent.putExtra("carnumber", carnumber);
                    intent.putExtra("carcode", carcode);
                    intent.putExtra("cardrivenumber", cardrivenumber);
                    intent.putExtra("remark", remark);
                    intent.putExtra("ischeck", ischeck);
                    intent.putExtra("proprefix", proprefix);
                    intent.putExtra("carname",carname);
                    intent.putExtra("cartype",cartype);
                    intent.putExtra("carbrand",carbrand);

                    if (ischeck.equals("-1")) {
                        if (!UserManager.checkUserStatus()){
                            UserManager.userActivation(activity);
                        }else {
                            intent.setClass(activity, EditCarActivity.class);
                            Toast.makeText(activity, "车辆信息有误，请重新编辑", Toast.LENGTH_LONG).show();
                            activity.startActivityForResult(intent, MainFragment.EDITCAR_RESULTCODE);
                        }
                    } else {
//                        intent = new Intent(activity, IllegalQueryActivty.class);
                    }

                }
            });
        }
        return views.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public int getItemPosition(Object object) {
//        return super.getItemPosition(object);

        return POSITION_NONE;
    }
}
