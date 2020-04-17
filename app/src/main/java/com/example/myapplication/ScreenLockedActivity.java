package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.Utils.ScreenListener;
import com.example.Utils.VibrateUtil;

public class ScreenLockedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScreenLockedActivity";

    private SharedPreferences sharedPreferences;

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
                Log.e(TAG, "onClick: ------调用一键求救功能");
                sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                String phoneNumber = sharedPreferences.getString("phone", "");
                if(!phoneNumber.equals("")) {
                    Log.e(TAG, "onClick: -----紧急联系人手机号:" + phoneNumber);
                    Uri data = Uri.parse("tel:" + phoneNumber);
                    intent.setData(data);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "onClick: -----未设置紧急联系人");
                    Toast.makeText(this, "请设置紧急联系人", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stopAlert://调用误触功能
                //关闭自定义振动
                VibrateUtil.virateCancle(BaseApplication.getGlobalContext());
                finish();
                break;
        }

    }


}
