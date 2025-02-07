package com.trup10ka.xiba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtil
{
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    public static List<String> getAllIPsInSubnet(String cidr) throws UnknownHostException
    {
        List<String> ipList = new ArrayList<>();

        String[] parts = cidr.split("/");
        if (parts.length != 2)
        {
            throw new IllegalArgumentException("Invalid CIDR format: " + cidr);
        }

        InetAddress baseAddress = InetAddress.getByName(parts[0]);
        int prefix = Integer.parseInt(parts[1]);

        byte[] ipBytes = baseAddress.getAddress();
        BigInteger baseIp = new BigInteger(1, ipBytes);
        int totalHosts = 1 << (32 - prefix);

        BigInteger networkAddress = baseIp.and(BigInteger.valueOf(0xFFFFFFFFL).shiftLeft(32 - prefix));
        BigInteger broadcastAddress = networkAddress.add(BigInteger.valueOf(totalHosts - 1));

        for (BigInteger currentIp = networkAddress.add(BigInteger.ONE);
             currentIp.compareTo(broadcastAddress) < 0; // Stop before broadcast address
             currentIp = currentIp.add(BigInteger.ONE))
        {

            String ip = convertBigIntegerToIp(currentIp, ipBytes.length);
            if (ip != null)
            {
                ipList.add(ip);
            }
        }

        return ipList;
    }

    public static String convertBigIntegerToIp(BigInteger ip, int length)
    {
        byte[] bytes = ip.toByteArray();
        byte[] ipBytes = new byte[length];

        int srcPos = bytes.length > length ? bytes.length - length : 0;
        int destPos = length - (bytes.length - srcPos);
        System.arraycopy(bytes, srcPos, ipBytes, destPos, bytes.length - srcPos);

        try
        {
            return InetAddress.getByAddress(ipBytes).getHostAddress();
        }
        catch (UnknownHostException e)
        {
            return null;
        }
    }
}
