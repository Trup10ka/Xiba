package com.trup10ka.xiba.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;
import static com.trup10ka.xiba.util.ClientUtils.processClientData;

public class ClientConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
    private final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);

    private static final int BUFFER_SIZE = 1024;

    private final Map<AsynchronousSocketChannel, StringBuilder> clientBuffers = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void completed(AsynchronousSocketChannel client, AsynchronousServerSocketChannel server)
    {
        logger.info("Accepted client connection from: {}", client);

        // Accept more clients
        server.accept(server, this);

        // Initialize client buffer
        clientBuffers.put(client, new StringBuilder());

        // Start reading input from the client
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

        client.read(buffer, client, new CompletionHandler<Integer, AsynchronousSocketChannel>()
        {
            @Override
            public void completed(Integer bytesRead, AsynchronousSocketChannel client)
            {
                if (bytesRead == -1)
                {
                    handleClientDisconnect(clientBuffers,client);
                    return;
                }

                buffer.flip();
                String receivedData = StandardCharsets.UTF_8.decode(buffer).toString();
                buffer.clear();

                if (processClientData(clientBuffers, client, receivedData))
                {
                    String command = clientBuffers.get(client).toString().trim();
                    clientBuffers.get(client).setLength(0);
                    handleCommand(client, command);
                }
                else
                    readFromClient(client);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel client)
            {
                logger.error("Failed to read from client: {}", exc.getMessage());
                handleClientDisconnect(clientBuffers, client);
            }
        });
    }

    private void handleCommand(AsynchronousSocketChannel client, String command)
    {
        logger.info("Received command from client: {}", command);

        if ("exit".equalsIgnoreCase(command))
        {
            handleClientDisconnect(clientBuffers, client);
            return;
        }

        ByteBuffer responseBuffer = ByteBuffer.wrap(("You said: " + command + "\n").getBytes(StandardCharsets.UTF_8));
        client.write(responseBuffer, client, new CompletionHandler<Integer, AsynchronousSocketChannel>()
        {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel attachment)
            {
                handleClientDisconnect(clientBuffers, client);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment)
            {
                logger.error("Failed to write response to client: {}", exc.getMessage());
            }
        });
    }
}
