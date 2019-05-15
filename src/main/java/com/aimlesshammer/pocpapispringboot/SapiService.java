package com.aimlesshammer.pocpapispringboot;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class SapiService {

    private RestTemplate restTemplate;

    public SapiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<BalanceRecord> getAllBalances(String customerId) {
        String creditCardBalanceUrl = "https://ah-poc-sapi-cc-bal.cfapps.io/balance?customerId=" + customerId;
        String accountBalanceUrl = "https://ah-poc-sapi-ca-bal.cfapps.io/balance?customerId=" + customerId;

        ResponseEntity<List<CreditCardBalance>> creditCardBalanceSapiResponse = restTemplate.exchange(creditCardBalanceUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CreditCardBalance>>() {});
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapiResponse.getBody();

        ResponseEntity<List<CurrentAccountBalance>> currentAccountBalanceSapiResponse = restTemplate.exchange(accountBalanceUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CurrentAccountBalance>>() {});
        List<CurrentAccountBalance> currentAccountBalanceList = currentAccountBalanceSapiResponse.getBody();


        List<BalanceRecord> ccBalance = creditCardBalanceList.stream()
                .map(cc -> new BalanceRecord("creditCardAccount", cc.getCreditCardNumber(), cc.getBalance()))
                .collect(toList());
        List<BalanceRecord> acBalance = currentAccountBalanceList.stream()
                .map(cc -> new BalanceRecord("currentAccount", cc.getAccountNumber(), cc.getBalance()))
                .collect(toList());

        return Stream.concat(ccBalance.stream(), acBalance.stream())
                .collect(Collectors.toList());
    }
}
