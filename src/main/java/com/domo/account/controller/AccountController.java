package com.domo.account.controller;

import com.domo.account.service.AccountService;
import com.domo.account.service.RedisServiceTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final RedisServiceTest redisServiceTest;

    @GetMapping("get-lock")
    public String getLock(){
        return redisServiceTest.getLock();
    }
}
