package com.aimlesshammer.pocpapispringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.boot.actuate.health.Status.UP;

@Configuration
public class CustomMetricsConfiguration {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Bean
    MeterRegistryCustomizer prometheusHealthCheck() {
        return registry -> registry
                 .gauge("health", healthEndpoint, CustomMetricsConfiguration::healthToCode);
    }

    private static int healthToCode(HealthEndpoint ep) {
        Status status = ep.health().getStatus();

        return status.equals(UP) ? 1 : 0;
    }
}
