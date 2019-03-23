package com.zmp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Created by zmp on 2017/10/19.
 */

public class IPAndMacUtils {

        // 获取本机的MAC地址
        @SuppressLint("MissingPermission")
        public String getLocalMacAddress(Context context) {
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                String macAddress = info.getMacAddress();
                String mac = macAddress.replace(":", "");
                return mac;

        }

        // 获取本机的MAC地址
        public static String getLocalMacAddressFromIp(Context context) {
                String mac_s = "";
                try {
                        byte[] mac;
                        NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
                        mac = ne.getHardwareAddress();
                        mac_s = byte2hex(mac);
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
                return mac_s;
        }

        public static String byte2hex(byte[] b) {
                StringBuffer hs = new StringBuffer(b.length);
                String temp = "";
                int len = b.length;
                for (int n = 0; n < len; n++) {
                        temp = Integer.toHexString(b[n] & 0xFF);
                        if (temp.length() == 1)
                                hs = hs.append("0").append(temp);
                        else {
                                hs = hs.append(temp);
                        }
                }
                return String.valueOf(hs);
        }

        public static String getLocalIpAddress() {
                try {
                        List<NetworkInterface> niList = Collections.list(NetworkInterface.getNetworkInterfaces());
                        for (NetworkInterface ni : niList) {
                                List<InetAddress> iaList = Collections.list(ni.getInetAddresses());
                                for (InetAddress address : iaList) {
                                        if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                                                String hostAddress = address.getHostAddress();
                                                Log.d("ipv4", hostAddress);
                                                if (!"192.168.12.1".equals(hostAddress)) {
                                                        return hostAddress;
                                                }
                                        }
                                }
                        }
                }
                catch (SocketException ex) {
                        ex.printStackTrace();
                }
                return null;
        }
}
