package com.aimlesshammer.pocpapispringboot;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Pact tutorial: https://www.baeldung.com/pact-junit-consumer-driven-contracts
 * Reactive web tests: https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/
 * Pact JVM examples: https://github.com/Mikuu/Pact-JVM-Example
 * Pact Broker: https://github.com/pact-foundation/pact_broker
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class SapiCCContractTest {

    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrl;

    @Value("${wiremock.server.port}")
    private int port;
    /*
     * This setups Wiremock to mock our provider
     */
    @Rule
    public PactProviderRuleMk2 mockSapiCC
            = new PactProviderRuleMk2("SAPI_CC", creditCardBalanceUrl, port, this);
    @Autowired
    private WebTestClient webTestClient;

    /*
     * This builds the contract and adds it to /target/pacts
     */
    @Pact(consumer = "balances_papi")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("A GET request for a balance")
                .uponReceiving("a customer ID")
                .path("/customer/10101010/balance")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body("[{\"customerId\":\"10101010\",\"creditCardNumber\":\"1234567890\",\"balance\":\"1234.50\"}]")
                .toPact();
    }

    /*
    This verifies the contract against the Pact mockSapiCC
     */
    @Test
    @PactVerification()
    public void givenGet_whenSendRequest_shouldReturn200WithProperHeaderAndBody() {
        webTestClient.get()
                .uri(mockSapiCC.getUrl() + "/customer/10101010/balance")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("[{\"customerId\":\"10101010\",\"creditCardNumber\":\"1234567890\",\"balance\":\"1234.50\"}]");
    }
}
