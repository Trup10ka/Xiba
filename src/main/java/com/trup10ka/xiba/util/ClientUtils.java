package com.trup10ka.xiba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;

public class ClientUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);

    public static final int BUFFER_SIZE = 1024;

    public static final String PROXY_SIGN = "PR";

    public static void sendErrorMessageToClient(AsynchronousSocketChannel client, String message, CompletionHandler<Integer, AsynchronousSocketChannel> handler)
    {
        try
        {
            client.write(ByteBuffer.wrap(("ER " + message).getBytes()), client, handler);
        }
        catch (Exception e)
        {
            logger.error("Failed to send error message to client {}: {}", e.getMessage(), client);
        }
    }

    public void sendErrorMessageToClient(AsynchronousSocketChannel client, CompletionHandler<Integer, AsynchronousSocketChannel> handler)
    {
        sendErrorMessageToClient(client, "Unknown error occurred on the server", handler);
    }

    public static void handleClientDisconnect(AsynchronousSocketChannel client, String reason)
    {
        if (!client.isOpen())
            return;
        logger.info("Client disconnected: {}, reason: {}", client, reason);
        informClientAboutDisconnect(client, reason);
        try
        {
            client.close();
        }
        catch (Exception e)
        {
            logger.error("Error closing client socket: {}", e.getMessage());
        }
    }

    public static void handleClientDisconnect(AsynchronousSocketChannel client)
    {
        handleClientDisconnect(client, "Client disconnected");
    }

    public static boolean processClientData(StringBuilder clientBuffer, AsynchronousSocketChannel client, String receivedData)
    {
        clientBuffer.append(receivedData);

        int newlineIndex = clientBuffer.indexOf("\n");
        return newlineIndex != -1;
    }

    private static void informClientAboutDisconnect(AsynchronousSocketChannel client, String reason)
    {
        try
        {
            client.write(ByteBuffer.wrap(("ER "  + reason).getBytes()));
        }
        catch (Exception e)
        {
            logger.error("Failed to inform client about disconnect, reason: {}", e.getMessage());
        }
    }
}
