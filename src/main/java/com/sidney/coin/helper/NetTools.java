package com.sidney.coin.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetTools {

    public static InetAddress getLocalAddress(AddressSelectionCondition condition) {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) continue;
                    if (condition.isAcceptableAddress(addr)) {
                        return addr;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e.getMessage());
        }
        throw new RuntimeException("Can't get our ip address, interfaces are: " + interfaces);
    }

    @FunctionalInterface
    public interface AddressSelectionCondition {
        boolean isAcceptableAddress(InetAddress address);
    }

}
