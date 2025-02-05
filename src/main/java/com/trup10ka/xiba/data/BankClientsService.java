package com.trup10ka.xiba.data;

import java.math.BigInteger;

public interface BankClientsService
{
    void addClient(int accountNumber);

    void removeClient(int accountNumber);

    boolean clientExists(int accountNumber);

    void deposit(int accountNumber, long amount);

    BigInteger getBalance(int accountNumber);

    void withdraw(int accountNumber, long amount);

    long getTotalBalance();

    long getNumberOfClients();
}
