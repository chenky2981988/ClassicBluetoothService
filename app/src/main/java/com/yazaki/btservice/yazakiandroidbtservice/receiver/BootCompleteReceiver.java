package com.yazaki.btservice.yazakiandroidbtservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.yazaki.btservice.yazakiandroidbtservice.service.ClassicBluetoothService;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)){
           Intent bluetoothServiceIntent = new Intent(context, ClassicBluetoothService.class);
           //Android 8.0  API support
           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
               context.startForegroundService(bluetoothServiceIntent);
           }else {
               context.startService(bluetoothServiceIntent);
           }
       }
    }
}
