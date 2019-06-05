package com.aimlesshammer.pocpapispringboot;

import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Client configuration used by all REST calls
 */
@Configuration
public
class SapiClientConf {

    private final Logger logger = LoggerFactory.getLogger(SapiClientConf.class);

    @Value("${sapi.timeout}")
    private int timeout;

    @Bean
    WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout));

        ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(clientHttpConnector)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return next.exchange(clientRequest);
        };
    }
}
