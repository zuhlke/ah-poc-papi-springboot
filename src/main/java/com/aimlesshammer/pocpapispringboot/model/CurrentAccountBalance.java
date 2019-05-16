package com.aimlesshammer.pocpapispringboot.model;

import java.util.Objects;

public class CurrentAccountBalance {
    private String customerId;
    private String accountNumber;
    private String balance;

    public CurrentAccountBalance(String customerId, String accountNumber, String balance) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public CurrentAccountBalance() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
    public String toString() {
        return "CreditCardBalance{" +
                "customerId='" + customerId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentAccountBalance that = (CurrentAccountBalance) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, accountNumber, balance);
    }
}
