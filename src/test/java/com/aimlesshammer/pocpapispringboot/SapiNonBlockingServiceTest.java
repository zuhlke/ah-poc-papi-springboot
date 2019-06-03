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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import(SapiNonBlockingService.class)
public class SapiNonBlockingServiceTest {

    @Autowired
    SapiNonBlockingService sapiNonBlockingService;

    @MockBean
    CreditCardBalanceRetriever creditCardBalanceRetriever;

    @MockBean
    AccountBalanceRetriever accountBalanceRetriever;

    @Test
    public void itGetsAllBalances() {
        List<GenericBalance> ccbs = new ArrayList<>();
        GenericBalance ccb = new GenericBalance("CreditCardAccount", "123", "10.5");
        ccbs.add(ccb);
        when(creditCardBalanceRetriever.getCreditCardBalance("1")).thenReturn(Mono.just(ccbs));

        List<GenericBalance> cabs = new ArrayList<>();
        GenericBalance cab = new GenericBalance("CurrentAccount", "123", "10.5");
        cabs.add(cab);
        when(accountBalanceRetriever.getCurrentAccountBalance("1")).thenReturn(Mono.just(cabs));

        List<GenericBalance> expected1 = new ArrayList<>();
        expected1.add(ccb);

        List<GenericBalance> expected2 = new ArrayList<>();
        expected2.add(ccb);

        assertEquals(Flux.concat(Mono.just(expected1), Mono.just(expected2)), sapiNonBlockingService.getBalances("1"));
    }
}