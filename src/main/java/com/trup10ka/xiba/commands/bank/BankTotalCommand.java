package com.trup10ka.xiba.commands.bank;

import com.trup10ka.xiba.commands.BankServiceCommand;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BankTotalCommand extends BankServiceCommand
{

    public BankTotalCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService)
    {
        super(identifier, bankClientsService);
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        return "";
    }
}
