package com.aimlesshammer.pocpapispringboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class SapiService {

    private RestTemplate restTemplate;

    @Value( "${sapis.creditCardBalance.url}" )
    private String creditCardBalanceUrl;
    @Value( "${sapis.currentAccountBalance.url}" )
    private String accountBalanceUrl;

    public SapiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<BalanceRecord> getAllBalances(String customerId) {
        ResponseEntity<List<CreditCardBalance>> creditCardBalanceSapiResponse = restTemplate.exchange(creditCardBalanceUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CreditCardBalance>>() {}, customerId);
        List<CreditCardBalance> creditCardBalanceList = creditCardBalanceSapiResponse.getBody();

        ResponseEntity<List<CurrentAccountBalance>> currentAccountBalanceSapiResponse = restTemplate.exchange(accountBalanceUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CurrentAccountBalance>>() {}, customerId);
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
