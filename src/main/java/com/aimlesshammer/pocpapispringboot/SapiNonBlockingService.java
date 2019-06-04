
package com.aimlesshammer.pocpapispringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import schema.GenericBalance;

@Service
public class SapiNonBlockingService implements SapiService {

    private static final Logger logger = LoggerFactory.getLogger(SapiNonBlockingService.class);
    private CreditCardBalanceRetriever creditCardBalanceRetriever;
    private AccountBalanceRetriever accountBalanceRetriever;

    @Autowired
    public SapiNonBlockingService(CreditCardBalanceRetriever creditCardBalanceRetriever, AccountBalanceRetriever accountBalanceRetriever) {
        this.creditCardBalanceRetriever = creditCardBalanceRetriever;
        this.accountBalanceRetriever = accountBalanceRetriever;
    }

    @Override
    public Flux<GenericBalance> getBalances(String customerId) {
        Flux<GenericBalance> ccbs = creditCardBalanceRetriever.getCreditCardBalance(customerId)
                .flatMapMany(Flux::fromIterable);
        Flux<GenericBalance> cabs = accountBalanceRetriever.getCurrentAccountBalance(customerId)
                .flatMapMany(Flux::fromIterable);

        Flux<GenericBalance> mergedPublishers = ccbs.mergeWith(cabs);

        mergedPublishers.subscribe(SapiNonBlockingService::newSapiResponse);

        return mergedPublishers;
    }

    private static void newSapiResponse(GenericBalance genericBalance) {
        System.out.println("A NEW NON-BLOCKING SAPI RESPONSE HAS UPDATED !!! :O ");
    }
}
