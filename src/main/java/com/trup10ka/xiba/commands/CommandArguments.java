package com.trup10ka.xiba.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CommandArguments(int account, @NotNull String ip, @Nullable String value, String errorMessage)
{
    public CommandArguments(int account, @NotNull String ip, @Nullable String value)
    {
        this(account, ip, value, null);
    }
}
