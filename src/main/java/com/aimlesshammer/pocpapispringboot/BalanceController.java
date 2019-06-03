package com.aimlesshammer.pocpapispringboot;

import com.aimlesshammer.pocpapispringboot.model.BalanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BalanceController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private BalanceService balanceService;
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance")
    public List<BalanceRecord> greeting(@RequestParam("customer-id") String customerId) {
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Requesting balances for customer: '{}'", customerId);
        List<BalanceRecord> allBalances = balanceService.getBalances(customerId);
        logger.info(PocPapiSpringbootApplication.LOG_ID + ": Returning aggregated balances for customer: '{}', balances: '{}'", customerId, allBalances);
        return allBalances;
    }

}
