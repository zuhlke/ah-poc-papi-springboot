package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.sapis.reactive.ReactiveCurrentAccountBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.reactive.ReactiveCreditCardBalanceSapi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import(BalanceNonBlockingService.class)
public class BalanceNonBlockingServiceTest {

    @Autowired
    private BalanceNonBlockingService balanceNonBlockingService;

    @MockBean
    private ReactiveCreditCardBalanceSapi reactiveCreditCardBalanceSapi;

    @MockBean
    private ReactiveCurrentAccountBalanceSapi reactiveCurrentAccountBalanceSapi;

    @Test
    public void itGetsAllBalances() {
        Balance ccb = new Balance("CreditCardAccount", "123", "10.5");
        when(reactiveCreditCardBalanceSapi.getCreditCardBalance("1")).thenReturn(Mono.just(Collections.singletonList(ccb)));

        Balance cab = new Balance("CurrentAccount", "123", "10.5");
        when(reactiveCurrentAccountBalanceSapi.getCurrentAccountBalance("1")).thenReturn(Mono.just(Collections.singletonList(cab)));

        Flux<Balance> balances = balanceNonBlockingService.getBalances("1");

        assertEquals(ccb, balances.blockFirst());
        assertEquals(cab, balances.blockLast());
    }
}