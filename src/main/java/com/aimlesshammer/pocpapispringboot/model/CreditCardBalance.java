package com.aimlesshammer.pocpapispringboot.model;

import java.util.Objects;

public class CreditCardBalance {
    private String customerId;
    private String creditCardNumber;
    private String balance;

    public CreditCardBalance(String customerId, String creditCardNumber, String balance) {
        this.customerId = customerId;
        this.creditCardNumber = creditCardNumber;
        this.balance = balance;
    }

    public CreditCardBalance() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
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
                ", creditCardNumber='" + creditCardNumber + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCardBalance that = (CreditCardBalance) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(creditCardNumber, that.creditCardNumber) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, creditCardNumber, balance);
    }
}
