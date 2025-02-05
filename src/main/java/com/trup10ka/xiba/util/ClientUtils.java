package com.trup10ka.xiba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

public class ClientUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);

    public static void handleClientDisconnect(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers, AsynchronousSocketChannel client, String reason)
    {
        logger.info("Client disconnected: {}", client);
        clientBuffers.remove(client);
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

    public static void handleClientDisconnect(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers, AsynchronousSocketChannel client)
    {
        handleClientDisconnect(clientBuffers, client, "Client disconnected");
    }

    public static boolean processClientData(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers, AsynchronousSocketChannel client, String receivedData)
    {
        StringBuilder clientBuffer = clientBuffers.computeIfAbsent(client, k -> new StringBuilder());

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
