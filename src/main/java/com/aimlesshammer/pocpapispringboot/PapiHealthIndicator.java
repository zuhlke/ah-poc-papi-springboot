
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PapiHealthIndicator implements HealthIndicator {

    private SapiBlockingService sapiBlockingService;

    public PapiHealthIndicator(SapiBlockingService sapiBlockingService) {
        this.sapiBlockingService = sapiBlockingService;
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
        return (int) sapiBlockingService
            .getStatuses()
            .stream()
            .filter(status -> !status.equals(new HealthStatus("UP")))
            .count();
    }

}
