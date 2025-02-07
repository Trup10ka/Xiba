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
    private static XibaTimeoutDaemon instance;

    private static final Logger logger = LoggerFactory.getLogger(XibaTimeoutDaemon.class);

    private final XibaConfig config;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final Map<AsynchronousSocketChannel, ScheduledFuture<?>> clientConnectionHandlers;

    public XibaTimeoutDaemon(XibaConfig config, Map<AsynchronousSocketChannel, ScheduledFuture<?>> clientConnectionHandlers)
    {
        instance = this;
        this.config = config;
        this.clientConnectionHandlers = clientConnectionHandlers;
        initAlreadyClosedClientGuard();
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

    private void initAlreadyClosedClientGuard()
    {
        scheduler.scheduleAtFixedRate(() -> clientConnectionHandlers.forEach((client, _) ->
        {
            if (client.isOpen())
                return;

            logger.warn("Client {} was closed without proper disconnect", client);
            removeTimeout(client);
        }), 0, 700, TimeUnit.MILLISECONDS);
    }

    public static XibaTimeoutDaemon getInstance()
    {
        return instance;
    }
}
