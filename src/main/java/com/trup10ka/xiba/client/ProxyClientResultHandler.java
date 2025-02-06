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

import static com.trup10ka.xiba.util.ClientUtils.processClientData;

public class ProxyClientResultHandler implements CompletionHandler<Integer, ByteBuffer>
{
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientResultHandler.class);

    private final StringBuilder clientStringBuffer;

    private final AsynchronousSocketChannel proxyClient;

    private final AsynchronousSocketChannel client;

    public ProxyClientResultHandler(AsynchronousSocketChannel client, AsynchronousSocketChannel proxyClient)
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
            String command = clientStringBuffer.toString().trim();
            clientStringBuffer.setLength(0);
            client.write(ByteBuffer.wrap(command.getBytes()), command, null);
        }
        else
            proxyClient.read(clientBuffer, XibaServer.getConfig().timeouts().clientTimeout(), TimeUnit.MILLISECONDS, clientBuffer, this);
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
