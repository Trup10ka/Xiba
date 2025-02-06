package com.trup10ka.xiba.data;

import java.math.BigInteger;
import java.util.List;

public interface BankClientsService
{
    int addClient();

    boolean removeClient(int accountNumber);

    boolean deposit(int accountNumber, long amount);

    BigInteger getBalance(int accountNumber);

    boolean withdraw(int accountNumber, long amount);

    long getTotalBalance();

    long getNumberOfClients();

    boolean saveClient(BankAccount account);

    List<BankAccount> loadClients();
}
