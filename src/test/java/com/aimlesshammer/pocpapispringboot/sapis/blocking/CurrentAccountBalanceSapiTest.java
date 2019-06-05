package com.aimlesshammer.pocpapispringboot.sapis.blocking;

import com.aimlesshammer.pocpapispringboot.model.blocking.CurrentAccountBalance;
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
@RestClientTest(CurrentAccountBalanceSapi.class)
@TestPropertySource(
        properties = {
                "sapi.currentAccountBalance.url=https://ah-poc-sapi-ca-bal.cfapps.io/customer/{CUSTOMER_ID}/balance"
        }
)
public class CurrentAccountBalanceSapiTest {

    @Autowired
    private CurrentAccountBalanceSapi unit;
    @Autowired
    private MockRestServiceServer server;
    @Value("${sapi.currentAccountBalance.url}")
    private String currentAccountBalanceUrl;
    @Value("${sapi.currentAccountBalance.health}")
    private String currentAccountHealth;

    private static final String accountBalance = "[\n" +
            "  {\n" +
            "    \"customerId\": \"10101010\",\n" +
            "    \"accountNumber\": \"64746383648\",\n" +
            "    \"balance\": \"34.50\"\n" +
            "  }\n" +
            "]";

    private static String healthStatusResponse(String healthStatus) {
        String jsonString = "{\"status\":\"%s\"}";
        return String.format(jsonString, healthStatus);
    }

    @Test
    public void itGetsCurrentAccountBalance() {
        String customerId = "10101010";
        this.server.expect(requestTo(currentAccountBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(accountBalance, APPLICATION_JSON));
        List<CurrentAccountBalance> expected = new ArrayList<>();
        expected.add(new CurrentAccountBalance("10101010", "64746383648", "34.50"));
        assertEquals(expected, unit.getData(customerId));
    }

    @Test
    public void healthIsUpWhenSapiHealthEndpointReturns2XXAndStatusIsUP() {
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withSuccess(healthStatusResponse("UP"), APPLICATION_JSON));
        Health expected = Health.up().build();
        Health actual = unit.health();
        assertEquals(expected, actual);
    }

    @Test
    public void healthIsUnknownWhenSapiHealthEndpointReturns2XXAndStatusIsNotUP() {
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withSuccess(healthStatusResponse("CUP"), APPLICATION_JSON));
        Health expected = Health.unknown().withDetail("status", "CUP").build();
        Health actual = unit.health();
        assertEquals(expected, actual);
    }

    @Test
    public void healthIsDownWhenSapiHealthEndpointReturnsSomethingElseThan2XX() {
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withBadRequest().body(healthStatusResponse("DOWN")));
        Health expected = Health.down().build();
        Health actual = unit.health();
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}