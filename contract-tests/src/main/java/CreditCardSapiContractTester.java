import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static org.junit.Assert.assertEquals;

public class CreditCardSapiContractTester implements ContractTester {
    private final String origin;

    public CreditCardSapiContractTester(String origin) {
        this.origin = origin;
    }

    @Override
    public void runTests() throws UnirestException {
        assertEquals(
                "[\n{\n\"customerId\":\"10101010\",\n\"creditCardNumber\":\"1234567890\",\n\"balance\":\"1234.50\"\n}\n]",
                getRequestText("/customer/10101010/balance")
        );
    }

    @SuppressWarnings("SameParameterValue")
    private String getRequestText(String requestPath) throws UnirestException {
        String url = origin + requestPath;
        HttpResponse<String> response = Unirest.get(url).asString();
        return response.getBody();
    }
}
