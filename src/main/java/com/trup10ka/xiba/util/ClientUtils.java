package com.trup10ka.xiba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

public class ClientUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);

    public static void handleClientDisconnect(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers, AsynchronousSocketChannel client)
    {
        logger.info("Client disconnected: {}", client);
        clientBuffers.remove(client);
        try
        {
            client.close();
        }
        catch (Exception e)
        {
            logger.error("Error closing client socket: {}", e.getMessage());
        }
    }

    public static boolean processClientData(Map<AsynchronousSocketChannel, StringBuilder> clientBuffers, AsynchronousSocketChannel client, String receivedData)
    {
        StringBuilder clientBuffer = clientBuffers.computeIfAbsent(client, k -> new StringBuilder());

        clientBuffer.append(receivedData);

        int newlineIndex = clientBuffer.indexOf("\n");
        return newlineIndex != -1;
    }
}
