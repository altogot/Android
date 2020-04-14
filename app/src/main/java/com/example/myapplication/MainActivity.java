package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.example.BroadcastReveiver.BluetoothListenerReceiver;
import com.example.Utils.VibrateUtil;

import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothListenerReceiver bluetoothListenerReceiver;

    private boolean isVirating = true;

    private void StateON() {
        if(!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //请求开启蓝牙设备
            startActivity(intent);
        }
        //获得已配对的远程蓝牙设备的集合
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        if(devices.size() > 0) {
            for(Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext();) {
                BluetoothDevice device = it.next();
                //Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void StateOFF() {
        if(adapter.isEnabled()) {
            //关闭蓝牙设备
            adapter.disable();
        }
    }

    private IntentFilter BluetoothStateFilter() {
        IntentFilter intentFilter = new IntentFilter();
        // 监听蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //监听蓝牙设备与手机连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothListenerReceiver = new BluetoothListenerReceiver();
        final Context context = this;
        //注册广播
        this.registerReceiver(bluetoothListenerReceiver, BluetoothStateFilter());
        //蓝牙按钮
        CompoundButton openBlueTooth = (CompoundButton) findViewById(R.id.openBlueTooth);
        if(adapter.getState() == BluetoothAdapter.STATE_ON) {
            openBlueTooth.setChecked(true);
            StateON();
        } else {
            openBlueTooth.setChecked(false);
        }
        openBlueTooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    StateON();
                } else {
                    StateOFF();
                }
            }
        });
        //停止振动按钮
        Button stopVibrateButton = (Button) findViewById(R.id.stopVibrate);
        stopVibrateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VibrateUtil.virateCancle(context);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(bluetoothListenerReceiver);
    }
}
