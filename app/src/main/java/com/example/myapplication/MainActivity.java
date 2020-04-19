package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BroadcastReveiver.BluetoothListenerReceiver;

import com.example.Utils.ScreenListener;

import java.security.Permission;
import java.util.Iterator;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothListenerReceiver bluetoothListenerReceiver;//蓝牙状态广播

    private SharedPreferences sharedPreferences;//轻量级数据库

    private boolean isVirating = true;

    private ScreenListener screenListener;//屏幕监听器

    private static final int PERMISSIONS = 100;//请求码


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
        //获取权限
        getPermission();
        bluetoothListenerReceiver = new BluetoothListenerReceiver();
//        final Context context = this;
        //注册广播
        this.registerReceiver(bluetoothListenerReceiver, BluetoothStateFilter());
        //蓝牙按钮
        CompoundButton openBlueTooth = (CompoundButton) findViewById(R.id.openBlueTooth);
//        //开启屏幕监听服务
//        Intent intent = new Intent(this, ScreenLockedService.class);
//        startService(intent);
        init();

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
        //保存紧急联系人
        Button confirmPhoneButton = (Button) findViewById(R.id.confirmPhone);
        confirmPhoneButton.setOnClickListener(this);
        BaseApplication.addDestroyActivity(this, "MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent intent = new Intent(this, ScreenLockedService.class);
        //stopService(intent);
        this.unregisterReceiver(bluetoothListenerReceiver);
        screenListener.unregisterScreenLockedListener();
        Log.e(TAG, "onDestroy: ------退出app" );
    }

    void init() {
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);//初始化数据库
        final SharedPreferences.Editor editor = sharedPreferences.edit();//设置editor用于往数据库里增删数据
        editor.putBoolean("isLocked", false);
        editor.commit();
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onScreenOn() {
                Log.e(TAG, "onScreenOn: -----屏幕已点亮");
                if(sharedPreferences.getBoolean("isLocked", false)) {
                    Log.e(TAG, "onScreenOn: -----进入锁屏界面");
                    Intent intent = new Intent(MainActivity.this, ScreenLockedActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onScreenOff() {
                Log.e(TAG, "onScreenoff: -----屏幕已关闭");
                editor.putBoolean("isLocked", true);
                editor.commit();
                BaseApplication.destroyActivity("ScreenLockedActivity");
            }

            @Override
            public void onUserPresent() {
                Log.e(TAG, "onUserPresent: -----屏幕已解锁");
                editor.putBoolean("isLocked", false);
                editor.commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.confirmPhone:
                EditText editText = (EditText) findViewById(R.id.phoneNumber);
                String phoneNumber =  editText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(MainActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                    editText.requestFocus();
                } else if (phoneNumber.length() != 11) {
                    Toast.makeText(MainActivity.this, "电话号码位数不正确", Toast.LENGTH_SHORT).show();
                    editText.requestFocus();
                } else {
                    String num = "[1][358]\\d{9}";
                    if (phoneNumber.matches(num)) {
                        Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phoneNumber);
                        editor.commit();
                        editText.clearFocus();
                        Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //获取权限
    public void getPermission() {
        String bluetooth[] = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, };
        String[] perms = {Manifest.permission.VIBRATE, Manifest.permission.DISABLE_KEYGUARD, Manifest.permission.WAKE_LOCK, Manifest.permission.CALL_PHONE};
        if(!EasyPermissions.hasPermissions(this, bluetooth)) {
            EasyPermissions.requestPermissions(this, "获取蓝牙权限", PERMISSIONS, bluetooth);
        }
        if(!EasyPermissions.hasPermissions(this, perms[0])) {
            EasyPermissions.requestPermissions(this, "获取振动权限", PERMISSIONS, perms[0]);
        }
        if(!EasyPermissions.hasPermissions(this, perms[1])) {
            EasyPermissions.requestPermissions(this, "获取锁屏权限", PERMISSIONS, perms[1]);
        }
        if(!EasyPermissions.hasPermissions(this, perms[2])) {
            EasyPermissions.requestPermissions(this, "获取打开屏幕权限", PERMISSIONS, perms[2]);
        }
        if(!EasyPermissions.hasPermissions(this, perms[3])) {
            EasyPermissions.requestPermissions(this, "获取电话权限", PERMISSIONS, perms[3]);
        }
    }
}
