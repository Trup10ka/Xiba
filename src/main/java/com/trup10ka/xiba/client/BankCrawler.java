package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaServer;
import com.trup10ka.xiba.config.XibaConfig;
import com.trup10ka.xiba.data.BankData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.trup10ka.xiba.util.NetworkUtil.getAllIPsInSubnet;

public class BankCrawler
{
    private static final Logger logger = LoggerFactory.getLogger(BankCrawler.class);

    private final List<BankData> banks = Collections.synchronizedList(new ArrayList<>(50));

    private final ExecutorService executor = Executors.newFixedThreadPool(XibaServer.getConfig().bankRobbery().maxPoolSize());

    public List<BankData> crawlBanks(String cidr)
    {
        List<BankData> banks = new ArrayList<>();
        List<String> ips;

        try
        {
            ips = getAllIPsInSubnet(cidr);
        }
        catch (IOException e)
        {
            logger.error("Failed to resolve IP address: {}", e.getMessage());
            return banks;
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String ip : ips)
        {
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> XibaClient.findBank(ip), executor)
                    .thenCompose(port -> port == -1 ? CompletableFuture.completedFuture(null) : fetchBankData(ip, port))
                    .thenAccept(bankData -> {
                        if (bankData != null)
                            synchronized (banks)
                            {
                                banks.add(bankData);
                            }
                    });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return banks;
    }

    private CompletableFuture<BankData> fetchBankData(String ip, int port)
    {
        return CompletableFuture.supplyAsync(() -> {
            try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open())
            {
                channel.connect(new InetSocketAddress(ip, port)).get(3, TimeUnit.SECONDS);

                String bankNumberOfClients = sendCommand(channel, "BN");
                String totalCash = sendCommand(channel, "BA");

                logger.info("Added bank: IP={}, Port={}, BN={}, BA={}", ip, port, bankNumberOfClients, totalCash);
                int numberOfClients = Integer.parseInt(bankNumberOfClients.trim().split(" ")[1]);
                BigInteger totalCashBigInt = new BigInteger(totalCash.trim().split(" ")[1]);
                return new BankData(new InetSocketAddress(ip, port), numberOfClients, totalCashBigInt);
            }
            catch (Exception e)
            {
                logger.warn("Failed to communicate with bank at {}:{} - {}", ip, port, e.getMessage());
                return null;
            }
        }, executor);
    }

    private String sendCommand(AsynchronousSocketChannel channel, String command) throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put((command + "\n").getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer).get(2, TimeUnit.SECONDS);
        buffer.clear();

        channel.read(buffer).get(2, TimeUnit.SECONDS);
        buffer.flip();

        return StandardCharsets.UTF_8.decode(buffer).toString().trim();
    }
}
