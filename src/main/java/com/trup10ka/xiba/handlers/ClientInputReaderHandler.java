package com.trup10ka.xiba.handlers;

import com.trup10ka.xiba.XibaTimeoutDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;
import static com.trup10ka.xiba.util.ClientUtils.processClientData;

public class ClientInputReaderHandler extends ClientHandler<Integer, AsynchronousSocketChannel>
{
    private static final Logger logger = LoggerFactory.getLogger(ClientInputReaderHandler.class);

    private final AsynchronousSocketChannel client;

    private final StringBuilder clientStringBuffer = new StringBuilder();

    private final ByteBuffer clientBuffer;

    private final ClientCommandHandler clientCommandHandler;

    public ClientInputReaderHandler(AsynchronousSocketChannel client, ByteBuffer clientBuffer, XibaTimeoutDaemon timeoutDaemon)
    {
        super(timeoutDaemon);
        this.client = client;
        this.clientBuffer = clientBuffer;
        this.clientCommandHandler = new ClientCommandHandler( this, timeoutDaemon);
    }

    @Override
    public void completed(Integer bytesRead, AsynchronousSocketChannel client)
    {
        getTimeoutDaemon().resetTimeout(client);
        if (bytesRead == -1)
        {
            handleClientDisconnect(client);
            return;
        }
        logger.info("Read {} bytes from client: {}", bytesRead, client);
        clientBuffer.flip();
        String receivedData = StandardCharsets.US_ASCII.decode(clientBuffer).toString();
        clientBuffer.clear();

        if (processClientData(clientStringBuffer, client, receivedData))
        {
            String command = clientStringBuffer.toString().trim();
            clientStringBuffer.setLength(0);
            handleCommand(client, command);
        }
        else
            client.read(clientBuffer, client, this);
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel client)
    {
        logger.error("Failed to read from client: {}", exc.getMessage());
        handleClientDisconnect(client, exc.getMessage());
    }

    private void handleCommand(AsynchronousSocketChannel client, String command)
    {
        logger.info("Received input: {} from client: {}", command, client);

        if (command.isBlank())
        {
            client.read(clientBuffer, client, this);
            return;
        }
        clientCommandHandler.executeCommand(command, client);
    }

    public ByteBuffer getClientBuffer()
    {
        return clientBuffer;
    }
}
