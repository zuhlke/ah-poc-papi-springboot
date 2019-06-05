package com.aimlesshammer.pocpapispringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import com.aimlesshammer.pocpapispringboot.model.reactive.Balance;

import java.util.List;

@RestController
public class BalanceController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private BalanceBlockingService balanceBlockingService;
    private BalanceNonBlockingService balanceNonBlockingService;

    public BalanceController(BalanceBlockingService balanceBlockingService, BalanceNonBlockingService balanceNonBlockingService) {
        this.balanceBlockingService = balanceBlockingService;
        this.balanceNonBlockingService = balanceNonBlockingService;
    }

    @GetMapping("/balance")
    public List<Balance> getBalances(@RequestParam("customer-id") String customerId) {
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Requesting balances for customer: '{}'", customerId);
        List<Balance> allBalances = balanceBlockingService.getBalances(customerId);
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Returning aggregated balances for customer: '{}', balances: '{}'", customerId, allBalances);
        return allBalances;
    }


    @GetMapping("/reactive-balance")
    public Flux<Balance> getReactiveBalances(@RequestParam("customer-id") String customerId) {
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Requesting balances for customer: '{}'", customerId);
        return balanceNonBlockingService.getBalances(customerId);
    }
}
