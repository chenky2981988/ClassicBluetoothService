package com.yazaki.btservice.yazakiandroidbtservice.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yazaki.btservice.yazakiandroidbtservice.service.ClassicBluetoothService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bluetoothServiceIntent = new Intent(this, ClassicBluetoothService.class);
        //Android 8.0  API support
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(bluetoothServiceIntent);
        } else {
            startService(bluetoothServiceIntent);
        }
        finish();
    }
}
