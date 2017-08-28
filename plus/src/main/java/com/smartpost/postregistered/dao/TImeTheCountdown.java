package com.smartpost.postregistered.dao;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/22 0022.
 * 超时倒计时逻辑类
 */

public class TImeTheCountdown {

    private static final int COUNTDOWN = 1;
    private static final int SWITCH_BITMAP = 2;
    // 倒计时的间隔时间
    private static final long COUNTDOWN_PERIOD = 1000;
    // 切换图片的间隔时间
    private static final long SWITCH_BITMAP_PERIOD = 100;

    private int curIndex;

    private int countDownSec = 60;

    private TextView time;

    public TImeTheCountdown(TextView textView) {
        this.time = textView;
        String times = getTimeTextFromSeconds(countDownSec);
        time.setText(times);
        // 发送一个延迟消息到消息队列中，当该消息的延迟时间到达的时候，才会被分发给Handler进行处理
        startCountDown(COUNTDOWN, COUNTDOWN_PERIOD);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COUNTDOWN:
                    countDownSec--;
                    if (countDownSec < 0) {
                        // 把延迟消息从消息队列中移除,停止倒计时
                        mHandler.removeMessages(COUNTDOWN);
                        // 开始切换图片
                        startCountDown(SWITCH_BITMAP, SWITCH_BITMAP_PERIOD);
                    } else {
                        String times = getTimeTextFromSeconds(countDownSec);
                        time.setText(times);
                        startCountDown(COUNTDOWN, COUNTDOWN_PERIOD);
                    }
                    break;

            }
        }
    };

    private void startCountDown(int what, long period) {
        Message msg = Message.obtain();
        msg.what = what;
        mHandler.sendMessageDelayed(msg, period);
    }

    private String getTimeTextFromSeconds(int seconds) {

        int hour = seconds / 3600;

        int munite = seconds / 60 % 60;

        int second = seconds % 60;

        StringBuilder sb = new StringBuilder();

        String h = getNumber(hour);
        String m = getNumber(munite);
        String s = getNumber(second);

        sb.append(h).append(":").append(m).append(":").append(s);
//        sb.append(s);
        return sb.toString();
    }

    private String getNumber(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

}
