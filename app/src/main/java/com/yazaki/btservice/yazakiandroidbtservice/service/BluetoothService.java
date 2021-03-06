package com.yazaki.btservice.yazakiandroidbtservice.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.yazaki.btservice.yazakiandroidbtservice.base.BaseListener;
import com.yazaki.btservice.yazakiandroidbtservice.base.BluetoothListener;
import com.yazaki.btservice.yazakiandroidbtservice.base.BluetoothState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothService {

    private static final String TAG = "LMBluetoothSdk";

    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BaseListener mBluetoothListener;
    private BluetoothDevice mBluetoothDevice;

    private int mState;

    // Hint: If you are connecting to a Bluetooth serial board then try
    // using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB.
    private UUID mAppUuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public BluetoothService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BluetoothState.STATE_NONE;
    }

    /**
     * Set bluetooth listener.
     * @param listener BaseListener
     */
    public synchronized void setBluetoothListener(BaseListener listener) {
        this.mBluetoothListener = listener;
    }

    /**
     * Get current SDP recorded UUID.
     * @return an UUID
     */
    public UUID getAppUuid() {
        return mAppUuid;
    }

    /**
     * Set a UUID for SDP record.
     * @param uuid an UUID
     */
    public void setAppUuid(UUID uuid) {
        this.mAppUuid = uuid;
    }

    /**
     * Set the current state of the connection.
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        if (mBluetoothListener != null){
            mBluetoothListener.onBluetoothServiceStateChanged(state);
        }
    }

    /**
     * Get the current state of connection.
     * Possible return values are STATE_NONE, STATE_LISTEN, STATE_CONNECTING, STATE_CONNECTED,
     * STATE_DISCONNECTED, STATE_UNKNOWN in {@link BluetoothState} class.
     * @return the connection state
     */
    public int getState() {
        return mState;
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode.
     */
    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(BluetoothState.STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mState == BluetoothState.STATE_CONNECTING && mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(BluetoothState.STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection.
     * @param socket The BluetoothSocket on which the connection was made
     */
    public synchronized void connected(BluetoothSocket socket) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        Log.d("Yazaki","Connected");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        mBluetoothDevice = socket.getRemoteDevice();
        setState(BluetoothState.STATE_CONNECTED);
    }

    /**
     * Stop all threads.
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mBluetoothDevice = null;
        setState(BluetoothState.STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner.
     * @param out The bytes to write
     */
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != BluetoothState.STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(out, 0, out.length);
    }

    /**
     * Write a file as bytes to the ConnectedThread in an unsynchronized manner.
     * @param path the file path
     * @param fileName the file name
     */
    /*public void write(final String path, final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectedThread r;
                synchronized (this) {
                    if (mState != State.STATE_CONNECTED) {
                        return;
                    }
                    r = mConnectedThread;
                }
                try {
                    FileInputStream in = new FileInputStream(path + "/" + fileName);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1){
                        r.write(buffer, 0, len);
                    }
                    in.close();
                }catch (IOException e){}
            }
        }).start();
    }*/

    /**
     * Get connected device.
     * @return a bluetooth device
     */
    public BluetoothDevice getConnectedDevice(){
        return mBluetoothDevice;
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(TAG, mAppUuid);
            } catch (IOException e) {}
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (mState != BluetoothState.STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case BluetoothState.STATE_LISTEN:
                            case BluetoothState.STATE_CONNECTING:
                            case BluetoothState.STATE_DISCONNECTED:
                                connected(socket);
                                break;
                            case BluetoothState.STATE_NONE:
                            case BluetoothState.STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {}
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(mAppUuid);
            } catch (IOException e) {}
            mmSocket = tmp;
        }

        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                setState(BluetoothState.STATE_LISTEN);
                try {
                    mmSocket.close();
                } catch (IOException e2) {}
                BluetoothService.this.start();
                return;
            }
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            Log.d("Yazaki","Ready to receive Data");
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if (bytes > 0) {
                        byte[] data = Arrays.copyOf(buffer, bytes);
                        if (mBluetoothListener != null) {
                            ((BluetoothListener) mBluetoothListener).onReadData(mmSocket.getRemoteDevice(), data);
                        }
                    }
                } catch (IOException e) {
                    setState(BluetoothState.STATE_DISCONNECTED);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer, int start, int end) {
            try {
                mmOutStream.write(buffer, start, end);
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }
}
