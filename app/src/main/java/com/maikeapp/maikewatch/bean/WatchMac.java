package com.maikeapp.maikewatch.bean;

/**
 * Created by JLJ on 2016/5/9.
 */
public class WatchMac {
    private int rssi;
    private String mac;

    public WatchMac(int rssi, String mac) {
        this.rssi = rssi;
        this.mac = mac;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
