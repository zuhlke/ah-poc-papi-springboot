
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.sapis.reactive.ReactiveCurrentAccountBalanceSapi;
import com.aimlesshammer.pocpapispringboot.sapis.reactive.ReactiveCreditCardBalanceSapi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

@Service
public class BalanceNonBlockingService implements SapiService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceNonBlockingService.class);
    private ReactiveCreditCardBalanceSapi reactiveCreditCardBalanceSapi;
    private ReactiveCurrentAccountBalanceSapi reactiveCurrentAccountBalanceSapi;

    @Autowired
    public BalanceNonBlockingService(ReactiveCreditCardBalanceSapi reactiveCreditCardBalanceSapi, ReactiveCurrentAccountBalanceSapi reactiveCurrentAccountBalanceSapi) {
        this.reactiveCreditCardBalanceSapi = reactiveCreditCardBalanceSapi;
        this.reactiveCurrentAccountBalanceSapi = reactiveCurrentAccountBalanceSapi;
    }

    @Override
    public Flux<Balance> getBalances(String customerId) {
        Flux<Balance> ccbs = reactiveCreditCardBalanceSapi.getCreditCardBalance(customerId)
                .flatMapMany(Flux::fromIterable);
        Flux<Balance> cabs = reactiveCurrentAccountBalanceSapi.getCurrentAccountBalance(customerId)
                .flatMapMany(Flux::fromIterable);

        Flux<Balance> mergedPublishers = ccbs.mergeWith(cabs);

        mergedPublishers.subscribe(BalanceNonBlockingService::newSapiResponse);

        return mergedPublishers;
    }

    private static void newSapiResponse(Balance balance) {
        logger.info("A NEW NON-BLOCKING SAPI RESPONSE HAS UPDATED !!! :O - {}", balance);
    }
}
