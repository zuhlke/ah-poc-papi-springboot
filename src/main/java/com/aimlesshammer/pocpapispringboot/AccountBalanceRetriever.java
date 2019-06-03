package com.aimlesshammer.pocpapispringboot;

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
import schema.CurrentAccountBalance;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountBalanceRetriever {
    private final Logger logger = LoggerFactory.getLogger(AccountBalanceRetriever.class);

    private final WebClient webClient;
    private static final String API_ERROR = "Failed to get current account balances";

    @Value("${sapi.currentAccountBalance.url}")
    private String currentAccountBalanceUrlTemplate;

    @Value("${sapi.retries}")
    private int retries;

    @Value("${sapi.backoff}")
    private int backoff;

    @Autowired
    public AccountBalanceRetriever(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<CurrentAccountBalance>> getCurrentAccountBalance(String customerId) {
        logger.info("Getting current account balance for customer {}", customerId);

        String url = expandUrl(currentAccountBalanceUrlTemplate, customerId);

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new SapiApiException(API_ERROR + ": " + response.statusCode())))
                .bodyToMono(CurrentAccountBalance[].class)
                .map(Arrays::asList)
                .retryWhen(Retry.any()
                        .fixedBackoff(Duration.ofSeconds(backoff))
                        .retryMax(retries));
    }

    private String expandUrl(String template, String... values) {
        UriTemplate uriTemplate = new UriTemplate(template);
        return uriTemplate.expand(values).toString();
    }
}

