package com.domo.account.controller;

import com.domo.account.domain.Account;
import com.domo.account.dto.AccountDto;
import com.domo.account.dto.CreateAccount;
import com.domo.account.service.AccountService;
import com.domo.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;


    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ){
        return CreateAccount.Response.from(accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance()));
    }
    @GetMapping("get-lock")
    public String getLock() {
        return redisTestService.getLock();
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
