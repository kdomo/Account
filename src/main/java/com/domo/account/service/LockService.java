package com.domo.account.service;

import com.domo.account.exception.AccountException;
import com.domo.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            // 최대 1초동안 기다리면서 lock을 찾아보고
            // lock을 획득하였다면 5초동안 가지고 있다가 언락
            if(!isLock) {
                log.error("====Lock acquisition failed====");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (AccountException e) {
            throw e;
        } catch (Exception e){
            log.error("Redis lock failed");
        }

    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {} ", accountNumber);
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        if(lock.isLocked()){
            redissonClient.getLock(getLockKey(accountNumber));
        }
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
