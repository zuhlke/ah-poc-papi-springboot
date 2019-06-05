package com.aimlesshammer.pocpapispringboot.sapis.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;
import com.aimlesshammer.pocpapispringboot.model.reactive.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ReactiveCurrentAccountBalanceSapi {
    private final Logger logger = LoggerFactory.getLogger(ReactiveCurrentAccountBalanceSapi.class);

    private final WebClient webClient;
    private static final String API_ERROR = "Failed to get current account balances";

    @Value("${sapi.currentAccountBalance.url}")
    private String currentAccountBalanceUrlTemplate;

    @Value("${sapi.retries}")
    private int retries;

    @Value("${sapi.backoff}")
    private int backoff;

    @Autowired
    public ReactiveCurrentAccountBalanceSapi(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<Balance>> getCurrentAccountBalance(String customerId) {
        logger.info("Getting current account balance for customer {}", customerId);

        String url = expandUrl(currentAccountBalanceUrlTemplate, customerId);

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new SapiApiException(API_ERROR + ": " + response.statusCode())))
                .bodyToMono(CurrentAccountBalance[].class)
                .map(Arrays::asList)
                .map(List::stream)
                .map(ccb -> ccb.map(cc -> new Balance("CurrentAccount", cc.getAccountNumber(), cc.getBalance()))
                        .collect(toList()))
                .retryWhen(Retry.any()
                        .fixedBackoff(Duration.ofSeconds(backoff))
                        .retryMax(retries));
    }

    private String expandUrl(String template, String... values) {
        UriTemplate uriTemplate = new UriTemplate(template);
        return uriTemplate.expand(values).toString();
    }
}

