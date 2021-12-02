package com.drunkbull.drunkbullcloudcashbook.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class ToastUtil {

    static ToastUtil instance = null;
    static Context context = null;
    static int hideDelay = 0;

    static int ANIMATION_DURATION = 500;

    boolean isShow = false;
    AlphaAnimation fadeInAnimation = null;
    AlphaAnimation fadeOutAnimation = null;

    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {

        }
    };

    static TextView textView = null;
    static View container = null;

    public ToastUtil(Context ctx) {
        context = ctx;
    }

    enum LENGTH{
        SHORT,
        LONG
    }

    public static ToastUtil makeText(Context ctx, String message, LENGTH hd) {
        if (instance == null) {
            instance = new ToastUtil(context);
        } else {
            // 考虑Activity切换时，Toast依然显示
            if (!context.getClass().getName().endsWith(context.getClass().getName())) {
                instance = new ToastUtil(context);
            }
        }
        if (hd == LENGTH.LONG) {
            instance.hideDelay = 2500;
        } else {
            instance.hideDelay = 1500;
        }
        textView.setText(message);
        return instance;
    }

    public static ToastUtil makeText(Context context, int resId, LENGTH hd) {
        String mes = "";
        try {
            mes = context.getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return makeText(context, mes, hd);
    }

    public void show() {
        if (isShow) {
            // 若已经显示，则不再次显示
            return;
        }
        isShow = true;
        // 显示动画
        fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        // 消失动画
        fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(ANIMATION_DURATION);
        fadeOutAnimation
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // 消失动画后更改状态为 未显示
                        isShow = false;
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 隐藏布局，不使用remove方法为防止多次创建多个布局
                        container.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
        container.setVisibility(View.VISIBLE);
        fadeInAnimation.setDuration(ANIMATION_DURATION);
        container.startAnimation(fadeInAnimation);
        handler.postDelayed(runnable, hideDelay);
    }

}
