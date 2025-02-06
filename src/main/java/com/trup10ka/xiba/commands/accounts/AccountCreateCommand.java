package com.trup10ka.xiba.commands.accounts;

import com.trup10ka.xiba.commands.BankServiceCommand;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class AccountCreateCommand extends BankServiceCommand
{
    private static final Logger logger = LoggerFactory.getLogger(AccountCreateCommand.class);

    private final InetSocketAddress address;

    public AccountCreateCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService, InetSocketAddress address)
    {
        super(identifier, bankClientsService);
        this.address = address;
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        int result = getBankClientsService().addClient();
        return formatAnswer(result, address.getHostName());
    }

    private String formatAnswer(int result, String address)
    {
        if (result != -1)
        {
            return "AC " + result + "/" + address;
        }
        else
        {
            return "ER Account creation failed";
        }
    }
}
