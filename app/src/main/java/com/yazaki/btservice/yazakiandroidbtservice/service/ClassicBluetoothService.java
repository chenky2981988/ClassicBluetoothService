package com.yazaki.btservice.yazakiandroidbtservice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.yazaki.btservice.yazakiandroidbtservice.R;
import com.yazaki.btservice.yazakiandroidbtservice.base.BluetoothListener;
import com.yazaki.btservice.yazakiandroidbtservice.controller.BluetoothController;

import java.util.UUID;

public class ClassicBluetoothService extends Service implements BluetoothListener {

    private BluetoothController mBluetoothController;
    private static int FOREGROUND_SERVICE_NOTIFICATION_ID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG","Classic Bluetooth Service : onStartCommand()");
        startForegroundService();
        initBT();
        turnOnBT();
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG","Classic Bluetooth Service : onCreate()");
    }

    private void initBT() {
        mBluetoothController = BluetoothController.getInstance().build(this);
        mBluetoothController.setAppUuid(UUID.fromString("fa87c0d0-afac-12de-8a39-0450200c9a66"));
        mBluetoothController.setBluetoothListener(this);
    }

    private void turnOnBT() {
        if (!mBluetoothController.isEnabled()) {
            mBluetoothController.openBluetooth();
        } else {
            Toast.makeText(this, "Bluetooth has opened!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Android API 8.0 Foreground Service support
    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "yazaki_bluetooth_service";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.foreground_service_channel), NotificationManager.IMPORTANCE_NONE);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);

            Notification foregroundNotification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("").build();
            startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, foregroundNotification);
        }
    }


    @Override
    public void onReadData(BluetoothDevice device, byte[] data) {

    }

    @Override
    public void onActionStateChanged(int preState, int state) {

    }

    @Override
    public void onActionDiscoveryStateChanged(String discoveryState) {

    }

    @Override
    public void onActionScanModeChanged(int preScanMode, int scanMode) {

    }

    @Override
    public void onBluetoothServiceStateChanged(int state) {

    }

    @Override
    public void onActionDeviceFound(BluetoothDevice device, short rssi) {

    }
}
