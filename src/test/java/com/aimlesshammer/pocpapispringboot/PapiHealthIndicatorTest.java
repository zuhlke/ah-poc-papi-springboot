
package com.aimlesshammer.pocpapispringboot;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PapiHealthIndicatorTest {

    @Test
    public void testHappyPath() {
        SapiService service = Mockito.mock (SapiService.class);
        when(service.getSapiStatuses ()).thenReturn (Arrays.asList (HttpStatus.OK, HttpStatus.OK));
        final PapiHealthIndicator papiHealthIndicator = new PapiHealthIndicator(service);
        Health health = papiHealthIndicator.health ();
        assertEquals( Status.UP , health.getStatus() );
    }

    @Test
    public void testPapiOutOfService_WhenOneSapiDown() {
        SapiService service = Mockito.mock (SapiService.class);
        when(service.getSapiStatuses ()).thenReturn (Arrays.asList (HttpStatus.I_AM_A_TEAPOT, HttpStatus.OK));
        final PapiHealthIndicator papiHealthIndicator = new PapiHealthIndicator(service);
        Health health = papiHealthIndicator.health ();
        assertEquals (Status.DOWN, health.getStatus ());
    }

}
