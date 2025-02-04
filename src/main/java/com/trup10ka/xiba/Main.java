package com.trup10ka.xiba;

import ch.qos.logback.classic.LoggerContext;
import com.trup10ka.xiba.config.FileConfigLoader;
import com.trup10ka.xiba.config.XibaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;


public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException
    {
        FileConfigLoader configLoader = new FileConfigLoader("config.conf");
        XibaConfig config = configLoader.loadConfig();

        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel
                .open()
                .bind(config.getSocketAddress());

        XibaServer server = new XibaServer(serverSocketChannel);
        server.start();

        logger.info("Server started on: {}", config.getSocketAddress());

        startCliForServer(server);
        keepServerRunning(server);
    }

    private static void startCliForServer(XibaServer server)
    {
        Thread cliThread = new Thread(() -> {
            logger.info("CLI thread started, can insert server commands now");
            while (true)
            {
                try
                {
                    int command = System.in.read();
                    if (command == 'q')
                    {
                        logger.info("Received shutdown command, shutdown sequence started");
                        System.exit(0);
                    }
                }
                catch (IOException e)
                {
                    logger.error("Failed to read command from CLI, reason: {}", e.getMessage());
                }
            }
        });

        cliThread.start();
    }

    private static void keepServerRunning(XibaServer server) throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);

        Thread shutdownHook = new Thread(() -> {
            server.stop();
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.stop();
            latch.countDown();
        });

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        logger.info("Server shutdown hook initialized");

        latch.await();
    }

}
