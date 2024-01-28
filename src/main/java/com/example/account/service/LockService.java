package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
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

    public String lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber)); // redisson에서 제공하는 기본적인 rock기능
        log.debug("Trying lock for accountNumber : {}",accountNumber);

        try {
            boolean isLock = lock.tryLock(1,15, TimeUnit.SECONDS); // 1초동안 기다리고 5초동안 가지고 있다가 풀어준다.
            if(!isLock){// rock붙들기 실패
                log.error("====Lock acquisition failed======");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        }catch (AccountException e){ // globalExceptionHandler 가 받을수있도록
            throw e;
        }catch (Exception e){
            log.error("Redis lock failed", e);
        }

        return "get success";
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber: {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
