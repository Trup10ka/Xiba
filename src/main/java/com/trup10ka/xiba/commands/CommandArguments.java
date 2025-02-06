package com.trup10ka.xiba.commands;

import org.jetbrains.annotations.NotNull;

public record CommandArguments(int account, @NotNull String ip, int value, String errorMessage)
{
    public CommandArguments(int account, @NotNull String ip, int value)
    {
        this(account, ip, value, null);
    }
}
