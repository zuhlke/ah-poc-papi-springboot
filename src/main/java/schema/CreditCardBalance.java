
package schema;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "customerId",
        "creditCardNumber",
        "balance"
})
public class CreditCardBalance extends GenericBalance {

    @JsonProperty("customerId")
    private String customerId;
    @JsonProperty("creditCardNumber")
    private String creditCardNumber;
    @JsonProperty("balance")
    private String balance;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public CreditCardBalance() {
        super();
    }

    /**
     * @param balance
     * @param customerId
     * @param creditCardNumber
     */
    public CreditCardBalance(String customerId, String creditCardNumber, String balance) {
        super();
        this.customerId = customerId;
        this.creditCardNumber = creditCardNumber;
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

    @JsonProperty("creditCardNumber")
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    @JsonProperty("creditCardNumber")
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
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
        CreditCardBalance that = (CreditCardBalance) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(creditCardNumber, that.creditCardNumber) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, creditCardNumber, balance, additionalProperties);
    }
}
