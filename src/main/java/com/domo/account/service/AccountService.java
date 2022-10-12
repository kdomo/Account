package com.domo.account.service;

import com.domo.account.domain.Account;
import com.domo.account.domain.AccountStatus;
import com.domo.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount() {
        Account account = Account
                .builder()
                .accountNumber("4000")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);
    }
}
