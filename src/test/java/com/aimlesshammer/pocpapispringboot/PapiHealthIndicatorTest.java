
package com.aimlesshammer.pocpapispringboot;

import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PapiHealthIndicatorTest {

    @Test
    public void testHappyPath() {
        SapiService service = Mockito.mock(SapiService.class);
        when(service.getStatuses()).thenReturn(Arrays.asList(Status.UP, Status.UP));
        final PapiHealthIndicator unit = new PapiHealthIndicator(service);
        Health health = unit.health();
        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    public void testPapiOutOfService_WhenOneSapiDown() {
        SapiService service = Mockito.mock(SapiService.class);
        when(service.getStatuses()).thenReturn(Arrays.asList(Status.UP, Status.DOWN));
        final PapiHealthIndicator unit = new PapiHealthIndicator(service);
        Health health = unit.health();
        assertEquals(Status.DOWN, health.getStatus());
    }

}
