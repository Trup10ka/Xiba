package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaServer;
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

    private final AsynchronousSocketChannel proxyChannel;

    private final AsynchronousSocketChannel client;

    private static final XibaConfig config = XibaServer.getConfig();

    public XibaClient(AsynchronousSocketChannel client) throws IOException
    {
        this.client = client;
        this.proxyChannel = AsynchronousSocketChannel.open();
    }

    public static int findBank(String targetIp)
    {
        logger.info("Finding bank on IP: {}", targetIp);
        for (int port = config.ranges().minPort(); port <= config.ranges().maxPort(); port++)
        {
            try (AsynchronousSocketChannel testChannel = AsynchronousSocketChannel.open())
            {
                logger.info("Trying to connect to {}:{}", targetIp, port);
                testChannel.connect(new InetSocketAddress(targetIp, port)).get(XibaServer.getConfig().timeouts().proxyClientTimeout(), TimeUnit.MILLISECONDS);
                logger.info("Found bank on {}:{}", targetIp, port);
                return port;
            }
            catch (Exception exception)
            {
                logger.warn("Failed to connect to {}:{}, reason: serve did not respond or request timed out", targetIp, port);
            }
        }
        return -1;
    }

    public void executeProxyCommand(String targetIp, int targetPort, String command)
    {
        InetSocketAddress address = new InetSocketAddress(targetIp, targetPort);

        proxyChannel.connect(address, command, new ProxyClientConnectionHandler(address, client, proxyChannel));
    }
}
