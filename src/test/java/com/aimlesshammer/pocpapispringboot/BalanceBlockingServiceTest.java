
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.sapis.CreditCardBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.CurrentAccountBalanceSapi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import schema.GenericBalance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@RestClientTest(BalanceBlockingService.class)
public class BalanceBlockingServiceTest {

    @MockBean
    private CreditCardBalanceSapi creditCardBalanceSapi;
    @MockBean
    private CurrentAccountBalanceSapi currentAccountBalanceSapi;
    @Autowired
    private BalanceBlockingService unit;
    @Autowired
    private MockRestServiceServer server;

    @Test
    public void itGetsAccounts() {
        String customerId = "10101010";
        String creditCardNumber = "1234567890";
        String creditCardBalance = "1234.50";
        String currentAccountNumber = "64746383648";
        String currentAccountBalance = "34.50";
        when(creditCardBalanceSapi.getData(customerId)).thenReturn(Collections.singletonList(new CreditCardBalance(customerId, creditCardNumber, creditCardBalance)));
        when(currentAccountBalanceSapi.getData(customerId)).thenReturn(Collections.singletonList(new CurrentAccountBalance(customerId, currentAccountNumber, currentAccountBalance)));

        List<GenericBalance> expected = new ArrayList<>();
        expected.add(new GenericBalance("creditCardAccount", creditCardNumber, creditCardBalance));
        expected.add(new GenericBalance("currentAccount", currentAccountNumber, currentAccountBalance));
        assertEquals(expected, unit.getBalances(customerId));
    }
}
