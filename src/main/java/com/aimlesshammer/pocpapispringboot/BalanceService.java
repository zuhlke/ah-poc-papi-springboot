
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.sapis.CreditCardBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.CurrentAccountBalanceSapi;
import org.springframework.stereotype.Service;
import schema.GenericBalance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class BalanceService {

    private CreditCardBalanceSapi creditCardBalanceSapi;
    private CurrentAccountBalanceSapi currentAccountBalanceSapi;

    public BalanceService(CreditCardBalanceSapi creditCardBalanceSapi,
                          CurrentAccountBalanceSapi currentAccountBalanceSapi) {
        this.creditCardBalanceSapi = creditCardBalanceSapi;
        this.currentAccountBalanceSapi = currentAccountBalanceSapi;
    }

    List<GenericBalance> getBalances(String customerId) {
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapi.getData(customerId);
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapi.getData(customerId);

        List<GenericBalance> ccBalance = creditCardBalanceList.stream()
                .map(cc -> new GenericBalance("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
                .collect(toList());
        List<GenericBalance> acBalance = currentAccountBalanceList.stream()
                .map(cc -> new GenericBalance("currentAccount", cc.getAccountNumber(), cc.getBalance()))
                .collect(toList());

        return Stream.concat(ccBalance.stream(), acBalance.stream())
                .collect(Collectors.toList());
    }
}
