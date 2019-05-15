package com.aimlesshammer.pocpapispringboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(SapiService.class)
public class SapiServiceTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private SapiService sapiService;

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
        this.server.expect(requestTo("https://ah-poc-sapi-cc-bal.cfapps.io/balance?customerId=1")).andRespond(withSuccess(creditCardBalance, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo("https://ah-poc-sapi-ca-bal.cfapps.io/balance?customerId=1")).andRespond(withSuccess(accountBalance, MediaType.APPLICATION_JSON));

        final List<BalanceRecord> expected = new ArrayList<>();
        expected.add(new BalanceRecord("creditCardAccount", "1234567890", "1234.50"));
        expected.add(new BalanceRecord("currentAccount", "64746383648", "34.50"));
        assertEquals(expected, sapiService.getAllBalances("1"));
    }
}