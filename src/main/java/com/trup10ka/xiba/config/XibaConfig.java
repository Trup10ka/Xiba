package com.trup10ka.xiba.config;


import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public record XibaConfig(
        @NotNull String hostAddress,
        int port,
        Timeouts timeouts,
        Ranges ranges,
        BankRobbery bankRobbery
)
{

    public record Timeouts(int clientTimeout, int proxyClientTimeout)
    {
    }

    public record Ranges(int minAccountNumber, int maxAccountNumber, int minPort, int maxPort)
    {
    }

    public record BankRobbery(String bankRobberySubnetMask, int maxPoolSize, int commandTimeout)
    {
    }

    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(hostAddress, port);
    }
}
