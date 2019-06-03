
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.BalanceRecord;
import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.sapis.CreditCardBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.CurrentAccountBalanceSapi;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class BalanceService {

    private RestTemplate restTemplate;
    private CreditCardBalanceSapi creditCardBalanceSapi;
    private CurrentAccountBalanceSapi currentAccountBalanceSapi;

    public BalanceService(RestTemplateBuilder restTemplateBuilder,
                          CreditCardBalanceSapi creditCardBalanceSapi,
                          CurrentAccountBalanceSapi currentAccountBalanceSapi) {
        this.restTemplate = restTemplateBuilder.build();
        this.creditCardBalanceSapi = creditCardBalanceSapi;
        this.currentAccountBalanceSapi = currentAccountBalanceSapi;
    }

    List<BalanceRecord> getBalances(String customerId) {
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapi.getData(customerId);
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapi.getData(customerId);

        List<BalanceRecord> ccBalance = creditCardBalanceList.stream()
            .map(cc -> new BalanceRecord("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
            .collect(toList());
        List<BalanceRecord> acBalance = currentAccountBalanceList.stream()
            .map(cc -> new BalanceRecord("currentAccount", cc.getAccountNumber(), cc.getBalance()))
            .collect(toList());

        return Stream.concat(ccBalance.stream(), acBalance.stream())
            .collect(Collectors.toList());
    }
}
