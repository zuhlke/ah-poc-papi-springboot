
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
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
        System.out.println("in health");
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
            .filter(status -> !status.equals(new HealthStatus("UP")))
            .count();
    }

}
