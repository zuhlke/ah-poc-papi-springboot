package com.aimlesshammer.pocpapispringboot.sapis.reactive;

import com.aimlesshammer.pocpapispringboot.SapiClientConf;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@Import({ReactiveCurrentAccountBalanceSapi.class, SapiClientConf.class})
@TestPropertySource(properties = {
        "sapi.timeout = 10",
        "sapi.retries = 1",
        "sapi.backoff = 0",
        "sapi.delay = 0",
})
public class ReactiveCurrentAccountBalanceSapiTest {
    private static final String API_CC_ACCOUNTS = "/customer/1/balance";

    @Autowired
    private ReactiveCurrentAccountBalanceSapi retriever;

    @After
    public void setUp(){
        WireMock.reset();
    }

    @Test
    public void itReturnsAllCreditCardsForAGivenCustomer_WhenCustomerHasCards() {
        String response = "[\n" +
                "  {\n" +
                "    \"customerId\": \"1\",\n" +
                "    \"accountNumber\": \"64746383648\",\n" +
                "    \"balance\": \"34.50\"\n" +
                "  }\n" +
                "]";
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withBody(response)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        List<Balance> ccbs = new ArrayList<>();
        Balance ccb = new Balance("CurrentAccount", "64746383648", "34.50");
        ccbs.add(ccb);

        assertEquals(ccbs, retriever.getCurrentAccountBalance("1").block());
    }

    @Test
    public void itReturnsEmptyList_WhenCustomerHasNoCards() {
        String accountsResponse = "[]";

        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withBody(accountsResponse)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        assertEquals(emptyList(), retriever.getCurrentAccountBalance("1").block());
    }

    @Test(expected = Exception.class)
    public void itReturnsException_WhenRequestFails() {
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withStatus(HttpStatus.SC_BAD_REQUEST)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        retriever.getCurrentAccountBalance("1").block();
    }

    @Test
    public void itRetriesUpTo3Times_WhenRequestFails() {
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withStatus(HttpStatus.SC_BAD_REQUEST)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        try {
            retriever.getCurrentAccountBalance("1").block();
            fail("No exception thrown");
        } catch (Exception e) {
            verify(2, getRequestedFor(urlEqualTo(API_CC_ACCOUNTS)));
        }
    }
}