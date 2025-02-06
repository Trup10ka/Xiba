package com.trup10ka.xiba.commands.bank;

import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class BankCodeCommand extends Command
{
    private final InetSocketAddress address;

    public BankCodeCommand(@NotNull CommandIdentifier identifier, InetSocketAddress address)
    {
        super(identifier);
        this.address = address;
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        return "BC " + address.getHostString();
    }
}
