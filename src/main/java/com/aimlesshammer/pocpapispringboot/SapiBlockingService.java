
package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import com.aimlesshammer.pocpapispringboot.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import schema.GenericBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class SapiBlockingService implements SapiService {

    private static final Logger logger = LoggerFactory.getLogger(SapiBlockingService.class);
    private static final String CUSTOMER_ID_KEY = "{CUSTOMER_ID}";
    private RestTemplate restTemplate;

    @Value("${sapi.creditCardBalance.url}")
    private String creditCardBalanceUrlTemplate;
    @Value("${sapi.currentAccountBalance.url}")
    private String currentAccountBalanceTemplate;
    @Value("${sapi.creditCardBalance.health}")
    private String creditCardHealth;
    @Value("${sapi.currentAccountBalance.health}")
    private String currentAccountHealth;

    public SapiBlockingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Flux<GenericBalance> getBalances(String customerId) {
        String creditCardBalanceCustomerUrl = creditCardBalanceUrlTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CreditCardBalance>> creditCardBalanceSapiResponse = restTemplate.exchange(creditCardBalanceCustomerUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CreditCardBalance>>() {
                });
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapiResponse.getBody();
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Call to '{}' returned '{}' response with payload: '{}'", creditCardBalanceCustomerUrl, creditCardBalanceSapiResponse.getStatusCode(), creditCardBalanceList);

        String currentAccountBalance = currentAccountBalanceTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CurrentAccountBalance>> currentAccountBalanceSapiResponse = restTemplate.exchange(currentAccountBalance, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CurrentAccountBalance>>() {
                });
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapiResponse.getBody();
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Call to '{}' returned '{}' response with payload: '{}'", currentAccountBalance, currentAccountBalanceSapiResponse.getStatusCode(), currentAccountBalanceList);

        List<GenericBalance> ccBalance = creditCardBalanceList.stream()
                .map(cc -> new GenericBalance("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
                .collect(toList());
        List<GenericBalance> acBalance = currentAccountBalanceList.stream()
                .map(cc -> new GenericBalance("currentAccount", cc.getAccountNumber(), cc.getBalance()))
                .collect(toList());

        List<GenericBalance> mergedBalancesList = Stream.concat(ccBalance.stream(), acBalance.stream())
                .collect(toList());

        return Flux.fromIterable(mergedBalancesList);
    }

    List<HealthStatus> getStatuses() {
        List<HealthStatus> statuses = new ArrayList<>();
        statuses.add(getStatus(creditCardHealth));
        statuses.add(getStatus(currentAccountHealth));
        logger.info("statuses: " + statuses);
        return statuses;
    }

    private HealthStatus getStatus(String urlString) {
        try {
            ResponseEntity<HealthStatus> entity = restTemplate.exchange(urlString, HttpMethod.GET, null, HealthStatus.class);
            if (entity.getStatusCode().is2xxSuccessful()) {
                return entity.getBody();
            } else {
                return new HealthStatus("UNKNOWN");
            }
        } catch (HttpStatusCodeException exception) {
            return new HealthStatus("OUT_OF_SERVICE");
        }
    }

}
