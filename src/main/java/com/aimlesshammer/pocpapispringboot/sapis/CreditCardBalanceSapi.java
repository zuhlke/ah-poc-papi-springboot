package com.aimlesshammer.pocpapispringboot.sapis;

import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CreditCardBalanceSapi implements Sapi<CreditCardBalance> {
    private static final Logger logger = LoggerFactory.getLogger(CreditCardBalanceSapi.class);


    private RestTemplate restTemplate;

    @Value("${sapis.creditCardBalance.url}")
    private String creditCardBalanceUrlTemplate;

    @Value("${sapis.creditCardBalance.health}")
    private String creditCardHealth;

    public CreditCardBalanceSapi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<CreditCardBalance> getData(String customerId) {
        String creditCardBalanceCustomerUrl = creditCardBalanceUrlTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CreditCardBalance>> creditCardBalanceSapiResponse = restTemplate.exchange(creditCardBalanceCustomerUrl, GET, null,
                new ParameterizedTypeReference<List<CreditCardBalance>>() {
                });

        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapiResponse.getBody();
        logger.info("Call to '{}' returned '{}' response with payload: '{}'", creditCardBalanceCustomerUrl, creditCardBalanceSapiResponse.getStatusCode(), creditCardBalanceList);
        return creditCardBalanceList;
    }

    @Override
    public Health health() {
        try {
            restTemplate.exchange(creditCardHealth, GET, null, HealthStatus.class);
            return Health.up().build();
        } catch (HttpStatusCodeException exception) {
            return Health.down(exception)
                    .withDetail("status", exception.getRawStatusCode())
                    .build();
        }
    }

}
