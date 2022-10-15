package com.domo.account.controller;

import com.domo.account.dto.UseBalance;
import com.domo.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1.잔액 사용
 * 2.잔액 사용 취소
 * 3.거래 확
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("transaction/use")
    public UseBalance.Response useBalance(
        @Valid @RequestBody UseBalance.Request request
    ){


        return null;
    }
}
