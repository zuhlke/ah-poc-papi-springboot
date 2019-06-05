
package com.aimlesshammer.pocpapispringboot.model.reactive;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "customerId",
        "accountNumber",
        "balance"
})
public class CurrentAccountBalance {

    @JsonProperty("customerId")
    private String customerId;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("balance")
    private String balance;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public CurrentAccountBalance() {
        super();
    }

    /**
     * @param balance
     * @param accountNumber
     * @param customerId
     */
    public CurrentAccountBalance(String customerId, String accountNumber, String balance) {
        super();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    @JsonProperty("customerId")
    public String getCustomerId() {
        return customerId;
    }

    @JsonProperty("customerId")
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @JsonProperty("accountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("accountNumber")
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty("balance")
    public String getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(String balance) {
        this.balance = balance;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentAccountBalance that = (CurrentAccountBalance) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, accountNumber, balance, additionalProperties);
    }
}
