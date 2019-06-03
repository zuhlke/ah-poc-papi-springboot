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
import schema.CreditCardBalance;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class CreditCardBalanceRetriever {
    private final Logger logger = LoggerFactory.getLogger(CreditCardBalanceRetriever.class);

    private final WebClient webClient;
    private static final String API_ERROR = "Failed to get credit card balances";

    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrlTemplate;

    @Value("${sapi.retries}")
    private int retries;

    @Value("${sapi.backoff}")
    private int backoff;

    @Autowired
    public CreditCardBalanceRetriever(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<CreditCardBalance>> getCreditCardBalance(String customerId) {
        logger.info("Getting credit card balances for customer {}", customerId);

        String url = expandUrl(creditCardBalanceUrlTemplate, customerId);

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> Mono.error(new SapiApiException(API_ERROR + ": " + response.statusCode())))
                .bodyToMono(CreditCardBalance[].class)
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

