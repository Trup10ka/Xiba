package com.trup10ka.xiba;

import com.trup10ka.xiba.handler.ClientConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.trup10ka.xiba.util.ConsoleColor.RED;
import static com.trup10ka.xiba.util.ConsoleUtil.println;


public class XibaServer
{
    private static Logger logger = LoggerFactory.getLogger(XibaServer.class);

    private final AsynchronousServerSocketChannel serverSocketChannel;

    private final Collection<AsynchronousSocketChannel> clientConnectionHandlers = Collections.synchronizedCollection(new ArrayList<>());

    public XibaServer(AsynchronousServerSocketChannel serverSocketChannel)
    {
        this.serverSocketChannel = serverSocketChannel;
    }

    public void start()
    {
        serverSocketChannel.accept(serverSocketChannel, new ClientConnectionHandler());
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
        clientConnectionHandlers.forEach(socket -> {
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
