package com.trup10ka.xiba.commands.bank;

import com.trup10ka.xiba.XibaServer;
import com.trup10ka.xiba.client.BankCrawler;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RobberyPlanCommand extends Command
{
    private static Logger logger = LoggerFactory.getLogger(RobberyPlanCommand.class);

    private final InetSocketAddress address;

    public RobberyPlanCommand(@NotNull CommandIdentifier identifier, @NotNull InetSocketAddress address)
    {
        super(identifier);
        this.address = address;
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        if (args == null || args.isEmpty())
        {
            logger.error("Client provided no arguments for the robbery plan");
            return "ER Please provide the target amount for the robbery plan";
        }
        long targetAmount = parseLong(args);
        if (targetAmount <= 0)
        {
            logger.error("Client provided an invalid target amount for the robbery plan: {}", args);
            return "ER Invalid target amount";
        }
        BankCrawler bankCrawler = new BankCrawler();
        logger.info("CIDR mask: {}", address.getHostString() + XibaServer.getConfig().bankRobbery().bankRobberySubnetMask());
        List<BankData> banks = bankCrawler.crawlBanks(address.getHostString() + XibaServer.getConfig().bankRobbery().bankRobberySubnetMask());
        List<BankData> bestPlan = planRobbery(banks, BigInteger.valueOf(targetAmount));

        if (bestPlan.isEmpty())
        {
            return "ER No banks found to rob";
        }
        String formatedBanks = formatBanks(bestPlan);
        return "RP The best plan is to rob these banks: " + formatedBanks;
    }

    public static List<BankData> planRobbery(List<BankData> banks, BigInteger targetAmount)
    {
        // Sort banks: First by highest bankTotal, then by lowest numberOfClients
        banks.sort(Comparator.comparing(BankData::bankTotal).reversed()
                .thenComparingInt(BankData::numberOfClients));

        List<BankData> bestPlan = new ArrayList<>();
        findOptimalPlan(banks, 0, BigInteger.ZERO, targetAmount, new ArrayList<>(), bestPlan);
        return bestPlan;
    }

    private static void findOptimalPlan(List<BankData> banks,
                                        int index,
                                        BigInteger currentTotal,
                                        BigInteger target,
                                        List<BankData> currentPlan,
                                        List<BankData> bestPlan
    )
    {
        // If we reach the target or exceed it, check if it's the best solution
        if (currentTotal.compareTo(target) >= 0)
        {
            if (bestPlan.isEmpty() || getClientCount(currentPlan) < getClientCount(bestPlan))
            {
                bestPlan.clear();
                bestPlan.addAll(new ArrayList<>(currentPlan));
            }
            return;
        }

        // If no more banks to process, return
        if (index >= banks.size()) return;

        // Try including this bank
        currentPlan.add(banks.get(index));
        findOptimalPlan(banks, index + 1, currentTotal.add(banks.get(index).bankTotal()), target, currentPlan, bestPlan);
        currentPlan.removeLast();

        // Try skipping this bank
        findOptimalPlan(banks, index + 1, currentTotal, target, currentPlan, bestPlan);
    }

    private static int getClientCount(List<BankData> banks)
    {
        return banks.stream().mapToInt(BankData::numberOfClients).sum();
    }

    private static String formatBanks(List<BankData> banks)
    {
        StringBuilder builder = new StringBuilder();
        for (BankData bank : banks)
        {
            builder.append("[").append(bank.address()).append(" | ").append(bank.numberOfClients()).append(" | ").append(bank.bankTotal()).append("] ");
        }
        return builder.toString();
    }

    private long parseLong(String value)
    {
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException exception)
        {
            return -1;
        }
    }
}
