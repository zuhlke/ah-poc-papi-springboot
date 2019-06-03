package com.aimlesshammer.pocpapispringboot.sapis;

import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CurrentAccountBalanceSapi implements Sapi<CurrentAccountBalance> {
    private static final Logger logger = LoggerFactory.getLogger(CurrentAccountBalanceSapi.class);

    private RestTemplate restTemplate;

    @Value("${sapis.currentAccountBalance.url}")
    private String currentAccountBalanceTemplate;
    @Value("${sapis.currentAccountBalance.health}")
    private String currentAccountHealth;

    public CurrentAccountBalanceSapi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    @Override
    public List<CurrentAccountBalance> getData(String customerId) {
        String currentAccountBalance = currentAccountBalanceTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CurrentAccountBalance>> currentAccountBalanceSapiResponse = restTemplate.exchange(currentAccountBalance, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CurrentAccountBalance>>() {
                });
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapiResponse.getBody();
        logger.info("Call to '{}' returned '{}' response with payload: '{}'", currentAccountBalance, currentAccountBalanceSapiResponse.getStatusCode(), currentAccountBalanceList);
        return currentAccountBalanceList;
    }

    @Override
    public Health health() {
        try {
            restTemplate.exchange(currentAccountHealth, GET, null, HealthStatus.class);
            return Health.up().build();
        } catch (HttpStatusCodeException exception) {
            return Health.down(exception)
                    .withDetail("status", exception.getRawStatusCode())
                    .build();
        }
    }
}
