package com.trup10ka.xiba.commands;

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
        return "BC: " + address.getHostString();
    }
}
