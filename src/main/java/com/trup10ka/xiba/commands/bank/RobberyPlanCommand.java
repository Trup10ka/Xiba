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
        List<BankData> banks = bankCrawler.crawlBanks(address.getHostString() + XibaServer.getConfig().bankRobbery().bankRobberySubnetMask());
        List<BankData> bestPlan = planRobbery(banks, BigInteger.valueOf(targetAmount));

        if (bestPlan.isEmpty())
        {
            return "ER No banks found to rob";
        }
        String formatedBanks = formatBanks(bestPlan);
        return "RP The best plan is to rob these banks: " + formatedBanks;
    }

    /**
     * Finds the optimal robbery plan to reach as close as possible to the target amount while affecting the fewest number of clients.
     * @param banks The list of available banks to consider for the robbery.
     * @param targetAmount The target amount of money to be robbed.
     * @return The optimal robbery plan - banks to be robbed.
     */
    public static List<BankData> planRobbery(List<BankData> banks, BigInteger targetAmount)
    {
        // Sort banks: First by highest bankTotal, then by lowest numberOfClients
        banks.sort(Comparator
                .comparing(BankData::bankTotal)
                .reversed()
                .thenComparingInt(BankData::numberOfClients));

        List<BankData> bestPlan = new ArrayList<>();
        findOptimalPlan(banks, 0, BigInteger.ZERO, targetAmount, new ArrayList<>(), bestPlan);
        return bestPlan;
    }

    /**
     * Recursively finds the optimal robbery plan to reach as close as possible to the target amount
     * while affecting the fewest number of clients.
     *
     * @param banks        The list of available banks to consider for the robbery.
     * @param index        The current index of the bank being considered in the recursion.
     * @param currentTotal The sum of money collected so far from the selected banks.
     * @param target       The target amount of money to be robbed.
     * @param currentPlan  The list of banks currently selected in this recursive branch.
     * @param bestPlan     The best robbery plan found so far, minimizing the number of affected clients.
     */
    private static void findOptimalPlan(
            List<BankData> banks,
            int index,
            BigInteger currentTotal,
            BigInteger target,
            List<BankData> currentPlan,
            List<BankData> bestPlan
    )
    {
        // If we reach or exceed the target, check if this plan is better than the current best
        if (currentTotal.compareTo(target) >= 0)
        {
            if (bestPlan.isEmpty() || getClientCount(currentPlan) < getClientCount(bestPlan))
            {
                bestPlan.clear();
                bestPlan.addAll(new ArrayList<>(currentPlan));
            }
            return;
        }

        // If all banks have been considered, return
        if (index >= banks.size()) return;

        // Include the current bank in the plan and proceed with recursion
        currentPlan.add(banks.get(index));
        findOptimalPlan(banks, index + 1, currentTotal.add(banks.get(index).bankTotal()), target, currentPlan, bestPlan);
        currentPlan.removeLast();

        // Exclude the current bank and proceed with recursion
        findOptimalPlan(banks, index + 1, currentTotal, target, currentPlan, bestPlan);
    }

    /**
     * Returns the total number of clients affected by the robbery plan.
     * @param banks The list of banks to consider.
     * @return The total number of clients affected by the robbery plan.
     */
    private static int getClientCount(List<BankData> banks)
    {
        return banks.stream().mapToInt(BankData::numberOfClients).sum();
    }

    private static String formatBanks(List<BankData> banks)
    {
        StringBuilder builder = new StringBuilder();
        for (BankData bank : banks)
        {
            builder.append(bank.toString()).append(" | ");
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
