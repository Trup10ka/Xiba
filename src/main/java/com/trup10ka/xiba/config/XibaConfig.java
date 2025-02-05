package com.trup10ka.xiba.config;


import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public record XibaConfig(
        @NotNull String hostAddress,
        int port,
        Timeouts timeouts,
        Ranges ranges
)
{

    public record Timeouts(int clientTimeout, int sessionTimeout)
    {
    }

    public record Ranges(int minAccountNumber, int maxAccountNumber, int minPort, int maxPort)
    {
    }

    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(hostAddress, port);
    }
}
