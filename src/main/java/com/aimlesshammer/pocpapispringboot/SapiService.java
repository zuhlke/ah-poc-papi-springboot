package com.aimlesshammer.pocpapispringboot;

import reactor.core.publisher.Flux;
import schema.GenericBalance;

public interface SapiService {
    Flux<GenericBalance> getBalances(String customerId);
}
