package com.aimlesshammer.pocpapispringboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import schema.GenericBalance;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BalanceController.class)
public class BalanceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SapiBlockingService sapiBlockingService;

    @MockBean
    private SapiNonBlockingService sapiNonBlockingService;

    @Test
    public void itGetAllBalancesFromSapiService() throws Exception {
        when(sapiBlockingService.getBalances("1")).thenReturn(Collections.emptyList());

        this.mvc.perform(get("/balance").param("customer-id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }

    @Test
    public void itGetAllBalancesFromReactiveSapiService() throws Exception {
        Flux<GenericBalance> expected = Flux.just(
                new GenericBalance("CreditCardAccount", "1", "10.5"),
                new GenericBalance("CurrentAccount", "2", "11.5"),
                new GenericBalance("CurrentAccount", "3", "12.5")
        );

        when(sapiNonBlockingService.getBalances("1")).thenReturn(expected);


        this.mvc.perform(get("/reactive-balance").param("customer-id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }
}