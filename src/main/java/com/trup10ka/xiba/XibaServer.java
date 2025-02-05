package com.trup10ka.xiba;

import com.trup10ka.xiba.config.XibaConfig;
import com.trup10ka.xiba.handlers.ClientConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static com.trup10ka.xiba.util.ConsoleUtil.println;


public class XibaServer
{
    private static final Logger logger = LoggerFactory.getLogger(XibaServer.class);

    private final AsynchronousServerSocketChannel serverSocketChannel;

    private final Map<AsynchronousSocketChannel, ScheduledFuture<?>> clientConnectionHandlers = Collections.synchronizedMap(new HashMap<>());

    private final XibaTimeoutDaemon timeoutDaemon;

    public XibaServer(AsynchronousServerSocketChannel serverSocketChannel, XibaConfig config)
    {
        this.serverSocketChannel = serverSocketChannel;
        this.timeoutDaemon = new XibaTimeoutDaemon(config, clientConnectionHandlers);
    }

    public void start()
    {
        serverSocketChannel.accept(serverSocketChannel, new ClientConnectionHandler(timeoutDaemon));
    }

    public void stop()
    {
        try
        {
            closeAllSessions();
            serverSocketChannel.close();
            logger.info("Server socket channel closed, server effectively stopped");
        }
        catch (IOException e)
        {
            logger.error("Failed to close server socket channel, reason: {}", e.getMessage());
        }
    }

    private void closeAllSessions()
    {
        clientConnectionHandlers.forEach((socket, _) -> {
            try
            {
                logger.info("Closing client socket channel: {}", socket);
                socket.close();
            }
            catch (IOException e)
            {
                logger.error("Failed to close client socket channel, reason: {}", e.getMessage());
            }
        });

        logger.info("All client socket channels closed");
    }
}
