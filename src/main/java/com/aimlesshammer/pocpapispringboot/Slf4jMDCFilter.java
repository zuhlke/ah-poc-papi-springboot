package com.aimlesshammer.pocpapispringboot;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class Slf4jMDCFilter extends OncePerRequestFilter {

    private final String mdcKey;

    Slf4jMDCFilter(String mdcKey) {
        this.mdcKey = mdcKey;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws java.io.IOException, ServletException {
        try {
            String requestCorrelationHeaderValue = request.getHeader(mdcKey);
            if(StringUtils.isEmpty(requestCorrelationHeaderValue)){
                String generatedRequestCorrelationId = UUID.randomUUID().toString();
                MDC.put(mdcKey, getMdcValueFormat(generatedRequestCorrelationId));
                response.setHeader(mdcKey, generatedRequestCorrelationId);
            }else {
                MDC.put(mdcKey, getMdcValueFormat(requestCorrelationHeaderValue));
                response.setHeader(mdcKey, requestCorrelationHeaderValue);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(mdcKey);
        }
    }

    private String getMdcValueFormat(String value) {
        return String.format("%s: '%s'", mdcKey, value);
    }

}