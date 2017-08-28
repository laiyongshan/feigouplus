package com.example.youhe.youhecheguanjiaplus.utils;

import android.view.View;
import android.view.animation.Animation;

import java.util.List;

/**
 * 这是一个执行多View，多动画的工具类
 * Created by Administrator on 2017/5/10 0010.
 */

public class AnimManager implements Animation.AnimationListener{
    private final List<View> views;
    private final List<Animation> anims;
    private boolean isLast;
    private OnEndListener onEndListener;
    private int position=0;
    private View view;
    private Animation anim;

    public interface OnEndListener {
        void onStart(Animation animation, View view);

        void onEnd(Animation animation, View view);
    }

    /**
     * 当null == views || views.size() < 0，程序将不继续执行
     */
    public void startAnimation() {

        if (null == views || views.size() < 0) {
            throw new RuntimeException("views集合为空！不能开启动画");
        }

        if (null == anims || anims.size() < 0 || anims.size() != views.size()) {
            throw new RuntimeException("anim集合不能是空的，并且长度要与views的长度一样！不能开启动画");
        }

        excute();
    }

    private void excute() {
        view = views.get(position);
        anim = anims.get(position);
        anim.setAnimationListener(this);
        view.startAnimation(anim);
        view.startAnimation(anim);
    }

    public void setOnEndListener(OnEndListener onEndListener) {
        this.onEndListener = onEndListener;
    }

    public AnimManager(List<View> views, List<Animation> anims) {
        this.views = views;
        this.anims = anims;
    }

    @Override
    public void onAnimationStart(Animation animation) {

        if (null == view) {
            throw new RuntimeException("第" + position + "个view是空的");
        }

        view.setVisibility(View.VISIBLE);

        if (null != onEndListener && 0 == position) {
            onEndListener.onStart(animation, view);
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (null != onEndListener && position == views.size() - 1) {
            onEndListener.onEnd(animation, view);
        }

        if (position == views.size() - 1) {
            // 动画执行结束
            return;
        }

        position++;
        excute();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public int getPosition() {
        return position;
    }
}
