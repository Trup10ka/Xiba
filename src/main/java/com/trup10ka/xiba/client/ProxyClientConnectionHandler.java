package com.trup10ka.xiba.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ProxyClientConnectionHandler implements CompletionHandler<Void, String>
{

    private static final Logger logger = LoggerFactory.getLogger(ProxyClientConnectionHandler.class);

    private final InetSocketAddress address;

    private final AsynchronousSocketChannel channel;

    private final AsynchronousSocketChannel client;

    public ProxyClientConnectionHandler(InetSocketAddress address, AsynchronousSocketChannel client, AsynchronousSocketChannel channel)
    {
        this.client = client;
        this.address = address;
        this.channel = channel;
    }

    @Override
    public void completed(Void result, String command)
    {
        logger.info("Connected to {}:{}", address.getHostString(), address.getPort());

        ByteBuffer buffer = ByteBuffer.wrap(command.getBytes());
        channel.write(buffer, command, new ProxyClientCommandCallHandler(client, channel));
    }

    @Override
    public void failed(Throwable exc, String attachment)
    {
        logger.error("Failed to connect to {}:{}", address.getHostString(), address.getPort());
    }
}
