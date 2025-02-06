package com.trup10ka.xiba.commands.accounts;

import com.trup10ka.xiba.commands.BankServiceCommand;
import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import com.trup10ka.xiba.data.BankClientsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class AccountCreateCommand extends BankServiceCommand
{

    private InetSocketAddress address;

    public AccountCreateCommand(@NotNull CommandIdentifier identifier, BankClientsService bankClientsService, InetSocketAddress address)
    {
        super(identifier, bankClientsService);
        this.address = address;
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        boolean result = getBankClientsService().addClient();
        return formatAnswer(result, 10100, address.getHostName());
    }

    private String formatAnswer(boolean result, long accountNumber, String address)
    {
        if (result)
        {
            return "BC " + accountNumber + "/" + address;
        }
        else
        {
            return "ER Account creation failed";
        }
    }
}
