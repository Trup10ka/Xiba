package com.trup10ka.xiba.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;
import static com.trup10ka.xiba.util.ClientUtils.processClientData;

public class ClientInputReaderHandler implements CompletionHandler<Integer, AsynchronousSocketChannel>
{
    private static final Logger logger = LoggerFactory.getLogger(ClientInputReaderHandler.class);

    private final Map<AsynchronousSocketChannel, StringBuilder> clientBuffers = Collections.synchronizedMap(new HashMap<>());;

    private final ByteBuffer clientBuffer;

    private final ClientCommandHandler clientCommandHandler;

    public ClientInputReaderHandler(AsynchronousSocketChannel client, ByteBuffer clientBuffer)
    {
        this.clientBuffer = clientBuffer;
        this.clientCommandHandler = new ClientCommandHandler( this);

        clientBuffers.put(client, new StringBuilder());
    }

    @Override
    public void completed(Integer bytesRead, AsynchronousSocketChannel client)
    {
        if (bytesRead == -1)
        {
            handleClientDisconnect(clientBuffers, client);
            return;
        }

        clientBuffer.flip();
        String receivedData = StandardCharsets.US_ASCII.decode(clientBuffer).toString();
        clientBuffer.clear();

        if (processClientData(clientBuffers, client, receivedData))
        {
            String command = clientBuffers.get(client).toString().trim();
            clientBuffers.get(client).setLength(0);
            handleCommand(client, command);
        }
        else
            client.read(clientBuffer, client, this);
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel client)
    {
        logger.error("Failed to read from client: {}", exc.getMessage());
        handleClientDisconnect(clientBuffers, client, exc.getMessage());
    }

    private void handleCommand(AsynchronousSocketChannel client, String command)
    {
        logger.info("Received input: {} from client: {}", command, client);

        if (command.isBlank())
        {
            client.read(clientBuffer, client, this);
            return;
        }

        //ByteBuffer responseBuffer = ByteBuffer.wrap(("You said: " + command + "\n").getBytes(StandardCharsets.UTF_8));
        //client.write(responseBuffer, client, new ClientCommandHandler(clientBuffers));
        clientCommandHandler.executeCommand(command, client);
    }

    public ByteBuffer getClientBuffer()
    {
        return clientBuffer;
    }
}
