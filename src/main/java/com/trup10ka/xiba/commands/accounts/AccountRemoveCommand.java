package com.trup10ka.xiba.commands.accounts;

import com.trup10ka.xiba.commands.Command;
import com.trup10ka.xiba.commands.CommandIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccountRemoveCommand extends Command
{
    public AccountRemoveCommand(@NotNull CommandIdentifier identifier)
    {
        super(identifier);
    }

    @Override
    public @NotNull String execute(@Nullable String args)
    {
        return "";
    }
}
