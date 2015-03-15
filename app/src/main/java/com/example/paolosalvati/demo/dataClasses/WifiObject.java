package com.example.paolosalvati.demo.dataClasses;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Paolo on 14/03/2015.
 */
public class WifiObject {

    //Recover BSSID (MAC ADDRES of Device) and tore yhem to User Properties
    private WifiManager mainWifiObj;
    private String wifiBSSID;
    private String wifiMAC;
    private String wifiSSID;
    private List<ScanResult> wifiScanList;

    //Costructor
    public  WifiObject(Context context) {
        this.mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.wifiBSSID =mainWifiObj.getConnectionInfo().getBSSID();
        this.wifiMAC   =mainWifiObj.getConnectionInfo().getMacAddress();
        this.wifiSSID  =mainWifiObj.getConnectionInfo().getSSID();
        this.wifiScanList= mainWifiObj.getScanResults();
    }

    //Getter e Setter


    public String getWifiBSSID() {
        return wifiBSSID;
    }

    public String getWifiMAC() {
        return wifiMAC;
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public List<ScanResult> getWifiScanList() {
        return wifiScanList;
    }
}
