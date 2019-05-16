package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.BalanceRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(SapiService.class)
@TestPropertySource(
        properties = {
                "sapis.creditCardBalance.url=https://ah-poc-sapi-cc-bal.cfapps.io/customer/{CUSTOMER_ID}/balance",
                "sapis.currentAccountBalance.url=https://ah-poc-sapi-ca-bal.cfapps.io/customer/{CUSTOMER_ID}/balance"
        }
)
public class SapiServiceTest {
    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private SapiService sapiService;

    @Value("${sapis.creditCardBalance.url}")
    private String creditCardBalanceUrl;
    @Value("${sapis.currentAccountBalance.url}")
    private String currentAccountBalanceUrl;

    private static final String creditCardBalance = "[\n" +
            "  {\n" +
            "    \"customerId\": \"10101010\",\n" +
            "    \"creditCardNumber\": \"1234567890\",\n" +
            "    \"balance\": \"1234.50\"\n" +
            "  }\n" +
            "]";

    private static final String accountBalance = "[\n" +
            "  {\n" +
            "    \"customerId\": \"10101010\",\n" +
            "    \"accountNumber\": \"64746383648\",\n" +
            "    \"balance\": \"34.50\"\n" +
            "  }\n" +
            "]";

    @Test
    public void itGetsAccounts() {
        final String customerId = "10101010";
        this.server.expect(requestTo(creditCardBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(creditCardBalance, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(accountBalance, MediaType.APPLICATION_JSON));

        final List<BalanceRecord> expected = new ArrayList<>();
        expected.add(new BalanceRecord("creditCardAccount", "1234567890", "1234.50"));
        expected.add(new BalanceRecord("currentAccount", "64746383648", "34.50"));
        assertEquals(expected, sapiService.getAllBalances(customerId));
    }
}