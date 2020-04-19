package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.example.Utils.VibrateUtil;

import pub.devrel.easypermissions.EasyPermissions;

public class ScreenLockedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScreenLockedActivity";

    private SharedPreferences sharedPreferences;

    public static final String[] dualSimTypes = { "subscription", "Subscription",
            "com.android.phone.extra.slot",
            "phone", "com.android.phone.DialingMode",
            "simId", "simnum", "phone_type",
            "simSlot" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_screen_locked);
        //误触按钮
        Button stopAlert = findViewById(R.id.stopAlert);
        //一键求救按钮
        Button sos = findViewById(R.id.sos);
        sos.setOnClickListener(this);
        stopAlert.setOnClickListener(this);
        BaseApplication.addDestroyActivity(this, "ScreenLockedActivity");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sos://调用一键求救功能
                call();
                break;
            case R.id.stopAlert://调用误触功能
                //关闭自定义振动
                VibrateUtil.virateCancle(BaseApplication.getGlobalContext());
                finish();
                break;
        }

    }

    //拨打电话
    private void call() {
        if(!EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)) {
            EasyPermissions.requestPermissions(this, "获取电话权限", 1000, Manifest.permission.CALL_PHONE);
        }
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone", "");
        if(!phoneNumber.equals("")) {
            Log.e(TAG, "onClick: -----紧急联系人手机号:" + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            for (int i=0; i < dualSimTypes.length; i++) {
                callIntent.putExtra(dualSimTypes[i], 2);
            }
            this.startActivity(callIntent);
        } else {
            Log.e(TAG, "onClick: -----未设置紧急联系人");
            Toast.makeText(this, "请设置紧急联系人", Toast.LENGTH_SHORT).show();
        }
    }


}
