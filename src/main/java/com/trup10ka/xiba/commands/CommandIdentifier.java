package com.trup10ka.xiba.commands;

import org.jetbrains.annotations.Nullable;

/**
 * This class was reused from the previous project Kappa
 * @see <a href="https://github.com/Trup10ka/Kappa/blob/main/src/main/java/com/trup10ka/kappa/cli/commands/CommandIdentifier.java">Kappa - Command identifier</a>
 */
public enum CommandIdentifier
{
    /* ==== Bank commands ==== */
    BANK_CODE("bc"),
    BANK_AMOUNT("ba"),
    BANK_NUMBER_OF_CLIENTS("bn"),

    /* ==== Account commands ==== */
    ACCOUNT_CREATE("ac"),
    ACCOUNT_DEPOSIT("ad"),
    ACCOUNT_WITHDRAWAL("aw"),
    ACCOUNT_BALANCE("ab"),
    ACCOUNT_REMOVE("ar");

    public final String identifier;

    CommandIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    @Nullable
    public static CommandIdentifier fromString(String identifier)
    {
        for (CommandIdentifier commandIdentifier : CommandIdentifier.values())
        {
            if (commandIdentifier.identifier.equals(identifier.toLowerCase()))
            {
                return commandIdentifier;
            }
        }

        return null;
    }
}
