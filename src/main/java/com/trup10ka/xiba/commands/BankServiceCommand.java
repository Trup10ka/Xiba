package com.trup10ka.xiba.commands;

import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;

public abstract class BankServiceCommand extends Command
{
    private final BankClientsService bankClientsService;

    public BankServiceCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService)
    {
        super(identifier);
        this.bankClientsService = bankClientsService;
    }

    protected int parseAccountNumber(String args) throws NumberFormatException
    {
        return args.split("/").length > 1 ? Integer.parseInt(args.split("/")[1]) : -1;
    }

    protected String parseSocketAddress(String args)
    {
        String[] parts = args.split("/");
        return parts.length > 1 ? parts[1] : "";
    }

    protected int parseValue(String args) throws NumberFormatException
    {
        String[] parts = args.split("/");
        if (parts.length < 2)
            return -1;

        String[] valueParts = parts[1].split(" ");
        return valueParts.length > 1 ? Integer.parseInt(valueParts[1]) : -1;
    }

    protected BankClientsService getBankClientsService()
    {
        return bankClientsService;
    }
}
