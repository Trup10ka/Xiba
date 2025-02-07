package com.trup10ka.xiba.data;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public record BankData(InetSocketAddress address, int numberOfClients, BigInteger bankTotal)
{
    @Override
    public String toString()
    {
        return "Bank: " + address + " | Clients: " + numberOfClients + " | Total: " + bankTotal;
    }
}
