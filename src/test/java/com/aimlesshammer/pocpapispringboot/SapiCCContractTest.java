package com.aimlesshammer.pocpapispringboot;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactTestExecutionContext;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
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
public class SapiCCContractTest extends ConsumerPactTestMk2 {

    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrl;

    @Value("${wiremock.server.port}")
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Override
    protected String providerName() {
        return "SAPI_CC";
    }

    @Override
    protected String consumerName() {
        return "PAPI_BALANCES";
    }

    @Override
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

    @Override
    protected void runTest(MockServer mockServer, PactTestExecutionContext pactTestExecutionContext) throws IOException {
        webTestClient.get()
                .uri(mockServer.getUrl() + "/customer/10101010/balance")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("[{\"customerId\":\"10101010\",\"creditCardNumber\":\"1234567890\",\"balance\":\"1234.50\"}]");
    }
}
