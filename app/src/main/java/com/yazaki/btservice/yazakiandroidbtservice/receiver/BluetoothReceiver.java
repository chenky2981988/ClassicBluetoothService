package com.yazaki.btservice.yazakiandroidbtservice.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yazaki.btservice.yazakiandroidbtservice.base.BaseListener;

public class BluetoothReceiver extends BroadcastReceiver {

    private BaseListener mBluetoothListener;

    public BluetoothReceiver(BaseListener listener){
        mBluetoothListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || mBluetoothListener == null){
            return;
        }
        String action = intent.getAction();
        switch (action){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                int preState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
                mBluetoothListener.onActionStateChanged(preState, state);
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                mBluetoothListener.onActionDiscoveryStateChanged(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                break;

            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                mBluetoothListener.onActionDeviceFound(device, rssi);
                break;

            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                int preScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0);
                mBluetoothListener.onActionScanModeChanged(preScanMode, scanMode);
                break;
        }
    }
}
