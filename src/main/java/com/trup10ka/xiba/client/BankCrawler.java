package com.trup10ka.xiba.client;

import com.trup10ka.xiba.XibaServer;
import com.trup10ka.xiba.data.BankData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
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

import static com.trup10ka.xiba.util.ClientUtils.BUFFER_SIZE;
import static com.trup10ka.xiba.util.NetworkUtil.getAllIPsInSubnet;

public class BankCrawler
{
    private static final Logger logger = LoggerFactory.getLogger(BankCrawler.class);

    private static final int COMMAND_TIMEOUT = XibaServer.getConfig().bankRobbery().commandTimeout();

    private final List<BankData> banks = Collections.synchronizedList(new ArrayList<>(50));

    private final ExecutorService executor = Executors.newFixedThreadPool(XibaServer.getConfig().bankRobbery().maxPoolSize());

    public List<BankData> crawlBanks(String cidr)
    {
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

        List<CompletableFuture<Void>> futures = initAllBankRaids(ips);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return banks;
    }

    private CompletableFuture<BankData> fetchBankData(String ip, int port)
    {
        return CompletableFuture.supplyAsync(() -> {
            try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open())
            {
                return parseAsBankData(channel, ip, port);
            }
            catch (Exception e)
            {
                logger.warn("Failed to communicate with bank at {}:{} - {}", ip, port, e.getMessage());
                return null;
            }
        }, executor);
    }

    /**
     * Initiates asynchronous tasks to retrieve bank data from a list of IP addresses.
     * Each IP is queried to find an active bank, and if found, its data is fetched and stored.
     *
     * @param ips The list of IP addresses to check for banks.
     * @return A list of {@link CompletableFuture} objects representing ongoing asynchronous tasks.
     */
    private List<CompletableFuture<Void>> initAllBankRaids(List<String> ips)
    {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String ip : ips)
        {
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> XibaClient.findBank(ip), executor)
                    .thenCompose(port -> port == -1 ? CompletableFuture.completedFuture(null) : fetchBankData(ip, port))
                    .thenAccept(bankData -> {
                        if (bankData != null)
                            banks.add(bankData);
                    });

            futures.add(future);
        }

        return futures;
    }

    private String sendCommand(AsynchronousSocketChannel channel, String command) throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        buffer.put((command + "\n").getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer).get(COMMAND_TIMEOUT, TimeUnit.MILLISECONDS);
        buffer.clear();

        channel.read(buffer).get(COMMAND_TIMEOUT, TimeUnit.MILLISECONDS);
        buffer.flip();

        return StandardCharsets.UTF_8.decode(buffer).toString().trim();
    }

    private BankData parseAsBankData(AsynchronousSocketChannel channel, String ip, int port) throws Exception
    {
        channel.connect(new InetSocketAddress(ip, port)).get(COMMAND_TIMEOUT, TimeUnit.MILLISECONDS);

        String bankNumberOfClients = sendCommand(channel, "BN");
        String totalCash = sendCommand(channel, "BA");

        int numberOfClients = Integer.parseInt(getValueFromResponse(bankNumberOfClients));
        BigInteger totalCashBigInt = new BigInteger(getValueFromResponse(totalCash));

        logger.info("Added bank: IP={}, Port={}, BN={}, BA={}", ip, port, numberOfClients, totalCashBigInt);
        return new BankData(new InetSocketAddress(ip, port), numberOfClients, totalCashBigInt);
    }

    private String getValueFromResponse(String response)
    {
        return response.trim().split(" ")[1];
    }
}
