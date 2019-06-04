package com.aimlesshammer.pocpapispringboot;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import schema.GenericBalance;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@Import({CreditCardBalanceRetriever.class, SapiClientConf.class})
@TestPropertySource(properties = {
        "sapi.creditCardBalance.url=http://localhost:8080/customer/{CUSTOMER_ID}/balance",
        "sapi.timeout = 10",
        "sapi.retries = 1",
        "sapi.backoff = 0",
        "sapi.delay = 0",
})
public class CreditCardBalanceRetrieverTest {
    private static final String API_CC_ACCOUNTS = "/customer/1/balance";
    private static final WireMockServer wireMockServer = new WireMockServer();

    @Autowired
    private CreditCardBalanceRetriever retriever;

    @Before
    public void setup() {
        wireMockServer.start();
        configureFor("localhost", 8080);
    }

    @After
    public void tearDown() {
        verify(getRequestedFor(urlEqualTo(API_CC_ACCOUNTS)));
        wireMockServer.resetAll();
        wireMockServer.stop();
    }

    @Test
    public void itReturnsAllCreditCardsForAGivenCustomer_WhenCustomerHasCards() {
        String response = "[\n" +
                "  {\n" +
                "    \"customerId\": \"1\",\n" +
                "    \"creditCardNumber\": \"1234567890\",\n" +
                "    \"balance\": \"1234.50\"\n" +
                "  }\n" +
                "]";
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withBody(response)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        List<GenericBalance> ccbs = new ArrayList<>();
        GenericBalance ccb = new GenericBalance("CreditCardAccount", "1234567890", "1234.50");
        ccbs.add(ccb);

        assertEquals(ccbs, retriever.getCreditCardBalance("1").block());
    }

    @Test
    public void itReturnsEmptyList_WhenCustomerHasNoCards() {
        String accountsResponse = "[]";

        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withBody(accountsResponse)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        assertEquals(emptyList(), retriever.getCreditCardBalance("1").block());
    }

    @Test(expected = Exception.class)
    public void itReturnsException_WhenRequestFails() {
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withStatus(HttpStatus.SC_BAD_REQUEST)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        retriever.getCreditCardBalance("1").block();
    }

    @Test
    public void itRetriesUpTo3Times_WhenRequestFails() {
        stubFor(get(urlEqualTo(API_CC_ACCOUNTS)).willReturn(aResponse()
                .withStatus(HttpStatus.SC_BAD_REQUEST)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)));

        try {
            retriever.getCreditCardBalance("1").block();
            fail("No exception thrown");
        } catch (Exception e) {
            verify(2, getRequestedFor(urlEqualTo(API_CC_ACCOUNTS)));
        }
    }
}