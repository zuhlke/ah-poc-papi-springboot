package com.aimlesshammer.pocpapispringboot.sapis;

import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.List;

public interface Sapi<T> extends HealthIndicator {
    String CUSTOMER_ID_KEY = "{CUSTOMER_ID}";

    List<T> getData(String customerId);
}
