package com.aimlesshammer.pocpapispringboot.sapis;

import com.aimlesshammer.pocpapispringboot.model.CurrentAccountBalance;
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
public class CurrentAccountBalanceSapi extends Sapi<CurrentAccountBalance> {
    private static final Logger logger = LoggerFactory.getLogger(CurrentAccountBalanceSapi.class);

    private final String dataUrlTemplate;

    public CurrentAccountBalanceSapi(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${sapis.currentAccountBalance.url}") String dataUrlTemplate,
            @Value("${sapis.currentAccountBalance.health}") String healthUrl) {
        super(restTemplateBuilder, healthUrl);
        this.dataUrlTemplate = dataUrlTemplate;
    }

    @Override
    public List<CurrentAccountBalance> getData(String customerId){
        String resolvedDataUrl = dataUrlTemplate.replace(CUSTOMER_ID_KEY, customerId);
        ResponseEntity<List<CurrentAccountBalance>> sapiDataResponse = restTemplate.exchange(resolvedDataUrl, GET, null,
                new ParameterizedTypeReference<List<CurrentAccountBalance>>() {
                });


        List<CurrentAccountBalance> sapiDataResponseList= sapiDataResponse.getBody();
        logger.info("Call to '{}' returned '{}' response with payload: '{}'", resolvedDataUrl, sapiDataResponse.getStatusCode(), sapiDataResponseList);
        return sapiDataResponseList;
    }
}
