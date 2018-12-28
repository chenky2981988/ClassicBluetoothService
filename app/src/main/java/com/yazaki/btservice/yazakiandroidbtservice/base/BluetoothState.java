package com.yazaki.btservice.yazakiandroidbtservice.base;

public class BluetoothState {

    /** we're doing nothing*/
    public static final int STATE_NONE = 0;

    /** now listening for incoming connections*/
    public static final int STATE_LISTEN = 1;

    /** now initiating an outgoing connection*/
    public static final int STATE_CONNECTING = 2;

    /** now connected to a remote device*/
    public static final int STATE_CONNECTED = 3;

    /** lost the connection*/
    public static final int STATE_DISCONNECTED = 4;

    /** unknown state*/
    public static final int STATE_UNKNOWN = 5;

    /** got all characteristics*/
    public static final int STATE_GOT_CHARACTERISTICS = 6;
}
