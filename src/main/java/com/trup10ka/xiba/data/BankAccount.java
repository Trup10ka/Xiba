package com.trup10ka.xiba.data;

import java.math.BigInteger;

public record BankAccount(
    int accountNumber,
    BigInteger balance
)
{
}
