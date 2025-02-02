package com.trup10ka.xiba.config;


import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public record XibaConfig(
        @NotNull String hostAddress,
        int port
)
{
    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(hostAddress, port);
    }
}
