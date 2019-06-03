
package com.aimlesshammer.pocpapispringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schema.GenericBalance;

import java.util.List;

@Service
public class SapiNonBlockingService {

    private static final Logger logger = LoggerFactory.getLogger(SapiNonBlockingService.class);
    private CreditCardBalanceRetriever creditCardBalanceRetriever;
    private AccountBalanceRetriever accountBalanceRetriever;

    @Autowired
    public SapiNonBlockingService(CreditCardBalanceRetriever creditCardBalanceRetriever, AccountBalanceRetriever accountBalanceRetriever) {
        this.creditCardBalanceRetriever = creditCardBalanceRetriever;
        this.accountBalanceRetriever = accountBalanceRetriever;
    }

    public Flux<List<GenericBalance>> getBalances(String customerId) {
        Mono<List<GenericBalance>> ccbs = creditCardBalanceRetriever.getCreditCardBalance(customerId);
        Mono<List<GenericBalance>> cabs = accountBalanceRetriever.getCurrentAccountBalance(customerId);

        Flux<List<GenericBalance>> mergedPublishers = Flux.concat(ccbs, cabs);

        mergedPublishers.subscribe(SapiNonBlockingService::newSapiResponse);

        return mergedPublishers;
    }

    private static void newSapiResponse(List list) {
        System.out.println("A NEW NON-BLOCKING SAPI RESPONSE HAS UPDATED !!! :O ");
    }
}
