package com.yazaki.btservice.yazakiandroidbtservice.base;

import android.bluetooth.BluetoothDevice;

public interface BaseListener {

    /**
     * Callback when bluetooth power state changed.
     * @param preState previous power state
     * @param state current power state
     * Possible values are STATE_OFF, STATE_TURNING_ON,
     * STATE_ON, STATE_TURNING_OFF in {@link android.bluetooth.BluetoothAdapter} class.
     */
    void onActionStateChanged(int preState, int state);

    /**
     * Callback when local Bluetooth adapter discovery process state changed.
     * @param discoveryState the state of local Bluetooth adapter discovery process.
     * Possible values are ACTION_DISCOVERY_STARTED,
     * ACTION_DISCOVERY_FINISHED in {@link android.bluetooth.BluetoothAdapter} class.
     *
     */
    void onActionDiscoveryStateChanged(String discoveryState);

    /**
     * Callback when the current scan mode changed.
     * @param preScanMode previous scan mode
     * @param scanMode current scan mode
     * Possible values are SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE,
     * SCAN_MODE_CONNECTABLE_DISCOVERABLE in {@link android.bluetooth.BluetoothAdapter} class.
     */
    void onActionScanModeChanged(int preScanMode, int scanMode);

    /**
     * Callback when the connection state changed.
     * @param state connection state
     * Possible values are STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED,
     * STATE_DISCONNECTED and STATE_UNKNOWN in {@link State} class.
     */
    void onBluetoothServiceStateChanged(int state);

    /**
     * Callback when found device.
     * @param device a remote device
     * @param rssi the RSSI value of the remote device as reported by the Bluetooth hardware
     */
    void onActionDeviceFound(BluetoothDevice device, short rssi);
}
