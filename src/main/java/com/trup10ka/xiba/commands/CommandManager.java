package com.trup10ka.xiba.commands;

import com.trup10ka.xiba.commands.accounts.*;
import com.trup10ka.xiba.commands.bank.BankCodeCommand;
import com.trup10ka.xiba.commands.bank.BankTotalClientsCommand;
import com.trup10ka.xiba.commands.bank.BankTotalCommand;
import com.trup10ka.xiba.config.XibaConfig;
import com.trup10ka.xiba.data.BankClientsService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.trup10ka.xiba.commands.CommandIdentifier.*;

public class CommandManager
{
    private static final Map<CommandIdentifier, Command> commands = Collections.synchronizedMap(new HashMap<>());

    public static void initCommands(XibaConfig config, BankClientsService bankClientsService)
    {
        commands.put(BANK_CODE, new BankCodeCommand(BANK_CODE, config.getSocketAddress()));
        commands.put(BANK_AMOUNT, new BankTotalCommand(BANK_AMOUNT, bankClientsService));
        commands.put(BANK_NUMBER_OF_CLIENTS, new BankTotalClientsCommand(BANK_NUMBER_OF_CLIENTS, bankClientsService));

        commands.put(ACCOUNT_CREATE, new AccountCreateCommand(ACCOUNT_CREATE, bankClientsService, config.getSocketAddress()));
        commands.put(ACCOUNT_DEPOSIT, new AccountDepositCommand(ACCOUNT_DEPOSIT, bankClientsService));
        commands.put(ACCOUNT_WITHDRAWAL, new AccountWithdrawalCommand(ACCOUNT_WITHDRAWAL, bankClientsService));
        commands.put(ACCOUNT_BALANCE, new AccountBalanceCommand(ACCOUNT_BALANCE, bankClientsService));
        commands.put(ACCOUNT_REMOVE, new AccountRemoveCommand(ACCOUNT_REMOVE, bankClientsService));
    }

    public static Command getCommand(CommandIdentifier identifier)
    {
        return commands.get(identifier);
    }
}
