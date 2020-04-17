package com.example.BroadcastReveiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.Utils.VibrateUtil;

import com.example.myapplication.BaseApplication;
import com.example.myapplication.R;
import com.example.myapplication.ScreenLockedActivity;

public class BluetoothListenerReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothListenerReceiv";

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        if(action != null) {
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED://手机的蓝牙状态
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    CompoundButton openBlueTooth = (CompoundButton) ((Activity)context).findViewById(R.id.openBlueTooth);
                    //SharedPreferences sharedPreferences = context.getSharedPreferences("BLUETOOTH_STATUS", Context.MODE_WORLD_READABLE);
                    //SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                    switch(bluetoothState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e(TAG, "onReceive---------蓝牙正在打开中");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e(TAG, "onReceive---------蓝牙已打开");
                            Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                            openBlueTooth.setChecked(true);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e(TAG, "onReceive---------蓝牙正在关闭中");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.e(TAG, "onReceive---------蓝牙已关闭");
                            Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                            openBlueTooth.setChecked(false);
                            break;
                    }
                    break;
                //蓝牙设备已连接
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.e(TAG, "onReceive---------蓝牙设备已连接");
                    Toast.makeText(context,"蓝牙设备已连接", Toast.LENGTH_SHORT).show();
                    break;
                //蓝牙设备断开连接
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.e(TAG, "onReceive---------蓝牙设备已断开");
                    Toast.makeText(context,"蓝牙设备已断开",Toast.LENGTH_SHORT).show();
                    //开启手机振动
                    VibrateUtil.vibrate(BaseApplication.getGlobalContext(), new long[]{100, 200, 100, 200}, 0);
                    if(sharedPreferences.getBoolean("isLocked", false)) {
                        PowerManager powerManager = (PowerManager) BaseApplication.getGlobalContext().getSystemService(Context.POWER_SERVICE);
                        Log.e(TAG, "onReceive: ---------点亮屏幕");
                        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
                        //点亮屏幕
                        wakeLock.acquire();

                    } else {
                        Log.e(TAG, "onReceive: ---------无需点亮屏幕，直接跳转");
                        Intent screenLockedintent = new Intent(context, ScreenLockedActivity.class);
                        context.startActivity(screenLockedintent);
                    }
                    break;
            }
        }
    }
}
