package com.trup10ka.xiba.commands;

import com.trup10ka.xiba.config.XibaConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandManager
{
    private static final Map<CommandIdentifier, Command> commands = Collections.synchronizedMap(new HashMap<>());

    public static void initCommands(XibaConfig config)
    {
        commands.put(CommandIdentifier.BANK_CODE, new BankCodeCommand(CommandIdentifier.BANK_CODE, config.getSocketAddress()));
    }

    public static Command getCommand(CommandIdentifier identifier)
    {
        return commands.get(identifier);
    }
}
