package com.domo.account.controller;

import com.domo.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;

}
