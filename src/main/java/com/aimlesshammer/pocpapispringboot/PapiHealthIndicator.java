
package com.aimlesshammer.pocpapispringboot;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class PapiHealthIndicator implements HealthIndicator {

    private SapiService sapiService;

    public PapiHealthIndicator(SapiService sapiService) {
        this.sapiService = sapiService;
    }

    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        System.out.println("In check");
        return (int) sapiService
            .getStatuses()
            .stream()
            .filter(status -> !status.equals(Status.UP))
            .count();
    }

}
