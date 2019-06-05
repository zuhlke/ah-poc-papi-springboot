package com.aimlesshammer.pocpapispringboot.sapis.blocking;

import com.aimlesshammer.pocpapispringboot.model.blocking.CreditCardBalance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(CreditCardBalanceSapi.class)
@TestPropertySource(
        properties = {
                "sapi.creditCardBalance.url=https://ah-poc-sapi-cc-bal.cfapps.io/customer/{CUSTOMER_ID}/balance"
        }
)
public class CreditCardBalanceSapiTest {

    @Autowired
    private CreditCardBalanceSapi unit;
    @Autowired
    private MockRestServiceServer server;
    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrl;
    @Value("${sapi.creditCardBalance.health}")
    private String creditCardHealth;

    private static final String creditCardBalance = "[\n" +
            "  {\n" +
            "    \"customerId\": \"10101010\",\n" +
            "    \"creditCardNumber\": \"1234567890\",\n" +
            "    \"balance\": \"1234.50\"\n" +
            "  }\n" +
            "]";

    private static String healthStatusResponse(String healthStatus) {
        String jsonString = "{\"status\":\"%s\"}";
        return String.format(jsonString, healthStatus);
    }

    @Test
    public void itGetsCreditCardBalance() {
        String customerId = "10101010";
        this.server.expect(requestTo(creditCardBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(creditCardBalance, APPLICATION_JSON));
        List<CreditCardBalance> expected = new ArrayList<>();
        expected.add(new CreditCardBalance("10101010", "1234567890", "1234.50"));
        assertEquals(expected, unit.getData(customerId));
    }

    @Test
    public void healthIsUpWhenSapiHealthEndpointReturns2XXAndStatusIsUp() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(healthStatusResponse("UP"), APPLICATION_JSON));
        Health expected = Health.up().build();
        Health actual = unit.health();
        assertEquals(expected, actual);
    }

    @Test
    public void healthIsUnknownWhenSapiHealthEndpointReturns2XXAndStatusIsNotUP() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(healthStatusResponse("CUP"), APPLICATION_JSON));
        Health expected = Health.unknown().withDetail("status", "CUP").build();
        Health actual = unit.health();
        assertEquals(expected, actual);
    }

    @Test
    public void healthIsDownWhenSapiHealthEndpointReturnsSomethingElseThan2XX() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withBadRequest().body(healthStatusResponse("DOWN")));
        Health expected = Health.down().build();
        Health actual = unit.health();
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}