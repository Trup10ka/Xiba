package com.trup10ka.xiba.handlers;

import com.trup10ka.xiba.XibaTimeoutDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.trup10ka.xiba.util.ClientUtils.*;

public class ClientConnectionHandler extends ClientHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
    private final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);

    public ClientConnectionHandler(XibaTimeoutDaemon timeoutDaemon)
    {
        super(timeoutDaemon);
    }

    @Override
    public void completed(AsynchronousSocketChannel client, AsynchronousServerSocketChannel server)
    {
        logger.info("Accepted client connection from: {}", client);

        server.accept(server, this);

        getTimeoutDaemon().scheduleTimeout(client);
        readFromClient(client);
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment)
    {
        if (attachment.isOpen())
        {
            logger.error("Failed to accept client connection, reason: {}", exc.getMessage());
        }
        else
        {
            logger.info("Server socket channel closed, no longer accepting client connections");
        }
    }

    private void readFromClient(AsynchronousSocketChannel client)
    {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        client.read(buffer, client, new ClientInputReaderHandler(client, buffer, getTimeoutDaemon()));
    }
}
