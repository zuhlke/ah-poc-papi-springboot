package com.aimlesshammer.pocpapispringboot.sapis;

import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

public abstract class Sapi<T> implements HealthIndicator {
    static final String CUSTOMER_ID_KEY = "{CUSTOMER_ID}";

    RestTemplate restTemplate;
    private String healthUrl;

    Sapi(RestTemplateBuilder restTemplateBuilder, String healthUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.healthUrl = healthUrl;
    }


    public abstract List<T> getData(String customerId);

    @Override
    public Health health() {
        try {
            restTemplate.exchange(healthUrl, GET, null, HealthStatus.class);
            return Health.up().build();
        } catch (HttpStatusCodeException exception) {
            return Health.down(exception)
                    .withDetail("status", exception.getRawStatusCode())
                    .build();
        }
    }
}
