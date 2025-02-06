package com.trup10ka.xiba.commands.accounts;

import com.trup10ka.xiba.commands.BankServiceCommand;

import com.trup10ka.xiba.commands.CommandArguments;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class AccountBalanceCommand extends BankServiceCommand
{
    private static final Logger logger = LoggerFactory.getLogger(AccountBalanceCommand.class);

    private final InetSocketAddress address;

    public AccountBalanceCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService, InetSocketAddress address)
    {
        super(identifier, bankClientsService);
        this.address = address;
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        //  	AB <account>/<ip>
        CommandArguments parsedArguments = parseArguments(args);
        if (parsedArguments.account() == -1)
        {
            return "ER " + parsedArguments.errorMessage();
        }
        else if (parsedArguments.ip().isEmpty())
        {
            return "ER Invalid IP address or none provided";
        }

        if (parsedArguments.ip().equals(address.getHostName()))
            return "AB " + getBankClientsService().getBalance(parsedArguments.account()).toString();
        else
            return "ER Invalid IP address"; // TODO: implement proxying
    }

    private CommandArguments parseArguments(String args)
    {

        try
        {
            int account = parseAccountNumber(args);
            String ip = parseSocketAddress(args);
            return new CommandArguments(account, ip, null);
        }
        catch (NumberFormatException e)
        {
            logger.error("Failed to parse account number, invalid format", e);
            return new CommandArguments(-1, "", null, "Invalid account number format");
        }
    }
}
