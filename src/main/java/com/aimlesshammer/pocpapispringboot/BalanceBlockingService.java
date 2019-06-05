
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.blocking.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.blocking.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.sapis.blocking.CreditCardBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.blocking.CurrentAccountBalanceSapi;
import org.springframework.stereotype.Service;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class BalanceBlockingService {

    private CreditCardBalanceSapi creditCardBalanceSapi;
    private CurrentAccountBalanceSapi currentAccountBalanceSapi;

    public BalanceBlockingService(CreditCardBalanceSapi creditCardBalanceSapi,
                                  CurrentAccountBalanceSapi currentAccountBalanceSapi) {
        this.creditCardBalanceSapi = creditCardBalanceSapi;
        this.currentAccountBalanceSapi = currentAccountBalanceSapi;
    }

    List<Balance> getBalances(String customerId) {
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapi.getData(customerId);
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapi.getData(customerId);

        List<Balance> ccBalance = creditCardBalanceList.stream()
                .map(cc -> new Balance("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
                .collect(toList());
        List<Balance> acBalance = currentAccountBalanceList.stream()
                .map(cc -> new Balance("currentAccount", cc.getAccountNumber(), cc.getBalance()))
                .collect(toList());

        return Stream.concat(ccBalance.stream(), acBalance.stream())
                .collect(Collectors.toList());
    }
}
