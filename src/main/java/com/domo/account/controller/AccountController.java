package com.domo.account.controller;

import com.domo.account.domain.Account;
import com.domo.account.service.AccountService;
import com.domo.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @GetMapping("get-lock")
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("create-account")
    public String createAccount(){
        accountService.createAccount();
        return "success";
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
