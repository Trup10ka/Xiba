package com.trup10ka.xiba.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;

public class ClientCommandHandler implements CompletionHandler<Integer, AsynchronousSocketChannel>
{
    private static final Logger logger = LoggerFactory.getLogger(ClientCommandHandler.class);

    private final Map<AsynchronousSocketChannel, StringBuilder> clientBuffers;

    public ClientCommandHandler(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers)
    {
        this.clientBuffers = clientBuffers;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel client)
    {
        handleClientDisconnect(clientBuffers, client);
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment)
    {
        logger.error("Failed to write response to client: {}", exc.getMessage());
    }
}
