package com.aimlesshammer.pocpapispringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import schema.GenericBalance;

import java.util.List;

@RestController
public class BalanceController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private SapiBlockingService sapiBlockingService;
    private SapiNonBlockingService sapiNonBlockingService;

    public BalanceController(SapiBlockingService sapiBlockingService, SapiNonBlockingService sapiNonBlockingService) {
        this.sapiBlockingService = sapiBlockingService;
        this.sapiNonBlockingService = sapiNonBlockingService;
    }

    @GetMapping("/balance")
    public List<GenericBalance> getBalances(@RequestParam("customer-id") String customerId) {
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Requesting balances for customer: '{}'", customerId);
        List<GenericBalance> allBalances = sapiBlockingService.getBalances(customerId);
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Returning aggregated balances for customer: '{}', balances: '{}'", customerId, allBalances);
        return allBalances;
    }


    @GetMapping("/reactive-balance")
    public Flux<GenericBalance> getReactiveBalances(@RequestParam("customer-id") String customerId) {
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Requesting balances for customer: '{}'", customerId);
        return sapiNonBlockingService.getBalances(customerId);
    }
}
