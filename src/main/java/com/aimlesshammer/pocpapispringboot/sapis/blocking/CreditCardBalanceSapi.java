package com.aimlesshammer.pocpapispringboot.sapis.blocking;

import com.aimlesshammer.pocpapispringboot.model.blocking.CreditCardBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class CreditCardBalanceSapi extends Sapi<CreditCardBalance> {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardBalanceSapi.class);

    private final String dataUrlTemplate;

    public CreditCardBalanceSapi(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${sapi.creditCardBalance.url}") String dataUrlTemplate,
            @Value("${sapi.creditCardBalance.health}") String healthUrl) {
        super(restTemplateBuilder, healthUrl);
        this.dataUrlTemplate = dataUrlTemplate;
    }

    @Override
    public List<CreditCardBalance> getData(String customerId){
        String resolvedDataUrl = dataUrlTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CreditCardBalance>> sapiDataResponse = restTemplate.exchange(resolvedDataUrl, GET, null,
                new ParameterizedTypeReference<List<CreditCardBalance>>() {
                });


        List<CreditCardBalance> sapiDataResponseList= sapiDataResponse.getBody();
        logger.info("Call to '{}' returned '{}' response with payload: '{}'", resolvedDataUrl, sapiDataResponse.getStatusCode(), sapiDataResponseList);
        return sapiDataResponseList;
    }
}
