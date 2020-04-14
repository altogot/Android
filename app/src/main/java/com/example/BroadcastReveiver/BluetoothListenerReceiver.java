package com.example.BroadcastReveiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.Utils.VibrateUtil;

import com.example.myapplication.R;

public class BluetoothListenerReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothListenerReceiv";

    private DataCallBack dataCallBack;

    public void BluetoothReceive(DataCallBack callBack) {
        dataCallBack = callBack;
    }

    public interface DataCallBack {
        void onDataChanged(int buletoothState);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null) {
            switch (action) {
                //蓝牙开启关闭状态
                case BluetoothAdapter.ACTION_STATE_CHANGED:
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
                    VibrateUtil.vibrate(context, new long[]{100, 200, 100, 200}, 0);
                    break;
            }
        }
    }
}
