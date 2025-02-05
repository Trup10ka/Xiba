package com.trup10ka.xiba;

import ch.qos.logback.classic.LoggerContext;
import com.trup10ka.xiba.commands.CommandManager;
import com.trup10ka.xiba.config.FileConfigLoader;
import com.trup10ka.xiba.config.XibaConfig;
import com.trup10ka.xiba.util.ConsoleColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import static com.trup10ka.xiba.util.ConsoleColor.RED;
import static com.trup10ka.xiba.util.ConsoleUtil.println;
import static com.trup10ka.xiba.util.LoggingUtil.compressLogFile;


public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException
    {
        FileConfigLoader configLoader = new FileConfigLoader("config.conf");
        XibaConfig config = configLoader.loadConfig();

        CommandManager.initCommands(config);

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

    private static void keepServerRunning(XibaServer server) throws InterruptedException, IOException
    {
        CountDownLatch latch = new CountDownLatch(1);

        Thread shutdownHook = new Thread(() -> {
            server.stop();
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.stop();
            archiveLog();
            latch.countDown();
        });

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        logger.info("Server shutdown hook initialized");

        latch.await();
    }

    private static void archiveLog()
    {
        try
        {
            compressLogFile(
                    "logs/application.log",
                    "logs/application-" +LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log.gz"
            );
        }
        catch (IOException e)
        {
            println("Failed to archive log file, reason: " + e.getMessage(), RED);
        }
    }

}
