
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import schema.GenericBalance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@RestClientTest(SapiBlockingService.class)
@TestPropertySource(
    properties = {
            "sapi.creditCardBalance.url=https://ah-poc-sapi-cc-bal.cfapps.io/customer/{CUSTOMER_ID}/balance",
            "sapi.currentAccountBalance.url=https://ah-poc-sapi-ca-bal.cfapps.io/customer/{CUSTOMER_ID}/balance"
    }
)
public class SapiBlockingServiceTest {

    // these are the forms that the JSON must take to be converted into the corresponding Status objects
    private static HealthStatus statusUp = new HealthStatus("UP");
    private static HealthStatus statusDown = new HealthStatus("DOWN");
    private static HealthStatus statusUnknown = new HealthStatus("UNKNOWN");
    private static HealthStatus statusOutOfService = new HealthStatus("OUT_OF_SERVICE");
    private static String statusStringUp = jsonString(statusUp);
    private static String statusStringDown = jsonString(statusDown);
    private static String statusStringUnknown = jsonString(statusUnknown);
    private static String statusStringOutOfService = jsonString(statusOutOfService);
    @Autowired
    private SapiBlockingService unit;
    @Autowired
    private MockRestServiceServer server;
    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrl;
    @Value("${sapi.currentAccountBalance.url}")
    private String currentAccountBalanceUrl;
    @Value("${sapi.creditCardBalance.health}")
    private String creditCardHealth;
    @Value("${sapi.currentAccountBalance.health}")
    private String currentAccountHealth;

    private static final String creditCardBalance = "[\n" +
        "  {\n" +
        "    \"customerId\": \"10101010\",\n" +
        "    \"creditCardNumber\": \"1234567890\",\n" +
        "    \"balance\": \"1234.50\"\n" +
        "  }\n" +
        "]";

    private static final String accountBalance = "[\n" +
        "  {\n" +
        "    \"customerId\": \"10101010\",\n" +
        "    \"accountNumber\": \"64746383648\",\n" +
        "    \"balance\": \"34.50\"\n" +
        "  }\n" +
        "]";

    @Test
    public void itGetsAccounts() {
        final String customerId = "10101010";
        this.server.expect(requestTo(creditCardBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(creditCardBalance, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountBalanceUrl.replace("{CUSTOMER_ID}", customerId))).andRespond(withSuccess(accountBalance, MediaType.APPLICATION_JSON));
        final List<GenericBalance> expected = new ArrayList<>();
        expected.add(new GenericBalance("creditCardAccount", "1234567890", "1234.50"));
        expected.add(new GenericBalance("currentAccount", "64746383648", "34.50"));
        assertEquals(expected, unit.getBalances(customerId).collectList().block());
    }

    // happy path, both 200 responses (UP, UP)
    @Test
    public void itGetsStatuses_whenBothOk() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(statusStringUp, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withSuccess(statusStringUp, MediaType.APPLICATION_JSON));
        final List<HealthStatus> expected = Arrays.asList(statusUp, statusUp);
        final List<HealthStatus> actual = unit.getStatuses();
        assertEquals(expected, actual);
    }

    private static String jsonString(HealthStatus healthStatus) {
        String jsonString = "{\"status\":\"%s\"}";
        return String.format(jsonString, healthStatus.getStatus());
    }

    // one 200, one 404 response
    @Test
    public void itGetsStatuses_whenOneNotFound() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(statusStringUp, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        final List<HealthStatus> expected = Arrays.asList(statusUp, statusOutOfService);
        final List<HealthStatus> actual = unit.getStatuses();
        assertEquals(expected, actual);
    }

    // one 200 DOWN, one 200 UP
    @Test
    public void itGetsStatuses_whenOneSapiSHealthIsDown() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(statusStringUp, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withSuccess(statusStringDown, MediaType.APPLICATION_JSON));
        final List<HealthStatus> expected = Arrays.asList(statusUp, statusDown);
        final List<HealthStatus> actual = unit.getStatuses();
        assertEquals(expected, actual);
    }

    // one 200, one 500 response
    @Test
    public void itGetsStatuses_whenOneSapiIsNotRunning() {
        this.server.expect(requestTo(creditCardHealth)).andRespond(withSuccess(statusStringUp, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(currentAccountHealth)).andRespond(withServerError());
        final List<HealthStatus> expected = Arrays.asList(statusUp, statusOutOfService);
        final List<HealthStatus> actual = unit.getStatuses();
        assertEquals(expected, actual);
    }

}
