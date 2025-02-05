package com.trup10ka.xiba.handlers;

import com.trup10ka.xiba.XibaTimeoutDaemon;

import java.nio.channels.CompletionHandler;

public abstract class ClientHandler<V, E> implements CompletionHandler<V, E>
{
    private final XibaTimeoutDaemon timeoutDaemon;

    public ClientHandler(XibaTimeoutDaemon timeoutDaemon)
    {
        this.timeoutDaemon = timeoutDaemon;
    }

    protected XibaTimeoutDaemon getTimeoutDaemon()
    {
        return timeoutDaemon;
    }
}
