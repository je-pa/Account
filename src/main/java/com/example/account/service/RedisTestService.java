package com.example.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTestService {
    private final RedissonClient redissonClient;

    public String getLock(){
        RLock lock = redissonClient.getLock("sampleLock"); // redisson에서 제공하는 기본적인 rock기능

        try {
            boolean isLock = lock.tryLock(1,5, TimeUnit.SECONDS); // 1초동안 기다리고 3초동안 가지고 있다가 풀어준다.
            if(!isLock){// rock붙들기 실패
                log.error("====Lock acquisition failed======");
                return "Lock failed";
            }
        }catch (Exception e){
            log.error("Redis lock failed");
        }

        return "get success";
    }
}
