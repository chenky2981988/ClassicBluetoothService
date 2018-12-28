package com.yazaki.btservice.yazakiandroidbtservice.base;

import android.bluetooth.BluetoothDevice;

public interface BluetoothListener extends BaseListener {
    /**
     * Callback when remote device send data to current device.
     * @param device, the connected device
     * @param data, the bytes to read
     */
    void onReadData(BluetoothDevice device, byte[] data);
}
