package com.trup10ka.xiba.commands;

import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;

public abstract class BankServiceCommand extends Command
{
    private final BankClientsService bankClientsService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BankServiceCommand.class);

    public BankServiceCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService)
    {
        super(identifier);
        this.bankClientsService = bankClientsService;
    }

    protected CommandArguments parseArguments(String args)
    {
        try
        {
            int account = parseAccountNumber(args);
            String ip = parseSocketAddress(args);
            int value = parseValue(args);
            return new CommandArguments(account, ip, value);
        }
        catch (NumberFormatException e)
        {
            logger.error("Failed to parse account number, invalid format: {}", e.getMessage());
            return new CommandArguments(-1, "", -1, "Invalid account number format");
        }
    }

    private int parseAccountNumber(String args) throws NumberFormatException
    {
        return args.split("/").length > 1 ? Integer.parseInt(args.split("/")[0]) : -1;
    }

    private String parseSocketAddress(String args)
    {
        String[] parts = args.split("/");
        String ip = parts.length > 1 ? parts[1].split(" ")[0] : "";
        if (!isValidIpAddress(ip))
        {
            return "";
        }
        return ip;
    }

    private int parseValue(String args) throws NumberFormatException
    {
        String[] parts = args.split("/");
        if (parts.length < 2)
            return -1;

        String[] valueParts = parts[1].split(" ");
        return valueParts.length > 1 ? Integer.parseInt(valueParts[1]) : -1;
    }

    private boolean isValidIpAddress(String ip)
    {
        String ipPattern =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    protected BankClientsService getBankClientsService()
    {
        return bankClientsService;
    }
}
