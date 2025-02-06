package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaServer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import static com.trup10ka.xiba.util.ClientUtils.BUFFER_SIZE;


public class ProxyClientCommandCallHandler implements CompletionHandler<Integer, String>
{
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientCommandCallHandler.class);

    private final AsynchronousSocketChannel proxyChannel;

    private final AsynchronousSocketChannel client;

    public ProxyClientCommandCallHandler(AsynchronousSocketChannel client, AsynchronousSocketChannel proxyChannel)
    {
        this.client = client;
        this.proxyChannel = proxyChannel;
    }

    @Override
    public void completed(Integer result, String attachment)
    {
        if (result == -1)
        {
            logger.info("Server closed connection");
            return;
        }

        logger.info("Forwarded command to server of client {}: {}", proxyChannel, attachment);
        receiveData();
    }

    @Override
    public void failed(Throwable exc, String attachment)
    {
        logger.error("Failed to send message: {}", exc.getMessage());
    }

    private void receiveData()
    {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        logger.warn("Reading data from server of client {}", proxyChannel);
        proxyChannel.read(buffer, XibaServer.getConfig().timeouts().clientTimeout(), TimeUnit.MILLISECONDS, buffer, new ProxyClientCommandResultHandler(client, proxyChannel));
    }
}
