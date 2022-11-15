package com.example.netcap;
import java.io.IOException;
import java.util.Arrays;

import jpcap.*;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

/**
 * @author HY
 */
public class NetworkInterfaceDevice {

    NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    public NetworkInterface[] getNetworkInterfaceDevice(){

        for(NetworkInterface n : devices)
        {
            System.out.println(n.name + "     |     " + n.description+"\t|"+n.datalink_name+"\t|"+n.datalink_description+"\t|"+ Arrays.toString(n.addresses)+"\t|"+n.loopback+"\t|"+ Arrays.toString(n.mac_address));
        }
        System.out.println("-------------------------------------------");
        return devices;
    }

    public NetworkInterface getNetDevice(int device){
        return devices[device];
    }
}
