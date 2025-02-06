package com.trup10ka.xiba.client;

import com.trup10ka.xiba.config.XibaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

public class XibaClient
{
    private static final Logger logger = LoggerFactory.getLogger(XibaClient.class);

    private final XibaConfig config;

    private final AsynchronousSocketChannel proxyChannel;

    private final AsynchronousSocketChannel client;

    public XibaClient(XibaConfig config, AsynchronousSocketChannel client) throws IOException
    {
        this.config = config;
        this.client = client;
        this.proxyChannel = AsynchronousSocketChannel.open();
    }

    public int findBank(String targetIp)
    {
        logger.info("Finding bank on IP: {}", targetIp);
        for (int port = config.ranges().minPort(); port <= config.ranges().maxPort(); port++)
        {
            try (AsynchronousSocketChannel testChannel = AsynchronousSocketChannel.open())
            {
                testChannel.connect(new InetSocketAddress(targetIp, port)).get(config.timeouts().clientTimeout(), TimeUnit.MILLISECONDS);
                return port;
            }
            catch (Exception ignored)
            {
            }
        }
        return -1;
    }

    public void executeProxyCommand(String targetIp, int targetPort, String command)
    {
        InetSocketAddress address = new InetSocketAddress(targetIp, targetPort);

        proxyChannel.connect(address, command, new ProxyClientConnectionHandler(address, client,proxyChannel));
    }
}
