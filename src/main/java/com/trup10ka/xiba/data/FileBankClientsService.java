package com.trup10ka.xiba.data;

import com.trup10ka.xiba.config.XibaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FileBankClientsService implements BankClientsService
{
    private static final Logger logger = LoggerFactory.getLogger(FileBankClientsService.class);

    private final String CLIENTS_FILE;

    private final int minAccountNumber;

    private final int maxAccountNumber;

    private int currentAccountNumberCursor;

    private long numberOfClients;

    public FileBankClientsService(String clientsFile, XibaConfig config)
    {
        CLIENTS_FILE = clientsFile;
        minAccountNumber = config.ranges().minAccountNumber();
        maxAccountNumber = config.ranges().maxAccountNumber();
    }

    public void initCursor()
    {
        createSaveFileIfDoesNotExist();
        List<BankAccount> accounts = loadClients();
        numberOfClients = accounts.size();
        if (accounts.isEmpty())
        {
            currentAccountNumberCursor = minAccountNumber;
        }
        else
        {
            currentAccountNumberCursor = accounts.getLast().accountNumber() + 1;
        }
    }

    @Override
    public boolean addClient()
    {
        int generatedAccountNumber = currentAccountNumberCursor++;
        BankAccount bankAccount = new BankAccount(generatedAccountNumber, BigInteger.ZERO);
        saveClient(bankAccount);
        numberOfClients++;
        return true;
    }

    @Override
    public boolean removeClient(int accountNumber)
    {
        List<BankAccount> accounts = loadClients();
        accounts.removeIf(account -> account.accountNumber() == accountNumber);
        boolean success = saveClients(accounts);
        if (success)
            numberOfClients--;

        return success;
    }

    @Override
    public boolean deposit(int accountNumber, long amount)
    {
        removeClient(accountNumber);
        saveClient(
                new BankAccount(
                        accountNumber,
                        getBalance(accountNumber)
                                .add(BigInteger.valueOf(amount))
                )
        );
        return true;
    }

    @Override
    public BigInteger getBalance(int accountNumber)
    {
        List<BankAccount> accounts = loadClients();
        return accounts.stream()
                .filter(account -> account.accountNumber() == accountNumber)
                .findFirst()
                .map(BankAccount::balance)
                .orElse(BigInteger.ZERO);
    }

    @Override
    public boolean withdraw(int accountNumber, long amount)
    {
        removeClient(accountNumber);
        saveClient(
                new BankAccount(
                        accountNumber,
                        getBalance(accountNumber)
                                .subtract(BigInteger.valueOf(amount))
                )
        );
        return true;
    }

    @Override
    public long getTotalBalance()
    {
        return loadClients().stream()
                .map(BankAccount::balance)
                .mapToLong(BigInteger::longValue)
                .sum();
    }

    @Override
    public long getNumberOfClients()
    {
        return numberOfClients;
    }

    @Override
    public synchronized boolean saveClient(BankAccount bankAccount)
    {
        if (numberOfClients > maxAccountNumber - minAccountNumber)
        {
            logger.error("Cannot add more clients, limit reached");
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLIENTS_FILE, true)))
        {
            bw.write(bankAccount.accountNumber() + "," + bankAccount.balance());
            bw.newLine();
            numberOfClients++;
            return true;
        }
        catch (Exception e)
        {
            logger.error("Failed to write client {} to file: {}", bankAccount, e.getMessage());
            return false;
        }
    }

    private synchronized boolean saveClients(List<BankAccount> bankAccounts)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CLIENTS_FILE)))
        {
            bw.write(bankAccounts.size() + "");
            bw.newLine();
            for (BankAccount bankAccount : bankAccounts)
            {
                bw.write(bankAccount.accountNumber() + "," + bankAccount.balance());
                bw.newLine();
            }
            return true;
        }
        catch (Exception e)
        {
            logger.error("Failed to write clients to file: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<BankAccount> loadClients()
    {
        List<BankAccount> bankAccounts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CLIENTS_FILE)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                int accountNumber = Integer.parseInt(values[0]);
                BigInteger balance = new BigInteger(values[1]);
                bankAccounts.add(new BankAccount(accountNumber, balance));
            }
        }
        catch (Exception e)
        {
            logger.error("Failed to read from file: {}", e.getMessage());
        }

        return bankAccounts;
    }

    private void createSaveFileIfDoesNotExist()
    {
        try
        {
             File file = new File(CLIENTS_FILE);
             if (file.createNewFile())
             {
                 logger.info("File with clients created: {}", file.getName());
             }
             else
             {
                 logger.info("File with clients detected: {}", file.getName());
             }
        }
        catch (Exception e)
        {
            logger.error("Failed to create file: {}", e.getMessage());
        }
    }
}
