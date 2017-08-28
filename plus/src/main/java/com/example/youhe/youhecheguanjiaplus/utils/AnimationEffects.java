package com.example.youhe.youhecheguanjiaplus.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ImageView;

import static com.example.youhe.youhecheguanjiaplus.app.AppContext.getContext;

/**
 * Created by Administrator on 2016/10/21 0021.
 * 设置动画效果工具
 */

public class AnimationEffects {

    private static ValueAnimator animator;
    /**
     * 向上刷卡
     */
    public final static void twoWayCreditCard(final ImageView twoWayCreditCard) {
        animator = new ValueAnimator();//创建值动画；
        animator.setTarget(twoWayCreditCard);//指定要处理的View
        animator.setIntValues(0, 1000);//一定要设置
        animator.setDuration(4200);//设置他的播放时间
//        animator.setRepeatCount(animator.INFINITE);//设置他无限播放
//        animator.setRepeatMode(animator.REVERSE);
        animator.setRepeatCount(1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//给ValueAnimator设置一个值动画监听事件

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager wm = (WindowManager) getContext()//得到得到屏幕宽度
                        .getSystemService(Context.WINDOW_SERVICE);
                twoWayCreditCard.setAlpha(animation.getAnimatedFraction());//逐渐显示

                twoWayCreditCard.setTranslationY(-200 * animation.getAnimatedFraction());//向上平移
//                twoWayCreditCard.setTranslationY(wm.getDefaultDisplay().getHeight()* animation.getAnimatedFraction());//向下平移

//				imageView.setRotationX(2*animation.getAnimatedFraction());//放缩小
//				imageView.setScaleX(1 * animation.getAnimatedFraction());//旋转


            }
        });
        animator.start();
    }

    /**
     * 向下刷卡
     */
    public final static void twoWayXiaCreditCard(final ImageView twoWayCreditCard) {
       animator = new ValueAnimator();//创建值动画；
        animator.setTarget(twoWayCreditCard);//指定要处理的View
        animator.setIntValues(0, 1000);//一定要设置
        animator.setDuration(4200);//设置他的播放时间
//        animator.setRepeatCount(animator.INFINITE);//设置他无限播放
//        animator.setRepeatMode(animator.REVERSE);
        animator.setRepeatCount(1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//给ValueAnimator设置一个值动画监听事件

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager wm = (WindowManager) getContext()//得到得到屏幕宽度
                        .getSystemService(Context.WINDOW_SERVICE);
                twoWayCreditCard.setAlpha(animation.getAnimatedFraction());//逐渐显示

                twoWayCreditCard.setTranslationY(200 * animation.getAnimatedFraction());//向上平移
//                twoWayCreditCard.setTranslationY(wm.getDefaultDisplay().getHeight()* animation.getAnimatedFraction());//向下平移

//				imageView.setRotationX(2*animation.getAnimatedFraction());//放缩小
//				imageView.setScaleX(1 * animation.getAnimatedFraction());//旋转


            }
        });
        animator.start();
    }


    /**
     * 插卡
     */
    public static void cardWay(final ImageView twoWayCreditCard) {
        animator = new ValueAnimator();//创建值动画；
        animator.setTarget(twoWayCreditCard);//指定要处理的View
        animator.setIntValues(0, 1000);//一定要设置
        animator.setDuration(4200);//设置他的播放时间
        animator.setRepeatCount(animator.INFINITE);//设置他无限播放
//        animator.setRepeatMode(animator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//给ValueAnimator设置一个值动画监听事件

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager wm = (WindowManager) getContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                twoWayCreditCard.setAlpha(animation.getAnimatedFraction());//逐渐显示
                twoWayCreditCard.setTranslationY(-110 * animation.getAnimatedFraction());//平移
//				imageView.setRotationX(2*animation.getAnimatedFraction());//放缩小
//				imageView.setScaleX(1 * animation.getAnimatedFraction());//旋转


            }
        });
        animator.start();
    }

    /**
     * 滴卡
     */
    public static void DropsOfCard(final ImageView twoWayCreditCard) {
        animator = new ValueAnimator();//创建值动画；
        animator.setTarget(twoWayCreditCard);//指定要处理的View
        animator.setIntValues(0, 1000);//一定要设置
        animator.setDuration(4200);//设置他的播放时间
        animator.setRepeatCount(animator.INFINITE);//设置他无限播放
//        animator.setRepeatMode(animator.REVERSE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//给ValueAnimator设置一个值动画监听事件

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager wm = (WindowManager) getContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                twoWayCreditCard.setAlpha(animation.getAnimatedFraction());//逐渐显示
                twoWayCreditCard.setTranslationY(-150 * animation.getAnimatedFraction());
                twoWayCreditCard.setTranslationX(170 * animation.getAnimatedFraction());
//				imageView.setRotationX(2*animation.getAnimatedFraction());//放缩小
//				imageView.setScaleX(1 * animation.getAnimatedFraction());//旋转


            }
        });
        animator.start();
    }

}
