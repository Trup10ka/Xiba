package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.trup10ka.xiba.util.ClientUtils.handleClientDisconnect;
import static com.trup10ka.xiba.util.ClientUtils.processClientData;

public class ProxyClientCommandResultHandler implements CompletionHandler<Integer, ByteBuffer>
{
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientCommandResultHandler.class);

    private final StringBuilder clientStringBuffer;

    private final AsynchronousSocketChannel proxyClient;

    private final AsynchronousSocketChannel client;

    public ProxyClientCommandResultHandler(AsynchronousSocketChannel client, AsynchronousSocketChannel proxyClient)
    {
        this.client = client;
        this.proxyClient = proxyClient;
        this.clientStringBuffer = new StringBuilder();
    }

    @Override
    public void completed(Integer result, ByteBuffer clientBuffer)
    {
        if (result == -1)
        {
            logger.info("Server closed connection");
            return;
        }

        clientBuffer.flip();
        String receivedData = StandardCharsets.US_ASCII.decode(clientBuffer).toString();
        clientBuffer.clear();

        if (processClientData(clientStringBuffer, proxyClient, receivedData))
        {
            String command = clientStringBuffer.toString();
            clientStringBuffer.setLength(0);
            logger.info("Received response from server {}: {}", proxyClient, command);

            handleClientDisconnect(proxyClient, "Proxy client no longer in use, disconnecting");
            logger.info("Proxy client {} disconnected", proxyClient);
            client.write(ByteBuffer.wrap(command.trim().getBytes()), client, new ProxyClientReadResultHandler(client));
        }
        else
            proxyClient.read(clientBuffer, XibaServer.getConfig().timeouts().proxyClientTimeout(), TimeUnit.MILLISECONDS, clientBuffer, this);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment)
    {
        if (exc instanceof TimeoutException)
        {
            logger.error("Client {} timed out", proxyClient);
        }
        else
        {
            logger.error("Failed to read from client: {}", exc.getMessage());
        }
    }
}
