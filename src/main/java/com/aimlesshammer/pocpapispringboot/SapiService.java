package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.BalanceRecord;
import com.aimlesshammer.pocpapispringboot.model.CreditCardBalance;
import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class SapiService {
    private static final Logger logger = LoggerFactory.getLogger(SapiService.class);
    private static final String CUSTOMER_ID_KEY = "{CUSTOMER_ID}";

    private RestTemplate restTemplate;

    @Value("${sapis.creditCardBalance.url}")
    private String creditCardBalanceUrlTemplate;
    @Value("${sapis.currentAccountBalance.url}")
    private String currentAccountBalanceTemplate;
    @Value("${sapis.creditCardBalance.health}")
    private String creditCardHealth;
    @Value("${sapis.creditCardBalance.health}")
    private String currentAccountHealth;

    public SapiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    List<BalanceRecord> getAllBalances(String customerId) {
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


        List<BalanceRecord> ccBalance = creditCardBalanceList.stream()
            .map(cc -> new BalanceRecord("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
            .collect(toList());
        List<BalanceRecord> acBalance = currentAccountBalanceList.stream()
            .map(cc -> new BalanceRecord("currentAccount", cc.getAccountNumber(), cc.getBalance()))
            .collect(toList());

        return Stream.concat(ccBalance.stream(), acBalance.stream())
            .collect(Collectors.toList());
    }

    List<HttpStatus> getSapiStatuses() {
        List<HttpStatus> sapiStatuses = new ArrayList<>();
        System.out.println("into getSapiStatuses");
        try {
            System.out.println("into first try-block");
            ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
            System.out.println("before first rest call");
            ResponseEntity<String> ccEntity = restTemplate.exchange(creditCardHealth, HttpMethod.GET, null, reference);
            System.out.println("after first rest call");
            sapiStatuses.add(ccEntity.getStatusCode());
            logger.info("CC health retrieved");
        } catch (HttpStatusCodeException exception) {
            sapiStatuses.add(exception.getStatusCode());
            logger.info("CC health retrieved not");
        }
        try {
            ResponseEntity<Health> caEntity = restTemplate.exchange(currentAccountHealth, HttpMethod.GET, null, new ParameterizedTypeReference<Health>() {});
            sapiStatuses.add(caEntity.getStatusCode());
            logger.info("CA health retrieved");
        } catch (HttpStatusCodeException exception) {
            sapiStatuses.add(exception.getStatusCode());
            logger.info("CA health retrieved not");
        }
        return sapiStatuses;
    }

}
