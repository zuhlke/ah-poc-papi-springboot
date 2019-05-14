package com.aimlesshammer.pocpapispringboot;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class SpringHttpClient {
    private HttpEntity<Object> DEFAULT_HEADERS = new HttpEntity<>(new HttpHeaders());
    private Class<String> STRING_RESPONSE_TYPE = String.class;

    public String get(String requestUrl) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                DEFAULT_HEADERS,
                STRING_RESPONSE_TYPE
        ).getBody();
    }
}
