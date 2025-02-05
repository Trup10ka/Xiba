package com.trup10ka.xiba.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class FileBankClientsService implements BankClientsService
{
    private static final Logger logger = LoggerFactory.getLogger(FileBankClientsService.class);

    private final String CLIENTS_FILE;

    public FileBankClientsService(String clientsFile)
    {
        CLIENTS_FILE = clientsFile;
    }

    @Override
    public void addClient(int accountNumber)
    {

    }

    @Override
    public void removeClient(int accountNumber)
    {

    }

    @Override
    public boolean clientExists(int accountNumber)
    {
        return false;
    }

    @Override
    public void deposit(int accountNumber, long amount)
    {

    }

    @Override
    public BigInteger getBalance(int accountNumber)
    {
        return null;
    }

    @Override
    public void withdraw(int accountNumber, long amount)
    {

    }

    @Override
    public long getTotalBalance()
    {
        return 0;
    }

    @Override
    public long getNumberOfClients()
    {
        return 0;
    }
}
