
package com.aimlesshammer.pocpapispringboot;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PapiHealthIndicator implements HealthIndicator {

    private SapiService sapiService;

    public PapiHealthIndicator(SapiService sapiService) {
        this.sapiService = sapiService;
    }

    @Override
    public Health health() {
        System.out.println("into health method");
        int errorCode = check();
        System.out.println("after check method");
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        System.out.println("FUCK");
        return Health.up().build();
    }

    private int check() {
        System.out.println("into check method");
        return (int) sapiService
            .getSapiStatuses()
            .stream()
            .filter(status -> !status.is2xxSuccessful())
            .count();
    }

}
