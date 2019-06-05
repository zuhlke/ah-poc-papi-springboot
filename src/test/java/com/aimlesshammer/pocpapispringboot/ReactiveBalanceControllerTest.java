package com.aimlesshammer.pocpapispringboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReactiveBalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BalanceNonBlockingService balanceNonBlockingService;

    @Test
    public void itGetAllBalancesFromReactiveSapiService() throws Exception {
        Flux<Balance> stubbedFlux = Flux.just(
                new Balance("CreditCardAccount", "1", "10.5"),
                new Balance("CurrentAccount", "2", "11.5"),
                new Balance("CurrentAccount", "3", "12.5")
        );
        Mockito.when(balanceNonBlockingService.getBalances("1")).thenReturn(stubbedFlux);

        String jsonBlob = new ObjectMapper().writeValueAsString(stubbedFlux.collectList().block());
        webTestClient.get().uri("http://localhost:8080/reactive-balance?customer-id=1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(jsonBlob);
    }
}
