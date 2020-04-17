package com.example.Utils;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;



public class ScreenListener {

    private Context context;

    private ScreenLockedReceiver screenLockedReceiver;

    private ScreenStateListener screenStateListener;

    private static final String TAG = "ScreenListener";

    public ScreenListener(Context context) {
        this.context = context;
        screenLockedReceiver = new ScreenLockedReceiver();
    }

    //自定义接口
    public interface ScreenStateListener {

        void onScreenOn();

        void onScreenOff();

        void onUserPresent();
    }

    private void getScreenState() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(powerManager.isScreenOn()) {   //如果监听开启
            if(screenStateListener != null) {
                screenStateListener.onScreenOn();
            }
        } else {                          //如果监听未开启
            if(screenStateListener != null) {
                screenStateListener.onScreenOff();
            }
        }
    }


    public void begin(ScreenStateListener listener) {
        screenStateListener = listener;
        registerScreenLockedListener();
        getScreenState();
    }

    public void registerScreenLockedListener() {
        Log.e(TAG, "registerScreenLocked: -----注册屏幕监听广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(screenLockedReceiver,intentFilter);

    }

    public void unregisterScreenLockedListener() {
        Log.e(TAG, "unregisterScreenLocked: -----注销屏幕监听广播");
        context.unregisterReceiver(screenLockedReceiver);

    }

    public class ScreenLockedReceiver extends BroadcastReceiver {

        private String action = null;

        private static final String TAG = "ScreenLockedReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            switch(action) {
                //屏幕打开
                case Intent.ACTION_SCREEN_ON:
                    screenStateListener.onScreenOn();
                    break;
                //屏幕关闭
                case Intent.ACTION_SCREEN_OFF:
                    screenStateListener.onScreenOff();
                    break;
                //屏幕解锁
                case Intent.ACTION_USER_PRESENT:
                    screenStateListener.onUserPresent();
                    break;
            }
        }
    }


}
