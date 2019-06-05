package com.aimlesshammer.pocpapispringboot;

import reactor.core.publisher.Flux;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

public interface SapiService {
    Flux<Balance> getBalances(String customerId);
}
