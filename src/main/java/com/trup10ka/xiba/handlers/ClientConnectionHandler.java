package com.trup10ka.xiba.handlers;

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

public class ClientConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
    private final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel client, AsynchronousServerSocketChannel server)
    {
        logger.info("Accepted client connection from: {}", client);

        server.accept(server, this);

        readFromClient(client);
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment)
    {
        logger.error("Failed to accept client connection, reason: {}", exc.getMessage());
    }

    private void readFromClient(AsynchronousSocketChannel client)
    {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        client.read(buffer, client, new ClientInputReaderHandler(client, buffer));
    }
}
