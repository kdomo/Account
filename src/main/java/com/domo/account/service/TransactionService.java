package com.domo.account.service;

import com.domo.account.domain.Account;
import com.domo.account.domain.AccountUser;
import com.domo.account.dto.TransactionDto;
import com.domo.account.exception.AccountException;
import com.domo.account.repository.AccountRepository;
import com.domo.account.repository.AccountUserRepository;
import com.domo.account.repository.TransactionRepository;
import com.domo.account.type.AccountStatus;
import com.domo.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

import static com.domo.account.type.ErrorCode.*;
import static com.domo.account.type.ErrorCode.AMOUNT_EXCEED_BALANCE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount){
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(()-> new AccountException(ACCOUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);



    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }
}
