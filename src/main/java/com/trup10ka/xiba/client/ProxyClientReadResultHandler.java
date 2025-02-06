package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaTimeoutDaemon;
import com.trup10ka.xiba.handlers.ClientHandler;
import com.trup10ka.xiba.handlers.ClientInputReaderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import static com.trup10ka.xiba.util.ClientUtils.BUFFER_SIZE;
import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;

public class ProxyClientReadResultHandler extends ClientHandler<Integer, AsynchronousSocketChannel>
{

    private final static Logger logger = LoggerFactory.getLogger(ProxyClientReadResultHandler.class);

    private final AsynchronousSocketChannel client;

    public ProxyClientReadResultHandler(AsynchronousSocketChannel client)
    {
        super(XibaTimeoutDaemon.getInstance());
        this.client = client;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachment)
    {
        logger.info("Successfully sent message to client: {}", attachment);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        client.read(buffer, client, new ClientInputReaderHandler(client, buffer, getTimeoutDaemon()));
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment)
    {
        logger.error("Failed to accept client connection, reason: {}", exc.getMessage());
        handleClientDisconnect(client);
    }
}
