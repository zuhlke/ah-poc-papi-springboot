package com.aimlesshammer.pocpapispringboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
    private SapiService sapiService;

    @Test
    public void itGetAllBalancesFromSapiService() throws Exception {
        when(sapiService.getAllBalances("1")).thenReturn(Collections.emptyList());

        this.mvc.perform(get("/balance").param("customer-id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }
}