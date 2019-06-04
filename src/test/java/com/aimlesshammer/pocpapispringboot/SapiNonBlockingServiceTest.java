package com.aimlesshammer.pocpapispringboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schema.GenericBalance;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import(SapiNonBlockingService.class)
public class SapiNonBlockingServiceTest {

    @Autowired
    private SapiNonBlockingService sapiNonBlockingService;

    @MockBean
    private CreditCardBalanceRetriever creditCardBalanceRetriever;

    @MockBean
    private AccountBalanceRetriever accountBalanceRetriever;

    @Test
    public void itGetsAllBalances() {
        GenericBalance ccb = new GenericBalance("CreditCardAccount", "123", "10.5");
        when(creditCardBalanceRetriever.getCreditCardBalance("1")).thenReturn(Mono.just(Collections.singletonList(ccb)));

        GenericBalance cab = new GenericBalance("CurrentAccount", "123", "10.5");
        when(accountBalanceRetriever.getCurrentAccountBalance("1")).thenReturn(Mono.just(Collections.singletonList(cab)));

        Flux<GenericBalance> balances = sapiNonBlockingService.getBalances("1");

        assertEquals(ccb, balances.blockFirst());
        assertEquals(cab, balances.blockLast());
    }
}