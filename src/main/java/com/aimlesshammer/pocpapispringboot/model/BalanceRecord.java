package com.aimlesshammer.pocpapispringboot.model;

import java.util.Objects;

public class BalanceRecord {
    private String accountType;
    private String accountNumber;
    private String balance;

    public BalanceRecord(String accountType, String accountNumber, String balance) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public BalanceRecord(){}

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceRecord that = (BalanceRecord) o;
        return Objects.equals(accountType, that.accountType) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountType, accountNumber, balance);
    }

    @Override
    public String toString() {
        return "BalanceRecord{" +
                "accountType='" + accountType + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
