package com.trup10ka.xiba;

import com.trup10ka.xiba.config.XibaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;

public class XibaTimeoutDaemon
{
    private static final Logger logger = LoggerFactory.getLogger(XibaTimeoutDaemon.class);

    private final XibaConfig config;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final Map<AsynchronousSocketChannel, ScheduledFuture<?>> clientConnectionHandlers;

    public XibaTimeoutDaemon(XibaConfig config, Map<AsynchronousSocketChannel, ScheduledFuture<?>> clientConnectionHandlers)
    {
        this.config = config;
        this.clientConnectionHandlers = clientConnectionHandlers;
    }

    public void scheduleTimeout(AsynchronousSocketChannel client)
    {
        clientConnectionHandlers.put(client, scheduler.schedule(
                () -> {
                    logger.warn("Client {} timed out", client);
                    handleClientDisconnect(client, "Client timed out");
                    client.write(ByteBuffer.wrap("ER Timeout\r\n".getBytes()), client, null);
                }, config.timeouts().clientTimeout(), TimeUnit.MILLISECONDS)
        );
    }

    public void resetTimeout(AsynchronousSocketChannel clientChannel)
    {
        ScheduledFuture<?> previousTask = clientConnectionHandlers.remove(clientChannel);
        logger.info("Client interaction detected, resetting timeout for client: {}", clientChannel);
        if (previousTask != null)
        {
            previousTask.cancel(false);
        }
        scheduleTimeout(clientChannel);
    }

    public void removeTimeout(AsynchronousSocketChannel clientChannel)
    {
        ScheduledFuture<?> previousTask = clientConnectionHandlers.remove(clientChannel);
        if (previousTask != null)
        {
            previousTask.cancel(false);
        }
    }
}
