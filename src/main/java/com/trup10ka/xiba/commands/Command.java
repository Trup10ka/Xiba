package com.trup10ka.xiba.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class was reused from the previous project Kappa
 * @see <a href="https://github.com/Trup10ka/Kappa/blob/main/src/main/java/com/trup10ka/kappa/cli/commands/Command.java">Kappa - Command</a>
 */
public abstract class Command
{
    @NotNull
    private final CommandIdentifier identifier;

    public Command(@NotNull CommandIdentifier identifier)
    {
        this.identifier = identifier;
    }

    @NotNull
    public abstract String execute(@Nullable String args);

    @NotNull
    public CommandIdentifier getIdentifier()
    {
        return identifier;
    }
}
