package com.aimlesshammer.pocpapispringboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class BalanceController {

    private SapiService sapiService;

    public BalanceController(SapiService sapiService) {
        this.sapiService = sapiService;
    }

    @GetMapping("/balance")
    public List<BalanceRecord> greeting(@RequestParam("customer-id") String userId) {
        return sapiService.getAllBalances(userId);
    }
}
