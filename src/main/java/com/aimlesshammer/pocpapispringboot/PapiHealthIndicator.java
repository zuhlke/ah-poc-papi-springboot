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
    public Health health () {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        return (int) sapiService
            .getSapiStatuses ()
            .stream ()
            .filter (status -> ! status.is2xxSuccessful ())
            .count ();
    }

}
