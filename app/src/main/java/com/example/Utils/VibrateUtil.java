package com.example.Utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class VibrateUtil {

    private static final String TAG = "VibrateUtil";

    /**
     * 让手机振动millsecond毫秒
     * @param context
     * @param millsecond
     */
    public static void vibrate(Context context, long millsecond) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) {
            vibrator.vibrate(millsecond);
            Log.e(TAG, "vibrate: ---------开启振动");
        }
    }

    /**
     * 自定义振动模式
     * @param context
     * @param pattern = {1000, 20000, 10000, 10000, 30000}
     * @param repeat
     */
    public static void vibrate(Context context, long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) {
            vibrator.vibrate(pattern, repeat);
            Log.e(TAG, "vibrate: ---------开启自定义振动");
        }
    }

    /**
     * 停止振动
     * @param context
     */
    public static void virateCancle(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.cancel();
        Log.e(TAG, "vibrate: ---------关闭自定义振动");
    }
}
