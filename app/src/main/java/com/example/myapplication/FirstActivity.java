package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "FirstActivity";

    private  static BlueToothStateReceiver blueToothStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        blueToothStateReceiver = new BlueToothStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        registerReceiver(blueToothStateReceiver, intentFilter);
        Log.d(TAG, "onCreate: 111");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blueToothStateReceiver);
    }

    class BlueToothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, BluetoothAdapter.STATE_CONNECTED + "", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            for(String key: bundle.keySet()) {
                Log.d(key, "---" + bundle.get(key));
            }
        }
    }
}
