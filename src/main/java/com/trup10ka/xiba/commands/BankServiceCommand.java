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

    protected BankClientsService getBankClientsService()
    {
        return bankClientsService;
    }
}
