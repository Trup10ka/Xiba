package com.trup10ka.xiba.commands.accounts;

import com.trup10ka.xiba.commands.BankServiceCommand;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccountDepositCommand extends BankServiceCommand
{
    public AccountDepositCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService)
    {
        super(identifier, bankClientsService);
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        boolean success = getBankClientsService().deposit(0, 0);
        return "";
    }
}
